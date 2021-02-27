package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

public class AgregarActivity extends AppCompatActivity {
    SearchView userSearch;
    RecyclerView userView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);


        //Inflate
        userSearch=findViewById(R.id.userSearch);
        userView=findViewById(R.id.userView);

        initViews();
    }

    private void initViews() {

    }
}