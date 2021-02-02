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
    // Notre Listener custom
    private GameListener gameListener;
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
        gameListener.UpdateUI(this.GetGameState());
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

        this.gameListener = new GameListener();
        this.gameListener.AddListener(UI);

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

        this.gameListener = new GameListener();
        this.gameListener.AddListener(UI);

        this.maximumTries = maxTries;
        this.minimumDelay = minDelay;
        this.maximumDelay = maxDelay;
        this.roundDelay = rndDelay;
    }

    /**
     * Début de la partie
     */
    public void startGame() {
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
        int delay = Math.abs(randomSeed.nextInt()) % (maximumDelay - minimumDelay) + minimumDelay;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                counterStart = System.currentTimeMillis();
                SetGameState(GameState.Press);
                gameListener.StartTimer();
            }
        };
        this.gameTimer = new Timer();
        this.gameTimer.schedule(task, (long)delay);
    }

    /**
     * On passe au prochain essai
     */
    private void nextTry() {
        this.startGameTimer();
        this.SetGameState(GameState.Holding);
    }

    /**
     * Appelé si on obtient une bonne entrée de l'utilisateur
     */
    private void goodEndTry() {
        this.lastTime = System.currentTimeMillis() - this.counterStart;
        this.totalTime += this.lastTime;
        this.tryCounter++;
        this.SetGameState(GameState.GoodAnswer);
        this.endTry();
    }

    /**
     * Appelé si on obtient une bonne entrée de l'utilisateur
     */
    private void badEndTry() {
        this.lastTime = 0;
        this.SetGameState(GameState.BadAnswer);
        this.endTry();
    }

    /**
     * Termine le tour actuel et commence le prochain ou fini la partie dépendemment du numéro d'essai
     */
    private void endTry() {
        this.gameListener.StopTimer();
        this.gameTimer.cancel();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (tryCounter == maximumTries) {
                    endGame();
                } else {
                    nextTry();
                }
            }
        };

        this.gameTimer = new Timer();
        this.gameTimer.schedule(task, this.roundDelay);
    }

    /**
     * Termine la partie actuelle
     */
    public void endGame() {
        this.gameListener.StopTimer();
        this.SetGameState(GameState.Ended);
        this.tryCounter = 0;
    }

    /**
     * Fonction appelée lors de chaque clique de l'utilisateur
     */
    public void ButtonPressed() {
        switch (this.GetGameState()) {
            case NotStarted:
            case Ended:
                this.startGame();
                this.nextTry();
                break;
            case Press:
                this.goodEndTry();
                break;
            case BadAnswer:
            case GoodAnswer:
                break;
            case Holding:
                this.badEndTry();
                break;
        }
    }
}
