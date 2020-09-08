package com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels;

import android.net.Uri;

import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;

@SuppressWarnings("unused")
public class DocumentMessage extends MessengerChat {

    public DocumentMessage(){ super(); }

    public DocumentMessage(String id, String sender, String recipient, Integer type, Uri uri, String fileName, String size){
        super(id, sender, recipient, type);

        this.messageUriStr = uri.toString();
        this.messageFileName = fileName;
        this.fileSize = size;
    }
}
