package io.github.doma17.exchangerate;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.github.doma17.exchangerate.exception.ExchangeRateApiErrorType;
import io.github.doma17.exchangerate.exception.ExchangeRateApiException;
import io.github.doma17.exchangerate.model.ExchangeRateSnapshot;
import io.github.doma17.exchangerate.model.HistoricalRates;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeRateApiClientTest {

    private final Map<String, StubResponse> responses = new ConcurrentHashMap<>();
    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", this::handle);
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
        responses.clear();
    }

    @Test
    void getLatestRatesParsesResponse() {
        stub(
                "/v6/test-key/latest/USD",
                200,
                """
                {
                  "result": "success",
                  "time_last_update_unix": 1700000000,
                  "time_next_update_unix": 1700003600,
                  "base_code": "USD",
                  "conversion_rates": {
                    "KRW": 1321.21,
                    "EUR": 0.91
                  }
                }
                """
        );

        ExchangeRateApiClient client = newClient();
        ExchangeRateSnapshot snapshot = client.getLatestRates("usd");

        assertThat(snapshot.baseCode()).isEqualTo("USD");
        assertThat(snapshot.conversionRates().get("KRW")).isEqualByComparingTo("1321.21");
        assertThat(snapshot.conversionRates().get("EUR")).isEqualByComparingTo("0.91");
    }

    @Test
    void getPairRateThrowsTypedExceptionOnApiError() {
        stub(
                "/v6/test-key/pair/USD/ZZZ",
                200,
                """
                {
                  "result": "error",
                  "error-type": "unsupported-code"
                }
                """
        );

        ExchangeRateApiClient client = newClient();

        assertThatThrownBy(() -> client.getPairRate("USD", "ZZZ"))
                .isInstanceOf(ExchangeRateApiException.class)
                .extracting(ex -> ((ExchangeRateApiException) ex).errorType())
                .isEqualTo(ExchangeRateApiErrorType.UNSUPPORTED_CODE);
    }

    @Test
    void getHistoricalRatesUsesDateSegments() {
        stub(
                "/v6/test-key/history/USD/2024/5/1",
                200,
                """
                {
                  "result": "success",
                  "year": 2024,
                  "month": 5,
                  "day": 1,
                  "base_code": "USD",
                  "conversion_rates": {
                    "JPY": 154.22
                  }
                }
                """
        );

        ExchangeRateApiClient client = newClient();
        HistoricalRates rates = client.getHistoricalRates("USD", LocalDate.of(2024, 5, 1));

        assertThat(rates.date()).isEqualTo(LocalDate.of(2024, 5, 1));
        assertThat(rates.conversionRates()).containsKey("JPY");
    }

    private ExchangeRateApiClient newClient() {
        int port = server.getAddress().getPort();
        ExchangeRateApiOptions options = ExchangeRateApiOptions.builder("test-key")
                .baseUri(URI.create("http://localhost:" + port + "/v6"))
                .build();
        return new ExchangeRateApiClient(options);
    }

    private void stub(String path, int statusCode, String body) {
        responses.put(path, new StubResponse(statusCode, body));
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        StubResponse stubResponse = responses.getOrDefault(
                path,
                new StubResponse(404, "{\"result\":\"error\",\"error-type\":\"malformed-request\"}")
        );

        byte[] payload = stubResponse.body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(stubResponse.statusCode, payload.length);
        exchange.getResponseBody().write(payload);
        exchange.close();
    }

    private record StubResponse(int statusCode, String body) {
    }
}
