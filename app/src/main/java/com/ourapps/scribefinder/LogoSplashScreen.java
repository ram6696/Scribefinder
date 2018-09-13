package com.ourapps.scribefinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

public class LogoSplashScreen extends AppCompatActivity {

    private TextView logoText;
    private ImageView logoInformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        MobileAds.initialize(this,"admob_app_id");
        logoText = findViewById(R.id.logoText);
        logoInformer = findViewById(R.id.logo);

        Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.mytransition);

        logoInformer.startAnimation(myAnimation);
        logoText.startAnimation(myAnimation);

        final Intent loginPage = new Intent(this,Intro.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                    SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                    boolean loginStatus = prefs.getBoolean("logStatus", false);
                    if (loginStatus) {
                        String accType = prefs.getString("accType", null);
                        Intent destPage = null;
                        switch (accType){
                            case "Needy":
                                destPage = new Intent(LogoSplashScreen.this, NeedyMainPage.class);
                                break;
                            case "Volunteer":
                                destPage = new Intent(LogoSplashScreen.this, VolunteerMainPage.class);
                                break;
                        }
                        startActivity(destPage);
                        finish();
                    } else{
                        startActivity(loginPage);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }
}
