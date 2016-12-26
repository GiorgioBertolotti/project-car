package com.easytravel.app;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by bertolottig on 23/06/2016.
 */
public class Autostoppista extends User {
    private Double destlat;
    private Double destlon;
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
    public Double getDestlat(){return destlat;}
    public Double getDestlon(){return destlon;}
    public Autostoppista(String n, String s, String m, Integer t, Integer r, Bitmap i, Double destla, Double destlo, Double la, Double lo, Date d){
        super(n,s,m,t,r,lo,la,d,i);
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
    }
    public Autostoppista(String n, String s, String m, Integer t, Integer r, Bitmap i){
        super(n,s,m,t,r,i);
        toString = this.getName()+" "+this.getSurname();
    }
    @Override
    public String toString(){
        return toString;
    }
}
