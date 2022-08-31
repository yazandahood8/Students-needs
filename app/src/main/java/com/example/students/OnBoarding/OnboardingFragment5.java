package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;


public class OnboardingFragment5 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen5, container, false);
        ImageView imgacademic1=root.findViewById(R.id.imgacademic1);
        ImageView imgacademic2=root.findViewById(R.id.imgacademic2);
            imgacademic1.setImageResource(R.drawable.acadonboard1);
            imgacademic2.setImageResource(R.drawable.acadonboard2);
        return root;
    }
}