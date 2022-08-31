package com.example.students.ui2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students.Adapter.MyAdapterSubjectOffer;
import com.example.students.R;
import com.example.students.data.Subject;
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

import java.util.ArrayList;
import java.util.List;

public class ResultStudyOfferActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsExam2";
    public CheckBox dontShowAgain;

    private TextView tvArtistic;
    private TextView tvConventional;
    private TextView tvEntreprenuer;
    private TextView tvRealistic;
    private TextView tvResearcher;
    private TextView tvSocial;
    private Button btnContinue;
    private CheckBox btnSelectAll;
    private TextView tvMax1;
    private int Score[]=new int[6];
    String Arr[]={"Artistic","Conventional","Entreprenuer","Realistic","Researcher","Social"};
    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private int count=1;
    ArrayList<String> Subjects = new ArrayList<>();
    private MyAdapterSubjectOffer myAdapterSubjectOffer;
    private RecyclerView listOffers;
    int imax1=0,imax2=0,imax3=0;
    private List<Subject> tagsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_study_offer);
        //connecting xml to code

        tvArtistic=(TextView)findViewById(R.id.tvArtistic);
        tvConventional=(TextView)findViewById(R.id.tvConventional);
        tvEntreprenuer=(TextView)findViewById(R.id.tvEntreprenuer);
        tvRealistic=(TextView)findViewById(R.id.tvRealistic);
        tvResearcher=(TextView)findViewById(R.id.tvResearcher);
        tvSocial=(TextView)findViewById(R.id.tvSocial);
        tvMax1=(TextView)findViewById(R.id.tvMax1);
        btnSelectAll=(CheckBox)findViewById(R.id.btnSelectAll);
        btnContinue=(Button)findViewById(R.id.btnContinue);


        //array have textviews in xml
        TextView tv[]={tvArtistic,tvConventional,tvEntreprenuer,tvRealistic,tvResearcher,tvSocial};
        for (int i=0;i<Arr.length;i++) {
            //call function to get holland codes for user
            HollandCodes(Arr[i],tv[i],i);
        }
         listOffers=(RecyclerView) findViewById(R.id.listOffers);
        listOffers.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listOffers.setLayoutManager(linearLayoutManager);
        myAdapterSubjectOffer = new MyAdapterSubjectOffer(tagsList, ResultStudyOfferActivity.this);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                move the user from current activity to MainGradesOfferActivity
                to put his grades
                 */
                Intent myIntent = new Intent(ResultStudyOfferActivity.this, MainGradesOfferActivity.class);
                //send Subjects List to MainGradesOfferActivity
                myIntent.putStringArrayListExtra("data", Subjects);
                startActivity(myIntent);


            }

        });





    }
    //function to get top holland codes for user
    public void HollandCodes(String str,TextView tv,int index){
        //get scores of hollands code for user
        reference.child("users").child(email).child("HollandCode").child(str).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int s = dataSnapshot.getValue(int.class);
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);
                //add scores in array
                Score[index]=s;
                //show scores in activity
                tv.setText(str+" :"+s);
                //for design
                if (s==0){
                    tv.setMinWidth(250);
                    tv.setMaxWidth(250);
                }
                else {
                    tv.setMinWidth(s * 50);
                    tv.setMaxWidth(s * 50);
                }
                if (index%2==1)
                    tv.setBackgroundColor(Color.parseColor("#41A592"));
                else
                    tv.setBackgroundColor(Color.parseColor("#DAE8FC"));
                //get the top 3 holland codes from 6
                if (index==Score.length-1){
                        int max1=0;
                        int max2=0;
                        for (int i=0; i<Score.length;i++)
                        {
                            if (Score[i] > max1) {
                                max2 = max1;
                                max1 = Score[i];
                                imax1=i;
                            }
                            else if (Score[i] > max2) {
                                max2 = Score[i];
                                imax2=i;
                            }
                            else {
                                imax3=i;
                            }
                        }
                        tvMax1.setText(Arr[imax1]+" "+Arr[imax2]+" "+Arr[imax3]+" ");
                        //call function to get the subjects that have this holland codes
                        TopSubjects(Arr[imax1],1);
                        TopSubjects(Arr[imax2],2);
                        TopSubjects(Arr[imax3],3);
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //function to get the subjects that have holland codes for user
    public void TopSubjects(String type,int x){
        reference.child("HollandCode").child(type).child("TopSubjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    //getting information about the subject
                    ImageView image = new ImageView(ResultStudyOfferActivity.this);
                    image.setMinimumWidth(300);
                    image.setMinimumHeight(300);
                    Button btnSelect = new Button(ResultStudyOfferActivity.this);
                    btnSelect.setText("Show ِAcademics");
                    String name = ds.getValue(String.class);
                    //make class to set the values in
                    Subject subject = new Subject();
                        subject.setTop1(type);
                    subject.setName(name);
                    String Types[] = {"Engineering", "Natural sciences and exact sciences", "Health and medical professions", "Languages", "history", "Social Sciences", "laws", "Other circles", "Business management and administration"
                            , "Integration of circles", "Education",};
                    for (int i = 0; i < Types.length; i++) {
                        //call function to show the subjects in activity by RecyclerView
                        ShowSubjects(Types[i], name, subject,x);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    //function to show the subjects in activity by RecyclerView
    public  void ShowSubjects(String type,String name,Subject subject,int x){
        reference.child("Subjects").child("BA").child(type).child(name).child("informations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting the image of subject
                String ss=(dataSnapshot.child("info").child("img").getValue(String.class));
                String ss2=(dataSnapshot.child("img").getValue(String.class));

                if (ss!=null&&!ss.isEmpty()) {
                    //set the image and type of subject to class
                    subject.setImg(ss);
                    subject.setType(type);
                    //add subject to list
                    tagsList.add(subject);
                    Subjects.add(subject.getName());
                }
                else  if (ss2!=null&&!ss2.isEmpty()) {
                    //set the image and type of subject to class
                    subject.setImg(ss2);
                    subject.setType(type);
                    //add subject to list
                    tagsList.add(subject);
                    Subjects.add(subject.getName());
                }
                if (x==3) {
                    //add adapter to list
                    myAdapterSubjectOffer.notifyDataSetChanged();
                    listOffers.setAdapter(myAdapterSubjectOffer);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    //get image of subject from database
    public void img(String str,ImageView img,int width,int height) {

        img.setImageResource(R.drawable.ic_launcher_foreground);
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                img.setImageBitmap(Bitmap.createScaledBitmap(bmp,width,
                        height, false));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                //   progressDialog.dismiss();
                Toast.makeText(ResultStudyOfferActivity.this,exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onResume() {
        /*
         * show dialog with title "Attention" and Message "Click Add Course and write your grade with units"
         * the alert is have checkbox too ,if check it the user can't show the alert again
         * the alert explain how to continue to reduction more topics
         */
        AlertDialog.Builder adb = new AlertDialog.Builder(ResultStudyOfferActivity.this);
        LayoutInflater adbInflater = LayoutInflater.from(ResultStudyOfferActivity.this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setTitle("Attention");
        adb.setMessage(Html.fromHtml("These are all relevant topics for you...Click continue to reduction more"));

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";
                //check if checkbox isChecked
                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }
                //save the data SharedPreferences
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();


                return;
            }
        });
        //check if in past the user don't checked it "don't show again"
        if (!skipMessage.equals("checked")) {
            adb.show();
        }
        //else do nothing(don't show the alert)

        super.onResume();
    }

}