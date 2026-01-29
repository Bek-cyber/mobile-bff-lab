package com.project.mobilebfflab.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Resilience4jLoggingConfig {
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    public void subscribeToEvents() {
        circuitBreakerRegistry.getAllCircuitBreakers()
                .forEach(this::subscribe);
    }

    private void subscribe(CircuitBreaker cb) {
        cb.getEventPublisher()
                .onStateTransition(event ->
                        log.warn("CircuitBreaker '{}' сменил состояние: {} -> {}",
                                cb.getName(),
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()
                        )
                );
    }
}
