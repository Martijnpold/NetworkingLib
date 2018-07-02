package com.mpolder.networking.listeners.Client;

import com.mpolder.networking.Client;
import com.mpolder.networking.listeners.Server.ServerConnectionListener;
import com.mpolder.networking.objects.ConnectionType;

public class ClientConnectEvent {
    private Client client;
    private ConnectionType connectionType;

    public ClientConnectEvent(Client c, ConnectionType ct) {
        this.client = c;
        this.connectionType = ct;
    }

    public void listenerEvent(ServerConnectionListener e) {
    }

    public Client getClient() {
        return client;
    }

    public ConnectionType getType() {
        return connectionType;
    }
}
