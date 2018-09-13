package com.ourapps.scribefinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterSuccessfulMessage extends AppCompatActivity implements View.OnClickListener {

    private Button btnCancel, btnResendLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_successfull_message);

        btnCancel = findViewById(R.id.btnCancel);
        btnResendLink = findViewById(R.id.btnResendLink);

        btnCancel.setOnClickListener(this);
        btnResendLink.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v == btnCancel){
            finish();
            startActivity(new Intent(RegisterSuccessfulMessage.this, Login.class));
        }else if(v == btnResendLink){

        }
    }
}
