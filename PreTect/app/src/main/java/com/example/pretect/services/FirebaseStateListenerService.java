package com.example.pretect.services;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pretect.MainActivity;
import com.example.pretect.R;
import com.example.pretect.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirebaseStateListenerService extends JobIntentService {

    //Identificador del servicio
    private final static int JOB_ID = 12;


    //Atributos de Firebase database
    //accedo a la base de datos a la sección de usuarios
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = db.getReference("users");
    ValueEventListener vel;
    //firebase authentication
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser usuario = mAuth.getCurrentUser();
    DatabaseReference myRef = db.getReference("users/" + usuario.getUid() +"/contacts");
    User usuarioAlerta;
    public static void enqueueWork(Context context, Intent intent){
        enqueueWork(context, FirebaseStateListenerService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                usuarioAlerta = snapshot.getValue(User.class);
                if(usuarioAlerta.getSolicitud().equals("aceptado") && usuarioAlerta.getState()==true)
                    buildAndShowNotification();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onStopCurrentWork() {
        myRef.removeEventListener(vel);
        return super.onStopCurrentWork();
    }

    private void buildAndShowNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        mBuilder.setContentTitle("Alerta.");
        mBuilder.setContentText("Tu contacto " + usuarioAlerta.getUserName() + " a activado la alarma.");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        int notificationId = 001;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, mBuilder.build());


    }
}
