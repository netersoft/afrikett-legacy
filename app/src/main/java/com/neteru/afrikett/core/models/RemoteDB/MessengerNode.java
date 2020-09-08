package com.neteru.afrikett.core.models.RemoteDB;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

@SuppressWarnings("unused")
public class MessengerNode implements Serializable {

    private Map<String, MessengerChat> chats;
    private Boolean firstIdBlockState;
    private Boolean secondIdBlockState;
    private Boolean pendingMsg;
    private Boolean formalBlock;
    private String firstId;
    private String secondId;
    private String lastMsg;
    private String lastTime;
    private String firstName;
    private String secondName;
    private String pendingMsgTarget;

    public MessengerNode(){}

    public MessengerNode(String id_0, String id_1, String name_0, String name_1, Boolean formalBlock){
        this.firstId = id_0;
        this.secondId = id_1;
        this.firstName = name_0;
        this.secondName = name_1;
        this.chats = new HashMap<>();
        this.firstIdBlockState = false;
        this.secondIdBlockState = false;
        this.formalBlock = formalBlock;
        this.pendingMsgTarget = EMPTY;
        this.pendingMsg = false;
        this.lastTime = EMPTY;
        this.lastMsg = EMPTY;
    }

    public String getFirstId() {
        return firstId;
    }

    public String getSecondId() {
        return secondId;
    }

    public Map<String, MessengerChat> getChats() {
        return chats;
    }

    public Boolean getFirstIdBlockState() {
        return firstIdBlockState;
    }

    public Boolean getSecondIdBlockState() {
        return secondIdBlockState;
    }

    public Boolean getPendingMsg() {
        return pendingMsg;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public Boolean getFormalBlock() {
        return formalBlock;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getLastTime() {
        return lastTime;
    }

    public String getPendingMsgTarget() {
        return pendingMsgTarget;
    }

    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
    }

    public void setChats(Map<String, MessengerChat> chats) {
        this.chats = chats;
    }

    public void setFirstIdBlockState(Boolean firstIdBlockState) {
        this.firstIdBlockState = firstIdBlockState;
    }

    public void setSecondIdBlockState(Boolean secondIdBlockState) {
        this.secondIdBlockState = secondIdBlockState;
    }

    public void setPendingMsg(Boolean pendingMsg) {
        this.pendingMsg = pendingMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public void setFormalBlock(Boolean formalBlock) {
        this.formalBlock = formalBlock;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public void setPendingMsgTarget(String pendingMsgTarget) {
        this.pendingMsgTarget = pendingMsgTarget;
    }
}
