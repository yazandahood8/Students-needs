package com.example.students.ui2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students.Adapter.MyAdapterSubjectOffer;
import com.example.students.NotStudentActivity;
import com.example.students.R;
import com.example.students.data.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultStudyOfferSubjectsActivity extends AppCompatActivity {
    private TableLayout tblSub;
    private Button btnEditGrades,btnBackToHome;
    private EditText etD2,etPs2;
    ArrayList arrayList;
    boolean flag;
    private int psychometry;
    private Double deploma;
    private MyAdapterSubjectOffer myAdapterSubjectOffer;
    private RecyclerView FinalResultList;
    private List<Subject> tagsList = new ArrayList<>();
    Subject subject=new Subject();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_study_offer_subjects);
        //connecting xml to code
        tblSub=(TableLayout)findViewById(R.id.tblSub);
        btnEditGrades=(Button)findViewById(R.id.btnEditGrades);
        etD2=(EditText)findViewById(R.id.etD2);
        etPs2=(EditText)findViewById(R.id.etPs2);
        btnBackToHome=(Button)findViewById(R.id.btnBackToHome);
        FinalResultList=(RecyclerView)findViewById(R.id.FinalResultList);
        FinalResultList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        FinalResultList.setLayoutManager(linearLayoutManager);
        myAdapterSubjectOffer = new MyAdapterSubjectOffer(tagsList, ResultStudyOfferSubjectsActivity.this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            /*
            getting data(grades) from last activity
            grades : psychometric and high school diploma
             */
            arrayList = extras.getParcelableArrayList("list");
             deploma=extras.getDouble("deploma");
            psychometry=extras.getInt("psycho");
            //show the data in activity
            etD2.setText(deploma+"");
            etPs2.setText(psychometry+"");
            etPs2.setEnabled(false);
            etD2.setEnabled(false);
            //call function to make the table of subjects
            MakeTable();


        }
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move the user from current activity to mainActivity
                Intent intent=new Intent(ResultStudyOfferSubjectsActivity.this, NotStudentActivity.class);
                startActivity(intent);
                finish();


            }

        });

        btnEditGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if want to edit grades or want to save his edit
               if (btnEditGrades.getText().toString().equals("Save")){
                   //user can't change his grades
                   etPs2.setEnabled(false);
                   etD2.setEnabled(false);
                   if (etPs2.length()>0&&etD2.length()>0){
                       //parse the string to double
                       deploma=Double.parseDouble(etD2.getText().toString());
                       //parse the string to int
                       psychometry=Integer.parseInt(etPs2.getText().toString());
                       //call function to make new table
                       MakeTable();

                   }
                   //change text of btnEditGrades from "Save" to "Edit Your Grades"
                   btnEditGrades.setText("Edit Your Grades");
               }
               else{
                   //change text of btnEditGrades from "Edit Your Grades" to "Save"
                   btnEditGrades.setText("Save");
                   //user can change his grades
                   etPs2.setEnabled(true);
                   etD2.setEnabled(true);
               }
            }

        });


    }
    //function to make table of subjects
    public void MakeTable(){
        tblSub.removeAllViews();
        for (int i = 0; i < arrayList.size(); i++) {
            //array have types of subjects
            String Types[]={"Engineering","Natural sciences and exact sciences","Health and medical professions","Languages","history","Social Sciences","laws","Other circles","Business management and administration"
                    ,"Integration of circles","Education",};
            for (int j=0;j<Types.length;j++){
                /*
                call function to get information about the subject
                 */
                SubjectInformation(Types[j],arrayList.get(i).toString());
            }
        }
    }

    //function to get information about subject
    public  void SubjectInformation(String type,String name){
        reference.child("Subjects").child("BA").child(type).child(name).child("informations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting image of subject from database
                String ss=(dataSnapshot.child("info").child("img").getValue(String.class));
                String ss2=(dataSnapshot.child("img").getValue(String.class));
                if (ss!=null&&!ss.isEmpty()) {
                    subject.setImg(ss);
                    subject.setName(name);
                    //call function to check acceptable about the subject
                    CheckAcceptable(type,name,tblSub,name);
                }
                else  if (ss2!=null&&!ss2.isEmpty()) {
                    subject.setImg(ss2);
                    //call function to check acceptable about the subject in academics
                    CheckAcceptable(type,name,tblSub,name);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    //function to check acceptable about the subject in academics
    public void CheckAcceptable(String type,String name,TableLayout tblSub,String s){
        reference.child("Subjects").child("BA").child(type).child(name).child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    boolean f=false;
                    String arr[]=new String[1];
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        flag=false;
                        //getting admission
                    String diploma = ds.child("diploma").getValue(String.class);
                    String psycho = ds.child("psycho").getValue(String.class);
                    String sekhem = ds.child("sekhem").getValue(String.class);
                    //Acceptance check for user
                    if(diploma!=null&&!diploma.equals("")&&!diploma.equals("full")){
                        //parse string to int
                        int intDiploma=Integer.parseInt(diploma);
                        //check if user have high school diploma grade higher than admission high school diploma
                        if(intDiploma>deploma){
                            flag=true;
                        }
                    }
                    if (psycho!=null&&!psycho.equals("")) {
                        //parse string to int
                        int intpsycho = Integer.parseInt(psycho);
                        //check if user have psychometric grade higher than admission psychometric
                        if (intpsycho > psychometry) {
                            flag = true;
                        }
                    }
                    if(sekhem!=null&&!sekhem.equals("")){
                        /*
                        calculate the sekhem for "Technion"
                        It has a special formula
                         */
                        if(!ds.getKey().equals("Technion")) {
                            double doublesekhem = Double.parseDouble(sekhem);
                            double temp = deploma;
                            //getting the formula from Technion Web
                            temp = temp * 7.26 - 116.8;
                            double mysekhem = (temp + psychometry) / 2;
                            if (doublesekhem > mysekhem) {
                                flag = true;
                            }
                        }
                        else{
                           // calculate the sekhem for other academics
                            double doublesekhem = Double.parseDouble(sekhem);
                            //getting formula of must academics in country
                            double mysekhem = (0.5 * deploma) +(0.075*psychometry)-19;
                            if (doublesekhem > mysekhem) {
                                flag = true;
                            }
                        }
                    }
                    //show the academics in activity by table
                        TextView tv = new TextView(ResultStudyOfferSubjectsActivity.this);
                        tv.setTextSize(16);
                        //make text bold
                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                        //set text red color
                        tv.setTextColor(Color.RED);
                        tv.setText(ds.getKey());
                        //save subject in array
                        if (arr.length==0)
                            arr= Arrays.copyOf(arr,arr.length+1);
                        arr[arr.length-1]=ds.getKey();
                        arr= Arrays.copyOf(arr,arr.length+1);
                    //check if user is acceptable in academics
                    if (flag==false) {
                        if (f==false){
                            //set name and type of subject in class
                            subject.setName(s);
                            subject.setType(type);
                            TextView tvname=new TextView(ResultStudyOfferSubjectsActivity.this);
                            tvname.setText(s);
                            //set size text of name 20
                            tvname.setTextSize(20);
                            //set name bold and green color
                            tvname.setTypeface(Typeface.DEFAULT_BOLD);
                            tvname.setBackgroundColor(Color.GREEN);
                            //add name of subject to table
                            tblSub.addView(tvname);
                            f=true;
                        }
                        TableRow row1=new TableRow(ResultStudyOfferSubjectsActivity.this);
                        Button btn=new Button(ResultStudyOfferSubjectsActivity.this);
                        tv.setMaxWidth(600);
                        btn.setText("Registration");
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //call function to open link for Register to Academic
                                Registrate(tv.getText().toString());
                            }
                        });
                        //add academic name to row
                        row1.addView(tv);
                        //add button for Register to Academic to row
                        row1.addView(btn);
                        // add row to table
                       tblSub.addView(row1);

                    }

                }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    // open link for Register to Academic
    public void Registrate(String name){
        reference.child("academics").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get url web academic
                String Registration = dataSnapshot.child("informations").child("Registration").getValue(String.class);
                //move user from current activity to web academic
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Registration));
                startActivity(browserIntent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}