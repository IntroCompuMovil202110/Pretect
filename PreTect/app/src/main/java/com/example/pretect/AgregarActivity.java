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

import com.example.pretect.Utils.AdapterClass;
import com.example.pretect.Utils.FindFriends;
import com.example.pretect.Utils.Functions;
import com.example.pretect.Utils.UsersAdapter;
import com.example.pretect.entities.User;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
    SearchView userSearch;
    static RecyclerView contactsList;
    BottomNavigationView menuInferior;
    AdapterClass adapterClass;
    ArrayList<FindFriends> list;
    ArrayList<String> userContacts = new ArrayList<>();
    private String userKey, userName;

    //Db
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseContacts;
    private static final String PATH_USERS = "users/";
    private static final String PATH_CONTACTS = "contacts/";
    private String authEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(PATH_USERS);
        mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child(PATH_CONTACTS);
        mAuth = FirebaseAuth.getInstance();


        //Inflate
        userSearch=findViewById(R.id.userSearch);

        contactsList = (RecyclerView) findViewById(R.id.contactsList);
        contactsList.setHasFixedSize(true);
        contactsList.setLayoutManager(new LinearLayoutManager(this));


        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });

    }

    public void readOnce(){
        if(mDatabase != null){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        list = new ArrayList<>();
                        int position = 0;
                        for(DataSnapshot keyId: snapshot.getChildren()){
                            if(!keyId.getKey().equals(userKey)){
                                if(!isContact(keyId.getKey())){
                                    list.add(keyId.getValue(FindFriends.class));
                                    list.get(position).setKey(keyId.getKey());
                                    position++;
                                }
                            }
                        }

                        adapterClass = new AdapterClass(list);
                        contactsList.setAdapter(adapterClass);

                        toAddUser(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        if(userSearch != null){
            userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }

    private void searchContacts(){
        mDatabaseContacts.orderByKey().equalTo(userKey).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot keyId: dataSnapshot.getChildren()){
                    for(DataSnapshot keyContact: keyId.getChildren()){
                        userContacts.add(keyContact.getKey());
                    }
                }
                readOnce();
            }
        });
    }

    private boolean isContact(String check) {
        return userContacts.contains(check);
    }

    private void toAddUser(AdapterClass adapterClass) {
        adapterClass.setmListener(new AdapterClass.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("ADD", "User touched");
            }

            @Override
            public void onAddUser(int position) {
                pubAddRequest(position);
            }
        });
    }

    private void pubAddRequest(int position) {
        String nameToAdd = list.get(position).getName();
        String keyToAdd = list.get(position).getKey();
        Log.i("ADDUSER", "Contact name to add: " + nameToAdd);
        mDatabaseContacts.child(userKey).child(keyToAdd).setValue(nameToAdd);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keyId: snapshot.getChildren()){
                    if(keyId.getKey().equals(keyToAdd)){
                        mDatabaseContacts.child(keyToAdd).child(userKey).setValue(userName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.i("ADDUSER", "Before delete: " + list.get(position).getName());
        list.remove(position);
        Log.i("ADDUSER", "After delete: " + list.get(position).getName());
        adapterClass.notifyItemRemoved(position);
    }

    public void search(String query){
        ArrayList<FindFriends> mList = new ArrayList<>();
        for(FindFriends ob: list){
           if(ob.getName().toLowerCase().contains(query.toLowerCase())){
                mList.add(ob);
           }
        }
        AdapterClass adapterClass = new AdapterClass(mList);
        contactsList.setAdapter(adapterClass);
    }

    public void current(){
        mDatabase.orderByKey().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot keyId: dataSnapshot.getChildren()){
                    if(keyId.child("email").getValue(String.class).equals(authEmail)){
                        userKey = keyId.getKey();
                    }
                }
                searchContacts();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        authEmail = mAuth.getCurrentUser().getEmail();
        current();
    }
}