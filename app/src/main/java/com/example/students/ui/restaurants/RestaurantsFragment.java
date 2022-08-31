package com.example.students.ui.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.R;
import com.example.students.ResturantsMapsActivity;

public class RestaurantsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_restaurants, container, false);
        //move the user from current activity to ResturantsMapsActivity
        //because this is fragment not activity
        Intent myIntent = new Intent(RestaurantsFragment.this.getActivity(), ResturantsMapsActivity.class);
        startActivity(myIntent);
        return root;
    }
}