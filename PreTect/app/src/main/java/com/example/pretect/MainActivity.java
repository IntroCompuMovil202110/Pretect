package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretect.Utils.Functions;
import com.example.pretect.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int contador = 0;
    boolean estado = false;
    Button panico, aceptarClave, cancelarClave, aceptarMensaje, cancelarMensaje;
    RelativeLayout avisoClave, avisoMensaje;
    TextView clave;

    String claveFalsa = "falsa";
    String claveVerdadera = "verdadera";
    BottomNavigationView menuInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Prueba Mapas (QUITAR)
        Intent intent=new Intent(this,MapActivity.class);
        startActivity(intent);

        panico = findViewById(R.id.botonPanico);
        aceptarClave = findViewById(R.id.aceptarClave);
        cancelarClave = findViewById(R.id.cancelarClave);
        aceptarMensaje = findViewById(R.id.aceptarMensaje);
        cancelarMensaje = findViewById(R.id.cancelarMensaje);
        clave = findViewById(R.id.claveSecereta);

        avisoClave = findViewById(R.id.avisoClave);
        avisoMensaje = findViewById(R.id.avisoMensaje);

        avisoClave.setVisibility(View.INVISIBLE);
        avisoMensaje.setVisibility(View.INVISIBLE);

        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });

        panico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estado == false){
                    contador++;
                    if(contador==3){
                        panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.rojo_principal));
                        estado=true;
                    }
                }else{
                    contador--;
                    if(contador==0){
                        avisoClave.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        aceptarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String claves = clave.getText().toString();
                if(claves.equals(claveVerdadera) || claves.equals(claveFalsa)){
                    panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.verde_principal));
                    estado=false;
                    avisoClave.setVisibility(View.INVISIBLE);
                    avisoMensaje.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(), "Clave incorrecta", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelarClave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                avisoClave.setVisibility(View.INVISIBLE);
                contador=3;
            }
        });

        cancelarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avisoMensaje.setVisibility(View.INVISIBLE);
            }
        });

        aceptarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avisoMensaje.setVisibility(View.INVISIBLE);
            }
        });
    }



}