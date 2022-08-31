package com.example.students.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.students.R;
import com.example.students.data.Comment_Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAdapterChat extends ArrayAdapter<Comment_Chat> {
    private static  Boolean f=false;

    public MyAdapterChat(Context context, int resource) {
        super(context, resource);
        }
@Override
public View getView(int position, View convertView, final ViewGroup parent) {
    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat, parent, false);

    //connecting xml to code
    TextView tvTextChat = (TextView) convertView.findViewById(R.id.tvTextChat);
    TextView tvChatName = (TextView) convertView.findViewById(R.id.tvChatName);
    TextView tvTextTime = (TextView) convertView.findViewById(R.id.tvTextTime);
    TextView tvDay = (TextView) convertView.findViewById(R.id.tvDay);

    //getting email user from firebase
    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    //class to get values
    final Comment_Chat commentChat = getItem(position);

    //check if this chat
    if (commentChat.getType()!=null&&!commentChat.getType().equals("Comment")){
        //set time sent chat
        tvTextTime.setText(commentChat.getHour() + ":" + commentChat.getMin() + "\n" + commentChat.getDay() + "." + commentChat.getMonth());

        //check if current user send chat
    if (commentChat.getEmail().equals(email)) {

        //check if current  chat is deleted
        if (commentChat.getId().equals("this chat is deleted")) {

            //set text chat is deleted
            tvTextChat.setText(commentChat.getId());

            //make background red
            tvTextChat.setBackgroundColor(Color.RED);

            //set max width for chat text
            tvTextChat.setMaxWidth(300);

            //move the text to right of screen
            tvTextChat.setX(550);

            //remove time send chat
            tvTextTime.setX(9999);


        }
        //if chat not deleted
        else {
            //move the time send to right of screen
            tvTextTime.setX(750);

            //set max width for chat text
            tvTextChat.setMaxWidth(200);

            //move the text to right of screen
            tvTextChat.setX(550);

            //remove name of sender
            tvChatName.setText("");

            //set text chat
            tvTextChat.setText(commentChat.getText());

        }
    }
    //if another user send chat
    else {
        //if chat is deleted
        if (commentChat.getId().equals("this chat is deleted")) {
            tvTextChat.setText(commentChat.getId());
            tvTextChat.setBackgroundColor(Color.RED);
            tvTextChat.setMaxWidth(300);
            tvTextTime.setX(9999);
        }
        //if not deleted
        else {
            tvTextChat.setText(commentChat.getText());
            tvChatName.setText(commentChat.getUser());
        }
    }
    tvTextChat.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //check if chat for current email and chat not deleted in past
            if (commentChat.getEmail().equals(email) && !commentChat.getId().equals("this chat is deleted")) {

                //make dialog to submit the delete
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Did you want to delete this  chat?");
                builder1.setCancelable(false);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //call function to delete the chat
                                delete(commentChat);
                            }
                        });
                builder1.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        }
    });
}
    //if this comment
    else{
        tvTextChat.setText(commentChat.getText());
        tvChatName.setText(commentChat.getUser());
        tvDay.setText("");
    }
    return convertView;
    }

    //function to delete chat
    public static void delete(Comment_Chat c){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                replace id of current chat to " this chat is deleted"
                this mean the chat deleted all users can't show text of chat
                 */
               String lemod = dataSnapshot.child("lemod").getValue(String.class);
                String subject = dataSnapshot.child("subject").getValue(String.class);
                reference.child("Chats").child(lemod).child(subject).child(c.getId()).child("id").setValue("this chat is deleted");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

}
