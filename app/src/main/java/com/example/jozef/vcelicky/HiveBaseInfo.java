package com.example.jozef.vcelicky;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

/**
 * Created by MSI on 2. 11. 2017.
 */

public class HiveBaseInfo {

    int hiveId;
    String hiveName;
    float outsideTemperature;
    float insideTemperature;
    float humidity;
    float weight;
    boolean accelerometer;
    float battery;
    GregorianCalendar timeStamp;

    public HiveBaseInfo(int hiveId, String hiveName, float outsideTemperature, float insideTemperature, float humidity, float weight, GregorianCalendar timeStamp, boolean accelerometer, float battery) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.humidity = humidity;
        this.weight = weight;
        this.accelerometer = accelerometer;
        this.battery = battery;
        this.timeStamp = timeStamp;
    }

    public HiveBaseInfo(int hiveId, String hiveName, float outsideTemperature, float insideTemperature, float humidity, float weight, boolean accelerometer, float battery) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.humidity = humidity;
        this.weight = weight;
        this.accelerometer = accelerometer;
        this.battery = battery;
    }

    public HiveBaseInfo(int hiveId, String hiveName, float outsideTemperature, float insideTemperature, float humidity, float weight) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.humidity = humidity;
        this.weight = weight;
    }

    public HiveBaseInfo(int hiveId, String hiveName, float outsideTemperature, float insideTemperature, float humidity, float weight,GregorianCalendar timeStamp ) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.humidity = humidity;
        this.weight = weight;
        this.timeStamp = timeStamp;
    }

    public GregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(GregorianCalendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getHiveId() {
        return hiveId;
    }

    public void setHiveId(int hiveId) {
        this.hiveId = hiveId;
    }

    public String getHiveName() {
        return hiveName;
    }

    public void setHiveName(String hiveName) {
        this.hiveName = hiveName;
    }

    public float getOutsideTemperature() {
        return outsideTemperature;
    }

    public void setOutsideTemperature(float outsideTemperature) {
        this.outsideTemperature = outsideTemperature;
    }

    public float getInsideTemperature() {
        return insideTemperature;
    }

    public void setInsideTemperature(float insideTemperature) {
        this.insideTemperature = insideTemperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(boolean accelerometer) {
        this.accelerometer = accelerometer;
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }
}

