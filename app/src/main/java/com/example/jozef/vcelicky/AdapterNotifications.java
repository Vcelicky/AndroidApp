package com.example.jozef.vcelicky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MSI on 8. 3. 2018.
 */

public class AdapterNotifications extends ArrayAdapter<NotificationInfo> {

    ArrayList<NotificationInfo> notificationsList;

    public AdapterNotifications(Context context, ArrayList<NotificationInfo> notificationsList){
        //nezabudni mu povedat ze listView bude pouzivat toto xml pre jeden riadok "custom_row_devices"
        super(context, R.layout.notification_row , notificationsList);
        this.notificationsList = notificationsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.notification_row, parent, false);

        //vyber i-ty prvok z listu
        // Device i = items.get(position);
          TextView textHiveName = (TextView) customView.findViewById(R.id.hive_name);
          textHiveName.setText(notificationsList.get(position).getViewTitleText());
//
        TextView textOutTemperature = (TextView) customView.findViewById(R.id.textOutTemperature);
        textOutTemperature.setText(notificationsList.get(position).getViewText());
//
//        TextView textInTemperature =   (TextView) customView.findViewById(R.id.textInTemperature);
//        textInTemperature.setText( String.valueOf("T:"+notificationInfoList.get(position).getInsideTemperature())+"°C (vnútorná)");
//
//        TextView textOutHumidity = customView.findViewById(R.id.textOutHumidity);
//        textOutHumidity.setText(String.valueOf("H:"+notificationInfoList.get(position).getOutsideHumidity())+"% (vonkajšia)");
//
//        TextView textInHumidity = customView.findViewById(R.id.textInHumidity);
//        textInHumidity.setText(String.valueOf("H:"+notificationInfoList.get(position).getInsideHumidity())+"% (vnútorná)");
//
//        TextView textWeight = (TextView) customView.findViewById(R.id.textWeight);
//        textWeight.setText(String.valueOf("W:"+notificationInfoList.get(position).getWeight())+"kg");
//
//        TextView textAccelerometer = (TextView) customView.findViewById(R.id.textAccelerometer);
//        if ((notificationInfoList.get(position).isAccelerometer())){
//            textAccelerometer.setText("A: OK");
//        }else{
//            textAccelerometer.setText("A: Úľ je prevrátený");
//        }
//        TextView textBattery = (TextView) customView.findViewById(R.id.textBattery);
//        textBattery.setText(String.valueOf("B:"+notificationInfoList.get(position).getBattery())+"%");

        return customView;
    }
}