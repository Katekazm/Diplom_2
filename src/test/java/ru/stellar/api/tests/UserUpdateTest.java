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

public class UserUpdateTest extends BaseTest {

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
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void testUpdateUserWithAuth() {
        User updatedUser = UserGenerator.generateRandomUser();
        updateUserWithAuth(updatedUser);
    }

    @Step("Обновление данных пользователя с авторизацией")
    private void updateUserWithAuth(User updatedUser) {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch(Constants.USER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail()))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void testUpdateUserWithoutAuth() {
        User updatedUser = UserGenerator.generateRandomUser();
        updateUserWithoutAuth(updatedUser);
    }

    @Step("Обновление данных пользователя без авторизации")
    private void updateUserWithoutAuth(User updatedUser) {
        given()
                .header("Content-type", "application/json")
                .body(updatedUser)
                .when()
                .patch(Constants.USER_ENDPOINT)
                .then()
                .statusCode(401)
                .body("message", equalTo(Constants.UNAUTHORIZED));
    }
} 