package com.mpolder.networking.listeners.Client;

import com.mpolder.networking.Client;
import com.mpolder.networking.listeners.Listener;
import com.mpolder.networking.objects.PacketType;

public class ClientPacketListener implements Listener {
    private Client client;
    private PacketType packetType;
    private String content;

    public ClientPacketListener(Client c, PacketType pt, String ct) {
        this.client = c;
        this.packetType = pt;
        this.content = ct;
    }

    public void listenerEvent(Listener e) {
    }

    public String getContent() {
        return content;
    }

    public Client getClient() {
        return client;
    }

    public PacketType getPacketType() {
        return packetType;
    }
}