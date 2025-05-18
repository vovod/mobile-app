package com.nhom13.learningenglishapp.database.models;
public class Message {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    private String messageText;
    private int messageType;
    private long timestamp;

    public Message(String messageText, int messageType) {
        this.messageText = messageText;
        this.messageType = messageType;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
