package com.example.tdandroid.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de convertir les noms de capteurs
 * en codes abrégés utilisés dans les échanges avec le serveur
 */
public class SensorUtils {
    /**
     * Convertit une liste de noms de capteurs en leurs codes associés :
     * - Température → T
     * - Luminosité  → L
     * - Humidité    → H
     * - Pression    → P
     *
     * @param labels Liste des noms de capteurs
     * @return Liste des codes correspondants pour communiquer avec le serveur
     */
    public static List<String> mapLabelsToCodes(List<String> labels) {
        List<String> codes = new ArrayList<>();

        for (String capteur : labels) {
            switch (capteur) {
                case "Température": codes.add("T"); break;
                case "Luminosité":  codes.add("L"); break;
                case "Humidité":    codes.add("H"); break;
                case "Pression":    codes.add("P"); break;
                default:break;
            }
        }

        return codes;
    }
}
