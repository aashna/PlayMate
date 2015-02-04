package com.example.playmate;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class New_Post extends Activity implements OnClickListener{
	
	int post_type=0; String data="";
	Boolean flag=false;
	private String URL_NEWPOST = "http://aashna.webatu.com/write_post.php";
	private static final String TAG = "New_Post-Error";
	String encodedString;
	ProgressDialog prgDialog;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post_dialog);
        
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);

        addListeners();
	}
	
	private void addListeners() {
		// TODO Auto-generated method stub
		ImageButton info=(ImageButton)findViewById(R.id.info);
		ImageButton question=(ImageButton)findViewById(R.id.question);
		ImageButton ad=(ImageButton)findViewById(R.id.ad);
		ImageButton meetup=(ImageButton)findViewById(R.id.meetup);
		ImageButton donation=(ImageButton)findViewById(R.id.donation);
		Button pic=(Button)findViewById(R.id.pic);
		Button ok_button=(Button)findViewById(R.id.ok);
		Button cancel_button=(Button)findViewById(R.id.cancel);
		
		info.setOnClickListener(this);
		question.setOnClickListener(this);
		ad.setOnClickListener(this);
		meetup.setOnClickListener(this);
		donation.setOnClickListener(this);
		pic.setOnClickListener(this);
		ok_button.setOnClickListener(this);
		cancel_button.setOnClickListener(this);
	}
	
	 public void loadImagefromGallery(View view) {
	        // Create intent to Open Image applications like Gallery, Google Photos
	        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	        // Start the Intent
	        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
	    }
	 
	    // When Image is selected from Gallery
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        try {
	            // When an Image is picked
	            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
	                    && null != data) {
	                // Get the Image from data
	 
	                Uri selectedImage = data.getData();
	                String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	                // Get the cursor
	                Cursor cursor = getContentResolver().query(selectedImage,
	                        filePathColumn, null, null, null);
	                // Move to first row
	                cursor.moveToFirst();
	 
	                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	                imgPath = cursor.getString(columnIndex);
	                cursor.close();
	                ImageView imgView = (ImageView) findViewById(R.id.imageView1);
	                // Set the Image in ImageView
	                imgView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
	                // Get the Image's file name
	                String fileNameSegments[] = imgPath.split("/");
	                fileName = fileNameSegments[fileNameSegments.length - 1];
	                // Put file name in Async Http Post Param which will used in Php web app
	                params.put("filename", fileName);	 
	            } else {
	                Toast.makeText(this, "You haven't picked Image",
	                        Toast.LENGTH_LONG).show();
	            }
	        } catch (Exception e) {
	            Toast.makeText(this, "Oops! Something went wrong!", Toast.LENGTH_LONG)
	                    .show();
	        }	 
	    }
	    
	    @SuppressLint("NewApi") public void uploadImage(View v) {
	        // When Image is selected from Gallery
	        if (imgPath != null && !imgPath.isEmpty()) {
	            prgDialog.setMessage("Converting Image to Binary Data");
	            prgDialog.show();
	            // Convert image to String using Base64
	            encodeImagetoString();
	        // When Image is not selected from Gallery
	        } else {
	            Toast.makeText(
	                    getApplicationContext(),
	                    "You must select image from gallery before you try to upload",
	                    Toast.LENGTH_LONG).show();
	        }
	    }
	    
	    // AsyncTask - To convert Image to String
	    public void encodeImagetoString() {
	        new AsyncTask<Void, Void, String>() {	 
	            protected void onPreExecute() {	 
	            };
	 
	            @Override
	            protected String doInBackground(Void... params) {
	                BitmapFactory.Options options = null;
	                options = new BitmapFactory.Options();
	                options.inSampleSize = 3;
	                bitmap = BitmapFactory.decodeFile(imgPath,
	                        options);
	                ByteArrayOutputStream stream = new ByteArrayOutputStream();
	                // Must compress the Image to reduce image size to make upload easy
	                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream); 
	                byte[] byte_arr = stream.toByteArray();
	                // Encode Image to String
	                encodedString = Base64.encodeToString(byte_arr, 0);
	                return "";
	            }
	 
	            @Override
	            protected void onPostExecute(String msg) {
	                prgDialog.setMessage("Calling Upload");
	                // Put converted Image string into Async Http Post param
	                params.put("image", encodedString);
	                // Trigger Image upload
	                triggerImageUpload();
	            }
	        }.execute(null, null, null);
	    }
	    
	    public void triggerImageUpload() {
	        makeHTTPCall();
	    }
	 
	    // Make Http call to upload Image to Php server
	    public void makeHTTPCall() {
	        prgDialog.setMessage("Invoking Php");        
	        AsyncHttpClient client = new AsyncHttpClient();

	        client.post("http://aashna.webatu.com/upload_postimage.php",params, new AsyncHttpResponseHandler() 
	        {
	                    // When the response returned by REST has Http
	                    // response code '200'
	                    public void onSuccess(String response) {
	                        // Hide Progress Dialog
	                        prgDialog.hide();
	                        Toast.makeText(getApplicationContext(), response,
	                                Toast.LENGTH_LONG).show();
	                    }
	 
	                    // When the response returned by REST has Http
	                    // response code other than '200' such as '404',
	                    // '500' or '403' etc
	                    public void onFailure(int statusCode, Throwable error,String content) {
	                        // Hide Progress Dialog
	                        prgDialog.hide();
	                        // When Http response code is '404'
	                        if (statusCode == 404) {Toast.makeText(getApplicationContext(),"Requested resource not found",
	                                    Toast.LENGTH_LONG).show();
	                        }
	                        // When Http response code is '500'
	                        else if (statusCode == 500) {
	                            Toast.makeText(getApplicationContext(),"Something went wrong at server end",
	                                    Toast.LENGTH_LONG).show();
	                        }
	                        // When Http response code other than 404, 500
	                        else {
	                            Toast.makeText(
	                                    getApplicationContext(),
	                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
	                                            + statusCode, Toast.LENGTH_LONG).show();
	                        }
	                    }

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
						}
	                });
	    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	
		switch(v.getId()) {
        case R.id.info:
            post_type=1;
            
            break;   
        case R.id.question:
            post_type=2;
            break;
        case R.id.ad:
            post_type=3;
            break;
        case R.id.meetup:
            post_type=4;
            break;
        case R.id.donation:
            post_type=5;
            break;
        case R.id.pic:
        {
        	loadImagefromGallery(v);
            break; 
        }
        case R.id.ok:
	        {
	       	 EditText enter_post=(EditText)findViewById(R.id.enter_post);
	       	 String post=enter_post.getText().toString();
	       	 
	       	 new AddPost().execute(post,String.valueOf(post_type));
	       	 
	       	Intent i = new Intent(New_Post.this, MainDb.class);
            startActivity(i);
            finish();
	        }
        case R.id.cancel:
        	finish();
		}	
	}	
	
	@Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }
	private class AddPost extends AsyncTask<String,Void,Void> {

        private static final String TAG = "NewPostError";

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
 
 @Override
 protected Void doInBackground(String... arg) {
     // TODO Auto-generated method stub   
	 
	 String new_post=String.valueOf(arg[0]);
	 String postType=String.valueOf(arg[1]);
	 String user_name="";
	 String user_id="";

	 SharedPreferences applicationpreferences = PreferenceManager.getDefaultSharedPreferences(New_Post.this);
	 String phone=applicationpreferences.getString("phone_number", "");
	 Log.e("New_Post:Phone=",""+phone);

	 try{
		 GetUserFromDB getdb = new GetUserFromDB();
		 data = getdb.getUserFromDB(phone);
		 
		 JSONArray ja=new JSONArray(data);
		 for (int i = 0; i < ja.length(); i++) {
		 JSONObject json_data = ja.getJSONObject(i);
		 user_id=json_data.getString("Id");
		 user_name=json_data.getString("UserName");
		 }
	}
	catch(Exception ex){
	ex.printStackTrace();
	}
     
     TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
     Calendar c = Calendar.getInstance(tz);
     String time =String.format("%02d" , c.get(Calendar.DAY_OF_MONTH))+"/"+ 
    		    String.format("%02d" , (c.get(Calendar.MONTH)+1))+"/"+
                  String.format("%02d" , c.get(Calendar.YEAR))+"-"+
    		 String.format("%02d" , c.get(Calendar.HOUR_OF_DAY))+":"+
                 String.format("%02d" , c.get(Calendar.MINUTE))+":"+
                      String.format("%02d" , c.get(Calendar.SECOND));

     // Preparing post params
     List<NameValuePair> params = new ArrayList<NameValuePair>();
     params.add(new BasicNameValuePair("user_id", user_id));
     params.add(new BasicNameValuePair("user_name", user_name));
     params.add(new BasicNameValuePair("post", new_post));
     params.add(new BasicNameValuePair("post_type", postType));
     params.add(new BasicNameValuePair("img_name", fileName));
     params.add(new BasicNameValuePair("time", time));

     ServiceHandler serviceClient = new ServiceHandler();

     String json = serviceClient.makeServiceCall(URL_NEWPOST,ServiceHandler.POST, params);

     Log.d("Create Location Request: ", "> " + json);

     if (json != null) {
         try {
             JSONObject jsonObj = new JSONObject(json);
             boolean error = jsonObj.getBoolean("error");
             // checking for error node in json
             
             if (!error) {
                 
                 Log.e("Post added successfully ","> " + jsonObj.getString("message"));
                 
             } else {
                 Log.e(TAG,"> " + jsonObj.getString("message"));
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
