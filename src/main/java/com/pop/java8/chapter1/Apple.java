package com.pop.java8.chapter1;

import lombok.Data;

/**
 * @author Pop
 * @date 2019/10/2 14:42
 */
@Data
public class Apple {
    private Integer weight;
    private String color;

    public Apple(Integer weight) {
        this.weight = weight;
    }

    public Apple() {
    }

    public static boolean isGreenApple(Apple apple){
        return "green".equals(apple.getColor());
    }

    public static boolean isHeavyApple(Apple apple){
        return apple.getWeight()>150;
    }
}
