package com.example.jozef.vcelicky;

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


    public HiveBaseInfo(int hiveId, String hiveName, float outsideTemperature, float insideTemperature, float humidity, float weight) {
        this.hiveId = hiveId;
        this.hiveName = hiveName;
        this.outsideTemperature = outsideTemperature;
        this.insideTemperature = insideTemperature;
        this.humidity = humidity;
        this.weight = weight;
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
}
