package com.ourapps.scribefinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Instruction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
    }
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(Instruction.this);
        super.onResume();
    }
    public void goBackToPreviousActivity(View view) {
        finish();
    }

    public void facebook(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pg/Scribe-Finder-327894291336361/posts"));
        startActivity(intent);
    }
}
