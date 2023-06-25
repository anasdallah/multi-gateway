package com.tap.multigateway.client.paypal;

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
public class PayPalClientConfiguration  {

    @Bean
    public Feign.Builder feignBuilder() {

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(2)
                .build();


        CircuitBreaker circuitBreaker = CircuitBreaker.of("paypal-gateway", circuitBreakerConfig);
        circuitBreaker.getEventPublisher().onStateTransition(e -> log.info("Circuit Breaker event:" + e));
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withFallbackFactory(exception -> {
                    if (exception.getCause() instanceof SocketTimeoutException) {
                        return new PayPalTimeoutFallback();
                    } else if (exception instanceof TapException) {
                        return new PayPalFallBack((TapException) exception);
                    } else {
                        return new PayPalFallBack();
                    }
                })
                .build();


        return Resilience4jFeign.builder(decorators);
    }
}
