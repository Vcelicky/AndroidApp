package com.example.jozef.vcelicky;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by MSI on 2. 11. 2017.
 */

public class AdapterHive  extends ArrayAdapter<HiveBaseInfo> {

    ArrayList<HiveBaseInfo> hiveList;
    private static String TAG = "MainActivity";

    public AdapterHive(Context context, ArrayList<HiveBaseInfo> hiveList){
        //nezabudni mu povedat ze listView bude pouzivat toto xml pre jeden riadok "custom_row_devices"
        super(context, R.layout.hive_row , hiveList);
        this.hiveList = hiveList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.hive_row, parent, false);

        //vyber i-ty prvok z listu
       // Device i = items.get(position);
        TextView textHiveName = (TextView) customView.findViewById(R.id.hive_name);
        textHiveName.setText(hiveList.get(position).getHiveName());

        TextView textHiveLocation = (TextView) customView.findViewById(R.id.hive_location);
        textHiveLocation.setText(hiveList.get(position).getHiveLocation()+"   ");

        TextView textOutTemperature = (TextView) customView.findViewById(R.id.textOutTemperature);
        textOutTemperature.setText(String.valueOf(hiveList.get(position).getOutsideTemperature())+"°C (vonkajšia)"); // ("Vonkajšia teplota"+"hiveList.get(position).getHiveName()"+"°C");

        if (hiveList.get(position).getOutsideTemperature() >= hiveList.get(position).getTemperature_out_up_limit() ||  hiveList.get(position).getOutsideTemperature() <= hiveList.get(position).getTemperature_out_down_limit()){
            textOutTemperature.setTextColor(Color.parseColor("#FF0000"));
        }

        TextView textInTemperature =   (TextView) customView.findViewById(R.id.textInTemperature);
        textInTemperature.setText( String.valueOf(hiveList.get(position).getInsideTemperature())+"°C (vnútorná)");

        if (hiveList.get(position).getInsideTemperature() >= hiveList.get(position).getTemperature_in_up_limit() ||  hiveList.get(position).getInsideTemperature() <= hiveList.get(position).getTemperature_in_down_limit()){
            textInTemperature.setTextColor(Color.parseColor("#FF0000"));
        }

        TextView textOutHumidity = customView.findViewById(R.id.textOutHumidity);
        textOutHumidity.setText(String.valueOf(hiveList.get(position).getOutsideHumidity())+"% (vonkajšia)");

        if (hiveList.get(position).getOutsideHumidity() >= hiveList.get(position).getHumidity_out_up_limit() ||  hiveList.get(position).getOutsideHumidity() <= hiveList.get(position).getHumidity_out_down_limit()){
            textOutHumidity.setTextColor(Color.parseColor("#FF0000"));
        }

        TextView textInHumidity = customView.findViewById(R.id.textInHumidity);
        textInHumidity.setText(String.valueOf(hiveList.get(position).getInsideHumidity())+"% (vnútorná)");

        if (hiveList.get(position).getInsideHumidity() >= hiveList.get(position).getHumidity_in_up_limit() ||  hiveList.get(position).getInsideHumidity() <= hiveList.get(position).getHumidity_in_down_limit()){
            textInHumidity.setTextColor(Color.parseColor("#FF0000"));
        }

        TextView textWeight = (TextView) customView.findViewById(R.id.textWeight);
        textWeight.setText(String.valueOf(hiveList.get(position).getWeight())+"kg");

        if (hiveList.get(position).getWeight() >= hiveList.get(position).getWeight_limit()){
           textWeight.setTextColor(Color.parseColor("#FF0000"));
        }

        TextView textTime = (TextView) customView.findViewById(R.id.textTime);
//        textHiveName.setText(hiveList.get(position).getTimeStamp().toString());
        Calendar ts = hiveList.get(position).getTimeStamp();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        textTime.setText(ts.get(Calendar.DAY_OF_MONTH)+"."+ts.get(Calendar.MONTH)+"."+ts.get(Calendar.YEAR)+" "+timeFormat.format(ts.getTime()));



        TextView textAccelerometer = (TextView) customView.findViewById(R.id.textAccelerometer);
        if (!(hiveList.get(position).isAccelerometer())){
            textAccelerometer.setText("Úľ je neprevrátený");
            //textAccelerometer.setText("OK");
        }else{
            textAccelerometer.setText("Úľ je prevrátený");
            textAccelerometer.setTextColor(Color.parseColor("#FF0000"));
        }

        TextView textBattery = (TextView) customView.findViewById(R.id.textBattery);
        if (hiveList.get(position).isCharging()) {
            textBattery.setText(String.valueOf(hiveList.get(position).getBattery()) + "%"+" (nabíja sa)");
        }else{
            textBattery.setText(String.valueOf(hiveList.get(position).getBattery()) + "%"+" (nenabíja sa)");
        }

        Log.i(TAG, "M: " + hiveList.get(position).getBattery());
        Log.i(TAG, "L: " + hiveList.get(position).getBatery_limit());
        if (hiveList.get(position).getBattery() <= hiveList.get(position).getBatery_limit()){
            textBattery.setTextColor(Color.parseColor("#FF0000"));
        }

        if (!hiveList.get(position).isCharging()) {
            ImageView imgC = customView.findViewById(R.id.charging);
            imgC.setImageDrawable(null);
            ImageView imgC2 = customView.findViewById(R.id.charging2);
            imgC2.setImageDrawable(null);
        }
        ImageView imgBattery = customView.findViewById(R.id.imageBattery);
        if (hiveList.get(position).getBattery()>79){
            return customView;
        }
        if (hiveList.get(position).getBattery()>50){
            imgBattery.setImageResource(R.drawable.battery_l3);
            return customView;
        }
        if (hiveList.get(position).getBattery()>25){
            imgBattery.setImageResource(R.drawable.battery_l2);
            return customView;
        }
        imgBattery.setImageResource(R.drawable.battery_l1);
        return customView;
    }
}