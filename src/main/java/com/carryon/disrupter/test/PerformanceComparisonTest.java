package com.carryon.disrupter.test;

import com.carryon.disrupter.constants.Constants;
import com.carryon.disrupter.disrupter.OrderEvent;
import com.carryon.disrupter.disrupter.OrderEventFactory;
import com.carryon.disrupter.model.Data;
import com.carryon.disrupter.producer.OrderEventProducer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

/**
 * @program: disruptor
 * @description: Disruptor vs ArrayBlockingQueue 性能对比测试
 * @author: Carry Chen
 * @create: 2025-08-04 13:06
 **/
public class PerformanceComparisonTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 性能对比测试开始 ===");
        System.out.println("测试数据量: " + Constants.EVENT_NUM_OM);

        // 测试ArrayBlockingQueue性能
        testArrayBlockingQueue();

        System.out.println("\n等待5秒后开始Disruptor测试...");
        Thread.sleep(5000);

        // 测试Disruptor性能
        testDisruptor();

        System.out.println("\n=== 性能对比测试结束 ===");
    }

    /**
     * 测试ArrayBlockingQueue性能
     */
    private static void testArrayBlockingQueue() throws InterruptedException {
        System.out.println("\n--- ArrayBlockingQueue 测试开始 ---");

        final ArrayBlockingQueue<Data> queue = new ArrayBlockingQueue<Data>(1024 * 1024);
        final long startTime = System.currentTimeMillis();

        // 生产者线程
        Thread producer = new Thread(() -> {
            long i = 0;
            while (i < Constants.EVENT_NUM_OM) {
                Data data = new Data(i, "data" + i);
                try {
                    queue.put(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                i++;
            }
            System.out.println("ArrayBlockingQueue 生产者完成");
        });

        // 消费者线程
        Thread consumer = new Thread(() -> {
            int k = 0;
            while (k < Constants.EVENT_NUM_OM) {
                try {
                    Data data = queue.take();
                    // 模拟消费处理，不打印避免影响性能
                    if (k % 1000000 == 0) {
                        System.out.println("ArrayBlockingQueue 已消费: " + k);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                k++;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("ArrayBlockingQueue costTime = " + (endTime - startTime) + " ms");
        });

        producer.start();
        consumer.start();

        // 等待线程完成
        producer.join();
        consumer.join();

        System.out.println("--- ArrayBlockingQueue 测试完成 ---");
    }

    /**
     * 测试Disruptor性能
     */
    private static void testDisruptor() throws InterruptedException {
        System.out.println("\n--- Disruptor 测试开始 ---");

        OrderEventFactory orderEventFactory = new OrderEventFactory();
        int ringBufferSize = 1024 * 1024;

        Disruptor<OrderEvent> disruptor = new Disruptor<OrderEvent>(
                orderEventFactory,
                ringBufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy());

        // 自定义事件处理器，避免打印影响性能
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            // 模拟消费处理
            if (sequence % 1000000 == 0) {
                System.out.println("Disruptor 已消费: " + sequence);
            }
        });

        disruptor.start();

        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        OrderEventProducer producer = new OrderEventProducer(ringBuffer);
        ByteBuffer bb = ByteBuffer.allocate(8);

        long startTime = System.currentTimeMillis();

        // 生产数据
        for (long i = 0; i < Constants.EVENT_NUM_OM; i++) {
            bb.putLong(0, i);
            producer.sendData(bb);
        }

        System.out.println("Disruptor 生产者完成");

        // 等待消费完成
        Thread.sleep(2000);

        long endTime = System.currentTimeMillis();
        System.out.println("Disruptor costTime = " + (endTime - startTime) + " ms");

        disruptor.shutdown();
        System.out.println("--- Disruptor 测试完成 ---");
    }
}