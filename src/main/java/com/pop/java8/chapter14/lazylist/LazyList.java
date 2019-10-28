package com.pop.java8.chapter14.lazylist;

import org.junit.jupiter.api.Test;
import org.omg.CORBA.INTERNAL;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Pop
 * @date 2019/10/28 20:52
 *
 * 2. 一个基础的延迟列表
 * 对这个类进行改造，使其符合延迟列表的思想，最简单的方法是避免让tail立刻出现在内
 * 存中，而是像第3章那样，提供一个Supplier<T>方法（你也可以将其看成一个使用函数描述符
 * void -> T的工厂方法），它会产生列表的下一个节点。使用这种方式的代码如下：
 */
public class LazyList<T> implements MyList<T> {

    final T head;
    final Supplier<MyList<T>> tail;

    public LazyList(T head, Supplier<MyList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public MyList<T> tail() {
        return tail.get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * 调用Supplier的get方法会触发延迟列表（LazyList）的节点创建，就像工厂会创建新的
     * 对象一样。
     * 现在，你可以像下面那样传递一个Supplier作为LazyList的构造器的tail参数，创建由
     * 数字构成的无限延迟列表了，该方法会创建一系列数字中的下一个元素：
     * @param n
     * @return
     */
    public static LazyList<Integer> from(int n){
        return new LazyList<Integer>(n,()->from(n+1));//只有调用getTail的时候才会出现这个
    }

    public static void main(String[] args) {
        //我们可以实际操做一下，看是不是可以生成数值
        LazyList<Integer> numbers = from(2);
        //这一步是生成了一个lazylist，之后如果我们不调用getTail这个方法是不会执行的
        int two = numbers.head();
        int three = numbers.tail().head();
        int four = numbers.tail().tail().head();

        System.out.println(two+" "+three+" "+four+" ");//2 3 4
    }

    /**
     * 3. 回到生成质数
     * 看看你能否利用我们目前已经做的去生成一个自定义的质数延迟列表（有些时候，你会遭遇
     * 无法使用Stream API的情况）。如果你将之前使用Stream API的代码转换成使用我们新版的
     * LazyList，它看起来会像下面这段代码：
     */

    public static MyList<Integer> primes(MyList<Integer> numbers){
        return new LazyList<>(
                numbers.head(),
                ()->primes(numbers.tail().filter(n->n%numbers.head()!=0)));
    }

    /**
     * 4. 实现一个延迟筛选器
     * 不过，这个LazyList（更确切地说是List接口）并未定义filter方法，所以前面的这段
     * 代码是无法编译通过的。让我们添加该方法的一个定义，修复这个问题：
        在MyList中
     */


}
