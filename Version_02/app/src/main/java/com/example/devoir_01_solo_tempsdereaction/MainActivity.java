package com.example.devoir_01_solo_tempsdereaction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    // Class de jeu où se passera la logique
    private ReactionGame reactionGame;
    // Les éléments de l'interface
    private Button button;
    private TextView txtViewTimer;
    private TextView txtViewTryCounter;
    // Timer utiliser pour "schedule" les mises à jours du décompte de temps
    private Timer updateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // À la création, on initisalise les variables des éléments d'interfaces pour qu'ils fassent référence à ceux-ci
        this.txtViewTryCounter = findViewById(R.id.tryCounterTextView);
        this.txtViewTimer = findViewById(R.id.tryTimerTextView);
        this.button = findViewById(R.id.button);
        // On ajoute un évènement On Click au boutton
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reactionGame.ButtonPressed();
            }
        });
        // On initialise l'interface à celui initial du jeu
        this.initialiseBaseUI();
    }

    /**
     * Affichage de l'interface de base du jeu
     */
    private void initialiseBaseUI() {
        reactionGame = new ReactionGame(this);
        txtViewTryCounter.setText("");
        txtViewTimer.setText("");
        button.setText(getResources().getString(R.string.start_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    /**
     * Affichage de l'interface d'attente du jeu
     */
    private void initialiseWaitUI() {
        txtViewTryCounter.setText(String.format(getResources().getString(R.string.try_counter_name), reactionGame.GetTryCounter() + 1, reactionGame.GetMaximumTries()));
        txtViewTimer.setText(String.format(getResources().getString(R.string.ms_counter_name), 0));
        button.setText(getResources().getString(R.string.wait_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    /**
     * Affichage de l'interface où on indique à l'utilisateur d'appuyer
     */
    private void initialisePressUI() {
        button.setText(getResources().getString(R.string.press_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.bumblebee));
    }

    /**
     * Affichage de l'interface signifiant à l'utilisateur qu'il a appuyé trop vite
     */
    private void initialiseTooSoonUI() {
        button.setText(getResources().getString(R.string.too_soon_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.red));
    }

    /**
     * Affichage de l'interface signifiant que l'utilisateur a appuyé au bon moment
     */
    private void initialiseWellDoneUI() {
        button.setText(getResources().getString(R.string.well_done_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.green));
    }

    /**
     * Fonction appelée par le listener GameListener lors d'un changement de l'état du jeu
     * @param state L'état du jeu lors de l'appel de l'évènement
     */
    public void UpdateUI(GameState state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case GoodAnswer:
                        initialiseWellDoneUI();
                        break;
                    case Press:
                        initialisePressUI();
                        break;
                    case Holding:
                        initialiseWaitUI();
                        break;
                    case BadAnswer:
                        initialiseTooSoonUI();
                        break;
                    case Ended:
                        showScore();
                    case NotStarted:
                    default:
                        initialiseBaseUI();
                        break;
                }
            }
        });
    }

    /**
     * Affichage du score moyen de l'utilisateur à l'aide de ScoreDialog, une interface qui hérite de la classe Dialog
     */
    private void showScore() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ScoreDialog scoreDialog = new ScoreDialog(String.format(getResources().getString(R.string.average_reaction_time_name), reactionGame.GetAverage()));
                scoreDialog.show(getSupportFragmentManager(), "scoreDialog");
            }
        });
    }

    /**
     * Démarrage d'un timer qui met à jour la boite textuelle timer à chaque 53 millisecondes avec la différence du temps actuel et un snapshot du temps où l'état à changé à appuyer
     */
    public void StartTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtViewTimer.setText(String.format(getResources().getString(R.string.ms_counter_name), System.currentTimeMillis() - reactionGame.GetCounterStart()));
                    }
                });
            }
        };

        this.updateTimer = new Timer();
        this.updateTimer.schedule(task, 0, 53);
    }

    /**
     * Arrêt du timer et mise à jour de la boite de texte du timer avec la différence entre le temps où l'utilisateur à appuyer sur le bouton et un snapshot du temps de début de l'essai
     */
    public void StopTimer() {
        updateTimer.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtViewTimer.setText(String.format(getResources().getString(R.string.ms_counter_name), reactionGame.GetLastTime()));
            }
        });
    }
}