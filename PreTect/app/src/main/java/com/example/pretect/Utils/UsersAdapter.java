package com.example.pretect.Utils;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pretect.R;
import com.google.firebase.database.core.Context;
import com.google.firebase.database.core.view.View;

public class UsersAdapter extends CursorAdapter {

    public UsersAdapter(android.content.Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public android.view.View newView(android.content.Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.agregar_usuario, parent, false);
    }

    @Override
    public void bindView(android.view.View view, android.content.Context context, Cursor cursor) {
        ImageView userImage = view.findViewById(R.id.userImage);
        TextView userName = view.findViewById(R.id.userName);
        Button addUser = view.findViewById(R.id.addUserButton);

        userName.setText(cursor.getString(0));

    }
}
