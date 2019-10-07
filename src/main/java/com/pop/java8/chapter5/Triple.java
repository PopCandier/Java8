package com.pop.java8.chapter5;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Pop
 * @date 2019/10/7 23:37
 */
public class Triple {

    /**
     * 生成1-100以内的勾股数
     * 勾股数满足 a*a+b*b=c*c
     */
    private static void triple(){

        Stream<int[]> ints=IntStream.rangeClosed(1,100)
                .boxed()//将IntStream转化为Stream<Integer>
                .flatMap(a->
                        IntStream.rangeClosed(a,100)//这里改成了a，避免重复
                        .filter(b-> Math.sqrt(a*a+b*b)%1==0)//判断是否是整数
                        .mapToObj(b->new int[]{a,b,(int)Math.sqrt(a*a+b*b)})
                );
        //这里使用flatMap是因为，如果直接使用map将a映射成三元
        /**
         * 流的话，就会得到一个由流构成的流 Stream<Stream<int[]>>
         *     所以，使用扁平化将所有的流转化为一个steam<int[]>
         */
        ints.limit(5).forEach(t-> System.out.println(t[0]+" "+t[1]+" "+t[2]));
    }


    private static void triple2(){
        //上面的方法并不是最优解，因为你要求两次平凡根，你完全可以将结果直接比对就好

        Stream<double[]> ints=IntStream.rangeClosed(1,100)
                .boxed()//将IntStream转化为Stream<Integer>
                .flatMap(a->
                        IntStream.rangeClosed(a,100)//这里改成了a，避免重复
                                .mapToObj(b->new double[]{a,b,Math.sqrt(a*a+b*b)})
                        .filter(t->t[2]%1==0));
        //这里使用flatMap是因为，如果直接使用map将a映射成三元
        /**
         * 流的话，就会得到一个由流构成的流 Stream<Stream<int[]>>
         *     所以，使用扁平化将所有的流转化为一个steam<int[]>
         */
        ints.limit(5).forEach(t-> System.out.println(t[0]+" "+t[1]+" "+t[2]));
    }

    public static void main(String[] args) {
        triple2();
    }
}
