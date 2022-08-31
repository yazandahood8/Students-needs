package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;


public class OnboardingFragment12 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen12, container, false);
        ImageView imgoffer1=root.findViewById(R.id.imgoffer1);
        ImageView imgoffer2=root.findViewById(R.id.imgoffer2);

        imgoffer1.setImageResource(R.drawable.offeronboard1);
        imgoffer2.setImageResource(R.drawable.offeronboard2);


        return root;
    }
}