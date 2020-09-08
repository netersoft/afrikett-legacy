package com.neteru.afrikett.core.models.RemoteDB;

import com.neteru.afrikett.core.utilities.Timing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

@SuppressWarnings("unused")
public class Post {

    private String id;
    private String showcaseId;
    private String authorId;
    private Integer type;
    private String postDate;
    private String notation;
    private Map<String, Float> assessors;
    protected String productOrServiceName;
    protected String productOrServiceDescription;
    protected String productOrServicePrice;
    protected Boolean productOrServiceAvailability;
    protected String eventName;
    protected String eventLocation;
    protected String eventDate;
    protected String eventHour;
    protected String eventAbout;
    protected String newsTitle;
    protected String newsContent;
    protected ArrayList<String> previews;

    public Post(){}

    public Post(String id, String showcaseId, String authorId, int type){

        this.id = id;
        this.showcaseId = showcaseId;
        this.type = type;
        this.postDate = Timing.getCurrentDate();
        this.assessors = new HashMap<>();
        this.notation = EMPTY;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShowcaseId() {
        return showcaseId;
    }

    public void setShowcaseId(String showcaseId) {
        this.showcaseId = showcaseId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getProductOrServiceName() {
        return productOrServiceName;
    }

    public void setProductOrServiceName(String productOrServiceName) {
        this.productOrServiceName = productOrServiceName;
    }

    public String getProductOrServiceDescription() {
        return productOrServiceDescription;
    }

    public void setProductOrServiceDescription(String productOrServiceDescription) {
        this.productOrServiceDescription = productOrServiceDescription;
    }

    public String getProductOrServicePrice() {
        return productOrServicePrice;
    }

    public void setProductOrServicePrice(String productOrServicePrice) {
        this.productOrServicePrice = productOrServicePrice;
    }

    public Boolean getProductOrServiceAvailability() {
        return productOrServiceAvailability;
    }

    public void setProductOrServiceAvailability(Boolean productOrServiceAvailability) {
        this.productOrServiceAvailability = productOrServiceAvailability;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventHour() {
        return eventHour;
    }

    public void setEventHour(String eventHour) {
        this.eventHour = eventHour;
    }

    public String getEventAbout() {
        return eventAbout;
    }

    public void setEventAbout(String eventAbout) {
        this.eventAbout = eventAbout;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public ArrayList<String> getPreviews() {
        return previews;
    }

    public void setPreviews(ArrayList<String> previews) {
        this.previews = previews;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public Map<String, Float> getAssessors() {
        return assessors;
    }

    public void setAssessors(Map<String, Float> assessors) {
        this.assessors = assessors;
    }

}
