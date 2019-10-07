package com.pop.java8.chapter4;

import lombok.Data;


/**
 * @author Pop
 * @date 2019/10/7 17:03
 */
@Data
public class Dish {

    private final String name;

    private final  boolean vegetarian;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    private final  int calories;
    private final Type type;

    public enum Type{MEAT,FISH,OTHER}
}
