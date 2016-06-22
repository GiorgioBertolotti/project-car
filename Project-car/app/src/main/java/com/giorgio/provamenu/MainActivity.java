package com.giorgio.provamenu;

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
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Register_Fragment.OnFragmentInteractionListener, Login_Fragment.OnFragmentInteractionListener, AsyncResponse {
    RelativeLayout rl;
    User loggato;
    NavigationView navigationView;
    ArrayList<City> cities;
    ArrayList<User> ActiveUsers;
    int stato = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    public void changeUI(int stato){
        switch (stato){
            case 0:{
                navigationView.getMenu().findItem(R.id.logout).setVisible(false);
                navigationView.getMenu().findItem(R.id.login).setVisible(true);
                navigationView.getMenu().findItem(R.id.register).setVisible(true);
                break;
            }
            case 20:{
                navigationView.getMenu().findItem(R.id.logout).setVisible(true);
                navigationView.getMenu().findItem(R.id.login).setVisible(false);
                navigationView.getMenu().findItem(R.id.register).setVisible(false);
                break;
            }

        }
    }

    public void onFragmentInteraction(Uri uri) {
        Toast toast = Toast.makeText(this, "Wheeee!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rl = (RelativeLayout) findViewById(R.id.RelativeLayout);
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        cities = new ArrayList<City>();
        ActiveUsers = new ArrayList<User>();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.register: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Register_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Register");
                stato = 10;
                break;
            }
            case R.id.login: {
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout, new Login_Fragment())
                        .commit();
                getSupportActionBar().setTitle("Login");
                stato = 15;
                break;
            }
            case R.id.autista:
                break;
            case R.id.autostoppista:
                break;
            case R.id.mappa:
                break;
            case R.id.logout: {
                CallAPI asyncTask = new CallAPI();
                asyncTask.delegate = this;
                String json = String.format("{\"mobile\":\"%s\"}",loggato.getMobile());
                asyncTask.execute("http://192.168.147.40/pcws/index.php","logoutUser",json);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickRegister(View v) {
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
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
        String json = String.format("{\"name\":\"%s\",\"surname\":\"%s\",\"mobile\":\"%s\",\"password\":\"%s\"}",Name,Surname,Mobile,Password);
        asyncTask.execute("http://192.168.147.40/pcws/index.php","registerUser",json);
    }

    public void onClickLogin(View v) {
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        String Mobile = ((EditText)findViewById(R.id.etmobile2)).getText().toString();
        String Password = ((EditText)findViewById(R.id.etpassword2)).getText().toString();
        if(Mobile.isEmpty()|| Password.isEmpty()) {
            Toast.makeText(this,"Compilare tutti i campi",Toast.LENGTH_LONG).show();
            return;
        }
        String json = String.format("{\"mobile\":\"%s\",\"password\":\"%s\"}",Mobile,Password);
        asyncTask.execute("http://192.168.147.40/pcws/index.php","loginUser",json);
    }

    @Override
    public void processFinish(String output){
        try {
            JSONObject obj = new JSONObject(output);
            switch (obj.getString("Function"))
            {
                case "registerUser": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    break;
                }
                case "loginUser": {
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    loggato = new User(obj2.getString("Name"),obj2.getString("Surname"),obj2.getString("Mobile"));
                    ((TextView) findViewById(R.id.textView)).setText(String.format("Benvenuto %s %s, %s",loggato.getName(), loggato.getSurname(), loggato.getMobile()));
                    Toast.makeText(this,"Benvenuto "+loggato.getName(),Toast.LENGTH_LONG).show();
                    stato = 20;
                    changeUI(stato);
                    break;
                }
                case "logoutUser": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    stato = 0;
                    changeUI(stato);
                    break;
                }
                case "User_City": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    break;
                }
                case "User_Type": {
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_LONG).show();
                    break;
                }
                case "getCities": {
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    for (int x = 0; x< obj.getJSONArray("Message").length();x++){
                        cities.add(new City(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Province")));
                    }
                    break;
                }
                case "getAS": {
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    for (int x = 0; x< obj.getJSONArray("Message").length();x++){
                        ActiveUsers.add(new User(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Type_id"))));
                    }
                    break;
                }
                case "getActiveUsers": {
                    JSONObject obj2 = new JSONObject(obj.getJSONArray("Message").getString(0));
                    for (int x = 0; x< obj.getJSONArray("Message").length();x++){
                        ActiveUsers.add(new User(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Name"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Surname"),new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Mobile"),Integer.parseInt(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Type_id")),Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude")),Double.parseDouble(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Latitude")), Date.valueOf(new JSONObject(obj.getJSONArray("Message").getString(x)).getString("Longitude"))));
                    }
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
