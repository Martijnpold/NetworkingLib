package com.mpolder.networking.example;

import com.mpolder.networking.Server;

import java.io.IOException;

class ExampleServer {
    public static void main(String[] args) {
        Server s = new Server();
        s.addListener(new ExampleListener());
        s.setDebug(false);
        try {
            s.open(27015);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
