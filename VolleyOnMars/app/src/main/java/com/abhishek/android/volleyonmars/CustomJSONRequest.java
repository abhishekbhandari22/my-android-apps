package com.abhishek.android.volleyonmars;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


/**
 * Created by Abhishek on 12/2/2015.
 */
public class CustomJSONRequest extends JsonObjectRequest {
    public CustomJSONRequest(int method,String url, JSONObject jsonRequest,
                             Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        super(method,url,jsonRequest,listener,errorListener);
    }
    private Priority mPriority;

    public void setmPriority(Priority priority){
        mPriority=priority;
    }
    @Override
    public Priority getPriority(){
        return (mPriority == null) ? Priority.NORMAL : mPriority;
    }
}
