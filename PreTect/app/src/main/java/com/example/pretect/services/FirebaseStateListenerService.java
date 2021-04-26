package com.example.pretect.services;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pretect.MainActivity;
import com.example.pretect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirebaseStateListenerService extends JobIntentService {

    private final static int JOB_ID = 12;
    //Firebase
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseUsers = db.getReference("users");
    DatabaseReference mDatabaseContacts = db.getReference("contacts");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ValueEventListener vel;

    public static void enqueueWork(Context context, Intent intent){
        enqueueWork(context, FirebaseStateListenerService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("FBSERVICE", "Running FB");
        vel = mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot keyId: snapshot.getChildren()){
                    if(MainActivity.isContact(keyId.getKey()) && keyId.child("state").getValue(boolean.class)){
                        Log.i("FBSERVICE", "Esucuchando contacto: " + keyId.getKey());
                        buildAndShowNotification(keyId.child("name").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onStopCurrentWork() {
        mDatabaseUsers.removeEventListener(vel);
        return super.onStopCurrentWork();
    }

    private void buildAndShowNotification(String friend) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        mBuilder.setContentTitle("Alerta.");
        mBuilder.setContentText("Tu contacto " + friend + " a activado la alarma.");
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
