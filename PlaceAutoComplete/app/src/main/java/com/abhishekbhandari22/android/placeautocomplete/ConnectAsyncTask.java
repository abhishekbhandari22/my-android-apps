package com.abhishekbhandari22.android.placeautocomplete;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Abhishek on 2/11/2016.
 */
public class ConnectAsyncTask extends AsyncTask<Void,Void,String> {
    public AsyncResponse delegate=null;
    private ProgressDialog progressDialog;
    Context context;
    String url;
    public ConnectAsyncTask(Context context,String url,AsyncResponse delegate){
        this.url=url;
        this.context=context;
        this.delegate=delegate;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Fetching route please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }
    @Override
    protected String doInBackground(Void... params){
        JSONParser jsonParser = new JSONParser();
        return jsonParser.getJsonFromURL(url);

    }
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        progressDialog.hide();
        delegate.processFinish(result);

    }
    public interface AsyncResponse{
        void processFinish(String result);
    }
}
