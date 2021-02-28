package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pretect.AbstractClass.PictureName;
import com.example.pretect.entities.User;

import java.io.Serializable;
import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    ListView conversationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        @SuppressWarnings("unchecked")
        ArrayList<User> contacts = (ArrayList<User>) getIntent().getSerializableExtra("contacts");

        conversationList = (ListView) findViewById(R.id.conversationList);
        conversationList.setAdapter(new PictureNameAdapter(this, contacts));

        conversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                intent.putExtra("user", contacts.get(position));
                startActivity(intent);
            }
        });



    }
}