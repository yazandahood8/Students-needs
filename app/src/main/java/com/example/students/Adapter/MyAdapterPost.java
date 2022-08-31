package com.example.students.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students.R;
import com.example.students.data.Post;
import com.example.students.ui2.AskToKnow.CommentsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterPost  extends RecyclerView.Adapter<MyAdapterPost.PostHandel> {

    private List<Post> mTagList= new ArrayList<>();
    private Context mContext;

    public MyAdapterPost(List<Post> mTagList, Context mContext) {
        this.mTagList = mTagList;
        this.mContext = mContext;
    }


    @Override
    public PostHandel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent,false);
        PostHandel pvh = new PostHandel(view);
        return pvh;
    }


    @Override
    public void onBindViewHolder(@NonNull MyAdapterPost.PostHandel holder, int position) {
        //set values to class by position in list view
        final Post post = mTagList.get(position);

        //set values
        holder.tvName.setText(post.getName());
        holder.tvText.setText(post.getText());

        //check if user have image profile
        if (post.getImgPro()!=null&&!post.getImgPro().equals(""))

            //call function to show the image
             img(post.getImgPro(),holder.imgprofile,130,130);

        //check if post have image
        if (post.getImgPost()!=null&&!post.getImgPost().equals(""))

            //call function to show the image
            img(post.getImgPost(),holder.imgPost,900,500);


        holder.btnViewCommets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                move user from current activity to CommentsActivity
                to show the comments about current post
                **send id post to CommentsActivity
                 */
                Intent i=new Intent(mContext, CommentsActivity.class);
                i.putExtra("name", post.getId());
                mContext.startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mTagList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(mContext,exception.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public class PostHandel extends RecyclerView.ViewHolder{
        private TextView tvName, tvText;//name user,text of post
        private ImageView imgprofile,imgPost; //image profile , image of post
        private Button btnViewCommets;//button to view the comments

        public PostHandel(@NonNull View itemView) {
            super(itemView);
            //connecting xml to code
             imgprofile=(ImageView)itemView.findViewById(R.id.imageView3);
             tvName = (TextView) itemView.findViewById(R.id.textView18);
             tvText = (TextView) itemView.findViewById(R.id.textView19);
             imgPost=(ImageView)itemView.findViewById(R.id.imageView4);
             btnViewCommets=(Button)itemView.findViewById(R.id.btnViewCommets);
        }
    }
}