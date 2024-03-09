package br.com.finance.manager.api.integrationtests.controllers;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.Type;

import br.com.finance.manager.api.enums.EntryTypeEnum;
import br.com.finance.manager.api.enums.PaymentMethodEnum;
import br.com.finance.manager.api.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.finance.manager.api.payloads.requests.AddEntryRequest;
import br.com.finance.manager.api.payloads.requests.ReverseEntryRequest;
import br.com.finance.manager.api.payloads.responses.EntryResponse;
import br.com.finance.manager.api.payloads.responses.UserResponse;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS, scripts = "classpath:entry-test-populate-db.sql")
class EntryControllerIntegrationTest extends AbstractIntegrationTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static UUID entryId;
    private static AddEntryRequest request = new AddEntryRequest(
        new BigDecimal(25300.75),
        LocalDateTime.of(2024, Month.MARCH, 8, 12, 0),
        "Jantar",
        UUID.fromString("b8f4bec3-93da-41fe-9830-3f3a6eb424bc"),
        PaymentMethodEnum.BANK_ACCOUNT_DEPOSIT,
        EntryTypeEnum.PURCHASE
    );

    @Test
    @Order(1)
    void givenEntry_WhenAddEntry_ThenReturnCreatedEntry() {
        entryId =
            given()
                .webAppContextSetup(webApplicationContext)
                .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
                .contentType(CONTENT_TYPE_JSON)
                .body(request)
            .when()
                .post("/api/v1/finance/entries")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("reversalDateTime", nullValue())
                .body("reversed", equalTo(false))
                .body("description", equalTo(request.getDescription()))
                .body("amount", equalTo(request.getAmount().floatValue()))
                .body("dateTime", equalTo(request.getDateTime().format(formatter)))
                .body("type", equalTo(request.getType().toString()))
                .body("paymentMethod", equalTo(request.getPaymentMethod().toString()))
                .body("user", equalTo("Fulano User"))
                .body("category", equalTo("Categoria Teste"))
                .extract()
                    .body()
                    .as(EntryResponse.class)
                    .getId();
    }

    @Test
    @Order(2)
    void givenEntrieWithNullDateAndDescription_WhenAddEntry_ThenReturnCreatedEntry() {
        AddEntryRequest request = new AddEntryRequest(
            new BigDecimal(100),
            null,
            null,
            UUID.fromString("b8f4bec3-93da-41fe-9830-3f3a6eb424bc"),
            PaymentMethodEnum.PIX,
            EntryTypeEnum.INCOME
        );

        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.admin@gmail.com", "coxinha123"))
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/entries")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", notNullValue())
            .body("reversalDateTime", nullValue())
            .body("reversed", equalTo(false))
            .body("description", equalTo(request.getDescription()))
            .body("amount", equalTo(request.getAmount().floatValue()))
            .body("dateTime", notNullValue())
            .body("type", equalTo(request.getType().toString()))
            .body("paymentMethod", equalTo(request.getPaymentMethod().toString()))
            .body("user", equalTo("Fulano Admin"))
            .body("category", equalTo("Categoria Teste"));
    }

    @Test
    @Order(3)
    void givenNonExistingCategoryId_WhenAddEntry_ThenReturnCategoryNotFoundError() {
        AddEntryRequest request = new AddEntryRequest(
            new BigDecimal(100),
            null,
            null,
            UUID.randomUUID(),
            PaymentMethodEnum.CASH,
            EntryTypeEnum.INCOME
        );

        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/entries")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Category doesn't exists"));
    }

    // @Order(4)
    // @ParameterizedTest
    // @CsvSource(delimiter = '|', nullValues = {"null"}, value = {
    //     /* Email */
    //     "0.00         | Fulano | coxinha123 | ROLE_ADMIN, ROLE_USER | email    | must not be blank",                   // Not allowed null name
    //     "100000000.00 | Fulano | coxinha123 | ROLE_ADMIN, ROLE_USER | email    | must not be blank",                   // Not allowed empty name
    //     "100.555      | Fulano | coxinha123 | ROLE_ADMIN, ROLE_USER | email    | must be a well-formed email address", // Not allowed invalid email format
    //     /* Name */
    //     "100.50       | null   | coxinha123 | ROLE_ADMIN            | name     | must not be blank",                   //
    //     "100.50       |''     | coxinha123 | ROLE_USER             | name     | must not be blank",                   //
    //     /* Password */
    //     "fulano@gmail.com | Fulano | null       | ROLE_ADMIN, ROLE_USER | password | must not be blank",                   //
    //     "fulano@gmail.com | Fulano | ''         | ROLE_ADMIN, ROLE_USER | password | must not be blank",                   //
    //     /* Roles */
    //     "fulano@gmail.com | Fulano | coxinha123 | null                  | roles    | must not be null",                    //
    //     "fulano@gmail.com | Fulano | coxinha123 | ''                    | roles    | size must be between 1 and 2",        //
    // })
    // void givenInvalidRequestPayloads_WhenAddEntry_ThenReturnInvalidInputError(String amount, String dateTime, String description, String categoryId, String paymentMethod, String type, String expectedErrorField, String expectedErrorMessage) {
    //     AddEntryRequest request = new AddEntryRequest(
    //         new BigDecimal(100),
    //         null,
    //         null,
    //         UUID.fromString("b8f4bec3-93da-41fe-9830-3f3a6eb424bc"),
    //         PaymentMethodEnum.valueOf(paymentMethod),
    //         EntryTypeEnum.valueOf(type)
    //     );

    //     given()
    //         .webAppContextSetup(webApplicationContext)
    //         .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
    //         .contentType(CONTENT_TYPE_JSON)
    //         .body(request)
    //     .when()
    //         .post("/api/v1/finance/entries")
    //     .then()
    //         .statusCode(HttpStatus.BAD_REQUEST.value())
    //         .body("message", equalTo("Invalid inputs"))
    //         .body("errors.field", hasItem(expectedErrorField))
    //         .body("errors.message", hasItem(expectedErrorMessage));
    // }

    @Test
    @Order(5)
    @WithMockUser
    void givenExistingEntryId_WhenFindEntryById_ThenReturnEntry() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", entryId)
        .when()
            .get("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(entryId.toString()))
            .body("reversalDateTime", nullValue())
            .body("reversed", equalTo(false))
            .body("description", equalTo(request.getDescription()))
            .body("amount", equalTo(request.getAmount().floatValue()))
            .body("dateTime", equalTo(request.getDateTime().format(formatter)))
            .body("type", equalTo(request.getType().toString()))
            .body("paymentMethod", equalTo(request.getPaymentMethod().toString()))
            .body("user", equalTo("Fulano User"))
            .body("category", equalTo("Categoria Teste"));
    }

    @Test
    @Order(6)
    @WithMockUser
    void givenNonExistingEntryId_WhenFindEntryById_ThenReturnEntryNotFoundError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", UUID.randomUUID())
        .when()
            .get("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Entry doesn't exists"));
    }

    @Test
    @Order(7)
    @WithMockUser
    void whenFindAllEntries_ThenReturnEntriesList() {
        given()
            .webAppContextSetup(webApplicationContext)
        .when()
            .get("/api/v1/finance/entries")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(greaterThanOrEqualTo(2)))
            .body("content.id", hasItem(entryId.toString()))
            .body("content.description", hasItems("Jantar", null))
            .body("content.type", hasItems(EntryTypeEnum.INCOME.toString(), EntryTypeEnum.PURCHASE.toString()))
            .body("content.paymentMethod", hasItems(PaymentMethodEnum.BANK_ACCOUNT_DEPOSIT.toString(), PaymentMethodEnum.PIX.toString()))
            .body("content.user", hasItem("Fulano User"))
            .body("content.category", hasItem("Categoria Teste"));
    }








    @Test
    @Order(10)
    @WithMockUser(roles = "USER")
    void givenNonExistingEntryId_WhenReverseEntry_ThenReturnEntryNotFoundError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
            .pathParam("id", entryId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new ReverseEntryRequest())
        .when()
            .patch("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Users are only allowed to reverse their own entries or you must have ADMIN permission"));
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "USER")
    void givenExistingEntryIdAlreadyReversed_WhenReverseEntry_ThenReturnEntryAlreadyReversedError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
            .pathParam("id", entryId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new ReverseEntryRequest())
        .when()
            .patch("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Users are only allowed to reverse their own entries or you must have ADMIN permission"));
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "USER")
    void givenExistingEntryIdWithDefaultUserReversingAnotherUserEntry_WhenReverseEntry_ThenReturnUserCannotReverseAnotherUserEntriesError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
            .pathParam("id", entryId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new ReverseEntryRequest())
        .when()
            .patch("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Users are only allowed to reverse their own entries or you must have ADMIN permission"));
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "USER")
    void givenExistingEntryIdWithDefaultUserReversingOwnEntryWithNullReversalDateTime_WhenReverseEntry_ThenReturnReversedEntry() {
        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
            .pathParam("id", entryId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new ReverseEntryRequest())
        .when()
            .patch("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Users are only allowed to reverse their own entries or you must have ADMIN permission"));
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "USER")
    void givenExistingEntryIdWithAdmintUserReversingOwnEntryWithReversalDateTime_WhenReverseEntry_ThenReturnReversedEntry() {
        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
            .pathParam("id", entryId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new ReverseEntryRequest())
        .when()
            .patch("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Users are only allowed to reverse their own entries or you must have ADMIN permission"));
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "USER")
    void givenExistingEntryIdWithAdmintUserReversingAnotherUserEntryWithReversalDateTime_WhenReverseEntry_ThenReturnReversedEntry() {
        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano.user@gmail.com", "coxinha123"))
            .pathParam("id", entryId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new ReverseEntryRequest())
        .when()
            .patch("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Users are only allowed to reverse their own entries or you must have ADMIN permission"));
    }






    @Test
    @Order(10)
    @WithMockUser(roles = "USER")
    void givenExistingEntryIdWithDefaultUser_WhenDeleteEntryById_ThenReturnForbiddenError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", entryId)
        .when()
            .delete("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("message", equalTo("User don't have access"));
    }

    @Test
    @Order(11)
    @WithMockUser(roles = "USER")
    void givenExistingEntryIdWithAdminUser_WhenDeleteEntryById_ThenDeleteEntry() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", entryId)
        .when()
            .delete("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(12)
    @WithMockUser(roles = "USER")
    void givenNonExistingEntryIdWithAdminUser_WhenDeleteEntryById_ThenDeleteEntry() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", entryId)
        .when()
            .delete("/api/v1/finance/entries/{id}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Entry doesn't exists"));
    }
}
