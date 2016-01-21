package com.abhishek.android.volleyonmars;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

public class WeatherActivity extends AppCompatActivity {

    ImageView mImageView;
    TextView mTxtDegrees, mTxtWeather, mTxtError;
    MarsWeather marsWeather;
    int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int mainColor = Color.parseColor("#FF5722");
    SharedPreferences mPreferences;

    final static String
        Flicker_API_Key="0d0e71ecf7eb3ec7482d47adb4b16075",
        Images_API_EndPoint="https://api.flickr.com/services/rest/?format=json&nojsoncallback=1&sort=random&method=flickr.photos.search&" +
                "tags=mars,planet,rover&tag_mode=all&api_key=",
        RECENT_API_ENDPOINT = "http://marsweather.ingenology.com/v1/latest/",

        SHARED_PREFS_IMG_KEY = "img",
        SHARED_PREFS_DAY_KEY = "day";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        marsWeather = MarsWeather.getmInstance();
        if (marsWeather==null){
            Log.e("marsWeather","null error");
        }
        // Views setup
        mImageView = (ImageView) findViewById(R.id.main_bg);
        mTxtDegrees = (TextView) findViewById(R.id.degrees);
        mTxtWeather = (TextView) findViewById(R.id.weather);
        mTxtError = (TextView) findViewById(R.id.error);

        // Font
        mTxtDegrees.setTypeface(Typeface.DEFAULT,1);
        mTxtWeather.setTypeface(Typeface.DEFAULT,1);

        //SharedPreference
        mPreferences=getPreferences(Context.MODE_PRIVATE);

        //picture
        if(mPreferences.getInt(SHARED_PREFS_DAY_KEY,0)!=today){
            try {
                searchRandomImage();
            }catch (Exception e){
                imageError(e);
            }
        }else {
            //we already have the picture of the day let's load it
            loadImage(mPreferences.getString(SHARED_PREFS_IMG_KEY,""));
        }

        loadWeatherData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
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

    /**
     * Fetches a random picture of Mars, using Flickr APIs, and then displays it.
     * @throws Exception When a working API key is not provided.
     */
    private void searchRandomImage() throws Exception{
        if(Flicker_API_Key.equals(""))
            throw new Exception("You didn't provide any API key");
        CustomJSONRequest request = new CustomJSONRequest(Request.Method.GET,Images_API_EndPoint+Flicker_API_Key,null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            JSONArray images = response.getJSONObject("photos").getJSONArray("photo");
                            int index = new Random().nextInt(images.length());
                            JSONObject imageItem = images.getJSONObject(index);

                            String imageUrl=  "http://farm" + imageItem.getString("farm") +
                                    ".static.flickr.com/" + imageItem.getString("server") + "/" +
                                    imageItem.getString("id") + "_" + imageItem.getString("secret") + "_" + "c.jpg";
                            //store pic of the day
                            SharedPreferences.Editor editor = mPreferences.edit();
                            editor.putInt(SHARED_PREFS_DAY_KEY,today);
                            editor.putString(SHARED_PREFS_IMG_KEY, imageUrl);
                            editor.commit();

                            loadImage(imageUrl);
                        }catch (Exception e){
                            imageError(e);
                        }
                    }
                },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                        imageError(error);
                }

        });
        request.setmPriority(Request.Priority.LOW);
        marsWeather.addRequest(request);
    }

    private void imageError(Exception e) {
        mImageView.setBackgroundColor(mainColor);
        e.printStackTrace();
    }

    private void txtError(Exception e) {
        mTxtError.setVisibility(View.VISIBLE);
        e.printStackTrace();
    }
    /**
     * Downloads and displays the picture using Volley.
     * @param imageUrl the URL of the picture.
     */
    private void loadImage(String imageUrl){
        //Retrieves the image in the url and displays it on the UI
        ImageRequest imageRequest = new ImageRequest(imageUrl,new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap bitmap){
                mImageView.setImageBitmap(bitmap);
            }
        },0,0, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                imageError(error);
            }
        });
        marsWeather.addRequest(imageRequest);
    }
    /**
     * Fetches and displays the weather data of Mars.
     */
    private void loadWeatherData(){
        CustomJSONRequest request = new CustomJSONRequest(Request.Method.GET, RECENT_API_ENDPOINT,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String minTemp,maxTemp,atmo;
                    int avgTemp;

                    response=response.getJSONObject("report");
                    minTemp=response.getString("min_temp"); minTemp=minTemp.substring(0, minTemp.indexOf("."));
                    maxTemp=response.getString("max_temp"); maxTemp=maxTemp.substring(0, maxTemp.indexOf("."));
                    avgTemp=(Integer.parseInt(minTemp)+Integer.parseInt(maxTemp))/2;
                    atmo=response.getString("atmo_opacity");
                    Log.d("minTemp",minTemp);
                    Log.d("maxTemp",maxTemp);
                    mTxtDegrees.setText(avgTemp+ " \u2103");
                    mTxtWeather.setText(atmo);
                }catch (Exception e){

                    txtError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                txtError(volleyError);
            }
        }
        );
        request.setmPriority(Request.Priority.HIGH);
        marsWeather.addRequest(request);
    }
}//end of activity
