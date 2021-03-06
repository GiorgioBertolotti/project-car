package com.easytravel.app;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.easytravel.app.MainActivity.context;

/**
 * Created by bertolottig on 23/06/2016.
 */
public class Autostoppista extends User {
    private Double destlat;
    private Double destlon;
    private String dest;
    private String toString;
    public String setDestlat(Double value){
        if(value!=null)
            if(value > 90 || value < -90)
                return "";
        destlat = value;
        return "ok";
    }
    public String setDestlon(Double value){
        if(value!=null)
            if(value > 180 || value < -180)
                return "";
        destlon = value;
        return "ok";
    }
    public String setDestination(Double lat,Double lon){
        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if(addresses.get(0).getAddressLine(0)!=null&&addresses.get(0).getAddressLine(1)!=null)
                dest = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1);
            else
                dest = addresses.get(0).getAddressLine(0);
        }catch (Exception e){
            Log.e("Error",e.getMessage());
            return "";
        }
        return "ok";
    }
    public Double getDestlat(){return destlat;}
    public Double getDestlon(){return destlon;}
    public String getDest(){return dest;}
    public Autostoppista(String n, String s, String m, String ma, Integer t, Integer r, Bitmap i, Double destla, Double destlo, Double la, Double lo, Date d,Double rating){
        super(n,s,m,ma,t,r,lo,la,d,i,rating);
        if(setDestlat(destla).equals("")||setDestlon(destlo).equals(""))
            return;
        Geocoder geoCoder = new Geocoder(MainActivity.context);
        List<Address> matches = null;
        try {
            matches = geoCoder.getFromLocation(getDestlat(), getDestlon(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address bestMatch = null;
        if (matches != null) {
            bestMatch = (matches.isEmpty() ? null : matches.get(0));
        }
        if (bestMatch != null) {
            toString = this.getName()+" "+this.getSurname()+", "+bestMatch.getAddressLine(1);
        }else
            toString = this.getName()+" "+this.getSurname()+" ["+this.getDestlat()+","+this.getDestlon()+"]";
        setDestination(this.getDestlat(),this.getDestlon());
    }
    public Autostoppista(String n, String s, String m, String ma, Integer t, Integer r, Bitmap i,Double rating){
        super(n,s,m,ma,t,r,i,rating);
        toString = this.getName()+" "+this.getSurname();
        setDestination(this.getDestlat(),this.getDestlon());
    }
    @Override
    public String toString(){
        return toString;
    }
}
