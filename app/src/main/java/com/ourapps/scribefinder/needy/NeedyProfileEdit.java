package com.ourapps.scribefinder.needy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ourapps.scribefinder.NetworkUtil;
import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.Users;

import java.util.Objects;

public class NeedyProfileEdit extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etEmail, etMobileNumber;
    private TextInputLayout etNameLayout;
    private TextInputLayout etMobileNumberLayout;
    private Button btnEdit;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    SharedPreferences sp = null;
    String currentUserId;
    NeedyData needyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_profile_edit);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        etName = findViewById(R.id.etName);
        etName.clearFocus();
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);

        etNameLayout = findViewById(R.id.etNameLayout);
        etMobileNumberLayout = findViewById(R.id.etMobileNumberLayout);


        etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        sp = getSharedPreferences("Login", MODE_PRIVATE);
        currentUserId = sp.getString("uid", "");

        setPreviousValues();
    }
    @Override
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(NeedyProfileEdit.this);

        super.onResume();


    }

    private void setPreviousValues() {

        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCancelable(false);

        DatabaseReference idReference = databaseReference.child("Needy").child(currentUserId);
        idReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    needyData = dataSnapshot.getValue(NeedyData.class);
                    assert needyData != null;
                    etName.setText(needyData.getName());
                    etEmail.setText(needyData.getEmail());
                    etMobileNumber.setText(needyData.getMobileNumber());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view == btnEdit){
            editProfile();
        }
    }

    private void editProfile() {

        if(!checkName()){
            return;
        }
        if(!checkMobileNumber()){
            return;
        }

        etNameLayout.setErrorEnabled(false);
        etMobileNumberLayout.setErrorEnabled(false);

        final String name           = etName.getText().toString().trim();
        final String email          = needyData.getEmail();
        final String mobileNumber   = etMobileNumber.getText().toString().trim();
        final String password       = needyData.getPassword();
        final String photoUrl       = needyData.getPhotoUrl();

        progressDialog.setMessage("Updating Profile Details..");
        progressDialog.show();

        final DatabaseReference needyReference = databaseReference.child("Needy").child(currentUserId);
        final DatabaseReference usersReference = databaseReference.child("Users").child(currentUserId);

        NeedyData newData = new NeedyData(currentUserId, name, email, mobileNumber,password, "Needy", photoUrl);

        needyReference.setValue(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Users newUsers = new Users(currentUserId, email, password, "Needy", name, mobileNumber);
                    usersReference.setValue(newUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                System.out.println("Successfully Updated");
                                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor Ed= sp.edit();
                                Ed.putString("name", name);
                                Ed.putString("mobileNumber", mobileNumber);
                                Ed.apply();
                                progressDialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(NeedyProfileEdit.this);
                                builder1.setMessage("Your details are successfully updated.");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        "Okay",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                finish();
                                                startActivity(new Intent(NeedyProfileEdit.this, NeedyMainPage.class));
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }else if(task.isCanceled()){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(NeedyProfileEdit.this);
                                builder1.setMessage("Oops..There is some error during update.");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        "Okay",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                                System.out.println(Objects.requireNonNull(task.getException()).getMessage());
                                progressDialog.dismiss();
                            }
                        }
                    });
                }else if(task.isCanceled()){
                    System.out.println(task.getException().getMessage());
                    progressDialog.dismiss();
                }
            }
        });
    }

    private boolean checkName() {
        if(etName.getText().toString().trim().isEmpty()){
            etNameLayout.setErrorEnabled(true);
            etNameLayout.setError("Please enter Valid Name");
            requestFocus(etName);
            return false;
        }
        etNameLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkMobileNumber() {
        String mobileNumber = etMobileNumber.getText().toString().trim();
        if(mobileNumber.isEmpty()){
            etMobileNumberLayout.setErrorEnabled(true);
            etMobileNumberLayout.setError("Please enter Mobile Number");
            requestFocus(etMobileNumber);
            return false;
        }else if(mobileNumber.length() != 10){
            etMobileNumberLayout.setErrorEnabled(true);
            etMobileNumberLayout.setError("Please enter Valid Mobile Number");
            requestFocus(etMobileNumber);
            return false;
        }
        etMobileNumberLayout.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        System.out.println("Pressed Back Button");
        finish();
        startActivity(new Intent(NeedyProfileEdit.this, NeedyMainPage.class));
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        startActivity(new Intent(NeedyProfileEdit.this, NeedyMainPage.class));
    }
}
