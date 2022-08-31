package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;


public class OnboardingFragment6 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen6, container, false);
        ImageView imgres1=root.findViewById(R.id.imgres1);
        ImageView imgres2=root.findViewById(R.id.imgres2);

        imgres1.setImageResource(R.drawable.resonboard1);
        imgres2.setImageResource(R.drawable.resonboard2);


        return root;
    }
}