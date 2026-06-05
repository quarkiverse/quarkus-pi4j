package io.quarkiverse.pi4j.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class Pi4jProcessor {

    private static final String FEATURE = "pi4j";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
