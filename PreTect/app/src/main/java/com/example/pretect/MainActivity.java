package com.example.pretect;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //TAG
    private static final String TAG = "MainScreen";

    //Usuario
    User user;
    String userKey, userName, userMail;
    ArrayList<FindFriends> list = new ArrayList<>();
    static ArrayList<String> userContacts = new ArrayList<>();

    //Views
    Button panico, aceptarClave, cancelarClave, aceptarMensaje, cancelarMensaje;
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
    //Paths
    private static final String PATH_USERS = "users/";

    //Atributos
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference mContacts;
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

    String userID;
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

        //pedir permiso
        Permisos.requestPermission(
                this,
                LOCATION_NAME,
                "Es necesario activar tu ubicación en el GPS",
                LOCATION_PERMISSION_ID
        );
        //iniciar ubicacion
        initUbicacion();


        //
        //base de datos
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mContacts = FirebaseDatabase.getInstance().getReference("contacts");

        //obtiene el correo
        userID = mAuth.getCurrentUser().getUid();

        //esto se llama cada vez que hay una actualizacion del GPS
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.i(TAG, "Longitud " + location.getLongitude());
                    Log.i(TAG, "Latitud " + location.getLatitude());
                    try {
                        //Actualizo la informacion del usuario en la bd
                        user.setLatitude(location.getLatitude());
                        user.setLongitude(location.getLongitude());
                        //Actualizo mi ubicacion
                        myRef = database.getReference(PATH_USERS);
                        myRef.child(userKey).child("latitude").setValue(location.getLatitude());
                        myRef.child(userKey).child("longitude").setValue(location.getLongitude());
                        //actualizar la ubicacion de todas las persona que me tienen agregado
                        DatabaseReference myRef2 = database.getReference("users/" + userKey + "/contacts");
                        myRef2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                                    String updateUserId = keyId.getKey();
                                    DatabaseReference myRef3 = database.getReference("users/" + updateUserId + "/contactsUbication/" + userKey);
                                    myRef3.child("latitude").setValue(location.getLatitude());
                                    myRef3.child("longitude").setValue(location.getLongitude());
                                }
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        };

        //panico onClickListener
        panico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estado == false) {
                    contador++;
                    if (contador == 3) {

                        //Permisos
                        Permisos.requestPermission(
                                MainActivity.this,
                                permAudio,
                                "Se recomienda grabar el audio en situaciones de peligro",
                                AUDIO_PERMISSION_ID
                        );

                        panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.rojo_principal));
                        estado = true;
                        grabarAudio();
                        //cambiar estado en la bd
                        DatabaseReference myRef = database.getReference("users/" + userKey);
                        myRef.child("state").setValue(estado);
                        //actualizar a todos los contactos
                        DatabaseReference myRef2 = database.getReference("users/" + userKey + "/contacts");
                        myRef2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                                    String updateUserId = keyId.getKey();
                                    DatabaseReference myRef3 = database.getReference("users/" + updateUserId + "/contacts/" + userKey);
                                    myRef3.child("state").setValue(true);
                                }
                            }
                        });

                        myRef2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                                    String updateUserId = keyId.getKey();
                                    DatabaseReference myRef3 = database.getReference("users/" + updateUserId + "/contactsUbication/" + userKey);
                                    myRef3.child("state").setValue(true);
                                }
                            }
                        });
                        user.setState(true);
                    }
                } else {
                    contador--;
                    if (contador == 0) {
                        avisoClave.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        aceptarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String claves = clave.getText().toString();
                if (claves.equals(claveVerdadera) || claves.equals(claveFalsa)) {
                    panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.verde_principal));
                    estado = false;
                    //parar la grabación
                    onRecord(false);
                    DatabaseReference myRef = database.getReference("users/" + userKey);
                    myRef.child("state").setValue(estado);
                    //actualizar a todos los contactos
                    DatabaseReference myRef2 = database.getReference("users/" + userKey + "/contacts");
                    myRef2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                                String updateUserId = keyId.getKey();
                                DatabaseReference myRef3 = database.getReference("users/" + updateUserId + "/contacts/" + userKey);
                                myRef3.child("state").setValue(false);

                            }
                        }
                    });

                    myRef2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                                String updateUserId = keyId.getKey();
                                DatabaseReference myRef3 = database.getReference("users/" + updateUserId + "/contactsUbication/" + userKey);
                                myRef3.child("state").setValue(false);
                            }
                        }
                    });

                    avisoClave.setVisibility(View.INVISIBLE);
                    avisoMensaje.setVisibility(View.VISIBLE);
                    user.setState(false);
                } else {
                    Toast.makeText(getApplicationContext(), "Clave incorrecta", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avisoClave.setVisibility(View.INVISIBLE);
                contador = 3;
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

    //creamos el objeto que establece cada cuanto y como pido la localizacion
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    //activar la actualizacion automatica de la ubicacion
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, LOCATION_NAME) == PackageManager.PERMISSION_GRANTED) {
            locationClient.requestLocationUpdates(locationRequest,
                    locationCallback, null);
        }
    }

    //pausar la actualizacion automatica de la ubicacion
    private void stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback);
    }

    private void initUbicacion() {
        if (ContextCompat.checkSelfPermission(this, LOCATION_NAME) == PackageManager.PERMISSION_GRANTED) {
            checkSettingsLocation();
        }
    }

    //Crear el canal de notificaciones
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        } else {
            return;
        }
    }

    private void cargarDatos(String userID) {
        //obtiene referencia de base de datos usuarios
        myRef = database.getReference(PATH_USERS + userID);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                userKey = snapshot.getKey();
                saludo.setText("Hola " + user.getUserName() + "!");
                claveFalsa = user.getBait_phrase();
                claveVerdadera = user.getSafety_phrase();
                if(user.getState()==true){
                    panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.rojo_principal));
                    estado = true;
                    contador = 3;
                    grabarAudio();
                }
                else{
                    panico.setBackgroundColor(panico.getContext().getResources().getColor(R.color.verde_principal));
                    estado = false;
                    contador = 0;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkSettingsLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
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
                Log.i(TAG, "APAGADO");
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, SETTINGS_GPS);
                        } catch (IntentSender.SendIntentException sendEx) {
// Ignore the error.
                        }
                        break;
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

    private void startFirebaseStateListenerService() {
        Intent intent = new Intent(MainActivity.this, FirebaseStateListenerService.class);
        FirebaseStateListenerService.enqueueWork(MainActivity.this, intent);
        Log.i("FBSERVICE", "Invoking FB Service...");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_ID) {
            initUbicacion();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarDatos(userID);
        startFirebaseStateListenerService();
    }
}