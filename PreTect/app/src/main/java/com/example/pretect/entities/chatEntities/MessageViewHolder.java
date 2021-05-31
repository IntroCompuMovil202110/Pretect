package com.example.pretect.entities.chatEntities;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pretect.R;
import com.example.pretect.entities.chatEntities.FriendlyMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "MessageViewHolder";

    TextView messageTextView;
    //ImageView messageImageView;
    TextView messengerTextView;


    public MessageViewHolder(View v) {
        super(v);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        //messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);

    }

    public void bindMessage(FriendlyMessage friendlyMessage) {
        if (friendlyMessage.getText() != null) {
            messageTextView.setText(friendlyMessage.getText());
            messageTextView.setVisibility(TextView.VISIBLE);
            //messageImageView.setVisibility(ImageView.GONE);
        }  else {

            }

            //messageImageView.setVisibility(ImageView.VISIBLE);
            //messageTextView.setVisibility(TextView.GONE);
        }

}