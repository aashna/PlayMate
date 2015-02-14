package com.example.playmate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.example.playmate.customviews.PullToRefreshListView;
import com.example.playmate.customviews.PullToRefreshListView.OnRefreshListener;
import com.example.playmate.models.User_Post;

public class MainDb extends Activity {
	
	String data = "";
	Boolean flag;
	ExpandableListView list;
    LazyAdapter adapter;
    String orig_lat,orig_long;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        list=(PullToRefreshListView)findViewById(R.id.list);
        
        registerForContextMenu(list);
        
        ((PullToRefreshListView) list).setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				new call_url().execute("");	
			}
		});
        
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		//prepareListData();       
		Intent reg_intent=getIntent();
		  orig_lat=reg_intent.getStringExtra("latitude");
		  orig_long=reg_intent.getStringExtra("longitude");
		  flag=reg_intent.getBooleanExtra("flag", false);
		  
		  if(orig_lat==null && orig_long==null)
		  {
		  SharedPreferences applicationpreferences = PreferenceManager.getDefaultSharedPreferences(MainDb.this);
			 orig_lat=applicationpreferences.getString("latitude", "");
			 orig_long=applicationpreferences.getString("longitude", "");
		  }
		 
		new call_url().execute("");	
		
	//	adapter=new LazyAdapter(this, listDataHeader, listDataChild);        
	//	 list.setAdapter(adapter);	
		
		
		    }

	class call_url extends AsyncTask<String, Void,String>{
		ProgressDialog dialog;
		@Override
		protected void onPreExecute(){
		dialog = ProgressDialog.show(MainDb.this,null,"Loading...");
		}

		@Override
		protected String doInBackground(String... params) {
				try{
					 GetDataFromDB getdb = new GetDataFromDB();
					 data = getdb.getDataFromDB(orig_lat,orig_long);
					 System.out.println(data);
				}
				catch(Exception ex){
				System.out.println("MainDb Exception"+ex);
				ex.printStackTrace();
				}
				// TODO Auto-generated method stub
				return " ";
		}
		
		@Override
		protected void onPostExecute(String Result){
			dialog.dismiss();
			 ArrayList<User_Post> users = parseJSON(data);
	         addData(users);
	         ((PullToRefreshListView) list).onRefreshComplete();
		  }
		}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
	//	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		//  String[] names = getResources().getStringArray(R.array.names);
		  switch(item.getItemId()) {
		  case R.id.edit:
		        Toast.makeText(this, "You have chosen the Edit option ",
		   //+ getResources().getString(R.string.edit) +  " context menu option for " + names[(int)info.id],
		                    Toast.LENGTH_SHORT).show();
		        return true;
		  case R.id.delete:
			  {
		      ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo)item.getMenuInfo();
			  adapter.remove(list.getPackedPositionGroup(info.packedPosition));
			  new call_url().execute("");
			  adapter.notifyDataSetChanged();			  
			 			  
			  return true;
			  }		
			  
		  case R.id.save:
			  Toast.makeText(this, "You have chosen the Save option ",Toast.LENGTH_SHORT).show();
			  return true;
		  default:
		        return super.onContextItemSelected(item);
		  }
	}
		  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_search:
	           // openSearch();
	            return true;
	        case R.id.action_compose:
	            Intent compose_intent=new Intent(MainDb.this,New_Post.class);
	            startActivity(compose_intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void addData(ArrayList<User_Post> users) {
		// TODO Auto-generated method stub
		list=(ExpandableListView)findViewById(R.id.list);
        
		adapter=new LazyAdapter(this, users, users,flag);       
        list.setAdapter(adapter);	
        
     
        
	}
	
	private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");
 
        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");
 
        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");
 
        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");
 
        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }
	
	   public ArrayList<User_Post> parseJSON(String result) {
	        ArrayList<User_Post> users = new ArrayList<User_Post>();
	        try {
	            JSONArray jArray = new JSONArray(result);
	            for (int i = 0; i < jArray.length(); i++) {
	                JSONObject json_data = jArray.getJSONObject(i);
	                User_Post user = new User_Post();
	                user.setPostId(json_data.getString("Post_Id"));
	                user.setPhoneNumber(json_data.getString("PhoneNumber"));
	                
	      
	                user.setUserName(json_data.getString("UserName"));
	                user.setPost(json_data.getString("Post"));
	                user.setDistance(json_data.getString("distance"));
	                user.setTime(json_data.getString("Time"));
	                user.setFavs(json_data.getString("Favourite"));
	                user.setComments(json_data.getString("Comment"));
	                user.setPostType(json_data.getString("Post_Type"));
	               
	                
	                String IMG_URL_PROFILE;
	                IMG_URL_PROFILE="http://aashna.webatu.com/uploadedimages/"+json_data.getString("ProfilePic");
	                user.setProfileImage(IMG_URL_PROFILE);
	                
	                String IMG_URL_POST=null;
	                if(!json_data.getString("Image").isEmpty())
	                {
	                 IMG_URL_POST="http://aashna.webatu.com/postedimages/"+json_data.getString("Image");
	                }
	                user.setPostedImage(IMG_URL_POST);

	                users.add(user);
	            }
	        } catch (JSONException e) {
	            Log.e("log_tag", "Error parsing data " + e.toString());  
	        }
	        return users;
	    }
}
