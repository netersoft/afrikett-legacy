package com.neteru.afrikett.core.models.RemoteDB;

import com.neteru.afrikett.core.utilities.Timing;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class Story implements Serializable {

    private String id;
    private String url;
    private String date;
    private String source;
    private Integer views;
    private Integer initViews;
    private List<String> viewers;

    public Story(){}

    public Story(String id, String source, String url, List<String> viewers){
        this.id = id;
        this.url = url;
        this.views = 0;
        this.source = source;
        this.viewers = viewers;
        this.initViews = viewers.size();
        this.date = Timing.getCurrentDate();
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Integer getViews() {
        return views;
    }

    public String getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }

    public List<String> getViewers() {
        return viewers;
    }

    public Integer getInitViews() {
        return initViews;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setViewers(List<String> viewers) {
        this.viewers = viewers;
    }

    public void setInitViews(Integer initViews) {
        this.initViews = initViews;
    }
}
