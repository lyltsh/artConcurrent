package com.art.cocurrent.chapter3;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/110:32 PM
 */
public class SynchronizedExample {
    int     a    = 0;
    boolean flag = false;

    public synchronized void writer() { //获取锁
        a = 1;
        flag = true;
    } //释放锁

    public synchronized void reader() { //获取锁
        if (flag) {
            int i = a;
        } //释放锁
    }
}
