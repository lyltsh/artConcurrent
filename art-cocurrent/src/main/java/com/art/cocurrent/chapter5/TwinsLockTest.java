package com.art.cocurrent.chapter5;

import com.art.cocurrent.chapter4.SleepUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/11/36:17 PM
 */
public class TwinsLockTest {

    private static TwinsLock lock = new TwinsLock();

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            Worker w = new Worker();
            w.setDaemon(true);
            w.start();
        }

        for (int i = 0; i < 10; i++) {
            SleepUtils.second(1);
            System.out.println();
        }
    }

    public static final class Worker extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    lock.lock();
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }
    }
}
