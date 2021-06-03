package com.chustle.mascota.ui_alimentacion;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.chustle.mascota.Modelo.Dispositivo;
import com.chustle.mascota.R;
import com.chustle.mascota.animator.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ActivityAlimentacion extends AppCompatActivity {

    ArrayList<Dispositivo> dispositivos = new ArrayList<>();
    FloatingActionButton fabAgregar, fabAgregarAlimento, fabAgregarHorario;
    boolean abierto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentacion);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        int id = getIntent().getExtras().getInt("id");

        //---------------------------------FLOATING ACTION BUTTONS


        fabAgregar = findViewById(R.id.fabAgregar);
        fabAgregarAlimento = findViewById(R.id.fabAgregarAlimento);
        fabAgregarHorario = findViewById(R.id.fabAgregarHorario);

        ViewAnimation.init(fabAgregarAlimento);
        ViewAnimation.init(fabAgregarHorario);

        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (abierto) {
                    ViewAnimation.showOut(fabAgregarAlimento);
                    ViewAnimation.showOut(fabAgregarHorario);
                } else {
                    ViewAnimation.showIn(fabAgregarAlimento);
                    ViewAnimation.showIn(fabAgregarHorario);
                }

                abierto = ViewAnimation.rotarFAB(fabAgregar, !abierto);
            }
        });
    }
}