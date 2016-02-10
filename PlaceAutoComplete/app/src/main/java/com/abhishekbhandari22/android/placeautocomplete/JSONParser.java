package com.abhishekbhandari22.android.placeautocomplete;

import android.location.Location;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
/**
 * Created by Abhishek on 2/8/2016.
 */
public class JSONParser {
    static InputStream is=null;
    static JSONObject jObj=null;
    static String json="";
    //constructor
    public JSONParser(){

    }

    public String getJsonFromURL(String url){
       //Making HTTP request
        try{
            //defaultHTTPClient
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse respone= httpclient.execute(request);
            is=respone.getEntity().getContent();
            BufferedReader br= new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            String str ="";
            String line=null;
            while ((line=br.readLine())!=null){
                str+=line+"\n";
            }
            json=str;
        }
        catch (IOException i){
            Log.e("getJsonFromURL",i.getMessage());
        }
        Log.d("getJsonFromURL",json);
        return json;
    }


}
