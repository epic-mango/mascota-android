package com.chustle.mascota.ui_alimentacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chustle.mascota.Modelo.Horario;
import com.chustle.mascota.R;

import java.text.SimpleDateFormat;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_horario, parent, false);

        return new HorariosViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull HorariosViewHolder holder, int position) {
        holder.tvAlimento.setText(horarios.get(position).dispositivo.alimento);
        holder.tvGramos.setText(Integer.toString(horarios.get(position).gramos) + " " + holder.itemView.getContext().getString(R.string.gramos));
        holder.tvHorario.setText(horarios.get(position).hora);

    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }
}

class HorariosViewHolder extends RecyclerView.ViewHolder {
    TextView tvAlimento, tvGramos, tvHorario;

    public HorariosViewHolder(@NonNull View v, AdapterHorariosListener listener) {
        super(v);

        tvAlimento = v.findViewById(R.id.tvAlimento);
        tvGramos = v.findViewById(R.id.tvGramos);
        tvHorario = v.findViewById(R.id.tvHora);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clic(getAdapterPosition());
            }
        });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.longClic(getAdapterPosition());
                return true;
            }
        });
    }
}
