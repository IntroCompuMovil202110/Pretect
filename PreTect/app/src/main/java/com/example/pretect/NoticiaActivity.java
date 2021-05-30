package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.TextureView;
import android.widget.TextView;

public class NoticiaActivity extends AppCompatActivity {

    TextView noticiaTitle, noticiaContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);

        noticiaTitle = findViewById(R.id.noticiaText);
        noticiaContent = findViewById(R.id.noticiaContent);

        String titulo = getIntent().getStringExtra("TituloNoticia");
        String contenido = getIntent().getStringExtra("ContenidoNoticia");

        noticiaTitle.setText(titulo);
        noticiaContent.setText(contenido);

    }
}