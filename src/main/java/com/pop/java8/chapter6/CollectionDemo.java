package com.pop.java8.chapter6;

import com.pop.java8.chapter4.Dish;
import com.pop.java8.chapter4.StreamDemo;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: java8
 * @description:
 * @author: Pop
 * @create: 2019-10-09 11:39
 **/
public class CollectionDemo {

    /**
     * 归约和汇总
     */

    public static void main(String[] args) {
//        count();


    }

    private static void count() {
        //collect 收集者，接受一个收集器 Collector 他的行为将会被收录
        long howManyDishe=
                StreamDemo.menu.stream().collect(Collectors.counting());

        //也有简单的写法
        long howManyDished = StreamDemo.menu.stream().count();

    }

    private static void maxAndMin(){
        /**
         * 如果你想要找出菜单中热量最高的菜
         * 可以使用两个收集器
         * Collectors.maxBy 和 Collectors.minBy
         * 来计算流中最大或最小值，
         * 这两个收集器接受一个Comparator参数来比较流
         * 中的元素。你可以创造一个Comparator来根据热量
         * 对材料进行比较
         */

        Comparator<Dish> dishCaloriesComparator
                = Comparator.comparingInt(Dish::getCalories);

    }
}
