package com.pop.java8.chapter6;

import com.pop.java8.chapter4.Dish;
import com.pop.java8.chapter4.StreamDemo;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @program: java8
 * @description: 自定义的结果收集器
 * @author: Pop
 * @create: 2019-10-11 18:48
 **/
public class ToListCollector<T> implements Collector<T, List<T>,List<T>> {

    /**
     * public interface Collector<T, A, R> {
     *  Supplier<A> supplier();
     *  BiConsumer<A, T> accumulator();
     *  Function<A, R> finisher();
     *  BinaryOperator<A> combiner();
     *  Set<Characteristics> characteristics();
     * }
     *
     * T 是流中要收集的项目的泛型
     * A 是累加器的类型，累加器是在收集过程中用于累积部分结果的对象
     * R 是收集操作得到的对象，（通常但不一定集合）的类型
     *
     */

    /**
     *
     * 创建一个空的累加器实例，
     * 供数据收集过程使用
     */
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    /**
     *
     * accumulator 方法会返回执行归约操作的函数
     * 当遍历到流中第n个元素时，这个函数执行时会有两个
     * 参数：
     *
     * 保存归约结果的累加器（已经收集了流中的前n-1个项目），
     * 还有第n个元素本身。
     *
     * 该函数将返回void，因为累加器是原位更新，即函数的执行改变了
     * 它的内部状态以体现遍历的元素效果。
     *
     * 对于ToListCollector，这个函数仅仅会把当前项目添加至
     * 已经遍历过的项目列表
     */
    @Override
      public BiConsumer<List<T>, T> accumulator() {
//        return (list,item)->list.add(item);//只是做一个添加,当然，可以做方法引用
        return List::add;
    }

    /**
     * 合并两个结果容器，combiner方法
     *四个方法中的最后一个 combiner方法会返回一个供
     * 归约操作使用的函数，它定义了对流的各个子部分进行
     * 并行处理时，各个子部分归约所得的累加器要如何合并。
     *
     * 那么这里的累加器可能就指的是我们的List<T>，若存在多个子部分的
     * List<T> 会如何合并操作
     *
     * 对于toList而言，这个方法的实现非常简单
     * 只要把从流的第二部分收集到的项目列表加到
     * 遍历第一部分得到的列表后面
     * 就行了。
     *
     */
    @Override
    public BinaryOperator<List<T>> combiner() {

        return (list1,list2)->{
            list1.addAll(list2);
                    return list1;
        };
    }

    /**
     *
     * 在遍历完流后，finisher方法必须返回在累积过程中的最后要调用
     * 的一个函数，以便将累加器对象转化为整个集合操作的最终结果
     * 通常，就像是ToListCollector的情况一样，累加器对象
     * 恰好符合预期的最终结果，因此无需进行转换
     * 所以finisher方法只需返回identity函数
     */
    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();//原样返回，不做处理
    }

    /**
     *
     * 这算是一个特殊的方法了，这会被存储一系列特征
     * 也就是一个提示列表，告诉collect方法在执行归约操作
     * 的时候可行
     *
     *  characteristics 会返回一个不可变的characteristics集合，
     *  它定义了收集器的行为，尤其是关于流是否可以并行归约，
     *  以及可以使用那些优化的提示。
     *  Characteristics 是一个包含三个项目的枚举。
     *
     *  UNORDERED -> 归约结果不受流中项目的遍历和累计顺序影响
     *
     *  CONCURRENT -> accumulator 函数可以从多个线程同时调用，
     *  且该收集器可以并行归约流。如果收集器没有标记为UNORDERED，那它
     *  仅在用于无序数据源时才可以并行归约
     *
     *  IDENTITY_FINISH -> 这表明完成器方法返回的函数是一个恒等函数，可以跳过。
     *  这种情况下，累加器对象将会直接用作归约过程的最终结果。这意味着，将累加器A
     *  不加检查地转化为结果R是安全的
     *
     *  在这个例子中，我们的已经是方法，已经算是IDENTITY_FINISH的了，因为累加器
     *  的结果，就是我们的最终结果 R
     *
     *  我们迄今开发的ToListCollector是IDENTITY_FINISH的，因为用来累积流中元素的
     *
     * List已经是我们要的最终结果，用不着进一步转换了，但它并不是UNORDERED，因为用在有序
     * 流上的时候，我们还是希望顺序能够保留在得到的List中。最后，它是CONCURRENT的，但我们
     * 刚才说过了，仅仅在背后的数据源无序时才会并行处理。
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(
                Characteristics.IDENTITY_FINISH,Characteristics.CONCURRENT));
    }

    public static void main(String[] args) {

        List<Dish> dishes = StreamDemo.menu.stream().collect(Collectors.toList());

        List<Dish> dishes1  =StreamDemo.menu.stream().collect(new ToListCollector<Dish>());

        /**
         * 进行自定义手机而不实现Collector
         *
         * 对于 IDENTITY_FINISH 的收集操作，还有一一种方法可以得到同样的结果
         * 而无需从头实现新的Collectors接口。
         * Stream有一个重载的collect方法，可以接受另外的三个函数
         * supplier accumulate combiner，其语义和Collector接口的相应方法返回的函数完全相同
         * 所以我们可以这样写
         *
         *
         */

        StreamDemo.menu.stream().collect(
                ArrayList::new,
                List::add,
                (list1,list2)->
                {list1.addAll(list2);
                }
        );


        List<Dish> t2= StreamDemo.menu.stream().collect(
                ArrayList::new,
                List::add,
                List::addAll
        );

        /**
         * 我们认为，这第二种形式虽然比前一个写法更为紧凑和简洁，却不那么易读。此外，以恰当
         * 的类来实现自己的自定义收集器有助于重用并可避免代码重复。另外值得注意的是，这第二个
         * collect方法不能传递任何Characteristics，所以它永远都是一个IDENTITY_FINISH和
         * CONCURRENT但并非UNORDERED的收集器。
         *
         *
         */

    }
}
