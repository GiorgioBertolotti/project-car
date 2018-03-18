package com.easytravel.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.easytravel.app.MainActivity.context;

/**
 * Created by bertolottig on 22/06/2016.
 */
public class User {
    private String name;
    private String surname;
    private String mail;
    private String mobile;
    private Integer type_id;
    private int range;
    private Bitmap img;
    private Double rating;
    private Double latitude;
    private Double longitude;
    private String position;
    private Date date;
    private String setName(String value){
        if(value.isEmpty()||value.equals(""))
            return "";
        if(value.length()>20)
            return "";
        name = value;
        return "ok";
    }
    private String setSurname(String value){
        if(value.isEmpty()||value.equals(""))
            return "";
        if(value.length()>20)
            return "";
        surname = value;
        return "ok";
    }
    private String setMail(String value){
        mail = value;
        return "ok";
    }
    private String setMobile(String value){
        if(value.isEmpty()||value.equals(""))
            return "";
        if(value.length()>20)
            return "";
        mobile = value;
        return "ok";
    }
    public String setType_id(Integer value){
        type_id = value;
        return "ok";
    }
    public String setLongitude(Double value){
        longitude = value;
        return "ok";
    }
    public String setLatitude(Double value){
        latitude = value;
        return "ok";
    }
    public String setDate(Date value){
        date = value;
        return "ok";
    }
    public String setRange(int value){
        if(value>100||value<0)
            return "";
        range = value;
        return "ok";
    }
    public String setImage(Bitmap value){
        if(value==null)
            return "";
        img = value;
        return "ok";
    }
    public String setRating(Double value){
        if(value>5||value<0)
            return "";
        rating = value;
        return "ok";
    }
    public String setPosition(Double lat,Double lon){
        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if(addresses.get(0).getAddressLine(0)!=null&&addresses.get(0).getAddressLine(1)!=null)
                position = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1);
            else
                position = addresses.get(0).getAddressLine(0);
        }catch (Exception e){
            Log.e("Error",e.getMessage());
            return "";
        }
        return "ok";
    }
    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getMobile(){ return mobile; }
    public String getMail(){ return mail; }
    public Integer getType_id(){return type_id;}
    public Double getLatitude(){return latitude;}
    public Double getLongitude(){return longitude;}
    public Date getDate(){return date;}
    public int getRange(){return range;}
    public Bitmap getImg(){return img;}
    public Double getRating(){return rating;}
    public String getPosition(){return position;}
    public User(String n, String s, String m,String ma, Integer t, Integer r, Bitmap i, Double rating){
        setName(n);
        setSurname(s);
        setMobile(m);
        setMail(ma);
        setType_id(t);
        setRange(r);
        setRating(rating);
        setImage(i);
    }
    public User(String n,String s,String m,String ma, Integer t,Integer r,Double lo,Double la,Date d,Bitmap i,Double rating){
        setName(n);
        setSurname(s);
        setMobile(m);
        setMail(ma);
        setType_id(t);
        setRange(r);
        setLatitude(la);
        setLongitude(lo);
        setDate(d);
        setRating(rating);
        setImage(i);
        setPosition(this.getLatitude(),this.getLongitude());
    }
}

