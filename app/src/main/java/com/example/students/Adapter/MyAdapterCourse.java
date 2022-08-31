package com.example.students.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.students.R;
import com.example.students.data.Courses;

public class MyAdapterCourse extends ArrayAdapter<Courses> {
    public MyAdapterCourse(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        //connecting xml to code
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.grade, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.textView10);
        TextView tvUnits = (TextView) convertView.findViewById(R.id.textView11);
        TextView tvGrade = (TextView) convertView.findViewById(R.id.textView12);

        //getting values by position item in listview
        final Courses courses = getItem(position);

        //set values
        tvName.setText(courses.getName());
        tvUnits.setText(courses.getUnits()+"");
        tvGrade.setText(courses.getGrade()+"");

        //make size text
        tvName.setTextSize(17);
        tvUnits.setTextSize(17);
        tvGrade.setTextSize(17);

        return convertView;
    }
}