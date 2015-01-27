package com.example.playmate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class New_Post extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post_dialog);
        
        addListenerOnOk();
	}
	
	public void addListenerOnOk()
	{
	 Button ok_button=(Button)findViewById(R.id.ok);
	 
	 ok_button.setOnClickListener(new OnClickListener() {
		 
		  @Override
		  public void onClick(View v) {
			  
			  
		  }});
	
	
	}
}
