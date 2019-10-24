# Java8

`java.util.function`中所设计的函数式接口

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}

@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

 public static void main(String[] args) throws IOException {
        String oneLine = processFile((BufferedReader br)->br.readLine()+br.readLine());


        List<Integer> l = map(Arrays.asList("lambdas","in","action"),
                (String s)->s.length());

    }


    public static <T,R> List<R> map(List<T> list, Function<T,R> f){

        List<R> result = new ArrayList<>();
        for (T t : list) {
            //这里是某个行为，可以将每个key的字符长度统计出来
            result.add(f.apply(t));
        }
        return result;
    }
```

![1570076560930](./img/1570076560930.png)

![1570076572643](./img/1570076572643.png)

![1570076738274](./img/1570076738274.png)

![1570077577216](./img/1570077577216.png)

![1570082590073](./img/1570082590073.png)

![1570085901993](./img/1570085901993.png)

![1570086090289](./img/1570086090289.png)

![1570086104721](./img/1570086104721.png)

#### 流简介

![1570439904061](./img/1570439904061.png)

需要说明的是，流只能被消费一次。如果你想要再次消费，只能重新获取流

```java
ist<String> title = Arrays.asList("java8","in","action");
        Stream<String> s = title.stream();
        s.forEach(System.out::println);
        s.forEach(System.out::println);//会打印错误
```

![1570440948268](./img/1570440948268.png)

终端操作是一个不会返回stream类型的操作，返回的可能是Integer，也可能是void

![1570451273944](./img/1570451273944.png)

#### 使用流

#### Optional

![1570455343846](./img/1570455343846.png)

![1570458261366](./img/1570458261366.png)

#### 收集器例子的整体回顾

![1570778658626](./img/1570778658626.png)

![1570778695608](./img/1570778695608.png)

![1570778736022](./img/1570778736022.png)

![1570779551065](./img/1570779551065.png)



### 自定义流的使用流程

![1570807296569](./img/1570807296569.png)

Collector接口中combiner方法的存在意义

原文的意思是指，因为我们使用流的一个比较大的优势是在可以默认帮我们优化收集的性能，提高性能比较简单的方法就是并行，stream会将一个完整的流拆分成若干的子流进行处理，最后通过combiner进行合并，也就是这里的含义。

当然拆分成子流并不是无限制拆分，直到定义流是否要进一步拆分的一个条件为非，就不会再次拆分了。

![1570808117740](./img/1570808117740.png)

![1570807939483](./img/1570807939483.png)

#### 并行流

请注意，在现实中，对顺序流调用parallel方法并不意味着流本身有任何实际的变化。它 

在内部实际上就是设了一个boolean标志，表示你想让调用parallel之后进行的所有操作都并 

行执行。类似地，你只需要对并行流调用sequential方法就可以把它变成顺序流。请注意，你 

可能以为把这两个方法结合起来，就可以更细化地控制在遍历流时哪些操作要并行执行，哪些要 

顺序执行。例如，你可以这样做： 

```java
stream.parallel().filter(...).sequential().map(...).parallel() .reduce();  
```

但最后一次parallel或sequential调用会影响整个流水线。在本例中，流水线会并行执 

行，因为最后调用的是它。

**配置并行流使用的线程池** 

看看流的parallel方法，你可能会想，并行流用的线程是从哪儿来的？有多少个？怎么 

自定义这个过程呢？

并行流内部使用了默认的ForkJoinPool（7.2节会进一步讲到分支/合并框架），它默认的 

线程数量就是你的处理器数量，这个值是由 Runtime.getRuntime().available

Processors()得到的。 

但是你可以通过系统属性 java.util.concurrent.ForkJoinPool.common.  

parallelism来改变线程池大小，如下所示： 

System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","12");  

这是一个全局设置，因此它将影响代码中所有的并行流。反过来说，目前还无法专为某个 

并行流指定这个值。一般而言，让ForkJoinPool的大小等于处理器数量是个不错的默认值， 

除非你有很好的理由，否则我们强烈建议你不要修改它。

#### 使用分支/合并框架的最佳做法

![1570983292064](./img/1570983292064.png)

![1570983792977](./img/1570983792977.png)

![1570983803223](./img/1570983803223.png)


#### 修改代码可阅读性

![1570983803223](./img/1571121735154.png)



#### 位于第九章的默认方法

![1571241530562](./img/1571241530562.png)

默认方法的引入主要是为了兼容

![1571242294414](./img/1571242294414.png)

### 当拥有相同方法前面的default方法的借口被实现的时候，子类调用会使用哪一个方法

![1571242457839](./img/1571242457839.png)

![1571242777278](./img/1571242777278.png)

![1571242937256](./img/1571242937256.png)

#### Optional 的意义

![1571392573010](./img/1571392573010.png)

Optional 的 简单 api

![1571392757968](./img/1571392757968.png)

分类概括

![1571580752014](./img/1571580752014.png)

![1571580760460](./img/1571580760460.png)

#### 基础类型的Optional对象，以及为什么应该避免使用它们

不知道你注意到了没有，与 Stream对象一样，Optional也提供了类似的基础类
型——OptionalInt、OptionalLong以及OptionalDouble——所以代码清单10-6中的方法可
以不返回Optional，而是直接返回一个OptionalInt类型的对象。第5章中，我们
讨论过使用基础类型Stream的场景，尤其是如果Stream对象包含了大量元素，出于性能的考量，
使用基础类型是不错的选择，但对Optional对象而言，这个理由就不成立了，因为Optional
对象最多只包含一个值。
我们不推荐大家使用基础类型的Optional，因为基础类型的Optional不支持map、

flatMap以及filter方法，而这些却是Optional类最有用的方法（正如我们在10.2节所看到的
那样）。此外，与Stream一样，Optional对象无法由基础类型的Optional组合构成，所以，举
例而言，如果代码清单10-6中返回的是OptionalInt类型的对象，你就不能将其作为方法引用传
递给另一个Optional对象的flatMap方法。



### Future

![1571584360024](./img/1571584360024.png)

![1571584523901](./img/1571584523901.png)

![1571585243303](./img/1571585243303.png)

#### 快速查询的流程简介

![1571650689089](./img/1571650689089.png)

#### 寻找更好的方案

![1571650973562](./img/1571650973562.png)

![1571651036834](./img/1571651036834.png)

![1571663936140](./img/1571663936140.png)

![1571665199338](./img/1571665199338.png)

#### 将两个不相干的CompletableFuture合并

![1571669670594](./img/1571669670594.png)

### 更加丰富的时间工具

![1571824788678](./img/1571824788678.png)

![1571824806808](./img/1571824806808.png)

这一部分，请查看`chapter12_bak`包下的内容



#### 函数式编程的说明

在java8以前，我们使用面向对象的思想去编写程序，所以大部分情况下，我们考虑的最多的是**如何做**，而对于java8中的函数编程来说，更多的操作被封装，所以如何去实现也就变成了**要做什么**，例如Stream中的max与filter只需要选择对应的方法，底层将由stream帮我们完成，这样的代码看起简洁明了。

之前的面向对象的思想去编写逻辑时，往往按照计算机的逻辑，去编码，这是我们所谓的**命令式编程**。

很显然，函数式编程的有特点

* 无副作用

  * 不会改变外面或者内部的数据结构
  * 不会抛出异常
  * 无论你这个方法调用多少次，结果都不会改变

* 引用透明性

  * 没有感知的副作用（不改变对调用者可见的变量、不进行I/O、不抛出异常）

    