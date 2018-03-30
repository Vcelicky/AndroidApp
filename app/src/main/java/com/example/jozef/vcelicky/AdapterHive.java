package com.example.jozef.vcelicky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MSI on 2. 11. 2017.
 */

public class AdapterHive  extends ArrayAdapter<HiveBaseInfo> {

    ArrayList<HiveBaseInfo> hiveList;

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

        TextView textInTemperature =   (TextView) customView.findViewById(R.id.textInTemperature);
        textInTemperature.setText( String.valueOf(hiveList.get(position).getInsideTemperature())+"°C (vnútorná)");

        TextView textOutHumidity = customView.findViewById(R.id.textOutHumidity);
        textOutHumidity.setText(String.valueOf(hiveList.get(position).getOutsideHumidity())+"% (vonkajšia)");

        TextView textInHumidity = customView.findViewById(R.id.textInHumidity);
        textInHumidity.setText(String.valueOf(hiveList.get(position).getInsideHumidity())+"% (vnútorná)");

        TextView textWeight = (TextView) customView.findViewById(R.id.textWeight);
        textWeight.setText(String.valueOf(hiveList.get(position).getWeight())+"kg");

        TextView textAccelerometer = (TextView) customView.findViewById(R.id.textAccelerometer);
        if (!(hiveList.get(position).isAccelerometer())){
            textAccelerometer.setText("OK");
        }else{
            textAccelerometer.setText("Úľ je prevrátený");
        }
        TextView textBattery = (TextView) customView.findViewById(R.id.textBattery);
        textBattery.setText(String.valueOf(hiveList.get(position).getBattery())+"%");

        ImageView imgBattery = (ImageView) customView.findViewById(R.id.imageBattery);
        if (hiveList.get(position).getBattery()>75){
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