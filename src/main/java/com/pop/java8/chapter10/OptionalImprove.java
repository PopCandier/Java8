package com.pop.java8.chapter10;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Pop
 * @date 2019/10/20 22:21
 *
 * 使用 Optional 改进我们的方法
 */
public class OptionalImprove {

    private void demo1(){

        /**
         * 使用Optional 封装可能为null的值
         *
         * 现存Java API几乎都是通过返回一个null的方式来表示需要值的缺失，或者由于某些原因计
         * 算无法得到该值。比如，如果Map中不含指定的键对应的值，它的get方法会返回一个null。但
         * 是，正如我们之前介绍的，大多数情况下，你可能希望这些方法能返回一个Optional对象。你
         * 无法修改这些方法的签名，但是你很容易用Optional对这些方法的返回值进行封装。我们接着
         * 用Map做例子，假设你有一个Map<String, Object>方法，访问由key索引的值时，如果map
         * 中没有与key关联的值，该次调用就会返回一个null。
         */

        Map<String,Object> map = Collections.EMPTY_MAP;

        Object value = map.get("key");//但是这个返回值可能为空

        //虽然你可以使用map的containkey去判断该key是否存在

        Optional<Object> v1 = Optional.ofNullable(map.get("key"));
        /**
         * 每次你希望安全地对潜在为null的对象进行转换，将其替换为Optional对象时，都可以考
         * 虑使用这种方法。
         */

    }

    /**
     * 异常与Optional的对比
     *
     * 由于某种原因，函数无法返回某个值，这时除了返回null，Java API比较常见的替代做法是
     * 抛出一个异常。这种情况比较典型的例子是使用静态方法Integer.parseInt(String)，将
     * String转换为int。在这个例子中，如果String无法解析到对应的整型，该方法就抛出一个
     * NumberFormatException。最后的效果是，发生String无法转换为int时，代码发出一个遭遇
     * 非法参数的信号，唯一的不同是，这次你需要使用try/catch 语句，而不是使用if条件判断来
     * 控制一个变量的值是否非空。
     * 你也可以用空的Optional对象，对遭遇无法转换的String时返回的非法值进行建模，这时
     * 你期望parseInt的返回值是一个optional。我们无法修改最初的Java方法，但是这无碍我们进
     * 行需要的改进，你可以实现一个工具方法，将这部分逻辑封装于其中，最终返回一个我们希望的
     * Optional对象，代码如下所示。
     */

    public static Optional<Integer> stringToInt(String s){
        try {
            return Optional.of(Integer.parseInt(s));//能返回就包装
        }catch (NumberFormatException e){
            return Optional.empty();//否则是一个空对象
        }

        /**
         * 我们的建议是，你可以将多个类似的方法封装到一个工具类中，让我们称之为OptionalUtility。通过这种方式，你以后就能直接调用OptionalUtility.stringToInt方法，将
         * String转换为一个Optional<Integer>对象，而不再需要记得你在其中封装了笨拙的
         * try/catch的逻辑了。
         */
    }

}
