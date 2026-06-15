package io.quarkiverse.pi4j.gpio;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;

@Dependent
public class GpioProducer {
    @Inject
    GpioService gpio;

    @Produces
    @GpioPin(0)
    public DigitalOutput digitalOutputByPin(InjectionPoint ip) {
        GpioPin pin = ip.getAnnotated().getAnnotation(GpioPin.class);
        return gpio.output(pin.value(), id(pin, "gpio-dout-" + pin.value()), name(pin, "GPIO Output " + pin.value()));
    }

    @Produces
    @GpioPin(0)
    public DigitalInput digitalInputByPin(InjectionPoint ip) {
        GpioPin pin = ip.getAnnotated().getAnnotation(GpioPin.class);
        return gpio.input(pin.value(), id(pin, "gpio-din-" + pin.value()), name(pin, "GPIO Input " + pin.value()));
    }

    @Produces
    @NamedGpio("")
    public DigitalOutput digitalOutputByName(InjectionPoint ip) {
        NamedGpio named = ip.getAnnotated().getAnnotation(NamedGpio.class);
        return gpio.namedOutput(named.value());
    }

    @Produces
    @NamedGpio("")
    public DigitalInput digitalInputByName(InjectionPoint ip) {
        NamedGpio named = ip.getAnnotated().getAnnotation(NamedGpio.class);
        return gpio.namedInput(named.value());
    }

    private static String id(GpioPin pin, String fallback) {
        return pin.id().isBlank() ? fallback : pin.id();
    }

    private static String name(GpioPin pin, String fallback) {
        return pin.name().isBlank() ? fallback : pin.name();
    }
}
