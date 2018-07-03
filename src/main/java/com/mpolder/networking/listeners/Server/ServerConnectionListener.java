package com.mpolder.networking.listeners.Server;

/**
 * Interface class for server connection listening.
 * Has to be registered on Server.
 * @see com.mpolder.networking.Server
 */
public interface ServerConnectionListener {
    void serverConnectEvent(ServerConnectEvent event);

    void serverPacketEvent(ServerPacketEvent event);
}
