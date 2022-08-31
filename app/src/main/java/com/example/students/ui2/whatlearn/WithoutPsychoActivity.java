package com.example.students.ui2.whatlearn;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students.Adapter.CardsAdapter;
import com.example.students.R;
import com.example.students.data.CardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WithoutPsychoActivity extends AppCompatActivity {
    CardsAdapter adapter ;
    AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_without_psycho);
        //connecting xml to code
        ListView lvCards = (ListView) findViewById(R.id.lvCards);

        adapter  = new CardsAdapter(this);
        //add the adapter to listview
        lvCards.setAdapter(adapter);
        //call function to the subject without psychometric admission
        Subjects();
    }
    public void Subjects(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Subjects").child("Study without psychometrics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get url image of subject
                    String img=ds.child("info").child("img").getValue(String.class);
                    //set values to class
                    CardModel cardModel=new CardModel(img,ds.getKey());
                    //add the class to adapter
                    adapter.add(cardModel);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    }
