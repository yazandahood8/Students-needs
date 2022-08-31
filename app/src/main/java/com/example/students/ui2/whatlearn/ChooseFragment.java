package com.example.students.ui2.whatlearn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.ChooseSubjectActivity;
import com.example.students.NotStudentActivity;
import com.example.students.R;
import com.example.students.ui2.StudyOfferActivity;

public class ChooseFragment extends Fragment {
    private Button btnCheckAcceptable,btnoStudyOffer,btnStuWithoutPsy;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose, container, false);


        //connecting xml to code
        btnCheckAcceptable=(Button)root.findViewById(R.id.btnCheckAcceptable) ;
        btnoStudyOffer=(Button)root.findViewById(R.id.btnoStudyOffer) ;
        btnStuWithoutPsy=(Button)root.findViewById(R.id.btnStuWithoutPsy) ;

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null||!extras.getString("GUEST").equals("GUEST")){
        }  else {
            /*
            show dialog with title "Guest" and Message "Please Sign in"
            to remember the user to login
             */
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Guest");
            builder1.setMessage("Please Sign in\n you can check acceptable to subject or get offer about subjects");

            builder1.setCancelable(false);

            builder1.setNegativeButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //move the user from current activity to main activity
                            Intent myIntent = new Intent(ChooseFragment.this.getActivity(), NotStudentActivity.class);
                            myIntent.putExtra("GUEST","GUEST");
                            startActivity(myIntent);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }



            btnCheckAcceptable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move user from current activity to ChooseSubjectActivity
                Intent intent = new Intent(getContext(), ChooseSubjectActivity.class);
                startActivity(intent);
            }
        });
        btnoStudyOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move user from current activity to StudyOfferActivity

                Intent intent = new Intent(getContext(), StudyOfferActivity.class);
                startActivity(intent);

            }
        });
        btnStuWithoutPsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move user from current activity to WithoutPsychoActivity

                Intent intent = new Intent(getContext(), WithoutPsychoActivity.class);
                startActivity(intent);

            }
        });
        return root;
    }
}