package com.chustle.mascota.ui_mascotas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chustle.mascota.Modelo.Mascota;
import com.chustle.mascota.R;
import com.chustle.mascota.REST.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogFragmentEditarMascota extends DialogFragment {

    EditarMascotaListener listener;
    Mascota mascota;
    Spinner spnEspecie, spnRaza;
    EditText etNombreMascota, etNacimiento;

    Calendar calendar = Calendar.getInstance();

    Button btnAceptar, btnCancelar;

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

        builder.setTitle(mascota.id==-1?getString(R.string.agregar_mascota):getString(R.string.editar_mascota));

        return builder.create();
    }

    private void initComponentes(View v) {
        etNombreMascota = v.findViewById(R.id.etNombreMascota);

        initSpnEspecie(v);

        spnRaza = v.findViewById(R.id.spnRaza);
        initSpnRaza();

        initETNacimiento(v);

        initBtnCancelar(v);

        initBtnAceptar(v);

        if (mascota.nombre != null)
            etNombreMascota.setText(mascota.nombre);

        if (mascota.especie > -1)
            spnEspecie.setSelection(mascota.especie);

        if (mascota.raza > -1)
            spnRaza.setSelection(mascota.raza);

        if (mascota.nacimiento > -1) {
            calendar.setTimeInMillis(mascota.nacimiento);

            etNacimiento.setText(formatearFecha(calendar.getTimeInMillis()));
        }


    }

    private void initSpnRaza() {
        spnRaza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mascota.raza = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String formatearFecha(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd / MMM / yyyy");
        return sdf.format(new Date(time));
    }

    private void initBtnAceptar(View v) {
        btnAceptar = v.findViewById(R.id.btnAceptar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNombreMascota.getText().toString().equals("") ||
                        spnEspecie.getSelectedItemPosition() < 0 ||
                        spnRaza.getSelectedItemPosition() < 0 ||
                        etNacimiento.getText().toString().equals(""))
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.faltan_datos), Toast.LENGTH_SHORT).show();
                else {
                    mascota.nombre = etNombreMascota.getText().toString();
                    mascota.nacimiento = calendar.getTimeInMillis();

                    if (mascota.id == -1) {
                        postMascota();
                    } else {
                        putMascota();
                    }

                }
            }
        });
    }

    private void putMascota() {


        RequestQueue cola = Volley.newRequestQueue(getContext());

        String URL = Uri.parse(Config.URL + "mascotas.php")
                .buildUpon()
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.PUT,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response",response);

                        try {


                        JSONObject json = new JSONObject(response);
                           String estado = json.getString("estado");

                            if (estado.equals("true")) {
                                listener.aceptar(mascota);
                                dismiss();
                            } else
                                Toast.makeText(getContext(), getString(R.string.error_guardar), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), getString(R.string.error_red) + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("nombre", mascota.nombre);
                params.put("especie", Integer.toString(mascota.especie));
                params.put("raza", Integer.toString(mascota.raza));
                params.put("nacimiento", Long.toString(mascota.nacimiento));
                params.put("id", Integer.toString(mascota.id));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", getActivity().getSharedPreferences("cuenta", Context.MODE_PRIVATE).getString("token", ""));

                return headers;
            }
        };

        peticion.setRetryPolicy(new DefaultRetryPolicy(3600, 0, 0));
        cola.add(peticion);

    }

    private void postMascota() {
        RequestQueue cola = Volley.newRequestQueue(getContext());

        String URL = Uri.parse(Config.URL + "mascotas.php")
                .buildUpon()
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("estado");
                            String id = json.getString("id");

                            if (estado.equals("true")) {
                                mascota.id = Integer.parseInt(id);
                                listener.aceptar(mascota);
                                dismiss();
                            } else
                                Toast.makeText(getContext(), "Error: " + id, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), getString(R.string.error_red), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("nombre", mascota.nombre);
                params.put("especie", Integer.toString(mascota.especie));
                params.put("raza", Integer.toString(mascota.raza));
                params.put("nacimiento", Long.toString(mascota.nacimiento));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", getActivity().getSharedPreferences("cuenta", Context.MODE_PRIVATE).getString("token", ""));

                return headers;
            }
        };

        peticion.setRetryPolicy(new DefaultRetryPolicy(3600, 0, 0));
        cola.add(peticion);
    }

    private void initBtnCancelar(View v) {
        btnCancelar = v.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    private void initETNacimiento(View v) {
        etNacimiento = v.findViewById(R.id.etNacimientoMascota);

        etNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragmentDate = new DialogFragmentDatePicker(new DialogFragmentDatePicker.OnDateSet() {
                    @Override
                    public void onDateSet(int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        etNacimiento.setText(formatearFecha(calendar.getTimeInMillis()));
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
                spnRaza.setSelection(mascota.raza);

                mascota.especie = position;
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
        void aceptar(Mascota mascota);

    }

}
