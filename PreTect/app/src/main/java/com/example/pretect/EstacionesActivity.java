package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pretect.Utils.AdapterEstacionesClass;
import com.example.pretect.Utils.Estacion;
import com.example.pretect.Utils.Functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class EstacionesActivity extends AppCompatActivity {

    static RecyclerView estacionesList;
    BottomNavigationView menuInferior;
    AdapterEstacionesClass adapterClass;
    ArrayList<Estacion> list = new ArrayList<>();
    Estacion[] listEstaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estaciones);

        estacionesList = (RecyclerView) findViewById(R.id.estacionesList);
        estacionesList.setHasFixedSize(true);
        estacionesList.setLayoutManager(new LinearLayoutManager(this));

        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });
    }

    public void readApi(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String api = "https://www.datos.gov.co/resource/jwvi-unqh.json?departamento=METROPOLITANA%20DE%20%20BOGOTA";
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, api, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response){
                        try{
                            Gson gson = new Gson();
                            listEstaciones = gson.fromJson(response.toString(), Estacion[].class);
                            Collections.addAll(list, listEstaciones);
                            adapterClass = new AdapterEstacionesClass(list);
                            estacionesList.setAdapter(adapterClass);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.i("TAG", "Error handling rest invocation"+error.getCause());
                    }
                }
                );
        queue.add(req);

    }

    @Override
    protected void onStart(){
        super.onStart();
        readApi();
    }
}