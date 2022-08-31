package com.example.students;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.data.Subject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainGradesActivity extends AppCompatActivity {
    private CheckBox chBDeyes,chBDeno,chBPsyyes,chBPsyno;
    private EditText etDeploma,etPsy,etMath,etPsychoKmote;
    private Button btnSubmit,btnCalAvg;
    private String type,Subject;
    Subject s=new Subject();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String email= FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_grades);

        //connecting xml to code
        chBDeyes=(CheckBox)findViewById(R.id.chBDeyes) ;
        chBDeno=(CheckBox)findViewById(R.id.chBDeno) ;
        chBPsyyes=(CheckBox)findViewById(R.id.chBPsyyes) ;
        chBPsyno=(CheckBox)findViewById(R.id.chBPsyno) ;
        etDeploma=(EditText)findViewById(R.id.etDeploma) ;
        etPsy=(EditText)findViewById(R.id.etPsy) ;
        btnSubmit=(Button)findViewById(R.id.btnSubmit) ;
        btnCalAvg=(Button)findViewById(R.id.btnCalAvg) ;
        etMath=(EditText)findViewById(R.id.etMath) ;
        etPsychoKmote=(EditText)findViewById(R.id.etPsychoKmote) ;

        //call function to check if CheckBox isChecked
        CheckBoxes();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function to check input validity
                dataHandler();

            }
        });
        btnCalAvg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                     move the user from current activity to DeplomaAverageActivity
                     to calculate his high school diploma
                     */
                Intent intent = new Intent(MainGradesActivity.this, DeplomaAverageActivity.class);
                startActivity(intent);

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            /*
            getting data from last activity
            name and type of subject
             */

            type = extras.getString("Type");
            Subject = extras.getString("Subject");
        }
    }
    double intDeploma=-1;
    int intPsy=-1;
    private void dataHandler()
    {
        //1.getting data
        String stDeploma=etDeploma.getText().toString();
        String stPsy=etPsy.getText().toString();
        String stMath=etMath.getText().toString();
        String stPsychoKmote=etPsychoKmote.getText().toString();

        if (stDeploma.length()!=0)
            intDeploma = Integer.parseInt(stDeploma);
        if (stPsy.length()!=0)
            intPsy = Integer.parseInt(stPsy);

        boolean flag=true;

        //2.checking
        if ((stDeploma.length()==0&&chBDeyes.isChecked()==true)||(chBDeyes.isChecked()==true&&(intDeploma<0||intDeploma>130))) {
            etDeploma.setError("Wrong Deploma");

            flag = false;
        }
        if (stPsy.length()==0&&chBPsyyes.isChecked()==true||(chBPsyyes.isChecked()==true&&(intPsy<0||intPsy>800))) {
            etPsy.setError("Wrong psychometric");
            flag = false;
        }
        if(chBDeyes.isChecked()==false&&chBDeno.isChecked()==false){
            Toast.makeText(MainGradesActivity.this,"please checkbox in deploma",Toast.LENGTH_LONG).show();
            flag = false;
        }
        if(chBPsyyes.isChecked()==false&&chBPsyno.isChecked()==false){
            Toast.makeText(MainGradesActivity.this,"please checkbox in psychometric",Toast.LENGTH_LONG).show();
            flag = false;
        }
        if (stMath.length()==0){
            etMath.setError("Please fill");
            flag = false;
        }
        if (stPsychoKmote.length()==0){
            etPsychoKmote.setError("Please fill");
            flag = false;
        }
        if(flag==true) {
            //check if user have psychometric and high school diploma grades
            if (stPsy.length()>0&&stDeploma.length()>0){
                //calculate sekhem
                double temp=intDeploma;
                temp=temp*7.26-116.8;
                double sekhem=(temp+intPsy)/2;
                //set information to class
                s.setName(Subject);
                s.setType(type);
                s.setSekem(sekhem);
                s.setDiploma(intDeploma);
                s.setPsycho(intPsy);
                s.setPsychokmote(Integer.parseInt(stPsychoKmote));
                s.setMath(Integer.parseInt(stMath));
                //call function to save data in database
                SaveData(s);
            }
            //if user don't have psychometric and high school diploma grades

            else{
                //call function to check what is missing  (psychometric , high school diploma grades)
                Check(s);

            }
        }
    }

    //function to save the data to database
    public  void SaveData(Subject s) {
        reference.child("users").child(email).child("myProfile").child("check").child("c").setValue(s, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    //move from current activity to ResultChooseActivity
                    Intent intent = new Intent(MainGradesActivity.this, ResultChooseActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainGradesActivity.this, "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    databaseError.toException().printStackTrace();
                }
            }
        });

    }

    //call function to check what is missing  (psychometric , high school diploma grades)
    public void Check(Subject s){

        String stDeploma=etDeploma.getText().toString();
        String stPsy=etPsy.getText().toString();
        s.setName(Subject);
        s.setType(type);
        s.setDiploma(intDeploma);
        s.setPsycho(intPsy);
        if (stPsy.length()==0)
        {
            //-1 this mean don't have grade
            s.setPsycho(-1);
        }
        if (stDeploma.length()==0)
            //-1 this mean don't have grade
            s.setDiploma(-1);

        //call function to save data after changing the values
        SaveData(s);

    }


    //call function to check if CheckBox isChecked
    public void CheckBoxes(){
        chBDeyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                    //change the Checking
                    chBDeno.setChecked(false);
                    etDeploma.setEnabled(true);
                }
            }
        });
        chBDeno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked)
                    //change the Checking
                    chBDeyes.setChecked(false);
                etDeploma.setEnabled(false);
                etDeploma.setText("");
            }
        });
        chBPsyyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked)
                    //change the Checking
                    chBPsyno.setChecked(false);
                etPsy.setEnabled(true);
            }
        }
        );
        chBPsyno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked)
                    //change the Checking
                    chBPsyyes.setChecked(false);
                etPsy.setEnabled(false);
                etPsy.setText("");
            }
        });
    }
}