package io.github.doma17.exchangerate.spring;

import io.github.doma17.exchangerate.ExchangeRateApiClient;
import io.github.doma17.exchangerate.ExchangeRateApiOptions;
import io.github.doma17.exchangerate.ExchangeRateClient;
import io.github.doma17.exchangerate.ExchangeRateFacade;
import java.time.Clock;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for ExchangeRate client beans.
 */
@AutoConfiguration
@ConditionalOnClass(ExchangeRateFacade.class)
@EnableConfigurationProperties(ExchangeRateApiProperties.class)
@ConditionalOnProperty(prefix = "exchangerate.api", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExchangeRateAutoConfiguration {

    /**
     * Creates immutable API options from bound properties.
     *
     * @param properties bound configuration properties
     * @return API options bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "exchangerate.api", name = "api-key")
    public ExchangeRateApiOptions exchangeRateApiOptions(ExchangeRateApiProperties properties) {
        return ExchangeRateApiOptions.builder(properties.getApiKey())
                .baseUri(properties.getBaseUrl())
                .connectTimeout(properties.getConnectTimeout())
                .readTimeout(properties.getReadTimeout())
                .build();
    }

    /**
     * Creates the low-level API client bean.
     *
     * @param options API options
     * @return API client bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ExchangeRateApiOptions.class)
    public ExchangeRateClient exchangeRateClient(ExchangeRateApiOptions options) {
        return new ExchangeRateApiClient(options);
    }

    /**
     * Creates the clock used by the facade for date-sensitive behavior.
     *
     * @return UTC clock bean
     */
    @Bean
    @ConditionalOnMissingBean
    public Clock exchangeRateClock() {
        return Clock.systemUTC();
    }

    /**
     * Creates the high-level facade bean.
     *
     * @param client API client bean
     * @param exchangeRateClock clock bean
     * @return facade bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ExchangeRateClient.class)
    public ExchangeRateFacade exchangeRateFacade(ExchangeRateClient client, Clock exchangeRateClock) {
        return new ExchangeRateFacade(client, exchangeRateClock);
    }
}
