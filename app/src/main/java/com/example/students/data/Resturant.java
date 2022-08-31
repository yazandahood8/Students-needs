package com.example.students.data;

public class Resturant extends location {
    private String imgID; //image url for Restaurant
    private String name; // name of Restaurant
    private String nearby; //academy nearby

    public Resturant(){};

    public Resturant(String name,String nearby,double lat,double lng){
        super(lat,lng);
        this.name=name;
        this.nearby=nearby;
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNearby(String nearby) {
        this.nearby = nearby;
    }

    public String getNearby() {
        return nearby;
    }



    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }

    @Override
    public void setLat(double lat) {
        super.setLat(lat);
    }

    @Override
    public double getLat() {
        return super.getLat();
    }

    @Override
    public void setLng(double lng) {
        super.setLng(lng);
    }

    @Override
    public double getLng() {
        return super.getLng();
    }
}
