package com.pop.java8.chapter13;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: java8
 * @description: 函数式编程实战
 * @author: Pop
 * @create: 2019-10-24 14:14
 **/
public class FunctionDemo {

    /**
     * 让我们从解决一个示例函数式的编程练习题入手：给定一个列表List<value>，比如{1, 4,
     * 9}，构造一个List<List<Integer>>，它的成员都是类表{1, 4, 9}的子集——我们暂时不考虑
     * 元素的顺序。{1, 4, 9}的子集是{1, 4, 9}、{1, 4}、{1, 9}、{4, 9}、{1}、{4}、{9}以及{}。
     * 包括空子集在内，这样的子集总共有8个。每个子集都使用List<Integer>表示，这就是答
     * 案中期望的List<List<Integer>>类型。
     *
     *
     * 通常新手碰到这个问题都会觉得无从下手，对于“{1, 4, 9}的子集可以划分为包含1和不包含
     * 1的两部分”也需要特别解释①。不包含1的子集很简单就是{4, 9}，包含1的子集可以通过将1插入
     * 到{4, 9}的各子集得到。这样我们就能利用Java，以一种简单、自然、自顶向下的函数式编程方
     * 式实现该程序了（一个常见的编程错误是认为空的列表没有子集）。
     */

    static List<List<Integer>> subsets(List<Integer> list){

        //空集是任何集合的子集
        if(list.isEmpty()){
            List<List<Integer>> ans = new ArrayList<>();
            ans.add(Collections.emptyList());
            return ans;//所以如果传进来，是空集合的，也可以分解成一个空集合
        }

        Integer first = list.get(0);
        List<Integer> rest = list.subList(1,list.size());//包含开头，不包含结尾

        //{1,4,9}
        //将列表的第一个元素抽取出来，分割成前一个后一段
        List<List<Integer>> subans = subsets(rest);
        //第一个元素和后面的集合内容进行分配，组合成余下的可能性
        List<List<Integer>> subans2 = insertAll(first,subans);
        //将所有结果组装成结果
        return concat(subans,subans2);//将单个元素的集合分别组合
    }

    private static List<List<Integer>> concat(List<List<Integer>> subans, List<List<Integer>> subans2) {

        /**
         *  不建议这样写
         *  a.addAll(b);
         *  return a;
         *
         *  // 因为这修改了结构
         */

        List<List<Integer>> r  = new ArrayList<>(subans);//避免修改参数对象
        r.addAll(subans2);
        return r;

        /**
         * 为什么呢？第二个版本的concat是纯粹的函数式。虽然它在内部会对对象进行修改（向列
         * 表r添加元素），但是它返回的结果基于参数却没有修改任何一个传入的参数。与此相反，第一个
         * 版本基于这样的事实，执行完concat(subans, subans2)方法调用后，没人需要再次使用
         * subans的值。对于我们定义的subsets，这的确是事实，所以使用简化版本的concat是个不错
         * 的选择。不过，这也取决于你如何审视你的时间，你是愿意为定位诡异的缺陷费劲心机耗费时间
         * 呢？还是花费些许的代价创建一个对象的副本呢？
         */
    }

    /**
     * 将第一个元素和后面的每个子元素组合
     * @param first
     * @param lists
     * @return
     */
    private static List<List<Integer>> insertAll(Integer first, List<List<Integer>> lists) {

        List<List<Integer>> result = new ArrayList<>();
        for(List<Integer> list:lists){
            List<Integer>  copyList = new ArrayList<>();
            copyList.add(first);
            copyList.addAll(list);
            result.add(copyList);
        }

        return result;
    }

    public static void main(String[] args) {

        //[[], [9], [4], [4, 9], [1], [1, 9], [1, 4], [1, 4, 9]]
        System.out.println(subsets(Arrays.asList(1,4,9)));

    }


}
