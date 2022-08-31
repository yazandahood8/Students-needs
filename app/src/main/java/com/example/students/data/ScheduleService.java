package com.example.students.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.students.BroadcastRec.scheduleAlarm;

public class ScheduleService extends Service {
    scheduleAlarm scheduleAlarm = new scheduleAlarm();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this,"start",Toast.LENGTH_LONG).show();
        scheduleAlarm.setAlarmschedule(this);
        return START_STICKY;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        startService(restartServiceIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    public void onCreate()
    {
        super.onCreate();
    }



    @Override
    public void onStart(Intent intent, int startId)
    {
        scheduleAlarm.setAlarmschedule(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}