package com.example.tdandroid.utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère la logique métier associée aux micro:bits disponibles,
 * (récupération / stockage local de leur identifiant, ...
 */
public class DeviceManager {
    private static List<String> availableDeviceIds = new ArrayList<>();

    /**
     * Met à jour la liste des device_id à partir d'une réponse JSON du serveur
     *
     * @param json Réponse JSON contenant les IDs des micro:bits
     */
    public static void updateDevicesFromResponse(String json) {
        try {
            JSONArray array = new JSONArray(json);
            availableDeviceIds.clear();

            for (int i = 0; i < array.length(); i++) {
                availableDeviceIds.add(array.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace(); // Mauvais format ou parsing échoué
        }
    }

    /**
     * Retourne tous les IDs des micro:bits connus.
     */
    public static List<String> getAvailableDeviceIds() {
        return availableDeviceIds;
    }

    public static void setDemoDevices() {
        availableDeviceIds.clear();
        for (int i = 1; i <= 10; i++) {
            availableDeviceIds.add(String.format("device-%03d", i));
        }
    }
}
