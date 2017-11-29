package com.example.jozef.vcelicky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class AdapterHiveDetails extends ArrayAdapter<HiveBaseInfo> {

    ArrayList<HiveBaseInfo> hiveList;

    public AdapterHiveDetails(Context context, ArrayList<HiveBaseInfo> hiveList){
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
//        textHiveName.setText(hiveList.get(position).getTimeStamp().toString());
        Calendar ts = hiveList.get(position).getTimeStamp();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        textHiveName.setText(ts.get(Calendar.DAY_OF_MONTH)+"."+ts.get(Calendar.MONTH)+"."+ts.get(Calendar.YEAR)+" "+timeFormat.format(ts.getTime()));

        TextView textOutTemperature = (TextView) customView.findViewById(R.id.textOutTemperature);
        textOutTemperature.setText(String.valueOf("T:"+hiveList.get(position).getOutsideTemperature())+"°C (vonkajšia)"); // ("Vonkajšia teplota"+"hiveList.get(position).getHiveName()"+"°C");

        TextView textInTemperature =   (TextView) customView.findViewById(R.id.textInTemperature);
        textInTemperature.setText( String.valueOf("T:"+hiveList.get(position).getInsideTemperature())+"°C (vnútorná)");

        TextView textHumidity = (TextView) customView.findViewById(R.id.textInHumidity);
        textHumidity.setText(String.valueOf("H:"+hiveList.get(position).getHumidity())+"%");

        TextView textWeight = (TextView) customView.findViewById(R.id.textWeight);
        textWeight.setText(String.valueOf("W:"+hiveList.get(position).getWeight())+"kg");

        return customView;
    }
}