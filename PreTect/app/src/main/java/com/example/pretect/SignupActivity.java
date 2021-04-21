package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretect.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText correo, password, nombre, edad;
    Button signupButton, hasAccountButton;

    public static final String TAG = "AUTH";
    private static final String PATH_USERS = "users/";
    private FirebaseAuth mAuth;
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
        signupButton = findViewById(R.id.signupButton);
        hasAccountButton = findViewById(R.id.hasAccountButton);

        //Signup
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference(PATH_USERS);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Signup
                String em = correo.getText().toString();
                String pass = password.getText().toString();
                if(validateForm(em, pass)){
                    signUp(em, pass);
                }

                //User data
                String userName = nombre.getText().toString();
                int age = Integer.parseInt(edad.getText().toString());
                Log.i("USER", em+userName+String.valueOf(age));
                user = new User(userName, em, age);

            }
        });

        //To Signin
        hasAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SigninActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signUp(String em, String pass) {
        mAuth.createUserWithEmailAndPassword(em, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);
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
        if(currentUser!=null){
            String keyId = mRef.push().getKey();
            mRef.child(keyId).setValue(user);
            startActivity(new Intent(this, MainActivity.class));
        }else{
            correo.setText("");
            password.setText("");
        }
    }

    private boolean validateForm(String em, String pass) {
        if(em != null && pass!=null){
            if(!em.isEmpty() && !pass.isEmpty()){
                if(em.contains("@") && pass.length() > 5){
                    return true;
                }else{
                    this.correo.setError("Invalid email address");
                    this.password.setError("Password must be at least 6 characters long");
                }
            }
        }
        return false;
    }

}