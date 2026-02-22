# fx-rates-java-client

> Unofficial community project. This library is **not affiliated with, endorsed by, or sponsored by ExchangeRate-API**.
>
> 이 라이브러리는 커뮤니티 프로젝트이며, **ExchangeRate-API와 관계가 없습니다.**
> 개인적인 목적으로 ExchangeRate API를 사용하기 위한 Client Wrapper Library 입니다. 

A Java client library for [ExchangeRate-API v6](https://www.exchangerate-api.com/docs), with:

- A pure Java core module (no Spring dependency)
- An optional Spring Boot starter module
- Support for latest rates, pair conversion, historical data, quota, supported codes, and enriched data

## Modules

- `fx-rates-core` (directory: `exchangerate-core`)
  - Artifact: `io.github.doma17:fx-rates-java-client`
  - Pure Java/Kotlin usage
  - No Spring Boot dependency
- `fx-rates-spring-boot-starter` (directory: `exchangerate-spring-boot-starter`)
  - Artifact: `io.github.doma17:fx-rates-java-client-spring-boot-starter`
  - Auto-configures client beans from `application.yml`

## Compatibility

- Java: **17+**
- Kotlin/JVM: supported (same JVM target)
- Spring Boot starter: designed for Spring Boot **3.x**

## Installation

Use the latest released version (replace `1.0.0` with your target version).

### Core (Pure Java/Kotlin)

Maven:

```xml
<dependency>
  <groupId>io.github.doma17</groupId>
  <artifactId>fx-rates-java-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle:

```kotlin
dependencies {
    implementation("io.github.doma17:fx-rates-java-client:1.0.0")
}
```

### Spring Boot Starter (Optional)

Maven:

```xml
<dependency>
  <groupId>io.github.doma17</groupId>
  <artifactId>fx-rates-java-client-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle:

```kotlin
dependencies {
    implementation("io.github.doma17:fx-rates-java-client-spring-boot-starter:1.0.0")
}
```

## Quick Start (Pure Java)

```java
import io.github.doma17.exchangerate.ExchangeRateApiClient;
import io.github.doma17.exchangerate.ExchangeRateClient;
import io.github.doma17.exchangerate.ExchangeRateFacade;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Example {
    public static void main(String[] args) {
        String apiKey = System.getenv("EXCHANGERATE_API_KEY");
        ExchangeRateClient client = new ExchangeRateApiClient(apiKey);
        ExchangeRateFacade facade = new ExchangeRateFacade(client);

        BigDecimal usdToKrwToday = facade.getRate("USD", "KRW");
        BigDecimal usdToEurOnDate = facade.getRate("USD", "EUR", LocalDate.of(2026, 2, 22));

        System.out.println("USD/KRW(today): " + usdToKrwToday);
        System.out.println("USD/EUR(2025-12-31): " + usdToEurOnDate);
    }
}
```

### Custom Timeouts / Base URL

```java
import io.github.doma17.exchangerate.ExchangeRateApiClient;
import io.github.doma17.exchangerate.ExchangeRateApiOptions;

import java.net.URI;
import java.time.Duration;

ExchangeRateApiOptions options = ExchangeRateApiOptions.builder(System.getenv("EXCHANGERATE_API_KEY"))
        .baseUri(URI.create("https://v6.exchangerate-api.com/v6"))
        .connectTimeout(Duration.ofSeconds(3))
        .readTimeout(Duration.ofSeconds(10))
        .build();

ExchangeRateApiClient client = new ExchangeRateApiClient(options);
```

## Spring Boot Usage

### `application.yml`

```yaml
exchangerate:
  api:
    enabled: true
    api-key: ${EXCHANGERATE_API_KEY}
    base-url: https://v6.exchangerate-api.com/v6
    connect-timeout: 3s
    read-timeout: 10s
```

### Auto-configured Beans

- `io.github.doma17.exchangerate.ExchangeRateClient`
- `io.github.doma17.exchangerate.ExchangeRateFacade`

### Example Service

```java
import io.github.doma17.exchangerate.ExchangeRateFacade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class FxTracker {

    private final ExchangeRateFacade exchangeRateFacade;

    public FxTracker(ExchangeRateFacade exchangeRateFacade) {
        this.exchangeRateFacade = exchangeRateFacade;
    }

    public BigDecimal latestUsdToKrw() {
        return exchangeRateFacade.getRate("USD", "KRW");
    }

    public BigDecimal historicalUsdToEur(LocalDate date) {
        return exchangeRateFacade.getRate("USD", "EUR", date);
    }
}
```

## API Coverage

| ExchangeRate-API Endpoint | Java Method |
|---|---|
| `latest/{base}` | `ExchangeRateClient#getLatestRates` |
| `pair/{base}/{target}` | `ExchangeRateClient#getPairRate` |
| `pair/{base}/{target}/{amount}` | `ExchangeRateClient#convertPairAmount` |
| `history/{base}/{year}/{month}/{day}` | `ExchangeRateClient#getHistoricalRates` |
| `history/{base}/{year}/{month}/{day}/{amount}` | `ExchangeRateClient#getHistoricalConvertedAmounts` |
| `quota` | `ExchangeRateClient#getQuotaStatus` |
| `codes` | `ExchangeRateClient#getSupportedCurrencies` |
| `enriched/{base}/{target}` | `ExchangeRateClient#getEnrichedRate` |

## Error Handling

The client throws runtime exceptions:

- `ExchangeRateApiException`
  - API returned `result=error`
  - inspect `errorType()` for API error classification
- `ExchangeRateClientException`
  - transport or JSON parsing failure
- `ExchangeRateException`
  - base exception type

Supported API error types include:

- `unsupported-code`
- `malformed-request`
- `invalid-key`
- `inactive-account`
- `quota-reached`
- `plan-upgrade-required`
- `no-data-available`
- `unknown-code`

Example:

```java
import io.github.doma17.exchangerate.exception.ExchangeRateApiException;
import io.github.doma17.exchangerate.exception.ExchangeRateApiErrorType;

try {
    client.getLatestRates("USD");
} catch (ExchangeRateApiException e) {
    if (e.errorType() == ExchangeRateApiErrorType.QUOTA_REACHED) {
        // fallback or backoff
    }
}
```

## Quota Notes

- `quota` endpoint calls also consume request quota.
- Query quota periodically (for example every 10-30 minutes), not per request.
- Implement local caching/backoff when remaining quota is low.

## Development

Required credentials/signing settings (in `~/.gradle/gradle.properties` or env vars):

- `ossrhUsername` / `OSSRH_USERNAME`
- `ossrhPassword` / `OSSRH_PASSWORD`
- `signingKey` / `SIGNING_KEY`
- `signingPassword` / `SIGNING_PASSWORD`

## Important Legal / Terms Note

This project is a third-party wrapper. You are responsible for complying with ExchangeRate-API terms and plan limits when distributing or operating software that uses this library:

- ExchangeRate-API terms: [https://www.exchangerate-api.com/terms](https://www.exchangerate-api.com/terms)
