package com.easytravel.app;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.easytravel.app.MainActivity.PERMISSION_TELEPHONE_NUMBER;
import static com.easytravel.app.MainActivity.img;
import static com.easytravel.app.MainActivity.ipServer;
import static com.easytravel.app.MainActivity.lat;
import static com.easytravel.app.MainActivity.loggato;
import static com.easytravel.app.MainActivity.lon;
import static com.easytravel.app.MainActivity.range;
import static com.easytravel.app.MainActivity.stato;

public class LoginActivity extends AppCompatActivity implements AsyncResponse,
        IPSettings_Fragment.OnFragmentInteractionListener,
        Register_Fragment.OnFragmentInteractionListener{
    RelativeLayout rl;
    boolean doubleBackToExitPressedOnce = false;
    public Handler h = new Handler();
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stato = 0;
        changeUI();
        SharedPreferences settings = getSharedPreferences("UserData", 0);
        String m = settings.getString("Mobile", null);
        String p = settings.getString("Password", null);
        ipServer = settings.getString("IP", null);
        if(m!=null||p!=null){
            funcPHP("loginUser",String.format("{\"mobile\":\"%s\",\"password\":\"%s\"}",m,p));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TELEPHONE_NUMBER: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String mPhoneNumber;
                    try {
                        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        mPhoneNumber = tMgr.getLine1Number();
                    }
                    catch(Exception e) {
                        mPhoneNumber = "";
                    }
                    if(!mPhoneNumber.isEmpty()) {
                        TextView tvmobile = (TextView) findViewById(R.id.lgnetmobile);
                        tvmobile.setText(mPhoneNumber);
                        tvmobile.setEnabled(false);
                    }
                }
                return;
            }
        }
    }
    public void funcPHP(String function,String json){
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        asyncTask.execute(ipServer,function,json);
    }
    public void onClickLogin(View v) {
        assert findViewById(R.id.lgnetmobile) != null;
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeUI(){
        switch (stato) {
            case 0:{
                setContentView(R.layout.activity_login);
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorLoginPrimary));
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MainActivity.PERMISSION_TELEPHONE_NUMBER);
                }
                else {
                    String mPhoneNumber;
                    try {
                        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        mPhoneNumber = tMgr.getLine1Number();
                    } catch (Exception e) {
                        mPhoneNumber = "";
                    }
                    if (!mPhoneNumber.isEmpty()) {
                        TextView tvmobile = (TextView) findViewById(R.id.lgnetmobile);
                        tvmobile.setText(mPhoneNumber);
                        tvmobile.setEnabled(false);
                    }
                }
                rl = (RelativeLayout) findViewById(R.id.RelativeLayout2);
                break;
            }
            case 1:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout2, new IPSettings_Fragment())
                        .commit();
                break;
            }
            case 10:{
                rl.removeAllViews();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.RelativeLayout2, new Register_Fragment())
                        .commit();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                break;
            }
        }
    }
    @Override
    public void onBackPressed() {
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
                stato= 0;
                changeUI();
                break;
            }
            case 10:{
                stato= 0;
                changeUI();
                break;
            }
            default: break;
        }
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
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(stato>=20) {
                                java.util.Date dt = new java.util.Date();
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String finaldate = sdf.format(dt);
                                funcPHP("setGPSLocation", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"date\":\"%s\"}",
                                        loggato.getMobile(), lat, lon, finaldate));
                                if (stato == 51)
                                    funcPHP("getActiveUsers", "{}");
                                if (stato == 44) {
                                    funcPHP("getAS", String.format("{\"mobile\":\"%s\",\"lat\":\"%s\",\"lon\":\"%s\",\"range\":\"%s\"}",
                                            loggato.getMobile(), lat, lon, range));
                                }
                                h.postDelayed(this, 60000);
                            }else{
                                h.removeCallbacks(this);
                            }
                        }
                    },60000);
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    finish();
                    break;
                }
                case "forgotPassword":{
                    Toast.makeText(this,obj.getString("Message"),Toast.LENGTH_SHORT).show();
                    ((TextView) findViewById(R.id.lgntvforgot)).setOnClickListener(null);
                    break;
                }
            }
        }
        catch (Exception e){}
    }
    public void onClickCreateAcc(View view) {
        stato = 10;
        changeUI();
    }
    public void onClickAdvSettings(View view) {
        stato = 1;
        changeUI();
    }
    public void onClickForgot(View v){
        String Mobile = ((EditText)findViewById(R.id.lgnetmobile)).getText().toString();
        funcPHP("forgotPassword",String.format("{\"mobile\":\"%s\"}",Mobile));
    }
    public void onClickSetIP(View view) {
        EditText et = (EditText)findViewById(R.id.setipetip);
        if(!et.getText().toString().isEmpty()){
            ipServer = et.getText().toString();
            Toast.makeText(this,"Nuovo IP impostato",Toast.LENGTH_SHORT).show();
            SharedPreferences settings = getSharedPreferences("UserData", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("IP", ipServer);
            editor.commit();
            stato = 0;
            changeUI();
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
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
