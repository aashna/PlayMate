package com.example.playmate;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class OneTimeRegister extends Activity {
		
	private String URL_LOGIN_INFO = "http://aashna.webatu.com/login.php";
	private static final String TAG = "MyError";
	String encodedString;
	ProgressDialog prgDialog;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;


	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);  
        
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
 
        addListenerOnCheck();
        addListenerOnGo();   
        addListenerOnUpload();
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
	                ImageView imgView = (ImageView) findViewById(R.id.upload);
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

	        client.post("http://aashna.webatu.com/upload_image.php",params, new AsyncHttpResponseHandler() 
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
			
	 public void addListenerOnCheck() {			
					 
		 CheckBox chk= (CheckBox) findViewById(R.id.checkBox1);
				 
			chk.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {
		                //is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					EditText name= (EditText)findViewById(R.id.editText1);
					String check_name = name.getText().toString();
					EditText phone= (EditText)findViewById(R.id.editText2);
					String num=phone.getText().toString();
					
					if (check_name.matches("")) {
					    Toast.makeText(OneTimeRegister.this, "Please enter your name", Toast.LENGTH_SHORT).show();
					    CheckBox chk1=(CheckBox) findViewById(R.id.checkBox1);
					    chk1.toggle();
					    return;
					}
					
					if(num.matches("")|num.length()<10){						
						 Toast.makeText(OneTimeRegister.this, "Please enter valid phone no.", Toast.LENGTH_SHORT).show();
						 CheckBox chk2=(CheckBox) findViewById(R.id.checkBox1);
						 chk2.toggle();   
						 return;
					}
					
					else
					{
					 ImageView iv = (ImageView) findViewById(R.id.imageview1);	
					 iv.setVisibility(View.VISIBLE);
					 Button button = (Button) findViewById(R.id.button1);
		             button.setEnabled(true);
					}
				}
				else
				{
					Toast.makeText(OneTimeRegister.this,
						 	   "Please accept the terms and conditions", Toast.LENGTH_LONG).show();
				}		 
			  }
			}); 
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
	 
	 public void addListenerOnUpload()
	 {
		 Button button=(Button)findViewById(R.id.button2);
		 
		 button.setOnClickListener(new OnClickListener()
	        {
	          public void onClick(View v)
	          {
	        	  loadImagefromGallery(v);
	          }	          
	        });	  
	 }
	 
	 public void addListenerOnGo()
	 {
		 Button button = (Button) findViewById(R.id.button1);

	        button.setOnClickListener(new OnClickListener()
	        {
	          public void onClick(View v)
	          {
	        	  EditText name= (EditText)findViewById(R.id.editText1);
				  String check_name = name.getText().toString();
				  EditText phone= (EditText)findViewById(R.id.editText2);
				  String num=phone.getText().toString();
				  
				  uploadImage(v);	  
				              
	        	//  new AddNewPrediction().execute(check_name,num); 
	        	  
	        	  Intent i = new Intent(OneTimeRegister.this, Location.class);
	        	  i.putExtra("username", check_name);
	        	  i.putExtra("phone", num);
	        	  i.putExtra("image", fileName);
	              startActivity(i);
	              finish();
	          }
	        });		 
	 }	
	 /*
	 private class AddNewPrediction extends AsyncTask<String, Void, Void> {

	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	        }
	 
	 @Override
     protected Void doInBackground(String... arg) {
         // TODO Auto-generated method stub
         String username = arg[0];
         String phone = arg[1];
     
         Log.e("Username=",""+username);
         Log.e("Phone Number=", ""+phone);
        

         // Preparing post params
         List<NameValuePair> params = new ArrayList<NameValuePair>();
         params.add(new BasicNameValuePair("UserName", username));
         params.add(new BasicNameValuePair("PhoneNumber", phone));
         params.add(new BasicNameValuePair("ImageName", fileName));

         ServiceHandler serviceClient = new ServiceHandler();

         String json = serviceClient.makeServiceCall(URL_LOGIN_INFO,
                 ServiceHandler.POST, params);

         Log.d("Create Prediction Request: ", "> " + json);

         if (json != null) {
             try {
                 JSONObject jsonObj = new JSONObject(json);
                 boolean error = jsonObj.getBoolean("error");
                 // checking for error node in json
                 
                 if (!error) {
                     // new category created successfully
                     Log.e("Prediction added successfully ",
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
 }*/
}
