package com.example.students.BroadcastRec;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.widget.Toast;

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

public class ChatAlarm extends BroadcastReceiver

{
    //to save last chat
    public static final String MyPREFERENCES = "MyPrefsChat" ;
    public static final String Email = "emailKey";
    public static final String Name = "nameKey";
    public static final String Text = "textKey";
    SharedPreferences sharedpreferences;


    String Sound,Group;
    NotificationManager mNotificationManager;
    Context context;

    //id of notification
    private static final int MY_NOTIFICATION_ID=1;
    NotificationManager notificationManager;
    Notification myNotification;

    @Override
    //This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    public void onReceive(Context context, Intent intent)
    {
        this.context=context;
            update1();
            setAlarm(context);
    }

    //function to set alarm that
    public void setAlarm(Context context)
    {

        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, ChatAlarm.class);

        PendingIntent pi = PendingIntent.getBroadcast(context,0, i, 0);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);

       Date d=new Date();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+1000*5, 1, pi);
    }

    public void cancelAlarm(Context context)
    {
        Toast.makeText(context,"Alam cancled",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, ChatAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void update1(){
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        FirebaseApp.initializeApp(context);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String lemod = dataSnapshot.child("lemod").getValue(String.class);
                    String subject = dataSnapshot.child("subject").getValue(String.class);
                    if (subject==null)
                        cancelAlarm(context);
                    else
                        setAlarm(context);
                     Sound = dataSnapshot.child("Settings").child("Chat").getValue(String.class);
                    Group = dataSnapshot.child("Settings").child("Group").getValue(String.class);
                    reference.child("Chats").child(lemod).child(subject).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (Sound==null){
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Settings").child("Chat").setValue("Sound");
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Settings").child("Group").setValue("On");
                            }
                            if( Group!=null&&!Sound.equals("Mute")&&Group.equals("On")&&FirebaseAuth.getInstance().getCurrentUser() != null)
                            for (DataSnapshot ds:dataSnapshot.getChildren()) {
                                String e = ds.child("email").getValue(String.class);
                                String u = ds.child("user").getValue(String.class);
                                String t = ds.child("text").getValue(String.class);
                                if(!e.equals(email))
                                    if (sharedpreferences!=null&&sharedpreferences.getString(Email, "").equals(e)&&
                                            sharedpreferences.getString(Name, "").equals(u)&&
                                            sharedpreferences.getString(Text, "").equals(t)) {
                                    }
                                    else{
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString(Email, e);
                                        editor.putString(Name, u);
                                        editor.putString(Text, t);
                                        editor.commit();
                                        if (subject!=null) {
                                            displayNotification(t, u);
                                        }
                                    }
                            }
                            setAlarm(context);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }catch (Exception ex){}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //function to show Notification
    public void displayNotification(String text,String user) {
        Intent intent = new Intent(context, ChatActivity.class);
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_001");
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(text);
        bigText.setBigContentTitle(user);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(user);
        mBuilder.setContentText(text);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setContentIntent(pendIntent).setAutoCancel(true);
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