package com.example.pretect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pretect.AbstractClass.PictureName;
import com.example.pretect.entities.User;

import java.util.ArrayList;

public class PictureNameAdapter extends BaseAdapter {
    Context context;
    static LayoutInflater inflater = null;
    ArrayList<PictureName> contacts;

    @SuppressWarnings("unchecked")
    public PictureNameAdapter(Context context, Object contacts) {
        this.context = context;
        this.contacts =  (ArrayList<PictureName>) contacts;
    }


    @Override
    public int getCount() {
        return this.contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.agregar_usuario, null);

        }
        ImageView imageView = (ImageView) row.findViewById(R.id.userImage);
        TextView textView = (TextView) row.findViewById(R.id.userName);
        Button addUser = (Button) row.findViewById(R.id.addUserButton);


        imageView.setImageResource(contacts.get(position).getPicture());
        textView.setText(contacts.get(position).getName());
        addUser.setVisibility(View.GONE);
        return row;
    }
}
