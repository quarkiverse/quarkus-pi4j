package io.quarkiverse.pi4j.runtime;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "quarkus.pi4j")
public interface Pi4jConfig {
    /** Enable or disable the Pi4J extension. */
    @WithDefault("true")
    boolean enabled();

    /** Use Pi4J mock providers. Recommended in dev/test/CI. */
    @WithDefault("false")
    boolean mock();

    /** Automatically shutdown Context when Quarkus stops. */
    @WithDefault("true")
    boolean shutdown();

    /** Fail startup if Pi4J cannot create a Context. */
    @WithDefault("true")
    boolean failOnStartupError();

    /** Named pins, for example quarkus.pi4j.pins.led=13. */
    Map<String, Integer> pins();

    /** Optional default digital input provider ID. Empty means Pi4J chooses. */
    Optional<String> digitalInputProvider();

    /** Optional default digital output provider ID. Empty means Pi4J chooses. */
    Optional<String> digitalOutputProvider();
}
