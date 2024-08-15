package com.mycompany.moutasem219140;
import java.io.*;
import java.net.*;
import java.util.Scanner;

class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    private String content;
    private boolean isPrivate;

    public Message(String sender, String content, boolean isPrivate) {
        this.sender = sender;
        this.content = content;
        this.isPrivate = isPrivate;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}