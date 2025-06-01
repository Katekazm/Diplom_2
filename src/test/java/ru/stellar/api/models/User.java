package ru.stellar.api.models;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class User {
    private String email;
    private String password;
    private String name;
} 