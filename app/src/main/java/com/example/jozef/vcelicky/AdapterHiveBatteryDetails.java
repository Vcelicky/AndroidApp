package com.example.jozef.vcelicky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AdapterHiveBatteryDetails extends ArrayAdapter<HiveBaseInfo> {

    ArrayList<HiveBaseInfo> hiveList;

    public AdapterHiveBatteryDetails(Context context, ArrayList<HiveBaseInfo> hiveList){
        //nezabudni mu povedat ze listView bude pouzivat toto xml pre jeden riadok "custom_row_devices"
        super(context, R.layout.hive_row , hiveList);
        this.hiveList = hiveList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.hive_row_battery, parent, false);

        //vyber i-ty prvok z listu
        // Device i = items.get(position);
        TextView textHiveName = (TextView) customView.findViewById(R.id.hive_name);
//        textHiveName.setText(hiveList.get(position).getTimeStamp().toString());
        Calendar ts = hiveList.get(position).getTimeStamp();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        textHiveName.setText(ts.get(Calendar.DAY_OF_MONTH)+"."+ts.get(Calendar.MONTH)+"."+ts.get(Calendar.YEAR)+" "+timeFormat.format(ts.getTime()));

        TextView textBattery = customView.findViewById(R.id.textBattery);
        textBattery.setText(String.valueOf("B:"+hiveList.get(position).getBattery())+"%");

        return customView;
    }
}