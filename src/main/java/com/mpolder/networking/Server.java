package com.mpolder.networking;

import com.mpolder.networking.listeners.Server.ServerConnectEvent;
import com.mpolder.networking.listeners.Server.ServerConnectionListener;
import com.mpolder.networking.listeners.Server.ServerPacketEvent;
import com.mpolder.networking.objects.ClientConnection;
import com.mpolder.networking.objects.ConnectionType;
import com.mpolder.networking.objects.PacketType;
import sun.awt.Mutex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Server class to handle packet sending and connections.
 */
public class Server {
    private ServerSocket server;
    private List<InetAddress> banned = new ArrayList<>();
    private List<ClientConnection> clients = new ArrayList<ClientConnection>();
    private List<ServerConnectionListener> listeners = new ArrayList<ServerConnectionListener>();
    private Mutex access = new Mutex();
    private Thread connectionThread;
    private int maxStrikes = 10;
    private boolean noPrintMode = false;

    /**
     * Open server and start all threads.
     * @param port Port to allow connections to occur on.
     * @throws IOException Throws exception when a connection with port could not be made.
     */
    public void open(int port) throws IOException {
        final Server s = this;

        if (server == null || server.isClosed()) {
            server = new ServerSocket(port);
        }

        if (connectionThread != null && connectionThread.isAlive()) connectionThread.interrupt();
        connectionThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket socket = server.accept();
                        if(!noPrintMode) System.out.println("Client connected from: " + socket.getInetAddress() + ":" + socket.getPort());
                        ClientConnection cc = new ClientConnection(socket, maxStrikes);
                        if (banned.contains(socket.getInetAddress())) {
                            runConnectionEvent(new ServerConnectEvent(s, cc, ConnectionType.SERVER_CLIENT_BANNED));
                            socket.close();
                            continue;
                        }
                        runConnectionEvent(new ServerConnectEvent(s, cc, ConnectionType.SERVER_CLIENT_CONNECT));
                        access.lock();
                        clients.add(cc);
                        access.unlock();

                        Runnable r = () -> {
                            while (clients.contains(cc)) {
                                try {
                                    String read = read(cc);
                                    if (read != null) {
                                        if (read.length() > 0) {
                                            runPacketEvent(new ServerPacketEvent(s, cc, PacketType.SERVER_RECEIVE, read));
                                        }
                                    }
                                } catch (IOException e) {
                                    if(!noPrintMode) System.out.println("Client disconnected: " + socket.getInetAddress() + ":" + socket.getLocalPort());
                                    runConnectionEvent(new ServerConnectEvent(s, cc, ConnectionType.SERVER_CLIENT_DISCONNECT));
                                    access.lock();
                                    clients.remove(cc);
                                    access.unlock();
                                }
                                if (cc.shouldBeBanned()) {
                                    if(!noPrintMode) System.out.println("Client banned due to reaching max strikes: " + cc.getClient().getInetAddress() + ":" + cc.getClient().getPort());
                                    runConnectionEvent(new ServerConnectEvent(s, cc, ConnectionType.SERVER_CLIENT_BANNED));
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
                        e.printStackTrace();
                    }
                }
            }
        });
        connectionThread.start();
        if(!noPrintMode) System.out.println("Awaiting connection...");
    }

    /**
     * Enable/Disable NoPrint Mode, this will disable any System prints.
     * @param noPrintMode Enable or Disable NoPrint.
     */
    public void setNoPrintMode(boolean noPrintMode) {
        this.noPrintMode = noPrintMode;
    }

    /**
     * Close the server.
     * @throws IOException Throws exception when server could not be closed.
     */
    public void close() throws IOException {
        if (server != null && !server.isClosed()) {
            server.close();
            if(!noPrintMode) System.out.println("Server closed.");
        }
    }

    /**
     * Send string to all connected clients.
     * @param line String to send.
     */
    public void sendAll(String line) {
        for (ClientConnection cc : getClients()) {
            send(cc, line);
        }
    }

    /**
     * Send string to specific client.
     * @param cc Client to send the string to.
     * @param line String to send.
     * @return Returns if sending was succesful.
     */
    public boolean send(final ClientConnection cc, String line) {
        for (ServerConnectionListener l : listeners) {
            ServerPacketEvent spl = new ServerPacketEvent(this, cc, PacketType.SERVER_SEND, line);
            l.serverPacketEvent(spl);
        }

        try {
            Socket s = cc.getClient();
            OutputStream outToServer = s.getOutputStream();
            outToServer.write((line + "\r").getBytes(Charset.forName("UTF-8")));
            return true;
        } catch (IOException e) {
            access.lock();
            clients.remove(cc);
            if(!noPrintMode) System.out.println("Client disconnected: " + cc.getClient().getInetAddress() + ":" + cc.getClient().getLocalPort());
            access.unlock();
            return false;
        }
    }

    /**
     * Read line from Client inputstream.
     * @param cc Client to listen to.
     * @return String sent by client.
     * @throws IOException Throws IOException when the client disconnects.
     */
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

    /**
     * Get all connected clients.
     * @return List of all connected clients.
     */
    public List<ClientConnection> getClients() {
        return new ArrayList<>(clients);
    }

    /**
     * Set max strikes before a client is banned from the server and not allowed to reconnect.
     * @param maxStrikes Amount of strikes.
     */
    public void setMaxStrikes(int maxStrikes) {
        this.maxStrikes = maxStrikes;
    }

    /**
     * Register a listener class to run code on when an event occurs.
     * @param l Listener class to register.
     */
    public void addListener(ServerConnectionListener l) {
        listeners.add(l);
    }

    /**
     * Run a connection event on all registered listeners.
     * @param l Event to run.
     */
    private void runConnectionEvent(ServerConnectEvent l) {
        for (ServerConnectionListener l2 : listeners) {
            l2.serverConnectEvent(l);
        }
    }

    /**
     * Run a packet event on all registered listeners.
     * @param l Event to run.
     */
    private void runPacketEvent(ServerPacketEvent l) {
        for (ServerConnectionListener l2 : listeners) {
            l2.serverPacketEvent(l);
        }
    }
}
