package ru.stellar.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    private String _id;
    private String name;
    private int price;
    private String type;
    private String image;
    private int calories;
} 