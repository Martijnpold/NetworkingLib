package com.mpolder.networking.listeners.Server;

import com.mpolder.networking.Server;
import com.mpolder.networking.objects.ClientConnection;
import com.mpolder.networking.objects.ConnectionType;

/**
 * Used to indicate an event occuring on the connection of the server to the client.
 */
public class ServerConnectEvent {
    private Server server;
    private ClientConnection cc;
    private ConnectionType connectionType;

    public ServerConnectEvent(Server s, ClientConnection cc, ConnectionType ct) {
        this.server = s;
        this.cc = cc;
        this.connectionType = ct;
    }

    public void listenerEvent(ServerConnectionListener e) {
    }

    public Server getServer() {
        return server;
    }

    public ClientConnection getClient() {
        return cc;
    }

    public ConnectionType getType() {
        return connectionType;
    }
}
