package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    TextInputEditText correo, password;
    Button signinButton, noAccountButton;

    //Signin
    public static final String TAG = "AUTH";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        correo = (TextInputEditText)findViewById(R.id.correo);
        password = (TextInputEditText)findViewById(R.id.password);
        signinButton = findViewById(R.id.signinButton);
        noAccountButton = findViewById(R.id.hasAccountButton);




        //To Signup
        noAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        //Signin
        mAuth = FirebaseAuth.getInstance();
        signinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                String mail = correo.getText().toString();
                String pass = password.getText().toString();
                if(!mail.isEmpty() && !pass.isEmpty()){
                    if(validateForm(mail, pass)){
                        signIn(mail, pass);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Ingresa todos los datos", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean validateForm(String em, String pass) {

        if(em.contains("@") && pass.length() > 5){
            return true;
        }else{
            this.correo.setError("Invalid email address");
            this.password.setError("Password must be at least 6 characters long");
        }
        return false;
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "SigninUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "SigninUserWithEmail:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(this, MainActivity.class));
        }else{
            correo.setText("");
            password.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("AUTH", "Supossed sent from logout");
        updateUI(mAuth.getCurrentUser());
    }
}