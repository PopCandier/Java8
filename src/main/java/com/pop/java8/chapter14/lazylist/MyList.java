package com.pop.java8.chapter14.lazylist;

import java.util.function.Predicate;

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

    default MyList<T> filter(Predicate<T> p){
        return isEmpty()?this:
                p.test(head())?//头部符合设置的条件，返回头和尾部符合的部分，否则直接剔除头部
                        new LazyList<T>(head(),()->tail().filter(p)):
                        tail().filter(p);
    }
}
