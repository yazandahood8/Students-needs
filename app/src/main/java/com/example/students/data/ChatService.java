package com.example.students.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.students.BroadcastRec.ChatAlarm;

public class ChatService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        chatAlarm.setAlarm(this);
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

    ChatAlarm chatAlarm = new ChatAlarm();
    public void onCreate()
    {
        super.onCreate();
    }



    @Override
    public void onStart(Intent intent, int startId)
    {
        chatAlarm.setAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    }