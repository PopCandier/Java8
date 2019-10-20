package com.pop.java8.chapter11.app;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author Pop
 * @date 2019/10/20 23:28
 *
 *
 */
public class Shop {
    private String name;

    public Shop(String name) {
        this.name = name;
    }

    /**
     * 为了实现最佳价格查询器应用，让我们从每个商店都应该提供的API定义入手。首先，商店
     *  * 应该声明依据指定产品名称返回价格的方法：
     * @param product
     * @return
     */
    public double getPrice(String product){
        /**
         *  todo
         *
         *  该方法的内部实现会查询商店的数据库，但也有可能执行一些其他耗时的任务，比如联系其
         * 他外部服务（比如，商店的供应商，或者跟制造商相关的推广折扣）。
         */
        return calculatePrice(product);
    }

    /**
     * 我们在本章剩下的内容中，
     * 采用delay方法模拟这些长期运行的方法的执行，它会人为地引入1秒钟的延迟，方法声明如下。
     */

    public static void delay(){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为了介绍本章的内容，getPrice方法会调用delay方法，并返回一个随机计算的值，代码
     * 清单如下所示。返回随机计算的价格这段代码看起来有些取巧。它使用charAt，依据产品的名
     * 称，生成一个随机值作为价格。
     */

    Random random = new Random();

    private double calculatePrice(String product){
        delay();
        return random.nextDouble()
                *product.charAt(0)+product.charAt(1);
    }

    /**
     * 很明显，这个API的使用者（这个例子中为最佳价格查询器）调用该方法时，它依旧会被
     * 阻塞。为等待同步事件完成而等待1秒钟，这是无法接受的，尤其是考虑到最佳价格查询器对
     * 网络中的所有商店都要重复这种操作。本章接下来的小节中，你会了解如何以异步方式使用同
     * 步API解决这个问题。但是，出于学习如何设计异步API的考虑，我们会继续这一节的内容，假
     * 装我们还在深受这一困难的烦扰：你是一个睿智的商店店主，你已经意识到了这种同步API会
     * 为你的用户带来多么痛苦的体验，你希望以异步API的方式重写这段代码，让用户更流畅地访
     * 问你的网站。
     */

    //将同步方法转化为异步方法
    /**
     * 为了实现这个目标，你首先需要将getPrice转换为getPriceAsync方法，并修改它的返
     * 回值：
     *
     * 我们在本章开头已经提到，Java 5引入了java.util.concurrent.Future接口表示一个异
     * 步计算（即调用线程可以继续运行，不会因为调用方法而阻塞）的结果。这意味着Future是一
     * 个暂时还不可知值的处理器，这个值在计算完成后，可以通过调用它的get方法取得。因为这样
     * 的设计，getPriceAsync方法才能立刻返回，给调用线程一个机会，能在同一时间去执行其他
     * 有价值的计算任务。新的CompletableFuture类提供了大量的方法，让我们有机会以多种可能
     * 的方式轻松地实现这个方法，比如下面就是这样一段实现代码。
     */

    public Future<Double> getPriceAsync(String product){
        CompletableFuture<Double> future = new CompletableFuture<>();
        new Thread(()->{
            double price = calculatePrice(product);
            future.complete(price);//需要长时间计算的任务结果，在这里完成
        }).start();
        return  future;
    }

    public static void main(String[] args) {
        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime
                + " msecs");
// 执行更多任务，比如查询其他商店
       // doSomethingElse();
// 在计算商品价格的同时
        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " msecs");
        /**
         * 我们看到这段代码中，客户向商店查询了某种商品的价格。由于商店提供了异步API，该次
         * 调用立刻返回了一个Future对象，通过该对象客户可以在将来的某个时刻取得商品的价格。这
         * 种方式下，客户在进行商品价格查询的同时，还能执行一些其他的任务，比如查询其他家商店中
         * 商品的价格，不会呆呆地阻塞在那里等待第一家商店返回请求的结果。最后，如果所有有意义的
         * 工作都已经完成，客户所有要执行的工作都依赖于商品价格时，再调用Future的get方法。执行
         * 了这个操作后，客户要么获得Future中封装的值（如果异步任务已经完成），要么发生阻塞，直
         * 到该异步任务完成，期望的值能够访问。
         */
    }

    /**
     * 你一定已经发现getPriceAsync方法的调用返回远远早于最终价格计算完成的时间。在11.4
     * 节中，你还会知道我们有可能避免发生客户端被阻塞的风险。实际上这非常简单，Future执行
     * 完毕可以发送一个通知，仅在计算结果可用时执行一个由Lambda表达式或者方法引用定义的回
     * 调函数。不过，我们当下不会对此进行讨论，现在我们要解决的是另一个问题：如何正确地管理
     * 异步任务执行过程中可能出现的错误。
     *
     * 如果没有意外，我们目前开发的代码工作得很正常。但是，如果价格计算过程中产生了错误
     * 会怎样呢？非常不幸，这种情况下你会得到一个相当糟糕的结果：用于提示错误的异常会被限制
     * 在试图计算商品价格的当前线程的范围内，最终会杀死该线程，而这会导致等待get方法返回结
     * 果的客户端永久地被阻塞。
     * 客户端可以使用重载版本的get方法，它使用一个超时参数来避免发生这样的情况。这是一
     * 种值得推荐的做法，你应该尽量在你的代码中添加超时判断的逻辑，避免发生类似的问题。使用
     * 这种方法至少能防止程序永久地等待下去，超时发生时，程序会得到通知发生了TimeoutException。不过，也因为如此，你不会有机会发现计算商品价格的线程内到底发生了什么问题
     * 才引发了这样的失效。为了让客户端能了解商店无法提供请求商品价格的原因，你需要使用
     * CompletableFuture的completeExceptionally方法将导致CompletableFuture内发生问
     * 题的异常抛出。对代码清单11-4优化后的结果如下所示。
     */

    public Future<Double> getPriceAsyncException(String product){
        CompletableFuture<Double> future = new CompletableFuture<>();
        new Thread(()->{
            try {
                double price = calculatePrice(product);
                future.complete(price);//需要长时间计算的任务结果，在这里完成
            }catch (Exception e){
                future.completeExceptionally(e);//抛出这个异常，完成这次Future操作
            }

        }).start();
        return  future;
    }

    /**
     * 使用工厂方法supplyAsync创建CompletableFuture
     * 目前为止我们已经了解了如何通过编程创建CompletableFuture对象以及如何获取返回
     * 值，虽然看起来这些操作已经比较方便，但还有进一步提升的空间，CompletableFuture类自
     * 身提供了大量精巧的工厂方法，使用这些方法能更容易地完成整个流程，还不用担心实现的细节。
     * 比如，采用supplyAsync方法后，你可以用一行语句重写代码清单11-4中的getPriceAsync方
     * 法，如下所示。
     */

    public Future<Double> getPriceAsync1(String product){
        return CompletableFuture.supplyAsync(()->calculatePrice(product));
    }

    /**
     * supplyAsync方法接受一个生产者（Supplier）作为参数，返回一个CompletableFuture
     * 对象，该对象完成异步执行后会读取调用生产者方法的返回值。生产者方法会交由ForkJoinPool
     * 池中的某个执行线程（Executor）运行，但是你也可以使用supplyAsync方法的重载版本，传
     * 递第二个参数指定不同的执行线程执行生产者方法。一般而言，向CompletableFuture的工厂
     * 方法传递可选参数，指定生产者方法的执行线程是可行的，在11.3.4节中，你会使用这一能力，我
     * 们会在该小节介绍如何使用适合你应用特性的执行线程改善程序的性能。
     * 此外，代码清单11-7中getPriceAsync方法返回的CompletableFuture对象和代码清单
     * 11-6中你手工创建和完成的CompletableFuture对象是完全等价的，这意味着它提供了同样的
     * 错误管理机制，而前者你花费了大量的精力才得以构建。
     * 本章的剩余部分中，我们会假设你非常不幸，无法控制Shop类提供API的具体实现，最终提
     * 供给你的API都是同步阻塞式的方法。这也是当你试图使用服务提供的HTTP API时最常发生的情
     * 况。你会学到如何以异步的方式查询多个商店，避免被单一的请求所阻塞，并由此提升你的“最
     * 佳价格查询器”的性能和吞吐量。
     */

}
