package com.mpolder.networking;

import com.mpolder.networking.listeners.Client.ClientPacketListener;
import com.mpolder.networking.listeners.Listener;
import com.mpolder.networking.objects.ConnectionType;
import com.mpolder.networking.objects.PacketType;
import com.mpolder.networking.example.ExampleListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Socket client;
    private boolean debug = false;

    private Thread in;

    private List<Listener> listeners = new ArrayList<Listener>();

    public void start(String ip, int port) throws IOException {
        final Client fthis = this;

        if (client != null && !client.isClosed()) client.close();
        client = new Socket(ip, port);
        runListener(new ClientConnectListener(this, ConnectionType.CLIENT_SERVER_CONNECT));
        System.out.println("Connected client");

        if (in != null && in.isAlive()) in.interrupt();
        in = new Thread(new Runnable() {
            public void run() {
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
                            runListener(new ClientPacketListener(fthis, PacketType.CLIENT_RECEIVE, l));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        in.start();
    }

    public void send(String l) {
        runListener(new ClientPacketListener(this, PacketType.CLIENT_SEND, l));

        try {
            OutputStream outToServer = client.getOutputStream();
            outToServer.write(("\r" + l + "\r").getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
            runListener(new ClientConnectListener(this, ConnectionType.CLIENT_SERVER_DISCONNECT));
        }
    }

    private void waitms(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void runListener(Listener l) {
        for (Listener l2 : listeners) {
            l2.listenerEvent(l);
        }
    }
}
