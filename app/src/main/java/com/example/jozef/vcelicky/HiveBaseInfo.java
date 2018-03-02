package com.example.jozef.vcelicky;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

/**
 * Created by MSI on 2. 11. 2017.
 */

public class HiveBaseInfo {

    private String hiveId;
    private String hiveName;
    private float outsideTemperature;
    private float insideTemperature;
    private float outsideHumidity;
    private float insideHumidity;
    private float weight;
    private boolean accelerometer;
    private float battery;
    private GregorianCalendar timeStamp;

    public HiveBaseInfo(String hiveId, String hiveName, float outsideTemperature, float insideTemperature, float outsideHumidity, float insideHumidity, float weight, boolean accelerometer, float battery, GregorianCalendar timeStamp) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.outsideHumidity = outsideHumidity;
        this.insideHumidity = insideHumidity;
        this.weight = weight;
        this.accelerometer = accelerometer;
        this.battery = battery;
        this.timeStamp = timeStamp;
    }

    public HiveBaseInfo(String hiveId, String hiveName, float outsideTemperature, float insideTemperature, float outsideHumidity, float insideHumidity, float weight, boolean accelerometer, float battery) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.outsideHumidity = outsideHumidity;
        this.insideHumidity = insideHumidity;
        this.weight = weight;
        this.accelerometer = accelerometer;
        this.battery = battery;
    }

    public HiveBaseInfo(String hiveId, String hiveName, float outsideTemperature, float insideTemperature, float outsideHumidity, float insideHumidity, float weight) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.outsideHumidity = outsideHumidity;
        this.insideHumidity = insideHumidity;
        this.weight = weight;
    }

    public HiveBaseInfo(String hiveId, String hiveName, float outsideTemperature, float insideTemperature, float outsideHumidity, float insideHumidity, float weight,GregorianCalendar timeStamp ) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.outsideHumidity = outsideHumidity;
        this.insideHumidity = insideHumidity;
        this.weight = weight;
        this.timeStamp = timeStamp;
    }

    public HiveBaseInfo(String hiveId, String hiveName){
        this.hiveId = hiveId;
        this.hiveName = hiveName;
    }

    public GregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(GregorianCalendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHiveId() {
        return hiveId;
    }

    public void setHiveId(String hiveId) {
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

    public float getOutsideHumidity() {
        return outsideHumidity;
    }

    public void setOutsideHumidity(float humidity) {
        this.outsideHumidity = humidity;
    }

    public float getInsideHumidity() {
        return insideHumidity;
    }

    public void setInsideHumidity(float humidity) {
        this.insideHumidity = humidity;
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

