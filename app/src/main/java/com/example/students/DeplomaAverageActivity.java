package com.example.students;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.data.Courses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeplomaAverageActivity extends AppCompatActivity {
    private TableLayout tblAverage;
    private Button btnAddCourseDeploma;
    private String []arr={"English","Mathematics","Hebrew expression","History","Arabic","citizenship"};
    private Button add;
    private TextView tvAverage;
    Courses courses = new Courses();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploma_average);
        //connecting xml to code
        tblAverage=(TableLayout)findViewById(R.id.tblAverage);
        btnAddCourseDeploma=(Button)findViewById(R.id.btnAddCourseDeploma);
        tvAverage=(TextView) findViewById(R.id.tvAverage);
        tvAverage.setText("Average: 0");

        MakeTable();
        btnAddCourseDeploma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddCourseDeploma.setVisibility(View.GONE);

                //create a new row
                TableRow row = new TableRow(DeplomaAverageActivity.this);
                //add Layouts to your new row
                EditText Subject = new EditText(DeplomaAverageActivity.this);
                EditText units = new EditText(DeplomaAverageActivity.this);
                EditText grade = new EditText(DeplomaAverageActivity.this);
                //make the input only numbers

                units.setInputType(InputType.TYPE_CLASS_NUMBER);
                grade.setInputType(InputType.TYPE_CLASS_NUMBER);
                ImageButton remove= new ImageButton(DeplomaAverageActivity.this);
                //make ImageButton with icon

                String uri = "@drawable/ic_delete_foreground";
                // where myresource (without the extension) is the file
                int imageResource = getResources().getIdentifier(uri, null, "com.example.students");
                Drawable res = getResources().getDrawable(imageResource);
                //add the icon to ImageButton

                remove.setImageDrawable(res);
                add = new Button(DeplomaAverageActivity.this);
                add.setText("add");
                ImageButton edit = new ImageButton(DeplomaAverageActivity.this);
                //set Subject hint to Course Name
                Subject.setHint("Course Name");
                row.addView(Subject);
                row.addView(units);
                row.addView(grade);
                row.addView(remove);
                //add the row to the table

                row.addView(add);
                tblAverage.addView(row);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //remove the row from table

                        tblAverage.removeView(row);
                        //make btnAddCourseDeploma button show in activity

                        btnAddCourseDeploma.setVisibility(View.VISIBLE);

                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Subject.length() > 0 && units.length() > 0 && grade.length() > 0) {
                            //make btnAddCourseDeploma button show in activity
                            btnAddCourseDeploma.setVisibility(View.VISIBLE);
                            //call SaveTable function to save the table in firebase
                            SaveTable(tblAverage);
                        }
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TableRow row = (TableRow) v.getParent();
                        //call EditRow function to edit current row
                        EditRow(row,edit,tblAverage);
                    }
                });


            }
        });
    }

    public void MakeTable(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");

        //make new row
        TableRow row = new TableRow(DeplomaAverageActivity.this);

        //make padding to row
        row.setDividerPadding(5);

        //set background color to row
        row.setBackgroundColor(Color.parseColor("#0079D6"));

        //array for head row in table
        String head[]={"Course","Units","Grade"};

        //set head row to table
        for (int i=0;i<head.length;i++){
            TextView tv = new TextView(DeplomaAverageActivity.this);
            tv.setTextSize(20);
            tv.setText(head[i]);
            row.addView(tv);
        }
        tblAverage.addView(row);

        reference.child("users").child(email).child("myProfile").child("Average").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check if database is empty
                if (!dataSnapshot.exists()) {
                    for (int i = 0; i < arr.length; i++) {
                        //save main courses of high school diploma in database
                        courses.setName(arr[i]);
                        courses.setGrade(0);
                        courses.setUnits(0);
                        reference.child("users").child(email).child("myProfile").child("Average").push().setValue(courses);
                    }
                    //call function to update the table
                    update(tblAverage);
                } else{
                    int index=0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (!ds.getKey().equals("average")) {
                            //getting data from database to fill the table
                            Courses c = ds.getValue(Courses.class);
                            TableRow row1 = new TableRow(DeplomaAverageActivity.this);
                            EditText Course = new EditText(DeplomaAverageActivity.this);
                            EditText units = new EditText(DeplomaAverageActivity.this);
                            EditText grade = new EditText(DeplomaAverageActivity.this);
                            Course.setEnabled(false);
                            units.setEnabled(false);
                            grade.setEnabled(false);

                            //set name of Course
                            Course.setText(c.getName());

                            //set units of Course
                            units.setText(c.getUnits() + "");

                            //set grade of Course
                            grade.setText(c.getGrade() + "");
                            //make BackgroundColor for row
                            row1.setBackgroundColor(Color.parseColor("#DAE8FC"));

                            //make units and grade type only numbers
                            units.setInputType(InputType.TYPE_CLASS_NUMBER);
                            grade.setInputType(InputType.TYPE_CLASS_NUMBER);
                            ImageButton remove = new ImageButton(DeplomaAverageActivity.this);
                            //make ImageButton with icon

                            String uri = "@drawable/ic_delete_foreground";

                            // where myresource (without the extension) is the file
                            int imageResource = getResources().getIdentifier(uri, null, "com.example.students");
                            Drawable res = getResources().getDrawable(imageResource);

                            //set the icon to ImageButton
                            remove.setImageDrawable(res);

                            add = new Button(DeplomaAverageActivity.this);
                            add.setText("add");
                            ImageButton edit = new ImageButton(DeplomaAverageActivity.this);
                            String uri1 = "@drawable/ic_baseline_edit_24";  // where myresource (without the extension) is the file
                            int imageResource1 = getResources().getIdentifier(uri1, null, "com.example.students");
                            Drawable res1 = getResources().getDrawable(imageResource1);

                            edit.setImageDrawable(res1);
                            Course.setEnabled(false);
                            //add Course,units,grade to row
                            row1.addView(Course);
                            row1.addView(units);
                            row1.addView(grade);

                            //if this course not main course
                            if (index>5)
                                //allow to remove it
                                 row1.addView(remove);
                            //add button edit to row
                            row1.addView(edit);
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TableRow row = (TableRow) v.getParent();
                                    //call function to edit current row in table
                                    EditRow(row, edit, tblAverage);
                                }
                            });
                            remove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //remove the row from table and in database
                                    reference.child("users").child(email).child("myProfile").child("Average").child(ds.getKey()).removeValue();
                                    if (tblAverage.getChildCount() <= 2)
                                        reference.child("users").child(email).child("myProfile").child("Average").removeValue();
                                    //call function to update the table after remove row
                                    update(tblAverage);
                                }
                            });
                            index++;
                            //add row to table
                            tblAverage.addView(row1);
                        }
                    }
            }
                //call function to calculate the average
                AverageCalculate();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void EditRow(TableRow row,ImageButton edit,TableLayout table){
        //allow the user to edit values in row
        if (row.getVirtualChildAt(0).isEnabled() == false) {
            row.getVirtualChildAt(0).setEnabled(true);
            row.getVirtualChildAt(1).setEnabled(true);
            row.getVirtualChildAt(2).setEnabled(true);
            String uri1 = "@drawable/ic_baseline_save_24";

            // where myresource (without the extension) is the file
            int imageResource1 = getResources().getIdentifier(uri1, null, "com.example.students");
            Drawable res1 = getResources().getDrawable(imageResource1);
            edit.setImageDrawable(res1);
        } else {
            //user can't make change values in row
            row.getVirtualChildAt(0).setEnabled(false);
            row.getVirtualChildAt(1).setEnabled(false);
            row.getVirtualChildAt(2).setEnabled(false);

            //call function to save the table in database
            SaveTable(table);
        }
    }

    //function to sava values in table to database
    public void SaveTable(TableLayout tblschedule){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
        reference.child("users").child(email).child("myProfile").child("Average").removeValue();
        boolean flag=false;
        for (int i = 1; i < tblschedule.getChildCount(); i++) {
            View child = tblschedule.getChildAt(i);
            //check if child is TableRow
            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;
                try {

                    //set values in class to save the class in database
                    courses.setName(((EditText) row.getChildAt(0)).getText().toString());
                    courses.setUnits(Integer.parseInt(((EditText) row.getChildAt(1)).getText().toString()));
                    courses.setGrade(Integer.parseInt(((EditText) row.getChildAt(2)).getText().toString()));
                    reference.child("users").child(email).child("myProfile").child("Average").push().setValue(courses, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {


                            } else {
                                Toast.makeText(DeplomaAverageActivity.this, "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                databaseError.toException().printStackTrace();
                            }

                        }
                    });
                }catch (Exception ex){
                    //if have name or grade or units is empty
                    Toast.makeText(DeplomaAverageActivity.this,"Please Fill All",Toast.LENGTH_LONG).show();
                    flag=true;
                }

            }
        }
        if (flag==false)
            //call function to update the table
            update(tblschedule);

    }

    //function to make or update the table
    public void update(TableLayout tblschedule){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //get email from database
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");

        //remove allview from tblschedule
        tblschedule.removeAllViews();

        //make row
        TableRow row = new TableRow(DeplomaAverageActivity.this);

        //make padding and background color
        row.setDividerPadding(5);
        row.setBackgroundColor(Color.parseColor("#0079D6"));

        //make array for first row in table
        String head[]={"Course","Units","Grade"};
        for (int i=0;i<head.length;i++){

            //make textView and set text by the index in array
            TextView tv = new TextView(DeplomaAverageActivity.this);
            tv.setText(head[i]);

            //set text size
            tv.setTextSize(20);

            //add textView to row
            row.addView(tv);
        }

        //add row to table
        tblschedule.addView(row);

        //get the values from firebase
        reference.child("users").child(email).child("myProfile").child("Average").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index=0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(!ds.getKey().equals("average")){
                        /*
                         * make new class by type Courses
                         * set the values to the class
                         */
                        Courses c= ds.getValue(Courses.class);
                        TableRow row1 = new TableRow(DeplomaAverageActivity.this);
                        EditText Course = new EditText(DeplomaAverageActivity.this);
                        EditText units = new EditText(DeplomaAverageActivity.this);
                        EditText grade = new EditText(DeplomaAverageActivity.this);

                        //the user can't change it
                        Course.setEnabled(false);
                        units.setEnabled(false);
                        grade.setEnabled(false);

                        //set text by the class
                        Course.setText(c.getName());
                        units.setText(c.getUnits()+"");
                        grade.setText(c.getGrade()+"");

                        //make background color to row
                        row1.setBackgroundColor(Color.parseColor("#DAE8FC"));



                        //make units and grade type only numbers
                        units.setInputType(InputType.TYPE_CLASS_NUMBER);
                        grade.setInputType(InputType.TYPE_CLASS_NUMBER);

                        //make ImageButton with icon
                        ImageButton remove= new ImageButton(DeplomaAverageActivity.this);
                        String uri = "@drawable/ic_delete_foreground";
                        // where myresource (without the extension) is the file
                        int imageResource = getResources().getIdentifier(uri, null, "com.example.students");
                        Drawable res = getResources().getDrawable(imageResource);

                        //set the icon to ImageButton
                        remove.setImageDrawable(res);
                        add = new Button(DeplomaAverageActivity.this);
                        add.setText("add");
                        ImageButton edit= new ImageButton(DeplomaAverageActivity.this);
                        String uri1 = "@drawable/ic_baseline_edit_24";

                        // where myresource (without the extension) is the file
                        int imageResource1 = getResources().getIdentifier(uri1, null, "com.example.students");
                        Drawable res1 = getResources().getDrawable(imageResource1);

                        edit.setImageDrawable(res1);
                        Course.setEnabled(false);
                        row1.addView(Course);
                        row1.addView(units);
                        row1.addView(grade);
                        if (index>5)
                            row1.addView(remove);
                        row1.addView(edit);
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TableRow row = (TableRow) v.getParent();

                                //call function to edit current row in table
                                EditRow(row,edit,tblschedule);
                            }
                        });
                        remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //remove the row from table and in database
                                reference.child("users").child(email).child("myProfile").child("Average").child(ds.getKey()).removeValue();
                                if (tblschedule.getChildCount()<=2)
                                    reference.child("users").child(email).child("myProfile").child("Average").removeValue();

                                //call function to update the table after remove row
                                update(tblschedule);
                            }
                        });

                        index++;
                        //add row to table
                        tblschedule.addView(row1);
                    }}
                //call function to calculate  average

                AverageCalculate();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    // function to calculate  average
    public void AverageCalculate(){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            //getting email from database
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
            reference.child("users").child(email).child("myProfile").child("Average").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double sum=0;
                    double countUnits=0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(!ds.getKey().equals("average")) {
                            //set data to class
                            Courses c = ds.getValue(Courses.class);
                            //check if unit is right
                            if (c.getUnits()==0) {
                                Toast.makeText(DeplomaAverageActivity.this,"Make sure about units",Toast.LENGTH_LONG).show();
                                return;
                            }
                            //count the units
                            countUnits += c.getUnits();
                            /*
                            check if course is Mathematics
                            have special Equation
                             */
                            // add bonus to grades by units
                            if (c.getUnits()==5&&c.getName().equals("Mathematics"))
                                c.setGrade(c.getGrade()+35);
                            else if(c.getUnits()==5)
                                c.setGrade(c.getGrade()+25);
                            else if(c.getUnits()==4)
                                c.setGrade(c.getGrade()+15);
                            sum += c.getUnits() * c.getGrade();
                        }
                    }

                    //show average in activty
                    tvAverage.setText("Average: " +sum/countUnits);

                    //save the average in database
                    reference.child("users").child(email).child("myProfile").child("Average").child("average").setValue(sum/countUnits);


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

    }
}