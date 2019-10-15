package com.pop.java8.chapter4;

import com.pop.java8.chapter6.CollectionDemo;
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

    /**
     * 为了实现 第八章的方法定义，我们需要实现一个
     * 可以完成分组的任务
     */

    public CollectionDemo.CaloricLevel getCaloricLevel(){
        if(this.getCalories()<=400) return CollectionDemo.CaloricLevel.DIET;
        else if (this.getCalories()>=700) return CollectionDemo.CaloricLevel.FAT;
        else return CollectionDemo.CaloricLevel.NORMAL;
    }

}
