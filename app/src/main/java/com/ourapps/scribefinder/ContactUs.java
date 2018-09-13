package com.ourapps.scribefinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
