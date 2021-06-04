package com.chustle.mascota.ui_alimentacion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.chustle.mascota.Modelo.Dispositivo;
import com.chustle.mascota.Modelo.Horario;
import com.chustle.mascota.R;
import com.chustle.mascota.REST.Config;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

interface EditarHorarioListener {
    void aceptar(Horario horario);
}

public class DialogFragmentEditarHorario extends DialogFragment {


    EditarHorarioListener listener;
    Horario horario;

    EditText etHora, etGramos;
    Button btnAceptar, btnCancelar;

    Calendar c;


    public DialogFragmentEditarHorario(EditarHorarioListener listener, Horario horario) {
        this.listener = listener;
        this.horario = horario;

    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = requireActivity().getLayoutInflater().inflate(R.layout.dialogfragment_editarhorario, null);
        builder.setCancelable(false);

        initComponentes(root);

        builder.setView(root);

        builder.setTitle((horario.id == -1 ? getString(R.string.agregar_horario) : getString(R.string.editar_horario))+" "+getString(R.string.para)+ " " + horario.dispositivo.alimento);

        return builder.create();
    }

    private void initComponentes(View v) {

        if(horario.id == -1){
            c = Calendar.getInstance();
        } else {
            //TODO Poner las cosas que pasarían si fuera editar en lugar de crear
        }
        etGramos = v.findViewById(R.id.etGramos);
        //-----------------------------EDIT TEXT HORA
        etHora = v.findViewById(R.id.etHora);

        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment timePicker = new DialogFragmentTimePicker(new DialogFragmentTimePicker.TimePickerListener() {
                    @Override
                    public void onTimeSet(int hour, int minute) {

                        c.set(Calendar.HOUR_OF_DAY, hour);
                        c.set(Calendar.MINUTE, minute);
                        etHora.setText(new SimpleDateFormat("HH:mm").format(c.getTime()));

                        horario.minuto = hour * 60 + minute;
                    }
                }, c);

                timePicker.show(getParentFragmentManager(), null);
            }

        });




        //-------------------------------------------BUTTON ACEPTAR
        btnAceptar = v.findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptar();
            }
        });

        //---------------------------------------BUTTON CANCELAR
        btnCancelar = v.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //
    }

    private void aceptar() {

        if (etHora.getText().toString().equals("") ||
                etGramos.getText().toString().equals(""))
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.faltan_datos), Toast.LENGTH_SHORT).show();
        else {
            horario.gramos = Integer.parseInt(etGramos.getText().toString());

            if (horario.id == -1)
                postHorario();
            else
                putHorario();

        }

    }

    void postHorario() {
        String mac_token = getActivity().getSharedPreferences("mac_tokens", Context.MODE_PRIVATE).getString(horario.dispositivo.mac, null);

        RequestQueue cola = Volley.newRequestQueue(getContext());

        String URL = Uri.parse(Config.URL + "horarios.php")
                .buildUpon()
                .appendQueryParameter("mac_token", mac_token )
                .appendQueryParameter("minuto", Integer.toString(horario.minuto))
                .appendQueryParameter("gramos", Integer.toString(horario.gramos))
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response editar hor", response);

                        try {


                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("registrado");

                            if (estado.equals("true")) {
                                listener.aceptar(horario);
                                dismiss();
                            } else
                                Toast.makeText(getContext(), getString(R.string.datos_incorrectos), Toast.LENGTH_SHORT).show();

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", getActivity().getSharedPreferences("cuenta", Context.MODE_PRIVATE).getString("token", ""));

                return headers;
            }
        };

        peticion.setRetryPolicy(new DefaultRetryPolicy(3600, 0, 0));
        cola.add(peticion);

    }

    private void putHorario() {

        RequestQueue cola = Volley.newRequestQueue(getContext());

        String URL = Uri.parse(Config.URL + "horarios.php")
                .buildUpon()
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.PUT,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response editar hor", response);

                        try {


                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("registrado");

                            if (estado.equals("true")) {
                                listener.aceptar(horario);
                                dismiss();
                            } else
                                Toast.makeText(getContext(), getString(R.string.datos_incorrectos), Toast.LENGTH_SHORT).show();

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
                String mac_token = getActivity().getSharedPreferences("mac_tokens", Context.MODE_PRIVATE).getString(horario.dispositivo.mac, null);

                params.put("id", Integer.toString(horario.id));
                params.put("mac_token", horario.dispositivo.mac);
                params.put("minuto", Integer.toString(horario.minuto));
                params.put("gramos", Integer.toString(horario.gramos));

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
}
