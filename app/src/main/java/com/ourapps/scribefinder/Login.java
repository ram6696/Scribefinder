package com.ourapps.scribefinder;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ourapps.scribefinder.needy.NeedyMainPage;
import com.ourapps.scribefinder.volunteer.VolunteerMainPage;

import java.util.Objects;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterHere;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private TextView tvForgetPassword;

    boolean doubleBackToExitPressedOnce = false;

    private TextInputLayout etEmailLayout, etPasswordLayout;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnlogin);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        progressDialog =new ProgressDialog(this);

        etEmailLayout = findViewById(R.id.etEmailLayout);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);

        btnLogin.setOnClickListener(this);
        tvRegisterHere.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        SharedPreferences sp = getSharedPreferences("PasswordUpdate", MODE_PRIVATE);
        if(sp.contains("email")){
            etEmail.setText(sp.getString("email", ""));
        }
    }

    @Override
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(Login.this);
        super.onResume();
    }

    private void userLogin(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(!checkEmail()){
            return;
        }
        if(!checkPassword()){
            return;
        }

        etEmailLayout.setErrorEnabled(false);
        etPasswordLayout.setErrorEnabled(false);

        progressDialog.setMessage("Logging in...Please wait a moment.");
        progressDialog.show();

        //TODO
//        Runnable progressRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                progressDialog.cancel();
//                Toast.makeText(Login.this,"Check your internet connection",Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        Handler pdCanceller = new Handler();
//        pdCanceller.postDelayed(progressRunnable, 9000);

        NetworkUtil.getConnectivityStatusString(Login.this);
        progressDialog.setCancelable(true);

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    boolean emailFlag = checkEmailVerification();
                    if(emailFlag){
                        final String id = task.getResult().getUser().getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Users currUser = dataSnapshot.getValue(Users.class);
                                    Intent destPage = null;
                                    assert currUser != null;
                                    switch (currUser.getAccountType()) {
                                        case "Needy":
                                            destPage = new Intent(Login.this, NeedyMainPage.class);
                                            break;
                                        case "Volunteer":
                                            destPage = new Intent(Login.this, VolunteerMainPage.class);
                                            break;
                                    }
                                    SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                                    SharedPreferences.Editor Ed = sp.edit();
                                    Ed.putBoolean("logStatus", true);
                                    Ed.putString("uid", id);
                                    Ed.putString("email", currUser.getEmail());
                                    Ed.putString("password", currUser.getPassword());
                                    Ed.putString("accType", currUser.getAccountType());
                                    Ed.putString("name", currUser.getName());
                                    Ed.putString("mobileNumber", currUser.getMobileNumber());
                                    Ed.apply();
                                    progressDialog.dismiss();
                                    startActivity(destPage);
                                    finish();
                                }else{
                                    System.out.println("Inside else");
                                    Toast.makeText(Login.this,"Please register to login",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }

                        });
                    }
                }else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Invalid Credentials...!", Toast.LENGTH_LONG).show();
                        etPasswordLayout.setErrorEnabled(true);
                        etPasswordLayout.setError("Invalid Credentials");
                        requestFocus(etPassword);
                    }catch(FirebaseAuthEmailException e){
                        progressDialog.dismiss();
                        Toast.makeText(Login.this,"Invalid email..!",Toast.LENGTH_LONG).show();
                        etEmailLayout.setErrorEnabled(true);
                        etEmailLayout.setError("Invalid Credentials");
                        requestFocus(etEmail);
                    } catch(FirebaseAuthInvalidUserException e) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "User does not exists...!", Toast.LENGTH_LONG).show();
                        etEmailLayout.setErrorEnabled(true);
                        etEmailLayout.setError(getString(R.string.error_user_does_not_exists));
                        requestFocus(etEmail);
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

            }
         });

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(Login.this,SplashScreenNew.class));
        finish();
//            if(doubleBackToExitPressedOnce) {
//                finish();
//                super.onBackPressed();
//                return;
//            }
//
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    doubleBackToExitPressedOnce = false;
//                }
//            }, 2000);
        }


    private boolean checkEmail() {
        String email = etEmail.getText().toString().trim();
        if(email.isEmpty()){
            etEmailLayout.setErrorEnabled(true);
            etEmailLayout.setError("Please enter Email");
            requestFocus(etEmail);
            return false;
        }else if(!isValidEmail(email)){
            etEmailLayout.setErrorEnabled(true);
            etEmailLayout.setError("Please enter valid Email");
            requestFocus(etEmail);
            return false;
        }
        etEmailLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {
        String newPassword = etPassword.getText().toString().trim();
        if(newPassword.isEmpty()){
            etPasswordLayout.setErrorEnabled(true);
            etPasswordLayout.setError("Please enter new password");
            requestFocus(etPassword);
            return false;
        }
        etPasswordLayout.setErrorEnabled(false);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin){
            userLogin();
        }else if(view == tvRegisterHere){
            Intent typeOfUser = new Intent(Login.this, TypeOfUser.class);
            startActivity(typeOfUser);
        } else if(view == tvForgetPassword){
            Intent forgotPasswordPage = new Intent(Login.this, ResetPassword.class);
            startActivity(forgotPasswordPage);
        }
    }

    private boolean checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Boolean emailFlag = firebaseUser.isEmailVerified();
        if(!emailFlag){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Login.this);
            builder1.setTitle("Email Not Verified");
            builder1.setMessage(" Please verify Your email  to login.");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Open Email",
                    new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_APP_EMAIL); ;
                            startActivity(intent);
                            progressDialog.dismiss();
                        }
                    });
            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog.dismiss();
                            startActivity(getIntent());
                            dialog.cancel();
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            progressDialog.dismiss();
            firebaseAuth.signOut();
            return false;
        }else{
            return true;
        }
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void intro(View view) {
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.setFirstTimeLaunch(true);
        startActivity(new Intent(Login.this, Intro.class));
        finish();
    }
}