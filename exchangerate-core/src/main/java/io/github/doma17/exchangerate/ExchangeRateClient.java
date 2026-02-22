package io.github.doma17.exchangerate;

import io.github.doma17.exchangerate.model.EnrichedRate;
import io.github.doma17.exchangerate.model.ExchangeRateSnapshot;
import io.github.doma17.exchangerate.model.HistoricalRates;
import io.github.doma17.exchangerate.model.PairRate;
import io.github.doma17.exchangerate.model.QuotaStatus;
import io.github.doma17.exchangerate.model.SupportedCurrency;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Contract for calling ExchangeRate-API endpoints.
 */
public interface ExchangeRateClient {

    /**
     * Returns the latest rates for all supported currencies against the given base currency.
     *
     * @param baseCode base ISO-4217 code (for example {@code USD})
     * @return latest snapshot for the base currency
     */
    ExchangeRateSnapshot getLatestRates(String baseCode);

    /**
     * Returns the latest conversion rate between two currencies.
     *
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @return latest pair rate
     */
    PairRate getPairRate(String baseCode, String targetCode);

    /**
     * Converts an amount using the latest pair rate.
     *
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @param amount amount to convert
     * @return pair rate including conversion result
     */
    PairRate convertPairAmount(String baseCode, String targetCode, BigDecimal amount);

    /**
     * Returns historical rates for all available currencies at a specific date.
     *
     * @param baseCode base ISO-4217 code
     * @param date date to query
     * @return historical rates snapshot
     */
    HistoricalRates getHistoricalRates(String baseCode, LocalDate date);

    /**
     * Converts an amount to all available currencies for a specific historical date.
     *
     * @param baseCode base ISO-4217 code
     * @param date date to query
     * @param amount amount to convert
     * @return historical conversion amounts response
     */
    HistoricalRates getHistoricalConvertedAmounts(String baseCode, LocalDate date, BigDecimal amount);

    /**
     * Returns the API quota status for the current account.
     *
     * @return quota metadata
     */
    QuotaStatus getQuotaStatus();

    /**
     * Returns the list of currently supported currency codes.
     *
     * @return supported currency codes and display names
     */
    List<SupportedCurrency> getSupportedCurrencies();

    /**
     * Returns enriched conversion data between two currencies.
     *
     * @param baseCode base ISO-4217 code
     * @param targetCode target ISO-4217 code
     * @return enriched pair rate metadata
     */
    EnrichedRate getEnrichedRate(String baseCode, String targetCode);
}
