package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;


public class OnboardingFragment4  extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen4, container, false);
        ImageView imgCal1=root.findViewById(R.id.imgCal1);
        ImageView imgCal2=root.findViewById(R.id.imgCal2);

        imgCal1.setImageResource(R.drawable.calonboard1);
        imgCal2.setImageResource(R.drawable.calonboard2);


        return root;
    }
}