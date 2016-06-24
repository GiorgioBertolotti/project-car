package com.giorgio.provamenu;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bertolottig on 24/06/2016.
 */
public class setGPSLocation extends AsyncTask<String,String,String> {
    Context mContext;
    public setGPSLocation(Context mContext){
        this.mContext = mContext;
    }
    @Override
    public String doInBackground(String... params){
        while(true){
            if(!GPSLoc(params))
                break;
        }
        return "Error";
    }
    public boolean GPSLoc(String[] params){
        // Get the location manager
        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
        } catch (NullPointerException e) {
            double lat = -1.0;
            double lon = -1.0;
        }
        String result= "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(params[0]);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("api_method", params[1]));
        nameValuePair.add(new BasicNameValuePair("api_data", params[2]));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {}
        try {
            HttpResponse response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            result = e.getMessage();
        }
        try{
            JSONObject obj = new JSONObject(result);
            if(obj.getBoolean("IsError")==true)
                return false;
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
