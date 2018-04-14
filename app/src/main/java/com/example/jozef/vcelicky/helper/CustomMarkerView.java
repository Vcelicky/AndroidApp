package com.example.jozef.vcelicky.helper;

import android.content.Context;
import android.widget.TextView;

import com.example.jozef.vcelicky.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CustomMarkerView extends MarkerView {

    private TextView timestamp, unit, value;
    private String unitName, symbol;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomMarkerView(Context context, int layoutResource, String unitName, String symbol) {
        super(context, layoutResource);
        timestamp = findViewById(R.id.timestamp);
        unit = findViewById(R.id.unit);
        value = findViewById(R.id.value);
        this.unitName = unitName;
        this.symbol = symbol;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        timestamp.setText(dateFormat.format(new Date((long)e.getX())));
        unit.setText(unitName + ":");
        value.setText("" + (int)e.getY() + " " + symbol);

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), - getHeight());
        }

        return mOffset;
    }
}
