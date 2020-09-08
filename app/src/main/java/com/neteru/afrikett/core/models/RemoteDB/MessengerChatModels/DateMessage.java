package com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels;

import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;

@SuppressWarnings("unused")
public class DateMessage extends MessengerChat {

    public DateMessage(){ super(); }

    public DateMessage(String id, String sender, String recipient, Integer type, String dateId){
        super(id, sender, recipient, type);

        this.dateId = dateId;
    }
}
