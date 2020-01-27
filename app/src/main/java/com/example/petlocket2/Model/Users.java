package com.example.petlocket2.Model;

public class Users {
    private String id;
    private String fullname;
    private String username;
    private String phone;
    private String imageUrl;
    private String country;
    private String city;
    private String area;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }






    public Users(String id, String fullname, String username, String phone, String imageUrl, String country, String city, String area) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.phone = phone;
        this.imageUrl=imageUrl;
        this.city=city;
        this.country=country;
        this.area=area;
    }

    public Users() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
