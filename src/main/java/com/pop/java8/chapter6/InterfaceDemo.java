package com.pop.java8.chapter6;

public interface InterfaceDemo {

    default void test1(){

    };


    static void test(){
        System.out.println("test");
    }

    public static void test2(){
        System.out.println("test");
    }
}
