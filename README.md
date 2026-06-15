# Quarkus Pi4J

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.pi4j/quarkus-pi4j?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse.pi4j/quarkus-pi4j-parent)

Quarkus extension for integrating [Pi4J v4](https://pi4j.com/) with Quarkus applications running on Raspberry Pi hardware. Provides CDI-managed GPIO access, named-pin injection, a readiness health check, and mock support for dev/test environments.

## Features

- Inject `DigitalOutput` and `DigitalInput` pins directly via CDI (`@GpioPin`, `@NamedGpio`)
- Imperative GPIO API via `GpioService`
- Raw `Context` injection for provider-level access
- Mock providers for tests and CI — no hardware required
- SmallRye Health readiness check
- Dev UI panel showing Pi4J context, platforms, and providers
- Configurable provider IDs, pin names, and shutdown behaviour

## Installation

Add the extension to your `pom.xml`:

```xml
<dependency>
  <groupId>io.quarkiverse.pi4j</groupId>
  <artifactId>quarkus-pi4j</artifactId>
  <version>${quarkus-pi4j.version}</version>
</dependency>
```

## Configuration

All properties are prefixed with `quarkus.pi4j`:

| Property | Default | Description |
|---|---|---|
| `quarkus.pi4j.enabled` | `true` | Enable or disable the extension entirely |
| `quarkus.pi4j.mock` | `false` | Use Pi4J mock providers instead of real hardware |
| `quarkus.pi4j.shutdown` | `true` | Automatically shut down the Pi4J Context on stop |
| `quarkus.pi4j.fail-on-startup-error` | `true` | Fail startup if the Pi4J Context cannot be initialized |
| `quarkus.pi4j.pins.<name>` | — | Map a logical name to a BCM GPIO address |
| `quarkus.pi4j.digital-input-provider` | — | Optional provider ID for digital inputs |
| `quarkus.pi4j.digital-output-provider` | — | Optional provider ID for digital outputs |

Example `application.properties`:

```properties
# Named pins — inject with @NamedGpio("led") or GpioService.namedOutput("led")
quarkus.pi4j.pins.led=13
quarkus.pi4j.pins.button=5
quarkus.pi4j.pins.buzzer=6

# Use mock providers in dev and test — no Raspberry Pi required
%dev.quarkus.pi4j.mock=true
%test.quarkus.pi4j.mock=true

# Optional: pin to a specific Pi4J provider ID (omit to let Pi4J choose)
# quarkus.pi4j.digital-input-provider=pigpio-digital-input
# quarkus.pi4j.digital-output-provider=pigpio-digital-output
```

## Usage

### Inject by BCM address (`@GpioPin`)

```java
@Inject
@GpioPin(13)
DigitalOutput led;

@Inject
@GpioPin(value = 5, id = "button", name = "Push Button")
DigitalInput button;
```

### Inject by configured name (`@NamedGpio`)

```java
// requires quarkus.pi4j.pins.led=13
@Inject
@NamedGpio("led")
DigitalOutput led;

// requires quarkus.pi4j.pins.button=5
@Inject
@NamedGpio("button")
DigitalInput button;
```

### Imperative API (`GpioService`)

```java
@Inject
GpioService gpio;

DigitalOutput out    = gpio.output(13);
DigitalInput  in     = gpio.input(5);
DigitalOutput led    = gpio.namedOutput("led");
DigitalInput  button = gpio.namedInput("button");
```

Pins are cached by ID — calling `output(13)` twice returns the same instance.

### Raw Pi4J Context

```java
@Inject
Context pi4j;
```

### Full example

```java
@ApplicationScoped
public class Pi4jApplication {

    @Inject
    Context pi4j;

    @Inject
    @GpioPin(13)
    DigitalOutput led;

    @Inject
    @GpioPin(value = 5, id = "button", name = "Push Button")
    DigitalInput button;

    @Inject
    @NamedGpio("buzzer")
    DigitalOutput buzzer;

    @Inject
    GpioService gpio;

    void onStart(@Observes StartupEvent event) {
        led.low();
        button.addListener(e -> {
            if (e.state().isHigh()) led.high();
            else led.low();
        });
    }

    public void togglePin(int address) {
        DigitalOutput out = gpio.output(address);
        if (out.isHigh()) out.low();
        else out.high();
    }
}
```

## Health Check

When `quarkus-smallrye-health` is on the classpath, the extension registers a `@Readiness` health check named `pi4j`:

```json
{
  "name": "pi4j",
  "status": "UP",
  "data": {
    "enabled": true,
    "mock": false
  }
}
```

If the Context fails to start and `fail-on-startup-error=false`, the check reports `DOWN` with the error details.

## Dev UI

In dev mode (`quarkus:dev`), a **Pi4J** card appears in the Quarkus Dev UI showing:

- Context status (UP / DOWN)
- Active platforms
- Registered providers

## Testing

Enable mock providers in the test profile so tests run without Raspberry Pi hardware:

```properties
%test.quarkus.pi4j.mock=true
```

Full `@QuarkusTest` example:

```java
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
    void ledCanToggle() {
        led.high();
        assertTrue(led.isHigh());
        led.low();
        assertTrue(led.isLow());
    }
}
```

## Project Structure

```
quarkus-pi4j/
├── runtime/                        # Runtime extension module
│   ├── src/main/java/
│   │   └── io/quarkiverse/pi4j/
│   │       ├── runtime/            # Pi4jConfig, Pi4jContextProducer
│   │       ├── gpio/               # GpioPin, NamedGpio, GpioProducer, GpioService
│   │       ├── health/             # Pi4jHealthCheck
│   │       └── devui/              # Pi4jJsonRpcService (Dev UI backend)
│   └── src/test/java/
│       └── io/quarkiverse/pi4j/test/  # Pi4jTestSupport
├── deployment/                     # Build-time extension module
│   ├── src/main/java/
│   │   └── io/quarkiverse/pi4j/deployment/
│   │       ├── Pi4jProcessor.java      # Bean registration, reflection, health
│   │       └── Pi4jDevUiProcessor.java # Dev UI pages and JSON-RPC registration
│   └── src/main/resources/dev-ui/
│       └── qwc-pi4j.js             # Dev UI web component
├── integration-tests/              # Integration test application
└── docs/                           # Extension documentation (Antora)
```

## Requirements

- Java 25+
- Quarkus 3.x
- Pi4J 4.x (automatically pulled as dependency)
- Raspberry Pi hardware (or `quarkus.pi4j.mock=true` for local dev/test)

## Documentation

Full documentation is available at <https://docs.quarkiverse.io/quarkus-pi4j/dev/>.
