package com.mpolder.networking.listeners.Server;

import com.mpolder.networking.Server;
import com.mpolder.networking.objects.ClientConnection;
import com.mpolder.networking.objects.PacketType;

public class ServerPacketEvent {
    private Server server;
    private ClientConnection client;
    private PacketType packetType;
    private String content;

    public ServerPacketEvent(Server s, ClientConnection cc, PacketType pt, String c) {
        this.server = s;
        this.client = cc;
        this.packetType = pt;
        this.content = c;
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