package io.quarkiverse.pi4j.test;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

/** Small helpers for tests. Prefer %test.quarkus.pi4j.mock=true in Quarkus tests. */
public final class Pi4jTestSupport {
    private Pi4jTestSupport() {
    }

    public static Context newMockContext() {
        return Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();
    }

    public static void setHigh(DigitalOutput output) {
        output.high();
    }

    public static void setLow(DigitalOutput output) {
        output.low();
    }

    public static boolean isHigh(DigitalInput input) {
        return input.state() == DigitalState.HIGH;
    }

    public static boolean isLow(DigitalInput input) {
        return input.state() == DigitalState.LOW;
    }
}
