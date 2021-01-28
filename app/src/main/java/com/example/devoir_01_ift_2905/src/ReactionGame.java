package com.example.devoir_01_ift_2905.src;

import com.example.devoir_01_ift_2905.MainActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ReactionGame {
    // Config properties
    private int maximumRounds;
    private int maximumDelay;
    private int minimumDelay;
    private int roundDelay;

    // Attributes
    private int roundCounter;
    private long lastTime;
    private long totalTime;
    private long counterStart;

    private Random randomSeed;
    private Timer gameTimer;

    // Event listeners and game state
    private GameListener gameListener;
    private GameState gameState;

    // Getter for when we last started counting time
    public long GetCounterStart() {
        return this.counterStart;
    }

    // Getter for the last time we had on the clock
    public long GetLastTime() {
        return this.lastTime;
    }

    // Get the maximum of tries allowed
    public int GetMaximumTries() {
        return this.maximumRounds;
    }

    // Get at what try we are
    public int GetTryCounter() {
        return this.roundCounter;
    }

    // Get average time
    public long GetAverage() {
        try {
            return this.totalTime / this.maximumRounds;
        } catch (ArithmeticException ex) {
            // This is supposed to be in case of a division by zero but it shouldn't really happen as the game settings aren't in a config file
            return 0;
        }
    }

    // Setter for the game state, has to be used as it makes a call to the UI
    public void SetGameState(GameState newState) {
        this.gameState = newState;
        this.stateChanged();
    }

    // Calls the UI when the game state is changed
    private void stateChanged() {
        gameListener.UpdateUI(this.GetGameState());
    }

    // Getter for the game state
    public GameState GetGameState() {
        return this.gameState;
    }

    // Base constructor
    public ReactionGame(MainActivity UI) {
        this.gameState = GameState.New;

        this.gameListener = new GameListener();
        this.gameListener.AddListener(UI);

        this.maximumRounds = 5;
        this.maximumDelay = 5000;
        this.minimumDelay = 2000;
        this.roundDelay = 1500;
    }

    // Constructor with settings
    public ReactionGame(MainActivity UI, int maxTries, int minDelay, int maxDelay, int rndDelay) {
        this.gameState = GameState.New;

        this.gameListener = new GameListener();
        this.gameListener.AddListener(UI);

        this.maximumRounds = maxTries;
        this.minimumDelay = minDelay;
        this.maximumDelay = maxDelay;
        this.roundDelay = rndDelay;
    }

    // Starts a new game
    public void startGame() {
        this.SetGameState(GameState.Wait);
        this.roundCounter = 0;

        this.totalTime = 0;
        this.lastTime = 0;

        this.randomSeed = new Random();
    }

    // Starts the timer for which the user needs to wait before pressing the button
    private void startWaitTimer() {
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

    // Initiate the next round
    private void nextRound() {
        this.startWaitTimer();
        this.SetGameState(GameState.Wait);
    }

    // Called when the round is ended with a valid input
    private void goodEndRound() {
        this.lastTime = System.currentTimeMillis() - this.counterStart;
        this.totalTime += this.lastTime;
        this.roundCounter++;
        this.SetGameState(GameState.Good);
        this.endRound();
    }

    private void badEndRound() {
        this.lastTime = 0;
        this.SetGameState(GameState.Bad);
        this.endRound();
    }

    // Called when the round is ended with an invalid input
    private void endRound() {
        this.gameListener.StopTimer();
        this.gameTimer.cancel();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (roundCounter == maximumRounds) {
                    endGame();
                } else {
                    nextRound();
                }
            }
        };

        this.gameTimer = new Timer();
        this.gameTimer.schedule(task, this.roundDelay);
    }

    // Called after every round
    public void endGame() {
        this.gameListener.StopTimer();
        this.SetGameState(GameState.Ended);
        this.roundCounter = 0;
    }

    // Called when the button is pressed
    public GameState ButtonPressed() {
        switch (this.GetGameState()) {
            case New:
            case Ended:
                this.startGame();
                this.nextRound();
                break;
            case Press:
                this.goodEndRound();
                break;
            case Bad:
            case Good:
                break;
            case Wait:
                this.badEndRound();
                break;
        }

        return this.GetGameState();
    }
}
