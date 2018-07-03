package com.mpolder.networking.example;

import com.mpolder.networking.Server;

import java.io.IOException;

/**
 * Example setup of a server that registers a listener. Starts on port 27015 and accepts connections from clients.
 */
class ExampleServer {
    public static void main(String[] args) {
        Server s = new Server();
        s.addListener(new ExampleServerListener());
        try {
            s.open(27015);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
