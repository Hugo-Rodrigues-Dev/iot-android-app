package com.example.tdandroid.ui.dashboard;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter permettant afficher dynamiquement la liste des capteurs dans un RecyclerView
 * Le RecyclerView nous permet de réorganiser l'ordre d'affichage des capteurs
 * en glissant-déposant les éléments (drag & drop).
 */
public class SensorOrderAdapter extends RecyclerView.Adapter<SensorOrderAdapter.ViewHolder> {
    // Liste des capteurs à afficher
    private final List<String> sensorList;

    /**
     * Constructeur du SensorAdapter
     * @param sensorList La liste des capteurs à afficher dans le RecyclerView
     */
    public SensorOrderAdapter(List<String> sensorList) {
        this.sensorList = sensorList;
    }

    /**
     * Méthode appelée par le RecyclerView lorsqu'il a besoin de créer un nouvel item visuel
     * Elle crée un simple TextView pour représenter un capteur
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());

        // Création du TextView pour chaque élément de la liste
        textView.setPadding(40, 30, 40, 30);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.DKGRAY);
        textView.setTextSize(18);

        return new ViewHolder(textView);
    }

    /**
     * Méthode appelée pour lier les données (texte) à un élément visuel existant
     * Elle est appelée automatiquement par Android à chaque fois qu’un item s’affiche.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(sensorList.get(position));
    }

    /**
     * Retourne le nombre total d’éléments dans la liste
     */
    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    /**
     * Permet de récupérer l’ordre actuel des capteurs
     * Pour construire un message à envoyer au serveur
     */
    public List<String> getCurrentOrder() {
        return sensorList;
    }

    /**
     * Classe interne ViewHolder : représentant un élément visuel de la liste
     * Ici, chaque item est simplement un TextView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            this.textView = itemView;
        }
    }
}
