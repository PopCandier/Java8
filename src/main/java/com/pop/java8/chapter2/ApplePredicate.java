package com.pop.java8.chapter2;


import com.pop.java8.chapter1.Apple;

/**
 * @author Pop
 * @date 2019/10/2 16:00
 * 一种可能的解决方案，是你对选择标准建模，你考虑的是苹果，需要
 * 根据Apple的某些属性，比如他是绿色的，重量可能超过150克，来返回一个boolean值
 * 我们把它称为谓词（即一个返回boolean类型的函数）。让我们定义一个接口来对选择标准建模
 */
public interface ApplePredicate {
    boolean test(Apple apple);
}
