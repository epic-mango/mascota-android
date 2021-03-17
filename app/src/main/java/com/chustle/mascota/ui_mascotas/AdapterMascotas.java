package com.chustle.mascota.ui_mascotas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chustle.mascota.Modelo.Mascota;
import com.chustle.mascota.R;

import java.util.ArrayList;

public class AdapterMascotas extends RecyclerView.Adapter<MascotasViewHolder> {

    ArrayList<Mascota> mascotas;
    AdapterMascotasListener listener;

    public AdapterMascotas(ArrayList<Mascota> mascotas, AdapterMascotasListener listener) {
        this.mascotas = mascotas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MascotasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_mascota, parent, false);

        return new MascotasViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotasViewHolder holder, int position) {
        holder.tvNombre.setText(mascotas.get(position).nombre);
    }

    @Override
    public int getItemCount() {
        return mascotas.size();
    }

    interface AdapterMascotasListener {
        void onItemClick(int position);

        void onLongClickListener(int position);
    }
}

class MascotasViewHolder extends RecyclerView.ViewHolder {

    TextView tvNombre;

    public MascotasViewHolder(@NonNull View v, AdapterMascotas.AdapterMascotasListener listener) {
        super(v);


        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClickListener(getAdapterPosition());
                return true;
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(getAdapterPosition());
            }
        });

        tvNombre = v.findViewById(R.id.tvNombre);
    }
}
