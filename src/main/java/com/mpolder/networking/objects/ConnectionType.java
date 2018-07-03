package com.mpolder.networking.objects;

/**
 * Enum class to store all possible events that can happen on the connection from a client to a server and vice versa.
 */
public enum ConnectionType {
    SERVER_CLIENT_DISCONNECT(),
    SERVER_CLIENT_BANNED(),
    SERVER_CLIENT_CONNECT(),
    CLIENT_SERVER_DISCONNECT(),
    CLIENT_SERVER_CONNECT();
}
