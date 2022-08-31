package com.example.students.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.students.R;
import com.example.students.data.CardModel;
import com.example.students.ui2.whatlearn.AcademicsWithoutPsycho;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CardsAdapter extends ArrayAdapter<CardModel> {
    public CardsAdapter(Context context) {
        super(context, R.layout.carditem);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            /*
            Instantiates a layout XML file into its corresponding View objects.
            It is never used directly.
             */
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //getting xml file
            convertView = inflater.inflate(R.layout.carditem, parent, false);

            //set xml file
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CardModel model = getItem(position);

        //call function to show image
        img(model.getImageId(),holder.imageView,300,300);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call function to change the image profile
                Intent i=new Intent(getContext(), AcademicsWithoutPsycho.class);
                i.putExtra("name",model.getTitle());
             //  Toast.makeText(getContext(),model.getTitle(),Toast.LENGTH_LONG).show();
                getContext().startActivity(i);
            }
        });


        //set name subject
        holder.tvTitle.setText(model.getTitle());

        return convertView;
    }

    //show image in activity
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
                // progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    static class ViewHolder {
        ImageView imageView;//image of subject
        TextView tvTitle;//name of subject

        ViewHolder(View view) {
            //connecting xml to code
            imageView = (ImageView) view.findViewById(R.id.imgCard);
            tvTitle = (TextView) view.findViewById(R.id.titleCard);
        }
    }
}
