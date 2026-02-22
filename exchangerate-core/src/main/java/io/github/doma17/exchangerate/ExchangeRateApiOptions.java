package io.github.doma17.exchangerate;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

/**
 * Immutable options for {@link ExchangeRateApiClient}.
 */
public final class ExchangeRateApiOptions {

    /**
     * Default ExchangeRate-API v6 base URI.
     */
    public static final URI DEFAULT_BASE_URI = URI.create("https://v6.exchangerate-api.com/v6");
    /**
     * Default HTTP connect timeout.
     */
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(3);
    /**
     * Default HTTP read timeout.
     */
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(10);

    private final String apiKey;
    private final URI baseUri;
    private final Duration connectTimeout;
    private final Duration readTimeout;

    private ExchangeRateApiOptions(Builder builder) {
        this.apiKey = requireNonBlank(builder.apiKey, "apiKey");
        this.baseUri = Objects.requireNonNull(builder.baseUri, "baseUri");
        this.connectTimeout = Objects.requireNonNull(builder.connectTimeout, "connectTimeout");
        this.readTimeout = Objects.requireNonNull(builder.readTimeout, "readTimeout");
    }

    /**
     * Returns the API key used to call ExchangeRate-API.
     *
     * @return API key
     */
    public String apiKey() {
        return apiKey;
    }

    /**
     * Returns the base URI for API requests.
     *
     * @return API base URI
     */
    public URI baseUri() {
        return baseUri;
    }

    /**
     * Returns the HTTP connect timeout.
     *
     * @return connect timeout
     */
    public Duration connectTimeout() {
        return connectTimeout;
    }

    /**
     * Returns the HTTP read timeout.
     *
     * @return read timeout
     */
    public Duration readTimeout() {
        return readTimeout;
    }

    /**
     * Creates a builder with the required API key.
     *
     * @param apiKey ExchangeRate-API key
     * @return options builder
     */
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    /**
     * Builder for {@link ExchangeRateApiOptions}.
     */
    public static final class Builder {

        private final String apiKey;
        private URI baseUri = DEFAULT_BASE_URI;
        private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private Duration readTimeout = DEFAULT_READ_TIMEOUT;

        private Builder(String apiKey) {
            this.apiKey = apiKey;
        }

        /**
         * Sets the API base URI.
         *
         * @param baseUri API base URI
         * @return this builder
         */
        public Builder baseUri(URI baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        /**
         * Sets the HTTP connect timeout.
         *
         * @param connectTimeout connect timeout
         * @return this builder
         */
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets the HTTP read timeout.
         *
         * @param readTimeout read timeout
         * @return this builder
         */
        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Builds immutable options.
         *
         * @return new options instance
         */
        public ExchangeRateApiOptions build() {
            return new ExchangeRateApiOptions(this);
        }
    }
}
