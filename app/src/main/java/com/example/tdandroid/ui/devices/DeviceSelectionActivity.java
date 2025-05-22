package com.example.tdandroid.ui.devices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tdandroid.R;
import com.example.tdandroid.network.UdpClient;
import com.example.tdandroid.ui.dashboard.DashboardActivity;
import com.example.tdandroid.utils.DeviceManager;

import org.json.JSONObject;

import java.util.List;

/**
 * Affiche la liste des devices disponibles
 * Une fois un device sélectionné, redirige vers DashboardActivity
 */
public class DeviceSelectionActivity extends AppCompatActivity {
    private String ip;
    private int port;
    private boolean isDemo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        // Récupération des données envoyées par l'écran précédent
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", 10000);
        isDemo = getIntent().getBooleanExtra("demo", false);

        LinearLayout deviceContainer = findViewById(R.id.device_list_container);

        // Gestion du bouton retour
        Button backBtn = findViewById(R.id.btn_back_devices);
        backBtn.setOnClickListener(v -> finish());

        // Mode démo : si activé, on simule des device_ids
        if (isDemo) {
            DeviceManager.setDemoDevices();
            updateListView(deviceContainer, DeviceManager.getAvailableDeviceIds());
        } else {
            // Sinon, requête UDP au serveur
            JSONObject json = new JSONObject();
            try {
                json.put("request", "get-devices");
                json.put("data", new JSONObject());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new UdpClient(ip, port, json.toString(), data -> {
                DeviceManager.updateDevicesFromResponse(data);

                new Handler(Looper.getMainLooper()).post(() -> {
                    List<String> devices = DeviceManager.getAvailableDeviceIds();

                    if (devices.isEmpty()) {
                        Toast.makeText(this, "Aucun device trouvé.", Toast.LENGTH_SHORT).show();
                    } else {
                        updateListView(deviceContainer, devices);
                    }
                });
            }).start();
        }
    }

    /**
     * Met à jour le ListView avec les devices disponibles
     */
    private void updateListView(LinearLayout container, List<String> devices) {
        container.removeAllViews();  // Vide la liste avant de la remplir

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String id : devices) {
            View item = inflater.inflate(R.layout.item_device, container, false);
            TextView label = item.findViewById(R.id.device_item_label);
            label.setText("Device ID : " + id);

            item.setOnClickListener(v -> {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra("ip", ip);
                intent.putExtra("port", port);
                intent.putExtra("device_id", id);
                intent.putExtra("demo", isDemo);
                startActivity(intent);
            });

            container.addView(item);
        }
    }
}
