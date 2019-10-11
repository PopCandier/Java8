package com.pop.java8.chapter6;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

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
        return null;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return null;
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return null;
    }

    /**
     *
     * 这算是一个特殊的方法了，这会被存储一系列特征
     * 也就是一个提示列表，告诉collect方法在执行归约操作
     * 的时候可行
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
        return null;
    }
}
