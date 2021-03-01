package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class AjustesActivity extends AppCompatActivity {

    TextView phone, emergencyContact, safeWord, baitWord;
    Button logout;
    Switch recordSwitch, geolocationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);


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