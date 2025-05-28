package ru.stellar.api.tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.BeforeClass;
import ru.stellar.api.constants.Constants;

public class BaseTest {
    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = Constants.BASE_URL;
        RestAssured.filters(new AllureRestAssured());
    }

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
} 