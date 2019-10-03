package com.pop.java8.chapter1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author Pop
 * @date 2019/10/2 15:03
 */
public class StreamDemo {

    public static void main(String[] args) {
        oldMap();
        streamMap();
        List<Apple> inventory = new ArrayList<>();
        /**
         * 顺序执行，先筛选，150以上的苹果，最后收集起来
         */
        List<Apple> heavyApples =
                inventory.stream().filter((Apple a)->a.getWeight()>150)
                .collect(Collectors.toList());

        /**
         * 利用多核处理集合
         * 例如前三个由cpu1处理，后两个由cpu2处理
         * 并行处理
         */

        List<Apple> heavyApples1 = inventory.parallelStream()
                .filter((Apple a)->a.getWeight()>150).collect(Collectors.toList());

    }

    private static void streamMap() {
        /**
         * 首先呢，看起来很复杂，如果用stream api的话，这些问题就很简单的就饿了
         */
        List<Transaction> transactions = new ArrayList<>();//需要处理的数据
        Map<Integer,List<Transaction>> ts =
                transactions.stream().filter((Transaction t)->t.getPrice()>1000)//筛选金额较高的交易
                .collect(groupingBy(Transaction::getCurrency));//按货币分组
    }

    private static void oldMap() {
        /**
         * 几乎每个java应用都会制造和处理集合，但是集合用起来总是不那么理想
         * 比方说，你需要从一个列表中筛选出金额较高的交易，然后按
         * 货币分组，你需要写很多逻辑去处理这个
         */

        List<Transaction> transactions = new ArrayList<>();
        Map<Integer, List<Transaction>> tranByCurrencies =
                new HashMap<>();

        for(Transaction transaction:transactions){
            if(transaction.getPrice()>1000){
                Integer integer = transaction.getCurrency();
                List<Transaction> list = tranByCurrencies.get(integer);
                if(list == null){
                    list = new ArrayList<>();
                    tranByCurrencies.put(integer,transactions);
                }
                list.add(transaction);
            }
        }
    }

}
