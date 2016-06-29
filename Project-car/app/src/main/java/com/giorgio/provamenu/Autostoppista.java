package com.giorgio.provamenu;

import java.util.Date;

/**
 * Created by bertolottig on 23/06/2016.
 */
public class Autostoppista extends User {
    private String city;
    private String province;
    public String setCity(String value){
        if(value != null)
            if(value.length()>20)
                return "";
        city = value;
        return "ok";
    }
    public String setProvince(String value){
        if(value != null)
            if(value.length()>2)
                return "";
        province = value;
        return "ok";
    }
    public String getCity(){
        return city;
    }
    public String getProvince(){
        return province;
    }
    public Autostoppista(String n,String s,String m,Integer t,Integer r,String c,String p,Double la,Double lo,Date d){
        super(n,s,m,t,r,lo,la,d);
        if(setCity(c).equals("")||setProvince(p).equals(""))
            return;
    }
    public Autostoppista(String n,String s,String m){
        super(n,s,m,null);
    }
    @Override
    public String toString(){
        return this.getName()+" "+this.getSurname()+", "+this.getCity();
    }
}
