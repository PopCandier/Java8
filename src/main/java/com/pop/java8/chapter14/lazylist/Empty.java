package com.pop.java8.chapter14.lazylist;

/**
 * @author Pop
 * @date 2019/10/28 20:28
 */
public class Empty<T> implements MyList<T> {
    @Override
    public T head() {
        throw  new UnsupportedOperationException();
    }

    @Override
    public MyList<T> tail() {
        throw new UnsupportedOperationException();
    }
}
