package com.art.cocurrent.chapter5;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/11/85:30 PM
 */
public class FairAndUnfairTest {
    public static void main(String[] args) {
//        testFair(fairLock);
        testUnFair(unFailLock);
    }

    private static Lock fairLock = new ReentrantLock2(true);
    private static Lock unFailLock = new ReentrantLock2(false);
    private static CountDownLatch latch;

    public static void testFair(Lock lock) {
        testLock(fairLock);
    }

    public static void testUnFair(Lock lock) {
        testLock(unFailLock);
    }

    private static void testLock(Lock lock) {
        latch = new CountDownLatch(1);
        for (int i = 0; i < 5; i++) {
            Thread thread = new Job(lock);
            thread.setName("" + i);
            thread.start();
        }
        latch.countDown();

    }

    private static final class Job extends Thread {
        private Lock lock;

        public Job(Lock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 2; i++) {
                lock.lock();
                try {
                    System.out.println("Lock by [" + getName() + "], Waiting by " + ((ReentrantLock2) lock).getQueuedThreads());
                } finally {
                    lock.unlock();
                }
            }
        }
    }


    private static final class ReentrantLock2 extends ReentrantLock {
        public ReentrantLock2(boolean fair) {
            super(fair);
        }

        public Collection<Thread> getQueuedThreads() {
            List<Thread> threadList = new ArrayList<>(super.getQueuedThreads());
            Collections.reverse(threadList);
            return threadList;
        }
    }
}
