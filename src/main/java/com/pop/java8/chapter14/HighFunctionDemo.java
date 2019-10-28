package com.pop.java8.chapter14;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Pop
 * @date 2019/10/28 16:31
 */
public class HighFunctionDemo {

    /**
     * 科里化
     *
     * 给出科里化的理论定义之前，让我们先来看一个例子。应用程序通常都会有国际化的需求，
     * 将一套单位转换到另一套单位是经常碰到的问题。
     * 单位转换通常都会涉及转换因子以及基线调整因子的问题。比如，将摄氏度转换到华氏度的
     * 公式是CtoF(x) = x*9/5 + 32。
     *
     * 所有的单位转换几乎都遵守下面这种模式：
     * (1) 乘以转换因子
     * (2) 如果需要，进行基线调整
     */

    static double converter(double x,double f,double b){
        return x*f+b;
    }
    /**
     * 这里x是你希望转换的数量，f是转换因子，b是基线值。但是这个方法有些过于宽泛了。通
     * 常，你还需要在同一类单位之间进行转换，比如公里和英里。当然，你也可以在每次调用
     * converter方法时都使用3个参数，但是每次都提供转换因子和基准比较繁琐，并且你还极有可
     * 能输入错误。
     */

    /**
     * 当然，你也可以为每一个应用编写一个新方法，不过这样就无法对底层的逻辑进行复用。
     * 这里我们提供一种简单的解法，它既能充分利用已有的逻辑，又能让converter针对每个应
     * 用进行定制。你可以定义一个“工厂”方法，它生产带一个参数的转换方法，我们希望借此来说
     * 明科里化。下面是这段代码：
     */
    static DoubleUnaryOperator curriedConverter(double f,double b){
        return (double x)-> x*f+b;
    }

    /**
     * 现在，你要做的只是向它传递转换因子和基准值（f和b），它会不辞辛劳地按照你的要求返
     * 回一个方法（使用参数x）。比如，你现在可以按照你的需求使用工厂方法产生你需要的任何
     * converter：
     */

    DoubleUnaryOperator convertCtoF = curriedConverter(9.0/5,32);
    DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6,0);
    DoubleUnaryOperator convertKmtoMi = curriedConverter(0.6214,0);

    /**
     * 由于DoubleUnaryOperator定义了方法applyAsDouble，你可以像下面这样使用你的
     * converter：
     */
    public void demo1(){

        double gbp = convertUSDtoGBP.applyAsDouble(1000);

    }

    /**
     * 这样一来，你的代码就更加灵活了，同时它又复用了现有的转换逻辑！让我们一起回顾下你
     * 都做了哪些工作。你并没有一次性地向converter方法传递所有的参数x、f和b，相反，你只是
     * 使用了参数f和b并返回了另一个方法，这个方法会接收参数x，最终返回你期望的值x * f + b。
     * 通过这种方式，你复用了现有的转换逻辑，同时又为不同的转换因子创建了不同的转换方法。
     */

    /**
     * 科里化的理论定义
     * 科里化①是一种将具备2个参数（比如，x和y）的函数f转化为使用一个参数的函数g，并
     * 且这个函数的返回值也是一个函数，它会作为新函数的一个参数。后者的返回值和初始函数的
     * 返回值相同，即f(x,y) = (g(x))(y)。
     * 当然，我们可以由此推出：你可以将一个使用了6个参数的函数科里化成一个接受第2、4、
     * 6号参数，并返回一个接受5号参数的函数，这个函数又返回一个接受剩下的第1号和第3号参数
     * 的函数。
     * 一个函数使用所有参数仅有部分被传递时，通常我们说这个函数是部分应用的（partially
     * applied）。
     *
     * 也就是说 科里化 主要是为了
     * 先固定部分参数的计算逻辑，然后某个参数暴露给调用方
     * 进行定制，由于那部分参数已经被固定并且返回了行为函数
     * 所以，这是一个部分应用的概念。
     */

    /**
     * 现在我们转而讨论函数式编程的另一个方面。如果你不能修改数据结构，还能用它们编程
     * 吗？
     */

}
