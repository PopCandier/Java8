package com.pop.java8.chapter12_bak;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Pop
 * @date 2019/10/23 23:08
 */
public class DateFormatterDemo {

    /**
     * 打印输出及解析日期-时间对象
     *
     * 处理日期和时间对象时，格式化以及解析日期时间对象是另一个非常重要的功能。新的
     * java.time.format包就是特别为这个目的而设计的。这个包中，最重要的类是DateTimeFormatter。创建格式器最简单的方法是通过它的静态工厂方法以及常量。像BASIC_ISO_DATE
     * 和 ISO_LOCAL_DATE 这样的常量是 DateTimeFormatter 类的预定义实例。所有的
     * DateTimeFormatter实例都能用于以一定的格式创建代表特定日期或时间的字符串。比如，下
     * 面的这个例子中，我们使用了两个不同的格式器生成了字符串：
     */

    @Test
    public void demo1(){

        LocalDate date = LocalDate.of(2014,3,18);
        String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
//        String s3 = date.format(DateTimeFormatter.ISO_OFFSET_DATE);
        System.out.println(s1+" "+s2+" ");

        /**
         * 你也可以通过解析代表日期或时间的字符串重新创建该日期对象。所有的日期和时间API
         * 都提供了表示时间点或者时间段的工厂方法，你可以使用工厂方法parse达到重创该日期对象
         * 的目的：
         *
         * 也就是转换的目的
         * 20140318 2014-03-18
         */
        LocalDate date1 = LocalDate.parse("20140318",DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate date2 = LocalDate.parse("2014-03-18",DateTimeFormatter.ISO_LOCAL_DATE);
        /**
         * 和老的java.util.DateFormat相比较，所有的DateTimeFormatter实例都是线程安全
         * 的。所以，你能够以单例模式创建格式器实例，就像DateTimeFormatter所定义的那些常量，
         * 并能在多个线程间共享这些实例。DateTimeFormatter类还支持一个静态工厂方法，它可以按
         * 照某个特定的模式创建格式器，代码清单如下。
         */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date3 = LocalDate.of(2019,10,23);
        String date3_str = date3.format(formatter);
        //再转换回来
        LocalDate date4 = LocalDate.parse(date3_str,formatter);
        System.out.println(date3_str+" "+date4);//23/10/2019 2019-10-23

        //创建本地化的DateTimeFormatter
        DateTimeFormatter chinaFormatter =
                DateTimeFormatter.ofPattern("yyyy 年 MMMM d 日", Locale.CHINA);
        LocalDate date6 = LocalDate.of(2019,10,23);
        String formatterChina = date.format(chinaFormatter);
        LocalDate date7 = LocalDate.parse(formatterChina,chinaFormatter);
        System.out.println(formatterChina+" "+date7);//2014 年 三月 18 日 2014-03-18

        /**
         * 最后，如果你还需要更加细粒度的控制，DateTimeFormatterBuilder类还提供了更复杂
         * 的格式器，你可以选择恰当的方法，一步一步地构造自己的格式器。另外，它还提供了非常强大
         * 的解析功能，比如区分大小写的解析、柔性解析（允许解析器使用启发式的机制去解析输入，不
         * 精 确 地 匹 配 指 定 的 模 式 ）、 填 充 ， 以 及 在 格 式 器 中 指 定 可 选 节 。 比 如 ， 你 可 以 通 过
         * DateTimeFormatterBuilder自己编程实现我们在代码清单12-11中使用的italianFormatter，代码清单如下。
         */
        DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.CHINA);

    }

    /**
     * 之前你看到的日期和时间的种类都不包含时区信息。时区的处理是新版日期和时间API新增
     * 加的重要功能，使用新版日期和时间API时区的处理被极大地简化了。新的java.time.ZoneId
     * 类是老版java.util.TimeZone的替代品。它的设计目标就是要让你无需为时区处理的复杂和
     * 繁琐而操心，比如处理日光时（Daylight Saving Time，DST）这种问题。跟其他日期和时间类一
     * 样，ZoneId类也是无法修改的。
     */
    public void demo2(){
        /**
         * 时区是按照一定的规则将区域划分成的标准时间相同的区间。在ZoneRules这个类中包含了
         * 40个这样的实例。你可以简单地通过调用ZoneId的getRules()得到指定时区的规则。每个特定
         * 的ZoneId对象都由一个地区ID标识，比如：
         */
        ZoneId romeZone = ZoneId.of("Europe/Rome");
        /**
         * 地区ID都为“{区域}/{城市}”的格式，这些地区集合的设定都由英特网编号分配机构（IANA）
         * 的时区数据库提供。你可以通过Java 8的新方法toZoneId将一个老的时区对象转换为ZoneId：
         */
        ZoneId zoneId = TimeZone.getDefault().toZoneId();
        /**
         * 一旦得到一个ZoneId对象，你就可以将它与LocalDate、LocalDateTime或者是Instant
         * 对象整合起来，构造为一个ZonedDateTime实例，它代表了相对于指定时区的时间点，代码清
         * 单如下所示。
         */
        LocalDate date = LocalDate.of(2014, Month.MARCH,18);
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);//从这个时区开始转化时区

        LocalDateTime dateTime = LocalDateTime.of(2014,Month.MARCH,18,13,45);
        ZonedDateTime zdt2 = dateTime.atZone(romeZone);

        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(romeZone);

        //三者之间的关系，也就LocalDate LocalTime ZoneId LocalDateTime ZonedDateTime
        /**
         *
         *  | LocalDate | LocalTime | ZoneId |
         *  |     LocalDateTime     |
         *  |             ZonedDateTime     |
         *
         *  大包含的关系
         */

        /**
         * 通过ZoneId，你还可以将LocalDateTime转换为Instant：
         */

//        Instant instantFormDateTime = dateTime.toInstant()
        LocalDateTime dateTime7 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
//        Instant instantFromDateTime = dateTime.toInstant(romeZone);//这里会报错
//        你也可以通过反向的方式得到LocalDateTime对象：
        Instant instant7 = Instant.now();
        LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant, romeZone);

    }

}
