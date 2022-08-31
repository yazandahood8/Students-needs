package com.example.students.data;

public class Post {
    private String name; //name of the user
    private String imgPro;//image of user
    private String text;// text of post
    private String imgPost; //image of post
    private String id; // id Post (Key)

    public Post(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getImgPost() {
        return imgPost;
    }

    public String getImgPro() {
        return imgPro;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImgPost(String imgPost) {
        this.imgPost = imgPost;
    }

    public void setImgPro(String imgPro) {
        this.imgPro = imgPro;
    }
}
