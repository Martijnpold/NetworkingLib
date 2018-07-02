package com.mpolder.networking.example;

import com.mpolder.networking.listeners.Client.ClientPacketListener;
import com.mpolder.networking.listeners.Listener;
import com.mpolder.networking.listeners.Server.ServerPacketListener;

public class ExampleListener implements Listener {
    public void listenerEvent(Listener e) {
        if (e instanceof ServerPacketListener) {
            ServerPacketListener spl = (ServerPacketListener) e;
            System.out.println(spl.getPacketType() + "_CLIENT_" + spl.getClient().getId().toUpperCase() + ":" + spl.getContent());
        }
        if (e instanceof ClientPacketListener) {
            ClientPacketListener spl = (ClientPacketListener) e;
            System.out.println(spl.getPacketType() + "_SERVER_:" + spl.getContent());
        }
        if (e instanceof ClientConnectListener) {
            ClientConnectListener spl = (ClientConnectListener) e;
            System.out.println(spl.getConnectionType());
        }
    }
}