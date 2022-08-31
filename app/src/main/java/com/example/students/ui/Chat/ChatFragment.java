package com.example.students.ui.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.students.ChatActivity;
import com.example.students.R;

public class ChatFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        //move the user from current activity to ChatActivity
        //because this is fragment not activity
        Intent myIntent = new Intent(ChatFragment.this.getActivity(), ChatActivity.class);

        startActivity(myIntent);
        return root;
    }


}