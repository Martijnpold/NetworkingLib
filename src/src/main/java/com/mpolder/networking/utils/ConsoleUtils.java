package com.mpolder.networking.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUtils {
    public static String readInput() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = br.readLine();
            return line;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
