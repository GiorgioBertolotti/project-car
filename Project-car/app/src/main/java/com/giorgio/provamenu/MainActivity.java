package com.giorgio.provamenu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Register_Fragment.OnFragmentInteractionListener, Login_Fragment.OnFragmentInteractionListener, Profile_Fragment.OnFragmentInteractionListener, City_Fragment.OnFragmentInteractionListener, User_Fragment.OnFragmentInteractionListener, Wait_Fragment.OnFragmentInteractionListener, AsyncResponse {
    RelativeLayout rl;
    public static Autostoppista loggato;
    NavigationView navigationView;
    ArrayList<City> cities;
    public static ArrayAdapter<City> citiesAdapter;
    ArrayList<User> ActiveUsers;
    public static ArrayAdapter<User> usersAdapter;
    ArrayList<Autostoppista> Autostoppisti;
    public static ArrayAdapter<Autostoppista> autostoppistiAdapter;
    int stato = 0;
    private GoogleApiClient client2;
    public void changeUI(){
        switch (stato){
            case 0:{
                hideKeyboard();
                setContentView(R.layout.activity_main);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();
                getSupportActionBar().setTitle("Passaggi");
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);
                navigationView.getMenu().findItem(R.id.logout).setVisible(false);
                navigationView.getMenu().findItem(R.id.login).setVisible(true);
                navigationView.getMenu().findItem(R.id.register).setVisible(true);
                navigationView.getMenu().findItem(R.id.autostoppista).setEnabled(false);
                navigationView.getMenu().findItem(R.id.autista).setEnabled(false);
                navigationView.getMenu().findItem(R.id.mappa).setEnabled(false);
                rl = (RelativeLayout) findViewById(R.id.RelativeLayout);
                break;
            }
            case 10:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Register_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Register");
                break;
            }
            case 15:{
                hideKeyboard();
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Login_Fragment())
                        .addToBackStack(null)
                        .commit();
                getSupportActionBar().setTitle("Login");
                break;
            }
            case 20:{
                hideKeyboard();
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
            case 40:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new User_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Scegli un autostoppista");
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
            case 60:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Wait_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Attesa");
                break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeUI();
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        cities = new ArrayList<City>();
        citiesAdapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, cities);
        ActiveUsers = new ArrayList<User>();
        usersAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, ActiveUsers);
        Autostoppisti = new ArrayList<Autostoppista>();
        autostoppistiAdapter = new ArrayAdapter<Autostoppista>(this, android.R.layout.simple_list_item_1, Autostoppisti);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            switch (stato){
                case 0: {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                    break;
                }
                case 10:{
                    stato= 0;
                    changeUI();
                    break;
                }
                case 15:{
                    stato= 0;
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
                case 40:{
                    loggato.setType_id(null);
                    funcPHP("removeUser_Type",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                    stato = 20;
                    changeUI();
                    break;
                }
                case 50:{
                    stato = 20;
                    changeUI();
                    break;
                }
                case 60:{
                    loggato.setCity(null);
                    loggato.setProvince(null);
                    funcPHP("removeUser_City",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                    stato = 30;
                    changeUI();
                    break;
                }
                case 80:{
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
                stato = 15;
                changeUI();
                break;
            }
            case R.id.autista: {
                loggato.setType_id(2);
                funcPHP("User_Type",String.format("{\"mobile\":\"%s\",\"type\":\"autista\"}",loggato.getMobile()));
                break;
            }
            case R.id.autostoppista: {
                loggato.setType_id(1);
                funcPHP("User_Type",String.format("{\"mobile\":\"%s\",\"type\":\"autostoppista\"}",loggato.getMobile()));
                break;
            }
            case R.id.mappa: {
                funcPHP("getActiveUsers","{}");
                break;
            }
            case R.id.logout: {
                funcPHP("logoutUser",String.format("{\"mobile\":\"%s\"}",loggato.getMobile()));
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void processFinish(String output){
        try {
            JSONObject obj = new JSONObject(output);
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
                    loggato = new Autostoppista(obj2.getString("Name"),obj2.getString("Surname"),obj2.getString("Mobile"));
                    Toast.makeText(this,"Benvenuto "+loggato.getName(),Toast.LENGTH_LONG).show();
                    ((TextView) findViewById(R.id.textView)).setText(String.format("Benvenuto %s %s",loggato.getName(), loggato.getSurname()));
                    stato = 20;
                    changeUI();
                    break;
                }
                case "logoutUser": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    stato = 0;
                    changeUI();
                    break;
                }
                case "User_City": {
                    stato = 60;
                    changeUI();
                    break;
                }
                case "User_Type": {
                    if(loggato.getType_id()==1) {
                        funcPHP("getCities","{}");
                    }
                    else{
                        funcPHP("getAS","{}");
                    }
                    break;
                }
                case "getCities": {
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    for (int x = 0; x< obj.getJSONArray("Message").length();x++){
                        cities.add(new City(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Province")));
                    }
                    stato = 30;
                    changeUI();
                    break;
                }
                case "getAS": {
                    if(obj.getBoolean("IsError")==true) {
                        Toast.makeText(this, obj.getString("Message"), Toast.LENGTH_LONG).show();
                        break;
                    }
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    for (int x = 0; x< obj.getJSONArray("Message").length();x++){
                        Autostoppisti.add(new Autostoppista(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Name"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("City_Province")));
                    }
                    stato = 40;
                    changeUI();
                    break;
                }
                case "getActiveUsers": {
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    for (int x = 0; x< obj.getJSONArray("Message").length();x++){
                        ActiveUsers.add(new User(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Type_id")),Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")), Date.valueOf(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude"))));
                    }
                    stato = 50;
                    changeUI();
                    break;
                }
                case "removeUser_City": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    break;
                }
                case "removeUser_Type": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void onClickRegister(View v) {
        String Name = ((EditText)findViewById(R.id.etname)).getText().toString();
        String Surname = ((EditText)findViewById(R.id.etsurname)).getText().toString();
        String Mobile = ((EditText)findViewById(R.id.etmobile)).getText().toString();
        String Password = ((EditText)findViewById(R.id.etpassword)).getText().toString();
        String Confirm = ((EditText)findViewById(R.id.etconfirm)).getText().toString();
        if(Name.isEmpty()||Surname.isEmpty()|| Mobile.isEmpty()|| Password.isEmpty()|| Confirm.isEmpty()) {
            Toast.makeText(this,"Compilare tutti i campi",Toast.LENGTH_LONG).show();
            return;
        }
        if(!Password.equals(Confirm)) {
            Toast.makeText(this,"Password e conferma non coincidono", Toast.LENGTH_LONG).show();
            return;
        }
        funcPHP("registerUser",String.format("{\"name\":\"%s\",\"surname\":\"%s\",\"mobile\":\"%s\",\"password\":\"%s\"}",Name,Surname,Mobile,Password));
    }
    public void onClickLogin(View v) {
        String Mobile = ((EditText)findViewById(R.id.etmobile2)).getText().toString();
        String Password = ((EditText)findViewById(R.id.etpassword2)).getText().toString();
        if(Mobile.isEmpty()|| Password.isEmpty()) {
            Toast.makeText(this,"Compilare tutti i campi",Toast.LENGTH_LONG).show();
            return;
        }
        funcPHP("loginUser",String.format("{\"mobile\":\"%s\",\"password\":\"%s\"}",Mobile,Password));
    }
    public void onFragmentInteraction(Uri uri) {
        switch (loggato.getType_id()){
            case 1:{
                final ListView listView = (ListView) findViewById(R.id.listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object listItem = listView.getItemAtPosition(position);
                        funcPHP("User_City",String.format("{\"mobile\":\"%s\",\"city\":\"%s\"}",loggato.getMobile(),((City)listItem).getName()));
                    }
                });
                break;
            }
            case 2:{
                final ListView listView = (ListView) findViewById(R.id.listView2);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object listItem = listView.getItemAtPosition(position);
                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ ((Autostoppista)listItem).getMobile()));
                        startActivity(i);
                    }
                });
                break;
            }
        }
    }
    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void funcPHP(String function,String json){
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        asyncTask.execute("http://192.168.147.40/pcws/index.php",function,json);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.giorgio.provamenu/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.giorgio.provamenu/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }
}
