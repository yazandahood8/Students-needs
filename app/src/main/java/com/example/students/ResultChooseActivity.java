package com.example.students;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.students.data.Sort;
import com.example.students.data.Subject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ResultChooseActivity extends AppCompatActivity   implements AdapterView.OnItemSelectedListener{
    private Spinner tvSubject;
    private TextView tvSub;
    private ImageView imgSubjectCh;
    private TableLayout tblAcademics;
    private EditText etD,etPsy;
    private int index=0;
    private int j;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    boolean flag=true;
    double temp;
    Subject s;
    Double Mylat,Mylng;
    List<String> categories = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_choose);
        //connecting xml to code
        tvSubject=(Spinner)findViewById(R.id.tvSubject);
        tvSub=(TextView)findViewById(R.id.tvSub);
        etD=(EditText)findViewById(R.id.etD);
        etPsy=(EditText)findViewById(R.id.etPs);
        Button btnSubmitRes=(Button)findViewById(R.id.btnSubmitRes);
        imgSubjectCh=(ImageView)findViewById(R.id.imgSubjectCh);
        tblAcademics=(TableLayout)findViewById(R.id.tblAcademics);

        Button btnSortLoctaion=(Button)findViewById(R.id.btnSortLoctaion);
        // attaching data adapter to spinner
        tvSubject.setOnItemSelectedListener(ResultChooseActivity.this);



       //user can't change grades
        etD.setEnabled(false);
        etPsy.setEnabled(false);


        final String[] arrayOfStrings ={"Business management and administration"
                ,"Education","Engineering","Health and medical professions",
        "Integration of circles","Languages","Natural sciences and exact sciences","Social Sciences",
        "history","laws"};


        final ListView lst = (ListView) findViewById(R.id.dialog_list);

        //remove list view and spinner from activity
        lst.setVisibility(View.GONE);
        tvSubject.setVisibility(View.GONE);

        // Enables single selection
        lst.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lst.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                arrayOfStrings));

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int item, long arg3) {
                lst.setItemChecked(item,true);

                //call function to make list
                MakeList(arrayOfStrings[item]);

                //set type to class by index selection in spinner
                s.setType(arrayOfStrings[item]);

            }
        });
        btnSortLoctaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index=0;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                //getting email from database
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");

                //array  (to save the academics) of Sort type class with size number of academics
                Sort arr[]=new Sort[tblAcademics.getChildCount()/4];

                //getting user Location
                reference.child("users").child(email).child("Location").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         Mylat=dataSnapshot.child("loc").child("lat").getValue(Double.class);
                         Mylng=dataSnapshot.child("loc").child("lng").getValue(Double.class);


                        for (j=1;j<tblAcademics.getChildCount();j+=4){
                            if (tblAcademics.getChildAt(j) instanceof TextView){
                                Sort sort=new Sort();
                                //set name academic to class
                                sort.setName(((TextView) tblAcademics.getChildAt(j)).getText().toString());
                                //getting location of academics
                                reference.child("academics").child(((TextView) tblAcademics.getChildAt(j)).getText().toString()).child("location").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String lan=dataSnapshot.child("lan").getValue(String.class);
                                        String lat=dataSnapshot.child("lat").getValue(String.class);
                                        try{
                                            //call function to get distance between user and academy and set value to class
                                            sort.setDistance(distance(Mylat,Mylng,Double.parseDouble(lat),Double.parseDouble(lan)));
                                        }catch (Exception ex){}
                                        //check index and array length there be no exception
                                        if (index!=arr.length)
                                           arr[index++]=sort;
                                        //in the end
                                        if (j-1==tblAcademics.getChildCount())
                                            //call function to sort academics by his location
                                            SortTable(arr);
//                                        else
//                                            Toast.makeText(ResultChooseActivity.this,(j-1)+"   "+tblAcademics.getChildCount(),Toast.LENGTH_LONG).show();


                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        //call function to check acceptable
        check();


        btnSubmitRes.setText("Edit");
        btnSubmitRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if user want to change his grades or subject
                if (btnSubmitRes.getText().toString().equals("Edit")){
                    int x=  Arrays.asList(arrayOfStrings).indexOf(s.getType());

                    //check on list past type
                    lst.setItemChecked(x,true);

                    //change text from "Edit" to "Save"
                    btnSubmitRes.setText("Save");

                    //allow to user to change subject and type
                    tvSubject.setEnabled(true);
                    etD.setEnabled(true);
                    etPsy.setEnabled(true);

                    //show list and spinner in activity
                    lst.setVisibility(View.VISIBLE);
                    tvSubject.setVisibility(View.VISIBLE);

                    //remove title
                    tvSub.setVisibility(View.GONE);
                }
                else {
                    boolean flag=false;
                    //getting email from database
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");

                    //check input validity
                    if ((etPsy.getText().toString().length()==0)||(Integer.parseInt(etPsy.getText().toString())<200||Integer.parseInt(etPsy.getText().toString())>800))
                        flag=true;
                    if ((etD.getText().length()==0)||(Double.parseDouble(etD.getText().toString())<0||Double.parseDouble(etD.getText().toString())>113))
                        flag=true;

                    if (flag==false) {

                        //remove list and spinner from activity
                        tvSub.setVisibility(View.VISIBLE);
                        tvSubject.setVisibility(View.GONE);

                        //show title on activity
                        lst.setVisibility(View.GONE);

                        //user can't change subject and his grades
                        tvSubject.setEnabled(false);
                        etD.setEnabled(false);
                        etPsy.setEnabled(false);

                        //set grades to class
                        s.setPsycho(Integer.parseInt(etPsy.getText().toString()));
                        s.setDiploma(Double.parseDouble(etD.getText().toString()));

                        //save the class to database
                        reference.child("users").child(email).child("myProfile").child("check").child("c").setValue(s);
                        //call function to check acceptable
                        check();
                        btnSubmitRes.setText("Edit");
                    }
                    else{
                        Toast.makeText(ResultChooseActivity.this,"Please Check Your Grades",Toast.LENGTH_LONG).show();
                    }



                }


            }
        });


    }

    //function to sort the academics by his location
    public void SortTable(Sort arr[]) {
        TableLayout tableLayout=new TableLayout(getBaseContext());
        if (arr[arr.length - 1] != null) {
            //sort the array
            Arrays.sort(arr, new Comparator<Sort>() {
                @Override
                public int compare(Sort s1, Sort s2) {
                    if (s1.getDistance() > s2.getDistance()) return 1;
                    else if (s1.getDistance() < s2.getDistance()) return -1;
                    else return 0;
                }
            });

        for (int i = 0; i < arr.length; i++) {
            for (int k=1;k<tblAcademics.getChildCount()-2;k++){
                //check if child is TextView
                if (tblAcademics.getChildAt(k) instanceof TextView){
                    if (((TextView) tblAcademics.getChildAt(k)).getText().toString().equals(arr[i].getName()))
                    {

                        //get current academic values from table "tblAcademics"
                       ImageView img=(ImageView) tblAcademics.getChildAt(k-1);
                       TextView tvName=(TextView)tblAcademics.getChildAt(k);
                        TableRow row=(TableRow) tblAcademics.getChildAt(k+1);
                        Space sp=(Space)tblAcademics.getChildAt(k+2);

                        //remove current academic values from table "tblAcademics"
                        tblAcademics.removeViews(k-1,4);

                        //add current academic the values to table "tableLayout"
                        tableLayout.addView(img);
                        tableLayout.addView(tvName);
                        tableLayout.addView(row);
                        tableLayout.addView(sp);
                    }
                }
            }
        }

        int i=0;
        //this while for sort
        while (tableLayout.getChildCount()>0){
            ImageView img ;
            TextView tv;
            TableRow tableRow;
            Space space;
            //check if child is ImageView
            if ( tableLayout.getChildAt(i) instanceof ImageView) {
                img = (ImageView) tableLayout.getChildAt(i);
                tableLayout.removeViewAt(i);
                tblAcademics.addView(img);

            }
            //check if child is TextView
            else if(tableLayout.getChildAt(i) instanceof TextView ) {
                tv = (TextView) tableLayout.getChildAt(i);
                tableLayout.removeViewAt(i);
                tblAcademics.addView(tv);

            }
            //check if child is TableRow
            else if(tableLayout.getChildAt(i) instanceof TableRow ) {
                tableRow = (TableRow) tableLayout.getChildAt(i);
                tableLayout.removeViewAt(i);
                tblAcademics.addView(tableRow);

            }
            //check if child is Space
            else if(tableLayout.getChildAt(i) instanceof Space ) {
                space = (Space) tableLayout.getChildAt(i);
                tableLayout.removeViewAt(i);
                tblAcademics.addView(space);

            }
        }
    }

        }
   //function to subjects that same type to list
    public void MakeList(String type){
        categories.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //getting subjects from database
        reference.child("Subjects").child("BA").child(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    //add name subject to list
                    categories.add(ds.getKey());


                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ResultChooseActivity.this, android.R.layout.simple_spinner_item, categories);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //add adapter to spinner
                tvSubject.setAdapter(dataAdapter);
                for (int i=0;i<categories.size();i++){
                    if (categories.get(i).equals(s.getName())){
                        //set past subject to spinner
                        tvSubject.setSelection(i);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //function to calculate the distance betweent user and academy
    public double distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;


        return (distance * meterConversion);
    }

    public void check(){
        tblAcademics.removeAllViews();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("users").child(email.replace(".","_")).child("myProfile").child("check").child("c").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //getting user grades
                     s=dataSnapshot.getValue(Subject.class);
                     //show grades in activity
                    if (s.getDiploma()>0)
                          etD.setText(s.getDiploma() + "");
                    if (s.getPsycho()>0)
                       etPsy.setText(s.getPsycho() + "");

                    //set name subject  title
                    tvSub.setText(s.getName());

                    //getting image of subject from database
                    reference.child("Subjects").child("BA").child(s.getType()).child(s.getName()).child("informations").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //get url image
                            String imgID=dataSnapshot.child("img").getValue(String.class);
                           if (imgID!=null)
                               //call function to show image in activity
                                img(imgID,imgSubjectCh,900,400);

                           //call function to make list of subjects that same type
                            MakeList(s.getType());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                    reference.child("Subjects").child("BA").child(s.getType()).child(s.getName()).child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            tblAcademics.removeAllViews();
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                 flag=false;
                                //getting admission
                                String diploma = ds.child("diploma").getValue(String.class);
                                String psycho = ds.child("psycho").getValue(String.class);
                                String sekhem = ds.child("sekhem").getValue(String.class);
                                if(diploma!=null&&!diploma.equals("")&&!diploma.equals("full")){

                                    //parse string to int
                                    int intDiploma=Integer.parseInt(diploma);

                                    //check if user have high school diploma grade higher than admission high school diploma
                                    if(intDiploma>s.getDiploma()){
                                        flag=true;
                                    }
                                }
                                if (psycho!=null&&!psycho.equals("")) {

                                    //parse string to int
                                    int intpsycho = Integer.parseInt(psycho);

                                    //check if user have psychometric grade higher than admission psychometric
                                    if (intpsycho > s.getPsycho()) {
                                        flag = true;
                                    }
                                }
                                if(!sekhem.equals("")){
                                    if(!ds.getKey().equals("Technion")) {
                                         /*
                                           calculate the sekhem for "Technion"
                                             It has a special formula
                                            */
                                        double doublesekhem = Double.parseDouble(sekhem);
                                        double temp = s.getDiploma();

                                        //getting the formula from Technion Web
                                        temp = temp * 7.26 - 116.8;
                                        double mysekhem = (temp + s.getPsycho()) / 2;
                                        if (doublesekhem > mysekhem) {
                                            flag = true;
                                        }
                                    }
                                    else if(!ds.getKey().equals("Technion")&&ds.getKey().equals("Bar Ilan University")){
                                        double doublesekhem = Double.parseDouble(sekhem);

                                        int intDiploma=Integer.parseInt(diploma);
                                        int intpsycho = Integer.parseInt(psycho);

                                        /*
                                          calculate the sekhem for "Bar Ilan University"
                                          It has a special formula
                                        */

                                        //getting the formula from "Bar Ilan University" Web

                                        double mysekhem=(intDiploma*0.4)+(intpsycho*0.4)+s.getMath()*0.1+s.getPsychokmote()*0.1;
                                        if (doublesekhem > mysekhem) {
                                            flag = true;
                                        }

                                    }
                                    else{
                                        double doublesekhem = Double.parseDouble(sekhem);

                                        /*
                                         calculate the sekhem for other academics
                                         getting formula of must academics in country
                                         */
                                        double mysekhem = (0.5 * s.getDiploma()) +(0.075*s.getPsycho())-19;
                                        if (doublesekhem > mysekhem) {
                                            flag = true;
                                        }
                                    }

                                }
                                //show the academics in activity by table

                                TableRow row = new TableRow(ResultChooseActivity.this);
                                TextView name = new TextView(ResultChooseActivity.this);
                                ImageView img = new ImageView(ResultChooseActivity.this);
                                TextView res = new TextView(ResultChooseActivity.this);
                                Button btnmore = new Button(ResultChooseActivity.this);
                                btnmore.setText("View");

                                //set Width and Height for image
                                img.setMinimumWidth(300);
                                img.setMinimumHeight(300);

                                //set name academy and set text size 20
                                name.setTextSize(20);
                                name.setText(ds.getKey());

                                //if user is Acceptance to academy
                                if (flag==false) {
                                    //make background GREEN
                                    res.setText("Acceptance...please click to check for more requests");
                                    res.setBackgroundColor(Color.GREEN);
                                    name.setBackgroundColor(Color.GREEN);
                                }
                                else {
                                    //make background RED
                                    res.setText("Refusal...please click to check the reason of refuse");
                                    name.setBackgroundColor(Color.RED);
                                    res.setBackgroundColor(Color.RED);

                                }
                                row.addView(res);
                                row.addView(btnmore);
                                btnmore.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String strView = "";
                                        //if user is Acceptance to academy
                                        if (res.getText().equals("Acceptance...please click to check for more requests")) {

                                            //array for more admissions request
                                            String arr[] = {"Amir test", "psycho_eng", "eng4", "eng5", "math4", "math5", "pyzeks4", "pyzeks5", "yael"};
                                            String text[] = {"Amir test:", "psycho_eng:", "english 4 units:", "english 5 units:", "math 4 units:"
                                                    , "math 5 units:", "phyzeks 4 units:", "phyzeks 5 units:", "yael:"};

                                            //make dialog
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ResultChooseActivity.this);
                                            builder.setTitle("More admissions request");
                                            final TextView tv = new TextView(ResultChooseActivity.this);
                                            for (int i = 0; i < arr.length; i++) {

                                                //getting data from database
                                                String str = ds.child(arr[i]).getValue(String.class);

                                                //add data to string
                                                if (str != null && !str.equals(""))
                                                    strView += text[i] + " " + str + "\n";

                                            }
                                            //set dialog text
                                            tv.setText(strView);

                                            //text size 30
                                            tv.setTextSize(30);

                                            //make text bold
                                            tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);

                                            //add text to dialog
                                            builder.setView(tv);

                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            builder.show();
                                        }
                                        else{
                                            if(diploma!=null&&!diploma.equals("")&&!diploma.equals("full")){
                                                //parse string to int

                                                int intDiploma=Integer.parseInt(diploma);

                                                //add user's high school diploma grade to string in dialog
                                                if (s.getDiploma()!=-1)
                                                    strView+="your Grade diploma is "+s.getDiploma()+" but you need "+intDiploma+"\n";
                                                else{
                                                    strView+="You don't have Full diploma but you need "+ intDiploma+"\n";
                                                }
                                            }
                                            if (psycho!=null&&!psycho.equals("")) {

                                                //parse string to int
                                                int intpsycho = Integer.parseInt(psycho);

                                                if (intpsycho>s.getPsycho())
                                                    //add user's psychometric grade to string in dialog
                                                    if (s.getPsycho()!=-1)
                                                         strView+="your Grade psycho is "+s.getPsycho()+" but you need "+intpsycho+"\n";
                                                    else
                                                        strView+="You don't have Psycho but you need "+intpsycho+"\n";
                                            }
                                            if(!sekhem.equals("")){
                                                double doublesekhem = Double.parseDouble(sekhem);

                                                if(!ds.getKey().equals("Technion")) {
                                                     temp = s.getDiploma();
                                                    temp = temp * 7.26 - 116.8;
                                                }
                                                else if(!ds.getKey().equals("Technion")&&ds.getKey().equals("Bar Ilan University")){

                                                    int intDiploma=Integer.parseInt(diploma);
                                                    int intpsycho = Integer.parseInt(psycho);
                                                     temp=(intDiploma*0.4)+(intpsycho*0.4)+s.getMath()*0.1+s.getPsychokmote()*0.1;
                                                }
                                                else{
                                                     temp = (0.5 * s.getDiploma()) +(0.075*s.getPsycho())-19;
                                                    if (doublesekhem > temp) {
                                                        flag = true;
                                                    }
                                                }
                                                if (s.getDiploma()!=-1&&s.getPsycho()!=-1)
                                                    //add string to show why the user refused from academy
                                                strView+="your Sekhem is "+temp+" but you need "+doublesekhem+"\n";
                                                if (s.getDiploma()>0&&s.getPsycho()>0)
                                                     strView+="your Grades is"+"\n"+"deploma: "+s.getDiploma()+"\n"+"psycho: "+s.getPsycho()+"\n";
                                                else if(s.getDiploma()>0&&s.getPsycho()==-1)
                                                    strView+="your Grades is"+"\n"+"deploma: "+s.getDiploma()+"\n";
                                                else if(s.getDiploma()==-1&&s.getPsycho()>0)
                                                    strView+="your Grades is"+"\n"+"psycho: "+s.getPsycho()+"\n";

                                                strView+="\n you need to upgrade your grades to\n";
                                                int i;
                                                double t=temp;
                                                double d=s.getDiploma();
                                                int count=1;

                                                /*
                                                call function to get 3 options to upgrade grades :
                                                1)upgrade high school diploma and psychometric grade
                                                2)upgrade high school diploma only
                                                3)psychometric grade only
                                                 */
                                                if (!op(t,d,s,doublesekhem,"op1",ds.getKey()).equals(""))
                                                     strView+= "Option "+(count++)+"\n"+op(t,d,s,doublesekhem,"op1",ds.getKey())+"\n";
                                                if (!op(t,d,s,doublesekhem,"op2",ds.getKey()).equals(""))
                                                    strView+= "Option "+(count++)+"\n"+op(t,d,s,doublesekhem,"op2",ds.getKey())+"\n";
                                                if (!op(t,d,s,doublesekhem,"op3",ds.getKey()).equals(""))
                                                    strView+= "Option "+(count++)+"\n"+op(t,d,s,doublesekhem,"op3",ds.getKey())+"\n";
                                                if (count==1){
                                                    try {
                                                        if (diploma!=null&&psycho!=null)
                                                            strView += "Option " + (count++) + "\n" + "deploma: " + Integer.parseInt(diploma) + "\n" + "psycho: " + Integer.parseInt(psycho) + "\n";
                                                        else if(psycho!=null&&diploma==null)
                                                            strView += "Option " + (count++) + "\n" + "deploma: full" + "\n" + "psycho: " + Integer.parseInt(psycho) + "\n";
                                                        else if(psycho==null&&diploma!=null)
                                                            strView += "Option " + (count++) + "\n" + "deploma: " + Integer.parseInt(diploma) + "\n" + "psycho: 750  \n";
                                                        else{
                                                            strView="Can't get you Options to upgrade Your Grades\n" +
                                                                    "Your Grades are far below the requirements";                                                        }



                                                    }catch (Exception ex){
                                                        strView="Can't get you Options to upgrade Your Grades\n" +
                                                                "Your Grades are far below the requirements";
                                                    }
                                                }
                                            }
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ResultChooseActivity.this);
                                            builder.setTitle("More Informations");
                                            final TextView tv = new TextView(ResultChooseActivity.this);
                                            tv.setText(strView);
                                            tv.setTextSize(20);
                                            tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);
                                            builder.setView(tv);
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            builder.show();
                                        }
                                    }
                                });

                                Image(ds.getKey(),img);

                                Space s=new Space(ResultChooseActivity.this);
                                s.setMinimumHeight(300);
                                s.setBackgroundColor(Color.BLACK);

                                tblAcademics.addView(img);
                                tblAcademics.addView(name);
                                tblAcademics.addView(row);
                                tblAcademics.addView(s);
                           }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });


                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });

        }

        //function to get options to upgrade the grades
        private String op(double t,double d,Subject s,double doublesekhem,String option,String academic){
        int i;
        double j;
        //upgrade high school diploma and psychometric grade
        if (option.equals("op1")) {
            for (i = s.getPsycho(); i < 800; i++) {
                if (d < 114) {
                    if (!academic.equals("Technion")) {
                        d++;
                        t = d;
                        t = t * 7.26 - 116.8;
                        if ((t + i) / 2 >= doublesekhem) {
                            return "deploma: " + d + "\n" + "psycho: " + i + "\n";
                        }
                    }
                    else if(!academic.equals("Technion")&&academic.equals("Bar Ilan University")){
                        t=(d*0.4)+(i*0.4)+s.getMath()*0.1+s.getPsychokmote()*0.1;
                        if (t>=doublesekhem){
                            return "deploma: " + d + "\n" + "psycho: " + i + "\n";

                        }

                    }
                    else{
                        d++;
                       // t=d;
                         t = (0.5 * d) +(0.075*i)-19;
                        if(t>= doublesekhem)
                            return "deploma: " + d + "\n" + "psycho: " + i + "\n";
                    }
                }

            }
        }
        //upgrade high school diploma only
        else if (option.equals("op2")){
            for (i = s.getPsycho(); i < 800; i++) {
                if (!academic.equals("Technion")) {
                    if ((t + i) / 2 >= doublesekhem) {
                        return "deploma: " + d + "\n" + "psycho: " + i + "\n";
                    }
                }
                else if(!academic.equals("Technion")&&academic.equals("Bar Ilan University")){
                    t=(d*0.4)+(i*0.4)+s.getMath()*0.1+s.getPsychokmote()*0.1;
                    if (t>=doublesekhem){
                        return "deploma: " + d + "\n" + "psycho: " + i + "\n";

                    }

                }
                else{
                    if((0.5 * d) +(0.075*i)-19>=doublesekhem)
                        return "deploma: " + d + "\n" + "psycho: " + i + "\n";
                }
            }
        }
        //psychometric grade only
        else {
            for (j = s.getDiploma(); j < 115; j++) {
                if (!academic.equals("Technion")) {
                    t = j;
                    t = t * 7.26 - 116.8;
                    if ((j + s.getPsycho()) / 2 >= doublesekhem) {
                        return "deploma: " + j + "\n" + "psycho: " + s.getPsycho() + "\n";
                    }
                }
                else if(!academic.equals("Technion")&&academic.equals("Bar Ilan University")){
                    t=(d*0.4)+(s.getPsycho()*0.4)+s.getMath()*0.1+s.getPsychokmote()*0.1;
                    if (t>=doublesekhem){
                        return "deploma: " + d + "\n" + "psycho: " + s.getPsycho() + "\n";

                    }

                }
                else {
                    if((0.5 * j) +(0.075*s.getPsycho())-19>=doublesekhem)
                        return "deploma: " + j + "\n" + "psycho: " + s.getPsycho() + "\n";
                }
            }
        }
            return "";


        }



    //function to show image in activity
    public void img(String str,ImageView img,int width,int height) {

        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                img.setImageBitmap(Bitmap.createScaledBitmap(bmp,width,
                        height, false));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
             //   progressDialog.dismiss();
                Toast.makeText(ResultChooseActivity.this,exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }

    //function to get logo of academy
    public void Image(String name,ImageView img){
        reference.child("academics").child(name).child("informations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get url logo from database
                    String logo = dataSnapshot.child("logo").getValue(String.class);

                    try {

                        //call function to show logo in activity
                        img(logo,img,500,300);

                    }catch (Exception ex){}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        //get selection spinner
        String item = parent.getItemAtPosition(position).toString();

        //set item to class
        s.setName(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    
}