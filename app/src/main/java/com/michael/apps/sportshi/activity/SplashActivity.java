package com.michael.apps.sportshi.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.michael.apps.sportshi.R;

public class SplashActivity extends AppCompatActivity {
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                i = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(i);
                SplashActivity.this.finish();
            }
        }, 3000L);
    }

    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_splash);
    }
}
