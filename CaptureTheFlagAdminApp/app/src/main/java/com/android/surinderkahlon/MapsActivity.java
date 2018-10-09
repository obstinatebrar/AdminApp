package com.android.surinderkahlon;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ArrayList<LatLng> vertices;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private static final int LOCATION_REQUEST_CODE = 101;
    //LatLng latLng = new LatLng(47.8888,-71.777);
    Marker currentLocationMarker;
    FusedLocationProviderClient locationProvider;
    PolylineOptions options;

    private ChildEventListener teamALocationEventListener ;
    private ChildEventListener teamBLocationEventListener ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        //getUserLocation();

    }


    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    Location userLocation;

    private void checkLocationandAddToMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

    }



    public void getLocations(){



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mMap.clear();

                for (DataSnapshot postSnapshot : snapshot.child("Players").child("A").getChildren()) {

                    if(mMap != null){
                        //Add your nearby location lat long here. Positions should not be too far away as the Zoom size is 17
                        //LatLng milton = new LatLng(-34, 151);



                        LatLng brampton = new LatLng(43.7744671,-79.3360452);
                        LatLng missisauga = new LatLng(43.7747073,-79.3348436);
                        LatLng vaughan = new LatLng(43.7732611,-79.3343983);
                        //LatLng missisauga = new LatLng(43.7745353, -79.3364998);
                        //LatLng vaughan = new LatLng(43.7731098,	-79.3353518);


                        LatLng torontoCity = new LatLng(43.7730365,-79.3354336);
                        LatLng torontoPublic = new LatLng(43.7744671,-79.3360452);
                        LatLng vaughn2 = new LatLng(43.7732611,-79.3343983);



                        List<LatLng> list = new ArrayList<>();
                        list.add(torontoPublic);
                        list.add(missisauga);
                        list.add(vaughan);
                        list.add(torontoCity);
                        list.add(brampton);
                        list.add(torontoPublic);
                        list.add(vaughn2);

                        vertices = new ArrayList<>();
                        options = new PolylineOptions().width(2).color(Color.BLUE).geodesic(true);


                        PolygonOptions rectOptions = new PolygonOptions()
                                .add(torontoPublic,
                                        missisauga,
                                        vaughan,torontoCity,brampton, torontoPublic,vaughn2);
                        Polygon polygon = mMap.addPolygon(rectOptions);
                        polygon.setFillColor(Color.LTGRAY);
                        for (int z = 0; z < list.size(); z++) {
                            LatLng point = list.get(z);
                            vertices.add(point);
                            options.add(point);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(torontoCity));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(missisauga, 10.0f));//10
                    }

                    Player playr = postSnapshot.getValue(Player.class);

                    Log.d("test longitude", String.valueOf(playr.longitude));
                    Log.d("test latitude", String.valueOf(playr.latitude));
                    Log.d("test playerName", String.valueOf(playr.playerName));


                    LatLng playerLoc = new LatLng(playr.latitude, playr.longitude);
                    mMap.addMarker(new MarkerOptions().position(playerLoc).title(playr.playerName).alpha(7));

                    LatLng flagA = new LatLng(43.773961743324804,-79.33504863798453);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.773961743324804,-79.33504863798453), 21f));
                    MarkerOptions m1 = new MarkerOptions().position(flagA).title("Flag A");
                    m1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(m1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(flagA));

                    LatLng flagB = new LatLng(43.77339066210655,-79.33481394469572);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.77339066210655,-79.33481394469572), 21f));
                    MarkerOptions m2 = new MarkerOptions().position(flagB).title("Flag B");
                    m2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(m2);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(flagB));

                    String mLogn = Double.toString(playr.latitude);
                    String mLatd = Double.toString(playr.longitude);
                    mLogn=mLogn.trim();
                    mLatd=mLatd.trim();
                    if (mLatd==null || mLogn==null) {
                        if (mLatd.isEmpty()) {
                            mLatd = "No data found";
                        }
                        if (mLogn.isEmpty()) {
                            mLogn = "No data found";
                        }
                    }else{
                    }
                    //Toast.makeText(MapsActivity.this, "your location is " + mLogn + " " + mLatd, Toast.LENGTH_SHORT).show();

                    Location playerLocation = new Location("");
                    playerLocation.setLatitude(playr.latitude);
                    playerLocation.setLongitude(playr.longitude);
                    if(playerLocation!=null){
                        PolyUtil.containsLocation(new LatLng(playerLocation.getLatitude(), playerLocation.getLongitude()), vertices, false);
                    }
                    if( PolyUtil.containsLocation(new LatLng(playerLocation.getLatitude(), playerLocation.getLongitude()), vertices, false)){

                    }
                    else{
                        Toast.makeText(MapsActivity.this, playr.playerName+" is in prison ", Toast.LENGTH_LONG).show();

                    }

                }


                for (DataSnapshot postSnapshot : snapshot.child("Players").child("B").getChildren()) {


                    if(mMap != null){

                        LatLng brampton = new LatLng(43.7744671,-79.3360452);
                        LatLng missisauga = new LatLng(43.7747073,-79.3348436);
                        LatLng vaughan = new LatLng(43.7732611,-79.3343983);
                        //LatLng missisauga = new LatLng(43.7745353, -79.3364998);
                        //LatLng vaughan = new LatLng(43.7731098,	-79.3353518);


                        LatLng torontoCity = new LatLng(43.7730365,-79.3354336);
                        LatLng torontoPublic = new LatLng(43.7744671,-79.3360452);
                        LatLng vaughn2 = new LatLng(43.7732611,-79.3343983);



                        List<LatLng> list = new ArrayList<>();
                        list.add(torontoPublic);
                        list.add(missisauga);
                        list.add(vaughan);
                        list.add(torontoCity);
                        list.add(brampton);
                        list.add(torontoPublic);
                        list.add(vaughn2);

                        vertices = new ArrayList<>();
                        options = new PolylineOptions().width(2).color(Color.BLUE).geodesic(true);


                        PolygonOptions rectOptions = new PolygonOptions()
                                .add(torontoPublic,
                                        missisauga,
                                        vaughan,torontoCity,brampton, torontoPublic,vaughn2);
                        Polygon polygon = mMap.addPolygon(rectOptions);
                        polygon.setFillColor(Color.LTGRAY);
                        for (int z = 0; z < list.size(); z++) {
                            LatLng point = list.get(z);
                            vertices.add(point);
                            options.add(point);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(torontoCity));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(missisauga, 10.0f));//10
                    }
                    Player playr = postSnapshot.getValue(Player.class);

                    Log.d("test longitude", String.valueOf(playr.longitude));
                    Log.d("test latitude", String.valueOf(playr.latitude));
                    Log.d("test playerName", String.valueOf(playr.playerName));


                    LatLng playerLoc = new LatLng(playr.latitude, playr.longitude);
                    mMap.addMarker(new MarkerOptions().position(playerLoc).title(playr.playerName).alpha(5));

                    LatLng flagA = new LatLng(43.773961743324804,-79.33504863798453);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.773961743324804,-79.33504863798453), 21f));
                    MarkerOptions m1 = new MarkerOptions().position(flagA).title("Flag A");
                    m1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(m1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(flagA));

                    LatLng flagB = new LatLng(43.77339066210655,-79.33481394469572);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.77339066210655,-79.33481394469572), 21f));
                    MarkerOptions m2 = new MarkerOptions().position(flagB).title("Flag B");
                    m2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(m2);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(flagB));

                    String mLogn = Double.toString(playr.latitude);
                    String mLatd = Double.toString(playr.longitude);
                    mLogn=mLogn.trim();
                    mLatd=mLatd.trim();
                    if (mLatd==null || mLogn==null) {
                        if (mLatd.isEmpty()) {
                            mLatd = "No data found";
                        }
                        if (mLogn.isEmpty()) {
                            mLogn = "No data found";
                        }
                    }else{
                    }
                    //Toast.makeText(MapsActivity.this, "your location is " + mLogn + " " + mLatd, Toast.LENGTH_SHORT).show();

                    Location playerLocation = new Location("");
                    playerLocation.setLatitude(playr.latitude);
                    playerLocation.setLongitude(playr.longitude);
                    if(playerLocation!=null){
                        PolyUtil.containsLocation(new LatLng(playerLocation.getLatitude(), playerLocation.getLongitude()), vertices, false);
                    }
                    if( PolyUtil.containsLocation(new LatLng(playerLocation.getLatitude(), playerLocation.getLongitude()), vertices, false)){

                    }
                    else{
                        Toast.makeText(MapsActivity.this, playr.playerName+" is in prison ", Toast.LENGTH_LONG).show();

                    }

                }


            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                /*
                 * You may print the error message.
                 **/
            }
        });
    }



    private void addMarkers(){
        if(mMap != null){
            //Add your nearby location lat long here. Positions should not be too far away as the Zoom size is 17
            //LatLng milton = new LatLng(-34, 151);

            /*LatLng torontoPublic = new LatLng(43.7744268,-79.3370255);
             LatLng missisauga = new LatLng(43.7747677, -79.3353089);
             LatLng vaughan = new LatLng(43.7732880,	-79.3347188);
             //LatLng missisauga = new LatLng(43.7745353, -79.3364998);
             //LatLng vaughan = new LatLng(43.7731098,	-79.3353518);


            LatLng torontoCity = new LatLng(43.7729549,-79.3361565);
            LatLng brampton = new LatLng(43.7744268,-79.3370255);*/

            LatLng pointA = new LatLng(43.7744268,-79.3370255);
            LatLng pointB = new LatLng(43.730518, -79.618667);
            LatLng pointC = new LatLng(43.730729,	-79.622533);
            //LatLng missisauga = new LatLng(43.7745353, -79.3364998);
            //LatLng vaughan = new LatLng(43.7731098,	-79.3353518);


            LatLng pointD = new LatLng(43.726831,-79.622058);
            LatLng pointE = new LatLng(43.7744268,-79.612633);



            mMap.addMarker(new MarkerOptions().position(pointE)
                    .title("P"));

            mMap.addMarker(new MarkerOptions().position(pointB)
                    .title("Q"));

            mMap.addMarker(new MarkerOptions().position(pointC)
                    .title("R"));


            mMap.addMarker(new MarkerOptions().position(pointD)
                    .title("S"));

            mMap.addMarker(new MarkerOptions().position(pointA)
                    .title("t"));

            List<LatLng> list = new ArrayList<>();
            list.add(pointE);
            list.add(pointB);
            list.add(pointC);
            list.add(pointD);
            list.add(pointA);
            list.add(pointE);
            vertices = new ArrayList<>();
            options = new PolylineOptions().width(2).color(Color.BLUE).geodesic(true);


            PolygonOptions rectOptions = new PolygonOptions()
                    .add(pointE,
                            pointB,
                            pointC,pointD,pointA, pointE);
            Polygon polygon = mMap.addPolygon(rectOptions);
            polygon.setFillColor(Color.LTGRAY);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                vertices.add(point);
                options.add(point);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pointD));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointB, 10.0f));//10
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
      //  addMarkers();
        getLocations();
        checkLocationandAddToMap();
    }



    private void getUserLocation() {


      
    }


}
