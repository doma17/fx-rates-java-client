package io.github.doma17.exchangerate.model;

import java.util.Objects;

/**
 * Supported currency metadata entry.
 *
 * @param code ISO-4217 currency code
 * @param name human-readable currency name
 */
public record SupportedCurrency(String code, String name) {
    /**
     * Creates a validated supported currency record.
     *
     * @param code ISO-4217 currency code
     * @param name human-readable currency name
     */
    public SupportedCurrency {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(name, "name");
    }
}
