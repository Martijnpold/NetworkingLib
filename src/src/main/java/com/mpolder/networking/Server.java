package com.mpolder.networking;

import com.mpolder.networking.listeners.Listener;
import com.mpolder.networking.listeners.Server.ServerPacketListener;
import com.mpolder.networking.objects.ClientConnection;
import com.mpolder.networking.objects.ConnectionType;
import com.mpolder.networking.objects.PacketType;
import com.mpolder.networking.example.ExampleListener;
import sun.awt.Mutex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {
    private ServerSocket server;
    private List<InetAddress> banned = new ArrayList<>();
    private List<ClientConnection> clients = new ArrayList<ClientConnection>();
    private List<Listener> listeners = new ArrayList<Listener>();
    private Mutex access = new Mutex();
    private Thread connectionThread;
    private int maxStrikes = 10;
    private boolean debug = false;

    public void open(int port) throws IOException {
        final Server s = this;

        if (server == null || server.isClosed()) {
            server = new ServerSocket(port);
            if (debug) System.out.println("Server started.");
        }

        if (debug) addListener(new ExampleListener());

        if (connectionThread != null && connectionThread.isAlive()) connectionThread.interrupt();
        connectionThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket socket = server.accept();
                        ClientConnection cc = new ClientConnection(socket, maxStrikes);
                        if (banned.contains(socket.getInetAddress())) {
                            runListener(new ClientConnectListener(s, cc, ConnectionType.SERVER_CLIENT_BANNED));
                            socket.close();
                            continue;
                        }
                        runListener(new ClientConnectListener(s, cc, ConnectionType.SERVER_CLIENT_CONNECT));
                        access.lock();
                        clients.add(cc);
                        if (debug)
                            System.out.println("Added " + cc.getClient().getInetAddress() + ":" + cc.getClient().getPort() + " to connections");
                        access.unlock();
                        System.out.println("Client connected from: " + socket.getInetAddress() + ":" + socket.getPort());

                        Runnable r = () -> {
                            while (clients.contains(cc)) {
                                try {
                                    String read = read(cc);
                                    if (read != null) {
                                        if (read.length() > 0) {
                                            runListener(new ServerPacketListener(s, cc, PacketType.SERVER_RECEIVE, read));
                                        }
                                    }
                                } catch (IOException e) {
                                    System.out.println("Client disconnected: " + socket.getInetAddress() + ":" + socket.getLocalPort());
                                    runListener(new ClientConnectListener(s, cc, ConnectionType.SERVER_CLIENT_DISCONNECT));
                                    access.lock();
                                    clients.remove(cc);
                                    access.unlock();
                                }
                                if (cc.shouldBeBanned()) {
                                    System.out.println("Client banned due to reaching max strikes: " + cc.getClient().getInetAddress() + ":" + cc.getClient().getPort());
                                    runListener(new ClientConnectListener(s, cc, ConnectionType.SERVER_CLIENT_BANNED));
                                    banned.add(cc.getClient().getInetAddress());
                                    try {
                                        cc.getClient().close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                        };

                        new Thread(r).start();
                    } catch (IOException e) {
                        if (debug) System.out.println("Error connecting client...");
                        e.printStackTrace();
                    }
                }
            }
        });
        connectionThread.start();
        System.out.println("Awaiting connection...");
    }

    public void close() throws IOException {
        if (server != null && !server.isClosed()) {
            server.close();
            System.out.println("Server closed.");
        }
    }

    public void sendAll(String line) {
        for (ClientConnection cc : getClients()) {
            if (debug) System.out.println("Sending to " + cc.getClient().getInetAddress());
            send(cc, line);
        }
    }

    public Boolean send(final ClientConnection cc, String line) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (debug)
            System.out.println("Send called from: " + stackTraceElements[3].getClassName() + " " + stackTraceElements[3].getLineNumber());
        for (Listener l : listeners) {
            ServerPacketListener spl = new ServerPacketListener(this, cc, PacketType.SERVER_SEND, line);
            l.listenerEvent(spl);
        }

        try {
            Socket s = cc.getClient();
            OutputStream outToServer = s.getOutputStream();
            outToServer.write((line + "\r").getBytes(Charset.forName("UTF-8")));
            return true;
        } catch (IOException e) {
            access.lock();
            clients.remove(cc);
            System.out.println("Client disconnected: " + cc.getClient().getInetAddress() + ":" + cc.getClient().getLocalPort());
            access.unlock();
            return false;
        }
    }

    private String read(ClientConnection cc) throws IOException {
        Socket s = cc.getClient();
        InputStream outToServer = s.getInputStream();
        String l = (char) outToServer.read() + "";
        if (!l.startsWith("\r")) {
            while (outToServer.available() > 0) outToServer.read();
            cc.strike();
            return null;
        }
        l = "";
        while (!l.endsWith("\r")) {
            l += (char) outToServer.read() + "";
        }
        l = l.replace("\r", "");
        return l;
    }

    public String formatMessage(String sender, String message) {
        Date d = new Date();
        String hrs = String.format("%02d", d.getHours());
        String mins = String.format("%02d", d.getMinutes());
        String time = "[" + hrs + ":" + mins + "]";
        return time + " " + sender + ": " + message;
    }

    private void waitms(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getAvailableId(String s) {
        return getAvailableId(s, 0);
    }

    private String getAvailableId(String s, int id) {
        String name = s;
        if (id > 0) name = s + "-" + id;
        for (ClientConnection cc : getClients()) {
            if (cc.getId().equals(name)) {
                return getAvailableId(s, id + 1);
            }
        }
        return name;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setMaxStrikes(int maxStrikes) {
        this.maxStrikes = maxStrikes;
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void runListener(Listener l) {
        for (Listener l2 : listeners) {
            l2.listenerEvent(l);
        }
    }

    public List<ClientConnection> getClients() {
        return new ArrayList<ClientConnection>(clients);
    }
}
