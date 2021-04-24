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
import com.firebase.ui.database.CachingSnapshotParser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

public class AgregarActivity extends AppCompatActivity {
    SearchView userSearch;
    static RecyclerView contactsList;
    BottomNavigationView menuInferior;
    AdapterClass adapterClass;
    ArrayList<FindFriends> list;
    private String userKey;

    //Db
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String PATH_USERS = "users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(PATH_USERS);
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
                        for(DataSnapshot keyId: snapshot.getChildren()){
                            if(!keyId.child("email")
                                    .getValue(String.class)
                                    .equals(mAuth.getCurrentUser().getEmail())
                            ){
                                list.add(keyId.getValue(FindFriends.class));
                            }else{
                                userKey = keyId.getKey();
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

    /*private boolean isFriend(String email) {
        Task<DataSnapshot> tk= mDatabase.child(userKey).child("contacts").equalTo(email).get();
        return tk.getResult().exists();
        //return mDatabase.child(userKey).child("contacts").toString().equals(email);
    }*/

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
        String emailToAdd = list.get(position).getEmail();
        mDatabase.child(userKey).child("contacts").setValue(emailToAdd);
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


    @Override
    protected void onStart() {
        super.onStart();
        readOnce();
    }
}