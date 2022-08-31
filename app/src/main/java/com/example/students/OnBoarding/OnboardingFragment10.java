package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;


public class OnboardingFragment10 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen10, container, false);
        ImageView imgmap1=root.findViewById(R.id.imgmap1);
        ImageView imgmap2=root.findViewById(R.id.imgmap2);
        ImageView imgmap3=root.findViewById(R.id.imgmap3);

        imgmap1.setImageResource(R.drawable.maponboard1);
        imgmap2.setImageResource(R.drawable.maponboard2);
        imgmap3.setImageResource(R.drawable.maponboard3);


        return root;
    }
}