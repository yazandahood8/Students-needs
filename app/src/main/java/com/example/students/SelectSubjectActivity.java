package com.example.students;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectSubjectActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> Academy = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    EditText etSearch;
    AlertDialog alertDialog1;
   private String name,stAcademy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_subject);
        //connecting xml to code
        listView = findViewById(R.id.listSubje);
        etSearch = findViewById(R.id.etSearchSubje);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //get  name academy from last activity
            stAcademy = extras.getString("ACADEMY");
            //array have types of subjects
            String Types[]={"Engineering","Natural sciences and exact sciences","Health and medical professions","Languages","history","Social Sciences","laws","Other circles","Business management and administration"
                    ,"Integration of circles","Education",};
            int i;
            for (i=0;i<Types.length;i++) {
                //call function to get all subjects that same type
                GetSubjects(Types[i]);
            }


        }
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Academy);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arrayAdapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");

                if (view instanceof TextView) {
                    String str=((TextView) view).getText().toString();
                    CharSequence[] values = {"Yes","No"};
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SelectSubjectActivity.this);
                    builder1.setTitle("Are you sure you subject is "+str+"?");
                    builder1.setCancelable(true);
                    builder1.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    //save subject to database
                                    reference.child("users").child(email).child("myProfile").child("subject").setValue(str);
                                    //move user from current activity to ChatActivity
                                    Intent i=new Intent(SelectSubjectActivity.this,ChatActivity.class);
                                    startActivity(i);
                                    break;
                                case 1:
                                    break;
                            }
                            alertDialog1.dismiss();
                        }
                    });
                    alertDialog1 = builder1.create();
                    alertDialog1.show();
                }
            }
        });
    }
    //function to get all subjects that same type
    public void GetSubjects(String type){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Subjects").child("BA").child(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    //call function to add the subject to list
                    AddSubjectToList(ds.getKey(),type);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //function to add the subject to list
    public void AddSubjectToList(String name,String type){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Subjects").child("BA").child(type).child(name).child("academics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for(DataSnapshot ds1:dataSnapshot1.getChildren()) {
                    if (ds1.getKey().equals(stAcademy)) {
                        //add subject to ArrayList
                        Academy.add(name);

                    }
                }
                //add adapter to listView
                listView.setAdapter(arrayAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}