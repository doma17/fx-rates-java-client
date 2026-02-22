package io.github.doma17.exchangerate.spring;

import io.github.doma17.exchangerate.ExchangeRateClient;
import io.github.doma17.exchangerate.ExchangeRateFacade;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ExchangeRateAutoConfiguration.class));

    @Test
    void registersBeansWhenApiKeyConfigured() {
        contextRunner
                .withPropertyValues("exchangerate.api.api-key=test-key")
                .run(context -> {
                    assertThat(context).hasSingleBean(ExchangeRateClient.class);
                    assertThat(context).hasSingleBean(ExchangeRateFacade.class);
                });
    }

    @Test
    void skipsClientWhenApiKeyMissing() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(ExchangeRateClient.class);
            assertThat(context).doesNotHaveBean(ExchangeRateFacade.class);
        });
    }
}
