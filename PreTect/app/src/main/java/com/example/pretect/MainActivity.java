package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretect.Utils.FindFriends;
import com.example.pretect.Utils.Functions;
import com.example.pretect.Utils.Permisos;
import com.example.pretect.entities.Notification;
import com.example.pretect.entities.SingletoneUser;
import com.example.pretect.entities.User;
import com.example.pretect.services.FirebaseStateListenerService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class MainActivity extends AppCompatActivity {

    //Usuario
    User user;
    String userKey, userName, userMail;
    ArrayList<FindFriends> list = new ArrayList<>();
    static ArrayList<String> userContacts = new ArrayList<>();

    //UI
    Button panico, aceptarClave, cancelarClave, aceptarMensaje, cancelarMensaje ;
    RelativeLayout avisoClave, avisoMensaje;
    TextView clave, saludo;
    BottomNavigationView menuInferior;

    //Variables
    String claveFalsa;
    String claveVerdadera;
    int edad;
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
    private static final String PATH_LOGS = "logs/";
    private static final String TAG = "MainScreen";
    Location  toLogLocation;

    //Atributos
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference mContacts;
    DatabaseReference mLogs;
    public static String CHANNEL_ID = "PRETECT";

    //firebase authentication
    private FirebaseAuth mAuth;

    //Ubicación
    //Permisos
    private static final int LOCATION_PERMISSION_ID = 15;
    private static final String LOCATION_NAME = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int SETTINGS_GPS = 10;
    //atributos
    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    //singleton user
    SingletoneUser singletoneUser;

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

        //ubicacion
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = createLocationRequest();

        //singleton
        singletoneUser = SingletoneUser.getInstance();

        try{
            estado = singletoneUser.getData().getState();
            if(estado == true){
                panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.rojo_principal));
                estado = true;
                contador = 3;
            }else{
                panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.verde_principal));
                estado = false;
                contador=0;
            }
        }catch (Exception e){

        }



        Permisos.requestPermission(
                this,
                LOCATION_NAME,
                "Es necesario activar tu ubicación en el GPS",
                LOCATION_PERMISSION_ID
        );

        //initUbicacion();


        //
        //base de datos
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mContacts = FirebaseDatabase.getInstance().getReference("contacts");
        mLogs = FirebaseDatabase.getInstance().getReference(PATH_LOGS);

        //obtiene el correo
        String userEmail = mAuth.getCurrentUser().getEmail();
        cargarDatos(userEmail);

        //esto se llama cada vez que hay una actualizacion del GPS
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                toLogLocation = location;
                if(location != null){
                    Log.i(TAG, "Longitud " + location.getLongitude());
                    Log.i(TAG, "Latitud " + location.getLatitude());
                    try{
                        //Actualizo la informacion del usuario en la bd
                        user.setLatitude(location.getLatitude());
                        user.setLongitude(location.getLongitude());
                        myRef.child(userKey).child("latitude").setValue(location.getLatitude());
                        myRef.child(userKey).child("longitude").setValue(location.getLongitude());
                        //actualizo el singleton
                        singletoneUser.setData(user);
                    }catch (Exception e){

                    }

                }
            }
        };

        panico.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(estado == false){
                    contador++;
                    if(contador==3){

                        //Permisos
                        Permisos.requestPermission(
                                MainActivity.this,
                                permAudio,
                                "Se recomienda grabar el audio en situaciones de peligro",
                                AUDIO_PERMISSION_ID
                        );

                        panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.rojo_principal));
                        estado=true;
                        grabarAudio();
                        myRef.child(userKey).child("state").setValue(estado);
                        user.setState(true);
                        singletoneUser.setData(user);
                        logPanicActivation();
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
                    myRef.child(userKey).child("state").setValue(estado);
                    avisoClave.setVisibility(View.INVISIBLE);
                    avisoMensaje.setVisibility(View.VISIBLE);
                    user.setState(false);
                    singletoneUser.setData(user);
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

        createNotificationChannel();

    }


    private void logPanicActivation(){
        DatabaseReference pushedLog = mLogs.push();
        String keyToSave = pushedLog.getKey();
        Date currentTime = Calendar.getInstance().getTime();
       mLogs.child(keyToSave).child("latitude").setValue(toLogLocation.getLatitude());
       mLogs.child(keyToSave).child("longitude").setValue(toLogLocation.getLongitude());
       mLogs.child(keyToSave).child("age").setValue(edad);
       mLogs.child(keyToSave).child("time").setValue(currentTime);
    }

    //creamos el objeto que establece cada cuanto y como pido la localizacion
    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return  locationRequest;
    }

    //activar la actualizacion automatica de la ubicacion
    private void startLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this, LOCATION_NAME)== PackageManager.PERMISSION_GRANTED){
            locationClient.requestLocationUpdates(locationRequest,
                    locationCallback, null);
        }
    }

    //pausar la actualizacion automatica de la ubicacion
    private void stopLocationUpdates(){
        locationClient.removeLocationUpdates(locationCallback);
    }

    private void initUbicacion() {
        Log.i(TAG,"ANTES -1");
        if (ContextCompat.checkSelfPermission(this, LOCATION_NAME) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"ANTES 0");
            checkSettingsLocation();
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "channel";
            String description = "Channel Desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
                        saludo.setText("Hola " + user.getUserName()+"!");
                        claveFalsa = user.getBait_phrase();
                        claveVerdadera = user.getSafety_phrase();
                        singletoneUser.setData(user);
                        edad = user.getAge();
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
    private void checkSettingsLocation(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Log.i(TAG,"ANTES");
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        Log.i(TAG,"DESPUES");
        //si el GPS existe y ya esta prendido
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates(); //Todas las condiciones para recibir localizaciones
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"APAGADO");
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult (MainActivity.this,SETTINGS_GPS);
                        } catch (IntentSender.SendIntentException sendEx) {
// Ignore the error.
                        } break;
                    //en caso de que el celular no tenga GPS
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_GPS) {
            Log.i(TAG, "Entre");
            startLocationUpdates();
        } else {
            Toast.makeText(this,
                    "Sin acceso a localización, hardware deshabilitado!",
                    Toast.LENGTH_LONG).show();
        }
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
        singletoneUser.setRecorder(recorder);
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
        if(recorder==null){
            recorder = singletoneUser.getRecorder();
        }
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void startFirebaseStateListenerService() {
        Intent intent = new Intent(MainActivity.this, FirebaseStateListenerService.class);
        FirebaseStateListenerService.enqueueWork(MainActivity.this, intent);
        Log.i("FBSERVICE", "Invoking FB Service...");
    }

    private void current(){
        myRef.orderByKey().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot keyId: dataSnapshot.getChildren()){
                    if(keyId.child("email").getValue(String.class).equals(userMail)){
                        userKey = keyId.getKey();
                    }
                }
                searchContacts();
            }
        });
    }

    private void searchContacts(){
        mContacts.orderByKey().equalTo(userKey).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot keyId: dataSnapshot.getChildren()){
                    for(DataSnapshot keyContact: keyId.getChildren()){
                        userContacts.add(keyContact.getKey());
                    }
                }
                readUsers();
            }
        });
    }

    private void readUsers() {
        myRef.orderByKey().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot keyId: dataSnapshot.getChildren()){
                    int i = 0;
                    if(!keyId.getKey().equals(userKey)){
                        if(!isContact(keyId.getKey())){
                            list.add(keyId.getValue(FindFriends.class));
                            list.get(i).setKey(keyId.getKey());
                            i++;
                        }
                    }
                }
            }
        });
    }

    public static boolean isContact(String check){
        return userContacts.contains(check);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_ID){
            initUbicacion();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userMail = mAuth.getCurrentUser().getEmail();
        current();
        startFirebaseStateListenerService();
    }
}