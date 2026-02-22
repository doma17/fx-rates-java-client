package io.github.doma17.exchangerate.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Historical rates response for a date.
 *
 * @param date requested date
 * @param baseCode base ISO-4217 code
 * @param conversionRates map of currency rates for the date
 * @param requestedAmount amount used in conversion-amount queries, when present
 * @param conversionAmounts converted amounts map, when present
 */
public record HistoricalRates(
        LocalDate date,
        String baseCode,
        Map<String, BigDecimal> conversionRates,
        BigDecimal requestedAmount,
        Map<String, BigDecimal> conversionAmounts
) {
    /**
     * Creates a validated historical rates record.
     *
     * @param date requested date
     * @param baseCode base ISO-4217 code
     * @param conversionRates map of currency rates for the date
     * @param requestedAmount amount used in conversion-amount queries, when present
     * @param conversionAmounts converted amounts map, when present
     */
    public HistoricalRates {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(baseCode, "baseCode");
        conversionRates = Map.copyOf(Objects.requireNonNull(conversionRates, "conversionRates"));
        conversionAmounts = Map.copyOf(Objects.requireNonNull(conversionAmounts, "conversionAmounts"));
    }

    /**
     * Returns whether the response includes converted amounts.
     *
     * @return {@code true} when {@code conversionAmounts} is not empty
     */
    public boolean hasAmountConversions() {
        return !conversionAmounts.isEmpty();
    }
}
