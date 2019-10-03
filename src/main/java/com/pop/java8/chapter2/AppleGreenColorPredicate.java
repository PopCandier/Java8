package com.pop.java8.chapter2;

import com.pop.java8.chapter1.Apple;

/**
 * @author Pop
 * @date 2019/10/2 16:04
 */
public class AppleGreenColorPredicate implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return "green".equals(apple.getColor());
    }
}
