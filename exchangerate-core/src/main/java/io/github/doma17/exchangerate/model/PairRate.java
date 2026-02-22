package io.github.doma17.exchangerate.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Pair conversion response.
 *
 * @param lastUpdatedAt response last-updated timestamp
 * @param nextUpdateAt next expected refresh timestamp
 * @param baseCode base ISO-4217 code
 * @param targetCode target ISO-4217 code
 * @param conversionRate pair conversion rate
 * @param conversionResult converted amount, when requested
 */
public record PairRate(
        Instant lastUpdatedAt,
        Instant nextUpdateAt,
        String baseCode,
        String targetCode,
        BigDecimal conversionRate,
        BigDecimal conversionResult
) {
    /**
     * Creates a validated pair rate record.
     *
     * @param lastUpdatedAt response last-updated timestamp
     * @param nextUpdateAt next expected refresh timestamp
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @param conversionRate pair conversion rate
     * @param conversionResult converted amount, when requested
     */
    public PairRate {
        Objects.requireNonNull(lastUpdatedAt, "lastUpdatedAt");
        Objects.requireNonNull(nextUpdateAt, "nextUpdateAt");
        Objects.requireNonNull(baseCode, "baseCode");
        Objects.requireNonNull(targetCode, "targetCode");
        Objects.requireNonNull(conversionRate, "conversionRate");
    }
}
