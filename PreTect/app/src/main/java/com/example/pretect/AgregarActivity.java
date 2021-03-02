package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.pretect.Utils.Functions;
import com.example.pretect.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AgregarActivity extends AppCompatActivity {
    SearchView userSearch;
    ListView conversationList;
    BottomNavigationView menuInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);


        //Inflate
        userSearch=findViewById(R.id.userSearch);
        ArrayList<User> contacts = (ArrayList<User>) getIntent().getSerializableExtra("contacts");

        conversationList = (ListView) findViewById(R.id.conversationList);
        conversationList.setAdapter(new PictureNameAdapter(this, contacts));

        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });

        initViews();
    }

    private void initViews() {

    }
}