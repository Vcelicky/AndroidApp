package com.example.jozef.vcelicky.helper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class ValueFormatter implements IAxisValueFormatter {
    private String unit;

    public ValueFormatter(String unit){
        this.unit = unit;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return (int) value + unit;
    }
}
