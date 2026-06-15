package com.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;

import io.quarkiverse.pi4j.gpio.GpioPin;
import io.quarkiverse.pi4j.gpio.GpioService;
import io.quarkiverse.pi4j.gpio.NamedGpio;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Pi4jApplication {

    // Raw Pi4J context — use when you need provider-level access
    @Inject
    Context pi4j;

    // Inject a DigitalOutput by BCM address
    @Inject
    @GpioPin(13)
    DigitalOutput led;

    // Inject a DigitalInput by BCM address with explicit id and name
    @Inject
    @GpioPin(value = 5, id = "button", name = "Push Button")
    DigitalInput button;

    // Inject by logical name — requires quarkus.pi4j.pins.buzzer=<address>
    @Inject
    @NamedGpio("buzzer")
    DigitalOutput buzzer;

    // Imperative API — useful when the address is only known at runtime
    @Inject
    GpioService gpio;

    void onStart(@Observes StartupEvent event) {
        led.low();
        buzzer.low();

        button.addListener(e -> {
            if (e.state().isHigh()) {
                led.high();
            } else {
                led.low();
            }
        });
    }

    public void blink() {
        led.high();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        led.low();
    }

    public void togglePin(int address) {
        DigitalOutput out = gpio.output(address);
        if (out.isHigh())
            out.low();
        else
            out.high();
    }
}
