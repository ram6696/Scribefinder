package com.ourapps.scribefinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Objects;

public class DisplaySingleScribeDetails extends AppCompatActivity implements BasicProfileFragment.OnFragmentInteractionListener, AddressFragment.OnFragmentInteractionListener {
private FirebaseAuth firebaseAuth;
private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_single_scribe_details);



        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        String LoggedIn_User_Email= user.getEmail();
        android.support.v7.widget.Toolbar displaySingleScribeToolbar = findViewById(R.id.displaySingleScribeToolbar);
        TextView toolbarScribeName = displaySingleScribeToolbar.findViewById(R.id.toolbarScribeName);
        toolbarScribeName.setText(getIntent().getStringExtra("name"));

        TabLayout tabLayout= findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Contact"));
        tabLayout.addTab(tabLayout.newTab().setText("Address"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        String id = getIntent().getStringExtra("id");

        final ViewPager viewPager = findViewById(R.id.pager);
        final ScribePageAdapter scribePageAdapter = new ScribePageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), id);
        viewPager.setAdapter(scribePageAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(DisplaySingleScribeDetails.this);

        super.onResume();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
