package com.example.students;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswActivity extends AppCompatActivity {
    private EditText etResetEmail;
    private Button btnSubmitReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passw);

        //connecting xml to code
        etResetEmail=(EditText)findViewById(R.id.etResetEmail);
        btnSubmitReset=(Button)findViewById(R.id.btnSubmitReset);

        btnSubmitReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //call function to check input validity
                dataHandler();
            }
        });
    }
    ProgressDialog progressDialog;
    //function to show dialog in activity
    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(ResetPasswActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Preparing to download ...");
        progressDialog.show();
    }
    private void dataHandler() {
        //1.getting data
        String stEmail = etResetEmail.getText().toString();
        boolean isok = true;
        //2.checking
        if (stEmail.length() < 3) {
            etResetEmail.setError("Wrong Email");
            isok = false;
        }

        if (isok == true) {
            //call function to show dialog in activity

            showProgressDialogWithTitle();
            //call function to reset password
            sendPasswordReset(stEmail);
        }
    }
    public void sendPasswordReset(String email) {
        // [START send_password_reset]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email;
        //send email to reset password
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswActivity.this,"sent",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(ResetPasswActivity.this,"can't find this email make sure from the email",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // [END send_password_reset]
    }
}