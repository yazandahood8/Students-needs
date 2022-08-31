package com.example.students.ui2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.DeplomaAverageActivity;
import com.example.students.R;

import java.util.ArrayList;

public class MainGradesOfferActivity extends AppCompatActivity {

    private CheckBox chBDeyesOffer,chBDenoOffer,chBPsyyesOffer,chBPsynoOffer;
    private EditText etDeplomaOffer,etPsyOffer;
    private Button btnSubmitOffer,btnCalAvgOffer;
    ArrayList arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_grades_offer);
        //connecting xml to code
        chBDeyesOffer=(CheckBox)findViewById(R.id.chBDeyesOffer) ;
        chBDenoOffer=(CheckBox)findViewById(R.id.chBDenoOffer) ;
        chBPsyyesOffer=(CheckBox)findViewById(R.id.chBPsyyesOffer) ;
        chBPsynoOffer=(CheckBox)findViewById(R.id.chBPsynoOffer) ;
        etDeplomaOffer=(EditText)findViewById(R.id.etDeplomaOffer) ;
        etPsyOffer=(EditText)findViewById(R.id.etPsyOffer) ;
        btnSubmitOffer=(Button)findViewById(R.id.btnSubmitOffer) ;
        btnCalAvgOffer=(Button)findViewById(R.id.btnCalAvgOffer) ;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //getting data from last activity
            arrayList = extras.getParcelableArrayList("data");
            //call function to check if CheckBox isChecked
            CheckBoxes();
            btnSubmitOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //call function to check input validity
                    dataHandler();

                }
            });
            btnCalAvgOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                     move the user from current activity to DeplomaAverageActivity
                     to calculate his high school diploma
                     */
                    Intent intent = new Intent(MainGradesOfferActivity.this, DeplomaAverageActivity.class);
                    startActivity(intent);

                }
            });
        }

    }
    double intDeploma=-1;
    int intPsy=-1;
    private void dataHandler()
    {
        //1.getting data
        String stDeploma=etDeplomaOffer.getText().toString();
        String stPsy=etPsyOffer.getText().toString();

        if (stDeploma.length()>0)
            //parse string to int
            intDeploma = Integer.parseInt(stDeploma);
        if (stPsy.length()!=0)
            //parse string to int
            intPsy = Integer.parseInt(stPsy);

        boolean flag=true;

        //2.checking
        if ((stDeploma.length()==0&&chBDeyesOffer.isChecked()==true)||(chBDeyesOffer.isChecked()==true&&(intDeploma<0||intDeploma>130))) {
            etDeplomaOffer.setError("Wrong Deploma");

            flag = false;
        }
        if (stPsy.length()==0&&chBPsyyesOffer.isChecked()==true||(chBPsyyesOffer.isChecked()==true&&(intPsy<0||intPsy>800))) {
            etPsyOffer.setError("Wrong psychometric");
            flag = false;
        }
        if(chBDeyesOffer.isChecked()==false&&chBDenoOffer.isChecked()==false){
            Toast.makeText(MainGradesOfferActivity.this,"please checkbox in high school diploma",Toast.LENGTH_LONG).show();
            flag = false;
        }
        if(chBPsyyesOffer.isChecked()==false&&chBPsynoOffer.isChecked()==false){
            Toast.makeText(MainGradesOfferActivity.this,"please checkbox in psychometric",Toast.LENGTH_LONG).show();
            flag = false;
        }
        if(flag==true) {
            if (stPsy.length()>0&&stDeploma.length()>0) {
                /*
                move the user from current activity to ResultStudyOfferSubjectsActivity
                to show the subject that suitable for him
                and send psychometric , high school diploma and arrayList
                 */
                Intent intent = new Intent(MainGradesOfferActivity.this, ResultStudyOfferSubjectsActivity.class);
                intent.putExtra("psycho", intPsy);
                intent.putExtra("deploma", intDeploma);
                intent.putExtra("list", arrayList);
                startActivity(intent);
            }
        }
    }

    // function to check if CheckBox isChecked
    public void CheckBoxes(){
        chBDeyesOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                    //change the Checking
                    chBDenoOffer.setChecked(false);
                    etDeplomaOffer.setEnabled(true);
                }
            }
        });
        chBDenoOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked)
                    //change the Checking
                    chBDeyesOffer.setChecked(false);
                etDeplomaOffer.setEnabled(false);
                etDeplomaOffer.setText("");
            }
        });
        chBPsyyesOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                     if (isChecked)
                                                         //change the Checking

                                                         chBPsynoOffer.setChecked(false);
                                                     etPsyOffer.setEnabled(true);
                                                 }
                                             }
        );
        chBPsynoOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked)
                    //change the Checking

                    chBPsyyesOffer.setChecked(false);
                etPsyOffer.setEnabled(false);
                etPsyOffer.setText("");
            }
        });
    }
}