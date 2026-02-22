package io.github.doma17.exchangerate.exception;

import java.util.Arrays;

/**
 * Known API error types returned by ExchangeRate-API.
 */
public enum ExchangeRateApiErrorType {

    /** Unsupported currency code. */
    UNSUPPORTED_CODE("unsupported-code"),
    /** Malformed endpoint or request structure. */
    MALFORMED_REQUEST("malformed-request"),
    /** Invalid API key. */
    INVALID_KEY("invalid-key"),
    /** Account exists but is not activated. */
    INACTIVE_ACCOUNT("inactive-account"),
    /** API quota has been exhausted. */
    QUOTA_REACHED("quota-reached"),
    /** Current plan does not allow this endpoint. */
    PLAN_UPGRADE_REQUIRED("plan-upgrade-required"),
    /** No data is available for the requested date. */
    NO_DATA_AVAILABLE("no-data-available"),
    /** Unknown currency code. */
    UNKNOWN_CODE("unknown-code"),
    /** Fallback for unknown error values. */
    UNKNOWN_ERROR("unknown-error");

    private final String wireValue;

    ExchangeRateApiErrorType(String wireValue) {
        this.wireValue = wireValue;
    }

    /**
     * Returns the wire-format value used by the API response.
     *
     * @return API wire value
     */
    public String wireValue() {
        return wireValue;
    }

    /**
     * Maps a wire-format error string to a known enum value.
     *
     * @param wireValue API error-type field
     * @return mapped error type or {@link #UNKNOWN_ERROR}
     */
    public static ExchangeRateApiErrorType fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(value -> value.wireValue.equalsIgnoreCase(wireValue))
                .findFirst()
                .orElse(UNKNOWN_ERROR);
    }
}
