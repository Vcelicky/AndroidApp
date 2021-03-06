package com.example.jozef.vcelicky.helper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jozef on 02. 03. 2018.
 */

public class HourAxisValueFormatter implements IAxisValueFormatter {

    private long referenceTimestamp; // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;
    private String[] mValues;
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ENGLISH);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    public HourAxisValueFormatter(String[] values) {
        //this.referenceTimestamp = referenceTimestamp;
        //this.mDataFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        //this.mDate = new Date();
        this.mValues = values;
    }

    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     */

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // convertedTimestamp = originalTimestamp - referenceTimestamp
//        long convertedTimestamp = (long) value;
//
//        // Retrieve original timestamp
//        long originalTimestamp = referenceTimestamp + convertedTimestamp;
//
//        // Convert timestamp to hour:minute
//        return getHour(originalTimestamp);
        return dateFormat.format(new Date((long) value));
    }

    private String getHour(long timestamp) {
        try {
            mDate.setTime(timestamp * 1000);
            return mDataFormat.format(mDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}