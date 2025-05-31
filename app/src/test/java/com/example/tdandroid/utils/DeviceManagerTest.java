package com.example.tdandroid.utils;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;

public class DeviceManagerTest {

    @Before
    public void resetDeviceList() {
        DeviceManager.getAvailableDeviceIds().clear();
    }

    @Test
    public void testUpdateDevicesFromResponse() {
        // Simule une réponse JSON du serveur
        String fakeJson = "[\"device-001\", \"device-002\", \"device-003\"]";

        DeviceManager.updateDevicesFromResponse(fakeJson);
        List<String> devices = DeviceManager.getAvailableDeviceIds();

        assertEquals(3, devices.size());
        assertEquals("device-001", devices.get(0));
        assertEquals("device-002", devices.get(1));
        assertEquals("device-003", devices.get(2));
    }

    @Test
    public void testSetDemoDevices() {
        DeviceManager.setDemoDevices();
        List<String> demoDevices = DeviceManager.getAvailableDeviceIds();

        assertEquals(10, demoDevices.size());
        assertEquals("device-001", demoDevices.get(0));
        assertEquals("device-010", demoDevices.get(9));
    }

    @Test
    public void testUpdateDevicesFromResponse_withInvalidJson() {
        String invalidJson = "{not a json array}";
        DeviceManager.updateDevicesFromResponse(invalidJson);

        // On attend que la liste reste vide
        List<String> devices = DeviceManager.getAvailableDeviceIds();
        assertEquals(0, devices.size());
    }
}