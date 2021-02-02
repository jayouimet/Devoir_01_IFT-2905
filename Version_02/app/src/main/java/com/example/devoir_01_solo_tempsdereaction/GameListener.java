package com.example.devoir_01_solo_tempsdereaction;

import java.util.ArrayList;

public class GameListener {
    private ArrayList<MainActivity> listeners = new ArrayList<MainActivity>();

    public void AddListener(MainActivity toAdd) {
        listeners.add(toAdd);
    }

    public void UpdateUI(GameState newState) {
        for (MainActivity listener : listeners) {
            listener.UpdateUI(newState);
        }
    }

    public void StartTimer() {
        for (MainActivity listener : listeners) {
            listener.StartTimer();
        }
    }

    public void StopTimer() {
        for (MainActivity listener : listeners) {
            listener.StopTimer();
        }
    }
}
