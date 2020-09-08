package com.neteru.afrikett.core.models.RemoteDB;

@SuppressWarnings("unused")
public class LocalContactModel {

    private String name;
    private String phoneNumber;

    public LocalContactModel(String n, String p){
        this.name = n;
        this.phoneNumber = p;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
