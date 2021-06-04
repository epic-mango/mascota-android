package com.chustle.mascota.ui_alimentacion;

import com.chustle.mascota.Modelo.Horario;

import java.util.Comparator;

public class HorariosSorter implements Comparator<Horario> {
    @Override
    public int compare(Horario o1, Horario o2) {
        return o1.minuto > o2.minuto?1:-1;
    }
}
