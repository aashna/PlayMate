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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class GetUserFromDB {
	
	public String getUserFromDB(String phone) {
		
		  InputStream is = null;
		  String result=null;
      try {
      	
      	List<NameValuePair> params = new ArrayList<NameValuePair>();
          params.add(new BasicNameValuePair("phone", phone));

          
          Log.e("GetUserFromDBError","> " + "post received");
          Log.e("PhoneNumber","> " + phone);

          HttpPost httppost;
          HttpClient httpclient;
          httpclient = new DefaultHttpClient();
          httppost = new HttpPost("http://aashna.webatu.com/GetUserIDFromDb.php");

          
          HttpResponse response = httpclient.execute(httppost); 
          HttpEntity entity = response.getEntity();
          is = entity.getContent();

          Log.e("log_tag", "connection success ");
          
          Log.e("Response="+response, "");
           
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
              
    //          Log.e("Result=",""+result);
              return result;
      }
      catch(Exception e)
      {
             Log.e("log_tag", "Error converting result "+e.toString());
             return "error";

      }
  }


}
