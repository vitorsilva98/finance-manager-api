package br.com.finance.manager.api.integrationtests.controllers;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.finance.manager.api.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest
class HealthCheckControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void whenRequestHealthCheck_ThenReturnLocalDateTime() {
        given()
            .webAppContextSetup(webApplicationContext)
        .when()
            .get("/health")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body(notNullValue());
    }
}
