package com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels;

import android.net.Uri;

import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;

@SuppressWarnings("unused")
public class AudioMessage extends MessengerChat {

    public AudioMessage(){ super(); }

    public AudioMessage(String id, String sender, String recipient, Integer type, Uri uri, String fileName, String length, String size){
        super(id, sender, recipient, type);

        this.messageUriStr = uri.toString();
        this.messageFileName = fileName;
        this.mediaFileLength = length;
        this.fileSize = size;
    }
}
