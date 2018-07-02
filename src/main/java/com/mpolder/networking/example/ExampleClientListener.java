package com.mpolder.networking.example;

import com.mpolder.networking.listeners.Client.ClientConnectEvent;
import com.mpolder.networking.listeners.Client.ClientConnectionListener;
import com.mpolder.networking.listeners.Client.ClientPacketEvent;

public class ExampleClientListener implements ClientConnectionListener {
    @Override
    public void clientConnectEvent(ClientConnectEvent event) {
        System.out.println("Client " + event.getType());
    }

    @Override
    public void clientPacketEvent(ClientPacketEvent event) {
        System.out.println(event.getPacketType() + " packet from server containing: " + event.getContent());
    }
}
