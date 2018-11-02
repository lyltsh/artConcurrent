package com.art.cocurrent.chapter4.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/253:34 PM
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {

    // 线程池最大限制数量
    private static final int MAX_WORKER_NUMBERS = 10;
    // 线程池默认大小
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    // 线程池最小数量
    private static final int MIN_WORKER_NUMBERS = 1;
    //工作列表，将会里面插入工作
    private final LinkedList<Job> jobs = new LinkedList<Job>();
    // 工作者列表
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    // 工作者线程数量
    private int workerNum = DEFAULT_WORKER_NUMBERS;
    // 线程编号
    private AtomicLong threadNum = new AtomicLong();


    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);
    }


    @Override
    public void execute(Job job) {
        if (job != null) {
            synchronized (jobs) {
                //添加一个job，然后进行通知
                jobs.add(job);
                jobs.notify();
            }
        }

    }

    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {
            //限制worker数量不能超过最大数量
            if (num + this.workerNum > MAX_WORKER_NUMBERS) {
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num > this.workerNum) {
                throw new IllegalStateException("beyond workerNum");
            }
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                if (workers.remove(worker)) {
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    //初始化线程工作者
    private void initializeWorkers(final int workerNum) {
        for (int i = 0; i < workerNum; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    //工作者，复制消费线程
    class Worker implements Runnable {

        //是否在运行
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    //如果jobs是空的，则wait
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            //感知到外部对worker的中断操作，返回
                            Thread.currentThread().interrupt();
                            return;
                        }

                    }
                    //取出一个job执行
                    job = jobs.removeFirst();
                    if (job != null) {
                        try {
                            job.run();
                        } catch (Exception e) {
                            //忽略job执行中的exception
                        }

                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}
