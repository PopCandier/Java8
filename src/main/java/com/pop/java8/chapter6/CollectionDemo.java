package com.pop.java8.chapter6;

import com.pop.java8.chapter4.Dish;
import com.pop.java8.chapter4.StreamDemo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.reducing;

/**
 * @program: java8
 * @description:
 * @author: Pop
 * @create: 2019-10-09 11:39
 **/
public class CollectionDemo {

    /**
     * 归约和汇总
     */

    public static void main(String[] args) {
//        count();

        group();
    }


    private static void count() {
        //collect 收集者，接受一个收集器 Collector 他的行为将会被收录
        long howManyDishe=
                StreamDemo.menu.stream().collect(Collectors.counting());

        //也有简单的写法
        long howManyDished = StreamDemo.menu.stream().count();

    }

    private static void maxAndMin(){
        /**
         * 如果你想要找出菜单中热量最高的菜
         * 可以使用两个收集器
         * Collectors.maxBy 和 Collectors.minBy
         * 来计算流中最大或最小值，
         * 这两个收集器接受一个Comparator参数来比较流
         * 中的元素。你可以创造一个Comparator来根据热量
         * 对材料进行比较
         */

        Comparator<Dish> dishCaloriesComparator
                = Comparator.comparingInt(Dish::getCalories);
        //比较标准  比较卡路里最高的踩
        Optional<Dish> mostCalorieDish=StreamDemo.
                menu.stream().collect(maxBy(dishCaloriesComparator));

        Optional<Dish> smallCalorieDish =
                StreamDemo.menu.stream().collect(
                        minBy(dishCaloriesComparator)
                );

        mostCalorieDish.ifPresent(d-> System.out.println(d.getCalories()));
        smallCalorieDish.ifPresent(d-> System.out.println(d.getCalories()));

    }

    private static void sum(){
        /**
         * 汇总，Collectors 类专门为汇总提供了一个工厂方法
         * Collectors.summingInt 它可接受一个对象映射为求和所需的int函数
         * 并返回一个收集器。
         *
         * 该收集器在传递给普通的collect方法后，执行
         * 我们所需的汇总操作
         */
        int total=StreamDemo.menu.stream().collect(summingInt(Dish::getCalories));
        int total1=StreamDemo.menu.stream().map(Dish::getCalories).reduce(0,Integer::sum);
        System.out.println(total+" "+total1);
    }

    private static void average(){
        //同时，你也可以求平均值
        Double average=StreamDemo.menu.stream().collect(averagingDouble(Dish::getCalories));

    }

    private static void summary(){

        /**
         * 当然，一些情况下，我们可能需要获得不止最大值和最小值这种，
         * 也许会获得很多，这个时候通过一次summarizing操作就可以完成全部
         */

        IntSummaryStatistics summaryStatistics= StreamDemo.menu.stream().collect(summarizingInt(Dish::getCalories));
        System.out.println(summaryStatistics.getMax()+" "+
                summaryStatistics.getAverage()+" "+summaryStatistics.getCount()+" "+summaryStatistics.getSum());
    }

    private static void joining2(){

        /**
         * 连接字符串，joining工厂方法返回的收集器会把对流中每一个对象应用
         * toString方法得到所有字符串组成一个字符串
         * 那么在这个例子中，你会把菜单中所有的菜肴名称连接起来。
         */

        String shortMenu=StreamDemo.menu.stream().map(Dish::getName)
                .collect(joining());

        //你还可以选择 加入分割符
        String shortMenu1=StreamDemo.menu.stream().map(Dish::getName).collect(joining(", "));

        System.out.println(shortMenu1);


    }

    /**
     * 归约操作
     *
     * 事实上，我们已经讨论的所有收集器，都可以用reducing工厂方法
     * 归约过程的特殊情况而已，Collectors.reducing工厂方法是所有这些
     * 特殊情况的一般化
     *
     * 例如，用reducing方法创建的收集器来计算你的菜单总热量
     */
    private static void reduce(){
        //算出菜单的总热量
        int total=StreamDemo.menu.stream().collect(reducing(0,Dish::getCalories,(i,j)->i+j));
        //找出热量最高的菜
        Optional<Dish> dish= StreamDemo.menu.stream().collect(reducing((d1,d2)->d1.getCalories()>d1.getCalories()?d1:d2));
        /**
         * 第一参数是归约操作的初始值，也是流中没有元素时的返回值，所以很显然对于数值和而言
         * 是一个合适的值
         *
         * 第二参数就是将菜肴转化为int的一个类型
         *
         * 第三个参数就是二合一求值
         */

        dish.ifPresent(d-> System.out.println(d.getName()+" "+d.getCalories()));
    }

    private static void group(){
        /**
         * 分组
         *
         * 一个常见的数据库操作是根据一个
         * 或者多个属性对集合中的项目进行分组
         * 就像前面讲到货币进行分组的例子一样，如果
         * 用指令式风格来重写的话，就很容易
         * 转化为一个非常容易看懂的语句
         *
         * Collectors.groupingBy
         */

        Map<Dish.Type, List<Dish>> dishedByType
                = StreamDemo.menu.stream().collect(
                        //分类函数
                        Collectors.groupingBy(Dish::getType));

        System.out.println(dishedByType);

        /**
         * 但是，分类函数不一定像方法引用那样可用，因为你分类的
         * 条件可能会比较复杂
         *
         * 例如，将热量不到400卡路里的菜分为“低热量”（diet）
         * 热量400到700卡路里的菜划为普通（normal）
         * 高于700卡路里的划为高热量（fat）
         * 可能由于Dish类的作者没有把这个操作写成一个方法
         * 你无法使用方法引用，但是你可以将这个逻辑写成lambda表达式
         */
        Map<CaloricLevel,List<Dish>> s= StreamDemo.menu.stream().collect(
                Collectors.groupingBy(
                        dish -> {
                            if(dish.getCalories()<=400)return CaloricLevel.DIET;
                            else if(dish.getCalories()>=700)return  CaloricLevel.FAT;
                            else return CaloricLevel.NORMAL;
                        }
                )
        );

        /**
         * 多级分组
         *
         * 要实现多级分组，我们可以使用一个
         * 由双参数版本的Collectors.groupingBy 工厂方法创建
         * 的收集器，它除了普通的分类函数之外，还可以接受Collector
         * 类型的第二个参数。那么要进行二级分组的话，我们可以把
         * 内层的groupingBy传递给外层groupingBy，并定义一个成为流中
         * 项目分类的二级标准。
         */
        Map<Dish.Type,Map<CaloricLevel,List<Dish>>> typeMapMap=
        StreamDemo.menu.stream().collect(
                Collectors.groupingBy(Dish::getType,
                        Collectors.groupingBy(
                                dish -> {
                                    if(dish.getCalories()<=400)return CaloricLevel.DIET;
                                    else if(dish.getCalories()>=700)return  CaloricLevel.FAT;
                                    else return CaloricLevel.NORMAL;
                                }
                        )));
        /**
         * 一般来说，把groupingBy看做“桶”比较容易明白。
         * 第一个groupingBy给每个键建立了一个桶，再由一个下游的收集器
         * 去收集这个放入桶中
         */

        /**
         * 按子组收集数据
         *
         * 其实我们仔细观察可以发现groupingBy的第二个参数
         * 是一个收集器类型，也就是Collector，也就说不一定是groupingBy
         * 之前提到的收集器类型都可以使用
         *
         * 我们可以第二个参数可以数一数有多少个这个类型一共有多少个
         */

        StreamDemo.menu.stream().collect(Collectors.groupingBy(Dish::getType,
                Collectors.counting()));//分类的总数


        /**
         * 在举一个其它的例子，前面查找出热量最高的菜肴的收集器改一改
         * 按照菜的类型分类
         */
        Map<Dish.Type,Optional<Dish>> t= StreamDemo.menu.stream().collect(groupingBy(Dish::getType,
                maxBy(Comparator.comparingInt(Dish::getCalories))));
        /**
         * 这里需要补充的是，因为使用的是maxBy收集器，所以返回值是Optional
         * 但这是问题所在。
         * 如果菜单里中没有某一类型的Dish，这个类型就不会对应一个Optional.empty()
         * 也就是说，如果这某一Dish.Type不存在在这个集合中的情况下，这个类别的分组就不会存在
         * 就好像没有统计一样，在我们的映像中，起码你没统计到这一分类，map中也可以有
         * 这个类的key，只不过可能value，这里是optional也可以存在，只不过是空的。
         * 但是上述方法，现在无法实现
         *
         * 这意味着Optional包装器在这里不是很有用，因为它不会仅仅因为它是归约收集器的
         * 返回类型而表达一个最终可能不存在却意外存在的值
         */

        /**
         * 所以，这意味着我们需要另一种方案
         *
         * 1.把收集器结果转化为另一种类型
         *
         * 因为分组操作的map结果中的每个值上包装Optional没什么用，
         * 所以你可能想要把他们去掉，要做到这一点，或者更一般的说，把收集器返回
         * 的结果转化为另一种类型，你可以使用Collectors.collectingAndThen工厂方法返回的收集器
            查找每个子组中热量最高的Dish
         */
        Map<Dish.Type,Dish> t2= StreamDemo.menu.stream().collect(
                groupingBy(Dish::getType,
                        collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories)),Optional::get)));
        /**
         * 这个收集器需要两个参数，要转化的收集器，和转化函数
         */

        /**
         * 其他的一些常用的组合，与groupingBy联合使用的例子
         *
         * 一般来说，通过groupingBy工厂方法的第二个参数传递的收集器将会对同一组的所有流
         * 元素执行进一步的归约，例如，你还重用求出所有菜肴热量总和的收集器，不过这次是对
         * 每一组Dish求和
         *
         */

        Map<Dish.Type,Integer> t3=StreamDemo.menu.stream().collect(groupingBy(Dish::getType,summingInt(Dish::getCalories)));

        /**
         * 然而尝尝和groupingBy联合使用的另一个收集器是mapping方法生成的。
         * 这个方法接受两个参数，一个函数对流中的元素做变化，另一个则将变换的结果收集起来。
         * 其目的是在累加之前对每个输入元素应用一个映射函数，这样就可以让接受特定类型元素的收集器
         * 适应不同类型的对象。
         *
         * 比如，你想要知道对于每种类型Dish，菜肴中有哪些CaloricLevel，我们可以把groupingBy和mapping收集器
         * 结合起来。
         */

        Map<Dish.Type,Set<CaloricLevel>> t4=StreamDemo.menu.stream().collect(groupingBy(Dish::getType,mapping(dish->{//第一个就是对流中的元素
           //进行变化的函数
           if(dish.getCalories()<=400) return CaloricLevel.DIET;
           else if(dish.getCalories()<=700) return CaloricLevel.NORMAL;
           else return CaloricLevel.FAT;
       },toSet())));//这个就是收集，只不过这里是set
    }

    private static void partition(){
        /**
         * 分区。
         *
         * 分区是分组的特殊情况，原为映射为key的分类函数变为了谓词，也就是predicate，他将会
         * 返回一个boolean类型的值，所以，分区的map只有两种key，true与false
         *
         * 例如，如果你是素食者或清了一位速食的朋友来共进晚餐
         * 可能会想要把菜单按照速食和非速食分开
         */
        Map<Boolean,List<Dish>> partitionedMenu=
                StreamDemo.menu.stream().collect(partitioningBy(Dish::isVegetarian));

        //那么通过Map中键为true的值，就可以找出所有素食菜肴了
        List<Dish> vegetarianDishes = partitionedMenu.get(true);

        //虽然，你使用这个方法也可以相同的结果
        List<Dish> t2 = StreamDemo.menu.stream().filter(Dish::isVegetarian)
                .collect(toList());

        //当然，这个也可以传入第二个参数
        StreamDemo.menu.stream().collect(
                partitioningBy(Dish::isVegetarian,
                        groupingBy(Dish::getType))
        );

        //找到素食与非素食最高热量最高的那一道菜
        Map<Boolean,Dish> t4=StreamDemo.menu.stream()
                .collect(partitioningBy(Dish::isVegetarian,
                        collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories)),Optional::get)));

    }

    /**
     * 将数字按质数和非质数分区
     *
     * 首先我们需要一个可以判断一个数是否是质数的方法
     */
    public boolean isPrime(int candidate){
        //2-candidate 的范围中，都没有质数将会返回true
        /**
         * 产生一个自然数范围，从2开始，不包括待测数
         *
         * 如果待测数字不能被流中任意数字整除，则返回true
         *
         */
        return IntStream.range(2,candidate)
                .noneMatch(i->candidate%i==0);
    }

    /**
     * 一个简单的优化是仅测试小于等于数平方根的因子
     */
    public boolean isPrime0(int candidate){
        int candidateRoot=(int)Math.sqrt((double) candidate);
        //也就是缩小的范围，减少了candidate的倍数
        return IntStream.rangeClosed(2,candidateRoot)
                .noneMatch(i->candidate%i==0);
    }

    /**
     * 方法的一个总结
     *
     * 大于1，且只能被1和自己整除的数，叫做质数
     */

    public Map<Boolean,List<Integer>> partitionPrimes(int n){
        return IntStream.rangeClosed(2,n).boxed()
                .collect(partitioningBy
                        (candidate->isPrime0(candidate)));
    }



    enum CaloricLevel{DIET,NORMAL,FAT}
}
