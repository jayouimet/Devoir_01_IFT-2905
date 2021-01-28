package com.example.devoir_01_ift_2905;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView txtViewTryCounter;
    private TextView txtViewTimer;
    private Button button;

    // private ReactionGame reactionGame;

    // private Timer updateTimer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.txtViewTryCounter = findViewById(R.id.tryCounterTextView);
        this.txtViewTimer = findViewById(R.id.tryTimerTextView);
        this.button = findViewById(R.id.button);

        /*this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickedFunction();
            }
        });*/

        this.initialiseStartUI();
    }

    private void initialiseStartUI() {
        // this.reactionGame = new ReactionGame(this);
        // this.txtViewTryCounter.setText(String.format(getResources().getString(R.string.try_counter_name), this.reactionGame.GetTryCounter() + 1, this.reactionGame.GetMaximumTries()));
        this.txtViewTryCounter.setText(String.format(getResources().getString(R.string.try_counter_name), 1, 5));
        this.txtViewTimer.setText(String.format(getResources().getString(R.string.ms_counter_name), 0));
        this.button.setText(getResources().getString(R.string.start_button_instruction_name));
        this.button.setBackgroundColor(getResources().getColor(R.color.grey));
    }
}