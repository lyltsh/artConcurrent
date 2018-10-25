package com.art.cocurrent.chapter3;

/**
 * @author: leiyulin
 * @description:
 * @date:2018/10/110:31 PM
 */
public class ReorderExample {
    int     a    = 0;
    boolean flag = false;

    public void writer() {
        a = 1; //1
        flag = true; //2
    }

    public void reader() {
        if (flag) { //3
            int i = a * a; //4
        }
    }
}
