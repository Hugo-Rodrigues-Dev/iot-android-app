package com.example.tdandroid.network;

/**
 * Interface utilisée pour recevoir la réponse d'un envoi UDP
 */
public interface  UdpCallback {
    void onUdpResponse(String data);
}
