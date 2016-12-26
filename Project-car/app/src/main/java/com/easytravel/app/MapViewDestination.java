package com.easytravel.app;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.easytravel.app.MainActivity.MY_PERMISSION_REQUEST_READ_FINE_LOCATION;
import static com.easytravel.app.MainActivity.ipServer;
import static com.easytravel.app.MainActivity.lat;
import static com.easytravel.app.MainActivity.loggato;
import static com.easytravel.app.MainActivity.lon;
import static com.easytravel.app.MainActivity.range;
import static com.easytravel.app.MainActivity.stato;

public class MapViewDestination extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        AsyncResponse{
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    final Handler handler = new Handler();
    Runnable r;
    final Handler timer = new Handler();
    Runnable r2;
    private boolean isrunning = false;
    String mobilecl;
    private static float ZOOM=0,BEARING=0,TILT=0;
    private static float LAT=0,LON=0;
    private final int MAP_TYPE = GoogleMap.MAP_TYPE_NORMAL;
    private int curMapTypeIndex = 1;
    AnimateHorizontalProgressBar progressBar;
    Marker marker;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_READ_FINE_LOCATION);
        }else {
            setHasOptionsMenu(true);
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            initListeners();
            if(!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_READ_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setHasOptionsMenu(true);
                    mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                    initListeners();
                    if(!mGoogleApiClient.isConnected())
                        mGoogleApiClient.connect();
                } else {
                    return;
                }
                return;
            }
        }
    }
    private void initListeners() {
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnMapClickListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onStop(){
        super.onStop();
        ZOOM = getMap().getCameraPosition().zoom;
        BEARING = getMap().getCameraPosition().bearing;
        TILT = getMap().getCameraPosition().tilt;
        LAT = (float)getMap().getCameraPosition().target.latitude;
        LON = (float)getMap().getCameraPosition().target.longitude;
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        try{mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);}
        catch (SecurityException e){return;}
        initCamera(mCurrentLocation);
    }
    private void initCamera(Location location) {
        if(MainActivity.isFirstMapOpen2) {
            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                    .zoom(16f)
                    .bearing(0.0f)
                    .tilt(0.0f)
                    .build();
            getMap().animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
            MainActivity.isFirstMapOpen2 = false;
        }else{
            if(LAT!=0&&LON!=0) {
                CameraPosition position = CameraPosition.builder()
                        .target(new LatLng(LAT, LON))
                        .zoom(ZOOM)
                        .bearing(BEARING)
                        .tilt(TILT)
                        .build();
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(position));
            }else{
                CameraPosition position = CameraPosition.builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(ZOOM)
                        .bearing(BEARING)
                        .tilt(TILT)
                        .build();
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(position));
            }
        }
        getMap().setMapType(MAP_TYPE);
        getMap().setTrafficEnabled(false);
        try{getMap().setMyLocationEnabled(true);}
        catch (SecurityException e){return;}
        getMap().getUiSettings().setZoomControlsEnabled(false);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        asyncTask.execute(ipServer,"User_Destination", String.format("{\"mobile\":\"%s\",\"Latitude\":\"%s\",\"Longitude\":\"%s\"}",loggato.getMobile(), lat, lon));
        return true;
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    @Override
    public void onMapClick(LatLng latLng) {
        if(marker!=null)
            marker.remove();
        marker = getMap().addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker()));
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        if(marker!=null)
            marker.remove();
        marker = getMap().addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker()));
        Geocoder geoCoder = new Geocoder(MainActivity.context);
        List<Address> matches = null;
        try {
            matches = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address bestMatch = null;
        if (matches != null) {
            bestMatch = (matches.isEmpty() ? null : matches.get(0));
        }
        if (bestMatch != null) {
            Toast.makeText(MainActivity.context, bestMatch.getAddressLine(0), Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(MainActivity.context, "["+latLng.latitude+","+latLng.longitude+"]", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void processFinish(String output) {
        stato = 31;
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.RelativeLayout, new Wait_Fragment())
                .commit();
    }
}
