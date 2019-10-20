package com.pop.java8.chapter10;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Pop
 * @date 2019/10/20 22:33
 *
 * Optional的整合练习
 */
public class OptionalTest {

    Properties props = new Properties();

    {
        props.setProperty("a","5");
        props.setProperty("b","true");
        props.setProperty("c","-3");
    }
    //首先先随便设置一些值

    /**
     *
     * 现在，我们假设你的程序需要从这些属性中读取一个值，该值是以秒为单位计量的一段时间。
     * 由于一段时间必须是正数，你想要该方法符合下面的签名：
     * public int readDuration(Properties props, String name)
     * 即，如果给定属性对应的值是一个代表正整数的字符串，就返回该整数值，任何其他的情况都返
     * 回0。为了明确这些需求，你可以采用JUnit的断言，将它们形式化：
     * assertEquals(5, readDuration(param, "a"));
     * assertEquals(0, readDuration(param, "b"));
     * assertEquals(0, readDuration(param, "c"));
     * assertEquals(0, readDuration(param, "d"));
     * 这些断言反映了初始的需求：如果属性是a，readDuration方法返回5，因为该属性对应的
     * 字符串能映射到一个正数；对于属性b，方法的返回值是0，因为它对应的值不是一个数字；对于
     * c，方法的返回值是0，因为虽然它对应的值是个数字，不过它是个负数；对于d，方法的返回值
     * 是0，因为并不存在该名称对应的属性。让我们以命令式编程的方式实现满足这些需求的方法，
     * 代码清单如下所示。
     */

    public int readDuration(Properties props,String name){
        String value = props.getProperty(name);
        if(value!=null){
            try{
                int i = Integer.parseInt(value);
                if(i>0){
                    return i;
                }
            }catch (NumberFormatException e){

            }
        }
        return 0;//
    }

    /**
     * 改进思路
     */

    public int readDuration0(Properties props,String name){

        Optional<String> value = Optional.ofNullable(props.getProperty(name));

        return value.flatMap(OptionalImprove::stringToInt)
                .filter(integer -> integer.intValue()>0)
                .orElse(0);

    }

    public static Optional<Integer> string2Int(String src){
        try {
            return Optional.of(Integer.parseInt(src));
        }catch (NumberFormatException e){
            return Optional.empty();
        }
    }

}
