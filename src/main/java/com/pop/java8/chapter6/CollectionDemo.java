package com.pop.java8.chapter6;

import com.pop.java8.chapter4.StreamDemo;

import java.util.stream.Collectors;

/**
 * @program: java8
 * @description:
 * @author: Pop
 * @create: 2019-10-09 11:39
 **/
public class CollectionDemo {

    /**
     * 归约和汇总
     */

    public static void main(String[] args) {

        //collect 收集者，接受一个收集器 Collector 他的行为将会被收录
        long howManyDishe=
                StreamDemo.menu.stream().collect(Collectors.counting());

    }
}
