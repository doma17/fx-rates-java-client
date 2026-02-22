package io.github.doma17.exchangerate.exception;

/**
 * Exception raised for transport, interruption, or JSON parsing failures.
 */
public class ExchangeRateClientException extends ExchangeRateException {

    /**
     * Creates a client exception with message and cause.
     *
     * @param message error description
     * @param cause root cause
     */
    public ExchangeRateClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
