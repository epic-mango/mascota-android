package com.chustle.mascota.ui_alimentacion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chustle.mascota.R;

import org.jetbrains.annotations.NotNull;

public class DialogFragmentElegirAlimento extends DialogFragment {

    String[] alimentos;
    ElegirAlimentoListener listener;

    public DialogFragmentElegirAlimento(String[] alimentos, ElegirAlimentoListener listener) {
        this.alimentos = alimentos;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle(getString(R.string.alimentos_bebidas));
        builder.setItems(alimentos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               listener.aceptar(which);
            }
        });

        return builder.create();
    }

    interface ElegirAlimentoListener {
        void aceptar(int which);
    }
}
