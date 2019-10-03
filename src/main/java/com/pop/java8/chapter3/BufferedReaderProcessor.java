package com.pop.java8.chapter3;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Pop
 * @date 2019/10/3 11:51
 *
 * 但是由于要和方法匹配，所以我们需要定义一个
 * 接受BufferedReader->String 还可以抛出IOException异常的接口
 */
@FunctionalInterface
public interface BufferedReaderProcessor {

    String process(BufferedReader b) throws IOException;

}
