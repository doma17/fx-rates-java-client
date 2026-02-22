package io.github.doma17.exchangerate;

import io.github.doma17.exchangerate.exception.ExchangeRateException;
import io.github.doma17.exchangerate.model.ExchangeRateSnapshot;
import io.github.doma17.exchangerate.model.HistoricalRates;
import io.github.doma17.exchangerate.model.PairRate;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Convenience facade for common lookups in application code.
 */
public class ExchangeRateFacade {

    private final ExchangeRateClient client;
    private final Clock clock;

    /**
     * Creates a facade using the system UTC clock.
     *
     * @param client API client implementation
     */
    public ExchangeRateFacade(ExchangeRateClient client) {
        this(client, Clock.systemUTC());
    }

    /**
     * Creates a facade with a custom clock.
     *
     * @param client API client implementation
     * @param clock clock used to determine today's date
     */
    public ExchangeRateFacade(ExchangeRateClient client, Clock clock) {
        this.client = Objects.requireNonNull(client, "client");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Returns the latest conversion rate for a pair.
     *
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @return latest conversion rate value
     */
    public BigDecimal getRate(String baseCode, String targetCode) {
        ExchangeRateSnapshot snapshot = client.getLatestRates(normalizeCode(baseCode));
        return findRate(snapshot.conversionRates(), targetCode, snapshot.baseCode(), "latest");
    }

    /**
     * Returns the conversion rate for a specific date.
     * If the date is today, latest rates are used.
     *
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @param date target date, not in the future
     * @return conversion rate for the requested date
     */
    public BigDecimal getRate(String baseCode, String targetCode, LocalDate date) {
        Objects.requireNonNull(date, "date");

        LocalDate today = LocalDate.now(clock);
        if (date.isAfter(today)) {
            throw new IllegalArgumentException("date must not be in the future: " + date);
        }

        if (date.equals(today)) {
            return getRate(baseCode, targetCode);
        }

        HistoricalRates historicalRates = client.getHistoricalRates(normalizeCode(baseCode), date);
        return findRate(historicalRates.conversionRates(), targetCode, historicalRates.baseCode(), date.toString());
    }

    /**
     * Converts an amount with the latest pair endpoint.
     *
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @param amount amount to convert
     * @return converted amount
     */
    public BigDecimal convert(String baseCode, String targetCode, BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        PairRate rate = client.convertPairAmount(normalizeCode(baseCode), normalizeCode(targetCode), amount);

        if (rate.conversionResult() == null) {
            throw new ExchangeRateException("conversion result not returned by API");
        }
        return rate.conversionResult();
    }

    /**
     * Returns all rates for a given base currency and date.
     * If the date is today, latest rates are used.
     *
     * @param baseCode base ISO-4217 code
     * @param date target date, not in the future
     * @return map of target currency code to rate
     */
    public Map<String, BigDecimal> getRates(String baseCode, LocalDate date) {
        Objects.requireNonNull(date, "date");

        LocalDate today = LocalDate.now(clock);
        if (date.isAfter(today)) {
            throw new IllegalArgumentException("date must not be in the future: " + date);
        }

        if (date.equals(today)) {
            return client.getLatestRates(normalizeCode(baseCode)).conversionRates();
        }

        return client.getHistoricalRates(normalizeCode(baseCode), date).conversionRates();
    }

    private static BigDecimal findRate(Map<String, BigDecimal> rates, String targetCode, String baseCode, String from) {
        String normalizedTarget = normalizeCode(targetCode);
        BigDecimal rate = rates.get(normalizedTarget);
        if (rate == null) {
            throw new ExchangeRateException(
                    "No rate found for target code " + normalizedTarget + " from " + baseCode + " at " + from
            );
        }
        return rate;
    }

    private static String normalizeCode(String currencyCode) {
        Objects.requireNonNull(currencyCode, "currencyCode");
        String normalized = currencyCode.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("currency code must be ISO-4217 alpha-3: " + currencyCode);
        }
        return normalized;
    }
}
