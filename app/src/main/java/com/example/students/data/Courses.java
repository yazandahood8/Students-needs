package com.example.students.data;

public class Courses {
    private String name;//name of course
    private int units;//units for course
    private int grade;//grade for course
public Courses(){};
    public Courses(String name,int units,int grade){
        this.name=name;
        this.units=units;
        this.grade=grade;

    }


        public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
