package com.pop.java8.chapter7;

import java.util.function.Function;
import java.util.stream.LongStream;
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

    /**
     * 测试性能
     *
     * 你的测试方法，应用10次，查看时间
     */
    public static long measureSumPerf(Function<Long,Long> adder, long n){
        long fastest = Long.MAX_VALUE;
        for(int i=0;i<10;i++){
            long start = System.nanoTime();
            long sum = adder.apply(n);
            long duration = (System.nanoTime()-start)/1_000_000;
            System.out.println("Result : "+sum);
            if (duration<fastest) fastest = duration;
        }
        return fastest;
    }

    public static void main(String[] args) {

        //原始stream方式
//        System.out.println("Sequential sum done in :"+
//                measureSumPerf(ConcurrentStream::sequentialSum,10_000_000)+" msecs");
//
//        //传统方式
//        System.out.println("Sequential sum done in :"+
//                measureSumPerf(ConcurrentStream::iterativeSum,10_000_000)+" msecs");
//
//        //stream的方式
//        System.out.println("Sequential sum done in :"+
//                measureSumPerf(ConcurrentStream::parallelSum,10_000_000)+" msecs");

//        System.out.println("Sequential sum done in :"+
//                measureSumPerf(ConcurrentStream::rangedSum,10_000_000)+" msecs");
        //6 msecs 性能得到了很大的提升
//        System.out.println("Sequential sum done in :"+
//                measureSumPerf(ConcurrentStream::parallelRangedSum,10_000_000)+" msecs");

        //Sequential sum done in :1 msecs


        System.out.println("Sequential sum done in :"+
                measureSumPerf(BrankMergeFramework::forkJoinSum,10_000_000)+" msecs");
    }

    /**
     * 这相当令人失望，求和方法的并行版本比顺序版本要慢很多。你如何解释这个意外的结果
     * 呢？这里实际上有两个问题：
     * 1 .iterate生成的是装箱的对象，必须拆箱成数字才能求和；
     * 2 .我们很难把iterate分成多个独立块来并行执行。
     *
     *
     * 那到底要怎么利用多核处理器，用流来高效地并行求和呢？我们在第5章中讨论了一个叫
     * LongStream.rangeClosed的方法。这个方法与iterate相比有两个优点。
     * 1. LongStream.rangeClosed直接产生原始类型的long数字，没有装箱拆箱的开销。
     * 2. LongStream.rangeClosed会生成数字范围，很容易拆分为独立的小块。例如，范围1~20
     * 可分为1~5、6~10、11~15和16~20。
     */
    public static long rangedSum(long n){
        return LongStream.rangeClosed(1,n).reduce(0L,Long::sum);
    }
    /**
     * 这个数值流比前面那个用iterate工厂方法生成数字的顺序执行版本要快得多，因为数值流
     * 避免了非针对性流那些没必要的自动装箱和拆箱操作。由此可见，选择适当的数据结构往往比并
     * 行化算法更重要。但要是对这个新版本应用并行流呢？
     */
    public static long parallelRangedSum(long n){
        return LongStream.rangeClosed(1,n)
                .parallel().reduce(0L,Long::sum);
    }

    /**
     * 尽管如此，请记住，并行化并不是没有代价的。并行化过程本身需要对流做递归划分，把每
     * 个子流的归纳操作分配到不同的线程，然后把这些操作的结果合并成一个值。但在多个内核之间
     * 移动数据的代价也可能比你想的要大，所以很重要的一点是要保证在内核中并行执行工作的时间
     * 比在内核之间传输数据的时间长。总而言之，很多情况下不可能或不方便并行化。然而，在使用
     * 并行Stream加速代码之前，你必须确保用得对；如果结果错了，算得快就毫无意义了。让我们
     * 来看一个常见的陷阱。
     */

    /**
     * 错用并行流而产生错误的首要原因，就是使用的算法改变了某些共享状态。下面是另一种实
     * 现对前n个自然数求和的方法，但这会改变一个共享累加器：
     *
     * 也就是并行改了一个变量，如果不是原子操作，导致结果不同，这样的效率已经没有意义了。因为计算都不对，算的
     * 快就没意义了
     */

}
