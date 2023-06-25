package com.tap.multigateway.client.stripe;

import com.tap.multigateway.client.GatewayClientConfiguration;
import com.tap.multigateway.exception.TapException;
import feign.Feign;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.SocketTimeoutException;
import java.time.Duration;

@Configuration
@Slf4j
public class StripeClientConfiguration  {

    @Bean
    public Feign.Builder stripeFeignBuilder() {

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(2)
                .build();


        CircuitBreaker circuitBreaker = CircuitBreaker.of("stripe-gateway", circuitBreakerConfig);
        circuitBreaker.getEventPublisher().onStateTransition(e -> log.info("Stripe circuit Breaker event:" + e));
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withFallbackFactory(exception -> {
                    if (exception.getCause() instanceof SocketTimeoutException) {
                        return new StripeTimeoutFallback();
                    } else if (exception instanceof TapException) {
                        return new StripeFallBack((TapException) exception);
                    } else {
                        return new StripeFallBack();
                    }
                })
                .build();


        return Resilience4jFeign.builder(decorators);
    }
}
