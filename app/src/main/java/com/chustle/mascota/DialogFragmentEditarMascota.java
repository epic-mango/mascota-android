package com.chustle.mascota;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chustle.mascota.Modelo.Mascota;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DialogFragmentEditarMascota extends DialogFragment {

    EditarMascotaListener listener;
    Mascota mascota;
    Spinner spnEspecie, spnRaza;
    EditText etNombreMascota, etNacimiento;

    public DialogFragmentEditarMascota(EditarMascotaListener listener, Mascota mascota) {
        this.listener = listener;
        this.mascota = mascota;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = requireActivity().getLayoutInflater().inflate(R.layout.dialogfragment_editarmascota, null);
        builder.setCancelable(false);

        initComponentes(root);

        builder.setView(root);

        return builder.create();
    }

    private void initComponentes(View v) {
        etNombreMascota = v.findViewById(R.id.etNombreMascota);

        initSpnEspecie(v);

        spnRaza = v.findViewById(R.id.spnRaza);

        etNacimiento = v.findViewById(R.id.etNacimientoMascota);

        etNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragmentDate = new DialogFragmentDatePicker(new DialogFragmentDatePicker.OnDateSet() {
                    @Override
                    public void onDateSet(int year, int month, int dayOfMonth) {
                        etNacimiento.setText(dayOfMonth+" / "+month+" / "+year);
                    }
                });

                fragmentDate.show(getActivity().getSupportFragmentManager(), null);
            }
        });

    }

    private void initSpnEspecie(View v) {
        spnEspecie = v.findViewById(R.id.spnEspecie);

        List<String> especies = new ArrayList<>();
        especies.add("Perro");
        especies.add("Gato");

        ArrayAdapter<String> adapterEspecies = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, especies);
        adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnEspecie.setAdapter(adapterEspecies);

        spnEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                List<String> razas = new ArrayList<>();

                InputStream input;
                switch (position) {
                    case 1:
                        input = getResources().openRawResource(R.raw.gatos);
                        break;
                    default:
                        input = getResources().openRawResource(R.raw.perros);
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(input));

                try {

                    String linea;
                    while ((linea = br.readLine()) != null) {
                        razas.add(linea);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setSpinnerAdapter(spnRaza, razas);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerAdapter(Spinner spinner, List<String> itemList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public interface EditarMascotaListener {
        int MASCOTA_AGREGADA = 0, MASCOTA_EDITADA = 1, MASCOTA_ELIMINADA = 2;

        void aceptar(Mascota mascota, int codigoDeEdicion);

    }

}
