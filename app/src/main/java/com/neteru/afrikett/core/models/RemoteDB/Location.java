package com.neteru.afrikett.core.models.RemoteDB;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class Location {

    private String address;
    private String postalCode;
    private Double latitude;
    private Double longitude;

    public Location(){ }

    public Location(String address, Double latitude, Double longitude, @Nullable String postalCode){

        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
