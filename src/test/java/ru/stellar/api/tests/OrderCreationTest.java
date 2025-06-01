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
import java.util.stream.Collectors;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class OrderCreationTest extends BaseTest {

    private User testUser;
    private String accessToken;
    private List<Ingredient> ingredients;

    @Before
    public void setUp() {
        testUser = UserGenerator.generateRandomUser();
        registerUser(testUser);
        loginUserAndGetToken(testUser);
        ingredients = getIngredients();
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
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        createOrderWithAuth(ingredients);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    public void testCreateOrderWithAuthWithoutIngredients() {
        createOrderWithAuth(List.of());
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
                .statusCode(ingredients.isEmpty() ? 400 : 200)
                .body(ingredients.isEmpty() ? "message" : "success", 
                      ingredients.isEmpty() ? equalTo("Ingredient ids must be provided") : equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    public void testCreateOrderWithoutAuthWithIngredients() {
        createOrderWithoutAuth(ingredients);
    }

    @Step("Создание заказа без авторизации")
    private void createOrderWithoutAuth(List<Ingredient> ingredients) {
        List<String> ingredientIds = ingredients.stream()
                .map(Ingredient::get_id)
                .collect(Collectors.toList());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredientIds);
        
        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(Constants.ORDERS_ENDPOINT)
                .then()
                .statusCode(ingredients.isEmpty() ? 400 : 200)
                .body(ingredients.isEmpty() ? "message" : "success", 
                      ingredients.isEmpty() ? equalTo("Ingredient ids must be provided") : equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации без ингредиентов")
    public void testCreateOrderWithoutAuthWithoutIngredients() {
        createOrderWithoutAuth(List.of());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        List<Ingredient> invalidIngredients = List.of(
                new Ingredient("invalid_hash", "invalid_name", 100, "invalid_type", "invalid_image", 1)
        );
        createOrderWithInvalidIngredients(invalidIngredients);
    }

    @Step("Создание заказа с неверными ингредиентами")
    private void createOrderWithInvalidIngredients(List<Ingredient> invalidIngredients) {
        List<String> invalidIds = invalidIngredients.stream()
                .map(Ingredient::get_id)
                .collect(Collectors.toList());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", invalidIds);
        
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .post(Constants.ORDERS_ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("One or more ids provided are incorrect"));
    }
}