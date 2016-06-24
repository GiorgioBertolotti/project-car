package com.giorgio.provamenu;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by bertolottig on 24/06/2016.
 */
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        MainActivity.loggato.setLatitude(loc.getLatitude());
        MainActivity.loggato.setLongitude(loc.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
