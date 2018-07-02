package com.mpolder.networking.example;

import com.mpolder.networking.Client;

import java.io.IOException;

public class ExampleClient {
    public static void main(String[] args) {
        Client c = new Client();
        try {
            c.start("localhost", 27015);
        } catch (IOException e) {
            System.out.println("Error on client side connection.");
            e.printStackTrace();
        }
    }
}
