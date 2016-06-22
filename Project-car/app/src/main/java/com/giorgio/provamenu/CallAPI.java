package com.giorgio.provamenu;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CallAPI extends AsyncTask<String, String, String> {

    public AsyncResponse delegate = null;

    public CallAPI() {
        //set context variables if required
    }

    @Override
    protected String doInBackground(String... params) {
        /*String urlString = params[0]; // URL to call
        String resultToDisplay = "";
        InputStream in = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        try {
            resultToDisplay = IOUtils.toString(in, "UTF-8");
            //to [convert][1] byte stream to a string
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultToDisplay;*/
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(params[0]);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("api_method", "loginUser"));
        nameValuePair.add(new BasicNameValuePair("api_data", "{\"mobile\":\"444\",\"password\":\"ppp\"}"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e)
        {
            return e.getMessage();
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            // Log exception
            return e.getMessage();
        }
    }


    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}