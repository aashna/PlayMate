package com.example.playmate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

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
    private Animator mCurrentAnimator;
    ImageView expandedImageView=null;   
    private int mShortAnimationDuration;

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
    
    public void remove(int position)
    {    	
    	 SharedPreferences applicationpreferences = PreferenceManager.getDefaultSharedPreferences(_context);
	   	    final String phone_num=applicationpreferences.getString("phone_number", "");
	   	    
	   	 User_Post user = new User_Post();
	     user = data.get(position);
	   	    
	   	   Log.e("Phone Number: ", "> " + phone_num); 
	   	    if(phone_num.equals(user.getPhoneNumber()))
	   	    {
    	new DeleteItem().execute(position); 
	   	    }
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
   	    
   	    Log.e("phone",phone);
        
        TextView user_name = (TextView)convertView.findViewById(R.id.user_name); // User Name
        TextView post = (TextView)convertView.findViewById(R.id.post); // Post of the User
        TextView distance = (TextView)convertView.findViewById(R.id.dist); // Distance
        TextView time = (TextView)convertView.findViewById(R.id.time); // Timestamp
        ImageView posted_image=(ImageView)convertView.findViewById(R.id.posted_img); // Posted Image
        final ImageView thumb_image=(ImageView)convertView.findViewById(R.id.list_image); // Thumb image
        final ImageButton smiley=(ImageButton)convertView.findViewById(R.id.smiley); 
        ImageButton comment=(ImageButton)convertView.findViewById(R.id.comment);
        TextView comment_num = (TextView)convertView.findViewById(R.id.comment_num);
        TextView favs = (TextView)convertView.findViewById(R.id.favourites);
        ImageView postTypeimg=(ImageView)convertView.findViewById(R.id.post_type);
        expandedImageView = (ImageView)convertView.findViewById(R.id.expanded_image);
        final FrameLayout fl=(FrameLayout)convertView.findViewById(R.id.container);
        ImageView menu=(ImageView)convertView.findViewById(R.id.menu);

               
        mShortAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    
        
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
        
        menu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {       
			//	registerForContextMenu(v);
				v.showContextMenu();
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
        
        final String pr_img=user.getProfileImage();
        
        thumb_image.setOnClickListener(new OnClickListener(){
 			@Override
 			public void onClick(View v) {       
 				 zoomImageFromThumb(context,fl,thumb_image,pr_img);
 			}});
        
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

          String json = serviceClient.makeServiceCall(URL_FAVOURITE,
                  ServiceHandler.POST, params);

          Log.e("Created Favourite: ", "> " + json); }

    		return null;
    	}
    	 protected void onPostExecute(Void result) {
    	     super.onPostExecute(result);
    	 }	
    }
    
    private class DeleteItem extends AsyncTask<Object,Void,Void> {

        private static final String TAG = "FavouriteError";
        String Request;
        private String URL_DELETE = "http://aashna.webatu.com/delete_post.php";

    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    	@Override
    	protected Void doInBackground(Object... arg) {
    		// TODO Auto-generated method stub
        		int pos=(Integer)(arg[0]);      		

    		 User_Post user = new User_Post();
    	     user = data.get(pos);
    	     String postID=user.getPostId();
         
          List<NameValuePair> params = new ArrayList<NameValuePair>();
          params.add(new BasicNameValuePair("post_id", postID));

          ServiceHandler serviceClient = new ServiceHandler();

          String json = serviceClient.makeServiceCall(URL_DELETE,ServiceHandler.POST, params);

          Log.e("Deleted: ", "> " + json); 
 
    		return null;
    }
    	 protected void onPostExecute(Void result) {
    	     super.onPostExecute(result);
    	 }	
    }
    
    
    private void zoomImageFromThumb(Context context,FrameLayout fl,final ImageView thumbView, String pr_img) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
      
        //expandedImageView.setImageResource(pr_img);
        Picasso.with(context)
        .load(pr_img)
        .into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        fl.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        expandedImageView.bringToFront();

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                            .ofFloat(expandedImageView, View.X, startBounds.left))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView, 
                                            View.Y,startBounds.top))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView, 
                                            View.SCALE_X, startScaleFinal))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView, 
                                            View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
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