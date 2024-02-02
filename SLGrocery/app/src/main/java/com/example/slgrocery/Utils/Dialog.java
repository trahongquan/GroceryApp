package com.example.slgrocery.Utils;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Dialog extends DialogFragment {

    private final String dialogTitle;
    private final String dialogMessage;
    private String positiveBtnText = "Ok";

    public Dialog(String dialogTitle, String dialogMessage) {
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
    }

    public Dialog(String dialogTitle, String dialogMessage, String positiveBtnText) {
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.positiveBtnText = positiveBtnText;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMessage);
        builder.setPositiveButton(positiveBtnText, (dialog, which) -> {
        });
        return builder.create();
    }
}
