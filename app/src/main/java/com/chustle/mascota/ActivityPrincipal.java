package com.chustle.mascota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chustle.mascota.REST.Config;
import com.chustle.mascota.ui_mascotas.ActivityMascotas;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityPrincipal extends AppCompatActivity {

    EditText etId, etPass;
    Button btnIniciar, btnCrearCuenta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        etId = findViewById(R.id.etId);
        etPass = findViewById(R.id.etPass);

        btnIniciar = findViewById(R.id.btnIniciar);
        initBtnIniciar();

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        initBtnCrearCuenta();

        probarToken();
    }

    private void probarToken() {

        String token = this.getSharedPreferences("cuenta", Context.MODE_PRIVATE).getString("token", null);

        if (token != null) {
            RequestQueue queue = Volley.newRequestQueue(this);

            String URL = Uri.parse(Config.URL + "cuenta.php")
                    .buildUpon()
                    .build().toString();

            StringRequest request = new StringRequest(Request.Method.GET,
                    URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            login(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ERROR", "onErrorResponse: ", error.getCause());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();

                    headers.put("Authorization", token);

                    return headers;
                }
            };

            queue.add(request);
        }
    }


    private void login(String response) {
        try {
            JSONObject respuesta = new JSONObject(response);
            String estado = respuesta.getString("estado");
            String token = respuesta.getString("token");
            if (estado.equals("true")) {
                SharedPreferences preferences = getSharedPreferences("cuenta", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", token);
                editor.commit();

                startActivity(new Intent(getApplicationContext(), ActivityMascotas.class));

                finish();
            } else {
                if (token.equals("23000"))
                    Toast.makeText(this, getString(R.string.usuario_existe), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initBtnCrearCuenta() {
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etId.getText().toString().equals("") && !etPass.getText().toString().equals("")) {

                    RequestQueue cola = Volley.newRequestQueue(getApplicationContext());

                    String URL = Uri.parse(Config.URL + "cuenta.php")
                            .buildUpon()
                            .build().toString();

                    StringRequest peticion = new StringRequest(Request.Method.POST,
                            URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    login(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("ERROR", "onErrorResponse: ", error.getCause());
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parametros = new HashMap<>();

                            parametros.put("id", etId.getText().toString());
                            parametros.put("pass", etPass.getText().toString());
                            parametros.put("apodo", etId.getText().toString());

                            return parametros;
                        }
                    };


                    peticion.setRetryPolicy(new DefaultRetryPolicy(36000000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    cola.add(peticion);
                } else {
                    Toast.makeText(ActivityPrincipal.this, getString(R.string.faltan_datos), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initBtnIniciar() {
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etId.getText().toString().equals("") && !etId.getText().toString().equals("")) {


                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    String URL = Uri.parse(Config.URL + "cuenta.php")
                            .buildUpon()
                            .appendQueryParameter("id", etId.getText().toString())
                            .appendQueryParameter("pass", etPass.getText().toString())
                            .build().toString();

                    StringRequest request = new StringRequest(Request.Method.GET,
                            URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    login(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("ERROR", "onErrorResponse: ", error.getCause());
                                }
                            });

                    queue.add(request);
                } else
                    Toast.makeText(ActivityPrincipal.this, getString(R.string.faltan_datos), Toast.LENGTH_SHORT).show();

            }
        });
    }
}

