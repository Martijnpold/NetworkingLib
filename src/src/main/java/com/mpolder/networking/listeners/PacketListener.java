package com.mpolder.networking.listeners;

import com.mpolder.networking.objects.ClientConnection;
import com.mpolder.networking.objects.PacketType;

public class PacketListener implements Listener {
    private ClientConnection client;
    private PacketType packetType;
    private String content;

    public PacketListener(ClientConnection cc, PacketType pt, String c) {
        this.client = cc;
        this.packetType = pt;
        this.content = c;
    }

    public void listenerEvent(Listener e) {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public ClientConnection getClient() {
        return client;
    }

    public PacketType getPacketType() {
        return packetType;
    }
}
