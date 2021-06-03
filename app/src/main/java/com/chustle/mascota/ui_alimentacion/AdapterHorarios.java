package com.chustle.mascota.ui_alimentacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chustle.mascota.Modelo.Dispositivo;
import com.chustle.mascota.Modelo.Horario;
import com.chustle.mascota.R;

import java.util.ArrayList;

interface AdapterHorariosListener {
    void clic(int position);

    void longClic(int position);
}

public class AdapterHorarios extends RecyclerView.Adapter<HorariosViewHolder> {
    ArrayList<Horario> horarios;
    AdapterHorariosListener listener;

    public AdapterHorarios(ArrayList<Horario> horarios, AdapterHorariosListener listener) {
        this.horarios = horarios;
        this.listener = listener;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public HorariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dispositivo, parent, false);

        return new HorariosViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull HorariosViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }
}

class HorariosViewHolder extends RecyclerView.ViewHolder {
    public HorariosViewHolder(@NonNull View v, AdapterHorariosListener listener) {
        super(v);
    }
}
