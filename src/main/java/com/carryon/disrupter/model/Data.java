package com.carryon.disrupter.model;

/**
 * @program: BlocklingQueue
 * @description: 测试数据类
 * @author: Carry Chen
 * @create: 2025-08-04 13:06
 **/
public class Data {
    private long value;
    private String info;

    public Data(long value, String info) {
        this.value = value;
        this.info = info;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Data{" +
                "value=" + value +
                ", info='" + info + '\'' +
                '}';
    }
}