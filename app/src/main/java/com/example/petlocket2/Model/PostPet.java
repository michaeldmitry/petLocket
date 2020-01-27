package com.example.petlocket2.Model;

import com.google.firebase.auth.FirebaseUser;

public class PostPet {
    private String postPetId;
    private String postPetAgeY;
    private String postPetAgeM;
    private String postPetBreed;
    private String postPetImage;
    private String postPetLicense;
    private String postPetCountry;
    private String postPetCity;
    private String postPetArea;
    private String postPetName;
    private String postPetPrice;
    private String postPetPublisher;
    private String postPetService;
    private String postPetSex;
    private Long postPetTimestamp;
    private String postPetActive;

    public PostPet() {
    }



    public PostPet(String postPetId, String postPetAgeY, String postPetAgeM , String postPetBreed, String postPetImage, String postPetLicense, String postPetCountry, String postPetCity, String postPetArea, String postPetName, String postPetPrice, String postPetPublisher, String postPetService, String postPetSex, Long postPetTimestamp, String postPetActive ) {
        this.postPetId = postPetId;
        this.postPetAgeY = postPetAgeY;
        this.postPetAgeM = postPetAgeM;
        this.postPetBreed = postPetBreed;
        this.postPetImage = postPetImage;
        this.postPetLicense = postPetLicense;
        this.postPetCountry = postPetCountry;
        this.postPetCity = postPetCity;
        this.postPetArea = postPetArea;
        this.postPetName = postPetName;
        this.postPetPrice = postPetPrice;
        this.postPetPublisher = postPetPublisher;
        this.postPetService = postPetService;
        this.postPetSex = postPetSex;
        this.postPetTimestamp = postPetTimestamp;
        this.postPetActive=postPetActive;
    }

    public String getPostPetActive() {
        return postPetActive;
    }

    public String getPostPetId() {
        return postPetId;
    }

    public void setPostPetId(String postPetId) {
        this.postPetId = postPetId;
    }

    public String getPostPetAgeY() {
        return postPetAgeY;
    }

    public void setPostPetAgeY(String postPetAgeY) {
        this.postPetAgeY = postPetAgeY;
    }

    public String getPostPetBreed() {
        return postPetBreed;
    }

    public void setPostPetBreed(String postPetBreed) {
        this.postPetBreed = postPetBreed;
    }

    public String getPostPetImage() {
        return postPetImage;
    }

    public void setPostPetImage(String postPetImage) {
        this.postPetImage = postPetImage;
    }

    public String getPostPetLicense() {
        return postPetLicense;
    }

    public void setPostPetLicense(String postPetLicense) {
        this.postPetLicense = postPetLicense;
    }

    public String getPostPetCountry() {
        return postPetCountry;
    }

    public void setPostPetCountry(String postPetCountry) {
        this.postPetCountry = postPetCountry;
    }
    public String getPostPetCity() {
        return postPetCity;
    }

    public void setPostPetCity(String postPetCity) {
        this.postPetCity = postPetCity;
    }
    public String getPostPetArea() {
        return postPetArea;
    }

    public void setPostPetArea(String postPetArea) {
        this.postPetArea = postPetArea;
    }

    public String getPostPetName() {
        return postPetName;
    }

    public void setPostPetName(String postPetName) {
        this.postPetName = postPetName;
    }

    public String getPostPetPrice() {
        return postPetPrice;
    }

    public void setPostPetPrice(String postPetPrice) {
        this.postPetPrice = postPetPrice;
    }

    public String getPostPetPublisher() {
        return postPetPublisher;
    }

    public void setPostPetPublisher(String postPetPublisher) {
        this.postPetPublisher = postPetPublisher;
    }

    public String getPostPetService() {
        return postPetService;
    }

    public void setPostPetService(String postPetService) {
        this.postPetService = postPetService;
    }

    public String getPostPetSex() {
        return postPetSex;
    }

    public void setPostPetSex(String postPetSex) {
        this.postPetSex = postPetSex;
    }

    public Long getPostPetTimestamp() {
        return postPetTimestamp;
    }

    public void setPostPetTimestamp(Long postPetTimestamp) {
        this.postPetTimestamp = postPetTimestamp;
    }
    public void setPostPetAgeM(String postPetAgeM) {
        this.postPetAgeM = postPetAgeM;
    }

    public String getPostPetAgeM() {
        return postPetAgeM;
    }
}
