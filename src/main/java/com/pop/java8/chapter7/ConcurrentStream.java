package com.pop.java8.chapter7;

import java.util.stream.Stream;

/**
 * @program: java8
 * @description: 并行流
 * @author: Pop
 * @create: 2019-10-12 17:25
 **/
public class ConcurrentStream {

    /**
     * 并行的流可以让处理变得更加快速
     * 这会让其它并行忙碌起来
      */

    /**
     * 假设你需要写一个方法，接受数字n作为参数
     * 并返回从1到给定参数的所有数字和。
     * 一个直接的方法就是生成一个无穷大的数字流
     * 把他们限定到给定的数目，然后用对两个数学求和
     * 的BinaryOperator来归约这个流
     */

    public static long sequentialSum(long n){
        return Stream.iterate(1L,i->i+1)
                .limit(n).reduce(0L,Long::sum);
    }

    /**
     * 用更为传统的Java术语来说，这段代码和下面迭代等价
     */
    public static long iterativeSum(long n){
        long result = 0;
        for(long i=1L;i<=n;i++){
            result +=i;
        }
        return result;
    }

    /**
     * 将顺序流转化为并行流
     *
     * 你可以把流转换为并行流，从而让前面的函数归约过程
     * （也就是求和）并行运行 --- 对顺序流调用parallel方法
     */
    public static long parallelSum(long n){
        return Stream.iterate(1L,i->i+1)
                .limit(n)
                .parallel()//转化为并行流
                .reduce(0L,Long::sum);
    }


}
