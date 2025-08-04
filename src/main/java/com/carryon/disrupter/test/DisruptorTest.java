package com.carryon.disrupter.test;

import com.carryon.disrupter.disrupter.OrderEvent;
import com.carryon.disrupter.disrupter.OrderEventFactory;
import com.carryon.disrupter.disrupter.OrderEventHandler;
import com.carryon.disrupter.producer.OrderEventProducer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

/**
 * @program: disruptor
 * @description: Disruptor性能测试
 * @author: Carry Chen
 * @create: 2025-08-04 13:06
 **/
public class DisruptorTest {

    public static void main(String[] args) throws InterruptedException {

        // 参数准备工作
        OrderEventFactory orderEventFactory = new OrderEventFactory();
        int ringBufferSize = 1024 * 1024; // RingBuffer 大小，必须是2的N次方；

        /**
         * 1 eventFactory: 消息(event)工厂对象
         * 2 ringBufferSize: 容器的长度
         * 3 executor: 线程池(建议使用自定义线程池) RejectedExecutionHandler
         * 4 ProducerType: 单生产者 还是 多生产者
         * 5 waitStrategy: 等待策略
         */
        // 1. 实例化disruptor对象
        Disruptor<OrderEvent> disruptor = new Disruptor<OrderEvent>(
                orderEventFactory,
                ringBufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy());

        // 2. 添加消费者的监听 (构建disruptor 与 消费者的一个关联关系)
        disruptor.handleEventsWith(new OrderEventHandler());

        // 3. 启动disruptor
        disruptor.start();

        // 4. 获取实际存储数据的容器: RingBuffer
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();

        // 5. 创建生产者
        OrderEventProducer producer = new OrderEventProducer(ringBuffer);

        // 6. 指定缓冲区大小
        ByteBuffer bb = ByteBuffer.allocate(8);

        long startTime = System.currentTimeMillis();

        // 7. 发送消息(生产者发送消息)
        for (long i = 0; i < 100; i++) {
            bb.putLong(0, i);
            producer.sendData(bb);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Disruptor costTime = " + (endTime - startTime) + " ms");

        // 关闭disruptor
        Thread.sleep(1000); // 等待消费完成
        disruptor.shutdown();
    }
}