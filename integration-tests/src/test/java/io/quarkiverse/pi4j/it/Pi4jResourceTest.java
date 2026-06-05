package io.quarkiverse.pi4j.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class Pi4jResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/pi4j")
                .then()
                .statusCode(200)
                .body(is("Hello pi4j"));
    }
}
