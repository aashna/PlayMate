package com.example.playmate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Location extends Activity
{    	
	GoogleMap map; 
	double lat=0,longit=0;
	private static final LatLng DELHI = new LatLng(28.798709, 77.200380);

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.map);
        
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        if (map == null) {
            Toast.makeText(this, "Google Maps not available", 
                Toast.LENGTH_LONG).show();
                    }  
        
        // Getting reference to btn_find of the layout activity_main
        Button btn_find = (Button) findViewById(R.id.btn_find);
        
        addListenerOnGo();   
 
        // Defining button click event listener for the find button
        OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location
                EditText etLocation = (EditText) findViewById(R.id.et_location);
 
                // Getting user input location
                String location = etLocation.getText().toString();
 
                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        };
        // Setting button click event listener for the find button
        btn_find.setOnClickListener(findClickListener);               
}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
    }
    
    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
 
        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
 
            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }
 
        @Override
        protected void onPostExecute(List<Address> addresses) {
 
            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }
 
            // Clears all the existing markers on the map
            map.clear();
 
            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){
 
                Address address = (Address) addresses.get(i);
 
                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng= new LatLng(address.getLatitude(), address.getLongitude());
                
                lat=address.getLatitude();
                longit=address.getLongitude();
                
                String.valueOf(lat);String.valueOf(longit);
                
                if(String.valueOf(lat).matches(""))
                { Toast.makeText(Location.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
                return;}
                
                if(String.valueOf(longit).matches(""))
                {
                	Toast.makeText(Location.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                else
                {                
                Button button = (Button) findViewById(R.id.go_button);
	             button.setEnabled(true);
                }
                
 
                String addressText = String.format("%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                address.getCountryName());
                
                MarkerOptions markerOptions;
 
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
 
                map.addMarker(markerOptions);
                
                CameraPosition myPosition = new CameraPosition.Builder()
                .target(latLng).zoom(117).bearing(90).tilt(30).build();
        map.animateCamera(
            CameraUpdateFactory.newCameraPosition(myPosition));
 
                // Locate the first location
                if(i==0)
                   map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }
    private void addListenerOnGo() {
		// TODO Auto-generated method stub
        Button button = (Button) findViewById(R.id.go_button);
		button.setOnClickListener(new OnClickListener()
	        {
	          public void onClick(View v)
	          {				  	 				              
	        	  new AddLocation().execute(String.valueOf(lat),String.valueOf(longit)); 
	        	  
	        	  Intent i = new Intent(Location.this, MainDb.class);
	        	  i.putExtra("latitude", Double.toString(lat));
	        	  i.putExtra("longitude",Double.toString(longit));
	              startActivity(i);
	              finish();
	          }
	        });
    }
    private class AddLocation extends AsyncTask<String,Void,Void> {

        private static final String TAG = "LocationError";

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
 
 @Override
 protected Void doInBackground(String... arg) {
     // TODO Auto-generated method stub   
	 
	 String latitude=String.valueOf(arg[0]);
	 String longitude=String.valueOf(arg[1]);
	 
	 Intent reg_intent=getIntent();
	 String username=reg_intent.getStringExtra("username");
	 String phone=reg_intent.getStringExtra("phone");
	 String file_name=reg_intent.getStringExtra("image");
	 
	 Log.e("Username=",""+username);
     Log.e("Phone Number=", ""+phone);
     Log.e("Coordinates=",""+latitude);
     Log.e(",",""+longitude);
	 

     // Preparing post params
     List<NameValuePair> params = new ArrayList<NameValuePair>();
     params.add(new BasicNameValuePair("Latitude", latitude));
     params.add(new BasicNameValuePair("Longitude", longitude));
     params.add(new BasicNameValuePair("UserName", username));
     params.add(new BasicNameValuePair("PhoneNumber", phone));
     params.add(new BasicNameValuePair("ImageName", file_name));

     ServiceHandler serviceClient = new ServiceHandler();

     String json = serviceClient.makeServiceCall("http://aashna.webatu.com/login.php",
             ServiceHandler.POST, params);

     Log.d("Create Location Request: ", "> " + json);

     if (json != null) {
         try {
             JSONObject jsonObj = new JSONObject(json);
             boolean error = jsonObj.getBoolean("error");
             // checking for error node in json
             
             if (!error) {
                 
                 Log.e("Location added successfully ",
                         "> " + jsonObj.getString("message"));
                 
             } else {
                 Log.e(TAG,
                         "> " + jsonObj.getString("message"));
             }

         } catch (JSONException e) {
             e.printStackTrace();
         }

     } else {
         Log.e(TAG, "JSON data error!");
     }
     return null;
 }

 protected void onPostExecute(Void result) {
     super.onPostExecute(result);
 }
}
    }
