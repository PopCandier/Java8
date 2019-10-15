package com.pop.java8.chapter8;

import com.pop.java8.chapter3.BufferedReaderProcessor;
import com.pop.java8.chapter4.Dish;
import com.pop.java8.chapter4.StreamDemo;
import com.pop.java8.chapter6.CollectionDemo;
import org.junit.platform.commons.logging.LoggerFactory;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @program: java8
 * @description:
 * @author: Pop
 * @create: 2019-10-15 14:55
 **/
public class LambdaDemo {

    public void test(){}

    public static void main(String[] args) {

        /**
         * 如果你想要重构你的代码
         *
         * 匿名内部类写成lambda表达式
         */

        /**
         * 但是某些情况下，将匿名类转换为Lambda表达式可能是一个比较复杂的过程①。首先，匿名
         * 类和Lambda表达式中的this和super的含义是不同的。在匿名类中，this代表的是类自身，但
         * 是在Lambda中，它代表的是包含类。其次，匿名类可以屏蔽包含类的变量，而Lambda表达式不
         * 能（它们会导致编译错误），譬如下面这段代码：
         */

        int a = 10;

        Runnable r1 = ()->{
//            int a = 2;  也就意味着这个会出现在当前上下文中

//            this.test(); 也意味着，this的作用域还是再上下文中
            //不是那种内部

            System.out.println(a);
        };
        //拥有自己的作用域，和上面的lambda一样
        Runnable r2 = new Runnable() {
            @Override
            public void run() {

            }
        };

    }

    /**
     * 最后，在涉及重载的上下文里，将匿名类转换为Lambda表达式可能导致最终的代码更加晦
     * 涩。实际上，匿名类的类型是在初始化时确定的，而Lambda的类型取决于它的上下文。通过下
     * 面这个例子，我们可以了解问题是如何发生的。我们假设你用与Runnable同样的签名声明了一
     * 个函数接口，我们称之为Task（你希望采用与你的业务模型更贴切的接口名时，就可能做这样
     * 的变更）：
     */
    interface  Task{
        void execute();
    }

    public static void doSomething(Runnable r){r.run();}
    public static void doSomething(Task t){t.execute();}

    public static void some(){
        //如果你传入一个匿名内部类，当然没有什么问题
        doSomething(new Task() {
            @Override
            public void execute() {
                System.out.println(" 有匿名内部类");
            }
        });

        //但由于lambda的类型取决于他上下文，所以，你这样写，可能就不知道
        //是什么意思
        doSomething((Task) ()-> System.out.println(" 不明白具体匹配 runnable还是 task"));
        //通过转型，使用显示的转换，可以解决这种模棱两可的方法
        //虽然，现在你这样写会编译会无法通过
    }

    public static void lambda2MethodReference(){

        /**
         * 我们可以将lambda表达式转换成方法引用
         */

        Map<CollectionDemo.CaloricLevel, List<Dish>> t2= StreamDemo.menu.stream().collect(
                Collectors.groupingBy(dish -> {
                    if(dish.getCalories()<=400) return CollectionDemo.CaloricLevel.DIET;
                    else if (dish.getCalories()>=700) return CollectionDemo.CaloricLevel.FAT;
                    else return CollectionDemo.CaloricLevel.NORMAL;
                }));
        //也就是说，我们可以将方法变成这种引用 我们前往chapter4 包下面，修改Dish方法
        Map<CollectionDemo.CaloricLevel, List<Dish>> t3= StreamDemo.menu.stream().collect(
                Collectors.groupingBy(Dish::getCaloricLevel));
    }

    /**
     * 从命令式的数据处理切换到Stream
     *
     *我们建议你将所有使用迭代器这种数据处理模式处理集合的代码都转换成Stream API的方
     * 式。为什么呢？Stream API能更清晰地表达数据处理管道的意图。除此之外，通过短路和延迟载
     * 入以及利用第7章介绍的现代计算机的多核架构，我们可以对Stream进行优化。
     */

    private Logger logger = Logger.getLogger(LambdaDemo.class.getName());

    public void loggerDemo(){

        //有条件的延迟延迟
        if(logger.isLoggable(Level.FINER)){
            logger.finer(" 一段注解");
        }

        /**
         * 这段代码有什么问题吗？其实问题不少。
         *  日志器的状态（它支持哪些日志等级）通过isLoggable方法暴露给了客户端代码。
         *  为什么要在每次输出一条日志之前都去查询日志器对象的状态？这只能搞砸你的代码。
         * 更好的方案是使用log方法，该方法在输出日志消息之前，会在内部检查日志对象是否已经
         * 设置为恰当的日志等级：
         */

        logger.log(Level.FINER," 一段输出");

        /**
         * 这种方式更好的原因是你不再需要在代码中插入那些条件判断，与此同时日志器的状态也不
         * 再被暴露出去。不过，这段代码依旧存在一个问题。日志消息的输出与否每次都需要判断，即使
         * 你已经传递了参数，不开启日志。
         * 这就是Lambda表达式可以施展拳脚的地方。你需要做的仅仅是延迟消息构造，如此一来，
         * 日志就只会在某些特定的情况下才开启（以此为例，当日志器的级别设置为FINER时）。显然，
         * Java 8的API设计者们已经意识到这个问题，并由此引入了一个对log方法的重载版本，这个版本
         * 的log方法接受一个Supplier作为参数。这个替代版本的log方法的函数签名如下：
         */

        logger.log(Level.FINER,()->"Problem: 123");

        /**
         * public void log(Level level, Supplier<String> msgSupplier) {
         *         if (!isLoggable(level)) {
         *             return;
         *         }
         *         LogRecord lr = new LogRecord(level, msgSupplier.get());
         *         doLog(lr);
         *     }
         *
         *
         * 从这个故事里我们学到了什么呢？如果你发现你需要频繁地从客户端代码去查询一个对象
         * 的状态（比如前文例子中的日志器的状态），只是为了传递参数、调用该对象的一个方法（比如
         * 输出一条日志），那么可以考虑实现一个新的方法，以Lambda或者方法表达式作为参数，新方法
         * 在检查完该对象的状态之后才调用原来的方法。你的代码会因此而变得更易读（结构更清晰），
         * 封装性更好（对象的状态也不会暴露给客户端代码了）。
         */

    }

    /**
     * 环绕执行
     *
     * 我们介绍过另一种值得考虑的模式，那就是环绕执行。如果你发现虽然你的业务
     * 代码千差万别，但是它们拥有同样的准备和清理阶段，这时，你完全可以将这部分代码用Lambda
     * 实现。这种方式的好处是可以重用准备和清理阶段的逻辑，减少重复冗余的代码。下面这段代码
     * 你在第3章中已经看过，我们再回顾一次。它在打开和关闭文件时使用了同样的逻辑，但在处理
     * 文件时可以使用不同的Lambda进行参数化。
     *
     */

    public static String processFile(BufferedReaderProcessor processor) throws IOException{
        //拥有相同的开头，和结尾
       try (BufferedReader br = new BufferedReader(new FileReader("java8/data.txt"))){
           return processor.process(br);//行为被传递
       }
    }

    public static void processFile0() throws IOException {
        String oneLine = processFile(b -> b.readLine());
        String oneLine1 = processFile(b -> b.readLine()+b.readLine());//你可以任意定义多种行为
    }

    interface BufferedReaderProcessor{//使用lambda表达式的函数接口
        String process(BufferedReader b) throws IOException;
    }

}
