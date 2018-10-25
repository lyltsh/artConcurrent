package com.art.cocurrent.chapter4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/1510:01 PM
 */
public class WaitNotify {
    private static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws Exception{
        Thread waitThread = new Thread(new WaitClass(), "WaitThread");
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        notifyThread.start();
    }

    static class WaitClass implements Runnable {

        @Override
        public void run() {
            //加锁，拥有lock的monitor
            synchronized (lock) {
                //当条件不满足时，继续wait，同时释放了lock的锁
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true, " +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //满足条件，完成工作
                System.out.println(Thread.currentThread() + " flag is false" +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }

    static class Notify implements Runnable {

        @Override
        public void run() {
            //加锁，拥有lock的monitor
            synchronized (lock) {
                // 获得lock的锁，然后进行通知，通知时不会释放lock的锁
                // 直到当前线程释放了lock后，waitThread才能从wait方法中返回
                System.out.println(Thread.currentThread() + " hold lock."
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();
                flag = false;
                SleepUtils.second(5);
            }

            //再次加锁
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold lock again."
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                SleepUtils.second(5);
            }
        }
    }
}
