package ru.stellar.api.utils;

import ru.stellar.api.models.User;
import java.util.UUID;

public class UserGenerator {
    public static User generateRandomUser() {
        String email = UUID.randomUUID().toString() + "@example.com";
        String password = UUID.randomUUID().toString();
        String name = "Test User " + UUID.randomUUID().toString().substring(0, 5);
        
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }

    public static User generateUserWithoutField(String fieldToRemove) {
        User user = generateRandomUser();
        switch (fieldToRemove.toLowerCase()) {
            case "email":
                user.setEmail(null);
                break;
            case "password":
                user.setPassword(null);
                break;
            case "name":
                user.setName(null);
                break;
        }
        return user;
    }
} 