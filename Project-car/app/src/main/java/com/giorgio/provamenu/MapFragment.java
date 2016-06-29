package com.giorgio.provamenu;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.RunnableFuture;

/**
 * Created by bertolottig on 27/06/2016.
 */
public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
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
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.textView14));
            tvSnippet.setText(marker.getSnippet());
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
    private boolean isrunning = false;
    String mobilecl;
    private final int MAP_TYPE = GoogleMap.MAP_TYPE_NORMAL;
    private int curMapTypeIndex = 1;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder( getActivity() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        initListeners();
    }

    private void initListeners() {
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                MainActivity.stato = 52;
                for(User a : MainActivity.ActiveUsers){
                    String m = a.getMobile();
                    String t= marker.getTitle();
                    if(m.equals(t)) MainActivity.selected = a;
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Profile_Fragment())
                        .commit();
            }
        });
        getMap().setInfoWindowAdapter(new MyInfoWindowAdapter());
        getMap().setOnMapClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        switch (MainActivity.stato){
            case 50:
            case 51:{
                try{mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);}
                catch (SecurityException e){return;}
                initUsers(MainActivity.ActiveUsers);
                initCamera(mCurrentLocation);
                isrunning = true;
                r = new Runnable(){
                    @Override
                    public void run() {
                        getMap().clear();
                        initUsers(MainActivity.ActiveUsers);
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
                    mCurrentLocation.setLongitude(MainActivity.selected.getLongitude());
                    mCurrentLocation.setLatitude(MainActivity.selected.getLatitude());
                }
                catch (SecurityException e){return;}
                initUsers(MainActivity.Autostoppisti);
                initCamera(mCurrentLocation);
                isrunning = true;
                r = new Runnable(){
                    @Override
                    public void run() {
                        getMap().clear();
                        initUsers(MainActivity.Autostoppisti);
                        handler.postDelayed(this,60000);
                    }
                };
                handler.postDelayed(r, 60000 );
                break;
            }
        }

    }

    public void initUsers(ArrayList<?> attivi){
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
                    if (a.getMobile().equals(MainActivity.selected.getMobile()))
                        marker.showInfoWindow();
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
                    if(a.getMobile().equals(mobilecl))
                        marker.showInfoWindow();
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
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();
        getMap().animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
        getMap().setMapType(MAP_TYPE);
        getMap().setTrafficEnabled(false);
        try{getMap().setMyLocationEnabled(true);}
        catch (SecurityException e){return;}
        getMap().getUiSettings().setZoomControlsEnabled(false);
    }

    @Override
    public void onDestroy(){
        if(isrunning)
            handler.removeCallbacks(r);
        super.onDestroy();
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        mobilecl = marker.getTitle();
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mobilecl = null;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
