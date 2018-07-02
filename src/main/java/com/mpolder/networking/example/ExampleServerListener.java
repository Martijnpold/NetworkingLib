package com.mpolder.networking.example;

import com.mpolder.networking.listeners.Server.ServerConnectEvent;
import com.mpolder.networking.listeners.Server.ServerConnectionListener;
import com.mpolder.networking.listeners.Server.ServerPacketEvent;
import com.mpolder.networking.objects.PacketType;

public class ExampleServerListener implements ServerConnectionListener {
    @Override
    public void serverConnectEvent(ServerConnectEvent event) {
        System.out.println("Client " + event.getType() + " from " + event.getClient().getClient().getInetAddress().getHostAddress());
    }

    @Override
    public void serverPacketEvent(ServerPacketEvent event) {
        System.out.println(event.getPacketType() + " packet from " + event.getClient().getId() + " containing: " + event.getContent());
        if (event.getPacketType().equals(PacketType.SERVER_RECEIVE)) {
            event.getServer().sendAll("Echo " + event.getContent());
        }
    }
}