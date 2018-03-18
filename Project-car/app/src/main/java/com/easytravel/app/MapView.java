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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import static com.easytravel.app.MainActivity.mDrawerToggle;
import static com.easytravel.app.MainActivity.selectedOnMap;
import static com.easytravel.app.MainActivity.stato;

public class MapView extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;
        MyInfoWindowAdapter(){
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.info_window_map, null);
        }
        @Override
        public View getInfoContents(Marker marker) {
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.inftvdesc));
            tvSnippet.setText(marker.getSnippet());
            switch (stato) {
                case 50:
                case 51: {
                    (myContentsView.findViewById(R.id.infivprofile)).setVisibility(View.VISIBLE);
                    for(User a:MainActivity.ActiveUsers){
                        if(a.getMobile().equals(marker.getTitle())&&a.getImg()!=null){
                            ((ImageView) myContentsView.findViewById(R.id.infivprofile)).setImageBitmap(Bitmap.createScaledBitmap(a.getImg(), 64, 64, false));
                        }else{
                            ((ImageView) myContentsView.findViewById(R.id.infivprofile)).setImageResource(R.drawable.ic_user);
                        }
                    }
                    break;
                }
                case 43:
                case 44: {
                    (myContentsView.findViewById(R.id.infivprofile)).setVisibility(View.INVISIBLE);
                    break;
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
    public static GoogleMap map;
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
        PlaceAutocompleteFragment fragment = new PlaceAutocompleteFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                ZOOM = getMap().getCameraPosition().zoom;
                BEARING = getMap().getCameraPosition().bearing;
                TILT = getMap().getCameraPosition().tilt;
                mCurrentLocation.setLatitude(place.getLatLng().latitude);
                mCurrentLocation.setLongitude(place.getLatLng().longitude);
                initCamera(mCurrentLocation);
                Log.i("Place", "Place: " + place.getName());
            }
            @Override
            public void onError(Status status) {
                Log.i("Error", "An error occurred: " + status);
            }
        });
        if(stato==43||stato==44){
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getActivity().findViewById(R.id.tlbbtnsettings).setVisibility(View.INVISIBLE);
            MainActivity.notificationsVisibility = getActivity().findViewById(R.id.notification).getVisibility();
            getActivity().findViewById(R.id.notification).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.tlbbtnnotifications).setVisibility(View.INVISIBLE);
            mDrawerToggle.syncState();
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerToggle.setToolbarNavigationClickListener(null);
                    DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    getActivity().findViewById(R.id.tlbbtnsettings).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.notification).setVisibility(MainActivity.notificationsVisibility);
                    getActivity().findViewById(R.id.tlbbtnnotifications).setVisibility(View.VISIBLE);
                    mDrawerToggle.syncState();
                }
            });
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
        map = getMap();
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
        progressBar = (AnimateHorizontalProgressBar) this.getActivity().findViewById(R.id.animate_progress_bar);
        progressBar.setMax(60);
        progressBar.setProgress(0);
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
        switch (stato){
            case 50:
            case 51:{
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
                break;
            }
            case 43:
            case 44:{
                try{
                    mCurrentLocation = new Location("");
                    mCurrentLocation.setLongitude(MainActivity.selectedOnMap.getLongitude());
                    mCurrentLocation.setLatitude(MainActivity.selectedOnMap.getLatitude());
                }
                catch (SecurityException e){return;}
                Marker marker = getMap().addMarker(new MarkerOptions()
                        .position(new LatLng(selectedOnMap.getLatitude(), selectedOnMap.getLongitude()))
                        .title(selectedOnMap.getMobile())
                        .snippet(String.format("%s\n(%s, %s)", getAddressFromLatLng(new LatLng(selectedOnMap.getLatitude(), selectedOnMap.getLongitude())), selectedOnMap.getLatitude(), selectedOnMap.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                if(selectedOnMap.getMobile().equals(mobilecl))
                    marker.showInfoWindow();
                marker = getMap().addMarker(new MarkerOptions()
                        .position(new LatLng(selectedOnMap.getDestlat(), selectedOnMap.getDestlon()))
                        .title(selectedOnMap.getMobile())
                        .snippet(String.format("%s\n(%s, %s)", getAddressFromLatLng(new LatLng(selectedOnMap.getDestlat(), selectedOnMap.getDestlon())), selectedOnMap.getDestlat(), selectedOnMap.getDestlon()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                if(selectedOnMap.getMobile().equals(mobilecl))
                marker.showInfoWindow();
                getMap().addCircle(new CircleOptions()
                        .center(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                        .radius(MainActivity.selectedOnMap.getRange()*1000)
                        .strokeColor(Color.RED));
                connectAsyncTask task = new connectAsyncTask(makeURL(selectedOnMap.getLatitude(),selectedOnMap.getLongitude(),selectedOnMap.getDestlat(),selectedOnMap.getDestlon()));
                task.execute();
                initCamera(mCurrentLocation);
                isrunning = true;
                r = new Runnable(){
                    @Override
                    public void run() {
                        mCurrentLocation.setLongitude(MainActivity.selectedOnMap.getLongitude());
                        mCurrentLocation.setLatitude(MainActivity.selectedOnMap.getLatitude());
                        handler.postDelayed(this,60000);
                    }
                };
                handler.postDelayed(r, 60000 );
                break;
            }
        }
    }
    public void initUsers(ArrayList<?> attivi,Location currentloc){
        switch (stato){
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
        if(stato == 50|| stato == 51) {
            stato = 52;
            for(User a : MainActivity.ActiveUsers){
                String m = a.getMobile();
                String t= marker.getTitle();
                if(m.equals(t)) MainActivity.selected = a;
            }
        }
        if(stato == 43|| stato == 44) {
            stato = 45;
            for(User a : MainActivity.Autostoppisti){
                String m = a.getMobile();
                String t= marker.getTitle();
                if(m.equals(t)) MainActivity.selected = a;
            }
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.RelativeLayout, new Profile_Fragment())
                .commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profilo utente");
        ((TextView) getActivity().findViewById(R.id.tlbtxttitle)).setText("Profilo utente");
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
    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyCffbNuWyROpx1zpavp0hSgWSBwsu7m_6M");
        return urlString.toString();
    }
}
