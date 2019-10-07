package com.pop.java8.chapter5;

import lombok.Data;

/**
 * @author Pop
 * @date 2019/10/7 22:25
 */
@Data
public class Trader {

    private final String name;
    private final String city;

    public Trader(String name, String city) {
        this.name = name;
        this.city = city;
    }
}
