package com.pop.java8.chapter5;

import com.pop.java8.chapter4.Dish;
import com.pop.java8.chapter4.StreamDemo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Pop
 * @date 2019/10/7 20:43
 */
public class MappingDemo {

    public static void main(String[] args) {
//        demo2();
//        match();
//        find();
        /**
         * 归约 reduce
         */
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6);
        //求和
        int sum=numbers.stream().reduce(0,(a,b)->a+b);
        System.out.println(sum);
        int product=numbers.stream().reduce(1,(a,b)->a*b);
        System.out.println(product);
        /**
         * 当然，java8中，integer现在有一个金泰的sum方法可以对两个方法进行求和
         * 所以上面的代码还可以写成这样
         */
        numbers.stream().reduce(0,Integer::sum);

        //当然只有一个参数的情况下，返回的是一个Optional,这也不奇怪，因为他没有初始值
        Optional<Integer> sum1=numbers.stream().reduce(Integer::sum);

        /**
         * 计算最大值和最小值
         */
        numbers.stream().reduce(Integer::max);
        numbers.stream().reduce(Integer::min);
        //虽然，你完全也可以写成这样子
        numbers.stream().reduce((x,y)->x>y?x:y);//不过明显，前面可读性好一点

        /**
         * 测试题
         * 如何使用map和reduce来计算流中有多少个菜
         * 这里需要说明的是map可以映射成任何东西，这个任何东西
         * 这个东西也可以代表所需要的任何东西
         */
        int count=StreamDemo.menu.stream().map(d->1)
                .reduce(0,(a,b)->a+b);//将所有的项目映射成1，然后进行计算
        long count1 = StreamDemo.menu.stream().count();//当然，这样也可以
    }

    private static void find() {
        /**
         * 查找元素
         *
         * findAny 将会返回当前流中的任意对象，常常和filter结合使用
         */
        Optional<Dish> dish= StreamDemo.menu.stream().filter(Dish::isVegetarian)
                .findAny();
        //但是我们发现，这个时候发返回的是一个叫Optional的东西
        //在这类我们只需要知道，Optional只是一个代表一个值存不存在的容器
        StreamDemo.menu.stream().filter(Dish::isVegetarian)
                .findAny().
                ifPresent(dish1 -> System.out.println(dish1.getName()));//如果存在就打印一个值
    }

    private static void match() {
        /*
        * 查找和匹配
        *
        * allMatch anyMatch noneMatch findFirst findAny
        *
        * 给定一个谓词，查看是是否有符合条件的，返回boolean
        * */

        //查找菜单中是否有素食可以选择
        /**
         * 是否至少匹配一个元素
         */
        if(StreamDemo.menu.stream().anyMatch(Dish::isVegetarian)){
            System.out.println("有素食");
        }

        /**
         * 是否匹配所有元素
         * 也就是给定条件，是否都满足列表中所有项目
         * 例如，是否所有菜品的热量全部都低于1000卡路里
         */
        boolean isHealthy=StreamDemo.
                menu.stream().allMatch(dish -> dish.getCalories()>1000);

        /**
         * 与allMatch对应的是noneMatch，确保没有任何元素和给定谓词匹配
         */
        boolean isHealthy1=StreamDemo.
                menu.stream().noneMatch(dish -> dish.getCalories()<=1000);
    }

    private static void demo2() {
        //        demo1();
        /*
        * 给定两个数字列表，如何返回所有的数对，例如给定【1，2，3】和列表【3，4】
        * 应该返回【（1，3），（1，4）...】
        * */

        List<Integer> numbers1 = Arrays.asList(1,2,3);
        List<Integer> numbers2 = Arrays.asList(3,4);

        List<int[]> pairs = numbers1.
                stream().flatMap(i->numbers2.stream().map(j->new int[]{i,j}))
                .collect(Collectors.toList());

        /**
         * 如何返回总和能被3整除的数
         */
        List<int[]> pairs1 = numbers1.stream()
                .flatMap(i->numbers2.stream().filter(j->(i+j)%3==0).map(j->new int[]{i,j}))
                .collect(Collectors.toList());
    }

    private static void demo1() {
        /**
         * 给定一个字符串数组，需要将他们的长度运算出来，并放入一个另一个集合中
         */
        List<String> words = Arrays.asList("Java 8","Lambdas","In","Action");
        //其实这一步很简单
        List<Integer> worldLengths=words.
                stream().map(String::length).collect(Collectors.toList());

        //如果回到之前提取菜名的例子，也可以这样做
        List<Integer> worldLengths1= StreamDemo.menu.stream().
                map(Dish::getName)//首先映射出名字
                .map(String::length)//再映射出字符串长度
                .collect(Collectors.toList());

        /**
         * 流的扁平化
         * 如果是你遇到的不是同一个流怎么办，也就是说
         * 可能是两个不同的流
         */
        List<String> arrays = Arrays.asList("Hello","World");
        //如果你需要把它映射成["H","e","l"]..这样的表可能会这样做
        arrays.stream().map(worlds->worlds.split(""))
                .distinct().collect(Collectors.toList());
        //但是，这样map出来的东西是一个数组也就是String[]
        //你的流出来的不是List<String> 而是 List<String[]>
        //也就是说，你这个时候得到的是两个不同的Stream<String[]>流
        //而不是一串Stream<String> ,
        /*
        * 当然，我们可以假设，我们需要的不是一个字符组流，而是一个字符串的流
        * */
        String[] arrayOfWords = {"Goodbye","World"};
        Stream<String> streamOfwords = Arrays.stream(arrayOfWords);
        //再次放到流水线上
        List<Stream<String>> list=arrays.stream().map(w->w.split(""))
                .map(Arrays::stream).distinct().collect(Collectors.toList());
        //但是很显然，这也是有问题的，因为这返回的是Stream<String>的List
        //使用flatMap
        List<String> lists=words.stream().map(w->w.split(""))
                .flatMap(Arrays::stream)//可以将多个stream合并成一个
                .distinct().collect(Collectors.toList());
    }

}
