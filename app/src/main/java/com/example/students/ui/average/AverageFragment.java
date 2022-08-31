package com.example.students.ui.average;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.students.R;
import com.example.students.data.Courses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class AverageFragment extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile1";
    public CheckBox dontShowAgain;


    private Button btnAddCourse,btnPrev,btnNext;
    private TextView tvYear;
    private ImageButton imNext,imPrev;
    private String year;
    private String semester;

    private int countyear=1;
    private int countsemster=1;
    Courses courses = new Courses();
    private TextView tvAvgSem;
    private Button add;
    private static double avgY;

    private static double index=0;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_average, container, false);
        TableLayout table = (TableLayout) root.findViewById(R.id.tableLayout);
        table.setPadding(10,0,10,0);




        //connecting xml to code
        btnAddCourse = (Button) root.findViewById(R.id.btnAddCourse);
        btnPrev = (Button) root.findViewById(R.id.btnPrev);
        btnNext = (Button) root.findViewById(R.id.btnNext);
        imNext=(ImageButton) root.findViewById(R.id.imNext);
        imPrev=(ImageButton) root.findViewById(R.id.imPrev);
        tvAvgSem = (TextView) root.findViewById(R.id.tvAvgSem);
        tvYear = (TextView) root.findViewById(R.id.tvYear);
         add = new Button(getContext());

         //call function to show dialog
        showProgressDialogWithTitle();

        year="Year"+countyear;
        semester="Semster"+countsemster;
        MakeTable(table);
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddCourse.setVisibility(View.GONE);

                //create a new row
                TableRow row = new TableRow(getContext());
                //add Layouts to your new row
                EditText Subject = new EditText(getContext());
                EditText units = new EditText(getContext());
                EditText grade = new EditText(getContext());
                //make the input only numbers
                units.setInputType(InputType.TYPE_CLASS_NUMBER);
                grade.setInputType(InputType.TYPE_CLASS_NUMBER);

                //make ImageButton with icon
                ImageButton remove= new ImageButton(getContext());
                String uri = "@drawable/ic_delete_foreground";
                // where myresource (without the extension) is the file
                int imageResource = getResources().getIdentifier(uri, null, "com.example.students");
                Drawable res = getResources().getDrawable(imageResource);
                //add the icon to ImageButton
                remove.setImageDrawable(res);
                 add = new Button(getContext());
                add.setText("add");
                ImageButton edit = new ImageButton(getContext());
                //set Subject hint to Course Name
                Subject.setHint("Course Name");
                //add Subject,units,grade,remove,add to row
                row.addView(Subject);
                row.addView(units);
                row.addView(grade);
                row.addView(remove);
                row.addView(add);
                //add the row to the table
                table.addView(row);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //remove the row from table
                        table.removeView(row);
                        //make btnAddCourse button show in activity
                        btnAddCourse.setVisibility(View.VISIBLE);

                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Subject.length() > 0 && units.length() > 0 && grade.length() > 0) {
                            //make btnAddCourse button show in activity
                            btnAddCourse.setVisibility(View.VISIBLE);
                            //call SaveTable function to save the table in firebase
                            SaveTable(table);
                        }
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TableRow row = (TableRow) v.getParent();
                        //call EditRow function to edit current row
                        EditRow(row,edit,table);
                    }
                });


            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // to move to another year
                year="Year"+(++countyear);
                countsemster=1;
                semester="Semster"+countsemster;
                //get data about this year
                MakeTable(table);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 to move to another year
                 check if current year more than 1
                 to don't get negative number or zero
                 */

                if(countyear>1) {
                    year="Year"+(--countyear);
                    countsemster=1;
                    semester="Semster"+countsemster;
                    //get data about this year
                    MakeTable(table);
                }
            }
        });
        imNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to move to another semester in current year
                countsemster++;
                semester="Semster"+countsemster;
                //get data about current year and current semester

                MakeTable(table);

            }
        });
        imPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 to move to another semester
                 check if current semester more than 1
                 to don't get negative number or zero
                 */
                if(countsemster>1) {
                    countsemster--;
                    semester="Semster"+countsemster;
                    //get data about current year and current semester

                    MakeTable(table);
                }
            }
        });
        return root;
    }



    @Override
    public void onResume() {
        /*
        * show dialog with title "Attention" and Message "Click Add Course and write your grade with units"
        * the alert is have checkbox too ,if check it the user can't show the alert again
        * the alert explain how to calculate your average
         */

        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        LayoutInflater adbInflater = LayoutInflater.from(getContext());
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setTitle("Attention");
        adb.setMessage(Html.fromHtml("Click Add Course and write your grade with units"));

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";
                //check if checkbox isChecked
                if (dontShowAgain.isChecked()) {

                    checkBoxResult = "checked";
                }
                //save the data SharedPreferences
                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                // Do what you want to do on "OK" action

                return;
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                // Do what you want to do on "CANCEL" action

                return;
            }
        });
        //check if in past the user don't checked it "don't show again"
        if (!skipMessage.equals("checked")) {
            //show the alert
            adb.show();
        }
        //else do nothing(don't show the alert)

        super.onResume();
    }

    //function to make or update the table
    public void MakeTable(TableLayout tblaverage){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
        //remove allview from tblschedule
        tblaverage.removeAllViews();
        //remove courses the user fail in
        reference.child("users").child(email).child("myProfile").child("Average").child("Fails").removeValue();
        //make row
        TableRow row = new TableRow(getContext());

        //make padding and background color
        row.setDividerPadding(5);
        row.setBackgroundColor(Color.parseColor("#0079D6"));
        //make array for first row in table
        String head[]={"Course","Units","Grade"};
        for (int i=0;i<head.length;i++){

            //make textView and set text by the index in array
            TextView tv = new TextView(getContext());
            tv.setTextSize(20);
            tv.setText(head[i]);

            //add textView to row
            row.addView(tv);
        }

        //add row to table
        tblaverage.addView(row);

        //get the values from firebase
        reference.child("users").child(email).child("myProfile").child("Average").child(year).child(semester).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //check if the key is'nt average
                    if(!ds.getKey().equals("average")){
                        /*
                        * make new class by type Courses
                        * set the values to the class
                         */
                    Courses c= ds.getValue(Courses.class);
                    //make row and three editTexts (Course,units,grade)
                    TableRow row1 = new TableRow(getContext());
                    EditText Course = new EditText(getContext());
                    EditText units = new EditText(getContext());
                    EditText grade = new EditText(getContext());
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

                        if (c.getGrade()>55){}
                    else {
                        // if user fail in course save it in firebase under "Fails" child with name the course
                        reference.child("users").child(email).child("myProfile").child("Average").child("Fails").child(c.getName()).setValue(c, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                } else {
                                    Toast.makeText(getContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    databaseError.toException().printStackTrace();
                                }
                            }
                        });
                    }
                    //make units and grade type only numbers
                    units.setInputType(InputType.TYPE_CLASS_NUMBER);
                    grade.setInputType(InputType.TYPE_CLASS_NUMBER);
                    //make ImageButton with icon
                    ImageButton remove= new ImageButton(getContext());
                    String uri = "@drawable/ic_delete_foreground";  // where myresource (without the extension) is the file
                    int imageResource = getResources().getIdentifier(uri, null, "com.example.students");
                    Drawable res = getResources().getDrawable(imageResource);
                    //set the icon to ImageButton
                    remove.setImageDrawable(res);
                    add = new Button(getContext());
                    add.setText("add");
                    ImageButton edit= new ImageButton(getContext());
                    String uri1 = "@drawable/ic_baseline_edit_24";
                    // where myresource (without the extension) is the file
                    int imageResource1 = getResources().getIdentifier(uri1, null, "com.example.students");
                    Drawable res1 = getResources().getDrawable(imageResource1);

                    edit.setImageDrawable(res1);
                    Course.setEnabled(false);
                    row1.addView(Course);
                    row1.addView(units);
                    row1.addView(grade);
                    row1.addView(remove);
                    row1.addView(edit);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TableRow row = (TableRow) v.getParent();
                            //call function to edit current row in table
                            EditRow(row,edit,tblaverage);
                        }
                    });
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //remove the row from table and in database
                            reference.child("users").child(email).child("myProfile").child("Average").child(year).child(semester).child(ds.getKey()).removeValue();
                            if (tblaverage.getChildCount()<=2)
                                reference.child("users").child(email).child("myProfile").child("Average").child(year).child(semester).removeValue();
                            //call function to update the table after remove row
                            MakeTable(tblaverage);
                        }
                    });
                    //add row to table
                        tblaverage.addView(row1);
                    }}
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //call function to calculate Semester average
        AvgSemster();


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
        reference.child("users").child(email).child("myProfile").child("Average").child(year).child(semester).removeValue();
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
                    reference.child("users").child(email).child("myProfile").child("Average").child(year).child(semester).push().setValue(courses, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {


                            } else {
                                Toast.makeText(getContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                databaseError.toException().printStackTrace();
                            }

                        }
                    });
                }catch (Exception ex){
                    //if have name or grade or units is empty
                    Toast.makeText(getContext(),"Please Fill All",Toast.LENGTH_LONG).show();
                    flag=true;
                }

            }
        }
        if (flag==false)
            //call function to update the table
            MakeTable(tblschedule);

    }
    //function to calculate average of current semster
    public void AvgSemster(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
        reference.child("users").child(email).child("myProfile").child("Average").child(year).child(semester).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double sum=0;
                double countUnits=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                   if(!ds.getKey().equals("average")) {
                       //set values in class
                        Courses c = ds.getValue(Courses.class);
                        //calculate sum of grades by units
                        sum += c.getUnits() * c.getGrade();
                        //count all units
                        countUnits += c.getUnits();
                   }
                }
                //if have course
                if(countUnits!=0) {
                    //calculate the average
                    double avg1=sum / countUnits;
                    //set the average in activity
                    tvAvgSem.setText(semester + ":" + new DecimalFormat("##.##").format(avg1));
                    //save the average of semester in database
                    reference.child("users").child(email).child("myProfile").child("Average").child(year).child(semester).child("average").setValue(avg1, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                avgYear();
                                progressDialog.dismiss();

                            } else {
                                Toast.makeText(getContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                databaseError.toException().printStackTrace();
                            }
                        }
                    });

                }
                else{
                    //set text without the average for year and semester
                    tvAvgSem.setText(semester);
                    tvYear.setText(year);
                    //call function to calculate the average of year
                    avgYear();
                    //remove the dialog
                    progressDialog.dismiss();


                }
            }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }
    //function to calculate the average of year
    public void avgYear(){
        avgY=0;
        index=0;
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email).child("myProfile").child("Average").child(year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               int i=1;
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                 if(dataSnapshot.getKey()==null) {
                     //set text without the average
                     tvYear.setText(year);
                 }
                 else
                while (i-1<dataSnapshot.getChildrenCount()){
                    reference1.child("users").child(email).child("myProfile").child("Average").child(year).child("Semster"+i++).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                           index++;
                            float x = dataSnapshot1.child("average").getValue(float.class);
                            //sum all average of semesters in current yeat
                            avgY += x;
                            if (index==dataSnapshot.getChildrenCount()) {
                                //set text to show average of year in activity
                                tvYear.setText(year + ":" + new DecimalFormat("##.##").format(avgY / dataSnapshot.getChildrenCount()));
                            }
                    }
                        @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    ProgressDialog progressDialog;
    //function to show dialog in activity
    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }
}

