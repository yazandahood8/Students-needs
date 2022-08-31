package com.example.students.ui.Profile;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.BroadcastRec.ChatAlarm;
import com.example.students.BroadcastRec.scheduleAlarm;
import com.example.students.ImageActivity;
import com.example.students.Login2Activity;
import com.example.students.R;
import com.example.students.SelectAcademyActivity;
import com.example.students.StudentsMain;
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

public class ProfileFragment extends Fragment {
    private EditText tvEmail;
    private EditText tvPhone;
    private EditText tvName;
    private TextView tvAcademy;
    private ImageButton imgvProfile;
    private Button btnedit;
    private Button btnChange,btnReset;
    AlertDialog alertDialog;
    private CheckBox chBStudent,chBNotStudent;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        //function to show dialog
        showProgressDialogWithTitle();
        //call function to get data about user
        getProfileDetails();
        //call function to get image Profile
        imageProfile();

        //connecting xml to code
        tvEmail=(EditText)root.findViewById(R.id.tvEmail);
        tvPhone=(EditText)root.findViewById(R.id.tvPhone);
        tvName=(EditText)root.findViewById(R.id.tvName);
        tvAcademy=(TextView)root.findViewById(R.id.tvAcademy);
        imgvProfile=(ImageButton)root.findViewById(R.id.imgvProfile);
        btnedit=(Button)root.findViewById(R.id.btnedit);
        btnReset=(Button)root.findViewById(R.id.btnReset);
        chBStudent=(CheckBox)root.findViewById(R.id.chBStudent);
        chBNotStudent=(CheckBox)root.findViewById(R.id.chBNotStudent);
        btnChange=(Button)root.findViewById(R.id.btnChange);

        chBStudent.setEnabled(false);

        //check box to change the type for user
        chBNotStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                //if the user press the check box show alert to submit his choose
                if (isChecked){
                    /*
                    alert with title "Are You Sure You want to change your type??"
                    and Message " you will lose all your details!!"
                     */

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileFragment.this.getContext());
                    builder1.setTitle("Are You Sure You want to change your type??");
                    builder1.setMessage("you will lose all your details!!");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //stop alarms
                                    ChatAlarm chatAlarm=new ChatAlarm();
                                    chatAlarm.cancelAlarm(getContext());

                                    scheduleAlarm scheduleAlarm=new scheduleAlarm();
                                    scheduleAlarm.cancelAlarm(getContext());
                                    //if submit it remove all details about the user from firebase
                                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    chBStudent.setChecked(false);
                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("p").child("type").setValue("not Student");

                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("MyTable").removeValue();
                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Average").removeValue();
                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("TableSetting").removeValue();
                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Settings").child("Chat").setValue("Sound");
                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("subject").removeValue();
                                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("lemod").removeValue();

                                    reference.child("users").child(email.replace(".", "_")).child("MyRate").removeValue();
                                    Intent i=new Intent(getContext(),Login2Activity.class);
                                    startActivity(i);
                                    getActivity().finish();

                                }
                            });
                    builder1.setNegativeButton(
                            "cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    chBNotStudent.setChecked(false);
                                    chBStudent.setChecked(true);


                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            }
        });
        //set text to btnedit "EDIT"
        btnedit.setText("EDIT");



        imgvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function to change the image profile
                ChangeImageProfile();
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move the user from current activity to SelectAcademyActivity to change the academy he study in
                Intent myIntent = new Intent(ProfileFragment.this.getActivity(), SelectAcademyActivity.class);
                startActivity(myIntent);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog to user with options to reset his values
                CharSequence[] values = {"All", "Table Schedule","Grades Table","Rate","Cancle"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Did you want to Reset your Datials data?");
                builder.setCancelable(true);
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            //if choose All : remove all details about the user from firebase(his Grades,his Table lessons...)
                            case 0:
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("MyTable").removeValue();
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Average").removeValue();
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("TableSetting").removeValue();
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Settings").child("Chat").setValue("Sound");
                                reference.child("users").child(email.replace(".", "_")).child("MyRate").removeValue();
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("lemod").removeValue();
                                Intent i=new Intent(getContext(), StudentsMain.class);
                                startActivity(i);



                                break;
                                //if choose Table Schedule :clear the table and remove it from firebase
                            case 1:

                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("MyTable").removeValue();
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("TableSetting").removeValue();

                                break;
                                //if choose Grades Table:clear the table Grades and remove it from firebase
                            case 2:
                                reference.child("users").child(email.replace(".", "_")).child("myProfile").child("Average").removeValue();

                                break;
                                //if choose Rate: remove his Rate from firebase
                            case 3:
                                reference.child("users").child(email.replace(".", "_")).child("MyRate").removeValue();
                                break;
                                //if choose Cancle : nothing
                            case 4:
                                break;

                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();

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
                }
                else{
                    //show alert with Message "Did you want to save data?" to submit his change values
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileFragment.this.getContext());
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
        return root;
    }

    //function to change the profile image
    public void ChangeImageProfile(){
        //alert to submit
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileFragment.this.getContext());
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

                        Intent myIntent = new Intent(ProfileFragment.this.getActivity(), ImageActivity.class);
                        /*
                        send value with key "TYPE" to ImageActivity
                         */
                        myIntent.putExtra("TYPE","Student");

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
        reference.child("users").child(email.replace(".","_")).child("myProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the academy that learning in
                String stAcademy = dataSnapshot.child("lemod").getValue(String.class);
                tvAcademy.setText("Study in:"+" "+stAcademy);
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
                Toast.makeText(ProfileFragment.this.getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }

}


