package com.example.students.ui2.home2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.R;
import com.example.students.SelectAcademyActivity;
import com.example.students.UniverstyViewActivity;
import com.example.students.sql.dbAcademics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.Random;

public class HomeFragment2 extends Fragment {
    private TextView tvAcademic,tvInfoAcademic,tvSubjectRandom,tvInfoSubject,etSearchSub;
    private ImageView imgAcademic,Subjectimg;
    private Button btnSearchSub;
    private LinearLayout layoutliner;
    private  dbAcademics db;

    String imgID="";
    String name;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String Subj[]={"Business management and administration","Education","Engineering"
            ,"Health and medical professions","Languages","Natural sciences and exact sciences","Social Sciences"};
    Date d=new Date();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home2, container, false);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //database to Academics information
        db=new dbAcademics(getActivity());

        //connecting xml to code
        tvAcademic = (TextView) root.findViewById(R.id.tvAcademic);
        tvInfoAcademic = (TextView) root.findViewById(R.id.tvInfoAcademic);
        tvSubjectRandom = (TextView) root.findViewById(R.id.tvSubjectRandom);
        tvInfoSubject = (TextView) root.findViewById(R.id.tvInfoSubject);
        etSearchSub = (TextView) root.findViewById(R.id.etSearchSub);
        btnSearchSub = (Button) root.findViewById(R.id.btnSearchSub);
        imgAcademic = (ImageView) root.findViewById(R.id.imgAcademic);
        Subjectimg = (ImageView) root.findViewById(R.id.Subjectimg);
        layoutliner = (LinearLayout) root.findViewById(R.id.layoutliner);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            //get name from last activity
            String name = extras.getString("KEY");
            //set name of academy in edittext
            etSearchSub.setText(name);
        }
            //check if the user not GUEST
            if (extras == null||extras.getString("GUEST")==null||!extras.getString("GUEST").equals("GUEST")){
            btnSearchSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!etSearchSub.getText().toString().equals("")) {
                        reference.child("academics").child(etSearchSub.getText().toString()).child("informations").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    //set information about academy
                                    tvAcademic.setText(etSearchSub.getText().toString());
                                    String imgID = dataSnapshot.child("logo").getValue(String.class);
                                    String stInf = dataSnapshot.child("inf").getValue(String.class);
                                    tvInfoAcademic.setText(stInf);
                                    //call function to show logo of academy
                                    img(imgID, imgAcademic);
                                    tvInfoSubject.setText("");
                                    tvSubjectRandom.setText("");
                                    //remove the image of random subject
                                    Subjectimg.setTranslationX(9999);
                                    Button btn = new Button(HomeFragment2.this.getActivity());
                                    btn.setText("Show More");
                                    if (layoutliner.getChildCount() > 4) {
                                        layoutliner.removeViewAt(4);
                                    }
                                    layoutliner.addView(btn, 4);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //move the user from current activity to UniverstyViewActivity
                                            Intent myIntent = new Intent(HomeFragment2.this.getActivity(), UniverstyViewActivity.class);
                                            //send name of academy to UniverstyViewActivity
                                            myIntent.putExtra("KEY", etSearchSub.getText().toString());
                                            startActivity(myIntent);


                                        }
                                    });
                                } catch (Exception ex) {
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                }
            });
        etSearchSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move the user from current activity to SelectAcademyActivity to select academy
                Intent intent = new Intent(HomeFragment2.this.getActivity(), SelectAcademyActivity.class);
                intent.putExtra("HOME", "HOME");
                startActivity(intent);

            }
        });
        //call function to Random subject
        Random(1);
    }
            else{
                if(db.getAllrows().equals("")){
                    //add values to database we make it to guest
                    db.InsertRowAdmin("Haifa University","The University of Haifa is a research university located in the city of Haifa and is considered the largest academic center in the northern region. The university was founded in 1963 under the joint auspices of the Hebrew University of Jerusalem, and in 1972 the university received academic recognition from the Council for Higher Education. Today, the university has 18,000 students in all fields of study and degrees.  There are 7 faculties in the university: Education, Social Sciences, Humanities, Natural Sciences, Law, Welfare and Health Sciences and Management.",R.drawable.hifa,"35.0195184","32.7614296");
                    db.InsertRowAdmin("Bar Ilan University"," A Bar Ilan University is a research university located in the city of Ramat Gan. It was founded in 1955 and currently has close to 34,000 students  The uniqueness of the university is in imparting the foundations of the Jewish heritage through the basic studies in Judaism, the Higher Institute of Torah and the Cultivation of Jewish Identity, while studying academically and developing research in the various faculties of the University.  There are 8 faculties at the university: Jewish Studies, Social Sciences, Humanities, Exact Sciences, Engineering, Life Sciences, Law and Medicine.",R.drawable.bar,"34.8430876","32.0691989");
                    db.InsertRowAdmin("ORT Braude Academic College of Engineering",
                            "ORT Braude Academic College is located in the city of Carmiel. It was founded in 1993 and currently has over 5,500 students.  You can study at the college 8 fields for a bachelor's degree: optical engineering, biotechnology engineering, electrical and electronics engineering, mechanical engineering, information systems engineering, applied mathematics, software engineering and industrial engineering and management. In addition, these fields can be studied for a master's degree: biotechnology, systems engineering, software engineering and industrial engineering and management."
                    ,R.drawable.ort,"35.282708039392546","32.914004495286655");
                }
                //call function to Random subject
                Random(-1);
            }
        return root;
    }


        public void Random(int x)
        {
        if (x==1){
            reference.child("Subjects").child("BA").child(Subj[d.getDay()]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //random number
                    java.util.Random random = new Random();
                    int x = 0;
                    int i = 0;
                    try {
                        x = random.nextInt((int) dataSnapshot.getChildrenCount());
                    } catch (Exception ex) {
                    }
                    String name="";
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (x == i) {
                             name = ds.getKey();
                            try {
                                //get image and information of subject
                                String imgID = dataSnapshot.child(name).child("informations").child("img").getValue(String.class);
                                img(imgID, Subjectimg);
                                String stInf = dataSnapshot.child(name).child("informations").child("info").getValue(String.class);
                                tvInfoSubject.setText(stInf);
                                tvSubjectRandom.setText(name);
                            } catch (Exception ex) {
                            }

                        }
                        i++;
                    }
                    //call function to show random academy by subject
                    RandomAcademy(name);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
             }
        else{
            //remove edittext and button from activity
            etSearchSub.setX(9999);
            btnSearchSub.setX(9999);
            //number random to get random academy
           Random random = new Random();

            int id=db.getID();
            int rnd = random.nextInt(id+1);
            //get name and information about random academy
            String name=db.getName(rnd);
            String info=db.getInfo(rnd);
            //show name ,logo and  information in activity
            tvAcademic.setText(name);
            tvInfoAcademic.setText(info);
            imgAcademic.setImageDrawable(getResources().getDrawable(db.getImg(rnd)));
        }
        }
        //function to show random academy
        public void RandomAcademy(String subject){
            reference.child("Subjects").child("BA").child(Subj[d.getDay()]).child(subject).child("academics").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    java.util.Random random=new Random();
                     int   x = random.nextInt((int) dataSnapshot.getChildrenCount())+1;
                     int ind=0;
                     //get the random academy
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        name=ds.getKey();
                        ind++;
                        if(ind==x)
                           break;
                    }
                    //get information about the random academy
                     reference.child("academics").child(name).child("informations").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        //show name academy in activity
                                        tvAcademic.setText(name);
                                        //get logo and information of academy
                                         imgID = dataSnapshot.child("logo").getValue(String.class);
                                        String stInf = dataSnapshot.child("inf").getValue(String.class);
                                        tvInfoAcademic.setText(stInf);
                                        //show logo of academy
                                        img(imgID,imgAcademic);

                                    }catch (Exception ex){}
                                    return;

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    return;

                                }
                            });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }
    //get logo of academy from database
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

                Toast.makeText(HomeFragment2.this.getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }


    }


