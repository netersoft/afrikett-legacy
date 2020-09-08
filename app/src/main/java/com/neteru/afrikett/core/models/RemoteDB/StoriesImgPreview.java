package com.neteru.afrikett.core.models.RemoteDB;

import android.net.Uri;

import java.io.Serializable;

@SuppressWarnings("unused")
public class StoriesImgPreview implements Serializable {

    private String name;
    private String path;
    private String uriStr;
    private Integer position;

    public StoriesImgPreview(){}

    public StoriesImgPreview(String name, String path, Uri uri, Integer position){
        this.name = name;
        this.path = path;
        this.position = position;
        this.uriStr = uri.toString();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
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
