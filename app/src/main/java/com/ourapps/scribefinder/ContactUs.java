package com.ourapps.scribefinder;

import android.content.Intent;
import android.net.Uri;
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

    public void ramlinkedIn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/sriram-c-a62686146/"));
        startActivity(intent);
    }

    public void srikanthLinkedin(View view) {
        //TODO : Srikanths linked in link
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/sriram-c-a62686146/"));
        startActivity(intent);
    }
}
