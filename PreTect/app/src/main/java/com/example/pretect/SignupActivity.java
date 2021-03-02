package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText correo, password, nombre, edad;
    Button signupButton, hasAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Inflate
        correo = findViewById(R.id.correo);
        password = findViewById(R.id.password);
        nombre = findViewById(R.id.nombre);
        edad = findViewById(R.id.edad);
        signupButton = findViewById(R.id.signupButton);
        hasAccountButton = findViewById(R.id.hasAccountButton);


        //To Signin
        hasAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SigninActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                String mail = correo.getText().toString();
                String pass = password.getText().toString();
                String name = nombre.getText().toString();
                String age = edad.getText().toString();
                if(!mail.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !age.isEmpty()){
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Ingresa todos los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}