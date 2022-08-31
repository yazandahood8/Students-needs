package com.example.students.ui2.whatlearn;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.students.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ComparableActivity extends AppCompatActivity {
    private TableLayout tblCompare;
    private String type,Subject;
    String arr[]={"Amir test","deploma","eng4","eng5","math4","math5","psycho","pyzeks5","sekhem","yael"};
    String Academics[]=new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparable);
        //connecting xml to code
        tblCompare=findViewById(R.id.tblCompare);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //getting data from last activity
            type = extras.getString("Type");
            Subject = extras.getString("Subject");
            //call function to show all Academics
            Academics();


        }
    }


    public void Academics(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Subjects").child("BA").child(type).child(Subject).child("academics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //make row
                TableRow tableRow=new TableRow(ComparableActivity.this);
                TextView tv=new TextView(ComparableActivity.this);
                tv.setText(" ");
                tableRow.addView(tv);
                //make border to table
                tableRow.setBackground(ContextCompat.getDrawable(ComparableActivity.this, R.drawable.border));
                tv.setPadding(20,20,20,20);
                for (int i=0;i<arr.length;i++){
                    //add the texts in array to row
                    TextView textView=new TextView(ComparableActivity.this);
                    textView.setText(arr[i]);
                    tableRow.addView(textView);
                    //make text bold
                    textView.setTypeface(null, Typeface.BOLD_ITALIC);
                    //make padding
                    textView.setPadding(20,0,20,0);

                }
                //add the row to table
                tblCompare.addView(tableRow);
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //call function to update the table
                    UpdateTable(ds.getKey(),tblCompare);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    //function to update the table and getting data from firebase
    public void UpdateTable(String str,TableLayout tblCompare){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //make row
        TableRow row = new TableRow(ComparableActivity.this);
        //set border to row
        row.setBackground(ContextCompat.getDrawable(ComparableActivity.this, R.drawable.border));
        TextView textView=new TextView(ComparableActivity.this);
        textView.setText(str);
        textView.setMaxWidth(600);
        //make text bold
        textView.setTypeface(null, Typeface.BOLD_ITALIC);
        //make padding

        textView.setPadding(20,20,20,20);
        //add textView to row
        row.addView(textView);

        for (int i=0;i<arr.length;i++){
            int finalI = i;
            //getting admissions from database
                reference.child("Subjects").child("BA").child(type).child(Subject).child("academics").child(str).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String s = dataSnapshot.child(arr[finalI]).getValue(String.class);
                        if (s != null) {
                            //make textview
                            TextView tv=new TextView(ComparableActivity.this);
                            //add textview to row
                            row.addView(tv);
                            //set value to text view
                            tv.setText(s);
                            //set padding to textview
                            tv.setPadding(20,20,20,20);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
        }
        //add row to table
        tblCompare.addView(row);

    }
}