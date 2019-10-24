package com.pop.java8.chapter12_bak;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.*;

/**
 * @author Pop
 * @date 2019/10/23 22:29
 */
public class LocalDateDemo {

    /**
     * 使用下面的一些操作，看看date的变量值是什么
     */
    public void demo1(){

        LocalDate date = LocalDate.of(2019,10,23);
        date = date.with(ChronoField.MONTH_OF_YEAR,9);//将年改成9月
        date = date.plusYears(2).minusDays(10);//加上两年，再减去十天
        date.withYear(2011);//再修改时间为2011年，但是因为没有接受返回的副本，所以不会改变
        //时间变为 2021 年 9月 13 天

        /**
         * 使用 TemporalAdjuster
         *
         * 截至目前，你所看到的所有日期操作都是相对比较直接的。有的时候，你需要进行一些更加
         * 复杂的操作，比如，将日期调整到下个周日、下个工作日，或者是本月的最后一天。这时，你可
         * 以使用重载版本的with方法，向其传递一个提供了更多定制化选择的TemporalAdjuster对象，
         * 更加灵活地处理日期。对于最常见的用例，日期和时间API已经提供了大量预定义的
         * TemporalAdjuster。你可以通过TemporalAdjuster类的静态工厂方法访问它们，如下所示。
         */

        LocalDate date1 = LocalDate.of(2014,3,18);// 2014-3-18
        LocalDate date2 = date1. //时间调整到下一个周日
                with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));   // 2014-03-23
        //调整为当月的最后一天
        LocalDate date3 = date.with(TemporalAdjusters.lastDayOfMonth()); // 2014-03-31
    }

    /**
     * 正如我们看到的，使用TemporalAdjuster我们可以进行更加复杂的日期操作，而且这些方
     * 法的名称也非常直观，方法名基本就是问题陈述。此外，即使你没有找到符合你要求的预定义的
     * TemporalAdjuster，创建你自己的TemporalAdjuster也并非难事。实际上，
     * TemporalAdjuster接口只声明了单一的一个方法（这使得它成为了一个函数式接口），定义如下。
     */
}

/**
 *这意味着TemporalAdjuster接口的实现需要定义如何将一个Temporal对象转换为另一
 * 个Temporal对象。你可以把它看成一个UnaryOperator<Temporal>。花几分钟时间完成测验
 * 12.2，练习一下我们到目前为止所学习的东西，请实现你自己的TemporalAdjuster。
 实现一个定制的TemporalAdjuster

 请设计一个NextWorkingDay类，该类实现了TemporalAdjuster接口，能够计算明天
 的日期，同时过滤掉周六和周日这些节假日。格式如下所示：

 date = date.with(new NextWorkingDay());
 */

class NextWorkingDay implements  TemporalAdjuster{

    @Override
    public Temporal adjustInto(Temporal temporal) {
        //首先你需要获得今天是星期几
//        LocalDate date =
//                LocalDate.from(temporal);

        //读取今天是周几
        DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
        int dayToAdd = 1;
        if(dow==DayOfWeek.FRIDAY) dayToAdd=3;//如果是周五，增加三天到下周一
        else if(dow==DayOfWeek.SATURDAY) dayToAdd=2;
        //因为周天也是加一天，所以不算
        return temporal.plus(dayToAdd, ChronoUnit.DAYS);
    }

    public void demo1(){
        /**
         * 因为这是个函数式接口，所以如果你要想要使用
         * date = date.with(new NextWorkingDay());
         * 已函数式的方式去实现的话，你可能需要着这样写
         */

        LocalDate date = null;
        date = date.with((temporal -> {
            DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if(dow==DayOfWeek.FRIDAY) dayToAdd=3;//如果是周五，增加三天到下周一
            else if(dow==DayOfWeek.SATURDAY) dayToAdd=2;
            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        }));
        //就是这个这样子的过程，不过，我们当然希望可以封装起来使用】
        //增加可复用性
        /**
         * 如果你想要使用Lambda表达式定义TemporalAdjuster对象，
         * 推荐使用TemporalAdjusters类的静态工厂方法ofDateAdjuster，
         * 它接受一个UnaryOperator<LocalDate>
         类型的参数，代码如下：
         */
        TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
                localDate -> {
                    DayOfWeek dow = DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK));
                    int dayToAdd = 1;
                    if(dow==DayOfWeek.FRIDAY) dayToAdd=3;
                    else if(dow==DayOfWeek.SATURDAY) dayToAdd=2;
                    return localDate.plus(dayToAdd,ChronoUnit.DAYS);
                }
        );
        date = date.with(nextWorkingDay);

    }
}
