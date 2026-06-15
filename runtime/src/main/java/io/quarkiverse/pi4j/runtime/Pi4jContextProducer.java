package io.quarkiverse.pi4j.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Pi4jContextProducer {
    private volatile Context context;
    private volatile Throwable startupFailure;

    void onStart(@Observes StartupEvent event, Pi4jConfig config) {
        if (!config.enabled()) {
            return;
        }
        try {
            this.context = config.mock()
                    ? Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build()
                    : Pi4J.newAutoContext();
        } catch (Throwable t) {
            this.startupFailure = t;
            if (config.failOnStartupError()) {
                throw new IllegalStateException("Could not initialize Pi4J Context", t);
            }
        }
    }

    void onStop(@Observes ShutdownEvent event, Pi4jConfig config) {
        if (config.shutdown() && context != null) {
            context.shutdown();
        }
    }

    @Produces
    @ApplicationScoped
    public Context context() {
        if (context == null) {
            if (startupFailure != null) {
                throw new IllegalStateException("Pi4J Context failed during startup", startupFailure);
            }
            throw new IllegalStateException("Pi4J is disabled or Context was not initialized");
        }
        return context;
    }

    public boolean ready() {
        return context != null;
    }

    public Throwable startupFailure() {
        return startupFailure;
    }
}
