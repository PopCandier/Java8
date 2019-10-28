package com.pop.java8.chapter14;

import lombok.Data;

/**
 * @author Pop
 * @date 2019/10/28 17:11
 *
 * 转入新主题之前，让我们再看一个使用其他数据结构的例子——我们想讨论的对象是二叉查
 * 找树，它也是HashMap实现类似接口的方式。我们的设计中Tree包含了String类型的键，以及
 * int类型的键值，它可能是名字或者年龄：
 */
@Data
public class Tree {

    private String key;
    private int val;
    private Tree left,right;
    public Tree(String k,int v,Tree l,Tree r){
        key=k;
        val =v;
        left = l;
        right=r;
    }

    /**
     * 你希望通过二叉查找树找到String值对应的整型数。现在，我们想想你该如何更新与某个
     * 键对应的值（简化起见，我们假设键已经存在于这个树中了）：
     */
//    public static Tree update(String k, int newval, Tree t) {
//        if (t == null)
//            t = new Tree(k, newval, null, null);
//        else if (k.equals(t.getKey()))
//            t.getVal() = newval;
//        else if (k.compareTo(t.getKey()) < 0)
//            t.getLeft() = update(k, newval, t.getLeft());
//        else
//            t.getRight() = update(k, newval, t.getRight());
//        return t;
//    }
    /**
     * 注意，这两个版本的update都会对现有的树进行修改，这意味着使用树存放映射关系的所
     * 有用户都会感知到这些修改。
     *
     *
     */

    public static Tree fupdate(String k, int newval, Tree t) {
        return (t == null) ?
                new Tree(k, newval, null, null) :
                k.equals(t.key) ?
                        new Tree(k, newval, t.left, t.right) :
                        k.compareTo(t.key) < 0 ?
                                new Tree(t.key, t.val, fupdate(k,newval, t.left), t.right) :
                                new Tree(t.key, t.val, t.left, fupdate(k,newval, t.right));
    }
    /**
     * 逻辑图参考readme
     *
     */
}

class TressProcessor{

    public static int lookup(String k,int defaultval,Tree t){
        if(t==null) return defaultval;
        if(k.equals(t.getKey())) return t.getVal();//从本地找
        return lookup(k,defaultval,k.compareTo(t.getKey())<0?t.getLeft():t.getRight());
    }



}
