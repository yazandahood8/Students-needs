package com.example.students;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.students.data.Comment_Chat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UniverstyViewActivity extends AppCompatActivity {
    private Button btnMap,btnSubjects,btnComment;
    private ImageView imgLogo;
    private TextView tvTitle,tvInfo,tvWeb,tvchoose,tvRate;
    String stAcademy;
    private TableLayout tblUniversity;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    int i=0;
    float stcount,strate,stTotal;
    RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universty_view);
        //connecting xml to code
        btnMap = findViewById(R.id.btnMap);
        btnSubjects = findViewById(R.id.btnSubjects);
        tblUniversity = findViewById(R.id.tblUniversity);
        imgLogo=(ImageView)  findViewById(R.id.imgLogoview);
        tvTitle=(TextView)findViewById(R.id.tvTitleview);
        tvchoose=(TextView)findViewById(R.id.tvchoose);
        btnComment = findViewById(R.id.btnComment);
         ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvRate=(TextView)findViewById(R.id.tvRate);
        tvInfo=(TextView)findViewById(R.id.tvInfoview);
        tvWeb=(TextView)findViewById(R.id.tvWebview);

        //set background color
        btnMap.setBackgroundColor(Color.MAGENTA);
        btnSubjects.setBackgroundColor(Color.MAGENTA);
        btnComment.setBackgroundColor(Color.MAGENTA);
        ratingBar.setEnabled(false);

        //call function to get information about academy
        AcademyInformation();

        //call function to show dialog
        showProgressDialogWithTitle();

        //show map of academy
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set background color for buttons
                btnMap.setBackgroundColor(Color.GREEN);
                btnComment.setBackgroundColor(Color.MAGENTA);
                btnSubjects.setBackgroundColor(Color.MAGENTA);
                tblUniversity.removeAllViews();

                tvchoose.setText("Map Academic");
                TableRow row = new TableRow(UniverstyViewActivity.this);
                ImageView img = new ImageView(UniverstyViewActivity.this);
                //set Height and Width for image
                img.setMinimumHeight(500);
                img.setMinimumWidth(900);

                //add image to row
                row.addView(img);

                //button to open the map in pdf file
                Button btnFullpdf = new Button(UniverstyViewActivity.this);
                btnFullpdf.setText("SHOW FULL");

                reference.child("academics").child(stAcademy).child("informations").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get map and pdf url from database
                        String stPDf = dataSnapshot.child("map").child("pdf").getValue(String.class);
                        String imgIdMap = dataSnapshot.child("map").child("img").getValue(String.class);
                        try {
                            btnFullpdf.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //open map in pdf file
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse(stPDf), "application/pdf");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Intent newIntent = Intent.createChooser(intent, "Open File");
                                    try {
                                        startActivity(newIntent);
                                    } catch (ActivityNotFoundException e) {
                                    }     }
                            });

                            //call function to show the map in activity
                            img(imgIdMap, img,900,500);
                        } catch (Exception ex) {
                            //if not found url for map in database
                            tblUniversity.removeAllViews();
                            TextView tv = new TextView(UniverstyViewActivity.this);
                            tv.setText("Not Found map");
                            tblUniversity.addView(tv);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                //add row and button to table
                tblUniversity.addView(row);
                tblUniversity.addView(btnFullpdf);
            }
        });

        //show subjects in academy
        btnSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set title Subjects
                tvchoose.setText("Subjects");

                //set background color for buttons
                btnMap.setBackgroundColor(Color.MAGENTA);
                btnSubjects.setBackgroundColor(Color.GREEN);
                btnComment.setBackgroundColor(Color.MAGENTA);
                i=0;
                tblUniversity.removeAllViews();

                //array have types of subjects
               String Types[]={"Engineering","Natural sciences and exact sciences","Health and medical professions","Languages","history","Social Sciences","laws","Other circles","Business management and administration"
                ,"Integration of circles","Education",};
                for (i=0;i<Types.length;i++) {
                    //call function to get subjects
                    GetSubject(Types[i]);
                }
            }
        });

        //show comments about academy
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set Comments title
                tvchoose.setText("Comments");
                tblUniversity.removeAllViews();

                //set background color for buttons
                btnComment.setBackgroundColor(Color.GREEN);
                btnMap.setBackgroundColor(Color.MAGENTA);
                btnSubjects.setBackgroundColor(Color.MAGENTA);
                TableRow row = new TableRow(UniverstyViewActivity.this);
                tblUniversity.addView(row);
                reference.child("academics").child(stAcademy).child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //getting comments about academy from database
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            TableRow row1 = new TableRow(UniverstyViewActivity.this);
                            //add data to class
                            Comment_Chat commentChat = ds.getValue(Comment_Chat.class);
                            ImageView img=new ImageView(UniverstyViewActivity.this);
                            TextView tvName = new TextView(UniverstyViewActivity.this);
                            TextView tvText = new TextView(UniverstyViewActivity.this);
                            Space space=new Space(UniverstyViewActivity.this);

                            //call function to show image profile of commenter
                            img(commentChat.getImgId(),img,180,180);

                            //make space between the comments
                            space.setMinimumHeight(100);

                            //set name of commenter
                            tvName.setText(commentChat.getUser());

                            //make background
                            tvName.setBackgroundColor(Color.CYAN);

                            //set commentChat text
                            tvText.setText(commentChat.getText());

                            //add name to row
                            row1.addView(tvName);

                            //add row to table
                            tblUniversity.addView(row1);

                            //add commentChat and space to table
                            tblUniversity.addView(tvText);
                            tblUniversity.addView(space);

                        }


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            }
        });
    }

   // function to get subjects
    public void GetSubject(String type){
        reference.child("Subjects").child("BA").child(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    //call to function add the subject to table
                    AddSubjectToTable(ds.getKey(),type);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    //function to add the subject to table
    public void AddSubjectToTable(String name,String type){
        reference.child("Subjects").child("BA").child(type).child(name).child("academics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for(DataSnapshot ds1:dataSnapshot1.getChildren()) {
                    //check if academy have current subject
                    if (ds1.getKey().equals(stAcademy)) {
                        TableRow row = new TableRow(UniverstyViewActivity.this);
                        TextView tv=new TextView(UniverstyViewActivity.this);
                        Button btn=new Button(UniverstyViewActivity.this);

                        //add the subject to row
                        row.addView(tv);

                        //add button to show more information about subject
                        row.addView(btn);

                        //set width for subject name
                        tv.setMaxWidth(450);
                        tv.setText(name);
                        btn.setText("Show More");
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                /*
                                move user from current activity to ShowSubjectActivity
                                to show more information about subject
                                 */
                                Intent intent=new Intent(UniverstyViewActivity.this,ShowSubjectActivity.class);

                                /*
                                send name,type of subject
                                send name of academy
                                 */
                                intent.putExtra("type",type);
                                intent.putExtra("name",name);
                                intent.putExtra("Academy",stAcademy);
                                startActivity(intent);
                            }
                        });

                        //add row to table
                       tblUniversity.addView(row);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //function to get information about academy
    public void AcademyInformation(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            stAcademy = extras.getString("KEY");

            //call function to show the rate of academy
            Rating();

            //set name academy title
            tvTitle.setText(stAcademy);


            reference.child("academics").child(stAcademy).child("informations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //getting information about academy
                    String stInfo = dataSnapshot.child("inf").getValue(String.class);
                    String imgId = dataSnapshot.child("logo").getValue(String.class);
                    String stWeb = dataSnapshot.child("web").getValue(String.class);

                    //call function to show logo of academy in activity
                    img(imgId, imgLogo,1900,900);

                    //show information at activity
                    tvInfo.setText(stInfo);

                    tvWeb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //move user from current activity to web current academy
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(stWeb));
                            startActivity(browserIntent);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
    }

    //function to show image
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
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
            }
        });
    }

    ProgressDialog progressDialog;
    //show Dialog
    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(UniverstyViewActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }

    // function to show the rate of academy
    public void Rating(){
        reference.child("academics").child(stAcademy).child("Rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //getting rating from database

                    //get count of users the rate the academy
                    stcount = dataSnapshot.child("count").getValue(Float.class);

                    //get last rate
                    strate = dataSnapshot.child("rate").getValue(Float.class);

                    //get total rate
                    stTotal = dataSnapshot.child("total").getValue(Float.class);

                    //show the rate in number(example: 4.5/5)
                    tvRate.setText("Rating: "+strate+"/5");

                    //show the rate by stars
                    ratingBar.setRating(stTotal);
                }
                else
                tvRate.setText("Rating: 0/5");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
