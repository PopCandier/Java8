package com.pop.java8.chapter11.app;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @author Pop
 * @date 2019/10/21 23:02
 *
 * 你的“最佳价格查询器”应用基本已经完成，不过还缺失了一些元素。你会希望尽快将不同
 * 商店中的商品价格呈现给你的用户（这是车辆保险或者机票比价网站的典型需求），而不是像你之
 * 前那样，等所有的数据都完备之后再呈现。接下来的一节，
 * 你会了解如何通过响应CompletableFuture的completion事件实现这一功能
 * （与此相反，调用get或者join方法只会造成阻塞，直
 * 到CompletableFuture完成才能继续往下运行）。
 */
public class CompletableEvent {

    /**
     * 本章你看到的所有示例代码都是通过在响应之前添加1秒钟的等待延迟模拟方法的远程调
     * 用。毫无疑问，现实世界中，你的应用访问各个远程服务时很可能遭遇无法预知的延迟，触发的
     * 原因多种多样，从服务器的负荷到网络的延迟，有些甚至是源于远程服务如何评估你应用的商业
     * 价值，即可能相对于其他的应用，你的应用每次查询的消耗时间更长。
     * 由于这些原因，你希望购买的商品在某些商店的查询速度要比另一些商店更快。为了说明本
     * 章的内容，我们以下面的代码清单为例，使用randomDelay方法取代原来的固定延迟。
     */
    //一个模拟生成0.5s-2.5s随机延迟的方法
    private static final Random random = new Random();
    public static void randomDelay(){
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 目前为止，你实现的findPrices方法只有在取得所有商店的返回值时才显示商品的价格。
     * 而你希望的效果是，只要有商店返回商品价格就在第一时间显示返回值，不再等待那些还未返回
     * 的商店（有些甚至会发生超时）。你如何实现这种更进一步的改进要求呢？
     *
     * 你要避免的首要问题是，等待创建一个包含了所有价格的List创建完成。你应该做的是直
     * 接处理CompletableFuture流，这样每个CompletableFuture都在为某个商店执行必要的操
     * 作。为了实现这一目标，在下面的代码清单中，你会对代码清单11-12中代码实现的第一部分进
     * 行重构，实现findPricesStream方法来生成一个由CompletableFuture构成的流。
     */
    public static Stream<CompletableFuture<String>> findPricesStream(String product){
        return BootStrap.shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(()->shop.getPriceEnum(product),
                        BootStrap.executor))
                .map(future->future.thenApply(Quote::parse))
                .map(future->future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(
                                ()->Discount.applyDiscount(quote),BootStrap.executor)));
    }

    /**
     * 现在，你为findPricesStream方法返回的Stream添加了第四个map操作，在此之前，你
     * 已经在该方法内部调用了三次 map 。这个新添加的操作其实很简单，只是在每个
     * CompletableFuture上注册一个操作，该操作会在CompletableFuture完成执行后使用它的
     * 返回值。Java 8的CompletableFuture通 过thenAccept方法提供了这一功能，它接收
     * CompletableFuture执行完毕后的返回值做参数。在这里的例子中，该值是由Discount服务
     * 返回的字符串值，它包含了提供请求商品的商店名称及折扣价格，你想要做的操作也很简单，只
     * 是将结果打印输出：
     */
    private void demo1(){

        findPricesStream("myPhone")
                .map(f->f.thenAccept(System.out::println));//假设注册一个事件
        //一旦一个任务完成，就会返回

    }

    /**
     * 注意，和你之前看到的thenCompose和thenCombine方法一样，thenAccept方法也提供
     * 了一个异步版本，名为thenAcceptAsync。异步版本的方法会对处理结果的消费者进行调度，
     * 从线程池中选择一个新的线程继续执行，不再由同一个线程完成CompletableFuture的所有任
     * 务。因为你想要避免不必要的上下文切换，更重要的是你希望避免在等待线程上浪费时间，尽快
     * 响应CompletableFuture的completion事件，所以这里没有采用异步版本。
     * 由 于thenAccept方法已经定义了如何处理CompletableFuture返回的结果，一旦
     * CompletableFuture计算得到结果，它就返回一个CompletableFuture<Void>。所以，map
     * 操作返回的是一个Stream<CompletableFuture<Void>>。对这个<CompletableFuture-
     * <Void>>对象，你能做的事非常有限，只能等待其运行结束，不过这也是你所期望的。你还希望
     * 能给最慢的商店一些机会，让它有机会打印输出返回的价格。为了实现这一目的，你可以把构成
     * Stream的所有CompletableFuture<Void>对象放到一个数组中，等待所有的任务执行完成，
     * 代码如下所示。
     */

    public void demo2(){
        CompletableFuture[] futures = findPricesStream("myPhone")
                .map(f->f.thenAccept(System.out::println))
                .toArray(size->new CompletableFuture[size]);

        CompletableFuture.allOf(futures).join();//等待所有任务都打印完价格内容
        /**
         * allOf工厂方法接收一个由CompletableFuture构成的数组，数组中的所有CompletableFuture对象执行完成之后，它返回一个CompletableFuture<Void>对象。这意味着，如果你需
         * 要等待最初Stream中的所有 CompletableFuture对象执行完毕，对 allOf方法返回的
         * CompletableFuture执行join操作是个不错的主意。这个方法对“最佳价格查询器”应用也是
         * 有用的，因为你的用户可能会困惑是否后面还有一些价格没有返回，使用这个方法，你可以在执
         * 行完毕之后打印输出一条消息“All shops returned results or timed out”。
         * 然而在另一些场景中，你可能希望只要CompletableFuture对象数组中有任何一个执行完
         * 毕就不再等待，比如，你正在查询两个汇率服务器，任何一个返回了结果都能满足你的需求。在
         * 这种情况下，你可以使用一个类似的工厂方法anyOf。该方法接收一个CompletableFuture对象
         * 构成的数组，返回由第一个执行完毕的CompletableFuture对象的返回值构成的CompletableFuture<Object>。
         */
    }


    /**
     * 正如我们在本节开篇所讨论的，现在你可以通过代码清单11-19中的randomDelay方法模拟
     * 远程方法调用，产生一个介于0.5秒到2.5秒的随机延迟，不再使用恒定1秒的延迟值。代码清单
     * 11-21应用了这一改变，执行这段代码你会看到不同商店的价格不再像之前那样总是在一个时刻
     * 返回，而是随着商店折扣价格返回的顺序逐一地打印输出。为了让这一改变的效果更加明显，我
     * 们对代码进行了微调，在输出中打印每个价格计算所消耗的时间：
     * @param args
     */
    public static void main(String[] args) {
        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream("myPhone27S")
                .map(f -> f.thenAccept(
                        s -> System.out.println(s + " (done in " +
                                ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        System.out.println("All shops have now responded in "
                + ((System.nanoTime() - start) / 1_000_000) + " msecs");
        /**
         * LetsSaveBig price is 216.20100000000002 (done in 2168 msecs)
         * BuyItAll price is 186.18399999999997 (done in 2168 msecs)
         * MyFavoriteShop price is 109.26750000000001 (done in 2168 msecs)
         * BestPrice price is 209.68 (done in 2168 msecs)
         * All shops have now responded in 2169 msecs
         */
    }

}
