package com.example.devoir_01_solo_tempsdereaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ScoreDialog extends AppCompatDialogFragment {
    public String message;

    public ScoreDialog(String message) {
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Score")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teal_700));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.white));
    }
}
