package com.art.cocurrent.chapter4;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/114:01 PM
 */
public class MultiThread {
    public static void main(String[] args) {
        //获取Java线程管理MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //不需要获取同步的monitor和synchronize信息，仅获取线程和堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        //遍历线程信息，仅打印线程ID和线程名称信息
//        for (ThreadInfo threadInfo : threadInfos) {
//            System.out.println(threadInfo.getThreadId() + "," + threadInfo.getThreadName());
//        }
        Arrays.stream(threadInfos).forEach(System.out::println);
    }
}
