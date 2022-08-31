package com.example.students.ui2.AskToKnow;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.Adapter.MyAdapterChat;
import com.example.students.R;
import com.example.students.data.Comment_Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class CommentsActivity extends AppCompatActivity {
    private ListView listChat;
    private TextView etChat;
    private Button btnSentChat;
    private MyAdapterChat myAdapterChat;
    private Comment_Chat commentChat;
    private String name,id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        //connecting xml to code
        listChat = findViewById(R.id.listChat1);
        etChat = findViewById(R.id.etChat1);
        btnSentChat = findViewById(R.id.btnSentChat1);

        showProgressDialogWithTitle();
        //make connecting chat layout to list view
        myAdapterChat = new MyAdapterChat(CommentsActivity.this, R.layout.chat);
        //get value from last activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //id of the post
            id = extras.getString("name");
            //call function to show all comments
            commentShow();

        }


        listChat.setAdapter(myAdapterChat);
        listChat.setBackgroundColor(Color.WHITE);
        listChat.smoothScrollToPosition(listChat.getChildCount());
        btnSentChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etChat.getText().length() > 0) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    //save the commentChat to database
                    Date d = new Date();
                    Comment_Chat c = new Comment_Chat();
                    c.setText(etChat.getText().toString());
                    c.setEmail(email);
                    c.setUser(name);
                    c.setId(reference.push().getKey());
                    c.setHour(d.getHours());
                    c.setMin(d.getMinutes());
                    c.setDay(d.getDate());
                    c.setMonth(d.getMonth() + 1);
                    c.setType("Comment");
                    reference.child("Post").child(id).child("comments").push().setValue(c);
                    etChat.setText("");

                }
            }
        });

    }
    //funtion to get comments from database and show it in activity
    public void commentShow() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Post").child(id).child("comments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myAdapterChat.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                //set values to class
                                commentChat = ds.getValue(Comment_Chat.class);
                                //add the class to adapter
                                myAdapterChat.add(commentChat);
                            }
                            //dismiss the dialog
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

        reference.child("users").child(email).child("myProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name of user
                name = dataSnapshot.child("p").child("fullname").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    ProgressDialog progressDialog;

    //function to show dialog in activity
    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(CommentsActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }
}