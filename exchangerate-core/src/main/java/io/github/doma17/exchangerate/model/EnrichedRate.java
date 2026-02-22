package io.github.doma17.exchangerate.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Enriched pair conversion response including currency metadata.
 *
 * @param lastUpdatedAt response last-updated timestamp
 * @param nextUpdateAt next expected refresh timestamp
 * @param baseCode base ISO-4217 code
 * @param targetCode target ISO-4217 code
 * @param conversionRate latest conversion rate
 * @param targetData metadata for the target currency
 */
public record EnrichedRate(
        Instant lastUpdatedAt,
        Instant nextUpdateAt,
        String baseCode,
        String targetCode,
        BigDecimal conversionRate,
        TargetCurrencyMetadata targetData
) {
    /**
     * Creates a validated enriched rate record.
     *
     * @param lastUpdatedAt response last-updated timestamp
     * @param nextUpdateAt next expected refresh timestamp
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @param conversionRate latest conversion rate
     * @param targetData metadata for the target currency
     */
    public EnrichedRate {
        Objects.requireNonNull(lastUpdatedAt, "lastUpdatedAt");
        Objects.requireNonNull(nextUpdateAt, "nextUpdateAt");
        Objects.requireNonNull(baseCode, "baseCode");
        Objects.requireNonNull(targetCode, "targetCode");
        Objects.requireNonNull(conversionRate, "conversionRate");
        Objects.requireNonNull(targetData, "targetData");
    }
}
