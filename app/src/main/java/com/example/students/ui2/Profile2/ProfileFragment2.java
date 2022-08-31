package com.example.students.ui2.Profile2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.ImageActivity;
import com.example.students.Login2Activity;
import com.example.students.NotStudentActivity;
import com.example.students.R;
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

public class ProfileFragment2 extends Fragment {
    private EditText tvEmail;
    private EditText tvPhone;
    private EditText tvName;
    private ImageButton imgvProfile;
    private Button btnedit;
    private CheckBox chBStudent,chBNotStudent;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_profile_fragment2, container, false);
        Bundle extras = getActivity().getIntent().getExtras();

        if (extras == null||!extras.getString("GUEST").equals("GUEST")){

            //function to show dialog
            showProgressDialogWithTitle();
            //call function to get data about user
            getProfileDetails();
            //call function to get image Profile
            imageProfile();

            //connecting xml to code
        tvEmail = (EditText) root.findViewById(R.id.tvEmail2);
        tvPhone = (EditText) root.findViewById(R.id.tvPhone2);
        tvName = (EditText) root.findViewById(R.id.tvName2);
        imgvProfile = (ImageButton) root.findViewById(R.id.imgvProfile2);
        btnedit = (Button) root.findViewById(R.id.btnedit2);
            chBStudent=(CheckBox)root.findViewById(R.id.chBStudent2);
            chBNotStudent=(CheckBox)root.findViewById(R.id.chBNotStudent2);
            chBNotStudent.setEnabled(false);

            //check box to change the type for user
            chBStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    //if the user press the check box show alert to submit his choose

                    if (isChecked){
                          /*
                    alert with title "Are You Sure You want to change your type??"
                    and Message " you will lose all your details!!"
                     */
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileFragment2.this.getContext());
                        builder1.setTitle("Are You Sure You want to change your type??");
                        builder1.setMessage("you will lose all your details!!");

                        builder1.setCancelable(false);
                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //if submit it remove all details about the user from firebase
                                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                        chBNotStudent.setChecked(false);
                                        reference.child("users").child(email.replace(".", "_")).child("myProfile").child("p").child("type").setValue("Student");

                                        reference.child("users").child(email.replace(".", "_")).child("myProfile").child("check").removeValue();
                                        reference.child("users").child(email.replace(".", "_")).child("myProfile").child("lemod").removeValue();

                                        Intent i=new Intent(getContext(), Login2Activity.class);
                                        startActivity(i);
                                        getActivity().finish();

                                    }
                                });
                        builder1.setNegativeButton(
                                "cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        chBNotStudent.setChecked(true);
                                        chBStudent.setChecked(false);


                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                }
            });


            btnedit.setText("EDIT");
        imgvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function to change the image profile
                ChangeImageProfile();
            }
        });

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnedit.getText().equals("EDIT")) {
                    //set text to btnedit from "EDIT" to "SAVE"
                    btnedit.setText("SAVE");
                    /*
                     make the tvPhone and tvName to enable:
                     the user can change the values to update his profile
                     */
                    tvPhone.setEnabled(true);
                    tvName.setEnabled(true);
                } else {
                    //show alert with Message "Did you want to save data?" to submit his change values

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileFragment2.this.getContext());
                    builder1.setMessage("Did you want to save data?");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                     /*
                                    make the tvPhone and tvName to not enable:
                                     the user can't change the values to update his profile
                                  */
                                    tvPhone.setEnabled(false);
                                    tvName.setEnabled(false);
                                    //set text to btnedit from "SAVE" to "EDIT"
                                    btnedit.setText("EDIT");
                                    //save the data to firebase

                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("p").child("fullname").setValue(tvName.getText().toString());
                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("p").child("phone").setValue(tvPhone.getText().toString());

                                }
                            });
                    builder1.setNegativeButton(
                            "cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            }
        });
    }
        else {
            /*
            show dialog with title "Guest" and Message "Please Sign in"
            to remember the user to login
             */
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Guest");
            builder1.setMessage("Please Sign in");

            builder1.setCancelable(false);

            builder1.setNegativeButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //move the user from current activity to main activity
                            Intent myIntent = new Intent(ProfileFragment2.this.getActivity(), NotStudentActivity.class);
                            myIntent.putExtra("GUEST","GUEST");
                            startActivity(myIntent);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
        return root;
    }

    //function to change the profile image
    public void ChangeImageProfile(){
        //alert to submit
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileFragment2.this.getContext());
        builder1.setMessage("Did you want to change image?");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                         /*
                        move the user from current activity to ImageActivity
                        ImageActivity to change the image and save it to firebase
                         */
                        Intent myIntent = new Intent(ProfileFragment2.this.getActivity(), ImageActivity.class);
                         /*
                        send value with key "TYPE" to ImageActivity
                         */
                        myIntent.putExtra("TYPE","notStudent");
                        startActivity(myIntent);
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
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
    public void getProfileDetails() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //get Profile Details from firebase

        reference.child("users").child(email.replace(".","_")).child("myProfile").child("p").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stEmail = dataSnapshot.child("email").getValue(String.class);
                String stName = dataSnapshot.child("fullname").getValue(String.class);
                String stPhone = dataSnapshot.child("phone").getValue(String.class);
                //set the email,name and phone to text Views

                tvEmail.setText(stEmail);
                tvName.setText(stName);
                tvPhone.setText(stPhone);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    public void  imageProfile(){
        //get the image from firebase

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").child("p").child("img").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stUrl = dataSnapshot.child("image").getValue(String.class);
                if (stUrl!=null)
                    img(stUrl);
                else {
                    imgvProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground));
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    //function to show image profile in activity
    public void img(String str) {
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                imgvProfile.setImageBitmap(Bitmap.createScaledBitmap(bmp,imgvProfile.getWidth(),
                        imgvProfile.getHeight(), false));
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
                Toast.makeText(ProfileFragment2.this.getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }

}


