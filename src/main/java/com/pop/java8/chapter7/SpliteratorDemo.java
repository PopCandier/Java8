package com.pop.java8.chapter7;

import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Pop
 * @date 2019/10/14 22:59
 */
public class SpliteratorDemo {

    /**
     * public interface Spliterator<T> {
         *  boolean tryAdvance(Consumer<? super T> action);
         *  Spliterator<T> trySplit();
         *  long estimateSize();
         *  int characteristics();
     * }
     *
     * 与往常一样，T是Spliterator遍历的元素的类型。tryAdvance方法的行为类似于普通的
     * Iterator，因为它会按顺序一个一个使用Spliterator中的元素，并且如果还有其他元素要遍
     * 历就返回true。但trySplit是专为Spliterator接口设计的，因为它可以把一些元素划出去分
     * 给第二个Spliterator（由该方法返回），让它们两个并行处理。Spliterator还可通过
     * estimateSize方法估计还剩下多少元素要遍历，因为即使不那么确切，能快速算出来是一个值
     * 也有助于让拆分均匀一点。
     *
     * 这个拆分过程也受Spliterator本身的特性影响，而特性是通过characteristics方法声
     * 明的。
     * Spliterator的特性
     * Spliterator接口声明的最后一个抽象方法是characteristics，它将返回一个int，代
     * 表Spliterator本身特性集的编码。使用Spliterator的客户可以用这些特性来更好地控制和
     * 优化它的使用。
     *
     *
     */

    /**
     * 让我们来看一个可能需要你自己实现Spliterator的实际例子。我们要开发一个简单的方
     * 法来数数一个String中的单词数。这个方法的一个迭代版本可以写成下面的样子。
     * @param s
     * @return
     */

    public static int countWordsInteratively(String s){
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()){
            if(Character.isWhitespace(c)){
                lastSpace = true;
            }else{
                //上一个遍历是空格，而当前遍历的字符不是空格
                //的时候，将单词计数器+1
                if(lastSpace){ counter++;}
                lastSpace = false;
            }
        }
        return counter;
    }

    /**
     * 以函数风格重写单词计数器
     *
     * 首先你需要把String转换成一个流。不幸的是，原始类型的流仅限于int、long和double，
     * 所以你只能用Stream<Character>：
     *
     * 首先你需要把String转换成一个流。不幸的是，原始类型的流仅限于int、long和double，
     * 所以你只能用Stream<Character>：
     * Stream<Character> stream = IntStream.range(0, SENTENCE.length())
     *  .mapToObj(SENTENCE::charAt);
     * 你可以对这个流做归约来计算字数。在归约流时，你得保留由两个变量组成的状态：一个int
     * 逐个遍历String中的
     * 所有字符
     * 上一个字符是空格，而当前
     * 遍历的字符不是空格时，将
     * 单词计数器加一
     * 用来计算到目前为止数过的字数，还有一个boolean用来记得上一个遇到的Character是不是空
     * 格。因为Java没有元组（tuple，用来表示由异类元素组成的有序列表的结构，不需要包装对象），
     * 所以你必须创建一个新类WordCounter来把这个状态封装起来，如下所示。
     */

    static class WordCounter{
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c){//不断组成新的对象，返回
            if(Character.isWhitespace(c)){
                return lastSpace?this:new WordCounter(counter,true);
            }else{//，当上一个字符是空格，新字符不是空格时，计数器就加一
                return lastSpace?new WordCounter(counter+1,false):
                        this;
            }
        }

        public WordCounter combine(WordCounter wordCounter){
            return new WordCounter(counter+wordCounter.counter,
                    wordCounter.lastSpace);
        }

        public int getCounter(){
            return  counter;
        }
    }
    /**
     * 现在你已经写好了在WordCounter中累计字符，以及在WordCounter中把它们结合起来的
     * 逻辑，那写一个方法来归约Character流就很简单了：
     */
    private static int countWords(Stream<Character> stream){
        WordCounter wordCounter= stream.reduce(new WordCounter(0,true),
                WordCounter::accumulate,
                WordCounter::combine);
        return wordCounter.getCounter();
    }


    /**
     * 让我们把这个方法用在但丁的《神曲》的《地狱篇》的第一句话上：①
     */
    static final String SENTENCE =
            " Nel mezzo del cammin di nostra vita " +
                    "mi ritrovai in una selva oscura" +
                    " ché la dritta via era smarrita ";

    /**
     * 请注意，我们在句子里添加了一些额外的随机空格，以演示这个迭代实现即使在两个词之间
     * 存在多个空格时也能正常工作。正如我们所料，这段代码将打印以下内容：
     *
     */

    public static void main(String[] args) {

        System.out.println("Found "+countWordsInteratively(SENTENCE)+" words");

        //数字流，然后生成和对应字符串长度，然后映射到改对象对应的char，Stream
        Stream<Character> stream = IntStream.range(0,SENTENCE.length())
                .mapToObj(SENTENCE::charAt);
        System.out.println("Found "+countWords(stream)+" words");

        // 上面顺行很简单，但是目前位置，我们以函数式实现WordCounter的主要原因之一
        //就是能够轻松的并行处理，如果让WordCounter并行，会如何
        Stream<Character> stream1 = IntStream.range(0,SENTENCE.length())
                .mapToObj(SENTENCE::charAt);
        System.out.println("Found "+countWords(stream1.parallel())+" words");
        //Found 26 words 这显然很有问题

        /**
         * 显然有什么不对，可到底是哪里不对呢？问题的根源并不难找。因为原始的String在任意
         * 位置拆分，所以有时一个词会被分为两个词，然后数了两次。这就说明，拆分流会影响结果，而
         * 把顺序流换成并行流就可能使结果出错。
         * 如何解决这个问题呢？解决方案就是要确保String不是在随机位置拆开的，而只能在词尾
         * 拆开。要做到这一点，你必须为Character实现一个Spliterator，它只能在两个词之间拆开
         * String（如下所示），然后由此创建并行流。
         */

        Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
        Stream<Character> stream2 = StreamSupport.stream(spliterator,true);
        System.out.println("Found " + countWords(stream2) + " words");
        /**
         * 你已经看到了Spliterator如何让你控制拆分数据结构的策略。Spliterator还有最后一
         * 个值得注意的功能，就是可以在第一次遍历、第一次拆分或第一次查询估计大小时绑定元素的数
         * 据源，而不是在创建时就绑定。这种情况下，它称为延迟绑定（late-binding）的Spliterator。
         * 我们专门用附录C来展示如何开发一个工具类来利用这个功能在同一个流上执行多个操作。
         */
    }

}
