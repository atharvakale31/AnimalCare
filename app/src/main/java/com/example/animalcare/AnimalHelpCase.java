package com.example.animalcare;

public class AnimalHelpCase {

    private String userName;
    private String animalType,cityType;
    private String userLocation;
    private String photourl;
    private double latitude;
    private double longitude;


    public AnimalHelpCase( String userName, String animalType, String cityType,String userLocation, double latitude, double longitude,String photourl) {
        this.animalType = animalType;
        this.cityType = cityType;
        this.userName = userName;
        this.userLocation = userLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photourl=photourl;
    }

    public String getUserName() {
        return userName;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getcityType() {
        return cityType;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getPhotourl() {
        return photourl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
