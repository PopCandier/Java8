package com.pop.java8.chapter8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author Pop
 * @date 2019/10/15 23:46
 */
public class PatternDemo {

    /**
     * 使用lambda 来改变设计模式
     * 使得代码不再臃肿
     *
     *  策略模式
     *  模板方法
     *  观察者模式
     *  责任链模式
     *  工厂模式
     */

    /**
     * 策略模式
     *
     *  一个代表某个算法的接口（它是策略模式的接口）。
     *  一个或多个该接口的具体实现，它们代表了算法的多种实现（比如，实体类ConcreteStrategyA或者ConcreteStrategyB）。
     *  一个或多个使用策略对象的客户。
     *
     * 这之前，我们使用了不同的条件，比如苹果的重量或者颜色
     * 来进行筛选，这也算是一种算法的策略
     */

    /**
     * 我们假设你希望验证输入的内容是否根据标准进行恰当的格式化
     * 比如只包含小写字母或数字，你可以从定义一个验证文本（String）形式开始入手
     */
    public interface ValidationStrategy{
        boolean execute(String s);
    }

    //并有多种实现
    public  class IsAllLowerCase implements ValidationStrategy{

        @Override
        public boolean execute(String s) {
            return s.matches("[a-z]+");//允许小写字母的匹配
        }
    }

    public  class IsNumeric implements ValidationStrategy{

        @Override
        public boolean execute(String s) {
            return s.matches("\\d+");//匹配数字
        }
    }

    //你定义的一种实现
    public class Validator{

        private final ValidationStrategy strategy;

        public Validator(ValidationStrategy strategy) {
            this.strategy = strategy;
        }

        public boolean validate(String s){
            return strategy.execute(s);
        }

    }
    //策略模式的样例展示
    private void demo1(){

        Validator numericValidator = new Validator(new IsNumeric());
        boolean b1 = numericValidator.validate("aaaaa");
        Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
        boolean b2 = lowerCaseValidator.validate("bbbb");

        //其实这样的 ValidationStrategy 已经是一个函数接口了，也就意味着我们可以这样做


        Validator v1 = new Validator((String s)->s.matches("[a-z]+"));
        boolean b3 = v1.validate("aaa");
        Validator v2 = new Validator((String s)->s.matches("\\d+"));
        boolean b4 = v2.validate("bbb");
    }

    /**
     * 模版方法
     *
     * 如果你需要采用某个算法的框架，同时又希望有一定的灵活度，能对它的某些部分进行改进，
     * 那么采用模板方法设计模式是比较通用的方案。好吧，这样讲听起来有些抽象。换句话说，模板
     * 方法模式在你“希望使用这个算法，但是需要对其中的某些行进行改进，才能达到希望的效果”
     * 时是非常有用的。
     * 让我们从一个例子着手，看看这个模式是如何工作的。假设你需要编写一个简单的在线银行
     * 应用。通常，用户需要输入一个用户账户，之后应用才能从银行的数据库中得到用户的详细信息，
     * 最终完成一些让用户满意的操作。不同分行的在线银行应用让客户满意的方式可能还略有不同，
     * 比如给客户的账户发放红利，或者仅仅是少发送一些推广文件。你可能通过下面的抽象类方式来
     * 实现在线银行应用：
     */
    class Customer{ private String name;

        public String getName() {
            return name;
        }
    }
    class Database{
        public  Customer getCustomerWithId(int id){return null;}
    }
    abstract  class OnlineBanking{
        public void processCustomer(int id){
            Customer c  = new Database().getCustomerWithId(id);//去数据库拿到用户信息
            makeCustomerHappy(c);//对用户进行处理
        }
        abstract  void makeCustomerHappy(Customer customer);


    }

    class OnlineBankingLambda{
        //如果你使用lambda表达式，那么你可以这样使用
        public void processCustomer(int id, Consumer<Customer> makeCustomerHappy){
            Customer c  = new Database().getCustomerWithId(id);//去数据库拿到用户信息
            makeCustomerHappy.accept(c);//这一步就可以由用户自定义了
        }
    }

    public  void demo2(){

        new OnlineBankingLambda().processCustomer(1337,(Customer c)->{
            System.out.println("Hello "+c.getName());
        });

    }

    /**
     * 观察者模式
     *
     * 观察者模式是一种比较常见的方案，某些事件发生时（比如状态转变），如果一个对象（通
     * 常我们称之为主题）需要自动地通知其他多个对象（称为观察者），就会采用该方案。创建图形
     * 用户界面（GUI）程序时，你经常会使用该设计模式。这种情况下，你会在图形用户界面组件（比
     * 如按钮）上注册一系列的观察者。如果点击按钮，观察者就会收到通知，并随即执行某个特定的
     * 行为。 但是观察者模式并不局限于图形用户界面。比如，观察者设计模式也适用于股票交易的
     * 情形，多个券商可能都希望对某一支股票价格（主题）的变动做出响应。图8-2通过UML图解释
     * 了观察者模式。
     *
     * 让我们写点儿代码来看看观察者模式在实际中多么有用。你需要为Twitter这样的应用设计并
     * 实现一个定制化的通知系统。想法很简单：好几家报纸机构，比如《纽约时报》《卫报》以及《世
     * 界报》都订阅了新闻，他们希望当接收的新闻中包含他们感兴趣的关键字时，能得到特别通知。
     * 首先，你需要一个观察者接口，它将不同的观察者聚合在一起。它仅有一个名为notify的
     * 方法，一旦接收到一条新的新闻，该方法就会被调用：
     */

    interface Observer{
        void notify(String tweet);
    }

    /**
     * 现在，你可以声明不同的观察者（比如，这里是三家不同的报纸机构），依据新闻中不同的
     * 关键字分别定义不同的行为：
     */

    class NYTimes implements Observer{

        @Override
        public void notify(String tweet) {
            if(tweet!=null && tweet.contains("money")){
                System.out.println("Breaking news in NY! "+tweet);
            }
        }
    }

    class Guardian implements Observer{

        @Override
        public void notify(String tweet) {
            if(tweet!=null && tweet.contains("queen")){
                System.out.println("Yet another news in London... " + tweet);
            }
        }
    }

    class LeMonde implements Observer{

        @Override
        public void notify(String tweet) {
            if(tweet!=null && tweet.contains("wine")){
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        }
    }

    //还一个 Subject， 订阅者
    interface Subject{
        void registerObserver(Observer o);
        void notifyObservers(String tweet);
    }

    /**
     * Subject使用registerObserver方法可以注册一个新的观察者，使用notifyObservers
     * 方法通知它的观察者一个新闻的到来。让我们更进一步，实现Feed类：
     */

    class Feed implements Subject{

        private final List<Observer> observers = new ArrayList<>();

        @Override
        public void registerObserver(Observer o) {
            observers.add(o);
        }

        @Override
        public void notifyObservers(String tweet) {
            observers.forEach(o->o.notify(tweet));
        }
        //这还是很直观的，Feed维护了一个list，里面用来处理维护的列表
    }

    public void demo3(){

        Feed feed = new Feed();
        feed.registerObserver(new NYTimes());
        feed.registerObserver(new Guardian());
        feed.registerObserver(new LeMonde());

        feed.notifyObservers(" one message");//其它三个观察者都注册了Feed
        //所以Feed的变化，他们都会被看到。

        //逻辑没有什么区别
        feed.registerObserver((String tweet)->{
            if(tweet!=null && tweet.contains("Pop")){
                System.out.println("Hello ,Pop");
            }
        });
        //当然其实也不是，总是使用lambda表达式，当你的逻辑比较简单的时候
        //这样简单方便，而且不用写一对无意义的代码
        //但是如果逻辑复杂，还是使用类会比较妥当
    }

    /**
     * 责任链模式
     *
     * 责任链模式是一种创建处理对象序列（比如操作序列）的通用方案。一个处理对象可能需要
     * 在完成一些工作之后，将结果传递给另一个对象，这个对象接着做一些工作，再转交给下一个处
     * 理对象，以此类推。
     * 通常，这种模式是通过定义一个代表处理对象的抽象类来实现的，在抽象类中会定义一个字
     * 段来记录后续对象。一旦对象完成它的工作，处理对象就会将它的工作转交给它的后继。代码中，
     * 这段逻辑看起来是下面这样：
     */

    public abstract class ProcessingObject<T>{

        protected ProcessingObject<T> successor; //会包含下一个对象引用，组成链表

        public void setSuccessor(ProcessingObject<T> successor){
            this.successor = successor;
        }

        public T handle(T input){
            T r = handleWord(input);//进行当前逻辑处理
            if(successor !=null){
                return successor.handle(r);//如果含有引用，那么接着传递
            }
            return r;
        }

        abstract  protected  T handleWord(T input);
    }

    //还是创建两个对象去处理他们
    public class HeaderTextProcessing extends ProcessingObject<String>{

        @Override
        protected String handleWord(String input) {
            return "From Raoul, Mario and Alan: "+input;
        }
    }

    public class SpellCheckProcessing extends ProcessingObject<String>{

        @Override
        protected String handleWord(String input) {
            return  input.replaceAll("labda", "lambda");
        }
    }

    public void demo4(){

        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckProcessing();

        p1.setSuccessor(p2);

        //已经经过处理
        String result = p1.handle("Aren't labdas really sexy?!!");
        System.out.println(result);

        //使用lambda处理
        //将操作定义好
        UnaryOperator<String> headerProcessing =
                (String txt)-> "From Raoul, Mario and Alan: "+txt;//处理第一个
        UnaryOperator<String> spellCheckerProcessing =
                (txt)->txt.replaceAll("labda", "lambda");

        //连接起来
        Function<String,String> pipline =
                headerProcessing.andThen(spellCheckerProcessing);

        System.out.println(pipline.apply("Aren't labdas really sexy?!!"));
    }

    /**
     * 工厂模式
     */
    interface Product{}
    class Loan implements Product{}
    class Stock implements Product{}
    class Bond implements Product{}
    public class ProductFactory {
        public  Product createProduct(String name){
            switch(name){
                case "loan": return new Loan();
                case "stock": return new Stock();
                case "bond": return new Bond();
                default: throw new RuntimeException("No such product " + name);
            }
        }
    }

    public void demo5(){

        //我们可以这样用
        Product p = new ProductFactory().createProduct("loan");

        //我们已经知道可以像引用方法一样引用构造函数。比如，下面就是一个引用贷款
        //（Loan）构造函数的示例：
        Supplier<Product> loanSupplier = Loan::new;
        Loan loan = (Loan) loanSupplier.get();



    }
    //所以还可以这么写
     Map<String, Supplier<Product>> map = new HashMap<String, Supplier<Product>>(){{
        put("loan", Loan::new);
        put("stock", Stock::new);
        put("bond", Bond::new);
    }};
    //所以方法，还可以写成这样
    public  Product createProduct(String name){
        Supplier<Product> p = map.get(name);
        if(p!=null) return p.get();
        throw new IllegalArgumentException(" 没有这样的产品 "+name);
    }

    //如果你的参数很多，你就可能需要创建多个自定义的函数接口
    public interface TriFunction<T,U,V,R>{
        R apply(T t,U u,V v);//使用还是一样
    }


}
