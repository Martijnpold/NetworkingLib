package com.mpolder.networking.objects;

/**
 * All possible types of packet transmissions.
 */
public enum PacketType {
    CLIENT_RECEIVE(),
    CLIENT_SEND(),
    SERVER_RECEIVE(),
    SERVER_SEND();
}