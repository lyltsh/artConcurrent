package com.art.cocurrent.chapter3;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/29:11 PM
 */
public class ReentrantLockExample {
    private int a = 0;
    private ReentrantLock lock = new ReentrantLock();

    public void writer() {
        lock.lock(); //获取锁
        try {
            a++;
        } finally {
            lock.unlock(); //释放锁
        }
    }

    public void reader() {
        lock.lock(); //获取锁
        try {
            int i = a;
        } finally {
            lock.unlock(); //释放锁
        }
    }
}
