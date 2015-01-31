package com.example.playmate;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.loopj.android.http.RequestParams;

public class New_Post extends Activity implements OnClickListener{
	
	int post_type=0; String data="";
	private String URL_NEWPOST = "http://aashna.webatu.com/user_post.php";
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

        addListeners();
	}
	
	private void addListeners() {
		// TODO Auto-generated method stub
		ImageButton info=(ImageButton)findViewById(R.id.info);
		ImageButton question=(ImageButton)findViewById(R.id.question);
		ImageButton ad=(ImageButton)findViewById(R.id.ad);
		ImageButton meetup=(ImageButton)findViewById(R.id.meetup);
		ImageButton donation=(ImageButton)findViewById(R.id.donation);
		ImageButton pic=(ImageButton)findViewById(R.id.pic);
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
            post_type=6;
            break; 
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
	 
//	 Intent newpost_intent=getIntent();
	// String username=newpost_intent.getStringExtra("username");
	// String phone=newpost_intent.getStringExtra("phone");
	 
	 DataHolder d=(DataHolder)getApplicationContext();
	 String phone=d.getData();
	 
	// String phone=((OneTimeRegister)getApplicationContext()).num;
//	 TelephonyManager phoneManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
	//		String phone = phoneManager.getLine1Number();
	 
	 Log.e("New_Post:Phone=",""+phone);
	 
	 try{
		 GetUserFromDB getdb = new GetUserFromDB();
		 data = getdb.getUserFromDB(phone);
		 System.out.println(data);
		 
		 JSONArray ja=new JSONArray(data);
		 JSONObject json_data = ja.getJSONObject(0);
		 user_id=json_data.getString("Id");
		 user_name=json_data.getString("UserName");
	}
	catch(Exception ex){
	System.out.println("NEWPost Exception"+ex);
	ex.printStackTrace();
	}
 
	 Log.e("Username=",""+user_name);
     Log.e("New Post=", ""+new_post);
     Log.e("PostType=",""+postType); 

     // Preparing post params
     List<NameValuePair> params = new ArrayList<NameValuePair>();
     params.add(new BasicNameValuePair("user_id", user_id));
     params.add(new BasicNameValuePair("user_name", user_name));
     params.add(new BasicNameValuePair("post", new_post));
     params.add(new BasicNameValuePair("post_type", postType));
     params.add(new BasicNameValuePair("img_name", ""));

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
