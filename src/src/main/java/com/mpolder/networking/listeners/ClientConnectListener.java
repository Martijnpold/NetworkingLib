package com.mpolder.networking.listeners;

import com.mpolder.networking.Client;
import com.mpolder.networking.objects.ClientConnection;
import com.mpolder.networking.objects.ConnectionType;
import com.mpolder.networking.Server;

public class ClientConnectListener implements Listener {
    Client client;
    Server server;
    ClientConnection cc;
    ConnectionType connectionType;

    public ClientConnectListener(Client c, ConnectionType ct) {
        this.client = c;
        this.cc = cc;
        this.connectionType = ct;
    }

    public ClientConnectListener(Server s, ClientConnection cc, ConnectionType ct) {
        this.server = s;
        this.cc = cc;
        this.connectionType = ct;
    }

    public void listenerEvent(Listener e) {
    }

    public Client getClient() {
        return client;
    }

    public Server getServer() {
        return server;
    }

    public ClientConnection getClientConnection() {
        return cc;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }
}
