package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        boolean usuarioValidado = false;

        if(usuarioValidado){
            startActivity(new Intent(this, MainActivity.class));
        }else{
            startActivity(new Intent(this, SigninActivity.class));
        }
    }
}