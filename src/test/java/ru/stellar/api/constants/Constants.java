package ru.stellar.api.constants;

public class Constants {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    public static final String REGISTER_ENDPOINT = "/auth/register";
    public static final String LOGIN_ENDPOINT = "/auth/login";
    public static final String USER_ENDPOINT = "/auth/user";
    public static final String ORDERS_ENDPOINT = "/orders";
    public static final String INGREDIENTS_ENDPOINT = "/ingredients";
    
    // Сообщения об ошибках
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String REQUIRED_FIELD = "Email, password and name are required fields";
    public static final String INVALID_CREDENTIALS = "email or password are incorrect";
    public static final String UNAUTHORIZED = "You should be authorised";
    public static final String INVALID_INGREDIENTS = "Ingredient ids must be provided";
} 