package com.mpolder.networking.listeners.Client;

import com.mpolder.networking.Client;
import com.mpolder.networking.objects.PacketType;

/**
 * Used to indicate a packet being sent or received on the client side with the content attached.
 */
public class ClientPacketEvent {
    private Client client;
    private PacketType packetType;
    private String content;

    public ClientPacketEvent(Client c, PacketType pt, String ct) {
        this.client = c;
        this.packetType = pt;
        this.content = ct;
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