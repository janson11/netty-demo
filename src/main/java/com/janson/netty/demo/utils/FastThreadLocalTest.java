package com.janson.netty.demo.utils;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * @Description:
 * @Author: Janson
 * @Date: 2022/1/19 15:57
 **/
public class FastThreadLocalTest {

    private static FastThreadLocal<Object> threadLocal = new FastThreadLocal<Object>() {
        @Override
        protected Object initialValue() throws Exception {
            return new Object();
        }
    };

    public static void main(String[] args) {
        new Thread(() -> {
            Object o = threadLocal.get();
            System.out.println(o);
            while (true) {
                threadLocal.set(new Object());
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            Object o = threadLocal.get();
            System.out.println(o);
            while (true) {
                System.out.println(threadLocal.get() == o);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
