package com.example.playmate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashScreen extends Activity{
	
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
 
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                
            	Boolean flag;

            	SharedPreferences applicationpreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
            	SharedPreferences.Editor editor = applicationpreferences .edit();

            	flag = applicationpreferences .getBoolean("flag", false);

            	if (flag) {
            	///Main activity
            	Intent i = new Intent(SplashScreen.this, MainDb.class);
                startActivity(i);

            	}else{
            	//one time registration
            	editor.putBoolean("flag", true);
                editor.commit();
            	Intent i = new Intent(SplashScreen.this, OneTimeRegister.class);
                startActivity(i);           	
            	} 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
