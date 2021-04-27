package com.example.pretect.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permisos {

    //Pedir permiso si este no ha sido concedido
    public static void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        //Si el permido no ha sido concedido
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            //En caso de que solo lo haya negado una vez  dar una explicacion
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                //explicacion
                Toast.makeText(context, justificacion, Toast.LENGTH_SHORT).show();
            }
            // se pide el permiso
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

}
