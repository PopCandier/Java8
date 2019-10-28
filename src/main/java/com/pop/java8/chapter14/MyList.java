package com.pop.java8.chapter14;

/**
 * @author Pop
 * @date 2019/10/28 20:24
 *
 * 我们谈论得已经很多，现在让我们一起看看它是如何工作的。你想要利用我们前面介绍的算
 * 法，生成一个由质数构成的无限列表。
 */
public interface MyList<T> {

    T head();

    MyList<T> tail();

    default boolean isEmpty(){
        return true;
    }
}
