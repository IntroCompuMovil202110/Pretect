package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pretect.Utils.Functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Location;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

public class MapActivity extends AppCompatActivity {

    //General UI
    BottomNavigationView menuInferior;

    //Permission
    private String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_ID=1;
    private boolean locationEnable=false;

    //light sensor
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;

    //Map
    private MapView mapView;
    private Location myLocation;

    //Life Cycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Menu Bar
//        menuInferior = findViewById(R.id.bottom_nav_instructor);
//        menuInferior.setOnNavigationItemSelectedListener(item -> {
//            return Functions.navegacion(this, item);
//        });
//        ImageView mapPlaceHolder = new ImageView(getBaseContext());
//        mapPlaceHolder.setImageResource(R.drawable.map_placeholder_background);

        //Light Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        initLightSensor();

        // Get a MapViewLite instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        //Permissions
        //requestPermission(this, locationPerm,"Se necesita el permiso para mostrar los contactos",LOCATION_PERMISSION_ID);

        //Map
        loadMapScene();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        sensorManager.unregisterListener(lightSensorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    //Permissions
    private void requestPermission(Activity context, String permission, String justification, int id){
        if(ContextCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permission)){
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_ID) {
            loadMapScene();
        }
    }

    //Initialize Map
    private void loadMapScene(){
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if(mapError == null){
                mapView.getCamera().lookAt(new GeoCoordinates(52.5,13.3,1000));
                //initMyLocation();
            }else{
                //TODO: log error
            }
        });
    }

    //My Location Updates
    private void initMyLocation() {
        if(ContextCompat.checkSelfPermission(this, locationPerm)== PackageManager.PERMISSION_GRANTED) {
            locationEnable=true;
            mapView.getCamera().lookAt(new GeoCoordinates(52.5,13.3,1000));

        }
    }

    //Light Sensor
    private void initLightSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Toast.makeText(MapActivity.this, "Try", Toast.LENGTH_SHORT).show();
        if (lightSensor!=null){
            Toast.makeText(MapActivity.this, "YES", Toast.LENGTH_SHORT).show();
            lightSensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    Toast.makeText(MapActivity.this, "CAMBIO", Toast.LENGTH_SHORT).show();
                    if (mapView != null) {
                        if (event.values[0] < 10000) {
                            Log.i("MAPS", "DARK MAP " + event.values[0]);
                            changeScheme(MapScheme.NORMAL_NIGHT);
                        } else {
                            Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                            changeScheme(MapScheme.NORMAL_DAY);
                        }
                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            };
        }
        else Toast.makeText(MapActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
    }

    private void changeScheme(MapScheme scheme) {
        mapView.getMapScene().loadScene(scheme, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if(mapError==null){

                }else{
                    //TODO: handle error
                }
            }
        });
    }
}