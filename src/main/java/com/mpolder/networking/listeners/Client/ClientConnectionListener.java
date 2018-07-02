package com.mpolder.networking.listeners.Client;

public interface ClientConnectionListener {
    void clientConnectEvent(ClientConnectEvent event);

    void clientPacketEvent(ClientPacketEvent event);
}
