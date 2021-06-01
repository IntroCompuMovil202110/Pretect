package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.pretect.Utils.Functions;
import com.example.pretect.adapters.AgregarAdapter;
import com.example.pretect.adapters.ContactAdapter;
import com.example.pretect.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    //TAG
    private static final String TAG = "ListActivity";

    //Usuarios encontrados
    ArrayList<User> usuarios = new ArrayList<>();

    //Adapter
    ContactAdapter contactAdapter;

    //Views
    SearchView userSearch;
    BottomNavigationView menuInferior;
    ListView mlista;

    FirebaseUser usuario;

    //Databse
    //Paths
    private static final String PATH_USERS = "users/";
    //firebase realtime database
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference refContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        //realtime database
        database= FirebaseDatabase.getInstance();

        //auth firebase
        usuario = mAuth.getCurrentUser();
        //users/myUserID
        Log.i(TAG,PATH_USERS+usuario.getUid());
        refContacts = database.getReference(PATH_USERS+usuario.getUid()+"/contacts/");

        //inflate
        mlista =  findViewById(R.id.messagesList);
        contactAdapter = new ContactAdapter(usuarios,this,usuario.getUid(),database);
        mlista.setAdapter(contactAdapter);

        loadContactos();

        //Menu Inferior
        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });


    }
    //funcion para buscar los contactos/amigos del usuario actual
    public void loadContactos() {
        myRef = database.getReference(PATH_USERS+usuario.getUid()+"/contacts/");
        myRef. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarios = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User myUser = singleSnapshot.getValue(User.class);
                    myUser.setId(singleSnapshot.getKey());
                    usuarios.add(myUser);
                }
                //actualizo el adaptador cuando llegan los datos
                contactAdapter = new ContactAdapter(usuarios,getBaseContext(),usuario.getUid(),database);
                mlista.setAdapter(contactAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });
    }


}