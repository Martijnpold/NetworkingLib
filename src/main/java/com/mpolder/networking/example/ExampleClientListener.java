package com.mpolder.networking.example;

import com.mpolder.networking.listeners.Client.ClientConnectEvent;
import com.mpolder.networking.listeners.Client.ClientConnectionListener;
import com.mpolder.networking.listeners.Client.ClientPacketEvent;

/**
 * Example listener class that listens to client related events and prints information to the console when connected
 * or when a packet is sent/received.
 */
public class ExampleClientListener implements ClientConnectionListener {
    @Override
    public void clientConnectEvent(ClientConnectEvent event) {
        System.out.println("Client " + event.getType());
    }

    @Override
    public void clientPacketEvent(ClientPacketEvent event) {
        System.out.println(event.getPacketType() + " from/to server containing: " + event.getContent());
    }
}
