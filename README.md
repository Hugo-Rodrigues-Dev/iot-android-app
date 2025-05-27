# Projet Android IoT – CPE Lyon

## Objectif

Cette application Android a été développée dans le cadre du projet "Développement embarqué et IoT" à CPE Lyon.  
Elle permet de se connecter à un serveur IoT, de sélectionner un objet connecté (micro:bit), et d'afficher en temps réel les données de ses capteurs (température, humidité, pression, luminosité), avec la possibilité de personnaliser l'ordre d'affichage.

---

## Architecture de l'application

L'application est structurée en trois activités principales :

1. **ConnexionActivity**
   - Saisie de l'adresse IP et du port du serveur
   - Bouton pour se connecter ou activer le **mode démo**

2. **DeviceSelectionActivity**
   - Affichage de la liste des objets connectés
   - Sélection d’un objet pour visualiser ses capteurs

3. **DashboardActivity**
   - Affichage en temps réel des données du micro:bit sélectionné
   - Graphique dynamique du capteur prioritaire
   - Possibilité de réordonner les capteurs (drag & drop)
   - Envoi de l’ordre d’affichage au serveur

---

## Fonctionnalités clés

- Connexion réseau en UDP
- Réception et affichage des données capteurs
- Graphique en temps réel (MPAndroidChart)
- Réorganisation des capteurs via `RecyclerView`
- Mode démo avec données simulées
- Interface claire et adaptée au design CPE

---

## Mode démo

Un bouton **"Activer le mode démo"** est disponible sur l'écran de connexion.  
Il permet de tester toutes les fonctionnalités de l'application **sans serveur ni micro:bit**.

---

## Technologies utilisées

- **Language** : Java  
- **UI** : Android SDK
- **Networking** : UDP sockets
- **Charts** : MPAndroidChart  
- **Data binding** : RecyclerView  
- **Architecture** : Modular & commented code (SOLID-friendly)

---

🚀 Setup
1. Clone the repo

2. Open in Android Studio

3. Run on emulator or device

---

Projet réalisé à CPE Lyon – 3IRC

Ce projet est destiné à un usage pédagogique.
