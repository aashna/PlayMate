package com.example.playmate;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.playmate.models.User_Post;
import com.squareup.picasso.Picasso;

public class LazyAdapter extends BaseExpandableListAdapter {
    
    Boolean flag;
    String post_id;
    private Context _context;
	private Activity activity;
    private ArrayList<User_Post> data;
    private ArrayList<User_Post> _listDataChild;
    private static LayoutInflater inflater=null;

    public LazyAdapter(Context context, ArrayList<User_Post> d,
            ArrayList<User_Post> listChildData, Boolean flag) {
        this._context = context;
        this.data = d;
        this._listDataChild = listChildData;
        this.flag=flag;
    }
    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getGroupView(final int position,final boolean isExpanded, View convertView, final ViewGroup parent) {
        View vi=convertView;
    //    String headerTitle=(String)getGroup(position);
     
        	 LayoutInflater infalInflater = (LayoutInflater) this._context
                     .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             convertView = infalInflater.inflate(R.layout.list_row, null);

        final Context context = parent.getContext();      
        
    	SharedPreferences applicationpreferences = PreferenceManager.getDefaultSharedPreferences(context);
   	    final String phone=applicationpreferences.getString("phone_number", "");
        
        TextView user_name = (TextView)convertView.findViewById(R.id.user_name); // User Name
        TextView post = (TextView)convertView.findViewById(R.id.post); // Post of the User
        TextView distance = (TextView)convertView.findViewById(R.id.dist); // Distance
        TextView time = (TextView)convertView.findViewById(R.id.time); // Timestamp
        ImageView posted_image=(ImageView)convertView.findViewById(R.id.posted_img); // Posted Image
        ImageView thumb_image=(ImageView)convertView.findViewById(R.id.list_image); // Thumb image
        final ImageButton smiley=(ImageButton)convertView.findViewById(R.id.smiley); 
        ImageButton comment=(ImageButton)convertView.findViewById(R.id.comment);
        TextView comment_num = (TextView)convertView.findViewById(R.id.comment_num);
        TextView favs = (TextView)convertView.findViewById(R.id.favourites);
        ImageView postTypeimg=(ImageView)convertView.findViewById(R.id.post_type);

        smiley.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {       
				 smiley.setImageResource(R.drawable.smiley_green);
				 new AddFavourite().execute(position,phone,smiley); 
			}});
        
        comment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				 if(isExpanded){ ((ExpandableListView) parent).collapseGroup(position);
				 }
		          else 
		          {
		        	  ((ExpandableListView) parent).expandGroup(position, true);  
		          }
			}});
        
        User_Post user = new User_Post();
        user = data.get(position);
        post_id=user.getPostId();

       //  Setting all values in listview
        user_name.setText(user.getUserName());
        post.setText(user.getPost());
        time.setText(user.getTime());
        comment_num.setText(user.getComments());
        favs.setText(user.getFavs());
        
        Double dist=Double.parseDouble(user.getDistance());
        distance.setText(String.valueOf(Math.round(dist*1.60934*100.0)/100.0));
        Picasso.with(context)
        .load(user.getProfileImage())
        .into(thumb_image);
        
        if(user.getPostedImage()!=null)
        {
        posted_image.setVisibility(convertView.VISIBLE);
        Picasso.with(context)
        .load(user.getPostedImage())
        .into(posted_image);
        }
        
        int post_type_id=0;
        int id=Integer.valueOf(user.getPostType());
        
        switch(id)
        {
        case 1: post_type_id=R.drawable.info;
               break;
        case 2: post_type_id=R.drawable.question;
                break;
        case 3: post_type_id=R.drawable.ad;
                break;
        case 4: post_type_id=R.drawable.meetup;
                break;
        case 5: post_type_id=R.drawable.donate;
                break;   
        case 0: post_type_id=R.drawable.info;
        		break;  
        default: post_type_id=R.drawable.info;
        		break;
        }
        postTypeimg.setImageDrawable(context.getResources().getDrawable(post_type_id));
        
        return convertView;
    }
    private class AddFavourite extends AsyncTask<Object,Void,Void> {

        private static final String TAG = "FavouriteError";
        String Request;
        private String URL_FAVOURITE = "http://aashna.webatu.com/favourite.php";

    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    	@Override
    	protected Void doInBackground(Object... arg) {
    		// TODO Auto-generated method stub
        		int pos=(Integer)(arg[0]);
    		String ph=(String)(arg[1]);
    //		ImageButton smiley=(ImageButton)(arg[2]);
    		
    		 User_Post user = new User_Post();
    	     user = data.get(pos);
    	     String postID=user.getPostId();
 
         if(!ph.equals(user.getPhoneNumber()))        	
         { 	                  
          List<NameValuePair> params = new ArrayList<NameValuePair>();
          params.add(new BasicNameValuePair("post_id", postID));

          ServiceHandler serviceClient = new ServiceHandler();

          String json = serviceClient.makeServiceCall("http://aashna.webatu.com/favourite.php",
                  ServiceHandler.POST, params);

          Log.e("Created Favourite: ", "> " + json); }

    		return null;
    	}
    	 protected void onPostExecute(Void result) {
    	     super.onPostExecute(result);
    	 }	
    }
    
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		 return this.data.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		 return this._listDataChild
				 //.get(this.data.get(groupPosition))
	                .size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		 return this.data.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return this._listDataChild
				//.get(this.data.get(groupPosition))
                .get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}


	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}


	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	//	final String childText = (String) getChild(groupPosition, childPosition);
		Context child_context=parent.getContext();
		
		
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.comment_list_item, null);
        }
        LinearLayout containerLayout = (LinearLayout)convertView.findViewById(R.id.ll);
        
        TextView txtListChild = (TextView)convertView.findViewById(R.id.name);
                
        if (childPosition == getChildrenCount(groupPosition) - 1) {
            // This gives you confirmation that is the last child in the group
        	EditText editText = new EditText(child_context);
            containerLayout.addView(editText);
            editText.hasFocus();
            editText.setHint("Enter your comment here");
            LayoutParams lp1=(LayoutParams) txtListChild.getLayoutParams();
            lp1.weight=1;
            editText.setLayoutParams(lp1);           
            editText.setTextSize(10);
            
            Button button_send=new Button(child_context);
            containerLayout.addView(button_send);
            LayoutParams lp2=(LayoutParams) ((TextView)convertView.findViewById(R.id.comment)).getLayoutParams();
            button_send.setHeight(15);
            button_send.setWidth(15);
          //  button_send.setLayoutParams(lp2);
            button_send.setText("Send");
            
            button_send.setTextSize(10);
            
       }
        txtListChild.setText("test");
        return convertView;
	}


	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
}

