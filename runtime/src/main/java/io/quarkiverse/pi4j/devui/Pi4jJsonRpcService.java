package io.quarkiverse.pi4j.devui;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkiverse.pi4j.runtime.Pi4jConfig;
import io.quarkiverse.pi4j.runtime.Pi4jContextProducer;

@ApplicationScoped
public class Pi4jJsonRpcService {

    @Inject
    Pi4jContextProducer contextProducer;

    @Inject
    Pi4jConfig config;

    public Map<String, Object> getInfo() {
        boolean contextAvailable = contextProducer.ready();

        List<Map<String, String>> platforms = List.of();
        List<Map<String, String>> providers = List.of();

        if (contextAvailable) {
            var ctx = contextProducer.context();
            platforms = ctx.platforms().all().values().stream()
                    .map(p -> Map.of("id", p.id(), "name", p.name()))
                    .collect(Collectors.toList());
            providers = ctx.providers().all().values().stream()
                    .map(p -> Map.of("id", p.id(), "name", p.name(), "type", p.type().name()))
                    .collect(Collectors.toList());
        }

        return Map.of(
                "enabled", config.enabled(),
                "mock", config.mock(),
                "contextAvailable", contextAvailable,
                "platforms", platforms,
                "providers", providers);
    }
}
