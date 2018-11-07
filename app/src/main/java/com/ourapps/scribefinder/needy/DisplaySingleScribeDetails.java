package com.ourapps.scribefinder.needy;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ourapps.scribefinder.NetworkUtil;
import com.ourapps.scribefinder.R;

public class DisplaySingleScribeDetails extends AppCompatActivity implements BasicProfileFragment.OnFragmentInteractionListener, AddressFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_single_scribe_details);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        android.support.v7.widget.Toolbar displaySingleScribeToolbar = findViewById(R.id.displaySingleScribeToolbar);
        TextView toolbarScribeName = displaySingleScribeToolbar.findViewById(R.id.toolbarScribeName);
        toolbarScribeName.setText(getString(R.string.details_of) + " " + getIntent().getStringExtra("name"));

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

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
