package com.ourapps.scribefinder.StudyMaterials.BANotes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ourapps.scribefinder.R;

public class BA6thSem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba6th_sem);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
