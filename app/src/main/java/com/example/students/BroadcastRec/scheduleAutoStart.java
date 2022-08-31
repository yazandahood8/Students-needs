package com.example.students.BroadcastRec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class scheduleAutoStart extends BroadcastReceiver
{
    scheduleAlarm scheduleAlarm = new scheduleAlarm();
    @Override
    //This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            scheduleAlarm.setAlarmschedule(context);
        }
    }
}
