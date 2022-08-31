package com.example.students;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.students.BroadcastRec.ChatAlarm;
import com.example.students.OnBoarding.OnboardingActivity;
import com.example.students.data.Profile;
import com.example.students.data.ScheduleService;
import com.example.students.data.ChatService;
import com.example.students.sql.dbLoginUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login2Activity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
     //parameters for Login
    private EditText etEmail,etPassw;//email and password
    private TextView tvGuest,tvForgetPass2,btnSignUp2;
    private FirebaseAuth auth;
    private dbLoginUser db;//class to check the number of times the user has tried typing the password

    //parameters for Sign Up
    private EditText etemailReq, etNameReq, etPasswordReq, etPassword2Req, etPhoneReq;
    private Spinner spType;
    Profile p = new Profile();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        // Initialize Firebase Auth
        auth=FirebaseAuth.getInstance();
        /*
         call function to show dialog
         Purpose א: the user know page is loading
         */
        showProgressDialogWithTitle();

        /*
       ConnectivityManager: Class that answers queries about the state of network connectivity.
         It also notifies applications when network connectivity changes.
         */
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //checking state of wifi connectivity
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //checking state of Mobile connectivity
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //אNew object of type dbLoginUser
        db=new dbLoginUser(this);

        /*
        Interface for accessing and modifying preference data returned by
         Context.getSharedPreferences(String, int)
         */
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        // check if this first time the user start the application
        if(!preferences.getBoolean("onboarding_complete",false)) {
            /*
            * start OnboardingActivity
            * Note that this activity only start once to show you
            * about the application
             */
            Intent onboarding = new Intent(this, OnboardingActivity.class);
            startActivity(onboarding);
            finish();
       }
        //Check if the user is logged in before and email is verified
        if (FirebaseAuth.getInstance().getCurrentUser() != null&&FirebaseAuth.getInstance().getCurrentUser() .isEmailVerified()){
           // Check if the phone is connected to the internet
            if (mMobile.isConnected()||mWifi.isConnected()) {

                /*
                The entry point for accessing a Firebase Database. You can get an instance by calling getInstance().
                 To access a location in the database and read or write data, use getReference().
                 */
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_")).child("myProfile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String str1 = dataSnapshot.child("p").child("type").getValue(String.class);
                        //Move the user to the appropriate page
                        if(str1.equals("Student")) {
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Intent i = new Intent(Login2Activity.this, ChatAlarm.class);
                                PendingIntent pi = PendingIntent.getBroadcast(Login2Activity.this, 0, i, 0);
                                //add this to column and find value static
                            //final value have in manger Alarm


                                if (alarmManager!=null)
                                    alarmManager.cancel(pi);
                                /*
                                this service for Chat
                                Purpose the service is make application to play in background
                                and check if the user have new chat and send a notice
                                 */
                                Intent s = new Intent(Login2Activity.this, ChatService.class);
                                startService(s);
                                /*
                                this service for Schedule the user make it about his lessons
                                Purpose the service is make application to play in background
                                and remember the user the before 5 minuets to start a lesson
                                 */
                                 Intent a = new Intent(Login2Activity.this, ScheduleService.class);
                                 startService(a);

                                Toast.makeText(Login2Activity.this,"Students",Toast.LENGTH_LONG).show();
                            /*
                             move from the current activity to StudentsMain
                             StudentsMain:this activity for "Student"
                             */
                            Intent intent = new Intent(Login2Activity.this, StudentsMain.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                        }
                        else if(str1.equals("not Student"))
                        {

                            Toast.makeText(Login2Activity.this,"not Student",Toast.LENGTH_LONG).show();
                            /*
                             move from the current activity to NotStudentActivity
                             NotStudentActivity:this activity for "Student interested in academic studies"
                             */
                            Intent intent = new Intent(Login2Activity.this, NotStudentActivity.class);
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
            else {
                Toast.makeText(Login2Activity.this, "You don't have internet", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
        // check if user is login but the email is not verified
        else if(FirebaseAuth.getInstance().getCurrentUser() != null&&FirebaseAuth.getInstance().getCurrentUser() .isEmailVerified()==false){

            //get the cuurent email from firebase
            String email=FirebaseAuth.getInstance().getCurrentUser().getEmail();

            // call function to verified the email
            checkIfEmailVerified(email);
            progressDialog.dismiss();

        }
        else
            progressDialog.dismiss();


        db=new dbLoginUser(this);
//layout have get popular item
        View loginLayout = findViewById(R.id.loginLayout);
        View layout_register = findViewById(R.id.layout_register);


      //  layout_register

        //connecting the code to xml

         etEmail = (EditText)loginLayout.findViewById(R.id.editTextEmail);
         etPassw = (EditText)loginLayout.findViewById(R.id.editTextPassword);
        tvGuest=(TextView)loginLayout.findViewById(R.id.tvGuest);
        tvForgetPass2=(TextView)loginLayout.findViewById(R.id.tvForgetPass2);
        Button btnLogin1=(Button)loginLayout.findViewById(R.id.btnLogin1);
        TextView etSignUp=(TextView)loginLayout.findViewById(R.id.etSignUp);


        etemailReq = (EditText)layout_register. findViewById(R.id.etEmailReq);
        etNameReq = (EditText)layout_register.  findViewById(R.id.etNameReq);
        etPasswordReq = (EditText)layout_register.  findViewById(R.id.etPasswordReq);
        etPassword2Req = (EditText)layout_register.  findViewById(R.id.etPassword2Req);
        btnSignUp2 = (Button) layout_register. findViewById(R.id.btnSignUp2);
        etPhoneReq = (EditText)layout_register.  findViewById(R.id.etPhoneReq);
        spType=(Spinner)layout_register. findViewById(R.id.spType);
        TextView btnReturn=(TextView)layout_register.findViewById(R.id.btnReturn);
        spType.setOnItemSelectedListener(Login2Activity.this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Student");
        categories.add("Student interested in academic studies");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spType.setAdapter(dataAdapter);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        btnSignUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHuntler2();
            }
        });
        etSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginLayout.setVisibility(View.GONE);
                layout_register.setVisibility(View.VISIBLE);            }
        });
        btnLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHandler();           }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginLayout.setVisibility(View.VISIBLE);
                layout_register.setVisibility(View.GONE);            }
        });
        tvGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login2Activity.this, NotStudentActivity.class);
                i.putExtra("GUEST", "GUEST");

                startActivity(i);
                finish();
            }
        });
        tvForgetPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login2Activity.this, ResetPasswActivity.class);
                startActivity(i);
            }
        });

    }



    // Login Activity
    private void dataHandler()
    {
        //1.getting data
        String stEmail=etEmail.getText().toString();
        String stPassw=etPassw.getText().toString();
        boolean flag=true;
        //2.checking
        if (stEmail.length()<3) {
            etEmail.setError("Wrong Email");
            flag = false;
        }
        if (stPassw.length()<3) {
            etPassw.setError("Wrong Password");
            flag = false;
        }
        if(flag==true) {
            showProgressDialogWithTitle();

            signIn(stEmail, stPassw);
        }
    }
    public void verifyEmail(){
        /*
        show dialog with massege "Please verify your email!!"
         */
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Login2Activity.this);
        builder1.setMessage("Please verify your email!!");
        builder1.setCancelable(false);

        builder1.setNegativeButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }
    private void signIn(String email, String passw) {
        // check if user don't try to type password wrong more than 5 times
        if (db.getRow(email)<5){
            auth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(Login2Activity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //check if email and password are right
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        //call up function to check verfied email
                        checkIfEmailVerified(email);
                    } else {
                        Date d = new Date();
                        //check if password wrong and this first time
                        if (db.getRow(email) == -1) {
                            //save email and current time to database
                            int x = d.getHours() * 60 + d.getMinutes();
                            db.InsertRowAdmin(email, 0, x);

                        } else {
                            /*
                            update email and current time to database for current email
                            ** in class dbLogin we have parameter static to counter  times
                            that user type password wrong

                             */

                            db.update(email, db.getRow(email));
                        }
                        task.getException().printStackTrace();
                        progressDialog.dismiss();

                    }
                }
            });
        }
        // check if user try to type password wrong more than 5 times
        else {
            progressDialog.dismiss();
            Date d = new Date();
            int h= db.getTime(email);
            // If the current time is one hour greater than the time the password was written in the past
            if (d.getHours()*60+d.getMinutes()-60>=h)
            {
                //the user can to try login
                db.delete(email);
                signIn(email,passw);
            }
            else {
                /*
                * user can't to try to login
                * the user is show alert written on it to wait hour
                 */
                String str=d.getHours()*60+d.getMinutes()-60+"";
                Toast.makeText(Login2Activity.this, h+" "+str, Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Login2Activity.this);
                builder1.setTitle("You have made many attempts to log in!!");
                builder1.setMessage("Please wait hour");

                builder1.setCancelable(false);

                builder1.setNegativeButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }

    // this function to check if email is verified
    private void checkIfEmailVerified(String email)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            Toast.makeText(Login2Activity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("users").child(email.replace(".","_")).child("myProfile").child("p").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*
                     *if email verified :
                     * Move the user to the appropriate page
                     */
                    String str1 = dataSnapshot.child("type").getValue(String.class);
                    if (str1.equals("Student")) {
                        Toast.makeText(Login2Activity.this, "signIn Successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login2Activity.this, StudentsMain.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                    }
                    else if(str1.equals("not Student"))
                    {
                        Toast.makeText(Login2Activity.this, "signIn Successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login2Activity.this, NotStudentActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();

                    }}
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }
        else
        {
            // email is not verified, so just prompt the message to the user
            sendVerificationEmail();
            //logout the user from firebase
            FirebaseAuth.getInstance().signOut();
            verifyEmail();

        }
    }

    // send email to verified the email
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                        else
                        {
                            Toast.makeText(Login2Activity.this,"sent",Toast.LENGTH_LONG).show();
                            //refresh the activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());


                        }
                    }
                });
    }
    ProgressDialog progressDialog;
    private void showProgressDialogWithTitle() {
        // dialog with title "Please Wait" and message "Preparing to download ..."
        progressDialog = new ProgressDialog(Login2Activity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }

//

    private void dataHuntler2() {
        String stEmail = etemailReq.getText().toString();
        String stPassw1 = etPasswordReq.getText().toString();
        String stPassw2 = etPassword2Req.getText().toString();
        String stFullName = etNameReq.getText().toString();
        String stPhone = etPhoneReq.getText().toString();
        /*
         The compile(String) method of the Pattern class in Java is used to create
          a pattern from the regular expression passed as parameter to method.
         */
        Pattern ps = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$");
        Matcher ms = ps.matcher(stEmail);

        boolean flag = true;
        if (ms.matches() == false) {
            etemailReq.setError("Wrong Email");
            flag = false;
        }
        if (stPassw1.length()< 10) {
            etPasswordReq.setError("Wrong Password");
        }
        if (stPassw2.length()< 10) {
            etPassword2Req.setError("Wrong re-password");
            flag = false;
        }
        if (!stPassw2.equals(stPassw1)) {
            etPassword2Req.setError("passwords are not equals");
            flag = false;
        }
        if (stFullName.length() == 0) {
            etNameReq.setError("Wrong Name");
            flag = false;
        }
        if (stPhone.length() == 0) {
            etPhoneReq.setError("Wrong Phone");
            flag = false;
        }

        /*
        if all is correct add the information to class "Profile"
        and call "createAccount" Function to make new user in firebase
         */
        if (flag) {
            p.setFullname(stFullName);
            p.setPhone(stPhone);
            p.setEmail(stEmail);
            p.setPassw(stPassw1);
            createAccount(stEmail, stPassw1,p);

        }
    }
    private FirebaseAuth.AuthStateListener authStateListener=new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            //4.
            FirebaseUser user=firebaseAuth.getCurrentUser();
            if(user!=null)
            {
                //user is signed in
                Toast.makeText(Login2Activity.this, "user is signed in.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //user signed out
                Toast.makeText(Login2Activity.this, "user signed out.", Toast.LENGTH_SHORT).show();

            }
        }
    };

    // function to make new user
    private void createAccount(final String email, String passw,Profile p) {
        auth.createUserWithEmailAndPassword(email, passw).addOnCompleteListener(Login2Activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    //save the information about user to database "firebase"
                    reference.child("users").child(email.replace(".", "_")).child("myProfile").child("p").setValue(p, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(getBaseContext(), "save ok", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "save Err" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                databaseError.toException().printStackTrace();
                            }
                        }
                    });
                    Toast.makeText(Login2Activity.this, "Authentication Successful.", Toast.LENGTH_SHORT).show();
                    if (p.getType().equals("Student")) {
                        /*
                        move the user from current Activity to SelectAcademyActivity
                        to choose the academy he learning in
                         */
                        Intent intent = new Intent(Login2Activity.this, SelectAcademyActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        //refresh the activity to verify the email
                        Intent intent = new Intent(Login2Activity.this, Login2Activity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(Login2Activity.this,"please change  your email",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        // get the string of selected in spinner
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(Login2Activity.this,item,Toast.LENGTH_LONG).show();
        if (item.equals("Student"))
            p.setType(item);
        else
            p.setType("not Student");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}