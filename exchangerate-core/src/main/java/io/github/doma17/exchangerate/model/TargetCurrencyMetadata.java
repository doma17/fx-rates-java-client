package io.github.doma17.exchangerate.model;

/**
 * Extra metadata for the target currency in enriched responses.
 *
 * @param locale locale or country label
 * @param twoLetterCode country two-letter code
 * @param currencyName full currency name
 * @param currencyNameShort short currency name
 * @param displaySymbol symbol code string from the API
 * @param flagUrl flag image URL
 */
public record TargetCurrencyMetadata(
        String locale,
        String twoLetterCode,
        String currencyName,
        String currencyNameShort,
        String displaySymbol,
        String flagUrl
) {
}
