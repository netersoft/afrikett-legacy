package com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels;

import android.net.Uri;

import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;

@SuppressWarnings("unused")
public class ImageMessage extends MessengerChat {

    public ImageMessage(){ super(); }

    public ImageMessage(String id, String sender, String recipient, Integer type, Uri uri, String legend, String fileName, String size){
        super(id, sender, recipient, type);

        this.messageUriStr = uri.toString();
        this.messageLegend = legend;
        this.messageFileName = fileName;
        this.fileSize = size;
    }
}
