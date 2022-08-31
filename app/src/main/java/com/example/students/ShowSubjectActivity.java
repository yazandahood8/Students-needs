package com.example.students;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShowSubjectActivity extends AppCompatActivity {
    private ImageView imgSubject;
    private TextView tvTitleSub;
    private TextView tvAdmissionSub;
    private TextView tvInfoSub;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String strView="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        //connecting xml to code
        imgSubject=(ImageView)findViewById(R.id.imgSubject);
        tvTitleSub=(TextView)findViewById(R.id.tvTitleSub);
        tvAdmissionSub=(TextView)findViewById(R.id.tvAdmissionSub);
        tvInfoSub=(TextView)findViewById(R.id.tvInfoSub);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            /*
            getting name and type of subject
            getting name of academy
            from last academy
             */
           String name = extras.getString("name");
            String type = extras.getString("type");
            String Academy = extras.getString("Academy");

            //make name of subject is title
            tvTitleSub.setText(name);

            //call function to get information about the subject
            SubjectInformation(type,name);

            //call function to get admissions the academy about current subject
            Admissions(type,name,Academy);
        }
    }

    //function to get information about the subject
    public void SubjectInformation(String type,String name){
        reference.child("Subjects").child("BA").child(type).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get information and url image of subject
                String info = dataSnapshot.child("informations").child("info").getValue(String.class);
                String imgID = dataSnapshot.child("informations").child("img").getValue(String.class);

                //call function to show the image in activity
                img(imgID,imgSubject);

                //set information in activity
                tvInfoSub.setText(info);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //function to show the image in activity
    public void img(String str,ImageView img) {
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                img.setImageBitmap(Bitmap.createScaledBitmap(bmp,img.getWidth(),
                        img.getHeight(), false));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors

                Toast.makeText(ShowSubjectActivity.this,exception.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //function to get admissions the academy about current subject
    public void Admissions(String type, String name, String Academy){
        reference.child("Subjects").child("BA").child(type).child(name).child("academics").child(Academy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //array have keys in database
                String arr1[]={"Amir test","chemistry3","deploma","eng4","eng5","math4","math5",
                        "psycho","psycho_eng","psycho_kmote","pyzeks3","pyzeks5","sekhem","yael"};

                //array to design
                String text1[]={"Amir test:","chemistry 3 units:","deploma:","english 4 units","english 5 units",
                "math 4 units:","math 5 units:","psycho:","psycho_eng:","psycho_kmote:","pyzeks 3 units",
                "pyzeks 4 units","sekhem:","yael:"};
                for (int i = 0; i < arr1.length; i++) {
                    //getting data from database
                    String str = dataSnapshot.child(arr1[i]).getValue(String.class);
                    if (str != null && !str.equals(""))
                        //add the data to string
                        strView += text1[i] + " " + str + "\n";

                }

                //set Admissions in activity
                tvAdmissionSub.setText(strView);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
}