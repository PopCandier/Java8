package com.pop.java8.chapter7;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * @author Pop
 * @date 2019/10/13 23:45
 *
 * 分支合并应用框架
 *
 * 分支/合并框架的目的是以递归方式将可以并行的任务拆分成更小的任务，然后将每个子任
 * 务的结果合并起来生成整体结果。它是ExecutorService接口的一个实现，它把子任务分配给
 * 线程池（称为ForkJoinPool）中的工作线程。首先来看看如何定义任务和子任务。
 */
public class BrankMergeFramework {

    /**
     * 要把任务提交到这个池，必须创建RecursiveTask<R>的一个子类，其中R是并行化任务（以
     * 及所有子任务）产生的结果类型，或者如果任务不返回结果，则是RecursiveAction类型（当
     * 然它可能会更新其他非局部机构）。要定义RecursiveTask，只需实现它唯一的抽象方法
     * compute：
     * protected abstract R compute();
     * 这个方法同时定义了将任务拆分成子任务的逻辑，以及无法再拆分或不方便再拆分时，生成
     * 单个子任务结果的逻辑。正由于此，这个方法的实现类似于下面的伪代码：
         * if (任务足够小或不可分) {
         *  顺序计算该任务
         * } else {
         *  将任务分成两个子任务
         *  递归调用本方法，拆分每个子任务，等待所有子任务完成
         *  合并每个子任务的结果
     * }
     */

    static class ForkJoinSumCalculator extends RecursiveTask<Long>{//继承这个来创建用于分支合并框架的任务

        private final long[] numbers;//要求和的数组
        private final int start;//子任务处理的数组其实和终止位置
        private final int end;

        public static final long THREADHOLD = 10_000;//不再将任务分解为子任务的数组大小

        public ForkJoinSumCalculator(long[] numbers) {
            this(numbers,0,numbers.length);
        }

        public ForkJoinSumCalculator(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        /**
         * 该任务负责求和部分的逻辑
         * @return
         */
        @Override
        protected Long compute() {
            int length = end-start;//求和部分的大小
            if(length<=THREADHOLD){
                //如果小于阈值，那么就顺序计算结果
                return computeSequentially();
            }
            //拆分子任务
            ForkJoinSumCalculator leftTask
                    = new ForkJoinSumCalculator(numbers,start,start+length/2);
            leftTask.fork();//利用另一个ForkJoinPool线程异步执行新创建的子任务
            ForkJoinSumCalculator rightTask
                    = new ForkJoinSumCalculator(numbers,start+length/2,end);
            Long rightResult = rightTask.compute();//执行第二个子任务，也许还会接着划分
            Long leftResult = leftTask.join();//读取第一个子任务的结果，如果尚未完成就等待
            return leftResult+rightResult;
        }

        private long computeSequentially(){
            long sum = 0;
            for (int i=start;i>end;i++){
                sum+=numbers[i];
            }
            return sum;
        }
    }

    /**
     * 现在编写一个方法来并行对前n个自然数求和就很简单了。你只需把想要的数字数组传给
     * ForkJoinSumCalculator的构造函数：
     */
    public static long forkJoinSum(long n){
        long[] numbers = LongStream.rangeClosed(1,n).toArray();
        ForkJoinTask<Long> task = new BrankMergeFramework.ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);

        /**
         * 这里用了一个LongStream来生成包含前n个自然数的数组，然后创建一个ForkJoinTask
         * （RecursiveTask的父类），并把数组传递给代码清单7-2所示ForkJoinSumCalculator的公共
         * 构造函数。最后，你创建了一个新的ForkJoinPool，并把任务传给它的调用方法 。在
         * ForkJoinPool中执行时，最后一个方法返回的值就是ForkJoinSumCalculator类定义的任务
         * 结果。
         * 请注意在实际应用时，使用多个ForkJoinPool是没有什么意义的。正是出于这个原因，一
         * 般来说把它实例化一次，然后把实例保存在静态字段中，使之成为单例，这样就可以在软件中任
         * 何部分方便地重用了。这里创建时用了其默认的无参数构造函数，这意味着想让线程池使用JVM
         * 能够使用的所有处理器。更确切地说，该构造函数将使用Runtime.availableProcessors的
         * 返回值来决定线程池使用的线程数。请注意availableProcessors方法虽然看起来是处理器，
         * 但它实际上返回的是可用内核的数量，包括超线程生成的虚拟内核。
         */
    }
}
