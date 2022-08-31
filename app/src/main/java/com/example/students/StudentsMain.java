package com.example.students;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

public class StudentsMain extends AppCompatActivity {
    private NavigationView navigationView;
    private ImageView HeadimgProf;

    private AppBarConfiguration mAppBarConfiguration;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_main);
        //call function to get user details
        UserDetails();

        //call function to get profile image
        imageProfile();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

         HeadimgProf = (ImageView) headerView.findViewById(R.id.HeadimgProf);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_restaurants,R.id.nav_profile,R.id.nav_average,R.id.nav_asktoknow,R.id.nav_chat)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    //function to get user details
    public void UserDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser() .getEmail().replace(".","_")).child("myProfile").child("p").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name the user from firebase from child the key is "fullname"
                final String fullname = dataSnapshot.child("fullname").getValue(String.class);

                //get email the user from firebase from child the key is "email"
                final String email = dataSnapshot.child("email").getValue(String.class);

                // connect navigationView xml to the code
                NavigationView navigationView = findViewById(R.id.nav_view);

                // connect headerView xml to the code
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
                TextView navEmail = (TextView) headerView.findViewById(R.id.navEmail);

                // show the name and email for the user in headerView
                navUsername.setText(fullname);
                navEmail.setText(email);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //function to get profile image
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
                    HeadimgProf.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground));
                 //   progressDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    //function to show the image
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

                HeadimgProf.setImageBitmap(Bitmap.createScaledBitmap(bmp,HeadimgProf.getWidth(),
                        HeadimgProf.getHeight(), false));
              // progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            //  progressDialog.dismiss();
                Toast.makeText(StudentsMain.this,exception.toString(),Toast.LENGTH_LONG).show();
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
                        StudentsMain.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}