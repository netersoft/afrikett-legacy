package com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels;

import android.net.Uri;

import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;

@SuppressWarnings("unused")
public class VideoMessage extends MessengerChat {

    public VideoMessage(){ super(); }

    public VideoMessage(String id, String sender, String recipient, Integer type, Uri uri, Uri thumbUri, String legend, String fileName, String length, String size){
        super(id, sender, recipient, type);

        this.messageUriStr = uri.toString();
        this.videoThumbnailUriStr = thumbUri.toString();
        this.messageLegend = legend;
        this.messageFileName = fileName;
        this.mediaFileLength = length;
        this.fileSize = size;
    }
}
