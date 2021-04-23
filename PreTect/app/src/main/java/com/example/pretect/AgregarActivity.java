package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.pretect.Utils.FindFriends;
import com.example.pretect.Utils.Functions;
import com.example.pretect.Utils.UsersAdapter;
import com.example.pretect.entities.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

public class AgregarActivity extends AppCompatActivity {
    SearchView userSearch;
    static RecyclerView contactsList;
    BottomNavigationView menuInferior;
    String[] projection;
    Cursor cursor;
    UsersAdapter contactsAdapter;

    //Db
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String PATH_USERS = "users/";
    static FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(PATH_USERS);
        mAuth = FirebaseAuth.getInstance();


        //Inflate
        userSearch=findViewById(R.id.userSearch);
        //ArrayList<User> contacts = (ArrayList<User>) getIntent().getSerializableExtra("contacts");

        contactsList = (RecyclerView) findViewById(R.id.contactsList);
        contactsList.setHasFixedSize(true);
        contactsList.setLayoutManager(new LinearLayoutManager(this));

        //conversationList.setAdapter(new PictureNameAdapter(this, contacts));

        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });


        searchFriends("");

        //Search
        userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFriends(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void searchFriends(String query) {

        Query callOn= mDatabase.orderByChild("name").startAt(query).endAt(query+"\uf8ff");
        FirebaseRecyclerOptions<FindFriends> options =
                new FirebaseRecyclerOptions.Builder<FindFriends>().setQuery(callOn, FindFriends.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriends model) {

                if(!model.getEmail().contains(mAuth.getCurrentUser().getEmail())){
                    holder.setName(model.getName());
                    holder.setPicture(model.getPicture());
                    Log.i("FOUND", model.getName());
                }
                else{
                    //holder.delete(position);
                }
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agregar_usuario, parent, false);
                return new FindFriendsViewHolder(view);
            }
        };
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setPicture(String picture){
            if(picture!=null){
                ImageView userPicture = mView.findViewById(R.id.userImage);
                Picasso.get().load(picture).placeholder(R.drawable.photo_placeholder).into(userPicture);
            }
        }

        public void setName(String userName){
            if(userName != null){
                TextView nameUser = mView.findViewById(R.id.userName);
                nameUser.setText(userName);
            }
        }

        //public void delete(int position){
            //contactsList.removeViews(position,1);
            //firebaseRecyclerAdapter.getRef(position).removeValue();
        //}
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        contactsList.setAdapter(firebaseRecyclerAdapter);
    }
}