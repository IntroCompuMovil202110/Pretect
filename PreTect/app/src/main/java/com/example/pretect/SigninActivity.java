package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SigninActivity extends AppCompatActivity {

    TextInputEditText correo, password;
    Button signinButton, noAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        correo = (TextInputEditText)findViewById(R.id.correo);
        password = (TextInputEditText)findViewById(R.id.password);
        signinButton = findViewById(R.id.signinButton);
        noAccountButton = findViewById(R.id.hasAccountButton);



        //To Signin
        noAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                String mail = correo.getText().toString();
                String pass = password.getText().toString();
                if(!mail.isEmpty() && !pass.isEmpty()){
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Ingresa todos los datos", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}