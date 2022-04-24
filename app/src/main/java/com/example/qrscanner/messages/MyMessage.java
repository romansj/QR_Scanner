package com.example.qrscanner.messages;

import androidx.annotation.Nullable;

public class MyMessage {
    private String IP;
    private int port;
    private String message;
    private boolean kill;
    private long id;

    public MyMessage(String IP, int port, String message) {
        this.IP = IP;
        this.port = port;
        this.message = message;

        this.message += "<EOF>\n\n";
    }

    public MyMessage(String IP, int port, boolean kill) {
        this.IP = IP;
        this.port = port;
        this.kill = kill;
    }

    public MyMessage(long id, String IP, int port, String message) {
        this.id = id;
        this.IP = IP;
        this.port = port;
        this.message = message;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public String getMessage() {
        return message;
    }

    public boolean isKill() {
        return kill;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final MyMessage other = (MyMessage) obj;
        return this.id == other.id && this.message.equals(other.message) && this.IP.equals(other.IP) && this.kill == other.kill;

    }
}