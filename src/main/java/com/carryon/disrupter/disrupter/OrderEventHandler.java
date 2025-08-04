package com.carryon.disrupter.disrupter;

import com.lmax.disruptor.EventHandler;

public class OrderEventHandler implements EventHandler<OrderEvent> {

    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        // Thread.sleep(1); // 模拟处理时间，注释掉原来会卡住的代码
        System.err.println("消费者: " + event.getValue());
    }

}
