package com.example.students.ui2.AskToKnow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.students.R;
import com.example.students.data.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddPostActivity extends AppCompatActivity {
    private Button btnAddPost,btnAddImagePost;
    private EditText etPost;
    private ImageView imageView5;
    Post p=new Post();
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    static int num=1;

    //get value form data base

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        //connecting xml to code
        btnAddPost=(Button)findViewById(R.id.btnAddPost);
        btnAddImagePost=(Button)findViewById(R.id.btnAddImagePost);
        etPost=(EditText)findViewById(R.id.etPost);
        imageView5=(ImageView)findViewById(R.id.imageView5);
        //call function to get details about the user
        UserDetails();

        //get storage in database
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set the text of post in class
                p.setText(etPost.getText().toString());
                //call function to upload the image in database
                uploadImage();
            }
        });
        btnAddImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function to choose image of post
                SelectImage();
            }
        });

    }

    //function to get details about the user
    public void UserDetails() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").child("p").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name and image profile of user
                String stName = dataSnapshot.child("fullname").getValue(String.class);
                String imgId = dataSnapshot.child("img").child("image").getValue(String.class);
                //set the name and image to class
                p.setName(stName);
                p.setImgPro(imgId);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    // Select Image method
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView5.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


            // Defining the child of storageReference
            StorageReference ref = storageReference.child(email).child("Posts/"+(num++)+"");

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    //save url and id of post to database
                    p.setImgPost(taskSnapshot.getDownloadUrl().toString());
                    p.setId(reference.push().getKey());
                    //save the post to database
                    reference.child("Post").child(p.getId()).setValue(p);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded"+(int)progress+"%");
                            //progressDialog.setMessage(
                            // "Uploaded "
                            //    + (int)progress + "%");
                        }
                    });
        }
        else {
            //save the post to database without the image
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            p.setId(reference.push().getKey());
            reference.child("Post").child(p.getId()).setValue(p);
        }
    }
}