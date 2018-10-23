package com.ourapps.scribefinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourapps.scribefinder.needy.NeedyMainPage;
import com.ourapps.scribefinder.volunteer.VolunteerMainPage;

public class LogoSplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView logoText = findViewById(R.id.logoText);
        ImageView logoEyeDroid = findViewById(R.id.logo);

        Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.mytransition);

        logoEyeDroid.startAnimation(myAnimation);
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
                        if (accType != null) {
                            switch (accType) {
                                case "Needy":
                                    destPage = new Intent(LogoSplashScreen.this, NeedyMainPage.class);
                                    break;
                                case "Volunteer":
                                    destPage = new Intent(LogoSplashScreen.this, VolunteerMainPage.class);
                                    break;
                            }
                        } else
                            System.out.println("Account Type is Null");
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
