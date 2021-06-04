package com.chustle.mascota.ui_alimentacion;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DialogFragmentTimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TimePickerListener listener;
    Calendar c;

    public DialogFragmentTimePicker(TimePickerListener listener, Calendar c) {
        this.c = c;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
       listener.onTimeSet(hourOfDay, minute);
    }

    interface TimePickerListener {
        void onTimeSet(int hour, int minute);
    }
}