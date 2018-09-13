package com.ourapps.scribefinder.Notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ourapps.scribefinder.R;

public class BA5thSem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba5th_sem);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
