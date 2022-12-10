package com.creactivestudio.mathtilt;




public class SensorData  {
    float x1;
    float x2;
    float x3;
    long  timestamp;

    public SensorData (float x1, float x2, float x3, long timestamp) {
        this.x1=x1;
        this.x2=x2;
        this.x3=x3;
        this.timestamp=timestamp;
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getX3() {
        return x3;
    }

    public void setX3(float x3) {
        this.x3 = x3;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
