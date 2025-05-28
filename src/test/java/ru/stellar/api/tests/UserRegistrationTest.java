package ru.stellar.api.tests;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import org.junit.Test;
import ru.stellar.api.constants.Constants;
import ru.stellar.api.models.User;
import ru.stellar.api.utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserRegistrationTest extends BaseTest {

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void testSuccessfulUserRegistration() {
        User user = UserGenerator.generateRandomUser();
        registerUser(user);
        verifySuccessfulRegistration();
    }

    @Step("Регистрация пользователя")
    private void registerUser(User user) {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.REGISTER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Проверка успешной регистрации")
    private void verifySuccessfulRegistration() {
        // Дополнительные проверки, если нужны
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    public void testRegisterExistingUser() {
        User user = UserGenerator.generateRandomUser();
        registerUserFirstTime(user);
        tryRegisterExistingUser(user);
    }

    @Step("Первая регистрация пользователя")
    private void registerUserFirstTime(User user) {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.REGISTER_ENDPOINT);
    }

    @Step("Попытка повторной регистрации существующего пользователя")
    private void tryRegisterExistingUser(User user) {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.REGISTER_ENDPOINT)
                .then()
                .statusCode(403)
                .body("message", equalTo(Constants.USER_ALREADY_EXISTS));
    }

    @Test
    @DisplayName("Регистрация пользователя без обязательного поля")
    public void testRegisterUserWithoutRequiredField() {
        User user = UserGenerator.generateUserWithoutField("email");
        tryRegisterUserWithoutRequiredField(user);
    }

    @Step("Попытка регистрации пользователя без обязательного поля")
    private void tryRegisterUserWithoutRequiredField(User user) {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.REGISTER_ENDPOINT)
                .then()
                .statusCode(403)
                .body("message", equalTo(Constants.REQUIRED_FIELD));
    }
} 