package io.github.doma17.exchangerate.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Latest rates response for a base currency.
 *
 * @param lastUpdatedAt response last-updated timestamp
 * @param nextUpdateAt next expected refresh timestamp
 * @param baseCode base ISO-4217 code
 * @param conversionRates map of target currency code to rate
 */
public record ExchangeRateSnapshot(
        Instant lastUpdatedAt,
        Instant nextUpdateAt,
        String baseCode,
        Map<String, BigDecimal> conversionRates
) {
    /**
     * Creates a validated snapshot record.
     *
     * @param lastUpdatedAt response last-updated timestamp
     * @param nextUpdateAt next expected refresh timestamp
     * @param baseCode base ISO-4217 code
     * @param conversionRates map of target currency code to rate
     */
    public ExchangeRateSnapshot {
        Objects.requireNonNull(lastUpdatedAt, "lastUpdatedAt");
        Objects.requireNonNull(nextUpdateAt, "nextUpdateAt");
        Objects.requireNonNull(baseCode, "baseCode");
        conversionRates = Map.copyOf(Objects.requireNonNull(conversionRates, "conversionRates"));
    }
}
