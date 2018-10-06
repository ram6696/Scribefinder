package com.ourapps.scribefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TypeOfUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_of_user);

    }



    public void needy(View view)
    {
        Intent log= new Intent(this, NeedyRegister.class);
        startActivity(log);
    }

    public void volunteer(View view)
    {
        Intent log= new Intent(this, VolunteerRegister.class);
        startActivity(log);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}

