package com.example.students.ui2.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.R;
import com.example.students.UniversityMapsActivity;

public class MapFragment extends Fragment{

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        //if user not GUEST
        if (extras == null||!extras.getString("GUEST").equals("GUEST")){
            //move user from current activty to UniversityMapsActivity
            Intent myIntent = new Intent(MapFragment.this.getActivity(), UniversityMapsActivity.class);

            startActivity(myIntent);
        }
        //if user is GUEST
        else{
            Intent myIntent = new Intent(MapFragment.this.getActivity(), UniversityMapsActivity.class);
            /*
            move user from current activty to UniversityMapsActivity
            send value "GUEST" to know the user is guest in UniversityMapsActivity
             */

            myIntent.putExtra("GUEST","GUEST");

            startActivity(myIntent);
        }

        return root;

    }



}
