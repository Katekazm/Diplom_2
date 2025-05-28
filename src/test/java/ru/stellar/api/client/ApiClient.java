package ru.stellar.api.client;

import io.restassured.response.Response;
import ru.stellar.api.constants.Constants;
import ru.stellar.api.models.User;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiClient {

    public static Response registerUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.REGISTER_ENDPOINT);
    }

    public static Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.LOGIN_ENDPOINT);
    }

    public static Response updateUser(User user, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(Constants.USER_ENDPOINT);
    }
    
     public static Response updateUserWithoutAuth(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(Constants.USER_ENDPOINT);
    }

    public static Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(Constants.USER_ENDPOINT);
    }
    
    public static List<String> getIngredientHashes() {
         return given()
                 .when()
                 .get("/ingredients")
                 .then()
                 .statusCode(200)
                 .extract()
                 .jsonPath().getList("data._id");
    }
} 