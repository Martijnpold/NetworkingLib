package com.mpolder.networking.example;

import com.mpolder.networking.Client;

import java.io.IOException;

/**
 * Example setup of a client that registers a listener. Tries to connect to a locally hosted server on port 27015.
 */
public class ExampleClient {
    public static void main(String[] args) {
        Client c = new Client();
        c.addListener(new ExampleClientListener());
        try {
            c.start("localhost", 27015);
            c.send("hello world");
        } catch (IOException e) {
            System.out.println("Error on client side connection.");
            e.printStackTrace();
        }
    }
}
