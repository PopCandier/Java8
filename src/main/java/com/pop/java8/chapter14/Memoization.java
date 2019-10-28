package com.pop.java8.chapter14;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pop
 * @date 2019/10/28 21:32
 *
 * 缓存或记忆表
 */
public class Memoization {

    /**
     * 假设你有一个无副作用的方法omputeNumberOfNodes(Range)，它会计算一个树形网络中
     * 给定区间内的节点数目。让我们假设，该网络不会发生变化，即该结构是不可变的，然而调用
     * computeNumberOfNodes方法的代价是非常昂贵的，因为该结构需要执行递归遍历。不过，你可
     * 能需要多次地计算该结果。如果你能保证引用透明性，那么有一种聪明的方法可以避免这种冗余
     * 的开销。解决这一问题的一种比较标准的解决方案是使用记忆表（memoization）——为方法添加
     * 一个封装器，在其中加入一块缓存（比如，利用一个HashMap）——封装器被调用时，首先查看
     * 缓存，看请求的“（参数，结果）对”是否已经存在于缓存，如果已经存在，那么方法直接返回缓
     * 存的结果；否则，你会执行computeNumberOfNodes调用，不过从封装器返回之前，你会将新计
     * 算出的“（参数，结果）对”保存到缓存中。严格地说，这种方式并非纯粹的函数式解决方案，因
     * 为它会修改由多个调用者共享的数据结构，不过这段代码的封装版本的确是引用透明的。
     * 实际操作上，这段代码的工作如下
     */
    class Range{}
    //准备的缓存，使用 范围-结果 进行缓存
    final Map<Range,Integer> numberOfNodes = new HashMap<>();
    Integer computeNumberOfNodesUsingCache(Range range){
        Integer result = numberOfNodes.get(range);
        if(result != null){
            return result;
        }
        result = comeputNumberOfNodes(range);
        numberOfNodes.put(range,result);//缓存这次结果
        return result;
    }

    Integer computeNumberOfNodesUsingCache0(Range range){
        /**
         * 如果指定的键尚未与值关联(或映射为null)，则尝试使用给定的映射函数计算其值并将其输入到此映射中，除非为null。
         * 如果函数返回null，则不记录映射。如果函数本身抛出一个(未检查的)异常，则重新抛出异常，并且不记录映射。最常见的用法是构造一个新对象作为初始映射值或默记结果
            大概的意思就是，如果指定的key不存在，就使用第二个函数，给他初始化

         */
        return numberOfNodes.computeIfAbsent(range,this::comeputNumberOfNodes);
    }

    private Integer comeputNumberOfNodes(Range range) {
        return 0;//一个无副作用，但是开销高昂的发那个发
    }

}
