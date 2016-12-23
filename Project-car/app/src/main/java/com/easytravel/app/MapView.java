package com.easytravel.app;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import static com.easytravel.app.MainActivity.MY_PERMISSION_REQUEST_READ_FINE_LOCATION;

public class MapView extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;
        MyInfoWindowAdapter(){
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
        }
        @Override
        public View getInfoContents(Marker marker) {
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.inftvdesc));
            tvSnippet.setText(marker.getSnippet());
            switch (MainActivity.stato) {
                case 50:
                case 51: {
                    for(User a:MainActivity.ActiveUsers){
                        if(a.getMobile().equals(marker.getTitle())&&a.getImg()!=null){
                            ((ImageView) myContentsView.findViewById(R.id.infivprofile)).setImageBitmap(Bitmap.createScaledBitmap(a.getImg(), 64, 64, false));
                        }else{
                            ((ImageView) myContentsView.findViewById(R.id.infivprofile)).setImageResource(R.drawable.ic_user);
                        }
                    }
                }
                case 43:
                case 44: {
                    for(Autostoppista a:MainActivity.Autostoppisti){
                        if(a.getMobile().equals(marker.getTitle())&&a.getImg()!=null){
                            ((ImageView) myContentsView.findViewById(R.id.infivprofile)).setImageBitmap(Bitmap.createScaledBitmap(a.getImg(), 64, 64, false));
                        }else{
                            ((ImageView) myContentsView.findViewById(R.id.infivprofile)).setImageResource(R.drawable.ic_user);
                        }
                    }
                }
            }
            return myContentsView;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }
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
        getMap().setOnInfoWindowClickListener(this);
        getMap().setInfoWindowAdapter(new MyInfoWindowAdapter());
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
        switch (MainActivity.stato){
            case 50:
            case 51:{
                progressBar = (AnimateHorizontalProgressBar) this.getActivity().findViewById(R.id.animate_progress_bar);
                progressBar.setMax(60);
                progressBar.setProgress(0);
                try{mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);}
                catch (SecurityException e){return;}
                initUsers(MainActivity.ActiveUsers,mCurrentLocation);
                initCamera(mCurrentLocation);
                isrunning = true;
                r = new Runnable(){
                    @Override
                    public void run() {
                        getMap().clear();
                        initUsers(MainActivity.ActiveUsers,mCurrentLocation);
                        handler.postDelayed(this,60000);
                    }
                };
                handler.postDelayed(r, 60000 );
                r2 = new Runnable() {
                    @Override
                    public void run() {
                        if(progressBar.getProgress()==60)
                            progressBar.setProgress(1);
                        else
                            progressBar.setProgress(progressBar.getProgress()+1);
                        timer.postDelayed(this,1000);
                    }
                };
                timer.postDelayed(r2,1000);
                break;
            }
            case 43:
            case 44:{
                progressBar = (AnimateHorizontalProgressBar) this.getActivity().findViewById(R.id.animate_progress_bar);
                progressBar.setMax(60);
                progressBar.setProgress(0);
                try{
                    mCurrentLocation = new Location("");
                    mCurrentLocation.setLongitude(MainActivity.selected.getLongitude());
                    mCurrentLocation.setLatitude(MainActivity.selected.getLatitude());
                }
                catch (SecurityException e){return;}
                initUsers(MainActivity.Autostoppisti,mCurrentLocation);
                initCamera(mCurrentLocation);
                isrunning = true;
                r = new Runnable(){
                    @Override
                    public void run() {
                        getMap().clear();
                        mCurrentLocation.setLongitude(MainActivity.selected.getLongitude());
                        mCurrentLocation.setLatitude(MainActivity.selected.getLatitude());
                        initUsers(MainActivity.Autostoppisti,mCurrentLocation);
                        handler.postDelayed(this,60000);
                    }
                };
                handler.postDelayed(r, 60000 );
                r2 = new Runnable() {
                    @Override
                    public void run() {
                        if(progressBar.getProgress()==60)
                            progressBar.setProgress(1);
                        else
                            progressBar.setProgress(progressBar.getProgress()+1);
                        timer.postDelayed(this,1000);
                    }
                };
                timer.postDelayed(r2,1000);
                break;
            }
        }
    }
    public void initUsers(ArrayList<?> attivi,Location currentloc){
        switch (MainActivity.stato){
            case 50:{
                for(Object b : attivi) {
                    User a = (User) b;
                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm dd MMM yyyy");
                    if (a.getType_id() == 1) {
                        Marker marker = getMap().addMarker(new MarkerOptions()
                                .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                .title(a.getMobile())
                                .snippet(String.format("%s %s\n%s,%s\n%s\n%s", a.getName(), a.getSurname(), a.getLatitude(), a.getLongitude(), dt.format(a.getDate()).toString(), getAddressFromLatLng(new LatLng(a.getLatitude(), a.getLongitude()))))
                                .icon(BitmapDescriptorFactory.defaultMarker()));
                    } else {
                        Marker marker = getMap().addMarker(new MarkerOptions()
                                .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                .title(a.getMobile())
                                .snippet(String.format("%s %s\n%s,%s\n%s\n%s", a.getName(), a.getSurname(), a.getLatitude(), a.getLongitude(), dt.format(a.getDate()).toString(), getAddressFromLatLng(new LatLng(a.getLatitude(), a.getLongitude()))))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                }
                Circle circle = getMap().addCircle(new CircleOptions()
                        .center(new LatLng(currentloc.getLatitude(), currentloc.getLongitude()))
                        .radius(MainActivity.loggato.getRange()*1000)
                        .strokeColor(Color.RED));
                break;
            }
            case 51:{
                for(Object b : attivi) {
                    User a = (User) b;
                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm dd MMM yyyy");
                    if (a.getType_id() == 1) {
                        Marker marker = getMap().addMarker(new MarkerOptions()
                                .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                .title(a.getMobile())
                                .snippet(String.format("%s %s\n%s,%s\n%s\n%s", a.getName(), a.getSurname(), a.getLatitude(), a.getLongitude(), dt.format(a.getDate()).toString(), getAddressFromLatLng(new LatLng(a.getLatitude(), a.getLongitude()))))
                                .icon(BitmapDescriptorFactory.defaultMarker()));
                        if(a.getMobile().equals(mobilecl))
                            marker.showInfoWindow();
                    } else {
                        Marker marker = getMap().addMarker(new MarkerOptions()
                                .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                .title(a.getMobile())
                                .snippet(String.format("%s %s\n%s,%s\n%s\n%s", a.getName(), a.getSurname(), a.getLatitude(), a.getLongitude(), dt.format(a.getDate()).toString(), getAddressFromLatLng(new LatLng(a.getLatitude(), a.getLongitude()))))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        if(a.getMobile().equals(mobilecl))
                            marker.showInfoWindow();
                    }
                    Circle circle = getMap().addCircle(new CircleOptions()
                            .center(new LatLng(currentloc.getLatitude(), currentloc.getLongitude()))
                            .radius(MainActivity.loggato.getRange()*1000)
                            .strokeColor(Color.RED));
                }
                break;
            }
            case 43:{
                for(Object b : attivi) {
                    Autostoppista a = (Autostoppista) b;
                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm dd MMM yyyy");
                    Marker marker = getMap().addMarker(new MarkerOptions()
                            .position(new LatLng(a.getLatitude(), a.getLongitude()))
                            .title(a.getMobile())
                            .snippet(String.format("%s %s\n%s,%s\n%s\n%s", a.getName(), a.getSurname(), a.getLatitude(), a.getLongitude(), dt.format(a.getDate()).toString(), getAddressFromLatLng(new LatLng(a.getLatitude(), a.getLongitude()))))
                            .icon(BitmapDescriptorFactory.defaultMarker()));
                    if (a.getMobile().equals(MainActivity.selected.getMobile())) {
                        marker.showInfoWindow();
                        Circle circle = getMap().addCircle(new CircleOptions()
                                .center(new LatLng(currentloc.getLatitude(), currentloc.getLongitude()))
                                .radius(MainActivity.selected.getRange()*1000)
                                .strokeColor(Color.RED));
                    }
                }
                break;
            }
            case 44:{
                for(Object b : attivi) {
                    Autostoppista a = (Autostoppista) b;
                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm dd MMM yyyy");
                    Marker marker = getMap().addMarker(new MarkerOptions()
                            .position(new LatLng(a.getLatitude(), a.getLongitude()))
                            .title(a.getMobile())
                            .snippet(String.format("%s %s\n%s,%s\n%s\n%s", a.getName(), a.getSurname(), a.getLatitude(), a.getLongitude(), dt.format(a.getDate()).toString(), getAddressFromLatLng(new LatLng(a.getLatitude(), a.getLongitude()))))
                            .icon(BitmapDescriptorFactory.defaultMarker()));
                    if (a.getMobile().equals(MainActivity.selected.getMobile())) {
                        Circle circle = getMap().addCircle(new CircleOptions()
                                .center(new LatLng(currentloc.getLatitude(), currentloc.getLongitude()))
                                .radius(MainActivity.selected.getRange()*1000)
                                .strokeColor(Color.RED));
                    }
                    if(a.getMobile().equals(mobilecl)) {
                        marker.showInfoWindow();
                    }
                }
                break;
            }
        }
    }
    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder(getActivity());
        String address = "";
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            address = "";
        }
        return address;
    }
    private void initCamera(Location location) {
        if(MainActivity.isFirstMapOpen) {
            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                    .zoom(16f)
                    .bearing(0.0f)
                    .tilt(0.0f)
                    .build();
            getMap().animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
            MainActivity.isFirstMapOpen = false;
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
        if(isrunning) {
            handler.removeCallbacks(r);
            timer.removeCallbacks(r2);
        }
        super.onDestroy();
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        mobilecl = marker.getTitle();
        marker.showInfoWindow();
        return true;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        if(MainActivity.stato == 50||MainActivity.stato == 51)
            MainActivity.stato = 52;
        if(MainActivity.stato == 43||MainActivity.stato == 44)
            MainActivity.stato = 45;
        for(User a : MainActivity.ActiveUsers){
            String m = a.getMobile();
            String t= marker.getTitle();
            if(m.equals(t)) MainActivity.selected = a;
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.RelativeLayout, new Profile_Fragment())
                .commit();
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    @Override
    public void onMapClick(LatLng latLng) {
        mobilecl = null;
    }
    @Override
    public void onMapLongClick(LatLng latLng) {}
}
