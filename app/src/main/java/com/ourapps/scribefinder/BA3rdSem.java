package com.ourapps.scribefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ourapps.scribefinder.R;

public class BA3rdSem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba3rd_sem);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
