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

