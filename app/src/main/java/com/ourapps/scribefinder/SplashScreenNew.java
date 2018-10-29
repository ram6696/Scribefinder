package com.ourapps.scribefinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourapps.scribefinder.needy.NeedyMainPage;
import com.ourapps.scribefinder.volunteer.VolunteerMainPage;

public class SplashScreenNew extends AppCompatActivity {

    Button loginButton, registerButton;
    Animation fadeAnimation;
    boolean loginStatus;
    Intent destPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_new);

        ImageView logoEyeDroid = findViewById(R.id.logo);
        TextView logoText = findViewById(R.id.appName);
        logoText.setText(R.string.app_name);
        loginButton = findViewById(R.id.btn_login);
        registerButton = findViewById(R.id.btn_register);

        loginButton.setVisibility(View.INVISIBLE);
        registerButton.setVisibility(View.INVISIBLE);

        fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        loginStatus = prefs.getBoolean("logStatus", false);
        if (loginStatus) {
            logoEyeDroid.startAnimation(fadeAnimation);
            logoText.startAnimation(fadeAnimation);
            String accType = prefs.getString("accType", null);
            if (accType != null) {
                switch (accType) {
                    case "Needy":
                        destPage = new Intent(SplashScreenNew.this, NeedyMainPage.class);
                        break;
                    case "Volunteer":
                        destPage = new Intent(SplashScreenNew.this, VolunteerMainPage.class);
                        break;
                    default:
                        System.out.println("Account Type is null");
                }
            }
        } else {
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            logoEyeDroid.startAnimation(fadeAnimation);
            logoText.startAnimation(fadeAnimation);
            loginButton.startAnimation(fadeAnimation);
            registerButton.startAnimation(fadeAnimation);
        }

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    if (loginStatus) {
                        startActivity(destPage);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();

    }

    public void onLoginButtonCLicked(View view) {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    public void onRegisterButtonClicked(View view) {
        startActivity(new Intent(this, Intro.class));
        finish();
    }
}
