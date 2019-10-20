package com.pop.java8.chapter11;

import java.util.concurrent.*;

/**
 * @author Pop
 * @date 2019/10/20 23:16
 */
public class FutureDemo {

    public void demo1(){

        ExecutorService executor = Executors.newCachedThreadPool();

        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return 0.6;//假设这是一个非常久的任务
            }
        });

        //...****************
        //这期间，我们可以做一些其它的任务
        //...****************

        try {
            Double result = future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();//中断异常
        } catch (ExecutionException e) {
            e.printStackTrace();//计算异常
        } catch (TimeoutException e) {
            e.printStackTrace();//指定时间内没有返回，就抛出此异常
        }
    }



}
