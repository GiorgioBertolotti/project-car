package com.easytravel.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Register_Fragment.OnFragmentInteractionListener,
        Profile_Fragment.OnFragmentInteractionListener,
        Destination_Fragment.OnFragmentInteractionListener,
        Autostoppisti_Fragment.OnFragmentInteractionListener,
        Wait_Fragment.OnFragmentInteractionListener,
        Map_Fragment.OnFragmentInteractionListener,
        Settings_Fragment.OnFragmentInteractionListener,
        Notifications_Fragment.OnFragmentInteractionListener,
        AsyncResponse,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    RelativeLayout rl;
    NavigationView navigationView;
    public static Autostoppista loggato, selectedOnMap;
    public static User selected;
    public static ArrayList<User> ActiveUsers;
    public static ArrayAdapter<User> usersAdapter;
    public static ArrayList<Autostoppista> Autostoppisti;
    public static ArrayAdapter<Autostoppista> autostoppistiAdapter;
    public static ArrayList<com.easytravel.app.Notification> notifications;
    public static CustomAdapterNotifications adapter;
    public static String ipServer = "http://bertolotti.ddns.net/pcws/index.php";
    public static String lat, lon, img;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    public static int range, stato = 0, prec, idNotifica = 0, notificationsVisibility= 0;
    public static final int MY_PERMISSION_REQUEST_READ_FINE_LOCATION = 61626, PERMISSION_TELEPHONE_NUMBER = 61627;
    public static boolean isFirstMapOpen = true, isFirstMapOpen2 = true, doubleBackToExitPressedOnce = false;
    public static Context context;
    public static ActionBarDrawerToggle mDrawerToggle;
    public static AppCompatActivity activity;
    public void changeUI() {
        switch (stato) {
            case 20: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Profile_Fragment())
                        .addToBackStack(null)
                        .commit();
                getSupportActionBar().setTitle("EasyTravel");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("EasyTravel");
                funcPHP("getRating", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
                break;
            }
            case 21: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Settings_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Impostazioni");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Impostazioni");
                break;
            }
            case 22: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Notifications_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Notifiche");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Notifiche");
                break;
            }
            case 30: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Destination_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Scelta destinazione");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Scelta destinazione");
                break;
            }
            case 31: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Wait_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Attesa");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Attesa");
                break;
            }
            case 40: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Autostoppisti_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Scelta autostoppista");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Scelta autostoppista");
                break;
            }
            case 42: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Profile_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Profilo utente");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Profilo utente");
                break;
            }
            case 43: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Map_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Mappa");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Mappa");
                break;
            }
            case 50: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Map_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Mappa");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Mappa");
                break;
            }
            case 52: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Profile_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Profilo utente");
                ((TextView) findViewById(R.id.tlbtxttitle)).setText("Profilo utente");
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.logout).setVisible(true);
        navigationView.getMenu().findItem(R.id.autostoppista).setEnabled(true);
        navigationView.getMenu().findItem(R.id.autista).setEnabled(true);
        navigationView.getMenu().findItem(R.id.mappa).setEnabled(true);
        rl = (RelativeLayout) findViewById(R.id.RelativeLayout);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_CUSTOM);
        changeUI();
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            buildAlertMessageNoGps();
        ActiveUsers = new ArrayList<>();
        usersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ActiveUsers);
        Autostoppisti = new ArrayList<>();
        autostoppistiAdapter = new CustomAdapterAutostoppisti(Autostoppisti, getApplicationContext());
        notifications = new ArrayList<>();
        adapter = new CustomAdapterNotifications(notifications, getApplicationContext());
        range = 10;
        context = this;
        (findViewById(R.id.notification)).setVisibility(View.INVISIBLE);
        funcPHP("getUnseenContactsCount", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
        findViewById(R.id.notification).setZ(1000);
        final Handler notifications = new Handler();
        notifications.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (stato >= 20) {
                    funcPHP("checkContacts", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
                    notifications.postDelayed(this, 10000);
                } else {
                    notifications.removeCallbacks(this);
                }
            }
        }, 10000);
        activity = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (stato == 42 || stato == 45 || stato == 52 || stato == 31 || stato == 21 || stato == 43 || stato == 44 || stato == 22) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                mDrawerToggle.setToolbarNavigationClickListener(null);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                findViewById(R.id.tlbbtnsettings).setVisibility(View.VISIBLE);
                findViewById(R.id.notification).setVisibility(MainActivity.notificationsVisibility);
                findViewById(R.id.tlbbtnnotifications).setVisibility(View.VISIBLE);
                mDrawerToggle.syncState();
            }
            switch (stato) {
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
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                    break;
                }
                case 1: {
                    stato = prec;
                    changeUI();
                    break;
                }
                case 10: {
                    stato = 0;
                    changeUI();
                    break;
                }
                case 20: {
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
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                    break;
                }
                case 21: {
                    stato = prec;
                    changeUI();
                    break;
                }
                case 22: {
                    stato = prec;
                    changeUI();
                    break;
                }
                case 30: {
                    stato = 20;
                    changeUI();
                    break;
                }
                case 31: {
                    loggato.setType_id(null);
                    funcPHP("removeUser_Type", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
                    stato = 30;
                    changeUI();
                    break;
                }
                case 40: {
                    funcPHP("removeUser_Type", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
                    stato = 20;
                    changeUI();
                    break;
                }
                case 42: {
                    stato = 40;
                    changeUI();
                    break;
                }
                case 43: {
                    stato = 40;
                    changeUI();
                    break;
                }
                case 44: {
                    stato = 40;
                    changeUI();
                    break;
                }
                case 45: {
                    stato = 43;
                    changeUI();
                    break;
                }
                case 50: {
                    stato = 20;
                    changeUI();
                    break;
                }
                case 51: {
                    stato = 20;
                    changeUI();
                    break;
                }
                case 52: {
                    stato = 50;
                    changeUI();
                    break;
                }
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.home: {
                funcPHP("removeUser_Type", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
                stato = 20;
                changeUI();
                break;
            }
            case R.id.autostoppista: {
                stato = 30;
                changeUI();
                break;
            }
            case R.id.autista: {
                loggato.setType_id(2);
                if (stato == 31) {
                    loggato.setDestlon(null);
                    loggato.setDestlat(null);
                }
                funcPHP("User_Type", String.format("{\"mobile\":\"%s\",\"type\":\"autista\"}", loggato.getMobile()));
                break;
            }
            case R.id.mappa: {
                if (stato == 31) {
                    loggato.setDestlon(null);
                    loggato.setDestlat(null);
                }
                stato = 50;
                funcPHP("removeUser_Type", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
                funcPHP("getActiveUsers", "{}");
                break;
            }
            case R.id.logout: {
                SharedPreferences settings = getSharedPreferences("UserData", 0);
                String token = settings.getString("Token", null);
                funcPHP("logoutUser", String.format("{\"mobile\":\"%s\",\"token\":\"%s\"}", loggato.getMobile(), token));
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONObject obj = new JSONObject(output);
            if (obj.getBoolean("IsError")) {
                if (obj.getString("Message").equals("Errore nella query") || (stato == 20 && obj.getString("Message").equals("Nessun autostoppista")))
                    return;
                Toast.makeText(this, obj.getString("Message"), Toast.LENGTH_LONG).show();
                return;
            }
            switch (obj.getString("Function")) {
                case "logoutUser": {
                    SharedPreferences settings = getSharedPreferences("UserData", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Token", null);
                    editor.commit();
                    stato = 0;
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                }
                case "User_Destination": {
                    stato = 31;
                    changeUI();
                    break;
                }
                case "User_Type": {
                    if (loggato.getType_id() == 1) {
                        stato = 31;
                        changeUI();
                    } else {
                        stato = 40;
                        funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}",
                                loggato.getMobile(), lat, lon, range));
                    }
                    break;
                }
                case "getAS": {
                    //Autostoppisti.clear();
                    ArrayList<Autostoppista> tmp = new ArrayList<>();
                    for (int x = 0; x < obj.getJSONArray("Message").length(); x++) {
                        byte[] data = Base64.decode((new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Image")).replace("data:image/jpeg;base64,", ""), Base64.DEFAULT);
                        tmp.add(new Autostoppista(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mail"),
                                1,
                                Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Range")),
                                BitmapFactory.decodeByteArray(data, 0, data.length),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Destlat")),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Destlon")),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),
                                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Date")),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Rating"))));
                    }
                    for (Autostoppista a : tmp){
                        boolean found = false;
                        for (Autostoppista b : Autostoppisti){
                            if(!found) {
                                if (a.getMobile().equals(b.getMobile())) {
                                    found = true;
                                    if (!a.getPosition().equals(b.getPosition())) {
                                        b.setLatitude(a.getLatitude());
                                        b.setLongitude(a.getLongitude());
                                        b.setPosition(a.getLatitude(), a.getLongitude());
                                    }
                                    if (!a.getDest().equals(b.getDest())) {
                                        b.setDestlat(a.getDestlat());
                                        b.setDestlon(a.getDestlon());
                                        b.setDestination(a.getDestlat(), a.getDestlon());
                                    }
                                    b.setDate(a.getDate());
                                    b.setRating(a.getRating());
                                    b.setRange(a.getRange());
                                }
                            }
                        }
                        if(!found){
                            Autostoppisti.add(a);
                        }
                    }
                    for (Autostoppista a : Autostoppisti){
                        boolean found = false;
                        for (Autostoppista b : tmp) {
                            if(a.getMobile().equals(b.getMobile()))
                                found = true;
                        }
                        if(!found)
                            Autostoppisti.remove(a);
                    }
                    switch (stato) {
                        case 40: {
                            changeUI();
                            break;
                        }
                        case 43: {
                            changeUI();
                            stato = 44;
                            break;
                        }
                    }
                    break;
                }
                case "getActiveUsers": {
                    ActiveUsers.clear();
                    for (int x = 0; x < obj.getJSONArray("Message").length(); x++) {
                        byte[] data = Base64.decode((new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Image")).replace("data:image/jpeg;base64,", ""), Base64.DEFAULT);
                        User u = new User(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mail"),
                                Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Type_id")),
                                Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Range")),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")),
                                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Date")),
                                BitmapFactory.decodeByteArray(data, 0, data.length),
                                Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Rating")));
                        ActiveUsers.add(u);
                    }
                    if (stato == 50) {
                        changeUI();
                        stato = 51;
                        break;
                    }
                    break;
                }
                case "removeUser_Type": {
                    loggato.setType_id(null);
                    break;
                }
                case "setImage": {
                    Toast.makeText(this, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                    break;
                }
                case "forgotPassword": {
                    Toast.makeText(this, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                    ((TextView) findViewById(R.id.lgntvforgot)).setOnClickListener(null);
                    break;
                }
                case "setPassword": {
                    Toast.makeText(this, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.setetnewpassword)).setText("");
                    ((EditText) findViewById(R.id.setetoldpassword)).setText("");
                    ((EditText) findViewById(R.id.setetconfirm)).setText("");
                    break;
                }
                case "getRating": {
                    ((RatingBar) findViewById(R.id.prfrbrating)).setRating(Float.parseFloat(obj.getString("Message")));
                    break;
                }
                case "getUnseenContactsCount":
                case "checkContacts": {
                    if (!obj.getString("Message").equals("0")) {
                        if (stato != 42 && stato != 45 && stato != 52 && stato != 31 && stato != 21 && stato != 43 && stato != 44 && stato != 22) {
                            (findViewById(R.id.notification)).setVisibility(View.VISIBLE);
                        }
                        if (obj.getString("Function").equals("checkContacts"))
                            notifica();
                    }
                    break;
                }
                case "getContacts": {
                    notifications.clear();
                    for (int x = 0; x < obj.getJSONArray("Message").length(); x++) {
                        notifications.add(new com.easytravel.app.Notification(
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),
                                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Datetime")),
                                new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Type")));
                    }
                    if(notifications.size()>0) {
                        if (stato == 22)
                            return;
                        prec = stato;
                        stato = 22;
                        changeUI();
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public void onClickSave(View v) {
        Integer Range = ((SeekBar) findViewById(R.id.setskbrange)).getProgress();
        range = Range;
        loggato.setRange(range);
        funcPHP("setRange", String.format("{\"mobile\":\"%s\",\"range\":\"%s\"}", loggato.getMobile(), range));
        String oldpassword = ((EditText) findViewById(R.id.setetoldpassword)).getText().toString();
        if (oldpassword.isEmpty()) {
            Toast.makeText(this, "Range aggiornato", Toast.LENGTH_SHORT).show();
            return;
        }
        String newpassword = ((EditText) findViewById(R.id.setetnewpassword)).getText().toString();
        String confirm = ((EditText) findViewById(R.id.setetconfirm)).getText().toString();
        if (!newpassword.equals(confirm)) {
            Toast.makeText(this, "Password e conferma non coincidono", Toast.LENGTH_SHORT).show();
            return;
        }
        funcPHP("setPassword", String.format("{\"mobile\":\"%s\",\"old\":\"%s\",\"new\":\"%s\"}", loggato.getMobile(), md5(oldpassword), md5(newpassword)));
    }

    public void onClickForgot(View v) {
        String Mobile = ((EditText) findViewById(R.id.lgnetmobile)).getText().toString();
        funcPHP("forgotPassword", String.format("{\"mobile\":\"%s\"}", Mobile));
    }

    public void onClickSettings(View v) {
        if (stato == 21)
            return;
        prec = stato;
        stato = 21;
        changeUI();
    }

    public void onClickNotifications(View v) {
        funcPHP("getContacts", String.format("{\"mobile\":\"%s\"}", loggato.getMobile()));
        (findViewById(R.id.notification)).setVisibility(View.INVISIBLE);
    }

    public void onClickDeleteAll(View v){
        for(com.easytravel.app.Notification noti : notifications){
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String finaldate = sdf.format(noti.getDatetime());
            funcPHP("deleteContact", String.format("{\"caller\":\"%s\",\"receiver\":\"%s\",\"datetime\":\"%s\"}", noti.getMobile(),loggato.getMobile(), finaldate));
        }
        Snackbar.make(v, "Eliminati tutti i contatti", Snackbar.LENGTH_LONG)
                .setAction("No action", null).show();
        onBackPressed();
    }

    public void onFragmentInteraction(Uri uri) {
        switch (stato) {
            case 30: {
                break;
            }
            case 40: {
                final ListView listView = (ListView) findViewById(R.id.listAutostoppisti);
                listView.setAdapter(autostoppistiAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        com.easytravel.app.Notification notification = notifications.get(position);
                    }
                });
                registerForContextMenu(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        activity.openContextMenu(view);
                    }
                });
//                final ListView listView = (ListView) findViewById(R.id.listUsers);
//                assert listView != null;
//                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
//                listView.setAdapter(autostoppistiAdapter);
//                registerForContextMenu(listView);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Object listItem = listView.getItemAtPosition(position);
//                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ((Autostoppista) listItem).getMobile()));
//                        try {
//                            startActivity(i);
//                        } catch (Exception e) {
//                            Toast.makeText(getApplicationContext(), "Errore nella chiamata", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
                break;
            }
            case 20: {
                ImageView ivprofile = (ImageView) findViewById(R.id.prfivprofileimage);
                Bitmap bm = ((BitmapDrawable) ivprofile.getDrawable()).getBitmap();
                bm = Bitmap.createScaledBitmap(bm, 256, 256, false);
                img = getStringImage(bm);
                loggato.setImage(bm);
                if (img == "")
                    return;
                funcPHP("setImage", String.format("{\"mobile\":\"%s\",\"img\":\"%s\"}", loggato.getMobile(), img));
                break;
            }
            case 22: {
                final ListView listView = (ListView) findViewById(R.id.listNotifications);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        com.easytravel.app.Notification notification = notifications.get(position);
                    }
                });
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            image_str = image_str.replace("\n", "");
            return image_str;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return "";
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Opzioni");
        menu.add(0, v.getId(), 0, "Visualizza profilo");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Visualizza su mappa");
        menu.add(0, v.getId(), 0, "Chiama");
        menu.add(0, v.getId(), 0, "E-mail");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle().equals("Visualizza profilo")) {
            MainActivity.selected = Autostoppisti.get(info.position);
            MainActivity.stato = 42;
            changeUI();
            funcPHP("selectAutostoppista", String.format("{\"caller\":\"%s\",\"receiver\":\"%s\"}", loggato.getMobile(), MainActivity.selected.getMobile()));
        } else if (item.getTitle().equals("Visualizza su mappa")) {
            selectedOnMap = Autostoppisti.get(info.position);
            stato = 43;
            funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}", loggato.getMobile(), lat, lon, range));
            funcPHP("selectAutostoppista", String.format("{\"caller\":\"%s\",\"receiver\":\"%s\"}", loggato.getMobile(), selectedOnMap.getMobile()));
        } else if (item.getTitle().equals("Chiama")) {
            Autostoppista listItem = Autostoppisti.get(info.position);
            Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listItem.getMobile()));
            try {
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "Errore nella chiamata", Toast.LENGTH_SHORT).show();
                return false;
            }
            funcPHP("addContact", String.format("{\"caller\":\"%s\",\"receiver\":\"%s\",\"type\":\"Call\"}", loggato.getMobile(), listItem.getMobile()));
        } else if (item.getTitle().equals("E-mail")) {
            Autostoppista listItem = Autostoppisti.get(info.position);
            Uri uri = Uri.parse("mailto:" + listItem.getMail() + "?subject=" + URLEncoder.encode("EasyTravel: ti serve un passaggio?"));
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(uri);
            startActivity(Intent.createChooser(intent, "Send Email"));
            funcPHP("addContact", String.format("{\"caller\":\"%s\",\"receiver\":\"%s\",\"type\":\"Mail\"}", loggato.getMobile(), listItem.getMobile()));
        } else {
            return false;
        }
        return true;
    }

    public void funcPHP(String function, String json) {
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        asyncTask.execute(ipServer, function, json);
    }

    public String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(s.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
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

    public void notifica() {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        Intent intent = new Intent(this, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icontoolbar)
                        .setContentTitle("EasyTravel")
                        .setLargeIcon(largeIcon)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentText("Sei stato contattato!");
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(idNotifica, mBuilder.build());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loggato != null && !loggato.getMobile().isEmpty() && !(stato == 0)) {
            SharedPreferences settings = getSharedPreferences("UserData", 0);
            String token = settings.getString("Token", null);
            funcPHP("logoutUser", String.format("{\"mobile\":\"%s\",\"token\":\"%s\"}", loggato.getMobile(), token));
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_READ_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationRequest mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(60000);
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    } catch (SecurityException e) {
                        String a = e.getMessage();
                        return;
                    }
                    try {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    } catch (SecurityException e) {
                        return;
                    }
                    if (mLastLocation != null) {
                        lat = String.valueOf(mLastLocation.getLatitude());
                        lon = String.valueOf(mLastLocation.getLongitude());
                        if (stato >= 20) {
                            java.util.Date dt = new java.util.Date();
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String finaldate = sdf.format(dt);
                            funcPHP("setGPSLocation", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"date\":\"%s\"}",
                                    loggato.getMobile(), lat, lon, finaldate));
                            funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}",
                                    loggato.getMobile(), lat, lon, range));
                        }
                    }
                } else {

                    return;
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_READ_FINE_LOCATION);
        } else {

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(60000);
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException e) {
                Log.e("Error", e.getMessage());
                return;
            }
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e) {
                return;
            }
            if (mLastLocation != null) {
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());
                if (stato >= 20) {
                    java.util.Date dt = new java.util.Date();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String finaldate = sdf.format(dt);
                    funcPHP("setGPSLocation", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"date\":\"%s\"}",
                            loggato.getMobile(), lat, lon, finaldate));
                    funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}",
                            loggato.getMobile(), lat, lon, range));
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        if (stato > 20) {
            loggato.setLatitude(Double.parseDouble(lat));
            loggato.setLongitude(Double.parseDouble(lon));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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
