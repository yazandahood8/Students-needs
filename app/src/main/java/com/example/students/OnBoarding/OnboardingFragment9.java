package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;


public class OnboardingFragment9 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen9, container, false);
        ImageView imgho1=root.findViewById(R.id.imgho1);
        ImageView imgho2=root.findViewById(R.id.imgho2);

        imgho1.setImageResource(R.drawable.hoboard1);
        imgho2.setImageResource(R.drawable.hoboard2);


        return root;
    }
}