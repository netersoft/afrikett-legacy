package com.neteru.afrikett.core.models.RemoteDB;

import com.neteru.afrikett.core.utilities.AppUtilities;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.UNSPECIFIED;

@SuppressWarnings("unused")
public class User {

    private String id;
    private String name;
    private String nationalNumber;
    private String countryCode;
    private String number;
    private String country;
    private String email;
    private String registrationDate;
    private String cryptoKey;
    private String profileUrl;
    private String watcher;
    private String lastConnection;
    private String connectedWith;
    private String bio;
    private String birthday;
    private String appVersionName;
    private Integer nbSubscriptions;
    private Integer nbShowcases;
    private Integer status;
    private Integer sex;
    private Integer appVersionCode;
    private List<String> subscriptions;
    private Location location;
    private Boolean pass;

    public User(){}

    public User(String id, String name, String nationalNumber, String countryCode, String number, String country, String email, String connectedWith){
        this.id = id;
        this.name = name;
        this.nationalNumber = nationalNumber;
        this.countryCode = countryCode;
        this.number = number;
        this.country = country;
        this.email = email;
        this.registrationDate = AppUtilities.getCurrentDate();
        this.lastConnection = AppUtilities.getCurrentDate();
        this.cryptoKey = AppUtilities.getCryptKey();
        this.profileUrl = DEFAULT;
        this.pass = true;
        this.status = 0;
        this.nbShowcases = 0;
        this.nbSubscriptions = 0;
        this.subscriptions = new ArrayList<>();
        this.watcher = DEFAULT;
        this.connectedWith = connectedWith;
        this.location = new Location();
        this.bio = EMPTY;
        this.sex = UNSPECIFIED;
        this.birthday = EMPTY;
    }

    /*** GETTERS ***/

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNationalNumber() {
        return nationalNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getNumber() {
        return number;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getCryptoKey() {
        return cryptoKey;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public Boolean getPass() {
        return pass;
    }

    public Integer getNbShowcases() {
        return nbShowcases;
    }

    public Integer getNbSubscriptions() {
        return nbSubscriptions;
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }

    public String getWatcher() {
        return watcher;
    }

    public String getLastConnection() {
        return lastConnection;
    }

    public Integer getStatus() {
        return status;
    }

    public String getConnectedWith() {
        return connectedWith;
    }

    public String getBio() {
        return bio;
    }

    public Location getLocation() {
        return location;
    }

    public String getBirthday() {
        return birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    /*** SETTERS ***/

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNationalNumber(String nationalNumber) {
        this.nationalNumber = nationalNumber;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setPass(Boolean pass) {
        this.pass = pass;
    }

    public void setNbShowcases(Integer nbShowcases) {
        this.nbShowcases = nbShowcases;
    }

    public void setNbSubscriptions(Integer nbSubscriptions) {
        this.nbSubscriptions = nbSubscriptions;
    }

    public void setSubscriptions(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void setWatcher(String watcher) {
        this.watcher = watcher;
    }

    public void setLastConnection(String lastConnection) {
        this.lastConnection = lastConnection;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setConnectedWith(String connectedWith) {
        this.connectedWith = connectedWith;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public void setAppVersionCode(Integer appVersionCode) {
        this.appVersionCode = appVersionCode;
    }
}
