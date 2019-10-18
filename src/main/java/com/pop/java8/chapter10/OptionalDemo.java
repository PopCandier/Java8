package com.pop.java8.chapter10;

import java.util.Optional;

/**
 * @program: java8
 * @description:
 * @author: Pop
 * @create: 2019-10-18 09:19
 **/
public class OptionalDemo {

    /**
     * java8 中引入了 Optional 是为了解决 null的问题
     *
     * 很多时间我们遇见null的问题常见的第一种方法是使用if
     * 判断这个值是否为空，如果你这样做了，若属性很多，代码会
     * 十分臃肿，同时判断一个值是否为null，只是暂时屏蔽了这个问题
     * 并没有解决这个问题，可能在业务上这个值传值过来是不可能为null
     * 的。Optional是对某个值的包装，当他包含一个值的时候，可以获得这个
     * 值，反之不会返回东西，虽然也是不会返回‘null’，会发返回一个静态
     * 工厂的单例Optional，Optional.empty,Optional本质上是一个对象，随意也不会存在空指针问题
     */
    static class Person{
        private Car car;

        public Car getCar() {
            return car;
        }

        public void setCar(Car car) {
            this.car = car;
        }
    }
    static class Car{
        private String name;
        private Insurance insurance;

        public Insurance getInsurance() {
            return insurance;
        }

        public void setInsurance(Insurance insurance) {
            this.insurance = insurance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    static class Insurance{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public void demo1(){

        //声明一个空的Optional
        Optional<Car> optCar = Optional.empty();

        //根据一个非空值创建一个Optional
        Car car = new Car();
        /**
         * 如果car是一个null，这段代码会立即抛出一个NullPointerException，而不是等到你
         * 试图访问car的属性值时才返回一个错误。
         */
        Optional<Car> optCar1 = Optional.of(car);

        /**
         *  可接受null的Optional
         *
         *  使用静态工厂方法Optional.ofNullable，你可以创建一个允许null值的Optional
         * 对象：
         */
        Optional<Car> optCar2 = Optional.ofNullable(car);

        /**
         * 当然，如果你optional是null的话，仍然会报出空指针异常
         */
    }

    /**
     * 使用 map 从 Optional 对象中提取和转换值
     */
    public void demo2(){
        /**
         * 如何从Optional获得值
         * 虽然Optional提供了get方法，但是如果你Optional是null，也同样会出现空指针异常
         */
        String name = null;
        Car insurance = new Car();
        if(insurance!=null){
            name = insurance.getName();
        }

        /**
         * 为了支持这种模式，Optional提供了一个map方法。它的工作方式如下
         */
        Optional<Car> optInsurance = Optional.ofNullable(insurance);
        /**
         * map操作会将提供的
         * 函数应用于流的每个元素。你可以把Optional对象看成一种特殊的集合数据，它至多包含一个
         * 元素。如果Optional包含一个值，那函数就将该值作为参数传递给map，对该值进行转换。如
         * 果Optional为空，就什么也不做。
         */
        Optional<String> name0 = optInsurance.map(Car::getName);
    }

    /**
     * 上面的方法看起来还挺有用的，取值依旧使用的是
     * Optional的方法，返回的也是Optional，避免了空值
     * 那么，我们可以用下面的例子进行改进
     */
    public  String getCarInsuranceName(Person person){
        return person.getCar().getInsurance().getName();
    }
    //为了达到这目的，我们需要求助Optional提供的另一个方法flatMap


    public void demo3(){
        Person person = new Person();
        Optional<Person> optPerson = Optional.of(person);
        //-----------
        Optional<String> name =
                optPerson.map(Person::getCar).
                        map(Car::getInsurance)
                        .map(Insurance::getName);
        /**
         * 上面这段代码看起来很没什么问题
         * 但是编译却无法通过
         * 之前说过Optional的处理可以看成是Stream
         * map的使用第一个Person::getCar的时候，返回的不是Car
         * 而是Optional<Car>，在你调用下一个Car::getInsurance
         * 的时候，这个值变为了Optional<Optional<Car>>，所以你的第二次
         * 调用是非法的，
         * 这种嵌套的optional该如何解决
         */

        /**
         * flatMap 其实就是将多个Optional合并成一个Optional
         */
    }

    public  static String getCarInsuranceName(Optional<Person> person){
        /**
         * 截至目前为止，返回的Optional可能是两种情况：如果调用链上的任何一个方法返回一个
         * 空的Optional，那么结果就为空，否则返回的值就是你期望的保险公司的名称。那么，你如何
         * 读出这个值呢？毕竟你最后得到的这个对象还是个Optional<String>，它可能包含保险公司的
         * 名称，也可能为空。我们使用了一个名为orElse的方法，当Optional的值为
         * 空时，它会为其设定一个默认值。除此之外，还有很多其他的方法可以为Optional设定默认值，
         * 或者解析出Optional代表的值。接下来我们会对此做进一步的探讨。
         */
        return person.flatMap(p->Optional.of(p.getCar()))
                .flatMap(car -> Optional.of(car.insurance))
                .map(Insurance::getName)
                .orElse("Unknown");
    }

    public static void main(String[] args) {

        Person person = new Person();
        Car car = new Car();
        person.setCar(car);
        Insurance insurance = new Insurance();
        car.setInsurance(insurance);
//        insurance.setName("123");

        String name=getCarInsuranceName(Optional.of(person));

        System.out.println(name);

    }

}
