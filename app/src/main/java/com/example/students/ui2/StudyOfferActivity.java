package com.example.students.ui2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.students.NotStudentActivity;
import com.example.students.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class StudyOfferActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsExam";
    public CheckBox dontShowAgain;
    private TableLayout tblQues;
    private Button btnSubmitQuiz;
    private  int count=1;
    //array to save  holland code's score
    private int Score[]=new int[6];
    //array have holland codes
    String Arr[]={"Artistic","Conventional","Entreprenuer","Realistic","Researcher","Social"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_offer);
        //connecting xml to code
        tblQues=(TableLayout)findViewById(R.id.tblQues);
        btnSubmitQuiz=(Button)findViewById(R.id.btnSubmitQuiz);

        tblQues.removeAllViews();
        for (int i=0;i<Arr.length;i++){
            //call function to build the quiz
            BuildTable(tblQues,Arr[i],i);
        }
        btnSubmitQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
               for (int i=0;i<Arr.length;i++){
                   //save score of user to database
                   reference.child("users").child(email.replace(".", "_")).child("HollandCode").child(Arr[i]).setValue(Score[i], new DatabaseReference.CompletionListener() {
                       public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                           if (databaseError == null) {
                           } else {
                               databaseError.toException().printStackTrace();
                           }
                       }
                   });

               }
               /*
               move  user from current activity to ResultStudyOfferActivity
               to show the subjects
                */
                Intent intent = new Intent(StudyOfferActivity.this, ResultStudyOfferActivity.class);
                startActivity(intent);

            }
        });
    }

    //function to build the quiz in table
    public void BuildTable(TableLayout tblQues,String str,int index){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //getting questions from database
        reference.child("HollandCode").child(str).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //find only the questions
                    if (!ds.getKey().equals("TopSubjects")){
                        //make row
                    TableRow row = new TableRow(StudyOfferActivity.this);
                    //make textView to set the question
                    TextView tv = new TextView(StudyOfferActivity.this);
                    //text view to get number of question in quiz (for design)
                    TextView counter = new TextView(StudyOfferActivity.this);
                    //make space between the questions and button (yes,no)
                    Space sp=new Space(StudyOfferActivity.this);
                    sp.setMinimumWidth(30);
                    //button to answer the question by yes or no
                    Button btnSelect = new Button(StudyOfferActivity.this);
                    //set default answer "no"
                    btnSelect.setText("No");
                    //make design for button
                        btnSelect.setBackgroundResource(R.drawable.btnjava);
                        //get questions from database
                    String s = ds.getValue(String.class);
                    //set questions to textView to show it in activity
                    tv.setText(s);
                    //make size 13 , bold , Max Width :800
                    tv.setTextSize(13);
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                    tv.setMaxWidth(800);
                    //counter the questions
                    counter.setText(count+++")");
                    btnSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (btnSelect.getText().toString().equals("No")) {
                                //add score to array score
                                Score[index] += 6;
                                /*
                                change text of button from "No" to "Yes"
                                This means that the user chose the appropriate sentence for him
                                 */
                                btnSelect.setText("Yes");
                                //make green Background for row
                                row.setBackgroundColor(Color.GREEN);
                            }
                            else{
                                //lowering the score from array score
                                Score[index] -= 6;
                                /*
                                change text of button from "No" to "Yes"
                                This means that the user chose the sentence that is not appropriate for him                                 */
                                btnSelect.setText("No");
                                //remove the background color from row
                                row.setBackgroundColor(Color.WHITE);
                            }
                        }
                    });
                    //add the question to table
                    row.addView(tv);
                    //make space
                     row.addView(sp);
                    //add btnSelect to row
                    row.addView(btnSelect);
                    //make spaces between the questions
                    Space space=new Space(StudyOfferActivity.this);
                    space.setMinimumHeight(100);
                    if (tblQues.getChildCount()>2) {
                        //make the questions in random index
                        Random random=new Random();
                        int x=random.nextInt(tblQues.getChildCount() - 1 + 1) + 1;
                        tblQues.addView(row, x);

                    }
                    else {
                        tblQues.addView(row);
                    }

                }

            }}
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    @Override
    public void onBackPressed() {
        //move user from current activity to main activity
        Intent intent = new Intent(StudyOfferActivity.this, NotStudentActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    @Override
    public void onResume() {
        /*
         * show dialog with title "Attention" and Message "Click Add Course and write your grade with units"
         * the alert is have checkbox too ,if check it the user can't show the alert again
         * the alert explain about the exam
         */
        AlertDialog.Builder adb = new AlertDialog.Builder(StudyOfferActivity.this);
        LayoutInflater adbInflater = LayoutInflater.from(StudyOfferActivity.this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setTitle("Attention");
        adb.setMessage(Html.fromHtml("Please change the answer from no to yes if you think the sentence is about you\n" +
                "...This exam determines your field through Holland Code"));

        adb.setPositiveButton("Holland Code's informations", new DialogInterface.OnClickListener() {
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
                //move user from current activity to web to explain about holland code
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unifrog.org/know-how/holland-codes"));
                startActivity(browserIntent);
                return;
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                // Do what you want to do on "CANCEL" action

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