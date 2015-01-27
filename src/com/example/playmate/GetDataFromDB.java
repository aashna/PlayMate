package com.example.playmate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class GetDataFromDB {
	
	public String getDataFromDB(String orig_lat,String orig_long) {
		
		  InputStream is = null;
		  String result=null;
        try {
        //	Log.e("Entered GetDataFromDB",""+"");   
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("orig_lat", orig_lat));
            params.add(new BasicNameValuePair("orig_long", orig_long));
            
            Log.e("GetDataFromDBError","> " + "coordinates received");
 
            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://aashna.webatu.com/GetFromDB.php");
            httppost.setEntity(new UrlEncodedFormEntity(params));

            //ResponseHandler<String> responseHandler = new BasicResponseHandler();
            //final String response = httpclient.execute(httppost,responseHandler);
            
            HttpResponse response = httpclient.execute(httppost); 
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            Log.e("log_tag", "connection success ");
            
           // Log.d("Response="+response, "");
             
            //return response.trim();
            
 
        } catch(Exception e)
        {
            Log.e("log_tag", "Error in http connection "+e.toString());
           // Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
            return "error";

    }
      //convert response to string
        try
        {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) 
                {
                        sb.append(line + "\n");
                }
                is.close();

                result=sb.toString();
                return result;
        }
        catch(Exception e)
        {
               Log.e("log_tag", "Error converting result "+e.toString());
               return "error";

        }
    }

}
