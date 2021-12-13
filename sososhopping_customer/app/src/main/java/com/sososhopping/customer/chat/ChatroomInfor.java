package com.sososhopping.customer.chat;

public class ChatroomInfor {
    public String customerName = "";
    public String storeName = "";
    public String chatroomId = "";
    public String lastMessage = "";
    public Object lastMessageTimestamp = null;
    public Object leaveChatroomTimestamp = null;

    public ChatroomInfor() {
    }

    public ChatroomInfor(String customerName, String storeName, String chatroomId) {
        this.customerName = customerName;
        this.storeName = storeName;
        this.chatroomId = chatroomId;
    }

    public ChatroomInfor(String customerName, String storeName, String chatroomId, String lastMessage, Object lastMessageTimestamp, Object leaveChatroomTimestamp) {
        this.customerName = customerName;
        this.storeName = storeName;
        this.chatroomId = chatroomId;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.leaveChatroomTimestamp = leaveChatroomTimestamp;
    }
}
