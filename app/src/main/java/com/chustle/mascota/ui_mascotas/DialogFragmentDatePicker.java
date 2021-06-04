package com.chustle.mascota.ui_mascotas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DialogFragmentDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    OnDateSet listener;
    Calendar c;

    public DialogFragmentDatePicker(OnDateSet listener, Calendar c) {
        this.listener = listener;
        this.c = c;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        listener.onDateSet(  year,  month,  dayOfMonth);
    }

    interface OnDateSet {
        void onDateSet( int year, int month, int dayOfMonth);
    }
}
