package com.example.students;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.Adapter.MyAdapterChat;
import com.example.students.data.ChatService;
import com.example.students.data.Comment_Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private ListView listChat;
    private TextView etChat,tvGroupName;
    private Button btnSentChat;
    private MyAdapterChat myAdapterChat;
    private Comment_Chat commentChat;
    private String name,  lemod ,subject;
    private ImageButton imgSound;
    private String Sound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //connecting xml to code
        listChat = findViewById(R.id.listChat);
        etChat = findViewById(R.id.etChat);
        tvGroupName = findViewById(R.id.tvGroupName);
        //showProgressDialogWithTitle();
        btnSentChat = findViewById(R.id.btnSentChat);
        imgSound=findViewById(R.id.imgSound);

        myAdapterChat=new MyAdapterChat(ChatActivity.this,R.layout.chat);
        //call function to show list of chats
        ChatShow();




        String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email).child("myProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                     Sound = dataSnapshot.child("Settings").child("Chat").getValue(String.class);
                     //check if chat is Mute
                     if (Sound!=null&&Sound.equals("Mute")){
                         //change icon of imgSound  to Mute icon
                         imgSound.setImageResource(R.drawable.ic_baseline_volume_of_24_foreground);
                     }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
        });
        listChat.setAdapter(myAdapterChat);
        //make listChat WHITE Background Color
        listChat.setBackgroundColor(Color.WHITE);
        //set Scroll Position in list to last chat
        listChat.smoothScrollToPosition(listChat.getChildCount());

        imgSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                //change sound notification
                if (Sound.equals("Mute")) {
                    //change from mute to sound
                    Sound="Sound";
                    //change icon to sound icon
                    imgSound.setImageResource(R.drawable.ic_baseline_volume_up_24_foreground);
                    //save to database
                    reference.child("users").child(email).child("myProfile").child("Settings").child("Chat").setValue("Sound");
                }
                else {
                    //change from sound to mute
                    Sound="Mute";
                    //change icon to mute icon
                    imgSound.setImageResource(R.drawable.ic_baseline_volume_of_24_foreground);
                    //save to database
                    reference.child("users").child(email).child("myProfile").child("Settings").child("Chat").setValue("Mute");

                }


            }
        });

        btnSentChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if user write chat
                if(etChat.getText().length()>0) {
                    //add the chat in class with current time
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Date d=new Date();
                    Comment_Chat c = new Comment_Chat();
                    c.setText(etChat.getText().toString());
                    c.setEmail(email);
                    c.setUser(name);
                    c.setId(reference.push().getKey());
                    c.setHour(d.getHours());
                    c.setMin(d.getMinutes());
                    c.setDay(d.getDate());
                    c.setMonth(d.getMonth()+1);
                    c.setType("Chat");
                    //save the class to database
                    reference.child("Chats").child(lemod).child(subject).child(c.getId()).setValue(c);
                    etChat.setText("");

                }



            }
        });

    }
    //menu for leave the group
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat, menu);
        return true;
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leaveGroup:

                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                /*
                remove subject from database of user
                this mean the user leave the group
                 */
                reference.child("users").child(email).child("myProfile").child("subject").removeValue();

                //delete in database that mean the user is leave the group
                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Settings").child("Group").removeValue();
                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Settings").child("Chat").removeValue();


                //move user from current activity to main activity
                startActivity(new Intent(this, StudentsMain.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }    }

    //function to show list of chats
    public void ChatShow(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting academic and subject that learning it the user
                lemod = dataSnapshot.child("lemod").getValue(String.class);
                subject = dataSnapshot.child("subject").getValue(String.class);
                //check if user not in group
                if (subject == null) {
                    /*
                    move user from current activity to SelectSubjectActivity
                    to select subject
                     */
                    Intent i = new Intent(ChatActivity.this, SelectSubjectActivity.class);
                    //send name of ACADEMY to SelectSubjectActivity
                    i.putExtra("ACADEMY",lemod);
                    startActivity(i);

                }
                //if user  in group
                else {
                    Intent s = new Intent(ChatActivity.this, ChatService.class);
                    startService(s);
                    //name of group (name academy...name subject)
                    tvGroupName.setText(lemod + "..." + subject);
                    //get name user from database
                    name = dataSnapshot.child("p").child("fullname").getValue(String.class);
                    //getting chat from database
                    reference.child("Chats").child(lemod).child(subject).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myAdapterChat.clear();
                            int x = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                //set data to class
                                commentChat = ds.getValue(Comment_Chat.class);
                                //add class to adapter
                                myAdapterChat.add(commentChat);
                                x++;
                            }
                            //set Scroll Position to last chat
                            listChat.smoothScrollToPosition(x);
                            //dismiss to dialog
                            //progressDialog.dismiss();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    @Override
    public void onBackPressed() {
        //move the user from current activity to main activity
        Intent intent = new Intent(ChatActivity.this, StudentsMain.class);
        startActivity(intent);
        finish();

        return;
    }
    ProgressDialog progressDialog;
    //function to show dialog in activity
    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }
}