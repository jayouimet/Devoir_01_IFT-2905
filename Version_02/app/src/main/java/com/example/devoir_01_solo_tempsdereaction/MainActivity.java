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
    private ReactionGame reactionGame;

    private Button button;
    private TextView txtViewTimer;
    private TextView txtViewTryCounter;

    private Timer updateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.txtViewTryCounter = findViewById(R.id.tryCounterTextView);
        this.txtViewTimer = findViewById(R.id.tryTimerTextView);
        this.button = findViewById(R.id.button);

        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reactionGame.ButtonPressed();
            }
        });

        this.initialiseBaseUI();
    }

    private void initialiseBaseUI() {

        MainActivity ui = this;
        reactionGame = new ReactionGame(ui);
        /*txtViewTryCounter.setText(String.format(getResources().getString(R.string.try_counter_name), reactionGame.GetTryCounter() + 1, reactionGame.GetMaximumTries()));
        txtViewTimer.setText(String.format(getResources().getString(R.string.ms_counter_name), 0));*/
        txtViewTryCounter.setText("");
        txtViewTimer.setText("");
        button.setText(getResources().getString(R.string.start_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    private void initialiseWaitUI() {
        android.util.Log.d("WaitBefore", "WaitBefore");
        txtViewTryCounter.setText(String.format(getResources().getString(R.string.try_counter_name), reactionGame.GetTryCounter() + 1, reactionGame.GetMaximumTries()));
        txtViewTimer.setText(String.format(getResources().getString(R.string.ms_counter_name), 0));
        button.setText(getResources().getString(R.string.wait_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.grey));
        android.util.Log.d("WaitAfter", "WaitAfter");
    }

    private void initialisePressUI() {
        button.setText(getResources().getString(R.string.press_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.bumblebee));
        android.util.Log.d("Press", "Press");
    }

    private void initialiseTooSoonUI() {
        button.setText(getResources().getString(R.string.too_soon_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.red));
        android.util.Log.d("Bad", "Bad");
    }

    private void initialiseWellDoneUI() {
        button.setText(getResources().getString(R.string.well_done_button_instruction_name));
        button.setBackgroundColor(getResources().getColor(R.color.green));
        android.util.Log.d("Well Done", "Well Done");
    }

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

    private void showScore() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ScoreDialog scoreDialog = new ScoreDialog(String.format(getResources().getString(R.string.average_reaction_time_name), reactionGame.GetAverage()));
                scoreDialog.show(getSupportFragmentManager(), "scoreDialog");
            }
        });
    }

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