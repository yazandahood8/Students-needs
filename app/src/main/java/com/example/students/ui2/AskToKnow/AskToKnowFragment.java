package com.example.students.ui2.AskToKnow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students.Adapter.MyAdapterPost;
import com.example.students.NotStudentActivity;
import com.example.students.R;
import com.example.students.data.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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


public class AskToKnowFragment extends Fragment {
    private ImageView imgMy;
    private TextView etQuestion;
    private RecyclerView PostsList;
    private List<Post> tagsList = new ArrayList<>();
    boolean flag=false;


    private MyAdapterPost myAdapterPost;


    public View onCreateView (@NonNull LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState){

            View root = inflater.inflate(R.layout.fragment_ask_to_know, container, false);
        //connecting xml to code

             imgMy=(ImageView)root.findViewById(R.id.imgMy);
            etQuestion=(TextView)root.findViewById(R.id.etQuestion);
            PostsList=(RecyclerView)root.findViewById(R.id.PostsList);
            PostsList.setHasFixedSize(true);
            //make RecyclerView VERTICAL
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            PostsList.setLayoutManager(linearLayoutManager);
            //add the apdater to RecyclerView
            myAdapterPost = new MyAdapterPost(tagsList, getContext());

        Bundle extras = getActivity().getIntent().getExtras();

        if (extras == null||!extras.getString("GUEST").equals("GUEST")){
        }  else {
            /*
            show dialog with title "Guest" and Message "Please Sign in"
            to remember the user to login
             */
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Guest");
            builder1.setMessage("Please Sign in\n you can ask what you want about subjects or academics");

            builder1.setCancelable(false);

            builder1.setNegativeButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //move the user from current activity to main activity
                            Intent myIntent = new Intent(AskToKnowFragment.this.getActivity(), NotStudentActivity.class);
                            myIntent.putExtra("GUEST","GUEST");
                            startActivity(myIntent);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }

            etQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //move the user from current activity to AddPostActivity
                    Intent myIntent = new Intent(AskToKnowFragment.this.getActivity(), AddPostActivity.class);
                    startActivity(myIntent);
                }
            });
        try {
            //call function to check if user have image\
            CheckImage();
            //call function to all posts from database
            Posts();
        }catch (Exception ex){}

            return root;

        }

        //function to check if user have image
    public void  CheckImage(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(email.replace(".","_")).child("myProfile").child("p").child("img").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stUrl = dataSnapshot.child("image").getValue(String.class);
                if (stUrl!=null)
                    //call function to show image of user in activity
                    img(stUrl);
                else {
                    //set default image
                    imgMy.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    //function to show image in activity
    public void img(String str) {
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference imagesRef = storageRef.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                imgMy.setImageBitmap(Bitmap.createScaledBitmap(bmp,130,
                        130, false));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(AskToKnowFragment.this.getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }

    //function to get all post from database
    public void Posts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Post").orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flag == false){
                    tagsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post != null) {
                        //add the post to RecyclerView
                        tagsList.add(0, post);
                        myAdapterPost.notifyDataSetChanged();
                        PostsList.setAdapter(myAdapterPost);
                    }

                }
                flag=true;
            }
                else {
                    //show snackbar if have new post allow to user make refresh the activity
                    Snackbar snackbar = Snackbar
                            .make(etQuestion," Have New Posts",Snackbar.LENGTH_INDEFINITE)
                            .setAction("Refresh", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    tagsList.clear();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Post post = ds.getValue(Post.class);
                                        if (post != null) {
                                            //add the posts to RecyclerView
                                            tagsList.add(0, post);
                                            myAdapterPost.notifyDataSetChanged();
                                            PostsList.setAdapter(myAdapterPost);
                                        }

                                    }
                                }
                            });

                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    }