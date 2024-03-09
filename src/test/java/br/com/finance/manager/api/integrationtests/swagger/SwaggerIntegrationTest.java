package br.com.finance.manager.api.integrationtests.swagger;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.finance.manager.api.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest
class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void whenRequestSwaggerPage_ThenReturnConfiguredElements() {
        given()
            .webAppContextSetup(webApplicationContext)
        .when()
            .get("/swagger-ui/index.html")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("Swagger UI"));
    }
}
