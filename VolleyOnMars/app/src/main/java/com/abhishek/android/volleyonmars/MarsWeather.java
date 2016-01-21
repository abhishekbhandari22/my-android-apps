package com.abhishek.android.volleyonmars;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Abhishek on 12/1/2015.
 */
public class MarsWeather extends Application {
    //Tag to identify all the requests that our application makes
    public static final String TAG = MarsWeather.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static MarsWeather mInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance=this;
        mRequestQueue= Volley.newRequestQueue(getApplicationContext());

    }//end of onCreate

    //method provides instance of MarsWeather
    public static synchronized MarsWeather getmInstance(){
        return mInstance;
    }
    //provide the volley request queue
    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }
    /**
     * Adds the request to the general queue.
     * @param req The object Request
     * @param <T> The type of the request result.
     */
    public <T> void addRequest(Request<T> req){
        req.setTag(TAG);
        mRequestQueue.add(req);
    }
    /**
     * Cancels all the pending requests.
     */
    public void cancelRequest(){
        mRequestQueue.cancelAll(TAG);
    }
}
