package com.example.pretect.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pretect.R;
import com.example.pretect.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class AgregarAdapter extends BaseAdapter {

    ArrayList<User> list;
    Context context;
    String idMyUser;
    FirebaseDatabase database;
    String nombreMio;


    public AgregarAdapter(ArrayList<User> list, Context context, String idMyUser, FirebaseDatabase database) {
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
            if(solicitud == null){
                boton.setText("Agregar +");
            }
        }catch (Exception e){

        }

        if(boton.getText().equals("aceptado") || boton.getText().equals("enviado")){
            boton.setEnabled(false);
        }

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(boton.getText().equals("Agregar +")){
                    //agregar al usuario a la lista de contactos
                    DatabaseReference myRef = database.getReference("users/" + idMyUser + "/contacts/" + currentItem.getId()+ "/");
                    myRef.child("userName").setValue(currentItem.getUserName());
                    myRef.child("solicitud").setValue("enviado");

                    //Proceso contrario con el usuario agregado
                    DatabaseReference myRef2 = database.getReference("users/" + currentItem.getId() + "/contacts/" +idMyUser+ "/");
                    //myRef2.child("nombre").setValue(currentItem.getUserName());
                    //myRef.child("estado").setValue(currentItem.getState());
                    myRef2.child("solicitud").setValue("solicitado");

                    DatabaseReference myRef3 = database.getReference("users/" + idMyUser);
                    myRef3.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            nombreMio = dataSnapshot.child("userName").getValue().toString();
                            myRef2.child("userName").setValue(nombreMio);
                        }
                    });
                    boton.setText("Enviado");
                }else if(boton.getText().equals("solicitado")){
                    DatabaseReference myRef = database.getReference("users/" + idMyUser + "/contacts/" + currentItem.getId()+ "/");
                    myRef.child("solicitud").setValue("aceptado");
                    myRef.child("state").setValue(false);
                    DatabaseReference myRef2 = database.getReference("users/" + currentItem.getId() + "/contacts/" +idMyUser+ "/");
                    myRef2.child("solicitud").setValue("aceptado");
                    myRef2.child("state").setValue(false);

                    //agregar chat
                    String chatId = createChatId(currentItem.getId(), idMyUser);
                    Log.i("CHATID", chatId);
                    //1. Agregar un chat nuevo a /chats
                    DatabaseReference myChatRef = database.getReference("chats/"+ chatId +"/");
                    //2. Agregar miembros a /chats/id/miembros
                    myChatRef.child("members").child("user1").setValue(idMyUser);
                    myChatRef.child("members").child("user2").setValue(currentItem.getId());
                    //3. users/idMyUser/chats/id/nombre
                    DatabaseReference userChatRef = database.getReference("users/" + idMyUser + "/chats/");
                    userChatRef.child(chatId);
                    userChatRef.child("nombre").setValue(currentItem.getName());
                    //4. users/currentItem.getId()/chats/id/nombre
                    DatabaseReference otherUserChatRef = database.getReference("users/" + currentItem.getId() + "/chats/" );
                    userChatRef.child(chatId);
                    otherUserChatRef.child("nombre").setValue(nombreMio);

                    //crear path de localizaciones
                    DatabaseReference myRef3 = database.getReference("users/" + idMyUser + "/contactsUbication/" + currentItem.getId()+ "/");
                    myRef3.child("longitude").setValue(0);
                    myRef3.child("latitude").setValue(0);
                    myRef3.child("state").setValue(false);
                    myRef3.child("enLinea").setValue(false);
                    myRef3.child("userName").setValue(currentItem.getUserName());

                    DatabaseReference myRef4 = database.getReference("users/" + currentItem.getId() + "/contactsUbication/" +idMyUser+ "/");
                    myRef4.child("longitude").setValue(0);
                    myRef4.child("latitude").setValue(0);
                    myRef4.child("state").setValue(false);
                    myRef4.child("enLinea").setValue(false);
                    DatabaseReference myRef5 = database.getReference("users/" + idMyUser);
                    myRef5.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            nombreMio = dataSnapshot.child("userName").getValue().toString();
                            myRef4.child("userName").setValue(nombreMio);
                        }
                    });
                }


            }
        });

        nombre.setText(currentItem.getUserName());
        //Picasso.with(c).load(currentItem.getFoto()).resize(100,100).into(imagen);
        return convertView;
    }
    private String createChatId(String currentItemId, String idMyUser){
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(currentItemId);
        ids.add(idMyUser);
        //Ordena los ids alfabeticamente
        Collections.sort(ids);
        //Retorna "ab" donde a es el id menor alfabeticamente
        return ids.get(0) + ids.get(1);
    }
}
