package com.pop.java8.chapter2;

import com.pop.java8.App;
import com.pop.java8.chapter1.Apple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Pop
 * @date 2019/10/2 16:00
 */
public class AppleDemo {

    public static void main(String[] args) {
        /**
         *
         */
        filterApples(null,(Apple a)->"green".equals(a.getColor()));

        List<Apple> result = new ArrayList<>();
        result.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });
        //也可以写成 一般来说，lambda 表达式常常用于内部内
        result.sort((Apple a1,Apple a2)->a1.getWeight().compareTo(a2.getWeight()));

    }

    public static List<Apple> filterApples(List<Apple> inventory,ApplePredicate p){
        List<Apple> result = new ArrayList<>();
        for(Apple apple:inventory){
            if(p.test(apple)){//谓词对象封装了测试苹果的动作，策略设计模式的一种体现
                result.add(apple);
            }
        }
        return  result;
    }

}
