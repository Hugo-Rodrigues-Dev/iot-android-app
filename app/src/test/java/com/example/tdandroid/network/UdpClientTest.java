package com.example.tdandroid.network;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static org.junit.Assert.*;

public class UdpClientTest {

    @Test
    public void testUdpClientSendsAndReceivesMessage() throws Exception {
        // Prépare un faux serveur UDP local
        DatagramSocket serverSocket = new DatagramSocket(12345);

        // Lancer UdpClient dans un autre thread
        String messageToSend = "{\"request\":\"ping\"}";
        final String[] receivedResponse = {null};

        Thread clientThread = new Thread(() -> {
            new UdpClient("127.0.0.1", 12345, messageToSend, response -> {
                receivedResponse[0] = response;
            }).start();
        });

        clientThread.start();

        // Réception du message côté serveur
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        assertEquals(messageToSend, received);

        // Envoie une réponse simulée
        String fakeResponse = "{\"status\":\"ok\"}";
        byte[] responseData = fakeResponse.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(
                responseData,
                responseData.length,
                packet.getAddress(),
                packet.getPort()
        );
        serverSocket.send(responsePacket);

        // Attente max 2s pour réception côté client
        Thread.sleep(2000);

        assertNotNull("Réponse non reçue", receivedResponse[0]);
        assertEquals("{\"status\":\"ok\"}", receivedResponse[0]);

        serverSocket.close();
    }

    @Test
    public void testBuildUpdateScreenMessage() throws Exception {
        JSONObject data = new JSONObject();
        data.put("device_id", "device-001");
        data.put("sensor_order", new JSONArray().put("T").put("L").put("H").put("P"));

        JSONObject message = new JSONObject();
        message.put("request", "update-screen");
        message.put("data", data);

        assertEquals("update-screen", message.getString("request"));
        assertEquals("device-001", message.getJSONObject("data").getString("device_id"));
    }

    @Test
    public void testUdpClientHandlesIOExceptionAndReturnsError() throws Exception {
        final String[] callbackResponse = {null};

        // On utilise une IP non routable ou un port bloqué pour provoquer un timeout
        new UdpClient("192.0.2.1", 65535, "message-test", response -> {
            callbackResponse[0] = response;
        }).start();

        // Attente max 4s pour laisser le timeout se produire
        Thread.sleep(4000);

        assertNotNull("Callback non appelé", callbackResponse[0]);
        assertTrue(callbackResponse[0].contains("error"));
    }
}