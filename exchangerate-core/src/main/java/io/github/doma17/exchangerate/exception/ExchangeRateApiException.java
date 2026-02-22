package io.github.doma17.exchangerate.exception;

/**
 * Exception raised when ExchangeRate-API responds with {@code result=error}.
 */
public class ExchangeRateApiException extends ExchangeRateException {

    /** HTTP status code returned by the API call. */
    private final int statusCode;
    /** Parsed API error type value. */
    private final ExchangeRateApiErrorType errorType;

    /**
     * Creates an API exception with HTTP status and API error type.
     *
     * @param statusCode HTTP status code
     * @param errorType API error classification
     * @param message error description
     */
    public ExchangeRateApiException(int statusCode, ExchangeRateApiErrorType errorType, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    /**
     * Returns the HTTP status code from the API call.
     *
     * @return HTTP status code
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * Returns the parsed API error type.
     *
     * @return API error type
     */
    public ExchangeRateApiErrorType errorType() {
        return errorType;
    }
}
