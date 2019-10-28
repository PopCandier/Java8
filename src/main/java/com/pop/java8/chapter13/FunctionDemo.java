package com.pop.java8.chapter13;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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

    /*
    * 递归和迭代
    *
    * 根据函数式变成的理论，我们希望我们的方法是
    * 无副作用的，即传入的东西，不该对该参数进行
    * 数据结构上的改变
    * */
    @Data
    class Stats{private String name;}
    public void searchForGold(List<String> l ,Stats stats){
        for (String s : l) {
            if("gold".equals(s)){
                stats.setName("gold");//改变了结构
            }
        }
    }
    /**
     * 实际上，对函数式而言，循环体带有一个无法避免的副作用：它会修改stats对象的状态，
     * 而这和程序的其他部分是共享的。
     * 由于这个原因，纯函数式编程语言，
     */

    /**
     * 比较理论的答案是每个程序都能使用无需修改的递归重写，通过这种方
     *      * 式避免使用迭代。使用递归，你可以消除每步都需更新的迭代变量。一个经典的教学问题是用迭
     *      * 代的方式或者递归的方式（假设输入值大于1）编写一个计算阶乘的函数（参数为正数），代码列
     *      * 表如下。
     */
    static int factorialIterative(int n){//迭代方法进行阶乘计算
        int r = 1;
        for (int i = 0; i <=n; i++) {
            r*=i;
        }
        return r;
    }

    static long factorialRecursive(long n){//递归的方法进行阶乘计算
        return n==1?1:n*factorialRecursive(n-1);
    }
    //当然，我们还可以使用java8的方式
    static long factorialStreams(long n){
        return LongStream.rangeClosed(1,n)
                .reduce(1,(a,b)->a*b);
    }
    /**
     * 但是，实际上递归方式的效率会差一点，首先是
     * 栈溢出的问题，每次运行是调用的方法都会在调用栈
     * 上创建一个新的栈帧，用于保存每个方法调用时候的状态
     * 这个操作会一直指导程序运行直到结束。这意味着你的递归迭代方法会依据它接收的
     * 输入成比例地消耗内存。这也是为什么如果你使用一个大型输入执行factorialRecursive方
     * 法，很容易遭遇StackOverflowError异常：
     */

    /**
     * 这是否意味着递归百无一用呢？当然不是！函数式语言提供了一种方法解决这一问题：尾
     * 调优化（tail-call optimization）。基本的思想是你可以编写阶乘的一个迭代定义，不过迭代调用发
     * 生在函数的最后（所以我们说调用发生在尾部）。这种新型的迭代调用经过优化后执行的速度快
     * 很多。作为示例，下面是一个阶乘的“尾-递”（tail-recursive）定义。
     */

    static long factorialTailRecursive(long n){
        return factorialHelper(1,n);
    }

    static long factorialHelper(long acc,long n){
        return n==1?acc:factorialHelper(acc*n,n-1);
    }

    /**
     * 使用Java 8进行编程时，我们有一个建议，你应该尽量使用Stream取代迭代操作，从而避免
     * 变化带来的影响。此外，如果递归能让你以更精炼，并且不带任何副作用的方式实现算法，你就
     * 应该用递归替换迭代。实际上，我们看到使用递归实现的例子更加易于阅读，同时又易于实现和
     * 理解（比如，我们在前文中展示的子集的例子），大多数时候编程的效率要比细微的执行时间差
     * 异重要得多。
     */

}
