package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    TextView emailTextFieldSU, passwordTextFieldSU, nameTextField, ageTextField;
    Button signupButton, hasAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Inflate
        emailTextFieldSU = findViewById(R.id.emailTextFieldSU);
        passwordTextFieldSU = findViewById(R.id.passwordTextFieldSU);
        nameTextField = findViewById(R.id.nameTextField);
        ageTextField = findViewById(R.id.ageTextField);
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
    }

}