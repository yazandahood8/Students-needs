package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;

public class OnboardingFragment2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen2, container, false);
        ImageView imgSt=root.findViewById(R.id.imgSt);
        imgSt.setImageResource(R.drawable.studentonboard);


        return root;
    }
}