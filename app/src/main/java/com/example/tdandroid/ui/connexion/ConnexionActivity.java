package com.example.tdandroid.ui.connexion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tdandroid.R;
import com.example.tdandroid.network.UdpClient;
import com.example.tdandroid.network.UdpCallback;
import com.example.tdandroid.ui.devices.DeviceSelectionActivity;

import org.json.JSONObject;

/**
 * Activité de connexion au serveur (adresse IP + port)
 * Permet aussi d'accéder à un mode démo sans connexion réseau
 */
public class ConnexionActivity extends AppCompatActivity {
    private EditText ipInput;
    private EditText portInput;
    private Button connectBtn;
    private Button demoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        // Références vers les composants de l'interface
        ipInput = findViewById(R.id.ip_input);
        portInput = findViewById(R.id.port_input);
        connectBtn = findViewById(R.id.connect_button);
        demoBtn = findViewById(R.id.demo_button);

        // Clic sur le bouton "Se connecter"
        connectBtn.setOnClickListener(v -> handleConnexion());

        // Clic sur "Mode Démo" → redirige sans connexion réseau
        demoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeviceSelectionActivity.class);
            intent.putExtra("demo", true);
            startActivity(intent);
        });
    }

    /**
     * Vérifie les champs et tente une connexion UDP avec un message "get-devices"
     */
    private void handleConnexion() {
        String ip = ipInput.getText().toString().trim();
        String portStr = portInput.getText().toString().trim();

        if (ip.isEmpty() || portStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Le port doit être un nombre valide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construction de la requête JSON
        JSONObject request = new JSONObject();
        try {
            request.put("request", "get-devices");
            request.put("data", new JSONObject());
        } catch (Exception e) {
            Toast.makeText(this, "Erreur interne JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Envoi de la requête UDP
        new UdpClient(ip, port, request.toString(), new UdpCallback() {
            @Override
            public void onUdpResponse(String response) {
                runOnUiThread(() -> {
                    if (response.contains("error")) {
                        Toast.makeText(ConnexionActivity.this, "Connexion échouée", Toast.LENGTH_LONG).show();
                    } else {
                        // Succès → passage à l'activité suivante avec les infos réseau
                        Intent intent = new Intent(ConnexionActivity.this, DeviceSelectionActivity.class);
                        intent.putExtra("ip", ip);
                        intent.putExtra("port", port);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
}