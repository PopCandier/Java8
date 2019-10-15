package com.pop.java8.chapter7;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author Pop
 * @date 2019/10/14 23:37
 */
public class WordCounterSpliterator implements Spliterator<Character> {

    private final String string;
    private int currentChar = 0;

    public WordCounterSpliterator(String string) {
        this.string = string;
    }

    /**
     * tryAdvance方法的行为类似于普通的
     * Iterator，因为它会按顺序一个一个使用Spliterator中的元素，并且如果还有其他元素要遍
     * 历就返回true。
     * @param action
     * @return
     */
    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        /**
         * tryAdvance方法把String中当前位置的Character传给了Consumer，并让位置加一。
         * 作为参数传递的Consumer是一个Java内部类，在遍历流时将要处理的Character传给了
         * 处理当前字符 如果还有字符要处理，则返回true返回null表示要解析的 String已经足够小，可
         * 以顺序处理将试探拆分位置设定为要解析的String的中间让拆分位置前进直到下一个空格
         * 创建一个新WordCounterSpliterator来解析String从开始到拆分位置的部分将这个WordCounterSpliterator
         * 的起始位置设为拆分位置一系列要对其执行的函数。这里只有一个归约函数，即WordCounter类的accumulate
         * 方法。如果新的指针位置小于String的总长，且还有要遍历的Character， 则
         * tryAdvance返回true。
         */
        action.accept(string.charAt(currentChar++));//处理当前字符
        return currentChar<string.length();//如果还有要处理的字符就返回true
    }

    /**
     * 但trySplit是专为Spliterator接口设计的，因为它可以把一些元素划出去分
     * 给第二个Spliterator（由该方法返回），让它们两个并行处理。
     * @return
     */
    @Override
    public Spliterator<Character> trySplit() {
        /**
         * trySplit方法是Spliterator中最重要的一个方法，因为它定义了拆分要遍历的数据
         * 结构的逻辑。就像在代码清单7-1中实现的RecursiveTask的compute方法一样（分支
         * /合并框架的使用方式），首先要设定不再进一步拆分的下限。这里用了一个非常低的下
         * 限——10个Character，仅仅是为了保证程序会对那个比较短的String做几次拆分。
         * 在实际应用中，就像分支/合并的例子那样，你肯定要用更高的下限来避免生成太多的
         * 任务。如果剩余的Character数量低于下限，你就返回null表示无需进一步拆分。相
         * 反，如果你需要执行拆分，就把试探的拆分位置设在要解析的String块的中间。但我
         * 们没有直接使用这个拆分位置，因为要避免把词在中间断开，于是就往前找，直到找到
         * 一个空格。一旦找到了适当的拆分位置，就可以创建一个新的Spliterator来遍历从
         * 当前位置到拆分位置的子串；把当前位置this设为拆分位置，因为之前的部分将由新
         * Spliterator来处理，最后返回。
         */
        int currentSize = string.length() - currentChar;
        if(currentSize<10){ return null;}
        //返回 null 表示解析的String，已经拆分的足够小了，可以顺序处理
        for(int splitPos = currentSize/2 + currentChar;//从中间开始试探
                splitPos<string.length();splitPos++){
            if(Character.isWhitespace(string.charAt(splitPos))){//拿到这个位置的char位置
                //如果是空格 拆分成新的Spliterator
                Spliterator<Character> spliterator
                         = new WordCounterSpliterator(string.substring(currentChar,
                        splitPos));
                currentChar = splitPos;//重新设置位置，因为 old currentChar - splitPos 已经被处理
                return  spliterator;
            }
        }
        return null;
    }

    /**
     * Spliterator还可通过
     * estimateSize方法估计还剩下多少元素要遍历，因为即使不那么确切，能快速算出来是一个值
     * 也有助于让拆分均匀一点。
     * @return
     */
    @Override
    public long estimateSize() {
        /**
         * 还需要遍历的元素的estimatedSize就是这个Spliterator解析的String的总长度和
         * 当前遍历的位置的差。
         */
        return string.length()-currentChar;
    }

    /**
     * Spliterator接口声明的最后一个抽象方法是characteristics，它将返回一个int，代
     * 表Spliterator本身特性集的编码。
     * @return
     */
    @Override
    public int characteristics() {
        /**
         * 最后，characteristic方法告诉框架这个Spliterator是ORDERED（顺序就是String
         * 中各个Character的次序）、SIZED（estimatedSize方法的返回值是精确的）、
         * SUBSIZED（trySplit方法创建的其他Spliterator也有确切大小）、NONNULL（String
         * 中不能有为 null 的 Character ） 和 IMMUTABLE （在解析 String 时不能再添加
         * Character，因为String本身是一个不可变类）的。
         */
        return ORDERED+SIZED+SUBSIZED+NONNULL+IMMUTABLE;
    }
}
