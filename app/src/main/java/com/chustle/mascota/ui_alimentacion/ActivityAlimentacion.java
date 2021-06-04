package com.chustle.mascota.ui_alimentacion;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActivityAlimentacion extends AppCompatActivity {

    ArrayList<Dispositivo> dispositivos = new ArrayList<>();
    ArrayList<Horario> horarios = new ArrayList<>();

    FloatingActionButton fabAgregar, fabAgregarAlimento, fabAgregarHorario;
    RecyclerView rvHorarios;
    boolean abierto = false;
    int id;
    String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentacion);

        inicializarComponentes();

        setTitle(getString(R.string.alimentos_bebidas) + " " + getString(R.string.de) + " " + nombre);

    }

    private void inicializarComponentes() {
        id = getIntent().getExtras().getInt("id");
        nombre = getIntent().getExtras().getString("nombre");

        //---------------------------------FLOATING ACTION BUTTONS


        fabAgregar = findViewById(R.id.fabAgregar);
        fabAgregarAlimento = findViewById(R.id.fabAgregarAlimento);
        fabAgregarAlimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarDispositivo();
            }
        });


        fabAgregarHorario = findViewById(R.id.fabAgregarHorario);
        fabAgregarHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dispositivos.size() == 0)
                    agregarDispositivo();
                else {

                    String[] alimentos = new String[dispositivos.size()];

                    for (int i = 0; i < alimentos.length; i++) {
                        alimentos[i] = dispositivos.get(i).alimento;
                    }
                    DialogFragment fragmentAlimentos = new DialogFragmentElegirAlimento(alimentos, new DialogFragmentElegirAlimento.ElegirAlimentoListener() {
                        @Override
                        public void aceptar(int which) {
                            agregarHorario(dispositivos.get(which));
                        }
                    });
                    fragmentAlimentos.show(ActivityAlimentacion.this.getSupportFragmentManager(), "");
                }
            }
        });

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


        //----------------------------------------RECYCLER VIEW DISPOSITIVOS
        rvHorarios = findViewById(R.id.rVDispositivos);
        rvHorarios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rvHorarios.setAdapter(new AdapterHorarios(horarios, new AdapterHorariosListener() {
            @Override
            public void clic(int position) {
                editarHorario(horarios.get(position));
            }

            @Override
            public void longClic(int position) {

            }
        }));

        //------------------------------------------ArrayList DISPOSITIVOS
        getDispositivos();
    }

    private void getDispositivos() {

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
                                    JSONObject token = json.getJSONObject("mac_tokens");

                                    Dispositivo dispositivo = new Dispositivo();
                                    dispositivo.mac = dato.getString("mac");
                                    dispositivo.alimento = dato.getString("alimento");
                                    dispositivo.serie = Integer.parseInt(dato.getString("serie"));

                                    agregarListaDipositivo(dispositivo, token.getString(dispositivo.mac));
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
    }

    private void getHorarios(Dispositivo dispositivo) {


        RequestQueue cola = Volley.newRequestQueue(getApplicationContext());

        String URL = Uri.parse(Config.URL + "horarios.php")
                .buildUpon()
                .appendQueryParameter("mac", dispositivo.mac)
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("GET HORARIOS", response);


                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("estado");
                            JSONArray datos = json.getJSONArray("datos");


                            if (estado.equals("true")) {

                                for (int i = 0; i < datos.length(); i++) {
                                    JSONObject dato = (JSONObject) datos.get(i);

                                    Horario horario = new Horario();
                                    horario.dispositivo = dispositivo;
                                    horario.id = Integer.parseInt(dato.getString("id"));
                                    horario.gramos = Integer.parseInt(dato.getString("gramos"));
                                    horario.minuto = Integer.parseInt(dato.getString("minuto"));

                                    SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.HOUR_OF_DAY, horario.minuto / 60);
                                    c.set(Calendar.MINUTE, horario.minuto % 60);

                                    horario.hora = formatoHora.format(c.getTime());
                                    agregarListaHorarios(horario);
                                }
                            }

                            if (datos.length() == 0)
                                agregarHorario(dispositivo);

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
    }

    private void agregarListaHorarios(Horario horario) {
        horarios.add(horario);

        Collections.sort(horarios, new HorariosSorter());

        rvHorarios.getAdapter().notifyItemInserted(horarios.indexOf(horario));
    }

    void editarHorario(Horario horario) {
        DialogFragment dialogFragment = new DialogFragmentEditarHorario(new DialogFragmentEditarHorario.EditarHorarioListener() {
            @Override
            public void aceptar(Horario horario, boolean inserted) {

                if (inserted){
                    rvHorarios.getAdapter().notifyItemInserted(horarios.indexOf(horario));
                    horarios.add(horario);
                }
                else
                    rvHorarios.getAdapter().notifyItemChanged(horarios.indexOf(horario));
            }
        }, horario);

        dialogFragment.show(getSupportFragmentManager(), "");
    }

    void agregarHorario(Dispositivo dispositivo) {
        Horario horario = new Horario();
        horario.dispositivo = dispositivo;


        editarHorario(horario);

    }

    void agregarListaDipositivo(Dispositivo dispositivo, String mac_token) {
        SharedPreferences preferences = getSharedPreferences("mac_tokens", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(dispositivo.mac, mac_token);
        editor.commit();

        dispositivos.add(dispositivo);

        getHorarios(dispositivo);
    }

    void editarDispositivo(Dispositivo dispositivo) {

        DialogFragment dialogFragment = new DialogFragmentEditarDispositivo(new EditarDispositivoListener() {
            @Override
            public void aceptar(Dispositivo dispositivo, String mac_token) {
                agregarListaDipositivo(dispositivo, mac_token);
            }
        }, dispositivo, id);

        dialogFragment.show(getSupportFragmentManager(), "");
    }

    void agregarDispositivo() {
        editarDispositivo(new Dispositivo());
    }
}