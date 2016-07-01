package com.giorgio.provamenu;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.Date;

/**
 * Created by bertolottig on 22/06/2016.
 */
public class User {
    private String name;
    private String surname;
    private String mobile;
    private Integer type_id;
    private Integer range;
    private Bitmap img;
    private Double latitude;
    private Double longitude;
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
    public String setRange(Integer value){
        if(value!=null&value<0)
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
    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getMobile(){ return mobile; }
    public Integer getType_id(){return type_id;}
    public Double getLatitude(){return latitude;}
    public Double getLongitude(){return longitude;}
    public Date getDate(){return date;}
    public Integer getRange(){return range;}
    public Bitmap getImg(){return img;}
    public User(String n,String s,String m,Integer t){
        if(setName(n).equals("")||setSurname(s).equals("")||setMobile(m).equals("")||setType_id(t).equals(""))
            return;
    }
    public User(String n,String s,String m,Integer t,Integer r,Double lo,Double la,Date d,Bitmap i){
        if(setName(n).equals("")||setSurname(s).equals("")||setMobile(m).equals("")||setType_id(t).equals("")||setRange(r).equals("")||setLatitude(la).equals("")||setLongitude(lo).equals("")||setDate(d).equals("")||setImage(i).equals(""))
            return;
    }
}

