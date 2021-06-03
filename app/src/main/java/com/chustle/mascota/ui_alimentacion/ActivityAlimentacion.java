package com.chustle.mascota.ui_alimentacion;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.chustle.mascota.animator.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityAlimentacion extends AppCompatActivity {

    ArrayList<Dispositivo> dispositivos = new ArrayList<>();
    ArrayList<Horario> horarios = new ArrayList<>();

    FloatingActionButton fabAgregar, fabAgregarAlimento, fabAgregarHorario;
    RecyclerView rvHorarios;
    boolean abierto = false;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentacion);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        id = getIntent().getExtras().getInt("id");

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
        //------------------------------------------ArrayList DISPOSITIVOS


        RequestQueue cola = Volley.newRequestQueue(getApplicationContext());

        String URL = Uri.parse(Config.URL + "dispositivos.php")
                .buildUpon()
                .appendQueryParameter("id", Integer.toString(id))
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Reponse", response);


                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("estado");
                            JSONArray datos = json.getJSONArray("datos");

                            if (estado.equals("true")) {

                                for (int i = 0; i < datos.length(); i++) {
                                    JSONObject dato = (JSONObject) datos.get(i);
                                    Dispositivo dispositivo = new Dispositivo();
                                    dispositivo.MAC = dato.getString("mac");
                                    dispositivo.alimento = dato.getString("alimento");
                                    dispositivo.serie = Integer.parseInt(dato.getString("serie"));


                                    dispositivos.add(dispositivo);

                                    if (dispositivo.horarios.size() == 0) {
                                        agregarHorario();
                                    } else
                                        for (Horario horario : dispositivo.horarios) {
                                            horarios.add(horario);
                                            rvHorarios.getAdapter().notifyItemInserted(horarios.indexOf(horario));
                                        }
                                }
                            }

                            if (dispositivos.size() == 0)
                                agregarDispositivo();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("", "onErrorResponse: ", error.getCause());
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", getSharedPreferences("cuenta", Context.MODE_PRIVATE).getString("token", ""));

                return headers;
            }
        };

        peticion.setRetryPolicy(new DefaultRetryPolicy(3600, 0, 0));
        cola.add(peticion);

        //----------------------------------------RECYCLER VIEW DISPOSITIVOS
        rvHorarios = findViewById(R.id.rVDispositivos);
        rvHorarios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rvHorarios.setAdapter(new AdapterHorarios(horarios, new AdapterHorariosListener() {
            @Override
            public void clic(int position) {

            }

            @Override
            public void longClic(int position) {

            }
        }));
    }

    void agregarHorario(){

    }

    void agregarDispositivo() {
        DialogFragment dialogFragment = new DialogFragmentEditarDispositivo(new EditarDispositivoListener() {
            @Override
            public void aceptar(Dispositivo dispositivo) {

            }
        }, new Dispositivo(), id);

        dialogFragment.show(getSupportFragmentManager(), "");
    }
}