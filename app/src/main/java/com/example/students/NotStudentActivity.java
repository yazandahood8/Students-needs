package com.example.students;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NotStudentActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView HeadimgProf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_student);
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            //check if user not Guest
            if (extras.getString("GUEST") != null || !extras.getString("GUEST").equals("GUEST")) {

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                DrawerLayout drawer = findViewById(R.id.drawer_layout1);
                NavigationView navigationView = findViewById(R.id.nav_view1);
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.

                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home2, R.id.nav_map, R.id.nav_profile2, R.id.nav_whatlearn,R.id.nav_asktoknow)
                        .setDrawerLayout(drawer)
                        .build();
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment1);
                NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
            }
        }
            else{
                //call function to get user details
            UserDetails();

            Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                DrawerLayout drawer = findViewById(R.id.drawer_layout1);
                NavigationView navigationView = findViewById(R.id.nav_view1);
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.

            View headerView = navigationView.getHeaderView(0);


            HeadimgProf = (ImageView) headerView.findViewById(R.id.HeadimgProf);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home2, R.id.nav_map, R.id.nav_profile2, R.id.nav_whatlearn,R.id.nav_asktoknow)
                        .setDrawerLayout(drawer)
                        .build();
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment1);
                NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);


        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment1);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //function to get user details
    public void UserDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //get the data for user from firebase
        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_")).child("myProfile").child("p").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name the user from firebase from child the key is "fullname"
                final String fullname = dataSnapshot.child("fullname").getValue(String.class);

                //get email the user from firebase from child the key is "email"
                final String email = dataSnapshot.child("email").getValue(String.class);

                // connect navigationView xml to the code
                NavigationView navigationView = findViewById(R.id.nav_view1);

                // connect headerView xml to the code
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
                TextView navEmail = (TextView) headerView.findViewById(R.id.navEmail);

                // show the name and email for the user in headerView
                navUsername.setText(fullname);
                navEmail.setText(email);

                //call function to check if the user have image for profile
                imageProfile();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    public void  imageProfile(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").child("p").child("img").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stUrl = dataSnapshot.child("image").getValue(String.class);
                if (stUrl!=null) {
                    //call function to show the image
                    img(stUrl);

                }
                else {
                    // if the user don't have image make the application icon the default image
                    HeadimgProf.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    public void img(String str) {
        //FirebaseStorage is a service that supports uploading and downloading large objects to Google Cloud Storage
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        // Create a reference with an initial file path and name
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                try {
                    HeadimgProf.setImageBitmap(Bitmap.createScaledBitmap(bmp,HeadimgProf.getWidth(),
                            HeadimgProf.getHeight(), false));
                }catch (Exception ex){}

                // progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                //  progressDialog.dismiss();
                Toast.makeText(NotStudentActivity.this,exception.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        //if press back show alert to submit the exit from application
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NotStudentActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}