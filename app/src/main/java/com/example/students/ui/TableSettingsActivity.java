package com.example.students.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.R;
import com.example.students.StudentsMain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class TableSettingsActivity extends AppCompatActivity {
    private EditText minBeginhours,minBeginmins;
    private EditText CountLec;
    private EditText etTime;
    private EditText Breaksnum;
    private TableLayout tblBreaks;
    private Button btnSubmitTable,btnSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_settings);

        minBeginhours=(EditText)findViewById(R.id.minBeginhours);
        minBeginmins=(EditText)findViewById(R.id.minBeginmins);

        CountLec=(EditText)findViewById(R.id.CountLec);
        etTime=(EditText)findViewById(R.id.etTime);
        Breaksnum=(EditText)findViewById(R.id.Breaksnum);
        tblBreaks=(TableLayout)findViewById(R.id.tblBreaks);
        btnSubmitTable=(Button)findViewById(R.id.btnSubmitTable);
        btnSub=(Button)findViewById(R.id.btnSub);


        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //table to add count of breaks in study
                tblBreaks.removeAllViews();
                //check if user add number of breaks
                if (Breaksnum.getText().length()>0&&Integer.parseInt(Breaksnum.getText().toString())>0) {
                    //parse the string to int
                    int x=Integer.parseInt(Breaksnum.getText().toString());
                    for(int i=0;i<x;i++) {
                        //make row that have hours and minuets and time of break
                        TableRow tableRow = new TableRow(TableSettingsActivity.this);
                        EditText TimeHour = new EditText(TableSettingsActivity.this);
                        TimeHour.setHint("Hour");
                        EditText TimeMin = new EditText(TableSettingsActivity.this);
                        TimeMin.setHint("Min");

                        EditText Time = new EditText(TableSettingsActivity.this);
                        Time.setHint("Time of Break");
                        tableRow.addView(TimeHour);
                        tableRow.addView(TimeMin);
                        tableRow.addView(Time);
                        //add row to table of breaks
                        tblBreaks.addView(tableRow);

                    }
                }

            }
        });
        btnSubmitTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                boolean flag=false;
                //array to save the times of breaks in String ("08:30","09:30")
                String str[]=new String[tblBreaks.getChildCount()];
                //array to save the times of breaks in int ("08:30","09:30")

                int Times[]=new int[tblBreaks.getChildCount()];

                for (int i = 0; i < tblBreaks.getChildCount(); i++) {
                    View child = tblBreaks.getChildAt(i);
                    if (child instanceof TableRow) {
                        TableRow row = (TableRow) child;
                        //get the breaks and save it in arrays
                        String hour=(((EditText) row.getChildAt(0)).getText().toString());
                        String min=(((EditText) row.getChildAt(1)).getText().toString());
                        String TimeBreak=(((EditText) row.getChildAt(2)).getText().toString());
                        String ss=hour+" "+min;
                        str[i]=ss;
                        Times[i]=Integer.parseInt(TimeBreak);
                    }
                }

                if (flag==false){
                    //get time of start first lesson
                    int iminBegin=Integer.parseInt(minBeginhours.getText().toString());
                    int iminBeginmin=Integer.parseInt(minBeginmins.getText().toString());

                    //get time of lesson
                    int time=Integer.parseInt(etTime.getText().toString());
                    //array to save the times(dates) of lessons
                    Date arr[]=new Date[Integer.parseInt(CountLec.getText().toString())];
                    Date d=new Date();
                    d.setMinutes(iminBeginmin);
                    d.setHours(iminBegin);
                    arr[0]=d;
                    if (arr.length>1)
                         for (int i=1;i<arr.length;i++){
                           Date d1=new Date();
                             d1.setMinutes(arr[i-1].getMinutes());
                             d1.setHours(arr[i-1].getHours());

                           d1.setMinutes(d1.getMinutes()+time+10);
                           arr[i]=d1;
                    }
                    for (int i=0;i<arr.length;i++){
                        String s=arr[i].getHours()+" "+(arr[i].getMinutes()-10);
                        for (int j=0;j<str.length;j++)
                        if (s.equals(str[j])){
                            for(int z=i;z<arr.length;z++){
                                arr[z].setMinutes(arr[z].getMinutes()+Times[j]);
                            }
                        }

                    }
                    //call function to save the table in database
                    SaveTable(arr);


                }


            }
        });
    }

    //function to save values of table in database
    public  void SaveTable(Date []arr){
        //array have days of week
        String weak[]={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
        //make the times to String
        for (int i=0;i<arr.length;i++){
            for (int j=0;j<7;j++) {
                String s;
                if (arr[i].getHours()<10){
                    if (arr[i].getMinutes()>9)
                         s="0"+arr[i].getHours() + ":" + arr[i].getMinutes();
                    else{
                        s="0"+arr[i].getHours() + ":0" + arr[i].getMinutes();

                    }
                }
                else{
                    if (arr[i].getMinutes()>9){
                        s=arr[i].getHours() + ":" + arr[i].getMinutes();
                    }
                    else{
                        s=arr[i].getHours() + ":0" + arr[i].getMinutes();

                    }
                }
                //save the value to database
                reference.child("users").child(email).child("myProfile").child("MyTable").child(s)
                        .child(weak[j]).setValue(" ");
            }
        }
        //move the user from current activity to main Activity
        Intent i=new Intent(TableSettingsActivity.this, StudentsMain.class);
        startActivity(i);

    }
}