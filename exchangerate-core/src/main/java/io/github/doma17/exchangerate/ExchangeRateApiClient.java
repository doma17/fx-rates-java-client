package io.github.doma17.exchangerate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.doma17.exchangerate.exception.ExchangeRateApiErrorType;
import io.github.doma17.exchangerate.exception.ExchangeRateApiException;
import io.github.doma17.exchangerate.exception.ExchangeRateClientException;
import io.github.doma17.exchangerate.model.EnrichedRate;
import io.github.doma17.exchangerate.model.ExchangeRateSnapshot;
import io.github.doma17.exchangerate.model.HistoricalRates;
import io.github.doma17.exchangerate.model.PairRate;
import io.github.doma17.exchangerate.model.QuotaStatus;
import io.github.doma17.exchangerate.model.SupportedCurrency;
import io.github.doma17.exchangerate.model.TargetCurrencyMetadata;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * HTTP client for ExchangeRate-API v6.
 */
public class ExchangeRateApiClient implements ExchangeRateClient {

    private static final String RESULT_SUCCESS = "success";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExchangeRateApiOptions options;

    /**
     * Creates a client with default options and default HTTP/Jackson instances.
     *
     * @param apiKey ExchangeRate-API key
     */
    public ExchangeRateApiClient(String apiKey) {
        this(ExchangeRateApiOptions.builder(apiKey).build());
    }

    /**
     * Creates a client with explicit options and default HTTP/Jackson instances.
     *
     * @param options immutable API options
     */
    public ExchangeRateApiClient(ExchangeRateApiOptions options) {
        this(options, null, null);
    }

    /**
     * Creates a client with explicit options and optional low-level dependencies.
     *
     * @param options immutable API options
     * @param httpClient optional custom HTTP client, defaulted when {@code null}
     * @param objectMapper optional custom object mapper, defaulted when {@code null}
     */
    public ExchangeRateApiClient(ExchangeRateApiOptions options, HttpClient httpClient, ObjectMapper objectMapper) {
        this.options = Objects.requireNonNull(options, "options");
        this.httpClient = httpClient != null
                ? httpClient
                : HttpClient.newBuilder()
                        .connectTimeout(options.connectTimeout())
                        .build();
        this.objectMapper = objectMapper != null
                ? objectMapper
                : JsonMapper.builder()
                        .addModule(new JavaTimeModule())
                        .build();
    }

    @Override
    public ExchangeRateSnapshot getLatestRates(String baseCode) {
        JsonNode root = request("latest", normalizeCode(baseCode));
        return new ExchangeRateSnapshot(
                toInstant(root.path("time_last_update_unix")),
                toInstant(root.path("time_next_update_unix")),
                normalizeCode(root.path("base_code").asText()),
                toRateMap(root.path("conversion_rates"))
        );
    }

    @Override
    public PairRate getPairRate(String baseCode, String targetCode) {
        JsonNode root = request("pair", normalizeCode(baseCode), normalizeCode(targetCode));
        return toPairRate(root);
    }

    @Override
    public PairRate convertPairAmount(String baseCode, String targetCode, BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        JsonNode root = request(
                "pair",
                normalizeCode(baseCode),
                normalizeCode(targetCode),
                amount.stripTrailingZeros().toPlainString()
        );
        return toPairRate(root);
    }

    @Override
    public HistoricalRates getHistoricalRates(String baseCode, LocalDate date) {
        Objects.requireNonNull(date, "date");
        JsonNode root = request(
                "history",
                normalizeCode(baseCode),
                String.valueOf(date.getYear()),
                String.valueOf(date.getMonthValue()),
                String.valueOf(date.getDayOfMonth())
        );

        return new HistoricalRates(
                toLocalDate(root),
                normalizeCode(root.path("base_code").asText()),
                toRateMap(root.path("conversion_rates")),
                null,
                Map.of()
        );
    }

    @Override
    public HistoricalRates getHistoricalConvertedAmounts(String baseCode, LocalDate date, BigDecimal amount) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(amount, "amount");
        JsonNode root = request(
                "history",
                normalizeCode(baseCode),
                String.valueOf(date.getYear()),
                String.valueOf(date.getMonthValue()),
                String.valueOf(date.getDayOfMonth()),
                amount.stripTrailingZeros().toPlainString()
        );

        return new HistoricalRates(
                toLocalDate(root),
                normalizeCode(root.path("base_code").asText()),
                Map.of(),
                root.path("requested_amount").isMissingNode() ? null : root.path("requested_amount").decimalValue(),
                toRateMap(root.path("conversion_amounts"))
        );
    }

    @Override
    public QuotaStatus getQuotaStatus() {
        JsonNode root = request("quota");
        return new QuotaStatus(
                root.path("plan_quota").asInt(),
                root.path("requests_remaining").asInt(),
                root.path("refresh_day_of_month").asInt()
        );
    }

    @Override
    public List<SupportedCurrency> getSupportedCurrencies() {
        JsonNode root = request("codes");
        JsonNode supportedCodes = root.path("supported_codes");
        if (!supportedCodes.isArray()) {
            return List.of();
        }

        List<SupportedCurrency> currencies = new ArrayList<>();
        for (JsonNode entry : supportedCodes) {
            if (entry.isArray() && entry.size() >= 2) {
                currencies.add(new SupportedCurrency(entry.get(0).asText(), entry.get(1).asText()));
            }
        }
        return List.copyOf(currencies);
    }

    @Override
    public EnrichedRate getEnrichedRate(String baseCode, String targetCode) {
        JsonNode root = request("enriched", normalizeCode(baseCode), normalizeCode(targetCode));

        JsonNode targetData = root.path("target_data");
        TargetCurrencyMetadata metadata = new TargetCurrencyMetadata(
                targetData.path("locale").asText(),
                targetData.path("two_letter_code").asText(),
                targetData.path("currency_name").asText(),
                targetData.path("currency_name_short").asText(),
                targetData.path("display_symbol").asText(),
                targetData.path("flag_url").asText()
        );

        return new EnrichedRate(
                toInstant(root.path("time_last_update_unix")),
                toInstant(root.path("time_next_update_unix")),
                normalizeCode(root.path("base_code").asText()),
                normalizeCode(root.path("target_code").asText()),
                root.path("conversion_rate").decimalValue(),
                metadata
        );
    }

    private PairRate toPairRate(JsonNode root) {
        return new PairRate(
                toInstant(root.path("time_last_update_unix")),
                toInstant(root.path("time_next_update_unix")),
                normalizeCode(root.path("base_code").asText()),
                normalizeCode(root.path("target_code").asText()),
                root.path("conversion_rate").decimalValue(),
                root.path("conversion_result").isMissingNode() ? null : root.path("conversion_result").decimalValue()
        );
    }

    private JsonNode request(String... endpointSegments) {
        URI uri = buildUri(endpointSegments);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .timeout(options.readTimeout())
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = parseBody(response.body(), uri);
            validateApiResult(root, response.statusCode(), uri);
            return root;
        } catch (IOException e) {
            throw new ExchangeRateClientException("I/O error while calling " + uri, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExchangeRateClientException("Interrupted while calling " + uri, e);
        }
    }

    private JsonNode parseBody(String body, URI uri) {
        try {
            return objectMapper.readTree(body);
        } catch (IOException e) {
            throw new ExchangeRateClientException("Unable to parse JSON response from " + uri, e);
        }
    }

    private void validateApiResult(JsonNode root, int statusCode, URI uri) {
        String result = root.path("result").asText("");
        if (RESULT_SUCCESS.equalsIgnoreCase(result)) {
            return;
        }

        String errorTypeWire = root.path("error-type").asText("unknown-error");
        ExchangeRateApiErrorType errorType = ExchangeRateApiErrorType.fromWireValue(errorTypeWire);

        throw new ExchangeRateApiException(
                statusCode,
                errorType,
                "ExchangeRate-API error for " + uri + ": " + errorType.wireValue()
        );
    }

    private URI buildUri(String... endpointSegments) {
        String base = options.baseUri().toString();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String endpointPath = List.of(endpointSegments)
                .stream()
                .map(this::encodePathSegment)
                .collect(Collectors.joining("/"));

        return URI.create(base + "/" + encodePathSegment(options.apiKey()) + "/" + endpointPath);
    }

    private String encodePathSegment(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String normalizeCode(String currencyCode) {
        Objects.requireNonNull(currencyCode, "currencyCode");
        String normalized = currencyCode.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("currency code must be ISO-4217 alpha-3: " + currencyCode);
        }
        return normalized;
    }

    private static Instant toInstant(JsonNode epochSecondsNode) {
        long epochSeconds = epochSecondsNode.asLong(Long.MIN_VALUE);
        if (epochSeconds == Long.MIN_VALUE) {
            throw new IllegalArgumentException("missing epoch seconds field");
        }
        return Instant.ofEpochSecond(epochSeconds);
    }

    private static LocalDate toLocalDate(JsonNode root) {
        int year = root.path("year").asInt(-1);
        int month = root.path("month").asInt(-1);
        int day = root.path("day").asInt(-1);
        if (year < 0 || month < 0 || day < 0) {
            throw new IllegalArgumentException("historical response missing date fields");
        }
        return LocalDate.of(year, month, day);
    }

    private static Map<String, BigDecimal> toRateMap(JsonNode node) {
        if (!node.isObject()) {
            return Map.of();
        }
        return node.properties().stream()
                .collect(Collectors.toUnmodifiableMap(entry -> normalizeCode(entry.getKey()), entry -> entry.getValue().decimalValue()));
    }
}
