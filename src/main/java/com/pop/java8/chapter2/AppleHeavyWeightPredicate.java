package com.pop.java8.chapter2;

import com.pop.java8.chapter1.Apple;

/**
 * @author Pop
 * @date 2019/10/2 16:03
 * 只针对苹果的重量 选择
 */
public class AppleHeavyWeightPredicate implements ApplePredicate{


    @Override
    public boolean test(Apple apple) {
        return apple.getWeight()>150;
    }
}
