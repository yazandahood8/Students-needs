package com.example.students.data;

public class Academy extends location{
    private String name;
    private int diploma;

    public Academy(){}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getDiploma() {
        return diploma;
    }

    public void setDiploma(int diploma) {
        this.diploma = diploma;
    }

}
