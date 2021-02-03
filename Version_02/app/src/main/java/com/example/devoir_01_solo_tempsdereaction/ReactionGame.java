package com.example.devoir_01_solo_tempsdereaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ReactionGame {
    // Configuration du jeu
    private int maximumTries;
    private int maximumDelay;
    private int minimumDelay;
    private int roundDelay;
    // Compteur d'essai
    private int tryCounter;
    // Variables pour stocker les temps en milliseconde
    private long lastTime;
    private long totalTime;
    private long counterStart;
    // Random utiliser pour le timer ci-dessous, qui lui est ce qui va "schedule" les différentes actions dépendente d'un temps d'attente
    private Random randomSeed;
    private Timer gameTimer;
    // Référence à l'UI pour appeler les fonctions d'update
    private MainActivity mainActivity;
    // Enum d'états de jeu
    private GameState gameState;

    /**
     * Getter de la variable du snapshot du temps système lors du début de la phase Press de l'essai en milliseconde
     * @return Retourn un long qui représente le temps ou la phase de jeu a changée pour Press
     */
    public long GetCounterStart() {
        return this.counterStart;
    }

    /**
     * Getter de la variable qui sert à stocker le temps où l'utilisateur a appuyé sur le bouton au bon moment
     * @return Retourne un long qui représente le moment ou le joueur a fait un bon input
     */
    public long GetLastTime() {
        return this.lastTime;
    }

    /**
     * Getter de la variable de configuration maximumTries
     * @return Retourne le nombre maximum d'essai par partie permis à l'utilisateur
     */
    public int GetMaximumTries() {
        return this.maximumTries;
    }

    /**
     * Getter de la variable du compteur d'essai de l'utilisateur
     * @return Retourne le numéro d'essai actuel de l'utilisateur
     */
    public int GetTryCounter() {
        return this.tryCounter;
    }

    /**
     * Getter de la moyenne des essai du joueur
     * @return Retourn la moyenne de score de tous les essais du joueur
     */
    public long GetAverage() {
        try {
            return this.totalTime / this.maximumTries;
        } catch (ArithmeticException ex) {
            // Au cas où on ait une division par zéro, cela ne devrait pas arriver, car la configuration est hardcodée,
            // mais c'est nécessaire pour éviter un crash si on le mettait dans un fichier de config modifiable par un utilisateur par exemple
            return 0;
        }
    }

    /**
     * Setter de l'état du jeu actuel, dois être utilisé pour tout changement d'état de jeu, car il lève un évènement stateChanged qui averti le listener qu'il faut mettre à jour l'interface
     * @param newState Prend le nouvel état du jeu
     */
    public void SetGameState(GameState newState) {
        this.gameState = newState;
        this.stateChanged();
    }

    /**
     * Utilisé pour avertir le listener qu'il faut mettre à jour l'interface
     */
    private void stateChanged() {
        this.mainActivity.UpdateUI(this.GetGameState());
    }

    /**
     * Getter pour obtenir l'état actuel du jeu
     * @return Retourne l'état du jeu actuel
     */
    public GameState GetGameState() {
        return this.gameState;
    }

    /**
     * Constructeur par défaut du jeu
     * @param UI L'activité d'interface auquel le jeu est relié
     */
    public ReactionGame(MainActivity UI) {
        this.gameState = GameState.NotStarted;

        this.mainActivity = UI;

        this.maximumTries = 5;
        this.maximumDelay = 10000;
        this.minimumDelay = 3000;
        this.roundDelay = 1500;
    }

    /**
     * Constructeur paramétré avec options de configuration du jeu
     * @param UI L'activité d'interface auquel le jeu est relié
     * @param maxTries Nombres d'essais maximum
     * @param minDelay Temps minimum de délai avant que l'utilisateur puisse appuyer
     * @param maxDelay Temps maximum de délai avant que l'utilisateur puisse appuyer
     * @param rndDelay Délai entre chaque essai
     */
    public ReactionGame(MainActivity UI, int maxTries, int minDelay, int maxDelay, int rndDelay) {
        this.gameState = GameState.NotStarted;

        this.mainActivity = UI;

        this.maximumTries = maxTries;
        this.minimumDelay = minDelay;
        this.maximumDelay = maxDelay;
        this.roundDelay = rndDelay;
    }

    /**
     * Début de la partie
     */
    public void startGame() {
        // On initialise les variables du jeu et on le met dans l'état d'attente
        this.SetGameState(GameState.Holding);
        this.tryCounter = 0;

        this.totalTime = 0;
        this.lastTime = 0;

        this.randomSeed = new Random();
    }

    /**
     * Fonction qui part le timer de jeu qui défini le moment oû l'utilisateur pourra appuyer
     */
    private void startGameTimer() {
        // On met un délais avant l'exécution de la différence entre le délai maximum et le délai minimum
        int delay = Math.abs(randomSeed.nextInt()) % (maximumDelay - minimumDelay) + minimumDelay;
        // La tache à exécuter après le délai
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // On fait un snapshot du temps de la machine actuel
                counterStart = System.currentTimeMillis();
                // On met l'état du jeu à "Appuyer"
                SetGameState(GameState.Press);
                // On part le timer qui met à jour le UI
                mainActivity.StartTimer();
            }
        };
        // On initialise le timer et on lui attribue la tâche à exécuter
        this.gameTimer = new Timer();
        this.gameTimer.schedule(task, (long)delay);
    }

    /**
     * On passe au prochain essai
     */
    private void nextTry() {
        // On part le timer qui détermine le temps avant de permettre d'appuyer
        this.startGameTimer();
        // On met l'état en attente
        this.SetGameState(GameState.Holding);
    }

    /**
     * Appelé si on obtient une bonne entrée de l'utilisateur
     */
    private void goodEndTry() {
        // On prend un snapshot de la différence de temps entre le début et le moment où l'utilisateur a appuyé
        this.lastTime = System.currentTimeMillis() - this.counterStart;
        // On ajoute au temps total
        this.totalTime += this.lastTime;
        // Incrémentation du compteur d'essai
        this.tryCounter++;
        // État du jeu changé pour l'état "Bonne réponse"
        this.SetGameState(GameState.GoodAnswer);
        // On termine le tour
        this.endTry();
    }

    /**
     * Appelé si on obtient une bonne entrée de l'utilisateur
     */
    private void badEndTry() {
        // On ne compte pas le temps et on le met à 0
        this.lastTime = 0;
        // On change l'état pour "Mauvaise réponse"
        this.SetGameState(GameState.BadAnswer);
        // On termine le tour
        this.endTry();
    }

    /**
     * Termine le tour actuel et commence le prochain ou fini la partie dépendemment du numéro d'essai
     */
    private void endTry() {
        // On arrête les timers
        this.mainActivity.StopTimer();
        this.gameTimer.cancel();

        // On se part une nouvelle tache pour le timer
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Si l'utilisateur a fait tous ses essais, on termine le jeu, autrement on passe au prochain tour
                if (tryCounter == maximumTries) {
                    endGame();
                } else {
                    nextTry();
                }
            }
        };
        // On initialise le timer avec la tache ci-haut et on la "schedule" avec notre délai d'entre tour
        this.gameTimer = new Timer();
        this.gameTimer.schedule(task, this.roundDelay);
    }

    /**
     * Termine la partie actuelle
     */
    public void endGame() {
        // On arrête le refresh du compteur de l'UI
        this.mainActivity.StopTimer();
        // On change l'état pour "Terminé"
        this.SetGameState(GameState.Ended);
        // On réinitialise les essais
        this.tryCounter = 0;
    }

    /**
     * Fonction appelée lors de chaque clique de l'utilisateur
     */
    public void ButtonPressed() {
        // Switch sur l'état du jeu lors de l'entrée utilisateur
        switch (this.GetGameState()) {
            case NotStarted:
            case Ended:
                // Si le jeu n'est pas en cours, on l'initialise et on commence le premier tour
                this.startGame();
                this.nextTry();
                break;
            case Press:
                // S'il était temps d'appuyer, on continu avec la fonction de bonne réponse
                this.goodEndTry();
                break;
            case Holding:
                // Si l'utilisateur a appuyé trop vite, on continu avec la fonction de mauvaise réponse
                this.badEndTry();
                break;
                // Dans tous les autres cas, on ignore l'entrée utilisateur
            default:
                break;
        }
    }
}
