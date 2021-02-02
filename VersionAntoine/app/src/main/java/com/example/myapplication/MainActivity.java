package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    Button button1;
    float tempsmoyen;
    int essai = 0;

    enum etat {
        REPOS, ATTENTE, JEU, TERMINE, ECHEC
    }

    etat actuel;
    long avant;
    long apres;
    Chronometer chrono;
    int[] temps = new int[5];


    public void resetAffichage() {

        button1.setBackgroundColor(getResources().getColor(R.color.grey));
        button1.setTextColor(getResources().getColor(R.color.white));
        button1.setText(R.string.debut);
        chrono = findViewById(R.id.chrono);
        chrono.setTextColor(getResources().getColor(R.color.white));
        TextView essaitext = findViewById(R.id.essai);
        essaitext.setText("");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(b1_listener);
        resetAffichage();
        actuel = etat.REPOS;
        essai = 1;
        avant = System.currentTimeMillis();
        apres = System.currentTimeMillis();



    }

    public void messageFin() {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(MainActivity.this);
        builder.setMessage("Votre temps moyen est : " + tempsmoyen + " secondes");

        // Set Alert Title
        builder.setTitle("Fin du jeu");


        // Show the Alert Dialog box

        builder
                .setPositiveButton(
                        "Termine",
                        (dialog, which) -> {
                            resetAffichage();
                            essai = 1;
                            actuel = etat.REPOS;

                            reset();

                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void reset() {
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.stop();
    }

    View.OnClickListener b1_listener = v -> faireEssai();


    public void faireEssai() {
        chrono.setTextColor(getResources().getColor(R.color.black));
        if (actuel == etat.ATTENTE) {

            button1.setText(R.string.tropvite);
            actuel = etat.ECHEC;
            button1.setTextColor(getResources().getColor(R.color.white));
            button1.setBackgroundColor(getResources().getColor(R.color.red));

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                actuel = etat.REPOS;
                button1.setBackgroundColor(getResources().getColor(R.color.grey));

                commencerAttente();


            }, 1500);


        } else if (actuel == etat.REPOS) {
            reset();
            commencerAttente();
        } else if (actuel == etat.JEU) {
            button1.setBackgroundColor(getResources().getColor(R.color.green));
            button1.setText(R.string.reussi);
            button1.setTextColor(getResources().getColor(R.color.black));
            essai++;
            reset();
            apres = System.currentTimeMillis();
            int diff = (int) (apres - avant);
            temps[essai - 2] = diff;


            if (essai == 6) {
                actuel = etat.TERMINE;
                int res = 0;
                for (int temp : temps) {
                    res += temp;

                }

                tempsmoyen = (float) res / (temps.length * 1000);
                messageFin();


            }

            final Handler handler = new Handler(Looper.getMainLooper());

            handler.postDelayed(() -> {
                if (etat.REPOS != actuel && etat.TERMINE != actuel) {
                    commencerAttente();
                }


            }, 1500);


        }


    }


    public void commencerAttente() {
        if (etat.JEU == actuel || etat.REPOS == actuel) {
            TextView essaitext = findViewById(R.id.essai);
            button1.setTextColor(getResources().getColor(R.color.white));
            button1.setBackgroundColor(getResources().getColor(R.color.grey));
            button1.setText(R.string.attente);
            actuel = etat.ATTENTE;
            int temps = (int) (Math.random() * 7 + 3) * 1000;
            essaitext.setText("Essai " + essai + " de 5");
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {

                if (actuel == etat.ATTENTE) {
                    button1.setBackgroundColor(getResources().getColor(R.color.yellow));
                    button1.setTextColor(getResources().getColor(R.color.black));
                    button1.setText(R.string.clique);
                    actuel = etat.JEU;
                    reset();
                    chrono.start();
                    avant = System.currentTimeMillis();
                }


            }, temps);
        }
    }


}