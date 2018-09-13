package com.ourapps.scribefinder.Notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ourapps.scribefinder.R;

public class BA2ndSem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba2nd_sem);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
