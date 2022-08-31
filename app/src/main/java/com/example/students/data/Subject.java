package com.example.students.data;

public class Subject {
    private String Academic; //The academy that teaches the subject
    private String name; // name of subject
    private double sekem; //sekhem admission
    private int psycho; //psychometric admission
    private int math; //mathematics admission
    private int psychokmote; //psychometric of math admission
    private double diploma; // high school diploma admission
    private String Type;//type of subject
    private String img; // image of subject
    private String top1; //holland code



    public Subject(){}

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTop1() {
        return top1;
    }


    public void setTop1(String top1) {
        this.top1 = top1;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcademic() {
        return Academic;
    }

    public void setAcademic(String academic) {
        Academic = academic;
    }

    public double getDiploma() {
        return diploma;
    }

    public void setDiploma(double diploma) {
        this.diploma = diploma;
    }

    public double getSekem() {
        return sekem;
    }

    public void setSekem(double sekem) {
        this.sekem = sekem;
    }

    public int getPsycho() {
        return psycho;
    }

    public void setPsycho(int psycho) {
        this.psycho = psycho;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getMath() {
        return math;
    }

    public void setMath(int math) {
        this.math = math;
    }

    public int getPsychokmote() {
        return psychokmote;
    }

    public void setPsychokmote(int psychokmote) {
        this.psychokmote = psychokmote;
    }
}
