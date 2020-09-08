package com.neteru.afrikett.core.models.RemoteDB;

import android.net.Uri;

import java.io.Serializable;

@SuppressWarnings("unused")
public class ChatImgVidPreview implements Serializable {

    private String name;
    private String path;
    private String description;
    private String uriStr;
    private Integer type;
    private Integer position;

    public ChatImgVidPreview(){}

    public ChatImgVidPreview(String name, String path, Uri uri, Integer type, String description, Integer position){
        this.name = name;
        this.path = path;
        this.type = type;
        this.position = position;
        this.uriStr = uri.toString();
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public Integer getType() {
        return type;
    }

    public String getUriStr() {
        return uriStr;
    }

    public Integer getPosition() {
        return position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setUriStr(String uriStr) {
        this.uriStr = uriStr;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Uri getUri(){
        return Uri.parse(this.uriStr);
    }

    public void setUri(Uri uri){
        this.uriStr = uri.toString();
    }
}
