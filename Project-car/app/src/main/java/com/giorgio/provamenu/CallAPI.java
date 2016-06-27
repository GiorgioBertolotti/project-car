package com.giorgio.provamenu;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CallAPI extends AsyncTask<String, String, String> {
    public AsyncResponse delegate = null;
    public CallAPI() {}
    @Override
    protected String doInBackground(String... params) {
        return execRequest(params);
    }
    private String execRequest(String[] params){
        String result= "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(params[0]);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("api_method", params[1]));
        nameValuePair.add(new BasicNameValuePair("api_data", params[2]));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {}
        try {
            HttpResponse response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}