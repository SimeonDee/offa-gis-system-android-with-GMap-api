package com.example.simeon_dee.offagisapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback,
GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener
{


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE = 2;
    private static final int PROXIMITY_RADIUS = 5000;
    private GoogleMap mMap;
    private EditText location;
    //private Toolbar toolbar;
    private PlaceDetectionApi mDetClient = null;
    private FusedLocationProviderApi mFusedLocationProviderClient = null;
    Location deviceLocation = null;
    private String mNearbyTypeSelectedItem = "";
    double latitude, longitude;
    double end_latitude, end_longitude;
    private  static  final int MY_DEFAULT_ZOOM = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);

        //toolbar = (Toolbar) findViewById(R.id.tbToolBar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
/*
        mDetClient = Places.PlaceDetectionApi;
        mFusedLocationProviderClient = LocationServices.FusedLocationApi;*/

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

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);



        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
               // deviceLocation = mFusedLocationProviderClient.getLastLocation(null);
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                //Temporarily
                setStartAtDefaultLocation();

                //Use device Location as Starting Point
                //setStartAtMyLocation(deviceLocation);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE);
            }
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //Add codes here to position marker at device location
                return false;
            }
        });

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                else{
                    /*deviceLocation = mFusedLocationProviderClient.getLastLocation(null);
                    setStartAtMyLocation(deviceLocation);*/

                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);

                    //Temporarily
                    setStartAtDefaultLocation();

                }

            }
            else{
                setStartAtDefaultLocation();
            }
        }
        else
        {
            setStartAtDefaultLocation();
        }

    }



    private void setStartAtMyLocation(Location deviceLocation) {

        double lat = 0;
        double lng = 0;
        lat = deviceLocation.getLatitude();
        lng = deviceLocation.getLongitude();
        String locationName = "";

        if(lat != 0 && lng != 0)
        {
            LatLng latLng = new LatLng(lat,lng);
            Geocoder geocoder = new Geocoder(this);

            try {
                List<android.location.Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                android.location.Address address = addressList.get(0);
                if(address.hasLatitude() && address.hasLongitude())
                {
                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    locationName = address.getLocality();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            MarkerOptions marker = new MarkerOptions();
            marker.title(locationName)
                    .position(latLng);
            mMap.clear();
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));

        }
        else{

            setStartAtDefaultLocation();

        }

    }

    private void setStartAtDefaultLocation() {
        List<android.location.Address> addresses = null;
        // Add a marker in Sydney and move the camera
        Geocoder gcoder = new Geocoder(this);
        try {
            addresses = gcoder.getFromLocationName("Offa",1);
            android.location.Address address = addresses.get(0);

            if(address.hasLatitude() && address.hasLongitude()){
                latitude = address.getLatitude();
                longitude = address.getLongitude();

                LatLng offa = new LatLng(latitude,longitude);

                mMap.addMarker(new MarkerOptions().position(offa).title("Offa"));
                    //    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_pointer_black)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(offa,MY_DEFAULT_ZOOM));

            }
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    public void onLocate(View view) throws IOException {
        location = (EditText) findViewById(R.id.etLocation);
        String locationText = location.getText().toString();

        List<android.location.Address> addressList = null;

        if(locationText != null || !locationText.equals("")){
            try {

                Geocoder geocoder = new Geocoder(this);
                addressList = geocoder.getFromLocationName(locationText,1);
                android.location.Address address = addressList.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                LatLng latLng = new LatLng(latitude,longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(locationText)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                       //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_pointer_white_big)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,MY_DEFAULT_ZOOM));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


            }
            catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            Toast.makeText(this,"Please supply the location to Search for",Toast.LENGTH_LONG).show();
            location.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.map_menu, menu);

        return true;
    }
    public void onItemClickHandler(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itmChangeMapType:
                if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.itmZoomIn:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.itmZoomOut:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.bTestDialog:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final CharSequence[] placeTypes = {"ATM","Bank","Hospital","Restaurant","School"};

                builder.setTitle("Pick a Place")
                        .setItems(placeTypes,null);
                AlertDialog dialog = builder.create();
                dialog.show();

        }

    }

    public void onButtonClickHandler(View view) {
        switch (view.getId()){
            case R.id.bChangeViewType:
                if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.bZoomIn:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.bZoomOut:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.bPlaceType:
                showPlaceTypeDialog();
                break;
            case R.id.bDirection:
                //Direction Code goes here
                plotDirection();
                break;

            case R.id.bDuration:
                //Duration Code goes here
                calculateDuration();
                break;

            case R.id.bDistance:
                calculateDistance();
                break;

        }
    }

    private void calculateDistance() {
        float results[] = new float[10];
        Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(end_latitude,end_longitude))
                .title("Destination")
                .snippet("Distance = "+results[0]);
        mMap.clear();
        mMap.addMarker(markerOptions);
        LatLng latLng = new LatLng(end_latitude,end_longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void plotDirection()
    {
        Object dataTransferDir[] = new Object[3];
        String urlDir = "";
        urlDir = getDirectionsUrl();

        dataTransferDir[0] = mMap;
        dataTransferDir[1] = urlDir;
        dataTransferDir[2] = new LatLng(end_latitude,end_longitude);

        GetPlotDirectionsLineData getPlotDirectionsLineData = new GetPlotDirectionsLineData();
        getPlotDirectionsLineData.execute(dataTransferDir);

        GetDirectionsData getDirectionsData = new GetDirectionsData();
        getDirectionsData.execute(dataTransferDir);

    }

    private void calculateDuration()
    {
        Object dataTransfer[] = new Object[3];
        String url="";
        url = getDirectionsUrl();

        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(end_latitude,end_longitude);

        GetDirectionsData getDirectionsData = new GetDirectionsData();
        getDirectionsData.execute(dataTransfer);

    }

    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + latitude + "," + longitude)
                .append("&destination=" + end_latitude + "," + end_longitude)
                .append("&key=" + "AIzaSyAq9xzM-O2F3VsIy0ZlBpFfhlYCMxlZPq4");

        return googleDirectionsUrl.toString();
    }

    public void onPlaceDetailsButtonClick(View view) {
        Intent intent = new Intent(this, PlacePickerActivity.class);
        startActivity(intent);
    }

    public void showPlaceTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMapsActivity.this);
        final CharSequence[] someList = {"Atm","Bank","Church","Hospital","Lodging","Mosque",
                "Restaurant","School",};

        builder.setTitle("Select Place Type to Search For")
                .setItems(someList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mNearbyTypeSelectedItem ="";
                        if (which != -1){
                            mNearbyTypeSelectedItem = someList[which].toString().toLowerCase();
                        }

                        try{
                            findPlacesOnMap(mNearbyTypeSelectedItem);
                        }catch (Exception ex){
                            Toast.makeText(MainMapsActivity.this, "Something went wrong\n"+
                                    ex.getMessage(),Toast.LENGTH_LONG)
                                    .show();
                        }


                        /*Toast.makeText(MainMapsActivity.this, mNearbyTypeSelectedItem +" Selected",Toast.LENGTH_LONG)
                                .show();*/
                    }
                });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void findPlacesOnMap(String mNearbyTypeSelectedItem) {
        mMap.clear();
        String url = getUrl(latitude,longitude,mNearbyTypeSelectedItem);
        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(dataTransfer);

        Toast.makeText(MainMapsActivity.this,"Showing Nearby "+mNearbyTypeSelectedItem,
                Toast.LENGTH_LONG).show();
    }

    private String getUrl(double latitude, double longitude, String mNearbyTypeSelectedItem) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+mNearbyTypeSelectedItem);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=AIzaSyAIO5oDc3IMm_TTLbsACqvktFCRAT4Rdfg");

        return googlePlaceUrl.toString();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;
    }
}

