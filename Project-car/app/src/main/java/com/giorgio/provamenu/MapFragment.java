package com.giorgio.provamenu;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
                MainActivity.stato = 80;
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
            case 50:{
                try{mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);}
                catch (SecurityException e){return;}
                initUsers(MainActivity.ActiveUsers);
                initCamera(mCurrentLocation);
                break;
            }
            case 55:{
                try{
                    mCurrentLocation = new Location("");
                    mCurrentLocation.setLongitude(MainActivity.selected.getLongitude());
                    mCurrentLocation.setLatitude(MainActivity.selected.getLatitude());
                }
                catch (SecurityException e){return;}
                initUsers(MainActivity.ActiveUsers);
                initCamera(mCurrentLocation);
                break;
            }
        }

    }

    public void initUsers(ArrayList<User> attivi){
        for(User a : attivi) {
            SimpleDateFormat dt = new SimpleDateFormat("HH:mm dd MMM yyyy");
            if(a.getType_id()==1) {
                Marker marker = getMap().addMarker(new MarkerOptions()
                        .position(new LatLng(a.getLatitude(),a.getLongitude()))
                        .title(a.getMobile())
                        .snippet(String.format("%s %s\n%s,%s\n%s\n%s",a.getName(),a.getSurname(),a.getLatitude(),a.getLongitude(),dt.format(a.getDate()).toString(),getAddressFromLatLng(new LatLng(a.getLatitude(),a.getLongitude()))))
                        .icon(BitmapDescriptorFactory.defaultMarker()));
                if(MainActivity.stato == 55&&a.getMobile().equals(MainActivity.selected.getMobile()))
                    marker.showInfoWindow();
            }
            else {
                Marker marker = getMap().addMarker(new MarkerOptions()
                        .position(new LatLng(a.getLatitude(),a.getLongitude()))
                        .title(a.getMobile())
                        .snippet(String.format("%s %s\n%s,%s\n%s\n%s",a.getName(),a.getSurname(),a.getLatitude(),a.getLongitude(),dt.format(a.getDate()).toString(),getAddressFromLatLng(new LatLng(a.getLatitude(),a.getLongitude()))))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                if(MainActivity.stato == 55&&a.getMobile().equals(MainActivity.selected.getMobile()))
                    marker.showInfoWindow();
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
    public boolean onMarkerClick(Marker marker) {
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

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
