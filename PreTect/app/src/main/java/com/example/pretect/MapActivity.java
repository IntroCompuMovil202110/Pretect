package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ImageView mapPlaceHolder = new ImageView(getBaseContext());

        mapPlaceHolder.setImageResource(R.drawable.map_placeholder_background);

    }
}