package com.janson.netty.demo.queue;


import org.jctools.queues.MpscArrayQueue;

/**
 * @Description:
 * @Author: Janson
 * @Date: 2022/3/14 7:38
 **/
public class MpscArrayQueueTest {
    public static final MpscArrayQueue<String> MPSC_ARRAY_QUEUE = new MpscArrayQueue<>(2);

    public static void main(String[] args) {
        for (int i = 0; i <= 2; i++) {
            int index = i;
            new Thread(() -> MPSC_ARRAY_QUEUE.offer("data" + index), "thread" + index).start();
        }
        try {
            Thread.sleep(1000L);
            MPSC_ARRAY_QUEUE.add("data");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("队列大小：" + MPSC_ARRAY_QUEUE.size() + "队列容量：" + MPSC_ARRAY_QUEUE.capacity());
        System.out.println("出队：" + MPSC_ARRAY_QUEUE.remove());
        System.out.println("出队：" + MPSC_ARRAY_QUEUE.poll());
    }
}
