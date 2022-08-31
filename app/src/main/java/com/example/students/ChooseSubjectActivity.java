package com.example.students;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.ui2.SubjectsListActivity;

public class ChooseSubjectActivity extends AppCompatActivity    {
    private ImageButton imageButton,imageButton2,imageButton3;
    private ImageButton imageButton4,imageButton5,imageButton6,imageButton7;
    private ImageButton imageButton8,imageButton9,imageButton10,imageButton11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_subject);
        //connecting xml to code
        imageButton=(ImageButton)findViewById(R.id.imageButton);
        imageButton2=(ImageButton)findViewById(R.id.imageButton2);
        imageButton3=(ImageButton)findViewById(R.id.imageButton3);
        imageButton4=(ImageButton)findViewById(R.id.imageButton4);
        imageButton5=(ImageButton)findViewById(R.id.imageButton5);
        imageButton6=(ImageButton)findViewById(R.id.imageButton6);
        imageButton7=(ImageButton)findViewById(R.id.imageButton7);
        imageButton8=(ImageButton)findViewById(R.id.imageButton8);
        imageButton9=(ImageButton)findViewById(R.id.imageButton9);
        imageButton10=(ImageButton)findViewById(R.id.imageButton10);
        imageButton11=(ImageButton)findViewById(R.id.imageButton11);
        //array of ImageButton
         ImageButton img[]={imageButton,imageButton2,imageButton3,
                imageButton4,imageButton5,imageButton6,imageButton7,
                imageButton8,imageButton9,imageButton10,imageButton11};
         //array of subject's types
        String Types[]={"Engineering","Natural sciences and exact sciences","Health and medical professions","Languages","history","Social Sciences","laws","Other circles","Business management and administration"
                ,"Integration of circles","Education",};
        //array of images from drawable
         int arr[]={R.drawable.engineering,R.drawable.nature,R.drawable.healthandmedicalprofessions,
         R.drawable.languages,R.drawable.history,R.drawable.socialsciences,R.drawable.laws
         ,R.drawable.othercircles,R.drawable.businessmanagementandadministration,R.drawable.integrationofcircles
         ,R.drawable.education};
        //show the images in activity
        for (int i=0;i<img.length;i++){
            img[i].setImageResource(arr[i]);
            int x=i;
            img[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    /*
                    move the user from current activity to SubjectsListActivity
                    to show the subjects that same type
                     */
                    Intent myIntent = new Intent(ChooseSubjectActivity.this, SubjectsListActivity.class);
                    //send type of subject to SubjectsListActivity
                    myIntent.putExtra("Type",Types[x]);

                    startActivity(myIntent);

                }
            });
        }
    }


}