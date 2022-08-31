package com.example.students.ui2;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.students.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SubjectViewActivity extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private TextView tvSubjectView,tvAcademicView,tvSubjectInfo,tvDurationOfStudies,tvAdmission;
    private ImageView imSubjectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_view);
        Bundle extras = getIntent().getExtras();
        //connecting xml to code

        tvSubjectView=(TextView)findViewById(R.id.tvSubjectView);
        tvAcademicView=(TextView)findViewById(R.id.tvAcademicView);
        tvSubjectInfo=(TextView)findViewById(R.id.tvSubjectInfo);
        imSubjectView=(ImageView) findViewById(R.id.imSubjectView);
        tvDurationOfStudies=(TextView)findViewById(R.id.tvDurationOfStudies);
        tvAdmission=(TextView)findViewById(R.id.tvAdmission);


        if (extras != null) {
              /*
            getting data from last activity:
            name the subject
            type of subject
            Academic
             */
            String Subject = extras.getString("Subject");
            String Academic = extras.getString("Academic");
            String Type = extras.getString("Type");
            //show name subject and name Academic  in activity
            tvSubjectView.setText(Subject);
            tvAcademicView.setText(Academic);
            //call function to get information about subject
            SubjectInformation(Subject,Academic,Type);


        }
    }
    //function to get information about subject
    public void SubjectInformation(String Subject, String Academic,String type){
        reference.child("Subjects").child("BA").child(type).child(Subject).child("informations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //get information and image of subject from database
                    String inf = dataSnapshot.child("info").getValue(String.class);
                    String imgId = dataSnapshot.child("img").getValue(String.class);
                    //show the information in activity
                    tvSubjectInfo.setText(inf);
                    //check if have url image of subject
                    if (imgId.length()>0)
                        //call function to show dialog loading page
                         showProgressDialogWithTitle();
                    //call function to show image in activity
                        img(imgId);
                }catch (Exception ex){
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        reference.child("Subjects").child("BA").child(type).child(Subject).child("academics").child(Academic).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //getting Admission and Duration Of Studies from database
                    String Admission = dataSnapshot.child("Admission").getValue(String.class);
                    String DurationOfStudies = dataSnapshot.child("DurationOfStudies").getValue(String.class);

                    tvDurationOfStudies.setText(DurationOfStudies);
                    StringBuilder stringBuilder=new StringBuilder();
                    int ind=0;
                    //set Admissions in stringBuilder to make it in lines(for design)
                    for (int i=0;i<Admission.length();i++){
                        if (Admission.charAt(i)!='.')
                        stringBuilder.insert(ind++,Admission.charAt(i));
                        else {
                            stringBuilder.insert(ind++, "\n");
                        }
                    }
                    tvAdmission.setText(stringBuilder.toString());
                }catch (Exception ex){
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //get image of subject from database
    public void img(String str) {
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                imSubjectView.setImageBitmap(Bitmap.createScaledBitmap(bmp,imSubjectView.getWidth(),
                        imSubjectView.getHeight(), false));
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
                Toast.makeText(SubjectViewActivity.this,exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }
    ProgressDialog progressDialog;
    //function to show dialog in activity
    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(SubjectViewActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }

}