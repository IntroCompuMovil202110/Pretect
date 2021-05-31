package com.example.pretect;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pretect.Utils.Functions;
import com.example.pretect.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class ChatActivity extends AppCompatActivity {
    User currentUser;
    TextView chatTitle;
    ImageButton send;
    LinearLayout chatEntries;
    TextInputEditText input;

    BottomNavigationView menuInferior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUser = (User) getIntent().getExtras().get("user");

        //chatTitle = (TextView) findViewById(R.id.chatUserName);
        //chatEntries = (LinearLayout) findViewById(R.id.chatEntries);
        //send = (ImageButton) findViewById(R.id.sendButton);
        //input = (TextInputEditText) findViewById(R.id.chatTextInput);
        
        chatTitle.setText(currentUser.getName());

        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView newEntry = new TextView(getBaseContext());
                newEntry.setPadding(5,5,5,5);
                //newEntry.setBackgroundResource(R.drawable.two_sided_border);
                //newEntry.setBackgroundColor(Color.GRAY);
                newEntry.setText(input.getEditableText().toString());

                chatEntries.addView(newEntry);

                input.setText("");
            }
        });
    }


}