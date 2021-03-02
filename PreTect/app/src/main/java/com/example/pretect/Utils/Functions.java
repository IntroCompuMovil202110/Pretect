package com.example.pretect.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.example.pretect.AgregarActivity;
import com.example.pretect.AjustesActivity;
import com.example.pretect.MainActivity;
import com.example.pretect.MapActivity;
import com.example.pretect.MessagesActivity;
import com.example.pretect.R;
import com.example.pretect.entities.User;

import java.util.ArrayList;

public class Functions {

    //manejo general de la navegacion
    public static boolean navegacion (Context context, MenuItem item) {
            switch (item.getItemId()){
                case R.id.mapa_navigation:
                    context.startActivity(new Intent(context, MapActivity.class));
                    return true;
                case R.id.agregar_navigation:
                    context.startActivity(new Intent(context, AgregarActivity.class));
                    return true;
                case R.id.ajustes_navigation:
                    context.startActivity(new Intent(context, AjustesActivity.class));
                    return true;
                case R.id.chat_navigation:
                    Intent intent = new Intent(context, MessagesActivity.class);
                    ArrayList<User> contacts = User.getContacts();
                    //Bundle bundle = new Bundle();
                    //bundle.putSerializable("contacts", contacts);
                    intent.putExtra("contacts", contacts);
                    context.startActivity(intent);
                    return true;
                case R.id.principal_navigation:
                    context.startActivity(new Intent(context, MainActivity.class));
                    return true;
            }
            return false;
    }

}

