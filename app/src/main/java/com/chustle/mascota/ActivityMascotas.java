package com.chustle.mascota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chustle.mascota.Modelo.Mascota;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityMascotas extends AppCompatActivity {

    RecyclerView rvMascotas;
    FloatingActionButton fabAgregarMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas);
        setTitle(getString(R.string.mascotas));

        rvMascotas = findViewById(R.id.rvMascotas);
        rvMascotas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        initRVMascotas();
        
        fabAgregarMascota = findViewById(R.id.fabAgregarMascota);
        initFABAgregarMascota();
        
    }

    private void initFABAgregarMascota() {

        fabAgregarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogFragmentEditarMascota(new DialogFragmentEditarMascota.EditarMascotaListener() {
                    @Override
                    public void aceptar(Mascota mascota, int codigoDeEdicion) {

                    }
                },new Mascota());

                dialogFragment.show(getSupportFragmentManager(), "");
            }
        });

    }

    private void initRVMascotas() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mascotas_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuCerrarSesion:

                SharedPreferences preferenciasCuenta = getSharedPreferences("cuenta", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferenciasCuenta.edit();

                editor.remove("token");

                editor.commit();

                startActivity(new Intent(getApplicationContext(), ActivityPrincipal.class));

                finish();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}