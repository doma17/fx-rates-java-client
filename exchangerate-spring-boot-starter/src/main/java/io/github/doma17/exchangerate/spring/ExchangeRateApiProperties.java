package io.github.doma17.exchangerate.spring;

import io.github.doma17.exchangerate.ExchangeRateApiOptions;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for ExchangeRate-API integration.
 */
@ConfigurationProperties(prefix = "exchangerate.api")
public class ExchangeRateApiProperties {

    private boolean enabled = true;
    private String apiKey;
    private URI baseUrl = ExchangeRateApiOptions.DEFAULT_BASE_URI;
    private Duration connectTimeout = ExchangeRateApiOptions.DEFAULT_CONNECT_TIMEOUT;
    private Duration readTimeout = ExchangeRateApiOptions.DEFAULT_READ_TIMEOUT;

    /**
     * Returns whether auto-configuration is enabled.
     *
     * @return {@code true} when enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether auto-configuration is enabled.
     *
     * @param enabled enabled flag
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the configured API key.
     *
     * @return ExchangeRate-API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the API key.
     *
     * @param apiKey ExchangeRate-API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Returns the API base URL.
     *
     * @return API base URL
     */
    public URI getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the API base URL.
     *
     * @param baseUrl API base URL
     */
    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
    }

    /**
     * Returns HTTP connect timeout.
     *
     * @return connect timeout
     */
    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets HTTP connect timeout.
     *
     * @param connectTimeout connect timeout
     */
    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = Objects.requireNonNull(connectTimeout, "connectTimeout");
    }

    /**
     * Returns HTTP read timeout.
     *
     * @return read timeout
     */
    public Duration getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets HTTP read timeout.
     *
     * @param readTimeout read timeout
     */
    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = Objects.requireNonNull(readTimeout, "readTimeout");
    }
}
