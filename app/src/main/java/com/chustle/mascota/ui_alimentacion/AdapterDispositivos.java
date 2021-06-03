package com.chustle.mascota.ui_alimentacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chustle.mascota.Modelo.Dispositivo;
import com.chustle.mascota.R;

import java.util.ArrayList;

public class AdapterDispositivos extends RecyclerView.Adapter<DispositivosViewHolder>{
    ArrayList<Dispositivo> dispositivos;
    AdapterDispositivosListener listener;

    public AdapterDispositivos(ArrayList<Dispositivo> dispositivos, AdapterDispositivosListener listener){
        this.dispositivos=dispositivos;
        this.listener=listener;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public DispositivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dispositivo, parent, false);

        return new DispositivosViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull DispositivosViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

interface AdapterDispositivosListener {
    void clic(int position);
    void longClic(int position);
}

class DispositivosViewHolder extends RecyclerView.ViewHolder{
    public DispositivosViewHolder(@NonNull View v, AdapterDispositivosListener listener) {
        super(v);
    }
}
