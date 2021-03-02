package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.pretect.Utils.Functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AjustesActivity extends AppCompatActivity {

    TextView phone, emergencyContact, safeWord, baitWord;
    Button logout;
    SwitchMaterial recordSwitch, geolocationSwitch;
    BottomNavigationView menuInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });

        //inflate
        phone = findViewById(R.id.phone);
        emergencyContact = findViewById(R.id.emergencyContact);
        safeWord = findViewById(R.id.safeWord);
        baitWord = findViewById(R.id.baitWord);
        recordSwitch = findViewById(R.id.recordSwitch);
        geolocationSwitch = findViewById(R.id.geolocationSwitch);
        logout = findViewById(R.id.logout);



    }
}