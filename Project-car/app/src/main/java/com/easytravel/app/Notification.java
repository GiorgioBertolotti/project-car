package com.easytravel.app;

import java.util.Date;

public class Notification {
    private String name;
    private String surname;
    private String mobile;
    private String type;
    private Date datetime;
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
    private String setType(String value){
        if(value.isEmpty()||value.equals(""))
            return "";
        if(value.length()>20)
            return "";
        type = value;
        return "ok";
    }
    public String setDatetime(Date value){
        datetime = value;
        return "ok";
    }
    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getMobile(){ return mobile; }
    public Date getDatetime() {
        return datetime;
    }
    public String getType() {
        return type;
    }
    public Notification(String n,String s,String m,Date dt,String t){
        if(setName(n).equals("")||setSurname(s).equals("")||setMobile(m).equals("")||setDatetime(dt).equals("")||setType(t).equals(""))
            return;
    }
}
