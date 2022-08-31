package com.example.students.data;

public class Profile extends location{
    private String fullname; //name of user
    private String email; //email of user
    private String phone; //phone number of user
    private String passw; // password for user
    private String type; // type of user (Student,not Student)
    private String Image; //url image profile for user

    public  Profile(String fullName,String email,String phone,String type){
        this.fullname=fullName;
        this.email=email;
        this.phone=phone;

        this.type=type;


    }
    public Profile(){}

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPassw() {
        return passw;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getImage() {
        return Image;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passw='" + passw + '\'' +
                '}';
    }
}

