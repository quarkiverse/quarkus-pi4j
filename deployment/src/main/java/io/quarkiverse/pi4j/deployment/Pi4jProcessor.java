package io.quarkiverse.pi4j.deployment;

import io.quarkiverse.pi4j.devui.Pi4jJsonRpcService;
import io.quarkiverse.pi4j.gpio.GpioProducer;
import io.quarkiverse.pi4j.gpio.GpioService;
import io.quarkiverse.pi4j.health.Pi4jHealthCheck;
import io.quarkiverse.pi4j.runtime.Pi4jConfig;
import io.quarkiverse.pi4j.runtime.Pi4jContextProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ConfigMappingBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

public class Pi4jProcessor {
    private static final String FEATURE = "pi4j";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(
                        Pi4jContextProducer.class,
                        GpioService.class,
                        GpioProducer.class,
                        Pi4jHealthCheck.class,
                        Pi4jJsonRpcService.class)
                .setUnremovable()
                .build();
    }

    @BuildStep
    ConfigMappingBuildItem configMapping() {
        return new ConfigMappingBuildItem(Pi4jConfig.class, "quarkus.pi4j");
    }

    @BuildStep
    HealthBuildItem health() {
        return new HealthBuildItem("io.quarkiverse.pi4j.health.Pi4jHealthCheck", true);
    }

    @BuildStep
    ReflectiveClassBuildItem reflection() {
        return ReflectiveClassBuildItem.builder(
                "com.pi4j.plugin.mock.platform.MockPlatform",
                "com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider",
                "com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider")
                .methods().constructors().build();
    }
}
