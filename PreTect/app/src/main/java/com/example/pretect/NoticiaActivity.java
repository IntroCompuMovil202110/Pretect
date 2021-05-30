package com.example.pretect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.TextureView;
import android.widget.TextView;

import com.example.pretect.Utils.Functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NoticiaActivity extends AppCompatActivity {

    TextView noticiaTitle, noticiaContent;
    BottomNavigationView menuInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);

        noticiaTitle = findViewById(R.id.noticiaText);
        noticiaContent = findViewById(R.id.noticiaContent);

        String titulo = getIntent().getStringExtra("TituloNoticia");
        String contenido = getIntent().getStringExtra("ContenidoNoticia");

        noticiaTitle.setText(titulo);
        noticiaContent.setText(Html.fromHtml(contenido));

        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });
    }
}