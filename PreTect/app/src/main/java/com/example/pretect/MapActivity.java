package com.example.pretect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pretect.Utils.Functions;
import com.example.pretect.Utils.Permisos;
import com.example.pretect.entities.LocationPermissionsRequestor;
import com.example.pretect.entities.PlatformPositioningProvider;
import com.example.pretect.entities.SingletoneUser;
import com.example.pretect.entities.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Location;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.GestureState;
import com.here.sdk.gestures.LongPressListener;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.MapViewBase;
import com.here.sdk.mapview.PickMapItemsResult;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Notice;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Section;
import com.here.sdk.routing.Waypoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapActivity extends AppCompatActivity {

    //General UI
    BottomNavigationView menuInferior;

    //Permission
    private String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_ID=1;
    private boolean locationEnable=false;
    private LocationPermissionsRequestor permissionsRequestor;

    //light sensor
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;

    //Map
    private MapView mapView;
    private MapImage othersMapImage;
    private MapImage dangerMapImage;
    private Button centerButton;
    private MapMarker clickMarker=null;
    private MapImage clickImage;
    private RoutingEngine routingEngine;
    private MapPolyline routeMapPolyline=null;
    private ArrayList<MapMarker> markers=null;
    private MapMarker topmostMapMarker=null;
    TextView textView;
    MapView.ViewPin viewPin;

    //My Location
    private android.location.Location myLocation;
    private PlatformPositioningProvider platformPositioningProvider = null;
    private MapImage myMapImage;
    private MapMarker myMarker=null;

    //DataBase
    private final String PATH_USERS="users";
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String userKey;

    //singleton user
    SingletoneUser singletoneUser;

    //Life Cycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Inflate
        centerButton= findViewById(R.id.centerButton);
        centerButton.setVisibility(View.INVISIBLE);
        othersMapImage= MapImageFactory.fromResource(this.getResources(),R.drawable.others_marker);
        dangerMapImage= MapImageFactory.fromResource(this.getResources(),R.drawable.danger_marker);
        markers=new ArrayList<>();

        //DataBase
        mAuth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        userKey=mAuth.getUid();


        //Menu Bar
        menuInferior = findViewById(R.id.bottom_nav_instructor);
        menuInferior.setOnNavigationItemSelectedListener(item -> {
            return Functions.navegacion(this, item);
        });
        ImageView mapPlaceHolder = new ImageView(getBaseContext());
        mapPlaceHolder.setImageResource(R.drawable.map_placeholder_background);

        //Light Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        initLightSensor();

        // Get a MapViewLite instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        //singleton
        singletoneUser = SingletoneUser.getInstance();

        readOnce();

        //Permissions
        handleAndroidPermissions();

        //Map
        initMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        if(locationEnable){
            platformPositioningProvider.stopLocating();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        if(locationEnable){
            starLocating();
        }else{
            initMyLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //Permissions
    private void handleAndroidPermissions() {
        permissionsRequestor = new LocationPermissionsRequestor(this);
        permissionsRequestor.request(new LocationPermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                initMyLocation();
            }

            @Override
            public void permissionsDenied() {
                loadMapScene();
                Log.e("PermissionRequestor", "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    //Initialize Map
    private void loadMapScene(){
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if(mapError == null){
                if(!locationEnable){
                    mapView.getCamera().lookAt(new GeoCoordinates(0,0),2500);
                }
                onClickListener();
                setTapGestureHandler();
            }else{
                //TODO: log error
            }
        });
    }

    private void onClickListener() {
        mapView.getGestures().setLongPressListener(new LongPressListener() {
            @Override
            public void onLongPress(@NonNull GestureState gestureState, @NonNull Point2D touchPoint) {
                GeoCoordinates geoCoordinates = mapView.viewToGeoCoordinates(touchPoint);
                if(routeMapPolyline!=null){
                    mapView.getMapScene().removeMapPolyline(routeMapPolyline);
                }
                if (clickMarker == null) {
                    clickImage=MapImageFactory.fromResource(MapActivity.this.getResources(),R.drawable.position_marker);
                    Anchor2D anchor2D=new Anchor2D(0.5f,1.0f);
                    clickMarker=new MapMarker(geoCoordinates,clickImage,anchor2D);
                }else{
                    clickMarker.setCoordinates(geoCoordinates);
                }
                mapView.getMapScene().addMapMarker(clickMarker);
            }
        });
    }

    private void setTapGestureHandler() {
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(Point2D touchPoint) {
                pickMapMarker(touchPoint);
            }
        });
    }

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 2;
        mapView.pickMapItems(touchPoint, radiusInPixel, new MapViewBase.PickMapItemsCallback() {
            @Override
            public void onPickMapItems(@NonNull PickMapItemsResult pickMapItemsResult) {
                // Note that 3D map markers can't be picked yet. Only marker, polgon and polyline map items are pickable.
                List<MapMarker> mapMarkerList = pickMapItemsResult.getMarkers();
                if (mapMarkerList.size() == 0) {
                    return;
                }
                topmostMapMarker = mapMarkerList.get(0);
                if(locationEnable){
                    showRoute(topmostMapMarker);
                }else{
                    Toast.makeText(MapActivity.this, "No se puede mostrar la ruta sin localizacion actual", Toast.LENGTH_SHORT).show();
                }
                if(!mapView.getViewPins().isEmpty()){
                    mapView.getViewPins().get(0).unpin();
                }
                Metadata metadata = topmostMapMarker.getMetadata();
                if (metadata != null) {
                    String message = "No message found.";
                    String string = metadata.getString("user");
                    if (string != null && !string.equals("Username Not Found")) {
                        message = string;
                        createTextView(metadata.getDouble("lat"),metadata.getDouble("long"),message);
                    }
                    return;
                }
            }
        });
    }

    private void createTextView(Double lat, Double aLong, String message) {
        textView = new TextView(MapActivity.this);
        textView.setTextColor(android.graphics.Color.parseColor("#FF6200EE"));
        textView.setText(message);

        LinearLayout linearLayout = new LinearLayout(MapActivity.this);
        linearLayout.setBackgroundResource(R.color.teal_200);
        linearLayout.setPadding(10, 10, 10, 10);
        linearLayout.addView(textView);
        viewPin = mapView.pinView(linearLayout, new GeoCoordinates(lat,aLong));
        viewPin.setAnchorPoint(new Anchor2D(0.5f,2.8f));
    }

    private void showRoute(MapMarker destination) {
        if(locationEnable && !destination.getCoordinates().equals(myMarker.getCoordinates())) {
            try {
                routingEngine = new RoutingEngine();
                Waypoint startWaypoint = new Waypoint(myMarker.getCoordinates());
                Waypoint destinationWaypoint = new Waypoint(destination.getCoordinates());

                List<Waypoint> waypoints =
                        new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

                routingEngine.calculateRoute(
                        waypoints,
                        new CarOptions(),
                        new CalculateRouteCallback() {
                            @Override
                            public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                                if (routingError == null) {
                                    Route route = routes.get(0);
                                    showRouteOnMap(route);
                                    logRouteViolations(route);
                                } else {
                                    Log.e("onRouteCalculated", "Error while calculating a route:" + routingError.toString());
                                }
                            }
                        });
            } catch (InstantiationErrorException e) {
                throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
            }
        }
    }


    private void showRouteOnMap(Route route) {
        // Show route as polyline.
        GeoPolyline routeGeoPolyline;
        try {
            routeGeoPolyline = new GeoPolyline(route.getPolyline());
        } catch (InstantiationErrorException e) {
            // It should never happen that a route polyline contains less than two vertices.
            return;
        }

        float widthInPixels = 20;
        if(routeMapPolyline!=null){
            mapView.getMapScene().removeMapPolyline(routeMapPolyline);
        }
        routeMapPolyline = new MapPolyline(routeGeoPolyline,widthInPixels,Color.valueOf(0, 0.56f, 0.54f, 0.63f)); // RGBA
        Metadata met=new Metadata();
        met.setString("id",topmostMapMarker.getMetadata().getString("id"));
        met.setDouble("lat",topmostMapMarker.getCoordinates().latitude);
        met.setDouble("long",topmostMapMarker.getCoordinates().longitude);
        routeMapPolyline.setMetadata(met);
        mapView.getMapScene().addMapPolyline(routeMapPolyline);
    }

    // A route may contain several warnings, for example, when a certain route option could not be fulfilled.
    // An implementation may decide to reject a route if one or more violations are detected.
    private void logRouteViolations(Route route) {
        for (Section section : route.getSections()) {
            for (Notice notice : section.getNotices()) {
                Log.e("RouteViolation", "This route contains the following warning: " + notice.code.toString());
            }
        }
    }

    //My Location Updates
    private void initMyLocation() {
        if(locationEnable)
            return;
        if(ContextCompat.checkSelfPermission(this, locationPerm)== PackageManager.PERMISSION_GRANTED) {
            locationEnable=true;
            myMapImage= MapImageFactory.fromResource(this.getResources(),R.drawable.my_marker);
            platformPositioningProvider = new PlatformPositioningProvider(MapActivity.this);
            centerButton.setVisibility(View.VISIBLE);
            starLocating();
        }
        loadMapScene();
    }

    private void starLocating() {
        if(platformPositioningProvider==null){
            //TODO: Handle error
            return;
        }
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                myLocation=location;
                if(myMarker==null){
                    Anchor2D anchor2D=new Anchor2D(0.5f,1.0f);
                    myMarker=new MapMarker(new GeoCoordinates(location.getLatitude(),location.getLongitude()),myMapImage,anchor2D);
                    mapView.getMapScene().addMapMarker(myMarker);
                    mapView.getCamera().lookAt(new GeoCoordinates(location.getLatitude(),location.getLongitude()),2500);
                }else{
                    myMarker.setCoordinates(new GeoCoordinates(location.getLatitude(),location.getLongitude()));
                }
                if(routeMapPolyline!=null){
                    MapMarker mapDest=new MapMarker(new GeoCoordinates(routeMapPolyline.getMetadata().getDouble("lat"),routeMapPolyline.getMetadata().getDouble("long")),othersMapImage);
                    mapView.getMapScene().removeMapPolyline(routeMapPolyline);
                    showRoute(mapDest);
                }
            }
        });
    }

    //ContactsLocation
    private void readOnce(){
        reference=database.getReference(PATH_USERS);
        reference.child(userKey).child("contactsUbication").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean exist=false;
                for(DataSnapshot single: snapshot.getChildren()){
                    User user = new User();
                    user.setUserName("Username Not Found");
                    user.setState(false);
                    for (DataSnapshot s : single.getChildren()){
                        if(s.getKey().equals("longitude")){
                            user.setLongitude(s.getValue(Double.class));
                        }else if(s.getKey().equals("latitude")){
                            user.setLatitude(s.getValue(Double.class));
                        }else if(s.getKey().equals("state")){
                            user.setState(s.getValue(Boolean.class));
                        }else if(s.getKey().equals("userName")){
                            user.setUserName(s.getValue(String.class));
                        }
                    }
                    user.setId(single.getKey());
                    exist=false;
                    if(!markers.isEmpty()){
                        for (MapMarker marker: markers) {
                            if(marker.getMetadata().getString("id").equals(user.getId())){
                                if(!mapView.getViewPins().isEmpty()){
                                    if(mapView.getViewPins().get(0).getGeoCoordinates().latitude==marker.getCoordinates().latitude && mapView.getViewPins().get(0).getGeoCoordinates().longitude==marker.getCoordinates().longitude){
                                        mapView.getViewPins().get(0).unpin();
                                        if(!user.getUserName().equals("Username Not Found")){
                                            createTextView(user.getLatitude(), user.getLongitude(),user.getUserName());
                                        }
                                    }
                                }
                                marker.setCoordinates(new GeoCoordinates(user.getLatitude(), user.getLongitude()));
                                if(user.getState()){
                                    marker.setImage(dangerMapImage);
                                }else{
                                    marker.setImage(othersMapImage);
                                }
                                exist=true;
                                if(routeMapPolyline!=null && routeMapPolyline.getMetadata().getString("id").equals(user.getId())){
                                    if(routeMapPolyline.getMetadata().getDouble("lat")!=user.getLatitude() || routeMapPolyline.getMetadata().getDouble("long")!=user.getLongitude()){
                                        mapView.getMapScene().removeMapPolyline(routeMapPolyline);
                                        showRoute(marker);
                                    }else{
                                        Toast.makeText(MapActivity.this, "AJA", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        if(!exist){
                            addUserMarker(user);
                        }
                    }else{
                        addUserMarker(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addUserMarker(User user) {
        if(user.getLatitude()!= Double.MIN_VALUE && user.getLongitude()!=Double.MIN_VALUE){
            Anchor2D anchor2D=new Anchor2D(0.5f,1.0f);
            MapMarker othersMapMarker;
            if(user.getState()){
                othersMapMarker=new MapMarker(new GeoCoordinates(user.getLatitude(),user.getLongitude()),dangerMapImage,anchor2D);
            }else{
                othersMapMarker=new MapMarker(new GeoCoordinates(user.getLatitude(),user.getLongitude()),othersMapImage,anchor2D);
            }
            Metadata met=new Metadata();
            met.setString("user",user.getUserName());
            met.setString("id",user.getId());
            met.setDouble("lat",user.getLatitude());
            met.setDouble("long",user.getLongitude());
            othersMapMarker.setMetadata(met);
            markers.add(othersMapMarker);
            mapView.getMapScene().addMapMarker(othersMapMarker);
        }
    }

    //Light Sensor
    private void initLightSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor!=null){
            lightSensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
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


    //Extra Services
    public void centerMap(View view){
        if(locationEnable){
            mapView.getCamera().lookAt(new GeoCoordinates(myLocation.getLatitude(),myLocation.getLongitude()),2500);
        }
    }

}