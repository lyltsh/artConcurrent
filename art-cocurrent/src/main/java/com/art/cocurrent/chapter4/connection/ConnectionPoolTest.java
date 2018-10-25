package com.art.cocurrent.chapter4.connection;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/253:07 PM
 */
public class ConnectionPoolTest {
    static ConnectionPool pool = new ConnectionPool(10);
    //保证所有线程同时开始
    static CountDownLatch start = new CountDownLatch(1);
    //保证所有线程结束后，返回main线程
    static CountDownLatch end;

    public static void main(String[] args) throws Exception {
        //线程数量，可以修改观察状况
        int threadCount = 40;
        end = new CountDownLatch(threadCount);
        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }
        start.countDown();
        end.await();
        System.out.println("total invoke: " + threadCount * count);
        System.out.println("got connection:" + got);
        System.out.println("notgot connection:" + notGot);
    }

    static class ConnectionRunner implements Runnable {

        private int count;
        private AtomicInteger got;
        private AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (Exception e) {

            }
            while (count > 0) {
                try {
                    //从线程池中获取连接，如果1000ms内没有返回，将会得到null
                    //分别统计got和notgot数量
                    Connection connection = pool.fetchConnection(1000);
                    if (connection != null) {
                        try {
                            //模拟执行commit操作，sleep 100ms
                            connection.createStatement();
                            connection.commit();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        notGot.incrementAndGet();
                    }

                } catch (Exception e) {

                } finally {
                    count--;
                }

            }
            end.countDown();
        }
    }
}
