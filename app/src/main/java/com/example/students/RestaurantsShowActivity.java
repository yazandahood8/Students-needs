package com.example.students;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import static android.Manifest.permission.CALL_PHONE;

public class RestaurantsShowActivity extends AppCompatActivity {
    private TextView tvNameRest;
    private TextView tvPhoneRest;
    private  TableLayout tblTime;
    private ImageView imgRest;
    private Button btnCall;
    private TextView tvActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_show);
        //connecting xml to code
        tvNameRest=(TextView)findViewById(R.id.tvNameRest);
         tvPhoneRest=(TextView)findViewById(R.id.tvPhoneRest);
         tblTime = (TableLayout) findViewById(R.id.tblTime);
        imgRest=(ImageView)findViewById(R.id.imgRest);
        tvActive=(TextView)findViewById(R.id.tvActive);
        btnCall=(Button)findViewById(R.id.btnCall);

        btnCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //make call
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+tvPhoneRest.getText().toString()));

                //check Permission call
                if (ContextCompat.checkSelfPermission(RestaurantsShowActivity.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RestaurantsShowActivity.this, new String[]{CALL_PHONE},1);
                }
                else
                startActivity(callIntent);
            }
        });

        //call function to get information about Restaurant from database
        RestaurantView();
        //call function to show dialog in activity
        showProgressDialogWithTitle();
    }

    //function to get information about Restaurant from database
    public  void RestaurantView(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_")).child("Search").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name of restaurant  and name of academic that nearby
                String name = dataSnapshot.child("name").getValue(String.class);
                String nearby = dataSnapshot.child("nearby").getValue(String.class);

                tvNameRest.setText(name);
                //call function to check if restaurant have image
                uploadImage(nearby,name);

                //call function to get time work for restaurant
                Time(nearby,name);
           }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    public void uploadImage(String nearby,String name){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("academics").child(nearby).child("Resturants").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get url image and phone number for restaurant
                String imgId = dataSnapshot.child("imgId").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);

                tvPhoneRest.setText(phone);
                try {
                    //call function to show image of restaurant in activity
                    img(imgId);

                }catch (Exception ex){}

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    //function to get time work for restaurant
    public void Time(String nearby,String name){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("academics").child(nearby).child("Resturants").child(name).child("Time Work").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //array to save the times
                String Time[]=new String[7];

                //array days of week
               String weak[]={"Sunday","Monday","Tuesday","wednesday","Thursday","Friday","Saturday"};
                //getting data from database and save it in array
                for(int i=0;i<weak.length;i++) {
                    Time[i] = dataSnapshot.child(weak[i]).getValue(String.class);
                }

                //call function to make time work table
                TimeWorkTable(tblTime,Time,weak);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //function to make time work table
    public void TimeWorkTable(TableLayout tblTime,String[]Time,String[]weak){
        //remove all views from table
        tblTime.removeAllViews();

            //maek row for table
            TableRow row = new TableRow(RestaurantsShowActivity.this);

            //array for head row in table
            String m[]={"Day","Time"};
            for (int i=0;i<m.length;i++){
                TextView tv = new TextView(RestaurantsShowActivity.this);

                //set text by index of array("Day","Time") and set size text 20
                tv.setTextSize(20);
                tv.setText(m[i]);

                //add the text to row
                row.addView(tv);
            }
            //add the row to table
        tblTime.addView(row);

        for (int i=0;i<weak.length;i++){
            //add times work to row
                TableRow row1 = new TableRow(RestaurantsShowActivity.this);
                TextView tv = new TextView(RestaurantsShowActivity.this);

                //set size text 20
                tv.setTextSize(20);

                //set day text
                tv.setText(weak[i]);

                TextView tv1 = new TextView(RestaurantsShowActivity.this);

                //set size text 20
                tv1.setTextSize(20);

                //set time work
                tv1.setText(Time[i]);

                //add the day and time to row
                row1.addView(tv);
                row1.addView(tv1);

                //add row to table
                tblTime.addView(row1);
            }

        //getting current time
        Date currentTime = Calendar.getInstance().getTime();

        //check if restaurant open in current day
        if(!Time[currentTime.getDay()].equals("Close")) {
            //make string for hours
            String begin = "";
            begin += Time[currentTime.getDay()].charAt(0);
            begin += Time[currentTime.getDay()].charAt(1);
            String end = "";
            end += Time[currentTime.getDay()].charAt(6);
            end += Time[currentTime.getDay()].charAt(7);

            //check if current time between the time work in current day
              if (currentTime.getHours() >= Integer.parseInt(begin) && currentTime.getHours() <= Integer.parseInt(end)) {

                  //set text "Open Now" and background green
                tvActive.setText("Open Now");
                tvActive.setBackgroundColor(Color.GREEN);
            } else {

                  //set text "Closed Now" and background red
                  tvActive.setText("Closed Now");
                tvActive.setBackgroundColor(Color.RED);
            }
        }
        else{

            //set text "Closed Now" and background red
            tvActive.setText("Closed Now");
            tvActive.setBackgroundColor(Color.RED);
        }
    }

    //function to show image in activity
    public void img(String str)  {
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgRest.setImageBitmap(Bitmap.createScaledBitmap(bmp,imgRest.getWidth(),
                        imgRest.getHeight(), false));
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
               // Toast.makeText(MyProfileFragment.this.getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }

    ProgressDialog progressDialog;
    //function to show dialog in activity
    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(RestaurantsShowActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }
}