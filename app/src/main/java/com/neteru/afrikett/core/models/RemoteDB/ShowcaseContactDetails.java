package com.neteru.afrikett.core.models.RemoteDB;

@SuppressWarnings("unused")
public class ShowcaseContactDetails {

    private String email;
    private String number;
    private String website;
    private Location location;

    public ShowcaseContactDetails(){}

    public ShowcaseContactDetails(String email, String number, String website, Location location){
        this.email = email;
        this.number = number;
        this.website = website;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public Location getLocation() {
        return location;
    }

    public String getNumber() {
        return number;
    }

    public String getWebsite() {
        return website;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}
