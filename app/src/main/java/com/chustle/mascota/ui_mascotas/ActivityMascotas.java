package com.chustle.mascota.ui_mascotas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.chustle.mascota.ActivityPrincipal;
import com.chustle.mascota.Modelo.Mascota;
import com.chustle.mascota.R;
import com.chustle.mascota.REST.Config;
import com.chustle.mascota.animator.ViewAnimation;
import com.chustle.mascota.ui_alimentacion.ActivityAlimentacion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityMascotas extends AppCompatActivity {

    RecyclerView rvMascotas;
    FloatingActionButton fabAgregarMascota;
    ArrayList<Mascota> mascotas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas);
        setTitle(getString(R.string.mascotas));


        initRVMascotas();
        initMascotas();

        fabAgregarMascota = findViewById(R.id.fabAgregarMascota);
        initFABAgregarMascota();

    }

    private void initMascotas() {

        RequestQueue cola = Volley.newRequestQueue(getApplicationContext());

        String URL = Uri.parse(Config.URL + "mascotas.php")
                .buildUpon()
                .build().toString();

        StringRequest peticion = new StringRequest(Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("estado");
                            JSONArray datos = json.getJSONArray("datos");

                            Log.i("", "onResponse: "+ response);
                            if (estado.equals("true")) {

                                for (int i = 0; i < datos.length(); i++) {
                                    JSONObject dato = (JSONObject) datos.get(i);
                                    Mascota mascota = new Mascota();
                                    mascota.id = Integer.parseInt(dato.getString("id"));
                                    mascota.nombre = dato.getString("nombre");
                                    mascota.especie = Integer.parseInt(dato.getString("especie"));
                                    mascota.raza = Integer.parseInt(dato.getString("raza"));
                                    mascota.nacimiento = Long.parseLong(dato.getString("nacimiento"));
                                    mascota.usuario = dato.getString("usuario");


                                    mascotas.add(mascota);
                                    rvMascotas.getAdapter().notifyItemInserted(mascotas.indexOf(mascota));
                                }
                            }

                            if (mascotas.size()==0)
                                agregarMascota();

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

    private void agregarMascota(){

        DialogFragment dialogFragment = new DialogFragmentEditarMascota(new DialogFragmentEditarMascota.EditarMascotaListener() {
            @Override
            public void aceptar(Mascota mascota) {
                mascotas.add(mascota);
                rvMascotas.getAdapter().notifyItemInserted(mascotas.indexOf(mascota));


            }
        }, new Mascota());

        dialogFragment.show(getSupportFragmentManager(), "");
    }

    private void initFABAgregarMascota() {



        fabAgregarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarMascota();
            }
        });

    }

    private void initRVMascotas() {
        rvMascotas = findViewById(R.id.rvMascotas);
        rvMascotas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        rvMascotas.setAdapter(new AdapterMascotas(mascotas, new AdapterMascotas.AdapterMascotasListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", mascotas.get(position).id);
                bundle.putString("nombre", mascotas.get(position).nombre);

                startActivity(new Intent(getApplicationContext(), ActivityAlimentacion.class).putExtras(bundle));
            }

            @Override
            public void onLongClickListener(int position) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityMascotas.this);
                alert.setMessage(getResources().getString(R.string.advertencia_eliminar));
                alert.setTitle(getResources().getString(R.string.eliminar));
                alert.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue cola = Volley.newRequestQueue(getApplicationContext());
                        String URL = Uri.parse(Config.URL + "mascotas.php")
                                .buildUpon()
                                .appendQueryParameter("id", Integer.toString(mascotas.get(position).id))
                                .build().toString();


                        StringRequest peticion = new StringRequest(Request.Method.DELETE,
                                URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            Log.i("Response", response);
                                            JSONObject json = new JSONObject(response);
                                            String estado = json.getString("estado");

                                            if (estado.equals("true")) {
                                                mascotas.remove(position);
                                                rvMascotas.getAdapter().notifyItemRemoved(position);
                                            } else {
                                                Toast.makeText(ActivityMascotas.this, getString(R.string.error_eliminar), Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(ActivityMascotas.this, getString(R.string.error_red), Toast.LENGTH_SHORT).show();
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
                });

                alert.setNegativeButton(getString(R.string.cancelar), null);

                alert.create();
                alert.show();
            }
        }));
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