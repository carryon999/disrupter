package com.carryon.disrupter.test;

import com.carryon.disrupter.constants.Constants;
import com.carryon.disrupter.model.Data;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @program: BlocklingQueue
 * @description: 队列测试对比
 * @author: Carry Chen
 * @create: 2025-08-04 13:06
 **/

public class BlocklingQueue {

    public static void main(String[] args) {
        final ArrayBlockingQueue<Data> queue = new ArrayBlockingQueue<Data>(100000000);
        final long startTime = System.currentTimeMillis();

        new Thread(() -> {
            long i = 0;
            while (i < Constants.EVENT_NUM_OM) {
                Data data = new Data(i, "c" + i);
                try {
                    queue.put(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int k = 0;
                while (k < Constants.EVENT_NUM_OM) {
                    try {
                        queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    k++;
                }
                long endTime = System.currentTimeMillis();
                System.out.println("ArrayBlockingQueue costTime = " + (endTime - startTime) + " ms");
            }
        }).start();
    }
}
