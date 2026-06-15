package com.example;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;

import io.quarkiverse.pi4j.gpio.GpioService;
import io.quarkiverse.pi4j.gpio.NamedGpio;
import io.quarkus.test.junit.QuarkusTest;

// %test.quarkus.pi4j.mock=true must be set in src/test/resources/application.properties
@QuarkusTest
class Pi4jQuarkusTest {

    @Inject
    Context pi4j;

    @Inject
    GpioService gpio;

    @Inject
    @NamedGpio("led")
    DigitalOutput led;

    @Test
    void contextIsAvailable() {
        assertNotNull(pi4j);
    }

    @Test
    void ledStartsLow() {
        led.low();
        assertTrue(led.isLow());
    }

    @Test
    void outputCanToggle() {
        DigitalOutput out = gpio.output(13);
        out.high();
        assertTrue(out.isHigh());
        out.low();
        assertTrue(out.isLow());
    }

    @Test
    void namedOutputResolvesAddress() {
        // resolves quarkus.pi4j.pins.led=13 from application.properties
        DigitalOutput namedLed = gpio.namedOutput("led");
        assertNotNull(namedLed);
    }
}
