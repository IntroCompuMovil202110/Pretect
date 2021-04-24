package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretect.Utils.Functions;
import com.example.pretect.Utils.Permisos;
import com.example.pretect.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Usuario
    User user;

    //UI
    Button panico, aceptarClave, cancelarClave, aceptarMensaje, cancelarMensaje ;
    RelativeLayout avisoClave, avisoMensaje;
    TextView clave, saludo;
    BottomNavigationView menuInferior;

    //Variables
    String claveFalsa;
    String claveVerdadera;
    int contador = 0;
    boolean estado = false;

    //Permisos
    String permAudio = Manifest.permission.RECORD_AUDIO;
    private static final int AUDIO_PERMISSION_ID = 5;

    //Grabaciones
    private MediaPlayer player = null;
    private MediaRecorder recorder = null;
    private static String fileName = null;

    //
    //Base de datos
    private static final String PATH_USERS = "users/";
    private static final String TAG = "MainScreen";

    //Atributos
    FirebaseDatabase database;
    DatabaseReference myRef;

    //firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Inflate
        panico = findViewById(R.id.botonPanico);
        aceptarClave = findViewById(R.id.aceptarClave);
        cancelarClave = findViewById(R.id.cancelarClave);
        aceptarMensaje = findViewById(R.id.aceptarMensaje);
        cancelarMensaje = findViewById(R.id.cancelarMensaje);
        clave = findViewById(R.id.claveSecereta);
        saludo = findViewById(R.id.saludo);
        avisoClave = findViewById(R.id.avisoClave);
        avisoMensaje = findViewById(R.id.avisoMensaje);

        //Hacer los avisos adicionales invisibles
        avisoClave.setVisibility(View.INVISIBLE);
        avisoMensaje.setVisibility(View.INVISIBLE);

        //Menu Inferior
        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });

        //Permisos
        Permisos.requestPermission(
                this,
                permAudio,
                "Se recomienda grabar el audio en situaciones de peligro",
                AUDIO_PERMISSION_ID
        );

        //
        //base de datos
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //obtiene el correo
        String userEmail = mAuth.getCurrentUser().getEmail();
        cargarDatos(userEmail);

        panico.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(estado == false){
                    contador++;
                    if(contador==3){
                        panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.rojo_principal));
                        estado=true;
                        grabarAudio();
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
                    //parar la grabación
                    onRecord(false);
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

    private void grabarAudio() {
        if (ContextCompat.checkSelfPermission(this, permAudio) == PackageManager.PERMISSION_GRANTED) {
            //configuramos la dir del archivo
            Date currentTime = Calendar.getInstance().getTime();
            String fecha = currentTime.toString();
            fileName = getExternalCacheDir().getAbsolutePath();
            fileName += "/pretect " + fecha + ".3gp";
            Log.i(TAG, fileName);
            //comienza a grabar
            onRecord(true);
        }else{
            return;
        }
    }

    private void cargarDatos(String userEmail){
        //obtiene referencia de base de datos usuarios
        myRef= database.getReference(PATH_USERS);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Recorrer todos los elementos buscando el que tenga el correo adecuado
                for (DataSnapshot elemento: snapshot.getChildren()) {
                    if (elemento.child("email").getValue().equals(userEmail)) {
                        //guardo el usuario
                        user = elemento.getValue(User.class);
                        saludo.setText(user.getUserName());
                        claveFalsa = user.getBait_phrase();
                        claveVerdadera = user.getSafety_phrase();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       /* if (ContextCompat.checkSelfPermission(this, permAudio) == PackageManager.PERMISSION_GRANTED) {
            user.setGrabarAudio(true);
        }else{
            user.setGrabarAudio(false);
        }*/
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }


}