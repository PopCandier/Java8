package com.pop.java8.chapter4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Pop
 * @date 2019/10/7 17:05
 */
public class StreamDemo {

    public static List<Dish> menu = Arrays.asList(
        new Dish("pork",false,800, Dish.Type.MEAT),
        new Dish("beef",false,700, Dish.Type.MEAT),
        new Dish("chicken",false,400,Dish.Type.MEAT),
        new Dish("french",true,530, Dish.Type.OTHER),
        new Dish("rice",true,350, Dish.Type.OTHER),
        new Dish("season",true,120, Dish.Type.OTHER),
        new Dish("pizza",true,550, Dish.Type.OTHER),
        new Dish("prawns",false,300, Dish.Type.FISH),
            new Dish("salmon",false,450, Dish.Type.FISH)
    );
    //集合讲的是数据， 流讲的是计算


    public static void main(String[] args) {

//        demo1();

//        demo2();

        /**
         * 外部迭代和内部迭代
         * 我们用的最多的就是外部迭代，只是java8之前我们使用的方式
         */
        List<String> names = new ArrayList<>();
        // 使用for-each进行外部迭代
        for(Dish d:menu){
            names.add(d.getName());
        }
        //使用迭代器迭代
        Iterator<Dish> iterator = menu.iterator();
        while (iterator.hasNext()){
            Dish d = iterator.next();
            names.add(d.getName());
        }
        //使用流
        List<String> names2 = menu.stream().map(Dish::getName).collect(Collectors.toList());

    }

    private static void demo2() {
        List<String> title = Arrays.asList("java8","in","action");
        Stream<String> s = title.stream();
        s.forEach(System.out::println);
        s.forEach(System.out::println);//会打印错误
    }

    private static void demo1() {
        List<String> demo1 = menu.stream().filter(dish -> dish.getCalories()>300)//挑选卡路里300以上的
                .map(Dish::getName)//获取菜名
                .limit(3)//取三个
                .collect(Collectors.toList());//获得集合
        System.out.println(demo1);
    }

}
