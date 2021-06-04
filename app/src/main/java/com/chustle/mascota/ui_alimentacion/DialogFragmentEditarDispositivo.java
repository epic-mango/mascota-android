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
import com.chustle.mascota.R;
import com.chustle.mascota.REST.Config;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

interface EditarDispositivoListener {
    //TODO: Agregar los métodos aceptar y eliminar

    void aceptar(Dispositivo dispositivo, String mac_token);
}

public class DialogFragmentEditarDispositivo extends DialogFragment {

    EditarDispositivoListener listener;
    Dispositivo dispositivo;

    EditText etAlimento, etMAC, etContrasena;
    Button btnAceptar, btnCancelar;
    int mascota;

    public DialogFragmentEditarDispositivo(EditarDispositivoListener listener, Dispositivo dispositivo, int mascota) {
        this.listener = listener;
        this.dispositivo = dispositivo;
        this.mascota = mascota;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = requireActivity().getLayoutInflater().inflate(R.layout.dialogfragment_editardispositivo, null);
        builder.setCancelable(false);

        initComponentes(root);

        builder.setView(root);

        builder.setTitle(dispositivo.mac.equals("-1") ? getString(R.string.agregar_alimento) : getString(R.string.editar_alimento));

        return builder.create();
    }

    private void initComponentes(View v) {
        etAlimento = v.findViewById(R.id.etHora);
        etMAC = v.findViewById(R.id.etMAC);
        etContrasena = v.findViewById(R.id.etContrasena);

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
    }

    private void aceptar() {

        if (etAlimento.getText().toString().equals("") ||
                etMAC.getText().toString().equals("") ||
                etContrasena.getText().toString().equals(""))
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.faltan_datos), Toast.LENGTH_SHORT).show();
        else {
            dispositivo.mac = etMAC.getText().toString();
            dispositivo.alimento = etAlimento.getText().toString();
                    putDispositivo();
        }

    }

    private void putDispositivo() {


        RequestQueue cola = Volley.newRequestQueue(getContext());

        String URL = Uri.parse(Config.URL + "dispositivos.php")
                .buildUpon()
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.PUT,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response editar dis", response);

                        try {


                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("registrado");
                            if (estado.equals("true")) {
                                String mac_token = json.getString("mac_token");
                                listener.aceptar(dispositivo, mac_token);
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

                params.put("mac", dispositivo.mac);
                params.put("pass", etContrasena.getText().toString());
                params.put("mascota", Integer.toString(mascota));
                params.put("alimento", dispositivo.alimento);

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
