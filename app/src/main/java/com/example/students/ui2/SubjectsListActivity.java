package com.example.students.ui2;

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

import com.example.students.MainGradesActivity;
import com.example.students.R;
import com.example.students.ui2.whatlearn.ComparableActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectsListActivity extends AppCompatActivity {
    ListView listSubects;
    ArrayList<String> Subjects = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    EditText etSearchSubjects;
    AlertDialog alertDialog1;
    private  String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_list);
        //connecting xml to code
        listSubects = findViewById(R.id.listSubects);
        etSearchSubjects = findViewById(R.id.etSearchSubjects);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            /*
            getting data(type) from last activity
            type of subject
             */
             type = extras.getString("Type");
             //call function to show the subject that same type
            funcSubjects(type);

        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Subjects);

        etSearchSubjects.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //change the arrayAdapter
                arrayAdapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listSubects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    String str=((TextView) view).getText().toString();
                    //make dialog that have 3 options to select
                    CharSequence[] values = {"Check Acceptable","Comparable","Cancel"};
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SubjectsListActivity.this);
                    builder1.setTitle("Are you sure you what to check  "+str+"?");
                    builder1.setCancelable(true);
                    builder1.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                //if choose : Check Acceptable
                                case 0:
                                    /*
                                    move the user from current subject to MainGradesActivity
                                    to put his grades
                                     */
                                    Intent myIntent = new Intent(SubjectsListActivity.this, MainGradesActivity.class);
                                    //send name and type subject to MainGradesActivity
                                    myIntent.putExtra("Subject",str);
                                    myIntent.putExtra("Type",type);
                                    startActivity(myIntent);
                                    break;
                                //if choose : Comparable

                                case 1:
                                     /*
                                    move the user from current subject to ComparableActivity
                                    to comparable admissions of academics in current subject
                                     */
                                    Intent i = new Intent(SubjectsListActivity.this, ComparableActivity.class);
                                    //send name and type subject to MainGradesActivity
                                    i.putExtra("Subject",str);
                                    i.putExtra("Type",type);
                                    startActivity(i);
                                    break;
                                //if choose : Cancel
                                case 2:
                                    //do nothing
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

    //function to show the subject that same type
    public void funcSubjects(String type){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Subjects").child("BA").child(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting subjects
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if (!ds.getKey().equals("img"))
                        //add subject to Array list
                    Subjects.add(ds.getKey());


                }
                //add adapter to listview
                listSubects.setAdapter(arrayAdapter);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


    }
}