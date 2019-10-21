package com.pop.java8.chapter11.app;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Pop
 * @date 2019/10/20 23:28
 *
 * /**
 *      * 使用 CompletableFuture 构建异步应用
 *      * 为了展示CompletableFuture的强大特性，我们会创建一个名为“最佳价格查询器”
 *      * （best-price-finder）的应用，它会查询多个在线商店，依据给定的产品或服务找出最低的价格。这
 *      个过程中，你会学到几个重要的技能。
 *       首先，你会学到如何为你的客户提供异步API（如果你拥有一间在线商店的话，这是非常
 *      有帮助的）。
 *       其次，你会掌握如何让你使用了同步API的代码变为非阻塞代码。你会了解如何使用流水
 *      线将两个接续的异步操作合并为一个异步计算操作。这种情况肯定会出现，比如，在线
 *      商店返回了你想要购买商品的原始价格，并附带着一个折扣代码——最终，要计算出该
 *      商品的实际价格，你不得不访问第二个远程折扣服务，查询该折扣代码对应的折扣比率。
 *       你还会学到如何以响应式的方式处理异步操作的完成事件，以及随着各个商店返回它的
 *      商品价格，最佳价格查询器如何持续地更新每种商品的最佳推荐，而不是等待所有的商
 *      店都返回他们各自的价格（这种方式存在着一定的风险，一旦某家商店的服务中断，用
 *      户可能遭遇白屏）。
 *      */
public class BootStrap {

    /**
     * 假设一个商品列表
     */

    public static List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
                                    new Shop("LetsSaveBig"),
                                    new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"));

    /**
     * 你需要使用下面这样的签名实现一个方法，它接受产品名作为参数，返回一个字符串列表，
     * 这个字符串列表中包括商店的名称、该商店中指定商品的价格：
     */

    public List<String> findPrices(String product){

        return shops.stream().map(shop -> String.format("%s price is %.2f",
                shop.getName(),shop.getPrice(product)))
                .collect(Collectors.toList());

    }

    /**
     * 使用并行流改进
     */

    public List<String> findPrices0(String product){
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f",
                        shop.getName(),shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    /**
     * 创建异步任务
     * @param product
     * @return
     */
    public List<CompletableFuture<String>> findPricesAsync(String product){
        return shops.
                stream().map(shop -> CompletableFuture.supplyAsync(()->String.format("%s price is %.2f",
                shop.getName(),shop.getPrice(product)))).collect(Collectors.toList());
    }

    /**
     * 使用这种方式，你会得到一个List<CompletableFuture<String>>，列表中的每个
     * CompletableFuture对象在计算完成后都包含商店的String类型的名称。但是，由于你用
     * CompletableFutures实现的findPrices方法要求返回一个List<String>，你需要等待所有
     * 的future执行完毕，将其包含的值抽取出来，填充到列表中才能返回。
     * 为了实现这个效果，你可以向最初的List<CompletableFuture<String>>施加第二个
     * map操作，对List中的所有future对象执行join操作，一个接一个地等待它们运行结束。注意
     * CompletableFuture类中的join方法和Future接口中的get有相同的含义，并且也声明在
     * Future接口中，它们唯一的不同是join不会抛出任何检测到的异常。使用它你不再需要使用
     * try/catch语句块让你传递给第二个map方法的Lambda表达式变得过于臃肿。所有这些整合在一
     * 起，你就可以重新实现findPrices了，具体代码如下。
     */

    public List<String> findPricesAsync0(String product){
        List<CompletableFuture<String>> priceFutures =
                shops.stream().map(shop ->
                        CompletableFuture.supplyAsync(()->String.format("%s price is %.2f",
                                shop.getName(),shop.getPrice(product)))).collect(Collectors.toList());
        //由于任务需要返回一个List列表，他们会等待futurn完成后才会把值取出来
        return priceFutures.stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<String> findPricesAsync1(String product){
        List<CompletableFuture<String>> priceFutures =
                shops.stream().map(shop -> //指定线程池
                        CompletableFuture.supplyAsync(()->String.format("%s price is %.2f",
                                shop.getName(),shop.getPrice(product),executor))).collect(Collectors.toList());
        //由于任务需要返回一个List列表，他们会等待futurn完成后才会把值取出来
        return priceFutures.stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private void demo1(){
        //记录运行时间 顺序执行
        long start = System.nanoTime();
        System.out.println(findPrices("myPhone27S"));
        long duration = (System.nanoTime()-start)/1_000_000;
        System.out.println(" Done is "+duration+" msecs");
    }

    private void demo2(){
        //并行拆分
        long start = System.nanoTime();
        System.out.println(findPrices0("myPhone27S"));
        long duration = (System.nanoTime()-start)/1_000_000;
        System.out.println(" Done is "+duration+" msecs");
    }

    public void demo3(){
        long start = System.nanoTime();
        System.out.println(findPricesAsync0("myPhone27S"));
        long duration = (System.nanoTime()-start)/1_000_000;
        System.out.println(" Done is "+duration+" msecs");
    }

    public static void main(String[] args) {

        BootStrap bootStrap = new BootStrap();
//        bootStrap.demo1();

        /**
         * [BestPrice price is 163.64, LetsSaveBig price is 173.72, MyFavoriteShop price is 121.55, BuyItAll price is 162.52]
         *  Done is 4109 msecs
         *
         *  很明显，这个是4秒以上，这不奇怪，我们总共加入了四个商店
         *  并且都是顺序的执行，所以会是4秒以上
         */
//        bootStrap.demo2();
        /**
         * [BestPrice price is 171.32, LetsSaveBig price is 226.83, MyFavoriteShop price is 202.53, BuyItAll price is 170.91]
         *  Done is 1130 msecs
         *
         *  相当不错啊！看起来这是个简单但有效的主意：现在对四个不同商店的查询实现了并行，所
         * 以完成所有操作的总耗时只有1秒多一点儿。你能做得更好吗？让我们尝试使用刚学过的
         * CompletableFuture，将findPrices方法中对不同商店的同步调用替换为异步调用。
         */
        bootStrap.demo3();
        /**
         * [BestPrice price is 134.48, LetsSaveBig price is 131.09, MyFavoriteShop price is 148.27, BuyItAll price is 138.12]
         *  Done is 2148 msecs
         *
         *  真很奇怪，不升反降
         */
    }

    public static final Executor executor =
            Executors.newFixedThreadPool(Math.min(shops.size(),100),//设置一个阈值 查询商店数量分配最多100个，防止崩溃
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setDaemon(true);//使用守护线程，不会阻止程序关停
                            return thread;
                        }
                    });
    /**
     * 注意，你现在正创建的是一个由守护线程构成的线程池。Java程序无法终止或者退出一个正
     * 在运行中的线程，所以最后剩下的那个线程会由于一直等待无法发生的事件而引发问题。与此相
     * 反，如果将线程标记为守护进程，意味着程序退出时它也会被回收。这二者之间没有性能上的差
     * 异。现在，你可以将执行器作为第二个参数传递给supplyAsync工厂方法了。比如，你现在可
     * 以按照下面的方式创建一个可查询指定商品价格的CompletableFuture对象：
     */


    /**
     * 11-15
     *
     * 由于Discount服务是一种远程服务，你还需要增加1秒钟的模拟延迟，代码如下所示。和在
     * 11.3节中一样，首先尝试以最直接的方式（坏消息是，这种方式是顺序而且同步执行的）重新实
     * 现findPrices，以满足这些新增的需求。
     */

    public List<String> findPrices11(String product){
        return shops.stream()
                .map(shop -> shop.getPriceEnum(product))//请求远程商店获取指定商品和折扣码
                .map(Quote::parse)//转化成对应格式的
                .map(Discount::applyDiscount)//请求远程折扣服务，计算出最终折扣价格，并返回价格和该商品的shoi
                .collect(Collectors.toList());
        /**
         * 但是很明显，这是一个方案，但是肯定不是最优解
         * 查询五个商店就需要话费5秒的时间（每个都delay）
         * 查询远程商店折扣每个也是5秒（每个都delay）
         * 所以最后测量的结果是10秒以上
         */
    }

    /**
     * 让我们用异步的方式来构造这个方法
     * 构造同步和异步操作
     */

    public List<String> findPricesAys(String product){

        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                .map(shop -> CompletableFuture.//以异步的方式取得shop中指定的产品原始价格
                        supplyAsync(()->shop.getPriceEnum(product),executor))
                .map(future->future.thenApply(Quote::parse))//转化为对应的返回值
                .map(future->future.thenCompose(quote ->//使用另一个异步任务构造期望的Future申请折扣
                        CompletableFuture.supplyAsync(//请求折扣代码
                                ()->Discount.applyDiscount(quote),executor
                        )))
                .collect(Collectors.toList());
        return priceFutures.stream()
                .map(CompletableFuture::join)//将所有的Future结合起来，等待流中的所有Future执行完毕
                //并提取各自的返回值
                .collect(Collectors.toList());
        /**
         * 1. 获取价格
         *
         * 这三个操作中的第一个你已经在本章的各个例子中见过很多次，只需要将Lambda表达式作
         * 为参数传递给supplyAsync工厂方法就可以以异步方式对shop进行查询。第一个转换的结果是
         * 一个Stream<CompletableFuture<String>>，一旦运行结束，每个CompletableFuture对
         * 象中都会包含对应shop返回的字符串。注意，你对CompletableFuture进行了设置，用代码清
         * 单11-12中的方法向其传递了一个订制的执行器Executor。
         *
         * 2. 解析报价
         *
         * 现在你需要进行第二次转换将字符串转变为订单。由于一般情况下解析操作不涉及任何远程
         * 服务，也不会进行任何I/O操作，它几乎可以在第一时间进行，所以能够采用同步操作，不会带
         * 来太多的延迟。由于这个原因，你可以对第一步中生成的CompletableFuture对象调用它的
         * thenApply，将一个由字符串转换Quote的方法作为参数传递给它。
         * 注意到了吗？直到你调用的CompletableFuture执行结束，使用的thenApply方法都不会
         * 阻塞你代码的执行。这意味着CompletableFuture最终结束运行时，你希望传递Lambda表达式
         * 给thenApply方法，将Stream中的每个CompletableFuture<String>对象转换为对应的
         * CompletableFuture<Quote>对象。你可以把这看成是为处理CompletableFuture的结果建
         * 立了一个菜单，就像你曾经为Stream的流水线所做的事儿一样
         *
         * 3. 为计算折扣价格构造Future
         *
         * 第三个map操作涉及联系远程的Discount服务，为从商店中得到的原始价格申请折扣率。
         * 这一转换与前一个转换又不大一样，因为这一转换需要远程执行（或者，就这个例子而言，它需
         * 要模拟远程调用带来的延迟），出于这一原因，你也希望它能够异步执行。
         * 为了实现这一目标，你像第一个调用传递getPrice给supplyAsync那样，将这一操作以
         * Lambda表达式的方式传递给了supplyAsync工厂方法，该方法最终会返回另一个CompletableFuture对象。到目前为止，你已经进行了两次异步操作，用了两个不同的CompletableFutures
         * 对象进行建模，你希望能把它们以级联的方式串接起来进行工作。
         *  从shop对象中获取价格，接着把价格转换为Quote。
         *  拿到返回的Quote对象，将其作为参数传递给Discount服务，取得最终的折扣价格。
         * Java 8的 CompletableFuture API提供了名为thenCompose的方法，它就是专门为这一目
         * 的而设计的，thenCompose方法允许你对两个异步操作进行流水线，第一个操作完成时，将其
         * 结果作为参数传递给第二个操作。换句话说，你可以创建两个CompletableFutures对象，对
         * 第一个CompletableFuture对象调用 thenCompose，并向其传递一个函数。当第一个
         * CompletableFuture执行完毕后，它的结果将作为该函数的参数，这个函数的返回值是以第一
         * 个CompletableFuture的返回做输入计算出的第二个CompletableFuture对象。使用这种方
         * 式，即使Future在向不同的商店收集报价，主线程还是能继续执行其他重要的操作，比如响应
         * UI事件。
         * 将这三次map操作的返回的Stream元素收集到一个列表，你就得到了一个List<CompletableFuture<String>>，
         * 等这些CompletableFuture对象最终执行完毕，
         */
    }

    /**
     * 将两个 CompletableFuture 对象整合起来，无论它们是否存在依赖
     *
     * 上面的例子呢 ，我对一个CompletableFuture对象调用了thenCompose方法，并向其
     * 传递了第二个 CompletableFuture，而第二个CompletableFuture又需要使用第一个
     * CompletableFuture的执行结果作为输入。但是，另一种比较常见的情况是，你需要将两个完
     * 全不相干的CompletableFuture对象的结果整合起来，而且你也不希望等到第一个任务完全结
     * 束才开始第二项任务。
     * 这种情况，你应该使用thenCombine方法，它接收名为BiFunction的第二参数，这个参数
     * 定义了当两个CompletableFuture对象完成计算后，结果如何合并。同thenCompose方法一样，
     * thenCombine方法也提供有一个Async的版本。这里，如果使用thenCombineAsync会导致
     * BiFunction中定义的合并操作被提交到线程池中，由另一个任务以异步的方式执行。
     * 回到我们正在运行的这个例子，你知道，有一家商店提供的价格是以欧元（EUR）计价的，
     * 但是你希望以美元的方式提供给你的客户。你可以用异步的方式向商店查询指定商品的价格，同
     * 时从远程的汇率服务那里查到欧元和美元之间的汇率。当二者都结束时，再将这两个结果结合起
     * 来，用返回的商品价格乘以当时的汇率，得到以美元计价的商品价格。用这种方式，你需要使用
     * 第三个 CompletableFuture 对象，当前两个 CompletableFuture 计算出结果，并由
     * BiFunction方法完成合并后，由它来最终结束这一任务，代码清单如下所示。
     */
    public void findPriceCombine(String product){

        Shop shop=null;//假设对其中一家店进行设计

        Future<Double> future =
                CompletableFuture.
                        supplyAsync(
                                ()-> shop.getPrice(product))//获得原始价格和折扣码
                        .thenCombine(CompletableFuture.supplyAsync(()->ExchangeService.getRate()),
                                (price,rate)->price * rate);
                        //这里，开启了第二个异步任务，去查询价格之间的利率，然后，会将第一个future
        //和第二个合并起来，这里就是简单的乘法
        /**
         * 这里整合的操作只是简单的乘法操作，用另一个单独的任务对其进行操作有些浪费资源，所
         * 以你只要使用thenCombine方法，无需特别求助于异步版本的thenCombineAsync方法。
         */


    }


}

//模仿折扣服务
class ExchangeService{
    public static double getRate(){
        return 0;//假设已经返回了利率
    }
}
