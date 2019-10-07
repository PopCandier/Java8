package com.pop.java8.chapter5;

import com.pop.java8.chapter4.Dish;
import com.pop.java8.chapter4.StreamDemo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Pop
 * @date 2019/10/7 22:28
 */
public class AppDemo {

    static Trader raoul = new Trader("Raoul","Cambridge");
    static Trader mario = new Trader("Mario","Milan");
    static Trader alan = new Trader("Alan","Cambridge");
    static Trader brian = new Trader("Brian","Cambridge");

    static List<Transaction> transactions =
            Arrays.asList(
                    new Transaction(brian,2011,300),
                    new Transaction(raoul,2012,100),
                    new Transaction(raoul,2011,400),
                    new Transaction(mario,2012,710),
                    new Transaction(alan,2012,950)
            );


    private static void one(){
        //找出2011年发生过所有交易，并按交易额排序（从高到低）
        List<Transaction> ts=transactions.stream().filter(transaction -> transaction.getYear()==2012).
                sorted(Comparator.comparing(Transaction::getValue)).collect(Collectors.toList());
        System.out.println(ts);
    }

    private static void two(){
        //交易员都在那些不同的城市工作过
        List<String> c=transactions.stream().
                map(t->t.getTrader().getCity()).distinct().collect(Collectors.toList());
        System.out.println(c);
    }

    private static void three(){
        //查找所有来自剑桥的交易员，按照名字排序
        List<Trader> ts=transactions.stream()
                .map(t->t.getTrader())
                .filter(t->"Cambridge".equals(t.getCity())).collect(Collectors.toList());
        ts.sort(Comparator.comparing(Trader::getName));
        System.out.println(ts);
    }

    private static boolean five(){
        //有咩有交易员在米兰工作
        return transactions.stream().
                filter(t->"Cambridge".equals(t.getTrader().getCity())).findAny().isPresent();
    }

    private static void six(){

        int sum=transactions.stream().
                filter(transaction -> "Milan".equals(transaction.getTrader().getCity()))
                .map(transaction -> transaction.getValue())
                .reduce(0,Integer::sum);
        System.out.println(sum);

    }

    private static void seven(){
        //所有交易额中的最大交易额是多少
        int max=transactions.stream().map(transaction -> transaction.getValue()).reduce(0,Integer::max);
        System.out.println(max);

        transactions.stream().max(Comparator.comparing(Transaction::getValue));
    }

    private static void night(){
        //所有交易额中的最大交易额是多少
        transactions.stream().map(transaction -> transaction.getValue()).reduce(Integer::min).ifPresent(
                System.out::println
        );
        transactions.stream().min(Comparator.comparing(Transaction::getValue));
    }

    public static void main(String[] args) {

//        one();
//        two();;
//        three();
//        if(five()){
//            System.out.println("存在来自米兰的人");
//        }
//        six();
//        seven();
//        night();

        /**
         * 但这里需要说明的是，reduce方法计算了流中元素的总和
         * 里面有一个暗含的装箱成本，每个Integer都必须拆箱成一个原始类型
         * 再进行求和，所以这样里会有一个性能消耗
         * 好在Stream中存在一个原始类型流特化
         * IntStream，DoubleStream，LongStream分别将他们流中的
         * 元素特化为int，long，double
         *
         * 要记住的是，这些特化的原因并并不在于流的复杂性，而是装箱
         * 造成的复杂性，即类型int和Integer之间的效率差异
         */
        //所以，计算总数你可以这样操作
        int calories= StreamDemo.menu.stream().mapToInt(Dish::getCalories).sum();
        //如果流是空的，默认返回0，当然IntStream还有min，average等方法
        //当然，你也可以转换成之前的对象流
        IntStream intStream = StreamDemo.menu.stream().mapToInt(Dish::getCalories);
        Stream<Integer> stream = intStream.boxed();

        //特价的Optional 即 OptionalInt OptionalDouble OptionLong
        OptionalInt maxCalories=
                StreamDemo.menu.stream().mapToInt(Dish::getCalories).max();
        //当然，这是算出最大值，你也可以显式的指定一个默认
        int max = maxCalories.orElse(1);

        /**
         * 数值范围
         *
         * 和数字打交道，一个常用的东西就是数值的范围
         * 如果你想要生成1-100之间的所有数字
         * java8引入了IntStream和LongStream的静态方法
         * range 和 rangClosed 这两个方法都是第一个参数接受初始值
         * 第二个接受结束之，但是rang不包含结束值，后者包含，也就是rangClosed
         */

        IntStream eventNumber =IntStream.rangeClosed(1,100)
                .filter(n->n%2==0);//得到一个1-100之间和可以被2整除的int流
        System.out.println(eventNumber.count());

    }


}
