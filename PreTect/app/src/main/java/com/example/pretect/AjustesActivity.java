package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretect.Utils.Functions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class AjustesActivity extends AppCompatActivity {

    //Logout
    FirebaseAuth mAuth;

    //Db
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String PATH_USERS = "users/";
    private StorageReference reference = FirebaseStorage.getInstance().getReference();

    //Picture
    static final int IMAGE_PICKER_REQUEST = 2;
    String permGaleria = Manifest.permission.READ_EXTERNAL_STORAGE;

    TextView phone, emergencyContact, safeWord, baitWord, nameUser;
    Button logout, save;
    ImageButton photo;
    SwitchMaterial recordSwitch, geolocationSwitch;
    BottomNavigationView menuInferior;
    String userEmail;

    private String nameDB, phoneDB = "", emergencyContactDB = "", baitWordDB = "", safeWordDB = "", photoDB, userKey;
    private Uri uriPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = database.getInstance().getReference().child(PATH_USERS);

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
        nameUser = findViewById(R.id.nameUser);
        photo = findViewById(R.id.photo);
        save = findViewById(R.id.save);


        //Read user data
        //userEmail = mAuth.getCurrentUser().getEmail();
        //readOnce(userEmail);

        //Select picture from files
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(
                        AjustesActivity.this,
                        permGaleria,
                        "Se necesita para seleccionar la foto",
                        IMAGE_PICKER_REQUEST
                );
                buscarImagen();
            }
        });

        //Save changes
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        //logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                //Log.d("AUTH", "logout");
                Intent i  = new Intent(v.getContext(), SigninActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

    }

    private void readOnce(String userEmail) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keyId: snapshot.getChildren()) {
                    if (keyId.child("email").getValue().equals(userEmail)) {
                        userKey = keyId.getKey();
                        nameUser.setText(keyId.child("name").getValue(String.class));

                        nameDB = keyId.child("name").getValue(String.class);

                        baitWordDB = keyId.child("bait_phrase").getValue(String.class);
                        if (baitWordDB == null) {
                            baitWordDB = "";
                        }
                        safeWordDB = keyId.child("safety_phrase").getValue(String.class);
                        if (safeWordDB == null) {
                            safeWordDB = "";
                        }
                        phoneDB = keyId.child("phone").getValue(String.class);
                        if (phoneDB == null) {
                            phoneDB = "";
                        }
                        emergencyContactDB = keyId.child("emergency_contact").getValue(String.class);
                        if (emergencyContactDB == null) {
                            emergencyContactDB = "";
                        }
                        photoDB = keyId.child("picture").getValue(String.class);

                        break;
                    }
                }

                nameUser.setText(nameDB);
                setPhoto(photoDB);
                phone.setText(phoneDB);
                emergencyContact.setText(emergencyContactDB);
                safeWord.setText(safeWordDB);
                baitWord.setText(baitWordDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void requestPermission(Activity context, String permission, String justification, int id){
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permission)){
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == IMAGE_PICKER_REQUEST){
            if (ContextCompat.checkSelfPermission(this, permGaleria) == PackageManager.PERMISSION_GRANTED) {
                buscarImagen();
            }else{
                //si no recibo el permiso
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try {
                final Uri imageUri = data.getData();
                uriPicture = imageUri;

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                photo.invalidate();
                photo.setImageBitmap(selectedImage);
                Log.i("entrando",selectedImage.toString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void buscarImagen(){
        //si tengo el permiso
        if (ContextCompat.checkSelfPermission(this, permGaleria) == PackageManager.PERMISSION_GRANTED) {
            Intent pickImage = new Intent(Intent.ACTION_PICK);

            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }
    }

    private void update(){
        boolean pC = isPhoneChanged();
        boolean ecC = isEmergencyContactChanged();
        boolean swC = isSafeWordChanged();
        boolean bwC = isBaitWordChanged();
        boolean phC = isPhotoChanged();

        if( pC || ecC || swC || bwC){
            readOnce(userEmail);
            Toast.makeText(this, "Se han actualizado los datos", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this, "Nada por actualizar", Toast.LENGTH_SHORT).show();
    }

    private boolean isPhoneChanged(){
        boolean res = false;
        if (!phoneDB.equals(phone.getText().toString())) {
            String number = phone.getText().toString();
            mDatabase.child(userKey).child("phone").setValue(number);
            res = true;
        }

        return res;
    }

    private boolean isEmergencyContactChanged(){
        boolean res = false;
        if (!emergencyContactDB.equals(emergencyContact.getText().toString())) {
            String number = emergencyContact.getText().toString();
            mDatabase.child(userKey).child("emergency_contact").setValue(number);
            res = true;
        }
        return res;
    }

    private boolean isSafeWordChanged(){
        boolean res = false;
        if (!safeWordDB.equals(safeWord.getText().toString())) {
            mDatabase.child(userKey).child("safety_phrase").setValue(safeWord.getText().toString());
            res = true;
        }
        return res;
    }

    private boolean isBaitWordChanged(){
        boolean res = false;
        if (!baitWordDB.equals(baitWord.getText().toString())) {
            mDatabase.child(userKey).child("bait_phrase").setValue(baitWord.getText().toString());
            res = true;
        }
        return res;
    }

    private boolean isPhotoChanged(){
        if(uriPicture != null){
            final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uriPicture));
            fileRef.putFile(uriPicture).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(uri != null) mDatabase.child(userKey).child("picture").setValue(uri.toString());
                        }
                    });
                }
            });
            return true;
        }else{
            return false;
        }
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    private void setPhoto(String uri){
        Picasso.get().load(uri).into(photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userEmail = mAuth.getCurrentUser().getEmail();
        readOnce(userEmail);
    }
}