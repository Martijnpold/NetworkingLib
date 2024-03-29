package com.mpolder.networking.objects;

import java.net.Socket;

/**
 * Data class to store all data related to a single client connection.
 * Only used server-sided.
 */
public class ClientConnection {
    private Socket client;
    private String id;
    private int strikes;
    private int maxStrikes;

    public ClientConnection(Socket socket, int maxStrikes) {
        client = socket;
        this.strikes = 0;
        this.maxStrikes = maxStrikes;
        id = "Anonymous";
    }

    public Socket getClient() {
        return client;
    }

    public void setId(String username) {
        this.id = username;
    }

    public String getId() {
        return id;
    }

    public void strike() {
        strikes++;
    }

    public void resetStrikes() {
        strikes = 0;
    }

    public boolean shouldBeBanned() {
        return strikes >= maxStrikes;
    }
}