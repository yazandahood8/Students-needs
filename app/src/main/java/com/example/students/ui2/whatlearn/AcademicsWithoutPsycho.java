package com.example.students.ui2.whatlearn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.students.R;
import com.example.students.data.Academy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AcademicsWithoutPsycho extends AppCompatActivity {
    private  TableLayout Academicstbl;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academics_without_psycho);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //name of the subject
            name = extras.getString("name");

        }
        //connecting xml to code
         Academicstbl=(TableLayout)findViewById(R.id.Academicstbl);

        //call function to get academics from database
         GetAcademics();

    }
   //function to get academics from database
    public void GetAcademics(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Subjects").child("Study without psychometrics").child(name).child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting academics from database
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    TableRow row=new TableRow(AcademicsWithoutPsycho.this);
                    //set values to class
                    Academy academy=ds.getValue(Academy.class);
                    //Toast.makeText(AcademicsWithoutPsycho.this,academy.getName()+" "+academy.getDiploma(),Toast.LENGTH_LONG).show();
                    TextView tvName=new TextView(AcademicsWithoutPsycho.this);
                    TextView tvGrade=new TextView(AcademicsWithoutPsycho.this);
                    ImageView img=new ImageView(AcademicsWithoutPsycho.this);
                    LinearLayout linearLayout=new LinearLayout(AcademicsWithoutPsycho.this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.addView(tvName);
                    linearLayout.addView(tvGrade);

                    tvName.setText(academy.getName());
                    tvName.setTypeface(null, Typeface.BOLD_ITALIC);
                    tvGrade.setText("Grade of High School diploma:"+academy.getDiploma());
                    tvGrade.setTypeface(null, Typeface.BOLD_ITALIC);

                    //call function to get url image from database for academy
                    GetImageAcademy(academy.getName(),img);
                    row.addView(img);
                    row.addView(linearLayout);

                    Academicstbl.addView(row);
                    Space sp=new Space(AcademicsWithoutPsycho.this);
                    sp.setMinimumHeight(80);
                    Academicstbl.addView(sp);



                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


    }

    //function to get url image from database for academy
    public void GetImageAcademy(String name,ImageView img){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("academics").child(name).child("informations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String urlImage=dataSnapshot.child("logo").getValue(String.class);
                img(urlImage,img);



            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    //function to show image Academy in activity
    public void img(String str,ImageView img) {
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                img.setImageBitmap(Bitmap.createScaledBitmap(bmp,400,
                        400, false));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(AcademicsWithoutPsycho.this,exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }
}