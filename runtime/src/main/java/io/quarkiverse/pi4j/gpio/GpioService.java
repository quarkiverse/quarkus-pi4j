package io.quarkiverse.pi4j.gpio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;

import io.quarkiverse.pi4j.runtime.Pi4jConfig;

@ApplicationScoped
public class GpioService {
    @Inject
    Context pi4j;
    @Inject
    Pi4jConfig config;

    public DigitalOutput output(int address) {
        return output(address, "gpio-dout-" + address, "GPIO Output " + address);
    }

    public DigitalOutput output(int address, String id, String name) {
        if (pi4j.hasIO(id))
            return (DigitalOutput) pi4j.io(id);
        DigitalOutputConfig cfg = DigitalOutput.newConfigBuilder(pi4j)
                .id(id)
                .name(name)
                .address(address)
                .initial(DigitalState.LOW)
                .shutdown(DigitalState.LOW)
                .build();
        return config.digitalOutputProvider()
                .map(providerId -> pi4j.provider(providerId, com.pi4j.io.gpio.digital.DigitalOutputProvider.class).create(cfg))
                .orElseGet(() -> pi4j.create(cfg));
    }

    public DigitalInput input(int address) {
        return input(address, "gpio-din-" + address, "GPIO Input " + address);
    }

    public DigitalInput input(int address, String id, String name) {
        if (pi4j.hasIO(id))
            return (DigitalInput) pi4j.io(id);
        DigitalInputConfig cfg = DigitalInput.newConfigBuilder(pi4j)
                .id(id)
                .name(name)
                .address(address)
                .build();
        return config.digitalInputProvider()
                .map(providerId -> pi4j.provider(providerId, com.pi4j.io.gpio.digital.DigitalInputProvider.class).create(cfg))
                .orElseGet(() -> pi4j.create(cfg));
    }

    public DigitalOutput namedOutput(String name) {
        return output(namedAddress(name), "gpio-dout-" + name, "GPIO Output " + name);
    }

    public DigitalInput namedInput(String name) {
        return input(namedAddress(name), "gpio-din-" + name, "GPIO Input " + name);
    }

    public int namedAddress(String name) {
        Integer address = config.pins().get(name);
        if (address == null)
            throw new IllegalArgumentException("No named GPIO configured for '" + name + "'");
        return address;
    }
}
