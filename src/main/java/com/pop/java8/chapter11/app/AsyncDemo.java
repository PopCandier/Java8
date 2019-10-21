package com.pop.java8.chapter11.app;

import static com.pop.java8.chapter11.app.Shop.delay;

/**
 * @author Pop
 * @date 2019/10/21 21:44
 *
 * 现在你已经了解了如何利用CompletableFuture为你的用户提供异步API，以及如何将一
 * 个同步又缓慢的服务转换为异步的服务。不过到目前为止，我们每个Future中进行的都是单次
 * 的操作。下一节中，你会看到如何将多个异步操作结合在一起，以流水线的方式运行，从描述形
 * 式上，它与你在前面学习的Stream API有几分类似。
 */
public class AsyncDemo {
}

/**
 * 让我们假设所有的商店都同意使用一个集中式的折扣服务。该折扣服务提供了五个不同的折
 * 扣代码，每个折扣代码对应不同的折扣率。你使用一个枚举型变量Discount.Code来实现这一
 * 想法，具体代码如下所示。
 */

class Discount{
    public enum Code{
        NONE(0),SILVER(5),
        COLD(10),PLATINUM(15),
        DIAMOND(20);
        private final int percentage;
        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * Discount服务还提供了一个applyDiscount方法，它接收一个Quote对象，返回一个字符
     * 串，表示生成该Quote的shop中的折扣价格，代码如下所示。
     */
    public static String applyDiscount(Quote quote){
        return quote.getShopName()+" price is "+
                //将折扣代码应用于商品最初的原始价格
                Discount.apply(quote.getPrice(),quote.getDicountCode());
    }

    private static double apply(double price,Code code){
        delay();//模拟 Discount 服务响应的延迟
        return (price*(100-code.percentage)/100);
    }

}

/**
 * 你的“最佳价格查询器”应用现在能从不同的商店取得商品价格，解析结果字符串，针对每
 * 个字符串，查询折扣服务取的折扣代码①。这个流程决定了请求商品的最终折扣价格（每个折扣
 * 代码的实际折扣比率有可能发生变化，所以你每次都需要查询折扣服务）。我们已经将对商店返
 * 回字符串的解析操作封装到了下面的Quote类之中：
 */
class Quote{

    private final String shopName;
    private final double price;
    private final Discount.Code dicountCode;

    public Quote(String shopName, double price, Discount.Code dicountCode) {
        this.shopName = shopName;
        this.price = price;
        this.dicountCode = dicountCode;
    }

    public static Quote parse(String s){
        //根据新的查询方法，将会返回这样的结果 BestPrice:123.26:GOLD
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code discountCode = Discount.Code.valueOf(split[2]);
        return new Quote(shopName,price,discountCode);
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDicountCode() {
        return dicountCode;
    }
}
