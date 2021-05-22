package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pretect.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    //Views
    EditText correo, password, nombre, edad, palabraSegura, palabraSeñuelo;
    Button signupButton, hasAccountButton;

    //TAG
    public static final String TAG = "AUTH";

    //Paths
    private static final String PATH_USERS = "users/";
    //firebase Auth
    private FirebaseAuth mAuth;
    //realtime database
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Inflate
        correo = findViewById(R.id.correo);
        password = findViewById(R.id.password);
        nombre = findViewById(R.id.nombre);
        edad = findViewById(R.id.edad);
        palabraSegura = findViewById(R.id.safeWordInput);
        palabraSeñuelo = findViewById(R.id.baitWordInput);
        signupButton = findViewById(R.id.signupButton);
        hasAccountButton = findViewById(R.id.hasAccountButton);

        //firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //realtime database
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference(PATH_USERS);

        user = new User();

        //signup action listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get parameters
                String em = correo.getText().toString();
                String pass = password.getText().toString();
                String nom = nombre.getText().toString();
                String eda = edad.getText().toString();
                String segur = palabraSegura.getText().toString();
                String senue = palabraSeñuelo.getText().toString();

                if (validateForm(em, pass, nom, eda, segur, senue)) {
                    signUp(em, pass);
                } else {
                    Toast.makeText(SignupActivity.this, "Llene todos los campos.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        //signin actionlister
        hasAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SigninActivity.class);
                startActivity(intent);
            }
        });
    }

    //validar que todos los campos estén llenos
    private boolean validateForm(String em, String pass, String nom, String eda, String segur, String senue) {
        if (em != null && pass != null && nom != null && eda != null && segur != null && senue != null) {
            if (!em.isEmpty() && !pass.isEmpty() && !nom.isEmpty() && !eda.isEmpty() && !segur.isEmpty() && !senue.isEmpty()) {
                if (em.contains("@") && pass.length() > 5) {
                    return true;
                } else {
                    this.correo.setError("Invalid email address");
                    this.password.setError("Password must be at least 6 characters long");
                }
            }
        }
        return false;
    }

    private void signUp(String em, String pass) {
        mAuth.createUserWithEmailAndPassword(em, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            user.setState(false);
                            user.setLongitude(0);
                            user.setLatitude(0);
                            user.setBait_phrase(palabraSeñuelo.getText().toString());
                            user.setEmergencyContact("");
                            user.setGrabarAudio(false);
                            user.setPhone("");
                            user.setSafety_phrase(palabraSegura.getText().toString());
                            user.setEmail(correo.getText().toString());
                            user.setUserName(nombre.getText().toString());
                            user.setAge(Integer.parseInt(edad.getText().toString()));

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String idUsuario = firebaseUser.getUid();

                            mRef = database.getReference(PATH_USERS+idUsuario);
                            mRef.setValue(user);
                            Log.d(TAG, "createUserWithEmail:success");
                            updateUI(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            //String keyId = mRef.push().getKey();
            //mRef.child(keyId).setValue(user);
            startActivity(new Intent(this, MainActivity.class));
        } else {
            correo.setText("");
            password.setText("");
        }
    }


}