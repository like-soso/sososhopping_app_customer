package com.sososhopping.customer.chat;

import com.google.firebase.database.ServerValue;

public class Chat {
    public String senderUid;
    public String messageType;
    public String content;
    public String imgUrl;
    public Object timeStamp;

    public Chat() {
    }

    public Chat(String senderUid, String messageType, String content, String imgUrl) {
        this.senderUid = senderUid;
        this.messageType = messageType;
        this.content = content;
        this.imgUrl = imgUrl;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
