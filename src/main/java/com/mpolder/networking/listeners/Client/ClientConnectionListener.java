package com.mpolder.networking.listeners.Client;

/**
 * Interface class for client connection listening.
 * Has to be registered on Client.
 * @see com.mpolder.networking.Client
 */
public interface ClientConnectionListener {
    void clientConnectEvent(ClientConnectEvent event);

    void clientPacketEvent(ClientPacketEvent event);
}
