package com.giorgio.provamenu;

/**
 * Created by bertolottig on 23/06/2016.
 */
public class Autostoppista extends User {
    private String city;
    private String province;
    public String setCity(String value){
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
    public Autostoppista(String n,String s,String m,String c,String p){
        super(n,s,m);
        if(setCity(c).equals("")||setProvince(p).equals(""))
            return;
    }
    public Autostoppista(String n,String s,String m){
        super(n,s,m);
    }
    @Override
    public String toString(){
        return this.getName()+" "+this.getSurname()+", "+this.getCity();
    }
}
