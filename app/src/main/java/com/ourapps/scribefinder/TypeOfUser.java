package com.ourapps.scribefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ourapps.scribefinder.needy.NeedyRegister;
import com.ourapps.scribefinder.volunteer.VolunteerRegister;

public class TypeOfUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_of_user);

    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this,SplashScreenNew.class));
        finish();
    }


    public void needy(View view)
    {
        Intent log= new Intent(this, NeedyRegister.class);
        startActivity(log);
        finish();
    }

    public void volunteer(View view)
    {
        Intent log= new Intent(this, VolunteerRegister.class);
        startActivity(log);
        finish();


    }

    public void goBackToPreviousActivity(View view) {

        startActivity(new Intent(this,SplashScreenNew.class));
        finish();

    }
}

