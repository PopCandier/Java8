package com.pop.java8.chapter9;

/**
 * @author Pop
 * @date 2019/10/16 23:48
 *
 *
 * default 方法主要是为了兼容
 *
 * 避免增加代码后，子类需要实现的窘境
 */
public interface Sized {

    int size();

    default boolean isEmpty(){
        return size()==0;
    }

}

/**
 * 现在你已经了解了默认方法怎样以兼容的方式演进库函数了。除了这种用例，还有其他场景
 * 也能利用这个新特性吗？当然有，你可以创建自己的接口，并为其提供默认方法。这一节中，我
 * 们会介绍使用默认方法的两种用例：
 * 可选方法和行为的多继承。
 *
 *
 * 你很可能也碰到过这种情况，类实现了接口，不过却刻意地将一些方法的实现留白。我们以
 * Iterator接口为例来说。Iterator接口定义了hasNext、next，还定义了remove方法。Java 8
 * 之前，由于用户通常不会使用该方法，remove方法常被忽略。因此，实现Interator接口的类
 * 通常会为remove方法放置一个空的实现，这些都是些毫无用处的模板代码。
 * 采用默认方法之后，你可以为这种类型的方法提供一个默认的实现，这样实体类就无需在自
 * 己的实现中显式地提供一个空方法。比如，在Java 8中，Iterator接口就为remove方法提供了
 * 一个默认实现，如下所示：
 * interface Iterator<T> {
 *  boolean hasNext();
 *  T next();
 *  default void remove() {
 *  throw new UnsupportedOperationException();
 *  }
 * }
 * 通过这种方式，你可以减少无效的模板代码。实现Iterator接口的每一个类都不需要再声
 * 明一个空的remove方法了，因为它现在已经有一个默认的实现。
 *
 *
 * 2. 利用正交方法的精简接口
 * 假设你需要为你正在创建的游戏定义多个具有不同特质的形状。有的形状需要调整大小，但
 * 是不需要有旋转的功能；有的需要能旋转和移动，但是不需要调整大小。这种情况下，你怎么设
 * 计才能尽可能地重用代码？
 * 你可以定义一个单独的Rotatable接口，并提供两个抽象方法setRotationAngle和
 * getRotationAngle，如下所示：
 */

interface Rotatable{

    void setRotationAngle(int angleInDegrees);

    int getRotationAngle();

    default void rotateBy(int angleINDegrees){
        setRotationAngle((getRotationAngle()+angleINDegrees)%360);//提供了一个默认实现
    }

}

/**
 * 这种方式和模板设计模式有些相似，都是以其他方法需要实现的方法定义好框架算法。
 * 现在，实现了Rotatable的所有类都需要提供setRotationAngle和getRotationAngle
 * 的实现，但与此同时它们也会天然地继承rotateBy的默认实现。
 * 类似地，你可以定义之前看到的两个接口Moveable和Resizable。它们都包含了默认实现。
 * 下面是Moveable的代码：
 */

interface Moveable {
    int getX();
    int getY();
    void setX(int x);
    void setY(int y);
    default void moveHorizontally(int distance){
        setX(getX() + distance);
    }
    default void moveVertically(int distance){
        setY(getY() + distance);
    }
}

interface Resizable {
    int getWidth();
    int getHeight();
    void setWidth(int width);
    void setHeight(int height);
    void setAbsoluteSize(int width, int height);
    default void setRelativeSize(int wFactor, int hFactor){
        setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
    }
}
//这里所谓的正交，也就意味这是借口隔离原则那样的，每个借口职责统一规划
//例如ArrayList实现，他虽然只继承了AbstractArrayList但是他却实现了6个借口
//所以，如果我们下面将这些全部组合起来，组合借口，那么子类也就具有他们的行为了

class Monster implements Rotatable,Moveable,Resizable{

    @Override
    public void setRotationAngle(int angleInDegrees) {

    }

    @Override
    public int getRotationAngle() {
        return 0;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setWidth(int width) {

    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public void setAbsoluteSize(int width, int height) {

    }

    //同时，享有他们的方法
    public static void main(String[] args) {
        Monster monster = new Monster();
        monster.rotateBy(180);// 由Rotatable借口而来
        monster.moveVertically(10);// 由于move接口而来
    }
}