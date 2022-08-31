package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;

public class OnboardingFragment8 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen8, container, false);
        ImageView imgnotSt=root.findViewById(R.id.imgnotSt);
        imgnotSt.setImageResource(R.drawable.notstudentonboard);


        return root;
    }
}