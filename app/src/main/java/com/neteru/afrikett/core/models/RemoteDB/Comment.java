package com.neteru.afrikett.core.models.RemoteDB;

import com.neteru.afrikett.core.utilities.Timing;

@SuppressWarnings("unused")
public class Comment {
    private String id;
    private String postId;
    private String authorId;
    private String showcaseId;
    private String content;
    private String date;
    private Integer authorStatus;

    public Comment(){}

    public Comment(String id, String postId, String showcaseId, String authorId, Integer authorStatus, String content){

        this.id = id;
        this.postId = id;
        this.authorId = authorId;
        this.content = content;
        this.date = Timing.getCurrentDate();
        this.authorStatus = authorStatus;
        this.showcaseId = showcaseId;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAuthorStatus() {
        return authorStatus;
    }

    public void setAuthorStatus(Integer authorStatus) {
        this.authorStatus = authorStatus;
    }

    public String getShowcaseId() {
        return showcaseId;
    }

    public void setShowcaseId(String showcaseId) {
        this.showcaseId = showcaseId;
    }

}
