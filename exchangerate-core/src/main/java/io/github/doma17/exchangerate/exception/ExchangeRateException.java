package io.github.doma17.exchangerate.exception;

/**
 * Base runtime exception for library-level failures.
 */
public class ExchangeRateException extends RuntimeException {

    /**
     * Creates an exception with a message.
     *
     * @param message error description
     */
    public ExchangeRateException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and cause.
     *
     * @param message error description
     * @param cause root cause
     */
    public ExchangeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
