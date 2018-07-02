package com.mpolder.networking.listeners.Server;

public interface ServerConnectionListener {
    void serverConnectEvent(ServerConnectEvent event);

    void serverPacketEvent(ServerPacketEvent event);
}
