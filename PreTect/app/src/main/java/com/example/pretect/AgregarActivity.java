package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretect.Utils.AdapterClass;
import com.example.pretect.Utils.FindFriends;
import com.example.pretect.Utils.Functions;
import com.example.pretect.Utils.UsersAdapter;
import com.example.pretect.adapters.AgregarAdapter;
import com.example.pretect.entities.User;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AgregarActivity extends AppCompatActivity {

    //TAG
    private static final String TAG = "ListActivity";

    //Usuarios encontrados
    ArrayList<User> usuarios = new ArrayList<>();

    //Adapter
    AgregarAdapter agregarAdapter;

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
        setContentView(R.layout.activity_agregar);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        //realtime database
        database= FirebaseDatabase.getInstance();

        //auth firebase
        usuario = mAuth.getCurrentUser();
        //users/myUserID
        Log.i(TAG,PATH_USERS+usuario.getUid());
        refContacts = database.getReference(PATH_USERS+usuario.getUid()+"/contacts/");

        loadContactos();

        //inflate
        mlista =  findViewById(R.id.contactsList);
        userSearch=findViewById(R.id.userSearch);
        agregarAdapter = new AgregarAdapter(usuarios,this,usuario.getUid(),database);
        mlista.setAdapter(agregarAdapter);

        //Menu Inferior
        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });

        //on search listener for search bar
        userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadUsers(query);
                Toast.makeText(getBaseContext(), query,Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //funcion para buscar los usuarios
    public void loadUsers(String searchValue) {
        myRef = database.getReference(PATH_USERS);
        Query query = myRef.orderByChild("userName").startAt(searchValue).endAt(searchValue+"\uf8ff");
        query. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarios = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User myUser = singleSnapshot.getValue(User.class);
                    myUser.setId(singleSnapshot.getKey());
                    usuarios.add(myUser);
                }
                //actualizo el adaptador cuando llegan los datos
                agregarAdapter = new AgregarAdapter(usuarios,getBaseContext(),usuario.getUid(),database);
                mlista.setAdapter(agregarAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error en la consulta", databaseError.toException());
            }
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
                agregarAdapter = new AgregarAdapter(usuarios,getBaseContext(),usuario.getUid(),database);
                mlista.setAdapter(agregarAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });
    }

}