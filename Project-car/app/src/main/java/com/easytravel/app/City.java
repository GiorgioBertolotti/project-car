package com.easytravel.app;

/**
 * Created by bertolottig on 22/06/2016.
 */
public class City {
    private String name;
    private String province;
    private String setName(String value){
        if(value.isEmpty()||value.equals(""))
            return "";
        if(value.length()>20)
            return "";
        name = value;
        return "ok";
    }
    private String setProvince(String value){
        if(value.isEmpty()||value.equals(""))
            return "";
        if(value.length()>2)
            return "";
        province = value;
        return "ok";
    }
    public String getName(){return name;}
    public String getProvince(){return province;}
    public City(String n,String p){
        if(setName(n).equals("")||setProvince(p).equals(""))
            return;
    }
    @Override
    public String toString(){
        return this.getName()+", "+this.getProvince();
    }
}
