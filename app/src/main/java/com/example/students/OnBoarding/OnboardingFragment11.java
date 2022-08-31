package com.example.students.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.students.R;


public class OnboardingFragment11 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onboarding_screen11, container, false);
        ImageView imgcheck1=root.findViewById(R.id.imgcheck1);
        ImageView imgcheck2=root.findViewById(R.id.imgcheck2);
        ImageView imgcheck3=root.findViewById(R.id.imgcheck3);

        imgcheck1.setImageResource(R.drawable.checkonboard1);
        imgcheck2.setImageResource(R.drawable.checkonboard2);
        imgcheck3.setImageResource(R.drawable.checkonboard3);


        return root;
    }
}