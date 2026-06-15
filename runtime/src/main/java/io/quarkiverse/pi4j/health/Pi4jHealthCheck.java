package io.quarkiverse.pi4j.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import io.quarkiverse.pi4j.runtime.Pi4jConfig;
import io.quarkiverse.pi4j.runtime.Pi4jContextProducer;

@Readiness
@ApplicationScoped
public class Pi4jHealthCheck implements HealthCheck {
    @Inject
    Pi4jConfig config;
    @Inject
    Pi4jContextProducer producer;

    @Override
    public HealthCheckResponse call() {
        var builder = HealthCheckResponse.named("pi4j")
                .withData("enabled", config.enabled())
                .withData("mock", config.mock());

        if (!config.enabled()) {
            return builder.up().withData("reason", "disabled").build();
        }
        if (producer.ready()) {
            return builder.up().build();
        }
        Throwable failure = producer.startupFailure();
        if (failure != null) {
            return builder.down().withData("error", failure.getClass().getName() + ": " + failure.getMessage()).build();
        }
        return builder.down().withData("reason", "context-not-ready").build();
    }
}
