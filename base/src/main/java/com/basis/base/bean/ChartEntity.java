package com.basis.base.bean;

/**
 * 作者：chs on 2016/9/6 15:14
 * 邮箱：657083984@qq.com
 * 线形图bean
 */
public class ChartEntity {
    private String xLabel;
    private int yValue;

    public ChartEntity(String xLabel, int yValue) {
        this.xLabel = xLabel;
        this.yValue = yValue;
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public ChartEntity(int yValue) {
        this.yValue = yValue;
    }

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }
}
