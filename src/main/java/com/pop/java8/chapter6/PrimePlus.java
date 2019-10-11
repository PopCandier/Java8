package com.pop.java8.chapter6;

import com.pop.java8.chapter1.LambdaDemo;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * @author Pop
 * @date 2019/10/12 0:06
 *
 * 得到质数和非质量的列表
 * 其中T、A和R分别是流中元素的类型、用于累积部分结果的对象类型，以及collect操作最
 * 终结果的类型。这里应该收集Integer流，而累加器和结果类型则都是Map<Boolean,
 * List<Integer>>（和先前代码清单6-6中分区操作得到的结果Map相同），键是true和false，
 *
 */
public class PrimePlus implements Collector<Integer,Map<Boolean,List<Integer>>,
        Map<Boolean,List<Integer>>> {

    /**
     * 我们可以对之前的质数的收集做一个优化，
     * 定义自己的质数收集器
     *
     * 一个可能的优化是仅仅看看被测试数是不是能够被质数整除。要是除数本身都不是质数就用
     * 不着测了。如果被测试的数据可以被之前的质数整除，那么他就是质数，因为质数只能被1和自己整数
     *
     * 所以我们可以仅仅用被测试数之前的质数来测试。然而我们目前所见的预定义收集器
     * 的问题，也就是必须自己开发一个收集器的原因在于，在收集过程中是没有办法访问部分结果的。
     * 这意味着，当测试某一个数字是否是质数的时候，你没法访问目前已经找到的其他质数的列表。
     *
     * 假设你有这个列表，那就可以把它传给isPrime方法，将方法重写如下：
     */
    public static boolean isPrime(List<Integer> primes,int candidate){
        return primes.stream().noneMatch(i->candidate%i==0);
    }

    /**
     * 而且还应该应用先前的优化，仅仅用小于被测数平方根的质数来测试。因此，你需要想办法
     * 在下一个质数大于被测数平方根时立即停止测试。不幸的是，Stream API中没有这样一种方法。
     * 你可以使用filter(p -> p <= candidateRoot)来筛选出小于被测数平方根的质数。但filter
     * 要处理整个流才能返回恰当的结果。如果质数和非质数的列表都非常大，这就是个问题了。你用
     * 不着这样做；你只需在质数大于被测数平方根的时候停下来就可以了。因此，我们会创建一个名
     * 为takeWhile的方法，给定一个排序列表和一个谓词，它会返回元素满足谓词的最长前缀：
     */
    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p){
        int i =0;
        for(A item:list){
            if(!p.test(item)){//检查列表中的当前项目是否满足谓词
                return list.subList(0,i);
                //如果不满足，返回该
                //项目之前的前缀子
                //列表
            }
            i++;
        }
        return list;
    }

    /**
     * 利用这个方法，你就可以优化isPrime方法，只用不大于被测数平方根的质数去测试了
     */
    public static boolean isPrime0(List<Integer> primes,int candidate){
        int candidateRoot=(int)Math.sqrt((double) candidate);
        return takeWhile(primes,i->i<=candidateRoot).stream()
                .noneMatch(p->candidate%p==0);
    }


    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return ()-> new HashMap<Boolean,List<Integer>>(){{
                put(true,new ArrayList<Integer>());
                put(false,new ArrayList<Integer>());
            }};
    }

    /*
    * 这里不但创建了用作累加器的Map，还为true和false两个键下面初始化了对应的空列表。
在收集过程中会把质数和非质数分别添加到这里。收集器中最重要的方法是accumulator，因
为它定义了如何收集流中元素的逻辑。这里它也是实现前面所讲的优化的关键。现在在任何一次
迭代中，都可以访问收集过程的部分结果，也就是包含迄今找到的质数的累加器：
    * */

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> map,Integer candidate)->{
            map.get(isPrime0(map.get(true),candidate))//与已经加入了质数列表进行比对
                    .add(candidate);
        };
    }

    /*
    * 下一个方法要在并行收集时把两个部分累加器合并起来，这里，它只需要合并两个Map，即
将第二个Map中质数和非质数列表中的所有数字合并到第一个Map的对应列表中就行了：
    *
    * */

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (map1,map2)->{
            map1.get(true).addAll(map2.get(true));
            map1.get(false).addAll(map2.get(false));
            return map1;
        };

        /*
        * 请注意，实际上这个收集器是不能并行使用的，因为该算法本身是顺序的。这意味着永远都
不会调用combiner方法，你可以把它的实现留空（更好的做法是抛出一个UnsupportedOperationException异常）。为了让这个例子完整，我们还是决定实现它。

        *
        * */
    }

    /*
    * 最后两个方法的实现都很简单。前面说过，accumulator正好就是收集器的结果，用不着
进一步转换，那么finisher方法就返回identity函数：
    * */

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(
                EnumSet.of(
                        Characteristics.IDENTITY_FINISH)
        );
    }


    //之前的例子，拿来用
    private static Map<Boolean,List<Integer>> prime(int n){
        return IntStream.rangeClosed(2,n).boxed().collect(
                new PrimePlus()
        );
    }

    /*
    * 当然，你可以选择直接使用collect定义，只不过可能性差一点
    * */
    private static Map<Boolean,List<Integer>> prime0(int n) {
        return IntStream.rangeClosed(2,n).boxed().collect(
                ()-> new HashMap<Boolean,List<Integer>>(){{
                    put(true,new ArrayList<Integer>());
                    put(false,new ArrayList<Integer>());
                }},
                (Map<Boolean, List<Integer>> map,Integer candidate)->{
                    map.get(isPrime0(map.get(true),candidate))//与已经加入了质数列表进行比对
                            .add(candidate);
                },
                (map1,map2)->{
                    map1.get(true).addAll(map2.get(true));
                    map1.get(false).addAll(map2.get(false));
                }
        );
    }

    public static void main(String[] args) {
        Map<Boolean,List<Integer>> t1 = prime(30);
        System.out.println(t1);


    }
}
