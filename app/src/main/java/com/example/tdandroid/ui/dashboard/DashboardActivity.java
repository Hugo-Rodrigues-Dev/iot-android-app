package com.example.tdandroid.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tdandroid.R;
import com.example.tdandroid.network.UdpClient;
import com.example.tdandroid.utils.SensorUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.LinkedList;

/**
 * Affichage des valeurs des capteurs en temps réel
 * et gestion de l'ordre d'affichage.
 */
public class DashboardActivity extends AppCompatActivity {

    private String ip;
    private int port;
    private String deviceId;
    private boolean isDemo = false;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pollingTask;

    private SensorOrderAdapter adapter;
    private TextView tempView, humView, pressView, lumView;

    private final List<String> sensors = new ArrayList<>(Arrays.asList("Température", "Luminosité", "Humidité", "Pression"));

    // Graphe
    private LineChart chart;
    private LineDataSet chartDataSet;
    private LineData chartLineData;
    private LinkedList<Entry> chartEntries = new LinkedList<>();
    private int timeIndex = 0;
    private String currentDisplayedSensor = "Température"; // Par défaut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Récupère les paramètres reçus
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", 10000);
        deviceId = getIntent().getStringExtra("device_id");
        isDemo = getIntent().getBooleanExtra("demo", false);

        // Initialisation des composants
        tempView = findViewById(R.id.TextsBox_Temp);
        humView = findViewById(R.id.TextsBox_Hum);
        pressView = findViewById(R.id.TextsBox_Press);
        lumView = findViewById(R.id.LightSensorBox_value);
        chart = findViewById(R.id.chart_temp);

        // Initialisation des capteurs dans le RecyclerView
        RecyclerView recyclerView = findViewById(R.id.sensor_order_list);
        adapter = new SensorOrderAdapter(sensors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Drag & Drop sur les capteurs
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder from, RecyclerView.ViewHolder to) {
                Collections.swap(sensors, from.getAdapterPosition(), to.getAdapterPosition());
                adapter.notifyItemMoved(from.getAdapterPosition(), to.getAdapterPosition());

                // Réinitialise le graphe pour suivre le premier capteur
                resetGraphForNewSensor();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}
        });
        helper.attachToRecyclerView(recyclerView);

        // Configuration initiale du graphique
        setupChart();

        // Rafraîchissement automatique toutes les 5 secondes
        pollingTask = () -> {
            if (isDemo) simulateSensorData();
            else sendGetValuesRequest();

            handler.postDelayed(pollingTask, 5000);
        };
        handler.post(pollingTask);

        // Bouton : Valider l’affichage
        findViewById(R.id.btn_send_order).setOnClickListener(v -> {
            List<String> codes = SensorUtils.mapLabelsToCodes(adapter.getCurrentOrder());

            if (isDemo) {
                Toast.makeText(this, "Ordre d'affichage simulé (mode démo)", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = new JSONObject();
                json.put("request", "update-screen");

                JSONObject data = new JSONObject();
                data.put("device_id", deviceId);
                data.put("sensor_order", new JSONArray(codes));

                json.put("data", data);

                new UdpClient(ip, port, json.toString(), response -> {
                    runOnUiThread(() -> {
                        if (response.contains("error")) {
                            Toast.makeText(this, "Erreur lors de l’envoi", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Ordre envoyé avec succès", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Bouton retour
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    /**
     * Configure les options visuelles du graphique
     */
    private void setupChart() {
        chartDataSet = new LineDataSet(new ArrayList<>(), currentDisplayedSensor);
        chartDataSet.setColor(getResources().getColor(R.color.cpe_blue));
        chartDataSet.setValueTextColor(getResources().getColor(android.R.color.white));
        chartDataSet.setLineWidth(2f);
        chartDataSet.setCircleRadius(3f);
        chartDataSet.setDrawValues(false);
        chart.getLegend().setTextColor(getResources().getColor(android.R.color.white));

        chartLineData = new LineData(chartDataSet);
        chart.setData(chartLineData);
        chart.getDescription().setEnabled(false);

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTextColor(getResources().getColor(android.R.color.white));
        chart.getAxisLeft().setTextColor(getResources().getColor(android.R.color.white));
        chart.getAxisRight().setTextColor(getResources().getColor(android.R.color.white));

        chart.invalidate();
    }

    /**
     * Réinitialise le graphe quand on change le capteur en haut de liste
     */
    private void resetGraphForNewSensor() {
        currentDisplayedSensor = adapter.getCurrentOrder().get(0); // Nouveau capteur
        chartEntries.clear();

        timeIndex = 0;
        chartDataSet.setLabel(currentDisplayedSensor);
        chartDataSet.clear();

        chartLineData.notifyDataChanged();

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    /**
     * Envoie la requête pour récupérer les valeurs depuis le serveur
     */
    private void sendGetValuesRequest() {
        try {
            JSONObject json = new JSONObject();
            json.put("request", "get-values");

            JSONObject data = new JSONObject();
            data.put("device_id", deviceId);
            json.put("data", data);

            new UdpClient(ip, port, json.toString(), response -> {
                try {
                    JSONObject res = new JSONObject(response);
                    updateSensorViews(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mode démo : génère des données aléatoires
     */
    private void simulateSensorData() {
        Random rand = new Random();
        JSONObject fake = new JSONObject();
        try {
            fake.put("t", 18 + rand.nextInt(8));
            fake.put("h", 40 + rand.nextInt(20));
            fake.put("p", 1000 + rand.nextInt(30));
            fake.put("lux", 200 + rand.nextInt(300));
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateSensorViews(fake);
    }

    /**
     * Met à jour les champs de texte + graphe (uniquement pour le 1er capteur)
     */
    private void updateSensorViews(JSONObject data) {
        runOnUiThread(() -> {
            // Mise à jour des TextView texte
            tempView.setText("Température : " + data.optString("t", "—") + "°C");
            humView.setText("Humidité : " + data.optString("h", "—") + " %");
            pressView.setText("Pression : " + data.optString("p", "—") + " hPa");
            lumView.setText("Luminosité : " + data.optString("lux", "—") + " lx");

            // On regarde quel capteur est en premier dans l'ordre choisi
            String currentSensor = adapter.getCurrentOrder().get(0);
            String value = "0";
            String unit = "";
            int color = getResources().getColor(android.R.color.white);

            switch (currentSensor) {
                case "Température":
                    value = data.optString("t", "0");
                    unit = "°C";
                    color = getResources().getColor(R.color.red);
                    break;
                case "Humidité":
                    value = data.optString("h", "0");
                    unit = "%";
                    color = getResources().getColor(R.color.blue);
                    break;
                case "Pression":
                    value = data.optString("p", "0");
                    unit = "hPa";
                    color = getResources().getColor(R.color.green);
                    break;
                case "Luminosité":
                    value = data.optString("lux", "0");
                    unit = "lx";
                    color = getResources().getColor(R.color.yellow);
                    break;
            }

            try {
                float val = Float.parseFloat(value);
                chartEntries.add(new Entry(timeIndex++, val));
                if (chartEntries.size() > 20) chartEntries.removeFirst();

                chartDataSet.setValues(chartEntries);
                chartDataSet.setLabel(currentSensor + " (" + unit + ")");
                chartDataSet.setColor(color);
                chartDataSet.setCircleColor(color);

                chartLineData.notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}