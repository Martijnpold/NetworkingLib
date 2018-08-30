package com.mpolder.networking;

import com.mpolder.networking.listeners.Client.ClientConnectEvent;
import com.mpolder.networking.listeners.Client.ClientConnectionListener;
import com.mpolder.networking.listeners.Client.ClientPacketEvent;
import com.mpolder.networking.objects.ConnectionType;
import com.mpolder.networking.objects.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Client class to handle packet sending and connections.
 */
public class Client {
    private List<ClientConnectionListener> listeners = new ArrayList<>();
    private Socket client;
    private Thread in;

    /**
     * Connect client to a server.
     * @param ip IP used to establish a connection with.
     * @param port Port to establish a connection with.
     * @throws IOException Throws exception when a connection could not be made.
     */
    public void start(String ip, int port) throws IOException {
        if (client != null && !client.isClosed()) client.close();
        client = new Socket(ip, port);

        runConnectionEvent(new ClientConnectEvent(this, ConnectionType.CLIENT_SERVER_CONNECT));
        startInThread();
    }

    /**
     * Close the socket
     * @return Succesfully closed client.
     * @throws IOException Thrown if socket is not opened.
     */
    public boolean close() throws IOException {
        if(client != null && !client.isClosed()) {
            client.close();
            return true;
        }
        return false;
    }

    /**
     * Start thread to read input from the server.
     */
    private void startInThread() {
        final Client fthis = this;
        if (in != null && in.isAlive()) in.interrupt();
        in = new Thread(() -> {
            while (true) {
                if (client.isClosed()) break;
                try {
                    InputStream inFromServer = client.getInputStream();
                    if (inFromServer.available() > 0) {
                        String l = (char) inFromServer.read() + "";
                        while (!l.endsWith("\r")) {
                            l += (char) inFromServer.read() + "";
                        }
                        l = l.replace("\r", "");
                        runPacketEvent(new ClientPacketEvent(fthis, PacketType.CLIENT_RECEIVE, l));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        in.start();
    }

    /**
     * Send a string of text to the server.
     * @param l Text to send.
     */
    public void send(String l) {
        runPacketEvent(new ClientPacketEvent(this, PacketType.CLIENT_SEND, l));

        try {
            OutputStream outToServer = client.getOutputStream();
            outToServer.write(("\r" + l + "\r").getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
            runConnectionEvent(new ClientConnectEvent(this, ConnectionType.CLIENT_SERVER_DISCONNECT));
        }
    }

    /**
     * Register a listener class to run code on when an event occurs.
     * @param l Listener class to register.
     */
    public void addListener(ClientConnectionListener l) {
        listeners.add(l);
    }

    /**
     * Run a connection event on all registered listeners.
     * @param l Event to run.
     */
    private void runConnectionEvent(ClientConnectEvent l) {
        for (ClientConnectionListener l2 : listeners) {
            l2.clientConnectEvent(l);
        }
    }

    /**
     * Run a packet event on all registered listeners.
     * @param l Event to run.
     */
    private void runPacketEvent(ClientPacketEvent l) {
        for (ClientConnectionListener l2 : listeners) {
            l2.clientPacketEvent(l);
        }
    }
}
