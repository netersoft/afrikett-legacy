package com.neteru.afrikett.core.models.RemoteDB.PostModels;

import com.neteru.afrikett.core.models.RemoteDB.Post;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class EventPost extends Post {

    public EventPost(){super();}

    public EventPost(String id, String showcaseId, String authorId, Integer type, String eventName, String eventLocation, String eventDate, String eventHour, String eventAbout, ArrayList<String> previews){
        super(id, showcaseId, authorId, type);

        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.eventHour = eventHour;
        this.eventAbout = eventAbout;
        this.previews = previews;
    }
}
