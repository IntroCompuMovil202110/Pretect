package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SigninActivity extends AppCompatActivity {

    TextView emailTextFieldSI, passwordTextFieldSI;
    Button signinButton, noAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        emailTextFieldSI = findViewById(R.id.emailTextFieldSI);
        passwordTextFieldSI = findViewById(R.id.passwordTextFieldSI);
        signinButton = findViewById(R.id.signupButton);
        noAccountButton = findViewById(R.id.hasAccountButton);

        //To Signip
        noAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

    }
}