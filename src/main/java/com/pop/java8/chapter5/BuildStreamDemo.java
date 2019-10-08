package com.pop.java8.chapter5;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Pop
 * @date 2019/10/8 21:39
 */
public class BuildStreamDemo {

    public static void main(String[] args) {
//        streamOfValue();
//        streamOfUnlimited();
        streamOfGenerate();
    }


    /**
     * 本案例将会介绍，如何自己构建流
     */

    private static void streamOfValue(){

        /**
         * 由值创造流
         *
         * 你可以使用静态方法Stream.of，通过显示值创建一个流。
         * 他/它可以接受任意数量的参数。例如，以下代码直接使用
         * Stream.of创建了一个字符串流。然后你可以将字符串转换成大写
         * 再一个个打印出来。
         */
        Stream<String> stream = Stream.of("Java 8","Lambdas ","In "," Action");
        //转化成大写，然后打印出来
        stream.map(String::toUpperCase).forEach(System.out::print);

        //也可以得到一个空的流
        Stream<String> emptyStream = Stream.empty();

    }

    private static void streamOfArray(){
        //由数组产生流
        int[] numbers = {2,3,4,5,6};
        int sum = Arrays.stream(numbers).sum();
    }

    private static void streamOfFile(){
        /**
         * Java 中 用于处理文件等IO操作的NIO API已经更新，以便利用Stream API。
         * java.nio.file.Files 中的很多静态方法都会返回一个流。
         * 例如，一个很有用的发那个发是Files.lines，它会返回一个由指定文件中各行构成的字符串流
         *
         */

        long uniqueWords = 0;
        try(Stream<String> lines=
                    Files.lines(Paths.get("data.txt"), Charset.defaultCharset())){//之后流会自动关闭
            uniqueWords = lines.flatMap(line->Arrays.stream(line.split(" ")))//生成单词流，因为一个句子中，空开的为一个单词
                    .distinct().count();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void streamOfUnlimited(){
        /**
         * 创造无限流
         *
         * Stream.iterate Stream.generate
         * 这两个操作可以创造所谓的无限流：不像是固定的集合创建的流那样有固定大小的流。
         * 由iterate和generate产生的流会给定函数按需创建值，因此可以无穷无尽的计算下去，
         * 一般来说，会使用limit(n)来对这种流加以限制，避免打印无穷个值
         */

        Stream.iterate(0,n->n+2)
                .limit(10).forEach(System.out::println);

        /**
         * 斐波那契数列，斐波那契数列的计算方法为，后一个数字为前两个数字的和
         * 0，1，1，2，3，5，8，13，21，24，55
         * (0,1) (1,1) (1,2)
         */
        Stream.iterate(new int[]{0,1},t->new int[]{t[1],t[0]+t[1]})
                .limit(20).forEach(t-> System.out.print("("+t[0]+","+t[1]+")"));

        //只想取第一个
        Stream.iterate(new int[]{0,1},t->new int[]{t[1],t[0]+t[1]})
                .map(t->t[0]).limit(20).forEach(System.out::print);
    }

    private static void streamOfGenerate(){

        /**
         * 与iterate方法类似，generate方法也可让你按需生辰一个无限流。
         * 但generate不是依次对每个新生成的值应用函数的，他会接受一个Supplier<T>
         * 类型的Lambda提供新的值。
         *
         * Supplier 有一个 get，用于得到一个值
         */
        Stream.generate(Math::random)
                .limit(5).forEach(System.out::println);

    }

}
