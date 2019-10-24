package com.pop.java8.chapter12;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * @program: java8
 * @description: java8中新的日期对象
 * @author: Pop
 * @create: 2019-10-22 16:48
 *
 * 开始使用新的日期和时间API时，你最先碰到的可能是LocalDate类。该类的实例是一个不
 * 可变对象，它只提供了简单的日期，并不含当天的时间信息。另外，它也不附带任何与时区相关
 * 的信息。
 * 你可以通过静态工厂方法of创建一个LocalDate实例。LocalDate实例提供了多种方法来
 * 读取常用的值，比如年份、月份、星期几等，如下所示。
 **/
public class LocalDateDemo {

    @Test
    public void demo1(){

        //创建一个日期
        LocalDate date = LocalDate.
                of(2019,10,22);//2019-10-22
        //我们可以获得年月日
        int year = date.getYear();// 2019
        Month month = date.getMonth();// Oct
        int day = date.getDayOfMonth();// 22
        DayOfWeek dow = date.getDayOfWeek();//TUESDAY
        int moth = date.lengthOfMonth();// 31 大月
        boolean leap = date.isLeapYear(); // false

        //获取当前时间
        LocalDate today = LocalDate.now();

        System.out.println(date);//2019-10-22
    }

    /**
     * 传递一个TemporalField（暂时的；当时的；现世的）参数给get方法拿到同样的信息。TemporalField是一个接口，它定
     * 义了如何访问temporal对象某个字段的值。ChronoField枚举实现了这一接口，所以你可以很
     * 方便地使用get方法得到枚举元素的值，如下所示。
     */
    public void demo2(){

        LocalDate date = LocalDate.of(2019,10,23);

        int year = date.get(ChronoField.YEAR);
        int month = date.get(ChronoField.MONTH_OF_YEAR);
        int day = date.get(ChronoField.DAY_OF_MONTH);

        /**
         * 类似地，一天中的时间，比如13:45:20，可以使用LocalTime类表示。你可以使用of重载的
         * 两个工厂方法创建LocalTime的实例。第一个重载函数接收小时和分钟，第二个重载函数同时还
         * 接收秒。同LocalDate一样，LocalTime类也提供了一些getter方法访问这些变量的值，如下
         * 所示。
         */
        LocalTime time = LocalTime.of(14,18,20);
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        /**
         * LocalDate和LocalTime都可以通过解析代表它们的字符串创建。使用静态方法parse，你
         * 可以实现这一目的：
         */
        LocalDate date1 = LocalDate.parse("2019-10-23");
        LocalTime time1 = LocalTime.parse("13:45:20");
        /**
         * 你可以向parse方法传递一个DateTimeFormatter。该类的实例定义了如何格式化一个日
         * 期或者时间对象。正如我们之前所介绍的，它是替换老版java.util.DateFormat的推荐替代
         * 品。我们会在12.2节展开介绍怎样使用DateTimeFormatter。同时，也请注意，一旦传递的字
         * 符串参数无法被解析为合法的LocalDate或LocalTime对象，这两个parse方法都会抛出一个继
         * 承自RuntimeException的DateTimeParseException异常。
         */
    }

    /**
     * 合并日期和时间
     *
     * 这个复合类名叫LocalDateTime，是LocalDate和LocalTime的合体。它同时表示了日期
     * 和时间，但不带有时区信息，你可以直接创建，也可以通过合并日期和时间对象构造，如下所示。
     *
     */
    public void demo3(){
        LocalDate date = LocalDate.of(2019,10,23);
        LocalTime time = LocalTime.of(14,26,10);

        // 2019-10-23 T 13：45：20
        LocalDateTime dt1 = LocalDateTime.
                of(2019,Month.OCTOBER,23,14,25,10);

        LocalDateTime dt2 = LocalDateTime.of(date,time);

        LocalDateTime dt3 = date.atTime(14,28,20);

        LocalDateTime dt4 = date.atTime(time);

        LocalDateTime dt5 = time.atDate(date);

        /**
         * 注意，通过它们各自的atTime或者atDate方法，向LocalDate传递一个时间对象，或者向
         LocalTime传递一个日期对象的方式，你可以创建一个LocalDateTime对象。你也可以使用
         toLocalDate或者toLocalTime方法，从LocalDateTime中提取LocalDate或者LocalTime
         组件：

         */

        LocalDate date1 = dt1.toLocalDate();
        LocalTime time1 = dt1.toLocalTime();

    }

    /**
     * 机器眼中的时间
     *
     * 作为人，我们习惯于以星期几、几号、几点、几分这样的方式理解日期和时间。毫无疑问，
     * 这种方式对于计算机而言并不容易理解。从计算机的角度来看，建模时间最自然的格式是表示一
     * 个持续时间段上某个点的单一大整型数。这也是新的java.time.Instant类对时间建模的方
     * 式，基本上它是以Unix元年时间（传统的设定为UTC时区1970年1月1日午夜时分）开始所经历的
     * 秒数进行计算。
     * 你可以通过向静态工厂方法ofEpochSecond传递一个代表秒数的值创建一个该类的实例。静
     * 态工厂方法ofEpochSecond还有一个增强的重载版本，它接收第二个以纳秒为单位的参数值，对
     * 传入作为秒数的参数进行调整。重载的版本会调整纳秒参数，确保保存的纳秒分片在0到999 999
     * 999之间。这意味着下面这些对ofEpochSecond工厂方法的调用会返回几乎同样的Instant对象：
     */
    public void demo4(){

        Instant.ofEpochSecond(3);
        Instant.ofEpochSecond(3,0);
        //2 秒之后再机上100万纳秒（1 秒）
        Instant.ofEpochSecond(2,1_000_000_000);
        Instant.ofEpochSecond(4,-1_000_000_000);

        /**
         * 正如你已经在LocalDate及其他为便于阅读而设计的日期时间类中所看到的那样，
         * Instant类也支持静态工厂方法now，它能够帮你获取当前时刻的时间戳。我们想要特别强调一
         * 点，Instant的设计初衷是为了便于机器使用。它包含的是由秒及纳秒所构成的数字。所以，它
         * 无法处理那些我们非常容易理解的时间单位。比如下面这段语句：
         * int day = Instant.now().get(ChronoField.DAY_OF_MONTH);
         * 它会抛出下面这样的异常：
         * java.time.temporal.UnsupportedTemporalTypeException: Unsupported field:
         *  DayOfMonth
         * 但是你可以通过Duration和Period类使用Instant，接下来我们会对这部分内容进行介绍。
         */
    }

    /**
     * 一般而言，我们都希望计算出两个时间点之间的时间差
     *
     * 目前为止，你看到的所有类都实现了Temporal接口，Temporal接口定义了如何读取和操纵
     * 为时间建模的对象的值。之前的介绍中，我们已经了解了创建Temporal实例的几种方法。很自
     * 然地你会想到，我们需要创建两个Temporal对象之间的duration。Duration类的静态工厂方
     * 法between就是为这个目的而设计的。你可以创建两个LocalTimes对象、两个LocalDateTimes
     对象，或者两个Instant对象之间的duration，如下所示：
     */
    public void demo5(){

        LocalTime time1 = null;
        LocalTime time2 = null;

        LocalDateTime dateTime1 = null;
        LocalDateTime dateTime2 = null;

        Instant instant1 = null;
        Instant instant2 = null;

        Duration d1 = Duration.between(time1,time2);
        Duration d2 = Duration.between(dateTime1,dateTime2);
        Duration d3 = Duration.between(instant1,instant2);

        /**
         * 由于LocalDateTime和Instant是为不同的目的而设计的，一个是为了便于人阅读使用，
         * 另一个是为了便于机器处理，所以你不能将二者混用。如果你试图在这两类对象之间创建
         * duration，会触发一个DateTimeException异常。此外，由于Duration类主要用于以秒和纳
         * 秒衡量时间的长短，你不能仅向between方法传递一个LocalDate对象做参数。

         不能混用的时依旧是，你不能这样用

         Duration d1 = Duration.between(dateTime1,time2);
         */

        /**
         * 如果你需要以年、月或者日的方式对多个时间单位建模，可以使用Period类。使用该类的
         * 工厂方法between，你可以使用得到两个LocalDate之间的时长，如下所示：
         */

        Period tenDays = Period.between(LocalDate.of(2014,3,8),
                LocalDate.of(2014,3,18));

        /**
         * 最后，Duration和Period类都提供了很多非常方便的工厂类，直接创建对应的实例；换
         * 句话说，就像下面这段代码那样，不再是只能以两个temporal对象的差值的方式来定义它们的
         * 对象。
         */
        Duration threeMinutes = Duration.ofMinutes(3);//三分钟的时间差
        Duration threeMinutes1 = Duration.of(3, ChronoUnit.MINUTES);

        Period p1 = Period.ofDays(10); //10天
        Period p2 = Period.ofWeeks(3);//三周
        //2 年 6 月 1 天
        Period twoYearsSixMonthsOneDay = Period.of(2,6,1);

        

    }

}
