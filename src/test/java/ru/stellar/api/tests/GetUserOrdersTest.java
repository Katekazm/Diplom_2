package ru.stellar.api.tests;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellar.api.constants.Constants;
import ru.stellar.api.models.Ingredient;
import ru.stellar.api.models.User;
import ru.stellar.api.utils.UserGenerator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetUserOrdersTest extends BaseTest {

    private User testUser;
    private String accessToken;
    private List<Ingredient> ingredients;

    @Before
    public void setUp() {
        testUser = UserGenerator.generateRandomUser();
        registerUser(testUser);
        loginUserAndGetToken(testUser);
        ingredients = getIngredients();
        createOrderWithAuth(ingredients);
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

    @Step("Получение списка ингредиентов")
    private List<Ingredient> getIngredients() {
        Response response = given()
                .when()
                .get(Constants.INGREDIENTS_ENDPOINT);
        return response.jsonPath().getList("data", Ingredient.class);
    }

    @Step("Создание заказа с авторизацией")
    private void createOrderWithAuth(List<Ingredient> ingredients) {
        List<String> ingredientIds = ingredients.stream()
                .map(Ingredient::get_id)
                .collect(Collectors.toList());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredientIds);
        
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .post(Constants.ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
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
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void testGetUserOrdersWithAuth() {
        getUserOrdersWithAuth();
    }

    @Step("Получение заказов пользователя с авторизацией")
    private void getUserOrdersWithAuth() {
        given()
                .header("Authorization", accessToken)
                .when()
                .get(Constants.ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void testGetUserOrdersWithoutAuth() {
        getUserOrdersWithoutAuth();
    }

    @Step("Получение заказов пользователя без авторизации")
    private void getUserOrdersWithoutAuth() {
        given()
                .when()
                .get(Constants.ORDERS_ENDPOINT)
                .then()
                .statusCode(401)
                .body("message", equalTo(Constants.UNAUTHORIZED));
    }
} 