package com.example.students.BroadcastRec;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.example.students.ChatActivity;
import com.example.students.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class scheduleAlarm  extends BroadcastReceiver

{
    String weak[]={"sun","mon","tue","wed","thu","fri","sat"};

    private static int id=0;
    NotificationManager mNotificationManager;
    Context context;
    @Override
    //This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    public void onReceive(Context context, Intent intent)
    {

        this.context=context;
       // Toast.makeText(context, "scheduleAlarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        RemmberFunc();
        setAlarmschedule(context);
    }


    public void setAlarmschedule(Context context)
    {

        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, scheduleAlarm.class);
        final int id = (int) System.currentTimeMillis();
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, i, 0);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);

        Date d=new Date();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+1000*60, 1, pi);
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, ChatAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    public void RemmberFunc(){
            FirebaseApp.initializeApp(context);
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("users").child(email.replace(".","_")).child("myProfile").child("MyTable").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Date d=new Date();
                    String s;
                    if (d.getHours()<10){
                        if (d.getMinutes()>9)
                            s="0"+d.getHours() + ":" + (d.getMinutes()+5);
                        else{
                            s="0"+d.getHours() + ":0" + (d.getMinutes()+5);

                        }
                    }
                    else{
                        if (d.getMinutes()>9){
                            s=d.getHours() + ":" + (d.getMinutes()+5);
                        }
                        else{
                            s=d.getHours() + ":0" + (d.getMinutes()+5);

                        }
                    }
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        setAlarmschedule(context);
                        if (ds.getKey().equals(s)){
                            String ch=ds.child(weak[d.getDay()]).getValue(String.class);
                            if (ch!=null&&ch.length()>0){
                                displayNotification(ch);
                            }

                        }
                    }
                    setAlarmschedule(context);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }
    public void displayNotification(String text) {
        Intent intent = new Intent(context, ChatActivity.class);
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_001");
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(text);
        bigText.setBigContentTitle("Reminder for Lesson");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Reminder for Lesson");
        mBuilder.setContentText(text);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);


        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Remember Lesson",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

}