package br.com.finance.manager.api.integrationtests.controllers;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import br.com.finance.manager.api.enums.RoleNameEnum;
import br.com.finance.manager.api.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.finance.manager.api.payloads.requests.ChangeUserNameRequest;
import br.com.finance.manager.api.payloads.requests.CreateUserRequest;
import br.com.finance.manager.api.payloads.requests.UpdateUserRequest;
import br.com.finance.manager.api.payloads.responses.UserResponse;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private static UUID userId;
    private static CreateUserRequest request = new CreateUserRequest("fulano@gmail.com", "Fulano", "coxinha123", Arrays.asList(RoleNameEnum.ROLE_USER));

    @Test
    @Order(1)
    @WithMockUser(roles = "USER")
    void givenNonExistingUserWithDefaultUser_WhenCreateUser_ThenReturnForbiddenError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/users")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("message", equalTo("User don't have access"));
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "ADMIN")
    void givenNonExistingUserWithUserAdmin_WhenCreateUser_ThenReturnCreatedUser() {
        userId =
            given()
                .webAppContextSetup(webApplicationContext)
                .contentType(CONTENT_TYPE_JSON)
                .body(request)
            .when()
                .post("/api/v1/finance/users")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("disabled", equalTo(false))
                .body("name", equalTo(request.getName()))
                .body("email", equalTo(request.getEmail()))
                .body("roles", hasSize(1))
                .body("roles.name", hasItem(RoleNameEnum.ROLE_USER.toString()))
                    .extract()
                    .body()
                    .as(UserResponse.class)
                    .getId();
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "ADMIN")
    void givenExistingUser_WhenCreateUser_ThenReturnUserAlreadyExistsError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(request)
        .when()
            .post("/api/v1/finance/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("User already exists"));
    }

    @Order(4)
    @WithMockUser(roles = "ADMIN")
    @ParameterizedTest
    @CsvSource(delimiter = '|', nullValues = {"null"}, value = {
        /* Email */
        "null             | Fulano | coxinha123 | ROLE_ADMIN, ROLE_USER | email    | must not be blank",                   // Not allowed null name
        "null             | Fulano | coxinha123 | ROLE_ADMIN, ROLE_USER | email    | must not be blank",                   // Not allowed empty name
        "fulano.com       | Fulano | coxinha123 | ROLE_ADMIN, ROLE_USER | email    | must be a well-formed email address", // Not allowed invalid email format
        /* Name */
        "fulano@gmail.com | null   | coxinha123 | ROLE_ADMIN            | name     | must not be blank",                   //
        "fulano@gmail.com | ''     | coxinha123 | ROLE_USER             | name     | must not be blank",                   //
        /* Password */
        "fulano@gmail.com | Fulano | null       | ROLE_ADMIN, ROLE_USER | password | must not be blank",                   //
        "fulano@gmail.com | Fulano | ''         | ROLE_ADMIN, ROLE_USER | password | must not be blank",                   //
        /* Roles */
        "fulano@gmail.com | Fulano | coxinha123 | null                  | roles    | must not be null",                    //
        "fulano@gmail.com | Fulano | coxinha123 | ''                    | roles    | size must be between 1 and 2",        //
    })
    void givenInvalidRequestPayloads_WhenCreateUser_ThenReturnInvalidInputError(String email, String name, String password, String rolesString, String expectedErrorField, String expectedErrorMessage) {
        List<RoleNameEnum> rolesList = getRolesList(rolesString);

        given()
            .webAppContextSetup(webApplicationContext)
            .contentType(CONTENT_TYPE_JSON)
            .body(new CreateUserRequest(email, name, password, rolesList))
        .when()
            .post("/api/v1/finance/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Invalid inputs"))
            .body("errors.field", hasItem(expectedErrorField))
            .body("errors.message", hasItem(expectedErrorMessage));
    }

    @Test
    @Order(5)
    @WithMockUser
    void givenExistingUserId_WhenFindUserById_ThenReturnUser() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", userId)
        .when()
            .get("/api/v1/finance/users/{id}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(userId.toString()))
            .body("disabled", equalTo(false))
            .body("name", equalTo(request.getName()))
            .body("email", equalTo(request.getEmail()))
            .body("roles", hasSize(1))
            .body("roles.name", hasItem(RoleNameEnum.ROLE_USER.toString()));
    }

    @Test
    @Order(6)
    @WithMockUser
    void givenNonExistingUserId_WhenFindUserById_ThenReturnUserNotFoundError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", UUID.randomUUID())
        .when()
            .get("/api/v1/finance/users/{id}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("User doesn't exists"));
    }

    @Test
    @Order(7)
    @WithMockUser
    void whenFindAllUsers_ThenReturnUsersList() {
        given()
            .webAppContextSetup(webApplicationContext)
        .when()
            .get("/api/v1/finance/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content.disabled", hasItem(false))
            .body("content.name", hasItems("Vitor Silva", "Fulano"))
            .body("content.email", hasItems("vitor.augsilva98@gmail.com", "fulano@gmail.com"))
            .body("content.roles.name", everyItem(hasItem(RoleNameEnum.ROLE_USER.toString())));
    }

    @Test
    @Order(8)
    @WithMockUser(roles = "ADMIN")
    void givenNonExistingUserId_WhenUpdateUserById_ThenReturnUserNotFoundError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", UUID.randomUUID())
            .contentType(CONTENT_TYPE_JSON)
            .body(new UpdateUserRequest(true, null))
        .when()
            .put("/api/v1/finance/users/{id}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("User doesn't exists"));
    }

    @Test
    @Order(9)
    @WithMockUser(roles = "USER")
    void givenExistingUserIdWithDefaultUser_WhenUpdateUserById_ThenReturnForbiddenError() {
        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", userId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new UpdateUserRequest(true, null))
        .when()
            .put("/api/v1/finance/users/{id}")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("message", equalTo("User don't have access"));
    }

    @Order(10)
    @WithMockUser(roles = "ADMIN")
    @ParameterizedTest
    @CsvSource(delimiter = '|', nullValues = {"null"}, value = {
        "true  | true  | ROLE_ADMIN, ROLE_USER | ROLE_ADMIN, ROLE_USER",
        "null  | true  | ''                    | ROLE_ADMIN, ROLE_USER",
        "false | false | ROLE_USER             | ROLE_USER",
        "null  | false | null                  | ROLE_USER",
        "null  | false | ROLE_ADMIN            | ROLE_ADMIN"
    })
    void givenExistingUserId_WhenUpdateUserById_ThenReturnUpdatedUser(Boolean disabled, Boolean expectedDisabledResponse, String rolesString, String expectedRolesStringResponse) {
        List<RoleNameEnum> rolesList = getRolesList(rolesString);
        String [] expectedRolesList = getRolesList(expectedRolesStringResponse).stream().map(role -> role.toString()).toArray(String[]::new);

        given()
            .webAppContextSetup(webApplicationContext)
            .pathParam("id", userId)
            .contentType(CONTENT_TYPE_JSON)
            .body(new UpdateUserRequest(disabled, rolesList))
        .when()
            .put("/api/v1/finance/users/{id}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("disabled", equalTo(expectedDisabledResponse))
            .body("roles.name", hasItems(expectedRolesList));
    }

    @Test
    @Order(11)
    void givenNewUserNameForLoggedUser_WhenChangeUserName_ThenReturnUpdatedUser() {
        String newUserName = "Fulano da Silva";

        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano@gmail.com", "coxinha123"))
            .contentType(CONTENT_TYPE_JSON)
            .body(new ChangeUserNameRequest(newUserName))
        .when()
            .patch("/api/v1/finance/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(newUserName));
    }

    @Order(12)
    @ParameterizedTest
    @CsvSource(delimiter = '|', nullValues = {"null"}, value = {
        "null | name | must not be blank", // Not allowed null name
        "''   | name | must not be blank"  // Not allowed empty name
    })
    void givenInvalidRequestPayloads_WhenChangeUserName_ThenReturnInvalidInputError(String name, String expectedErrorField, String expectedErrorMessage) {
        given()
            .webAppContextSetup(webApplicationContext)
            .header(HttpHeaders.AUTHORIZATION, getJwtToken("fulano@gmail.com", "coxinha123"))
            .contentType(CONTENT_TYPE_JSON)
            .body(new ChangeUserNameRequest(name))
        .when()
            .patch("/api/v1/finance/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Invalid inputs"))
            .body("errors.field", hasItem(expectedErrorField))
            .body("errors.message", hasItem(expectedErrorMessage));
    }

    private List<RoleNameEnum> getRolesList(String rolesString) {
        List<RoleNameEnum> rolesList = null;

        if (rolesString != null) {
            rolesList = new ArrayList<>();

            if (rolesString.contains("ROLE_ADMIN")) {
                rolesList.add(RoleNameEnum.ROLE_ADMIN);
            }

            if (rolesString.contains("ROLE_USER")) {
                rolesList.add(RoleNameEnum.ROLE_USER);
            }
        }

        return rolesList;
    }
}
