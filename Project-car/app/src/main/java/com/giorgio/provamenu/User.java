package com.giorgio.provamenu;

import java.util.Date;

/**
 * Created by bertolottig on 22/06/2016.
 */
public class User {
    private String name;
    private String surname;
    private String mobile;
    private int type_id;
    private double latitude;
    private double longitude;
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
    public String setType_id(int value){
        type_id = value;
        return "ok";
    }
    public String setLongitude(double value){
        longitude = value;
        return "ok";
    }
    public String setLatitude(double value){
        latitude = value;
        return "ok";
    }
    public String setDate(Date value){
        date = value;
        return "ok";
    }
    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getMobile(){ return mobile; }
    public int getType_id(){return type_id;}
    public User(String n,String s,String m){
        if(setName(n).equals("")||setSurname(s).equals("")||setMobile(m).equals(""))
            return;
    }
    public User(String n,String s,String m,int t){
        if(setName(n).equals("")||setSurname(s).equals("")||setMobile(m).equals("")||setType_id(t).equals(""))
            return;
    }
    public User(String n,String s,String m,int t,double lo,double la,Date d){
        if(setName(n).equals("")||setSurname(s).equals("")||setMobile(m).equals("")||setType_id(t).equals("")||setLatitude(la).equals("")||setLongitude(lo).equals("")||setDate(d).equals(""))
            return;
    }
}

