package com.easytravel.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Register_Fragment.OnFragmentInteractionListener,
        Login_Fragment.OnFragmentInteractionListener,
        Profile_Fragment.OnFragmentInteractionListener,
        City_Fragment.OnFragmentInteractionListener,
        User_Fragment.OnFragmentInteractionListener,
        Wait_Fragment.OnFragmentInteractionListener,
        Map_Fragment.OnFragmentInteractionListener,
        Settings_Fragment.OnFragmentInteractionListener,
        AsyncResponse,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    RelativeLayout rl;
    NavigationView navigationView;
    ArrayList<City> cities;
    ArrayAdapter<City> citiesAdapter;
    public static Autostoppista loggato;
    public static User selected;
    public static ArrayList<User> ActiveUsers;
    public static ArrayAdapter<User> usersAdapter;
    public static ArrayList<Autostoppista> Autostoppisti;
    public static ArrayAdapter<Autostoppista> autostoppistiAdapter;
    public static int stato = 0;
    public static String ipServer = "http://172.22.20.107:8081/pcws/index.php";
    int prec;
    String lat,lon;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int range;
    final Handler h = new Handler();
    public static String img;
    boolean doubleBackToExitPressedOnce = false;
    public void changeUI(){
        switch (stato){
            case 0:{
                setContentView(R.layout.activity_main);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                assert drawer != null;
                drawer.setDrawerListener(toggle);
                toggle.syncState();
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                assert navigationView != null;
                navigationView.setNavigationItemSelectedListener(this);
                navigationView.getMenu().findItem(R.id.logout).setVisible(false);
                navigationView.getMenu().findItem(R.id.login).setVisible(true);
                navigationView.getMenu().findItem(R.id.register).setVisible(true);
                navigationView.getMenu().findItem(R.id.autostoppista).setEnabled(false);
                navigationView.getMenu().findItem(R.id.autista).setEnabled(false);
                navigationView.getMenu().findItem(R.id.mappa).setEnabled(false);
                rl = (RelativeLayout) findViewById(R.id.RelativeLayout);
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Login_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Login");
                break;
            }
            case 1:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Settings_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Impostazioni");
                break;
            }
            case 10:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Register_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Registrati");
                break;
            }
            case 20:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Profile_Fragment())
                        .addToBackStack(null)
                        .commit();
                navigationView.getMenu().findItem(R.id.logout).setVisible(true);
                navigationView.getMenu().findItem(R.id.login).setVisible(false);
                navigationView.getMenu().findItem(R.id.register).setVisible(false);
                navigationView.getMenu().findItem(R.id.autostoppista).setEnabled(true);
                navigationView.getMenu().findItem(R.id.autista).setEnabled(true);
                navigationView.getMenu().findItem(R.id.mappa).setEnabled(true);
                getSupportActionBar().setTitle("EasyTravel");
                break;
            }
            case 21:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                            .replace(R.id.RelativeLayout, new Settings_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Impostazioni");
                break;
            }
            case 30:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new City_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Scegli destinazione");
                break;
            }
            case 31:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Wait_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Attesa");
                break;
            }
            case 40:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new User_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Scegli un autostoppista");
                break;
            }
            case 42:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Profile_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Profilo pubblico");
                break;
            }
            case 43:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Map_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Mappa");
                break;
            }
            case 50:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Map_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Mappa");
                break;
            }
            case 52:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Profile_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Profilo pubblico");
                break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        buildGoogleApiClient();
        changeUI();
        SharedPreferences settings = getSharedPreferences("UserData", 0);
        String m = settings.getString("Mobile", null);
        String p = settings.getString("Password", null);
        ipServer = settings.getString("IP", null);
        if(m!=null||p!=null){
            funcPHP("loginUser",String.format("{\"mobile\":\"%s\",\"password\":\"%s\"}",m,p));
        }
        cities = new ArrayList<>();
        citiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        ActiveUsers = new ArrayList<>();
        usersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ActiveUsers);
        Autostoppisti = new ArrayList<>();
        autostoppistiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Autostoppisti);
        range = 10;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            switch (stato){
                case 0: {
                    if (doubleBackToExitPressedOnce) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        break;
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Click back again to exit", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;
                        }
                    }, 2000);
                    break;
                }
                case 1: {
                    stato= prec;
                    changeUI();
                    break;
                }
                case 10:{
                    stato= 0;
                    changeUI();
                    break;
                }
                case 20:{
                    if (doubleBackToExitPressedOnce) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        break;
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Click back again to exit", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;
                        }
                    }, 2000);
                    break;
                }
                case 21:{
                    stato= prec;
                    changeUI();
                    break;
                }
                case 30:{
                    loggato.setType_id(null);
                    funcPHP("removeUser_Type",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                    stato = 20;
                    changeUI();
                    break;
                }
                case 31:{
                    funcPHP("removeUser_City",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                    stato = 30;
                    changeUI();
                    break;
                }
                case 40:{
                    funcPHP("removeUser_Type",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                    stato = 20;
                    changeUI();
                    break;
                }
                case 42:{
                    stato = 40;
                    changeUI();
                    break;
                }
                case 43:{
                    stato = 40;
                    changeUI();
                    break;
                }
                case 44:{
                    stato = 40;
                    changeUI();
                    break;
                }
                case 45:{
                    stato = 43;
                    changeUI();
                    break;
                }
                case 50:{
                    stato = 20;
                    changeUI();
                    break;
                }
                case 51:{
                    stato = 20;
                    changeUI();
                    break;
                }
                case 52:{
                    stato = 50;
                    changeUI();
                    break;
                }
                default: break;
            }
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.register: {
                stato = 10;
                changeUI();
                break;
            }
            case R.id.login: {
                stato = 0;
                changeUI();
                break;
            }
            case R.id.autista: {
                loggato.setType_id(2);
                if(stato == 31){
                    loggato.setCity(null);
                    loggato.setProvince(null);
                    funcPHP("removeUser_City",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                }
                funcPHP("User_Type",String.format("{\"mobile\":\"%s\",\"type\":\"autista\"}",loggato.getMobile()));
                break;
            }
            case R.id.autostoppista: {
                loggato.setType_id(1);
                funcPHP("User_Type",String.format("{\"mobile\":\"%s\",\"type\":\"autostoppista\"}",loggato.getMobile()));
                break;
            }
            case R.id.mappa: {
                if(stato == 31){
                    loggato.setCity(null);
                    loggato.setProvince(null);
                    funcPHP("removeUser_City",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                }
                stato = 50;
                funcPHP("removeUser_Type",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                funcPHP("getActiveUsers","{}");
                break;
            }
            case R.id.logout: {
                funcPHP("logoutUser",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void processFinish(String output){
        try {
            JSONObject obj = new JSONObject(output);
            if(obj.getBoolean("IsError")) {
                Toast.makeText(this, obj.getString("Message"), Toast.LENGTH_LONG).show();
                return;
            }
            switch (obj.getString("Function"))
            {
                case "registerUser": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    stato = 0;
                    changeUI();
                    break;
                }
                case "loginUser": {
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    java.util.Date dt = new java.util.Date();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String finaldate = sdf.format(dt);
                    img = obj2.getString("Image");
                    byte[] data = Base64.decode(img,Base64.DEFAULT);
                    if(lat==null||lon==null){
                        loggato = new Autostoppista(obj2.getString("Name"),
                                obj2.getString("Surname"),
                                obj2.getString("Mobile"),
                                null,
                                Integer.parseInt(obj2.getString("Range")),
                                BitmapFactory.decodeByteArray(data,0,data.length),
                                null,
                                null,
                                null,
                                null,
                                dt);

                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER))
                            buildAlertMessageNoGps();
                        else
                            Toast.makeText(this,"Benvenuto "+loggato.getName(),Toast.LENGTH_SHORT).show();
                    }
                    else{
                        loggato = new Autostoppista(obj2.getString("Name"),
                                obj2.getString("Surname"),
                                obj2.getString("Mobile"),
                                null,
                                Integer.parseInt(obj2.getString("Range")),
                                BitmapFactory.decodeByteArray(data,0,data.length),
                                null,
                                null,
                                Double.parseDouble(lat),
                                Double.parseDouble(lon),
                                dt);
                        Toast.makeText(this,"Benvenuto "+loggato.getName(),Toast.LENGTH_SHORT).show();
                    }
                    if(stato == 0) {
                        if (((CheckBox) findViewById(R.id.lgnchkrestaloggato)).isChecked()&&!((EditText) findViewById(R.id.lgnetpassword)).getText().toString().isEmpty()){
                            SharedPreferences settings = getSharedPreferences("UserData", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Mobile", loggato.getMobile());
                            editor.putString("Password", md5(((EditText) findViewById(R.id.lgnetpassword)).getText().toString()));
                            editor.commit();
                        }
                    }
                    stato = 20;
                    changeUI();
                    funcPHP("setGPSLocation", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"date\":\"%s\"}",
                            loggato.getMobile(),lat,lon,finaldate));
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            java.util.Date dt = new java.util.Date();
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String finaldate = sdf.format(dt);
                            funcPHP("setGPSLocation", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"date\":\"%s\"}",
                                    loggato.getMobile(),lat,lon,finaldate));
                            if(stato == 51)
                                funcPHP("getActiveUsers", "{}");
                            if(stato == 44) {
                                funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}",
                                        loggato.getMobile(), lat, lon, range));
                            }
                            h.postDelayed(this,60000);
                        }
                    },60000);
                    break;
                }
                case "logoutUser": {
                    SharedPreferences settings = getSharedPreferences("UserData", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Mobile", null);
                    editor.putString("Password", null);
                    editor.commit();
                    stato = 0;
                    changeUI();
                    break;
                }
                case "User_City": {
                    stato = 31;
                    changeUI();
                    break;
                }
                case "User_Type": {
                    if(loggato.getType_id()==1) {
                        funcPHP("getCities","{}");
                    }
                    else{
                        stato = 40;
                        funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}",
                                loggato.getMobile(), lat, lon, range));
                    }
                    break;
                }
                case "getCities": {
                    cities.clear();
                    for(int x = 0; x< obj.getJSONArray("Message").length();x++){
                        cities.add(new City(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Province")));
                    }
                    stato = 30;
                    changeUI();
                    break;
                }
                case "getAS": {
                    switch (stato){
                        case 40:{
                            Autostoppisti.clear();
                            for(int x = 0; x< obj.getJSONArray("Message").length();x++){
                                byte[] data = Base64.decode(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Image"),Base64.DEFAULT);
                                Autostoppisti.add(new Autostoppista(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                        1,
                                        Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Range")),
                                        BitmapFactory.decodeByteArray(data,0,data.length),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Province"),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),
                                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Date"))));
                            }
                            changeUI();
                            break;
                        }
                        case 43:{
                            Autostoppisti.clear();
                            for(int x = 0; x< obj.getJSONArray("Message").length();x++){
                                byte[] data = Base64.decode(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Image"),Base64.DEFAULT);
                                Autostoppisti.add(new Autostoppista(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                        1,
                                        Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Range")),
                                        BitmapFactory.decodeByteArray(data,0,data.length),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Province"),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),
                                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Date"))));
                            }
                            changeUI();
                            stato = 44;
                            break;
                        }
                        case 44:{
                            Autostoppisti.clear();
                            for(int x = 0; x< obj.getJSONArray("Message").length();x++){
                                byte[] data = Base64.decode(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Image"),Base64.DEFAULT);
                                Autostoppisti.add(new Autostoppista(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                        1,
                                        Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Range")),
                                        BitmapFactory.decodeByteArray(data,0,data.length),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Province"),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),
                                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Date"))));
                                if(selected!=null&&selected.getMobile().equals(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"))){
                                    selected.setLatitude(Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")));
                                    selected.setLongitude(Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")));
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case "getActiveUsers": {
                    switch(stato){
                        case 50:{
                            ActiveUsers.clear();
                            for(int x = 0; x< obj.getJSONArray("Message").length();x++){
                                User u = new User(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                        Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Type_id")),
                                        Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Range")),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")),
                                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Date")),
                                        null);
                                byte[] data = Base64.decode(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Image"),Base64.DEFAULT);
                                u.setImage(BitmapFactory.decodeByteArray(data,0,data.length));
                                ActiveUsers.add(u);
                            }
                            changeUI();
                            stato = 51;
                            break;
                        }
                        case 51:{
                            ActiveUsers.clear();
                            for(int x = 0; x< obj.getJSONArray("Message").length();x++){
                                User u = new User(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                        new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                        Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Type_id")),
                                        Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Range")),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),
                                        Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")),
                                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Date")),
                                        null);
                                byte[] data = Base64.decode(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Image"),Base64.DEFAULT);
                                u.setImage(BitmapFactory.decodeByteArray(data,0,data.length));
                                ActiveUsers.add(u);
                            }
                            break;
                        }
                    }
                    break;
                }
                case "removeUser_City": {
                    loggato.setCity(null);
                    loggato.setProvince(null);
                    break;
                }
                case "removeUser_Type": {
                    loggato.setType_id(null);
                    break;
                }
                case "setImage": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_SHORT).show();
                    break;
                }
                case "forgotPassword":{
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_SHORT).show();
                    ((TextView) findViewById(R.id.lgntvforgot)).setOnClickListener(null);
                    break;
                }
                case "setPassword":{
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_SHORT).show();
                    ((EditText)findViewById(R.id.setetnewpassword)).setText("");
                    ((EditText)findViewById(R.id.setetoldpassword)).setText("");
                    ((EditText)findViewById(R.id.setetconfirm)).setText("");
                    break;
                }
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void onClickRegister(View v) {
        String Name = ((EditText)findViewById(R.id.regetname)).getText().toString();
        Name = Name.substring(0,1).toUpperCase() + Name.substring(1);
        String Surname = ((EditText)findViewById(R.id.regetsurname)).getText().toString();
        Surname = Surname.substring(0,1).toUpperCase() + Surname.substring(1);
        String Email = ((EditText)findViewById(R.id.regetemail)).getText().toString();
        String Mobile = ((EditText)findViewById(R.id.regetmobile)).getText().toString();
        String Password = ((EditText)findViewById(R.id.regetpassword)).getText().toString();
        String Confirm = ((EditText)findViewById(R.id.regetconfirm)).getText().toString();
        if(Name.isEmpty()||Surname.isEmpty()||Email.isEmpty()||Mobile.isEmpty()||Password.isEmpty()||Confirm.isEmpty()) {
            Toast.makeText(this,"Compilare tutti i campi",Toast.LENGTH_LONG).show();
            return;
        }
        if(!Password.equals(Confirm)) {
            Toast.makeText(this,"Password e conferma non coincidono", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isEmailValid(Email)){
            Toast.makeText(this,"Inserisci una e-mail valida", Toast.LENGTH_LONG).show();
            return;
        }
        String md5pwd = md5(Password);
        if(md5pwd.isEmpty()) {
            Toast.makeText(this,"Errore nel codificare la password",Toast.LENGTH_SHORT).show();
        }
        funcPHP("registerUser",String.format("{\"name\":\"%s\",\"surname\":\"%s\",\"email\":\"%s\",\"mobile\":\"%s\",\"password\":\"%s\"}",Name,Surname,Email,Mobile,md5pwd));
    }
    public void onClickLogin(View v) {
        assert ((EditText)findViewById(R.id.lgnetmobile)) != null;
        String Mobile = ((EditText)findViewById(R.id.lgnetmobile)).getText().toString();
        String Password = ((EditText)findViewById(R.id.lgnetpassword)).getText().toString();
        if(Mobile.isEmpty()|| Password.isEmpty()) {
            Toast.makeText(this,"Compilare tutti i campi",Toast.LENGTH_LONG).show();
            return;
        }
        String md5pwd = md5(Password);
        if(md5pwd.isEmpty()) {
            Toast.makeText(this,"Errore nel codificare la password",Toast.LENGTH_SHORT).show();
        }
        funcPHP("loginUser",String.format("{\"mobile\":\"%s\",\"password\":\"%s\"}",Mobile,md5pwd));
    }
    public void onClickSave(View v){
        switch (stato){
            case 21:{
                Integer Range = ((SeekBar) findViewById(R.id.setskbrange)).getProgress();
                range = Range;
                loggato.setRange(range);
                funcPHP("setRange",String.format("{\"mobile\":\"%s\",\"range\":\"%s\"}",loggato.getMobile(),range));
                String oldpassword = ((EditText)findViewById(R.id.setetoldpassword)).getText().toString();
                if(oldpassword.isEmpty()) {
                    Toast.makeText(this,"Range aggiornato",Toast.LENGTH_SHORT).show();
                    return;
                }
                String newpassword = ((EditText)findViewById(R.id.setetnewpassword)).getText().toString();
                String confirm = ((EditText)findViewById(R.id.setetconfirm)).getText().toString();
                if(!newpassword.equals(confirm)) {
                    Toast.makeText(this,"Password e conferma non coincidono",Toast.LENGTH_SHORT).show();
                    return;
                }
                funcPHP("setPassword", String.format("{\"mobile\":\"%s\",\"old\":\"%s\",\"new\":\"%s\"}",loggato.getMobile(),md5(oldpassword),md5(newpassword)));
                break;
            }
            case 1:{
                EditText et = (EditText)findViewById(R.id.setetip);
                if(!et.getText().toString().isEmpty()){
                    ipServer = et.getText().toString();
                    Toast.makeText(this,"Nuovo IP impostato",Toast.LENGTH_SHORT).show();
                    SharedPreferences settings = getSharedPreferences("UserData", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("IP", ipServer);
                    editor.commit();
                }
                break;
            }
        }
    }
    public void onClickForgot(View v){
        String Mobile = ((EditText)findViewById(R.id.lgnetmobile)).getText().toString();
        funcPHP("forgotPassword",String.format("{\"mobile\":\"%s\"}",Mobile));
    }
    public void onFragmentInteraction(Uri uri) {
        switch (stato){
            case 30:{
                final ListView listView = (ListView) findViewById(R.id.listView);
                assert listView != null;
                listView.setAdapter(citiesAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object listItem = listView.getItemAtPosition(position);
                        funcPHP("User_City",String.format("{\"mobile\":\"%s\",\"city\":\"%s\"}",loggato.getMobile(),((City)listItem).getName()));
                    }
                });
                break;
            }
            case 40:{
                final ListView listView = (ListView) findViewById(R.id.listView2);
                assert listView != null;
                listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,new ArrayList<String>()));
                listView.setAdapter(autostoppistiAdapter);
                registerForContextMenu(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object listItem = listView.getItemAtPosition(position);
                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ ((Autostoppista)listItem).getMobile()));
                        try{startActivity(i);}
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(),"Errore nella chiamata",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
            case 20:{
                ImageView ivprofile = (ImageView) findViewById(R.id.prfivprofileimage);
                Bitmap bm = ((BitmapDrawable)ivprofile.getDrawable()).getBitmap();
                bm = Bitmap.createScaledBitmap(bm, 256, 256, false);
                //bm = decreaseColorDepth(bm,32);
                img = getStringImage(bm);
                loggato.setImage(bm);
                if(img == "")
                    return;
                funcPHP("setImage",String.format("{\"mobile\":\"%s\",\"img\":\"%s\"}",loggato.getMobile(),img));
                break;
            }
        }
    }
    public static Bitmap decreaseColorDepth(Bitmap src, int bitOffset) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        int A, R, G, B;
        int pixel;
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
                if(R < 0) { R = 0; }
                G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
                if(G < 0) { G = 0; }
                B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
                if(B < 0) { B = 0; }
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }
    public String getStringImage(Bitmap bmp){
        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            image_str = image_str.replace("\n","");
            return image_str;
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            return "";
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Opzioni");
        menu.add(0, v.getId(), 0, "Visualizza profilo");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Visualizza su mappa");
        menu.add(0, v.getId(), 0, "Chiama");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getTitle().equals("Visualizza profilo")){
            for(User a : Autostoppisti){
                String m = a.getMobile();
                String t= Autostoppisti.get(info.position).getMobile();
                if(m.equals(t)) {
                    MainActivity.selected = a;
                    break;
                }
            }
            MainActivity.stato = 42;
            changeUI();
        }
        else if(item.getTitle().equals("Chiama")){
            Autostoppista listItem = Autostoppisti.get(info.position);
            Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ listItem.getMobile()));
            try{startActivity(i);}
            catch(Exception e){
                Toast.makeText(this,"Errore nella chiamata",Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if(item.getTitle().equals("Visualizza su mappa")) {
            selected = Autostoppisti.get(info.position);
            stato = 43;
            funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}", loggato.getMobile(), lat, lon, range));
        }else{
            return false;
        }
        return true;
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Il tuo GPS è disattivato, vuoi attivarlo?")
                .setCancelable(false)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void funcPHP(String function,String json){
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        asyncTask.execute(ipServer,function,json);
    }
    public String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(s.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(16);
            while(hashtext.length() < 32 ){
                hashtext = "0"+hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {}
        return "";
    }
    /*
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }*/
    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[a-zA-Z0-9\\.-]+\\@[a-zA-Z0-9-]+\\.[a-z]{2,4}$";
        // ^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            if(stato>=20) {
                prec = stato;
                stato = 21;
                changeUI();
            }
            else {
                prec = stato;
                stato = 1;
                changeUI();
            }
        }
        if (super.onOptionsItemSelected(item)) return true;
        return false;
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loggato!=null&&!loggato.getMobile().isEmpty())
            funcPHP("logoutUser",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
        mGoogleApiClient.disconnect();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000);

        try{LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);}
        catch (SecurityException e){return;}


        try{mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);}
        catch (SecurityException e){return;}
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        if(stato > 20) {
            loggato.setLatitude(Double.parseDouble(lat));
            loggato.setLongitude(Double.parseDouble(lon));
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}