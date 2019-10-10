package com.pop.java8.chapter6;

import com.pop.java8.chapter4.Dish;
import com.pop.java8.chapter4.StreamDemo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

        group();
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


    private static void group(){
        /**
         * 分组
         *
         * 一个常见的数据库操作是根据一个
         * 或者多个属性对集合中的项目进行分组
         * 就像前面讲到货币进行分组的例子一样，如果
         * 用指令式风格来重写的话，就很容易
         * 转化为一个非常容易看懂的语句
         *
         * Collectors.groupingBy
         */

        Map<Dish.Type, List<Dish>> dishedByType
                = StreamDemo.menu.stream().collect(
                        //分类函数
                        Collectors.groupingBy(Dish::getType));

        System.out.println(dishedByType);

        /**
         * 但是，分类函数不一定像方法引用那样可用，因为你分类的
         * 条件可能会比较复杂
         *
         * 例如，将热量不到400卡路里的菜分为“低热量”（diet）
         * 热量400到700卡路里的菜划为普通（normal）
         * 高于700卡路里的划为高热量（fat）
         * 可能由于Dish类的作者没有把这个操作写成一个方法
         * 你无法使用方法引用，但是你可以将这个逻辑写成lambda表达式
         */
        Map<CaloricLevel,List<Dish>> s= StreamDemo.menu.stream().collect(
                Collectors.groupingBy(
                        dish -> {
                            if(dish.getCalories()<=400)return CaloricLevel.DIET;
                            else if(dish.getCalories()>=700)return  CaloricLevel.FAT;
                            else return CaloricLevel.NORMAL;
                        }
                )
        );

        /**
         * 多级分组
         *
         * 要实现多级分组，我们可以使用一个
         * 由双参数版本的Collectors.groupingBy 工厂方法创建
         * 的收集器，它除了普通的分类函数之外，还可以接受Collector
         * 类型的第二个参数。那么要进行二级分组的话，我们可以把
         * 内层的groupingBy传递给外层groupingBy，并定义一个成为流中
         * 项目分类的二级标准。
         */
        Map<Dish.Type,Map<CaloricLevel,List<Dish>>> typeMapMap=
        StreamDemo.menu.stream().collect(
                Collectors.groupingBy(Dish::getType,
                        Collectors.groupingBy(
                                dish -> {
                                    if(dish.getCalories()<=400)return CaloricLevel.DIET;
                                    else if(dish.getCalories()>=700)return  CaloricLevel.FAT;
                                    else return CaloricLevel.NORMAL;
                                }
                        )));
        /**
         * 一般来说，把groupingBy看做“桶”比较容易明白。
         * 第一个groupingBy给每个键建立了一个桶，再由一个下游的收集器
         * 去收集这个放入桶中
         */

        /**
         * 按子组收集数据
         *
         * 其实我们仔细观察可以发现groupingBy的第二个参数
         * 是一个收集器类型，也就是Collector，也就说不一定是groupingBy
         * 之前提到的收集器类型都可以使用
         *
         * 我们可以第二个参数可以数一数有多少个这个类型一共有多少个
         */

        StreamDemo.menu.stream().collect(Collectors.groupingBy(Dish::getType,
                Collectors.counting()));//分类的总数
    }

    enum CaloricLevel{DIET,NORMAL,FAT}
}
