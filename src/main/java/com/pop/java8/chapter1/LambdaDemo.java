package com.pop.java8.chapter1;



import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Pop
 * @date 2019/10/2 14:24
 */
public class LambdaDemo {


    /**
     * 传递代码
     */
    public static List<Apple> filterGreenApples(List<Apple> inventory){
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if("green".equals(apple.getColor())){
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterGreenApples1(List<Apple> inventory){
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if(apple.getWeight()>150){
                result.add(apple);
            }
        }
        return result;
    }
    /**
     * 当然上面两个例子其实只有一个地方存在不同，如果只是这一点差异的话
     * 完全可以将制定的条件传递给filter方法就可以了
     */



    public interface Predicate<T>{
        boolean test(T t);
    }

    static List<Apple> filterApples(List<Apple> inventory,Predicate<Apple> p){
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if(p.test(apple)){
                result.add(apple);
            }
        }
        return result;
    }

    public static void main(String[] args) {

        /**
         * 普通的写法，将一个匿名对象传入进去后，实现相应的方法
         * 包再一个FileFilter对象中，然后传递给File.listFiles
         */
        File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isHidden();
            }
        }) ;
        /*
        但这个看起来比想象中的要复杂,Java8 中你可以写成这样
         */
        File[] hiddenFiles1 = new  File(".").listFiles(File::isHidden);
        /**
         * 这是方法引用语法 ：：，意味着将这个方法作为值，传递给listFiles方法
         */


        //经过条件方法的简化，我们可以这样写\
        List<Apple> inventory = new ArrayList<>();
        filterApples(inventory,Apple::isGreenApple);
        filterApples(inventory,Apple::isHeavyApple);


        /**
         * 如果只是使用一次的话，我不希望定义那么长的定义
         *
         * 同样，相同返回值和相同参数，你就可以这样写
         */

        filterApples(inventory,(Apple a)->"green".equals(a.getColor()));

        filterApples(inventory,(Apple a)->a.getWeight()>150);

        filterApples(inventory,(Apple a)->a.getWeight()<80||
                "brown".equals(a.getColor()));



    }


}
