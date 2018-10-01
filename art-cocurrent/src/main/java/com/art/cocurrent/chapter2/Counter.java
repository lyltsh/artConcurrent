package com.art.cocurrent.chapter2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/19:12 PM
 */
public class Counter {
    private AtomicInteger atomicI = new AtomicInteger(0);
    private int i = 0;

    public static void main(String[] args) {
        final Counter cas = new Counter();
        List<Thread> ts = new ArrayList<Thread>(600);
        long start = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        cas.count();
                        cas.safeCount();
                    }
                }
            });
            ts.add(t);
        }
        for (Thread t : ts) {
            t.start();

        }
        // 等待线程执行完成
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println(cas.i);
        System.out.println(cas.atomicI.get());
        System.out.println(System.currentTimeMillis() - start);
    }

    private void count() {
        i++;
    }

    private void safeCount() {
        int num = atomicI.get();
        while (true) {
//            boolean cas = atomicI.compareAndSet(num, ++num);
//            if(cas){
//                break;
//            }
            if (atomicI.compareAndSet(num, ++num)) {
                break;
            }
        }

        //简单方式线程安全进行自增
//        atomicI.incrementAndGet();

    }
}
