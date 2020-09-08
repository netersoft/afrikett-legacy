package com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels;

import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class ContactMessage extends MessengerChat {

    public ContactMessage(){ super(); }

    public ContactMessage(String id, String sender, String recipient, Integer type, String contactName, @Nullable String contactNumber, @Nullable String contactEmail){
        super(id, sender, recipient, type);

        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
    }
}
