package com.example.jozef.vcelicky;

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
    private boolean charging;
    private GregorianCalendar timeStamp;
    private String hiveLocation;
    int temperature_in_up_limit;
    int temperature_in_down_limit;
    int weight_limit;
    int temperature_out_up_limit;
    int temperature_out_down_limit;
    int humidity_in_up_limit;
    int humidity_in_down_limit;
    int humidity_out_up_limit;
    int humidity_out_down_limit;
    int batery_limit;
    double longitude;
    double latitude;

    private long time;

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

    public HiveBaseInfo(String hiveId, String hiveName, String hiveLocation, float outsideTemperature, float insideTemperature, float outsideHumidity, float insideHumidity, float weight, boolean accelerometer, float battery) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.outsideHumidity = outsideHumidity;
        this.insideHumidity = insideHumidity;
        this.weight = weight;
        this.accelerometer = accelerometer;
        this.battery = battery;
        this.hiveLocation = hiveLocation;
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

    public HiveBaseInfo(String hiveId, String hiveName, String hiveLocation){
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.hiveLocation =  hiveLocation;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getTemperature_in_up_limit() {
        return temperature_in_up_limit;
    }

    public void setTemperature_in_up_limit(int temperature_in_up_limit) {
        this.temperature_in_up_limit = temperature_in_up_limit;
    }

    public int getTemperature_in_down_limit() {
        return temperature_in_down_limit;
    }

    public void setTemperature_in_down_limit(int temperature_in_down_limit) {
        this.temperature_in_down_limit = temperature_in_down_limit;
    }

    public int getWeight_limit() {
        return weight_limit;
    }

    public void setWeight_limit(int weight_limit) {
        this.weight_limit = weight_limit;
    }

    public int getTemperature_out_up_limit() {
        return temperature_out_up_limit;
    }

    public void setTemperature_out_up_limit(int temperature_out_up_limit) {
        this.temperature_out_up_limit = temperature_out_up_limit;
    }

    public int getTemperature_out_down_limit() {
        return temperature_out_down_limit;
    }

    public void setTemperature_out_down_limit(int temperature_out_down_limit) {
        this.temperature_out_down_limit = temperature_out_down_limit;
    }

    public int getHumidity_in_up_limit() {
        return humidity_in_up_limit;
    }

    public void setHumidity_in_up_limit(int humidity_in_up_limit) {
        this.humidity_in_up_limit = humidity_in_up_limit;
    }

    public int getHumidity_in_down_limit() {
        return humidity_in_down_limit;
    }

    public void setHumidity_in_down_limit(int humidity_in_down_limit) {
        this.humidity_in_down_limit = humidity_in_down_limit;
    }

    public int getHumidity_out_up_limit() {
        return humidity_out_up_limit;
    }

    public void setHumidity_out_up_limit(int humidity_out_up_limit) {
        this.humidity_out_up_limit = humidity_out_up_limit;
    }

    public int getHumidity_out_down_limit() {
        return humidity_out_down_limit;
    }

    public void setHumidity_out_down_limit(int humidity_out_down_limit) {
        this.humidity_out_down_limit = humidity_out_down_limit;
    }

    public int getBatery_limit() {
        return batery_limit;
    }

    public void setBatery_limit(int batery_limit) {
        this.batery_limit = batery_limit;
    }

    public String getHiveLocation() {
        return hiveLocation;
    }

    public void setHiveLocation(String hiveLocation) {
        this.hiveLocation = hiveLocation;
    }

    public HiveBaseInfo() {

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

