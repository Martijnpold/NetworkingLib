package com.mpolder.networking.listeners.Server;

import com.mpolder.networking.listeners.Listener;
import com.mpolder.networking.objects.ClientConnection;
import com.mpolder.networking.objects.PacketType;
import com.mpolder.networking.Server;

public class ServerPacketListener implements Listener {
    private Server server;
    private ClientConnection client;
    private PacketType packetType;
    private String content;

    public ServerPacketListener(Server s, ClientConnection cc, PacketType pt, String c) {
        this.server = s;
        this.client = cc;
        this.packetType = pt;
        this.content = c;
    }

    public void listenerEvent(Listener e) {
    }

    public String getContent() {
        return content;
    }

    public Server getServer() {
        return server;
    }

    public ClientConnection getClient() {
        return client;
    }

    public PacketType getPacketType() {
        return packetType;
    }
}