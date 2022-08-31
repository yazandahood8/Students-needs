package com.example.students.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.Adapter.MyAdapterCourse;
import com.example.students.R;
import com.example.students.RestaurantsShowActivity;
import com.example.students.SelectAcademyActivity;
import com.example.students.data.Courses;
import com.example.students.data.Resturant;
import com.example.students.data.Table;
import com.example.students.ui.TableSettingsActivity;
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

import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    private TextView tvWel,tvNameRes,tvOpen,tvNameGrade;
    private ImageView imgRes;//image for restaurant
    private ListView listCourses; // to show the courses the is fall in
    private LinearLayout layout1;
    private ProgressBar progressBar;
    private Button btnShow;

    //class to schedule
    Table table=new Table();
    //adapter to get the courses that user fall in
    private MyAdapterCourse myAdapterCourse;
    // array for colors to design the schedule for student
    String Colors[]={"#FFFF00","#FF00FF","#FF0000","#C0C0C0","#808080","#808000",
    "#800080","#800000","#00FFFF","#00FF00","#008080","#008000","#008000","#0000FF","#000080"};

   /*
   get the cuurent email and replace "." to "_" becouse the firebase key can't gets "."
   that throw error
    */
    String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");
    //to get the data or write
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //connecting the code to xml
          tvWel = root.findViewById(R.id.tvWel);
        tvNameRes = root.findViewById(R.id.tvNameRes);
        imgRes = root.findViewById(R.id.imgRes);
        tvOpen= root.findViewById(R.id.tvOpen);
        tvNameGrade= root.findViewById(R.id.tvNameGrade);
        listCourses=root.findViewById(R.id.listCourses);
        layout1=root.findViewById(R.id.layout1);
        progressBar=root.findViewById(R.id.progressBar);
        btnShow=root.findViewById(R.id.btnShow);
        TableLayout tblschedule = (TableLayout) root.findViewById(R.id.tblschedule);


        // adapter to show the courses in list view
        myAdapterCourse=new MyAdapterCourse(getContext(),R.layout.grade);


        //add the adapter to list view
        listCourses.setAdapter(myAdapterCourse);
        //call function to welcome the user
        SayHello();

        // call function to check if have at least one restaurant and show it to user
        Resturantview();



        //check if user have schedule lessons
        String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if user have schedule lessons
                if (dataSnapshot.child("MyTable").getValue()!=null){
                    /*
                    call function to update schedule by data that user update it in past
                    from the firebase
                     */
                    MakeTable(tblschedule);
                }
                else{
                    //make button to  allow the user his schedule lessons
                    Button button=new Button(getContext());
                    button.setText("Create your own education schedule");
                    tblschedule.addView(button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            /*
                            move the user from current activity to TableSettingsActivity
                            TableSettingsActivity in this activity the user can
                            write the setting about the table (lesson time...)
                             */
                            Intent myIntent = new Intent(HomeFragment.this.getActivity(), TableSettingsActivity.class);
                            startActivity(myIntent);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


        return root;
    }

    public void SayHello(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_")).child("myProfile").child("p").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get name the user
                final String fullname = dataSnapshot.child("fullname").getValue(String.class);

                //get current time
                Date currentTime = Calendar.getInstance().getTime();
                /*
                check the time if this is morning or evening
                and make text by suitable to the time with name user
                 */
                if(currentTime.getHours()<13)
                    tvWel.setText("Good Morning "+fullname);
                else if (currentTime.getHours()>13&&currentTime.getHours()<18)
                    tvWel.setText("Hello "+fullname);
                else
                    tvWel.setText("Good Night "+fullname);
                //change position and size the text
                tvWel.setX(150);
                tvWel.setTextSize(15);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }



    public void MakeTable(TableLayout tblschedule){
        if (tblschedule.getChildCount()>0)
            tblschedule.removeAllViews();
        //array to show first row in table by array values
        String weak[]={" ","Sun","Mon ","Tue ","Wed ","Thu ","Fri ","Sat "};
        //get current user
        String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").child("MyTable").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //make the first row in schedule
                TableRow row = new TableRow(getContext());
                for (int i=0;i<weak.length;i++){
                    // make new textview and set text the current index in array
                    TextView t=new TextView(getContext());
                    t.setText(weak[i]);
                    // add the textview to row
                    row.addView(t);
                }
                //add the row to the table
                tblschedule.addView(row);
                //get the data from firebase
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //make new row
                    TableRow row1 = new TableRow(getContext());
                    //make edit text and set text the time of lesson
                    EditText tv=new EditText(getContext());
                    tv.setText(ds.getKey()+"");
                    //can't change it
                    tv.setEnabled(false);
                    //add the edittext to the table
                    row1.addView(tv);
                    reference.child("users").child(email.replace(".","_")).child("myProfile").child("MyTable").child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            /*
                             array to get data from firebase by days
                             this array have the keys they are saved in firebase
                              */
                            String ww[]={"sun","mon","tue","wed","thu","fri","sat"};

                            for (int k=0;k<7;k++){
                                //make new Edit Text
                                EditText et = new EditText(getContext());
                                //get value from firebase and set it text in et
                                et.setText(ds.child(ww[k]).getValue(String.class));
                                //make the EditText mutableLines
                                et.setSingleLine(false);
                                et.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                                et.setEnabled(false);
                                et.setWidth(130);
                                et.setTextSize(13);
                                if (et.getText().length()>0) {
                                    //call function to set back ground color
                                    TableColor(et.getText().toString(),et);
                                }
                                //add the editText to the row
                                row1.addView(et);
                            }


                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                    //add the row to the table
                    tblschedule.addView(row1);

                }
                //make button to edit the schedule
                Button btnEdit=new Button(getContext());
                btnEdit.setText("edit");
                //add the button to the table
                tblschedule.addView(btnEdit);
                tblschedule.setEnabled(true);
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        //make button to reset the table
                        Button btnReset=new Button(getContext());
                        btnReset.setText("Reset The Table");
                        //check if the user want to edit the table or to save his editor
                        if (btnEdit.getText().equals("edit")) {
                            //set text on button from "edit" to "save"
                            btnEdit.setText("save");
                            //add btnReset to table
                            tblschedule.addView(btnReset);
                            //if click on btnReset
                            btnReset.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(),"click Save or Refresh the page to Cancle",Toast.LENGTH_LONG).show();
                                    //clear the table
                                    for (int i = 1; i < tblschedule.getChildCount(); i++) {
                                        View child = tblschedule.getChildAt(i);
                                        if (child instanceof TableRow) {
                                            TableRow row = (TableRow) child;
                                                for (int x = 1; x < row.getChildCount(); x++) {
                                                    if (row.getVirtualChildAt(x) instanceof EditText)
                                                        //remove the text and remove the background
                                                    ((EditText) row.getVirtualChildAt(x)).setText("");
                                                    row.getVirtualChildAt(x).setBackground(row.getVirtualChildAt(0).getBackground());


                                                }
                                        }
                                    }


                                }
                            });
                        }

                        else {
                            //set text on button from "save" to "edit"
                            btnEdit.setText("edit");
                            //remove reset button from the table
                            tblschedule.removeView(tblschedule.getChildAt(tblschedule.getChildCount()-1));

                        }

                        for (int i = 1; i < tblschedule.getChildCount(); i++) {
                            View child = tblschedule.getChildAt(i);
                            if (child instanceof TableRow) {
                                TableRow row = (TableRow) child;
                                if (!btnEdit.getText().equals("edit")) {
                                    /*
                                    make the edittext in the table to enable
                                     to allow the user to update his schedule
                                     */
                                    for (int x = 1; x < row.getChildCount(); x++) {

                                        row.getVirtualChildAt(x).setEnabled(true);
                                    }
                                }
                                else{
                                    for (int x = 0; x < row.getChildCount(); x++) {
                                     /*
                                        make the edittext in the table to not enable
                                        the user can't change the texts in his schedule
                                      */
                                        row.getVirtualChildAt(x).setEnabled(false);

                                        if( row.getChildAt(x) instanceof EditText && x!=0)
                                            if(((EditText) row.getChildAt(x)).getText().length()>0) {
                                                //call function to set background
                                                TableColor(((EditText) row.getChildAt(x)).getText().toString(),(EditText) row.getVirtualChildAt(x));
                                            }
                                            else if(((EditText) row.getChildAt(x)).getText().length()==0) {
                                                //set default color background if the edittext is empty
                                                row.getVirtualChildAt(x).setBackground(row.getVirtualChildAt(0).getBackground());
                                            }
                                    }
                                    //call function to save the schedule's data to firebase
                                    SaveTable(tblschedule);
                                }
                            }
                        }

                    }
                });


            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


    }

    //function to save the data to firebase
    public void SaveTable(TableLayout tblschedule) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
        reference.child("users").child(email).child("myProfile").child("MyTable").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 1;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    View child = tblschedule.getChildAt(i++);
                    if (child instanceof TableRow) {
                        TableRow row = (TableRow) child;
                        /*
                        update values to class to save it in firebase
                        set values by his day in specific time
                         */
                        table.setSun(((EditText) row.getChildAt(1)).getText().toString());
                        table.setMon(((EditText) row.getChildAt(2)).getText().toString());
                        table.setTue(((EditText) row.getChildAt(3)).getText().toString());
                        table.setWed(((EditText) row.getChildAt(4)).getText().toString());
                        table.setThu(((EditText) row.getChildAt(5)).getText().toString());
                        table.setFri(((EditText) row.getChildAt(6)).getText().toString());
                        table.setSat(((EditText) row.getChildAt(7)).getText().toString());

                        //save the class to firebase
                        reference.child("users").child(email).child("myProfile").child("MyTable").child(ds.getKey()).setValue(table, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                } else {
                                    Toast.makeText(getContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    databaseError.toException().printStackTrace();
                                }
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
   //function to make background color in the table
    public void TableColor(String str,EditText et){
        String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").child("TableSetting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //get the number of text in firebase and
                    // setBackgroundColor appropriate according to the index in array "Colors"
                    int s=dataSnapshot.child(str.replace("#","_")).getValue(int.class);
                    et.setBackgroundColor(Color.parseColor(Colors[s]));
                }catch (Exception ex){
                    //save the new value to firebase with his number by dataSnapshot children
                    reference.child("users").child(email.replace(".","_")).child("myProfile").child("TableSetting").child(str.replace("#","_")).setValue(dataSnapshot.getChildrenCount());
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    //function to Show Random Resturant
    public  void Resturantview(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
         String email=FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_");
        reference.child("users").child(email).child("myProfile").child("random").removeValue();

        reference.child("users").child(email).child("myProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the academy the user study in
                final String Search = dataSnapshot.child("lemod").getValue(String.class);
                if (Search!=null&&!Search.equals("")){
                    //get all restaurants they are nearby to the academy
                reference.child("academics").child(Search).child("Resturants").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        Date d = new Date();
                        //array to get all days in week
                        String weak[] = {"Sunday", "Monday", "Tuesday", "wednesday", "Thursday", "Friday", "Saturday"};
                        //get Time Work about the academics
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            String s = dataSnapshot2.child("Time Work").child(weak[d.getDay()]).getValue(String.class);
                            //check if is'nt close
                            if (!s.equals("Close")) {
                                //example: 08:00-23:30

                                /*
                                make string for begin hours
                                hours1 = "08"
                                 */
                                StringBuilder hours1 = new StringBuilder();
                                hours1.insert(0, s.charAt(0));
                                hours1.insert(1, s.charAt(1));

                                /*
                                make string for begin minuets
                                min1 = "00"
                                 */
                                StringBuilder min1 = new StringBuilder();
                                min1.insert(0, s.charAt(3));
                                min1.insert(1, s.charAt(4));

                                /*
                                make string for end hours
                                hours2 = "23"
                                 */
                                StringBuilder hours2 = new StringBuilder();
                                hours2.insert(0, s.charAt(6));
                                hours2.insert(1, s.charAt(7));
                                 /*
                                make string for begin minuets
                                min2 = "30"
                                 */
                                StringBuilder min2 = new StringBuilder();
                                min2.insert(0, s.charAt(9));
                                min2.insert(1, s.charAt(10));
                                //check if current time is between the time work restaurant in current day
                                if (d.getHours() >= Integer.parseInt(hours1.toString()) && d.getHours() <= Integer.parseInt(hours2.toString())
                                        && d.getMinutes() >= Integer.parseInt(min1.toString()) && d.getMinutes() <= Integer.parseInt(min2.toString())) {
                                    //get the name and image of the restaurant
                                    String name = dataSnapshot2.child("name").getValue(String.class);
                                    String imgID = dataSnapshot2.child("imgId").getValue(String.class);
                                    tvNameRes.setText(name);
                                    if (imgID != null)
                                        //function to set the image of restaurant
                                        img(imgID);
                                    tvOpen.setText("Opened now");
                                    //make background is green for name a restaurant and to layout
                                    tvOpen.setBackgroundColor(Color.parseColor("#D86DFF04"));
                                    layout1.setBackgroundColor(Color.parseColor("#03F4A9"));
                                    // remove progressBar
                                    progressBar.setVisibility(View.GONE);

                                    //clicking a button to show more information about a restaurant
                                    btnShow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //class restaurant
                                            Resturant r = new Resturant();
                                            //update the values of the class
                                            r.setName(name);
                                            r.setNearby(Search);
                                            //save it to firebase
                                            reference.child("users").child(email).child("Search").setValue(r, new DatabaseReference.CompletionListener() {
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        //move from current activity to RestaurantsShowActivity to show more information about it
                                                        Intent myIntent = new Intent(getActivity(), RestaurantsShowActivity.class);
                                                        startActivity(myIntent);
                                                    } else {
                                                        Toast.makeText(getActivity(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                        databaseError.toException().printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    return;

                                }
                            }
                        }
                        //remove the progressBar and the button
                        progressBar.setVisibility(View.GONE);
                        btnShow.setVisibility(View.GONE);
                        //show text to user to know the All restaurants closing now
                        tvNameRes.setText("All restaurants closing now");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
                //if the user don't select academy he learning in
                else {
                    //move the user from current activity to SelectAcademyActivity to choose academy
                    Intent i=new Intent(getActivity(), SelectAcademyActivity.class);
                    startActivity(i);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        //call function to get all courses the user falls in
        Grades();
    }

    //function to show image of restaurant
    public void img(String str) {
        //FirebaseStorage is a service that supports uploading and downloading large objects to Google Cloud Storage
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        // Create a reference with an initial file path and name
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                imgRes.setImageBitmap(Bitmap.createScaledBitmap(bmp,imgRes.getWidth(),
                        imgRes.getHeight(), false));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors

                Toast.makeText(HomeFragment.this.getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }
    //function to get all courses the fall in
    public void Grades(){
        reference.child("users").child(email).child("myProfile").child("Average").child("Fails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //add the value to class of Courses
                    Courses courses = ds.getValue(Courses.class);
                    //add the class to the adapter to show it in list view
                    myAdapterCourse.add(courses);
                    //title
                    tvNameGrade.setText("Courses Fails");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

}