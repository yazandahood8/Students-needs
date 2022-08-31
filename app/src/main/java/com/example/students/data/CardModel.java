package com.example.students.data;

public class CardModel {
    private String imageId;
    private String titleId;

    public CardModel (){

    }
    public CardModel(String imageId, String titleId) {
        this.imageId = imageId;
        this.titleId = titleId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getTitle() {
        return titleId;
    }


}