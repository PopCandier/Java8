package com.pop.java8.chapter3;

import com.pop.java8.App;
import com.pop.java8.chapter1.Apple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Pop
 * @date 2019/10/3 11:46
 */
public class LambdaExperss {

    /**
     * 接下来对下面这段代码进行改造
     *
     * 注意，如果你使用了java7中带资源的try语句，它已经简化了代码，因为你不需要
     * 显示的关闭资源了。
     */
    public static String processFile() throws IOException{
        try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))){
            return br.readLine();
        }
    }

    /**
     * 但以上的例子很有大局限性，因为他只能读取一行，如果你想要拥有更加丰富的返回
     * 这就有问题了，例如返回头两行，返回最频繁的字，
     * 所以我们应该把方法传递进去，类似接受一个BufferedReader，返回一个String的result
     */

    /**
     * 在定义好一个函数式接口的情况下，我们可以这样改造
     */
    public static String processFile(BufferedReaderProcessor p) throws IOException{
        try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))){
            return p.process(br);
        }
    }

    /**
     * 接着传递行为
     */
    public static void main(String[] args) throws IOException {
//        String oneLine = processFile((BufferedReader br)->br.readLine()+br.readLine());


        List<Integer> l = map(Arrays.asList("lambdas","in","action"),
                (String s)->s.length());
        System.out.println(l);

        Supplier<Apple> c2 = ()-> new Apple();
        Supplier<Apple> c1 = Apple::new;//指向默认构造函数

        //如果你的构造方法是有参数的话，那你就需要思考一下用其他的函数式接口
        Function<Integer,Apple> f1 = (weight)->new Apple(weight);
        Function<Integer,Apple> f2 = (Integer weight)->new Apple(weight);
        //由于定义了一个传入integer而产生一个apple的行为接口，所以上面也可以简化成这样样子
        Function<Integer,Apple> f3 = Apple::new;
        Apple apple = f3.apply(110);


    }

    public void test(){
        //一个综合练习
        List<Apple> inventory = Arrays.asList(new Apple(),new Apple());
        //如果要比较苹果之间的价格，首先我们需要实现sort方法
        inventory.sort(new AppleComparator());

        //当然还可以使用匿名内部类
        inventory.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });

        //接着使用lambda
        inventory.sort((Apple o1,Apple o2)->o1.getWeight().compareTo(o2.getWeight()));
        //java编译器可以根据lambda上下文判断，所以这一段还可以写成
        inventory.sort((o1,o2)->o1.getWeight().compareTo(o2.getWeight()));
        //Comparator具有一个叫做comparing的静态辅助方法，他可以接受
        //一个Function来提取Comparable键值，并生成一个Comparator对象
        Comparator<Apple> c = Comparator.comparing((a)->a.getWeight());
        inventory.sort(Comparator.comparing(a->a.getWeight()));


        //这一步已经看起来有点紧凑了，但是我们可以使用方法应用
        //再去简化他
        inventory.sort(Comparator.comparing(Apple::getWeight));

        //反序
        inventory.sort(Comparator.comparing(Apple::getWeight).reversed());
        //如果发现两个苹果一样重，这就需要加入第二个比较器
        inventory.sort(Comparator.comparing(Apple::getWeight).reversed()
        .thenComparing(Apple::getColor));//当重量无法用做比较参数的时候，加入颜色
    }

    class AppleComparator implements Comparator<Apple>{
        @Override
        public int compare(Apple o1, Apple o2) {
            return o1.getWeight().compareTo(o2.getWeight());
        }
    }


    public static <T,R> List<R> map(List<T> list, Function<T,R> f){

        List<R> result = new ArrayList<>();
        for (T t : list) {
            //这里是某个行为，可以将每个key的字符长度统计出来
            result.add(f.apply(t));
        }
        return result;
    }

}
