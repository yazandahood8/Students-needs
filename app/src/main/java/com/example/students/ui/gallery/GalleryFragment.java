package com.example.students.ui.gallery;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.R;
import com.example.students.data.Comment_Chat;
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

public class GalleryFragment extends Fragment {

    private Button btnFull,btnSubmitRate,btnCommentSave;
    private ImageView imgMap,imgLogo;
    private TextView tvTitle,tvInfo,tvWeb,tvMap;
    private RatingBar ratingBar;
    float stcount,stTotal;
    private EditText etComment;
    private TableLayout tblComments;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        showProgressDialogWithTitle();

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        //connecting xml to code
        btnFull = root.findViewById(R.id.btnFull);
        imgMap=(ImageView) root.findViewById(R.id.imgMap);
        imgLogo=(ImageView) root.findViewById(R.id.imgLogo);
        tvTitle=(TextView) root.findViewById(R.id.tvTitle);
        tvInfo=(TextView) root.findViewById(R.id.tvInfo);
        tvWeb=(TextView) root.findViewById(R.id.tvWeb);
        tvMap=(TextView) root.findViewById(R.id.tvMap);
        ratingBar=(RatingBar) root.findViewById(R.id.ratingBar2);
        btnSubmitRate=(Button) root.findViewById(R.id.btnSubmitRate);
        btnCommentSave=(Button) root.findViewById(R.id.btnCommentSave);
        etComment=(EditText) root.findViewById(R.id.etComment);
        tblComments=(TableLayout) root.findViewById(R.id.tblComments);

        btnSubmitRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");
                //number the rate user submit it
                float getrating = ratingBar.getRating();

                try {
                    //save the rate to database
                    stTotal += getrating;
                    reference.child("academics").child(tvTitle.getText().toString()).child("Rating").child("total").setValue(stTotal);
                    reference.child("academics").child(tvTitle.getText().toString()).child("Rating").child("count").setValue(++stcount);
                    reference.child("academics").child(tvTitle.getText().toString()).child("Rating").child("rate").setValue(stTotal / stcount);
                    reference.child("users").child(email).child("MyRate").child(tvTitle.getText().toString()).child("rate").setValue(getrating + "");
                    //the user can't change his rate
                    btnSubmitRate.setEnabled(false);
                    ratingBar.setEnabled(false);
                }catch (Exception ex){

                }



            }
        });
        btnCommentSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //check if user write text or comment
                        if (etComment.getText().length()>0) {
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("users").child(email.replace(".", "_")).child("myProfile").child("p").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //get name and image for user
                                    String stName = dataSnapshot.child("fullname").getValue(String.class);
                                    String img1 = dataSnapshot.child("img").child("image").getValue(String.class);
                                    //classs for comments
                                    Comment_Chat commentChat = new Comment_Chat();
                                    commentChat.setText(etComment.getText().toString());
                                    commentChat.setUser(stName);
                                    commentChat.setImgId(img1);
                                    //save the commentChat to database in current academy
                                    reference.child("academics").child(tvTitle.getText().toString()).child("comments").push().setValue(commentChat, new DatabaseReference.CompletionListener() {
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                Comments();
                                                Toast.makeText(getContext(), "save ok", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                databaseError.toException().printStackTrace();
                                            }
                                        }
                                    });

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }


                    }
                });
        //call function to show map academy
        MapShow();


        return root;
    }
    //function to get the comments from database and show it to user
    public void Comments(){
        tblComments.removeAllViews();
        etComment.setText("");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("academics").child(tvTitle.getText().toString()).child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    //getting data from firebase
                    Comment_Chat commentChat = ds.getValue(Comment_Chat.class);
                    //name
                    TextView tvName = new TextView(getContext());
                    //commentChat
                    TextView tvText = new TextView(getContext());
                    //space between the comments
                    Space space=new Space(getContext());
                    space.setMinimumHeight(100);
                    tvName.setText(commentChat.getUser());
                    tvName.setTextSize(20);
                    //make background for name
                    tvName.setBackgroundColor(Color.CYAN);
                    tvText.setText(commentChat.getText());
                    //add the commentChat to table
                    tblComments.addView(tvName,0);
                    tblComments.addView(tvText,1);
                    tblComments.addView(space,2);
                    
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void MapShow(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the academy name that user learning in
                String stAcademy = dataSnapshot.child("lemod").getValue(String.class);
                //set name academy title
                tvTitle.setText(stAcademy);
                //call Comments function
                Comments();
                reference.child("academics").child(stAcademy).child("informations").child("map").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get url for image and pdf map
                        String stPDf = dataSnapshot.child("pdf").getValue(String.class);
                        String imgId = dataSnapshot.child("img").getValue(String.class);
                        try {
                            //call function to set pdf
                            pdf(stPDf);
                            //call function to show image on activity
                            img(imgId,imgMap);
                        }catch (Exception ex){

                            /*
                            if don't find url for image and pdf in database
                            remove the image , button and title
                             */
                            imgMap.setVisibility(View.GONE);
                            btnFull.setVisibility(View.GONE);
                            tvMap.setVisibility(View.GONE);

                        }


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
                reference.child("academics").child(stAcademy).child("informations").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get information about the academy(information about academy,logo,web)
                        String stInfo = dataSnapshot.child("inf").getValue(String.class);
                        String imgId = dataSnapshot.child("logo").getValue(String.class);
                        String stWeb = dataSnapshot.child("web").getValue(String.class);
                        //call function to show logo the academy in activity
                        img(imgId,imgLogo);
                        tvInfo.setText(stInfo);
                        //call function to allow for user to rate the academy
                        Rating();
                        tvWeb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //move the user from current activity to web the academy
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(stWeb));
                                startActivity(browserIntent);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //function to allow for user to rate the academy
    public void Rating(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");



        reference.child("users").child(email).child("MyRate").child(tvTitle.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String rate=dataSnapshot.child("rate").getValue(String.class);
                //if user rate the academy in past
                if (rate!=null) {
                    //user can't rate again
                    btnSubmitRate.setEnabled(false);
                   ratingBar.setEnabled(false);
                    ratingBar.setRating((float) Double.parseDouble(rate));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //function to show the map in pdf file
    public void pdf(String url){
        btnFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show the map in pdf file by url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Intent newIntent = Intent.createChooser(intent, "Open File");
                try {
                    startActivity(newIntent);
                } catch (ActivityNotFoundException e) {
                }            }
        });
    }

    //function to show image in activity
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
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
                Toast.makeText(GalleryFragment.this.getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
            }
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