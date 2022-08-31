package com.example.students.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.Login2Activity;
import com.example.students.R;
import com.example.students.data.ChatService;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_logout, container, false);

        Intent s = new Intent(getContext(), ChatService.class);
        getActivity().startService(s);


        //signout from the database and from the application
        FirebaseAuth.getInstance().signOut();
        //move the user from current activity to LoginActivity
        Intent myIntent = new Intent(LogoutFragment.this.getActivity(), Login2Activity.class);
        startActivity(myIntent);

        getActivity().finish();

        return root;
    }
}
