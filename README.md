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