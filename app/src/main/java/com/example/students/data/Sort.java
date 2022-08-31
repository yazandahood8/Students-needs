package com.example.students.data;

import android.widget.ImageView;
import android.widget.Space;
import android.widget.TableRow;
import android.widget.TextView;

public class Sort {
    private String name; //name of academy
    private double distance; //distance between academy and user

    private ImageView img; //image for academy
    private TextView tv; //name of academy
    private TableRow tableRow; //row
    private Space space; //space between academics

    public Sort(){}

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public void setTableRow(TableRow tableRow) {
        this.tableRow = tableRow;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public ImageView getImg() {
        return img;
    }

    public Space getSpace() {
        return space;
    }

    public TableRow getTableRow() {
        return tableRow;
    }

    public TextView getTv() {
        return tv;
    }
}
