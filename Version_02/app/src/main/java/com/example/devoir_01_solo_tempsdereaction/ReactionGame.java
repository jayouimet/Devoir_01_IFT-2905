package com.example.devoir_01_solo_tempsdereaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ReactionGame {
    private int maximumTries;
    private int maximumDelay;
    private int minimumDelay;
    private int roundDelay;

    private int tryCounter;

    private long lastTime;
    private long totalTime;
    private long counterStart;

    private Random randomSeed;
    private Timer gameTimer;

    private GameListener gameListener;
    private GameState gameState;

    public long GetCounterStart() {
        return this.counterStart;
    }

    public long GetLastTime() {
        return this.lastTime;
    }

    public int GetMaximumTries() {
        return this.maximumTries;
    }

    public int GetTryCounter() {
        return this.tryCounter;
    }

    public long GetAverage() {
        try {
            return this.totalTime / this.maximumTries;
        } catch (ArithmeticException ex) {
            // This is supposed to be in case of a division by zero but it shouldn't really happen as the game settings aren't in a config file
            return 0;
        }
    }

    public void SetGameState(GameState newState) {
        this.gameState = newState;
        this.stateChanged();
    }

    private void stateChanged() {
        gameListener.UpdateUI(this.GetGameState());
    }

    public GameState GetGameState() {
        return this.gameState;
    }

    public ReactionGame(MainActivity UI) {
        this.gameState = GameState.NotStarted;

        this.gameListener = new GameListener();
        this.gameListener.AddListener(UI);

        this.maximumTries = 5;
        this.maximumDelay = 10000;
        this.minimumDelay = 3000;
        this.roundDelay = 1500;
    }

    public ReactionGame(MainActivity UI, int maxTries, int minDelay, int maxDelay, int rndDelay) {
        this.gameState = GameState.NotStarted;

        this.gameListener = new GameListener();
        this.gameListener.AddListener(UI);

        this.maximumTries = maxTries;
        this.minimumDelay = minDelay;
        this.maximumDelay = maxDelay;
        this.roundDelay = rndDelay;
    }

    public void startGame() {
        this.SetGameState(GameState.Holding);
        this.tryCounter = 0;

        this.totalTime = 0;
        this.lastTime = 0;

        this.randomSeed = new Random();
    }

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

    private void nextTry() {
        this.startGameTimer();
        this.SetGameState(GameState.Holding);
    }

    private void goodEndTry() {
        this.lastTime = System.currentTimeMillis() - this.counterStart;
        this.totalTime += this.lastTime;
        this.tryCounter++;
        this.SetGameState(GameState.GoodAnswer);
        this.endTry();
    }

    private void badEndTry() {
        this.lastTime = 0;
        this.SetGameState(GameState.BadAnswer);
        this.endTry();
    }

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

    public void endGame() {
        this.gameListener.StopTimer();
        this.SetGameState(GameState.Ended);
        this.tryCounter = 0;
    }

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
