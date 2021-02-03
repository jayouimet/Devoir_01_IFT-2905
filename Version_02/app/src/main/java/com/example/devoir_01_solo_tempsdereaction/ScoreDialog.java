package com.example.devoir_01_solo_tempsdereaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Classe hériter de Dialog qu'on utilisera pour afficher le score
 */
public class ScoreDialog extends AppCompatDialogFragment {
    // Le message à afficher
    public String message;
    /**
     * Constructeur paramétré
     */
    public ScoreDialog(String message) {
        this.message = message;
    }

    /**
     * On override la fonction onCreate
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        // On créé un builder de Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // On initialise le titre pour qu'il affiche "Score" et le message avec le message ci-haut
        // On met un bouton positif ayant comme texte Ok mais qui ne fait rien de spécial si fermer sauf fermer le dialog
        builder.setTitle("Score")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    /**
     * Override de la fonction onStart pour chager des couleurs, car la couleur du bouton par défaut est atroce
     */
    @Override
    public void onStart() {
        super.onStart();
        // On change le background de la fenêtre et du bouton à blanc et la couleur du texte en teal_700
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        // Pour changer la couleur des boutons, il faut cast le ScoreDialog en AlertDialog

        // Comme on fait un cast, il faut gérer les exceptions de cast, par exemple si un appareil utilise un API plus vieux, il est possible que le cast ne fonctionne pas.
        // Dans ce cas, je ne veux pas que l'application crash, dans le pire des cas l'utilisateur se retrouvera avec un bouton mauve dégueu
        try {
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teal_700));
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.white));
        } catch (Exception e) {
            Log.e("Dialog Cast Exception", "Could not cast the dialog as an AlertDialog");
        }
    }
}
