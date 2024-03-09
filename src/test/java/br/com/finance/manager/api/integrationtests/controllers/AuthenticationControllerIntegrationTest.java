package br.com.finance.manager.api.integrationtests.controllers;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import br.com.finance.manager.api.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.finance.manager.api.payloads.requests.LoginRequest;

@SpringBootTest
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS, scripts = "classpath:auth-test-populate-db.sql")
class AuthenticationControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void givenExistingEnabledUser_WhenTryToLogin_ThenReturnJwtToken() {
        LoginRequest request = new LoginRequest("fulano.enabled@gmail.com", "coxinha123");
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/auth/login")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("token", notNullValue())
            .body("expires", notNullValue());
    }

    @Test
    void givenDisabledExistingUser_WhenTryToLogin_ThenReturnUserDisabledError() {
        LoginRequest request = new LoginRequest("fulano.disabled@gmail.com", "coxinha123");
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/auth/login")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("message", equalTo("User disabled"));
    }

    @Test
    void givenNonExistingUser_WhenTryToLogin_ThenReturnUnauthorizedError() {
        LoginRequest request = new LoginRequest("fulano.nonexisting@gmail.com", "coxinha123");
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/auth/login")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("message", equalTo("Email or password invalid"));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', nullValues = {"null"}, value = {
        "null             | coxinha123 | email    | must not be blank",                   // Not allowed null email
        "''               | coxinha123 | email    | must not be blank",                   // Not allowed empty email
        "fulano.com       | coxinha123 | email    | must be a well-formed email address", // Not allowed invalid email format
        "fulano@gmail.com | null       | password | must not be blank",                   // Not allowed null password
        "fulano@gmail.com | ''         | password | must not be blank"                    // Not allowed empty password
    })
    void givenInvalidRequestPayloads_WhenTryToLogin_ThenReturnInvalidInputError(String email, String password, String expectedErrorField, String expectedErrorMessage) {
        LoginRequest request = new LoginRequest(email, password);
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/auth/login")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Invalid inputs"))
            .body("errors.field", hasItem(expectedErrorField))
            .body("errors.message", hasItem(expectedErrorMessage));
    }
}
