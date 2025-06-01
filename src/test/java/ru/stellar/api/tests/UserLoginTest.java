package ru.stellar.api.tests;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellar.api.constants.Constants;
import ru.stellar.api.models.User;
import ru.stellar.api.utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTest extends BaseTest {

    private User testUser;
    private String accessToken;

    @Before
    public void registerAndLoginUser() {
        testUser = UserGenerator.generateRandomUser();
        registerUser(testUser);
        loginUserAndGetToken(testUser);
    }

    @Step("Регистрация пользователя")
    private void registerUser(User user) {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.REGISTER_ENDPOINT);
    }

    @Step("Логин пользователя и получение токена")
    private void loginUserAndGetToken(User user) {
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.LOGIN_ENDPOINT);

        if (loginResponse.getStatusCode() == 200) {
            accessToken = loginResponse.jsonPath().getString("accessToken");
        }
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            deleteUserWithToken(accessToken);
        }
    }

    @Step("Удаление пользователя")
    private void deleteUserWithToken(String token) {
        given()
                .header("Authorization", token)
                .when()
                .delete(Constants.USER_ENDPOINT)
                .then()
                .statusCode(202)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Успешный логин пользователя")
    public void testSuccessfulUserLogin() {
        loginWithValidCredentials(testUser);
    }

    @Step("Логин с валидными учетными данными")
    private void loginWithValidCredentials(User user) {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void testLoginWithInvalidCredentials() {
        User invalidUser = UserGenerator.generateRandomUser();
        loginWithInvalidCredentials(invalidUser);
    }

    @Step("Логин с неверными учетными данными")
    private void loginWithInvalidCredentials(User user) {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.LOGIN_ENDPOINT)
                .then()
                .statusCode(401)
                .body("message", equalTo(Constants.INVALID_CREDENTIALS));
    }
} 