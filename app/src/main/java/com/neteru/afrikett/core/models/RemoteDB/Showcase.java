package com.neteru.afrikett.core.models.RemoteDB;

import com.neteru.afrikett.core.utilities.AppUtilities;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Showcase {

    private String id;
    private String name;
    private String description;
    private String banner;
    private String logo;
    private String registrationDate;
    private String primaryColor;
    private String secondaryColor;
    private String openingHours;
    private ShowcaseContactDetails showcaseContactDetails;
    private List<String> owners;
    private List<String> subscribers;
    private Integer nb_subscribers;
    private Integer field;

    public Showcase(){}

    public Showcase(String id, String name, String description, Integer field,
                    String banner, String logo, ShowcaseContactDetails showcaseContactDetails, List<String> owners, String color_1, String color_2){
        this.id = id;
        this.name = name;
        this.description = description;
        this.field = field;
        this.banner = banner;
        this.logo = logo;
        this.showcaseContactDetails = showcaseContactDetails;
        this.owners = owners;
        this.subscribers = new ArrayList<>();
        this.registrationDate = AppUtilities.getCurrentDate();
        this.nb_subscribers = 0;
        this.primaryColor = color_1;
        this.secondaryColor = color_2;
    }

    /** GETTERS **/

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getField() {
        return field;
    }

    public String getBanner() {
        return banner;
    }

    public String getLogo() {
        return logo;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public ShowcaseContactDetails getShowcaseContactDetails() {
        return showcaseContactDetails;
    }

    public List<String> getOwners() {
        return owners;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public Integer getNb_subscribers() {
        return nb_subscribers;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    /** SETTERS **/

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setField(Integer field) {
        this.field = field;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setShowcaseContactDetails(ShowcaseContactDetails showcaseContactDetails) {
        this.showcaseContactDetails = showcaseContactDetails;
    }

    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public void setNb_subscribers(Integer nb_subscribers) {
        this.nb_subscribers = nb_subscribers;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

}
