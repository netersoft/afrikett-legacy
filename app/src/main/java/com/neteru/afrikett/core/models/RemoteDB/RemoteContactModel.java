package com.neteru.afrikett.core.models.RemoteDB;

@SuppressWarnings("unused")
public class RemoteContactModel {

    private String id;
    private String name;
    private String phoneNumber;

    public RemoteContactModel(){}

    public RemoteContactModel(String id, String name, String phoneNumber){
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
