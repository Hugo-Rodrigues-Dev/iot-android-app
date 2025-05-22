package com.example.tdandroid.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Classe permettant d'établir une communication UDP simple avec un serveur distant
 * Elle est utilisée pour envoyer un message et recevoir une réponse
 */
public class UdpClient extends Thread {
    private final String ip;
    private final int port;
    private final String messageToSend;
    private final UdpCallback callback;

    /**
     * Constructeur de la classe UdpClient.
     *
     * @param ip Adresse IP du serveur
     * @param port Port UDP du serveur
     * @param messageToSend Message à envoyer sous forme de chaîne (ex: JSON)
     * @param callback Interface de rappel pour retourner la réponse reçue
     */
    public UdpClient(String ip, int port, String messageToSend, UdpCallback callback) {
        this.ip = ip;
        this.port = port;
        this.messageToSend = messageToSend;
        this.callback = callback;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;

        try {
            // Création d'une socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Timeout de 3 secondes

            // Conversion du message en bytes et envoi du paquet
            byte[] data = messageToSend.getBytes();
            InetAddress address = InetAddress.getByName(ip);

            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);

            System.out.println("UDP → Paquet envoyé : " + messageToSend);

            // Préparation à recevoir la réponse
            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

            socket.receive(responsePacket); // Attente bloquante jusqu’à timeout
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

            System.out.println("UDP ← Réponse reçue : " + response);

            if (callback != null) {
                callback.onUdpResponse(response);
            }

        } catch (IOException e) {
            System.err.println("UDP ✖ Erreur : " + e.getMessage());

            if (callback != null) {
                callback.onUdpResponse("{\"error\":\"timeout ou erreur réseau\"}");
            }

        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
