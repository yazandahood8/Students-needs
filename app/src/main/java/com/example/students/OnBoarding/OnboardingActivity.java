package com.example.students.OnBoarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.students.Login2Activity;
import com.example.students.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class OnboardingActivity extends FragmentActivity {

    private ViewPager pager;
    private SmartTabLayout indicator;
    private Button skip;
    private Button next,btntype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);
        //connecting xml to code
        pager = (ViewPager)findViewById(R.id.pager);
        indicator = (SmartTabLayout)findViewById(R.id.indicator);
        skip = (Button)findViewById(R.id.skip);
        next = (Button)findViewById(R.id.next);
        btntype = (Button)findViewById(R.id.btntype);

        //function return  fragment by his position to activity
        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0 : return new OnboardingFragment1();
                    case 1 : return new OnboardingFragment2();
                    case 2 : return new OnboardingFragment3();
                    case 3 : return new OnboardingFragment4();
                    case 4 : return new OnboardingFragment5();
                    case 5 : return new OnboardingFragment6();
                    case 6 : return new OnboardingFragment7();
                    case 7 : return new OnboardingFragment8();
                    case 8 : return new OnboardingFragment9();
                    case 9 : return new OnboardingFragment10();
                    case 10 : return new OnboardingFragment11();
                    case 11 : return new OnboardingFragment12();

                    default: return null;
                }
            }
            //count of fragments in activity
            @Override
            public int getCount() {
                return 12;
            }
        };

        pager.setAdapter(adapter);

        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position>6)
                    btntype.setText("Go to Student");
                else
                    btntype.setText("Go to not Student");

                if(position == 11){
                    btntype.setVisibility(View.GONE);

                    skip.setVisibility(View.GONE);
                    next.setText("Done");
                } else {
                    skip.setVisibility(View.VISIBLE);
                    btntype.setVisibility(View.VISIBLE);

                    next.setText("Next");
                }
            }

        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });
        btntype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btntype.getText().toString().equals("Go to not Student")) {
                    pager.setCurrentItem(7, true);
                    btntype.setText("Go to Student");
                }
                else{
                    pager.setCurrentItem(1, true);
                    btntype.setText("Go to not Student");

                }

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 11){
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    if (pager.getCurrentItem()>6)
                        btntype.setText("Go to Student");
                    else
                        btntype.setText("Go to not Student");

                }
            }
        });
    }
    private void finishOnboarding() {
        SharedPreferences preferences =
                getSharedPreferences("my_preferences", MODE_PRIVATE);

        preferences.edit()
                .putBoolean("onboarding_complete",true).apply();

        Intent main = new Intent(this, Login2Activity.class);
        startActivity(main);

        finish();
    }
}