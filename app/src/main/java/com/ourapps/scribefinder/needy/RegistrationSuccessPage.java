package com.ourapps.scribefinder.needy;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ourapps.scribefinder.Login;
import com.ourapps.scribefinder.R;

public class RegistrationSuccessPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success_page);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        finishActivity(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
    }

    public void openEmailForVerification(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        }
        startActivity(new Intent(this, Login.class));
        startActivity(intent);
        finish();
    }

    public void cancelPageAndGoBackToLogin(View view) {
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
