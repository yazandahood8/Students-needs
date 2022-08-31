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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectAcademyActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> Academy = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    EditText etSearch;
    AlertDialog alertDialog1;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_academy);
        //connecting xml to code
        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //getting data from last activity
             name = extras.getString("HOME");
        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //make simple adapter
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Academy);
        //get all academics from database
        reference.child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot ds:dataSnapshot.getChildren()){
                   //add academy to ArrayList
                   Academy.add(ds.getKey().toString());
               }
               //add adapter to listView
                listView.setAdapter(arrayAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

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
        //check selected academy
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                //getting email from database
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
                //check if view is TextView
                if (view instanceof TextView) {
                    String str=((TextView) view).getText().toString();
                    CharSequence[] values = {"Yes","No"};
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SelectAcademyActivity.this);
                    builder1.setTitle("Are you sure you are study in "+str+"?");
                    builder1.setCancelable(true);
                    builder1.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    //save academy to database
                                    reference.child("users").child(email).child("myProfile").child("lemod").setValue(str, new DatabaseReference.CompletionListener() {
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                Toast.makeText(getBaseContext(), "save ok", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getBaseContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                databaseError.toException().printStackTrace();
                                            }
                                        }
                                    });
                                    reference.child("users").child(email).child("myProfile").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String sub = dataSnapshot.child("subject").getValue(String.class);
                                            if (sub!=null)
                                                //delete the subject from database
                                                reference.child("users").child(email).child("myProfile").child("subject").removeValue();
                                            //get type of user
                                            String type = dataSnapshot.child("p").child("type").getValue(String.class);
                                            if (type.equals("Student")){
                                                //move from current activity to mainActivity
                                                Intent intent = new Intent(SelectAcademyActivity.this, Login2Activity.class);
                                                intent.putExtra("GUEST","");
                                                startActivity(intent);
                                                finish();
                                            }
                                            else if(name.equals("HOME")) {
                                                //move from current activity to mainActivity
                                                Intent intent = new Intent(SelectAcademyActivity.this, NotStudentActivity.class);
                                                intent.putExtra("KEY", str);
                                                intent.putExtra("GUEST","");

                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                //move from current activity to mainActivity
                                                Intent intent = new Intent(SelectAcademyActivity.this, UniversityMapsActivity.class);
                                                intent.putExtra("KEY", str);
                                                intent.putExtra("GUEST","");

                                                startActivity(intent);
                                                finish();
                                            }

                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) { }
                                    });

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
}