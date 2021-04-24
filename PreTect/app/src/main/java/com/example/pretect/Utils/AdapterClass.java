package com.example.pretect.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pretect.AgregarActivity;
import com.example.pretect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.FindFriendsViewHolder> {

    ArrayList<FindFriends> list;
    OnItemClickListener mListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
        void onAddUser(int position);
    }

    public void setmListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public AdapterClass(ArrayList<FindFriends> list){
        this.list = list;
    };

    @NonNull
    @Override
    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agregar_usuario, parent, false);
        return new FindFriendsViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position) {
        holder.setName(list.get(position).getName());
        holder.setPicture(list.get(position).getPicture());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView userPicture;
        TextView nameUser;
        Button addUserButton;

        public FindFriendsViewHolder(@NonNull android.view.View itemView, OnItemClickListener listener) {
            super(itemView);
            mView = itemView;
            userPicture = mView.findViewById(R.id.userImage);
            nameUser = mView.findViewById(R.id.userName);
            addUserButton = mView.findViewById(R.id.addUserButton);

            addUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!= null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddUser(position);
                        }
                    }
                }
            });
        }

        public void setPicture(String picture){
            if(picture!=null){
                Picasso.get().load(picture).placeholder(R.drawable.photo_placeholder).into(userPicture);
            }
        }

        public void setName(String userName){
            if(userName != null){
                nameUser.setText(userName);
            }
        }
    }
}
