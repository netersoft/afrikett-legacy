package com.neteru.afrikett.core.models.RemoteDB.PostModels;

import com.neteru.afrikett.core.models.RemoteDB.Post;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class NewsPost extends Post {

    public NewsPost(){super();}

    public NewsPost(String id, String showcaseId, String authorId, Integer type, String newsTitle, String newsContent, ArrayList<String> previews){
        super(id, showcaseId, authorId, type);

        this.newsTitle = newsTitle;
        this.newsContent = newsContent;
        this.previews = previews;
    }
}
