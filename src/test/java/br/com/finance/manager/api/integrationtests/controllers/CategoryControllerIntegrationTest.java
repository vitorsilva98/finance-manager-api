package br.com.finance.manager.api.integrationtests.controllers;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import br.com.finance.manager.api.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.finance.manager.api.payloads.requests.CreateCategoryRequest;
import br.com.finance.manager.api.payloads.responses.CategoryResponse;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS, scripts = "classpath:category-test-populate-db.sql")
class CategoryControllerIntegrationTest extends AbstractIntegrationTest {

    private static UUID categoryId;
    private static CreateCategoryRequest request = new CreateCategoryRequest("Cinema");

    @Test
    @Order(1)
    @WithMockUser
    void givenNonExistingCategory_WhenCreateCategory_ThenReturnCreatedCategory() {
        CategoryResponse response =
            given()
                .webAppContextSetup(webApplicationContext)
                .contentType(CONTENT_TYPE_JSON)
                .body(request)
            .when()
                .post("/api/v1/finance/categories")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo(request.getName()))
                    .extract()
                    .body()
                    .as(CategoryResponse.class);

        categoryId = response.getId();
    }

    @Test
    @Order(2)
    @WithMockUser
    void givenExistingCategory_WhenCreateCategory_ThenReturnCategoryAlreadyExistsError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/categories")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Category already exists"));
    }

    @Order(3)
    @WithMockUser
    @ParameterizedTest
    @CsvSource(delimiter = '|', nullValues = {"null"}, value = {
        "null | name | must not be blank", // Not allowed null name
        "''   | name | must not be blank", // Not allowed empty name
    })
    void givenInvalidRequestPayloads_WhenCreateCategory_ThenReturnInvalidInputError(String name, String expectedErrorField, String expectedErrorMessage) {
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(new CreateCategoryRequest(name))
        .when()
            .post("/api/v1/finance/categories")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Invalid inputs"))
            .body("errors.field", hasItem(expectedErrorField))
            .body("errors.message", hasItem(expectedErrorMessage));
    }

    @Test
    @Order(4)
    @WithMockUser
    void givenExistingCategoryId_WhenFindCategoryById_ThenReturnCategory() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", categoryId)
        .when()
            .get("/api/v1/finance/categories/{id}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(categoryId.toString()))
            .body("name", equalTo(request.getName()));
    }

    @Test
    @Order(5)
    @WithMockUser
    void givenNonExistingCategoryId_WhenFindCategoryById_ThenReturnCategoryNotFoundError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", UUID.randomUUID())
        .when()
            .get("/api/v1/finance/categories/{id}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Category doesn't exists"));
    }

    @Test
    @Order(6)
    @WithMockUser
    void whenFindAllCategories_ThenReturnCategoriesList() {
        given()
            .webAppContextSetup(webApplicationContext)
        .when()
            .get("/api/v1/finance/categories")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content.id", hasItem(categoryId.toString()))
            .body("content.name", hasItem(request.getName()));
    }

    @Test
    @Order(7)
    @WithMockUser(roles = "USER")
    void givenExistingCategoryIdWithDefaultUser_WhenDeleteCategoryById_ThenReturnForbiddenError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", categoryId)
        .when()
            .delete("/api/v1/finance/categories/{id}")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("message", equalTo("User don't have access"));
    }

    @Test
    @Order(8)
    @WithMockUser(roles = "ADMIN")
    void givenExistingCategoryIdWithUserAdminAndCategoryNotRelatedWithEntries_WhenDeleteCategoryById_ThenDeleteCategory() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", categoryId)
        .when()
            .delete("/api/v1/finance/categories/{id}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(9)
    @WithMockUser(roles = "ADMIN")
    void givenExistingCategoryIdWithUserAdminAndCategoryRelatedWithEntries_WhenDeleteCategoryById_ThenReturnCategoryAlreadyRelatedWithEntriesError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", UUID.fromString("c225d540-fa04-44ec-9841-73802752d241"))
        .when()
            .delete("/api/v1/finance/categories/{id}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Category already related with entries"));
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "ADMIN")
    void givenNonExistingCategoryIdWithUserAdmin_WhenDeleteCategoryById_ThenReturnCategoryNotFoundError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", UUID.randomUUID())
        .when()
            .delete("/api/v1/finance/categories/{id}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Category doesn't exists"));
    }
}
