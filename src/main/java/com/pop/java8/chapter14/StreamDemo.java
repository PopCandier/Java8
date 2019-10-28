package com.pop.java8.chapter14;

import java.util.stream.IntStream;

/**
 * @author Pop
 * @date 2019/10/28 19:59
 */
public class StreamDemo {

    /**
     * Stream 的延迟算法
     *
     * 通过前一章的介绍，你已经了解Stream是处理数据集合的利器。不过，由于各种各样的原因，
     * 包括实现时的效率考量，Java 8的设计者们在将Stream引入时采取了比较特殊的方式。其中一个
     * 比较显著的局限是，你无法声明一个递归的Stream，因为Stream仅能使用一次。在接下来的一节，
     * 我们会详细展开介绍这一局限会带来的问题。
     */

    /**
     * 不过这一方案看起来有些笨拙：你每次都需要遍历每个数字，查看它能否被候选数字整除（实
     * 际上，你只需要测试那些已经被判定为质数的数字）。
     * 理想情况下，Stream应该实时地筛选掉那些能被质数整除的数字。这听起来有些异想天开，
     * 不过我们一起看看怎样才能达到这样的效果。
     * (1) 你需要一个由数字构成的Stream，你会在其中选择质数。
     * (2) 你会从该Stream中取出第一个数字（即Stream的首元素），它是一个质数（初始时，这个
     * 值是2）。
     * (3) 紧接着你会从Stream的尾部开始，筛选掉所有能被该数字整除的元素。
     * (4) 最后剩下的结果就是新的Stream，你会继续用它进行质数的查找。本质上，你还会回到
     * 第一步，继续进行后续的操作，所以这个算法是递归的。
     * 注意，这个算法不是很好，原因是多方面的①。不过，就说明如何使用Stream展开工作这个
     * 目的而言，它还是非常合适的，因为算法简单，容易说明。让我们试着用Stream API对这个算法
     * 进行实现。
     */

    /**
     * 你可以使用方法IntStream.iterate构造由数字组成的Stream，它由2开始，可以上达无限，
     * 就像我们在第5章中介绍的那样，代码如下
     */
    static IntStream numbers(){
        return IntStream.iterate(2,n->n+1);
    }

    /**
     * 2. 第二步： 取得首元素
     * IntStream类提供了方法findFirst，可以返回Stream的第一个元素：
     */
    static int head(IntStream numbers){
        return numbers.findFirst().getAsInt();
    }

    /**
     * 3. 第三步： 对尾部元素进行筛选
     * 定义一个方法取得Stream的尾部元素：
     */
    static IntStream tail(IntStream numbers){
        return numbers.skip(1);
    }

    private void demo(){
        IntStream numbers = numbers();
        int head = head(numbers);
        IntStream filtered = tail(numbers).filter(n->n%head!=0);
        /**
         * 拿到Stream的头元素，你可以像下面这段代码那样对数字进行筛选：
         */
    }

    static IntStream primes(IntStream numbers) {
        int head = head(numbers);
        return IntStream.concat(
                IntStream.of(head),
                primes(tail(numbers).filter(n -> n % head != 0))
        );
        /**
         * 不幸的是，如果执行步骤四中的代码，你会遭遇如下这个错误：“java.lang.IllegalStateException:
         * stream has already been operated upon or closed.”实际上，你正试图使用两个终端操作：findFirst
         * 和skip将Stream切分成头尾两部分。还记得我们在第4章中介绍的内容吗？一旦你对Stream执行
         * 一次终端操作调用，它就永久地终止了！
         */
    }


}
