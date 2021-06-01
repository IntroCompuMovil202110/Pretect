package com.example.pretect.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretect.ChatActivity;
import com.example.pretect.MessagesActivity;
import com.example.pretect.R;
import com.example.pretect.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {

    ArrayList<User> list;
    Context context;
    String idMyUser;
    FirebaseDatabase database;
    String nombreMio;


    public ContactAdapter(ArrayList<User> list, Context context, String idMyUser, FirebaseDatabase database) {
        this.list = list;
        this.context = context;
        this.idMyUser = idMyUser;
        this.database = database;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.agregar_usuario_item, parent, false);
        }

        // get current item to be displayed
        User currentItem = (User) getItem(position);

        ImageView imagen = (ImageView) convertView.findViewById(R.id.userImage);
        TextView nombre = (TextView)convertView.findViewById(R.id.userName);
        Button boton = convertView.findViewById(R.id.addUserButton);

        try {
            String solicitud = currentItem.getSolicitud();
            boton.setText(solicitud);
            if(solicitud != null){
                boton.setText("Ir al chat");
            }
        }catch (Exception e){

        }

        if(boton.getText().equals("aceptado") || boton.getText().equals("enviado")){
            boton.setEnabled(false);
        }

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                //ArrayList<User> contacts = User.getContacts();
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("contacts", contacts);
                intent.putExtra("contactId", currentItem.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        nombre.setText(currentItem.getUserName());
        //Picasso.with(c).load(currentItem.getFoto()).resize(100,100).into(imagen);
        return convertView;
    }
}
