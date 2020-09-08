package com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels;

import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;

@SuppressWarnings("unused")
public class TextMessage extends MessengerChat {

    public TextMessage(){ super(); }

    public TextMessage(String id, String sender, String recipient, Integer type, String text){
        super(id, sender, recipient, type);

        this.messageText = text;
    }
}
