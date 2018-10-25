package com.art.cocurrent.chapter4.connection;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * @author: leiyulin
 * @description: 通过构造函数初始化连接的最大数量，获取连接池，释放链接
 * @date:2018/10/252:52 PM
 */
public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int size) {
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                pool.add(ConnectionDriver.createConnection());
            }
        }
    }

    /**
     * 释放链接池，将connection加回至pool
     *
     * @param connection
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                //释放连接池之后，进行通知，这样其他消费者能感知到连接池中归还了一个连接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    /**
     * 在mills内无法返回连接，则返回null
     *
     * @param mills
     * @return
     */
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            //完全超时
            if (mills <= 0) {
                //只要连接池为空，一致等待
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection connection = null;
                if (!pool.isEmpty()) {
                    connection = pool.removeFirst();
                }
                return connection;
            }
        }
    }
}
