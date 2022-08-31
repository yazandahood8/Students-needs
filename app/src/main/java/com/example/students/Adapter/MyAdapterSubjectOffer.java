package com.example.students.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students.R;
import com.example.students.data.Subject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterSubjectOffer  extends RecyclerView.Adapter<MyAdapterSubjectOffer.ItemViewHolder> {

    private List<Subject> mTagList= new ArrayList<>();
    private List<String> Academics = new ArrayList<String>();
   private ArrayAdapter arrayAdapter ;

    private Context mContext;

    public MyAdapterSubjectOffer(List<Subject> mTagList, Context mContext) {
        this.mTagList = mTagList;
        this.mContext = mContext;
        this.arrayAdapter = new ArrayAdapter<String>(
                mContext, // Context
                android.R.layout.simple_list_item_single_choice, // Layout
                Academics // List
        );
    }
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //getting layout from xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subjectoffer, parent,false);
        ItemViewHolder pvh = new ItemViewHolder(view);
        return pvh;
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapterSubjectOffer.ItemViewHolder holder, int position) {
        //set values to class by position in list view
        final Subject subject = mTagList.get(position);

        //set values
        holder.tvName.setText(subject.getName());
        holder.tv2.setText("Type: "+subject.getType());

        if (subject.getTop1()!=null)
            //set holland code for subject
            holder.tv1.setText("Holland Code: "+subject.getTop1());

        //change Background Color according to position (for design)
        if (position%2==1)
             holder.subOffer.setBackgroundColor(Color.parseColor("#41A592"));
        else
             holder.subOffer.setBackgroundColor(Color.parseColor("#DAE8FC"));

        //call function to show image of subject
        img(subject.getImg(),holder.image,350,250);

    }


    @Override
    public int getItemCount() {
        return mTagList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName, tv1,tv2;
        private ImageView     image;
        private View subOffer;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            subOffer = itemView.findViewById(R.id.subOffer);

            tvName = itemView.findViewById(R.id.textView15);
            tv1 = itemView.findViewById(R.id.textView16);
            tv2 = itemView.findViewById(R.id.textView17);
            image = itemView.findViewById(R.id.imageView2);
        }
    }


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
                // progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                //   progressDialog.dismiss();
                Toast.makeText(mContext,exception.toString(),Toast.LENGTH_LONG).show();
                //  holder.progressBar.setVisibility(View.GONE);
            }
        });
    }
}
//}