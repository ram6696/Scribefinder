package com.ourapps.scribefinder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class NeedyPasswordChange extends AppCompatActivity implements View.OnClickListener{

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout etOldPasswordLayout, etNewPasswordLayout, etConfirmPasswordLayout;
    private TextView tvForgetPassword;
    private Button btnChangePassword;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_password_change);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        etOldPasswordLayout = findViewById(R.id.etOldPasswordLayout);
        etNewPasswordLayout = findViewById(R.id.etNewPasswordLayout);
        etConfirmPasswordLayout  = findViewById(R.id.etConfirmPasswordLayout);

        btnChangePassword = findViewById(R.id.btnChangePassword);

        tvForgetPassword = findViewById(R.id.tvForgetPasswordPasswordChange);

        btnChangePassword.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        tvForgetPassword.setOnClickListener(this);

        progressDialog =new ProgressDialog(this);
    }
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(NeedyPasswordChange.this);

        super.onResume();


    }

    @Override
    public void onClick(View view) {
        if(view == btnChangePassword){
            changePassword();
        }else if(view == tvForgetPassword){
            Intent forgotPasswordPage = new Intent(NeedyPasswordChange.this, ResetPassword.class);
            startActivityForResult(forgotPasswordPage, 123);
        }
    }

    public void changePassword(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String userId = user.getUid();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();

        if(!checkOldPassword()){
            return;
        }
        if(!checkNewPassword()){
            return;
        }
        if(!checkConfirmPassword()){
            return;
        }

        progressDialog.show();
        progressDialog.setMessage("Changing password...");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Needy").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final NeedyData needyData = dataSnapshot.getValue(NeedyData.class);
                        assert needyData != null;
                        if (!((needyData.getPassword()).contentEquals(etOldPassword.getText().toString().trim()))) {
                            progressDialog.dismiss();
                            etOldPasswordLayout.setErrorEnabled(true);
                            etOldPasswordLayout.setError("Your current password is incorrect.");
                            requestFocus(etOldPassword);
                        } else {
                            user.updatePassword(confirmPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        NeedyData newNeedyData = new NeedyData(needyData.getNeedyId(), needyData.getName(), needyData.getEmail(), needyData.getMobileNumber(), etConfirmPassword.getText().toString().trim(), needyData.getAccountType(), needyData.getPhotoUrl());
                                        databaseReference.child("Needy").child(userId).setValue(newNeedyData);
                                        Users newUsersData = new Users(needyData.getNeedyId(), needyData.getEmail(), etConfirmPassword.getText().toString().trim(),needyData.getAccountType(), needyData.getName(), needyData.getMobileNumber());
                                        databaseReference.child("Users").child(userId).setValue(newUsersData);
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(NeedyPasswordChange.this);
                                        builder1.setMessage("Your password has been changed successfully, Please login again.");
                                        builder1.setCancelable(false);
                                        builder1.setPositiveButton(
                                                "Login",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.remove("uid");
                                                        editor.remove("email");
                                                        editor.remove("password");
                                                        editor.remove("accType");
                                                        editor.putBoolean("logStatus", false);
                                                        editor.apply();
                                                        firebaseAuth.signOut();
                                                        Intent loginForUpdatePassword = new Intent(NeedyPasswordChange.this, Login.class);
                                                        startActivity(loginForUpdatePassword);
                                                        finish();
                                                    }
                                                });
                                        AlertDialog alert1 = builder1.create();
                                        alert1.show();
                                    } else {
                                        try{
                                            throw Objects.requireNonNull(task.getException());
                                        }catch(FirebaseAuthWeakPasswordException weakPassword){
                                            progressDialog.dismiss();
                                            etNewPasswordLayout.setErrorEnabled(true);
                                            etNewPasswordLayout.setError("Password should be at least 6 characters.");
                                            etNewPassword.setText("");
                                            etConfirmPassword.setText("");
                                            requestFocus(etNewPassword);
                                        }catch (Exception e){
                                            signInAgain(needyData.getEmail(), needyData.getPassword());
                                            e.printStackTrace();
                                            progressDialog.dismiss();
                                            Toast.makeText(NeedyPasswordChange.this, "Unknown Error Occured..!.", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
    }

    private void signInAgain(String email, String password) {
        SharedPreferences sp = getSharedPreferences("PasswordUpdate", MODE_PRIVATE);
        SharedPreferences.Editor Ed= sp.edit();
        Ed.putString("email",email);
        Ed.putString("password",password);
        Ed.apply();
        Intent loginForUpdatePassword = new Intent(NeedyPasswordChange.this, Login.class);
        startActivity(loginForUpdatePassword);
        finish();
    }

    private boolean checkOldPassword() {
        String currentPassword = etOldPassword.getText().toString().trim();
        if(currentPassword.isEmpty()){
            etOldPasswordLayout.setErrorEnabled(true);
            etOldPasswordLayout.setError("Please enter Current Password");
            requestFocus(etOldPassword);
            return false;
        }
        etOldPasswordLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkNewPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        if(newPassword.isEmpty()){
            etNewPasswordLayout.setErrorEnabled(true);
            etNewPasswordLayout.setError("Please enter New Password");
            requestFocus(etNewPassword);
            return false;
        }
        etNewPasswordLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkConfirmPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if(confirmPassword.isEmpty()){
            etConfirmPasswordLayout.setErrorEnabled(true);
            etConfirmPasswordLayout.setError("Please enter Valid Password");
            requestFocus(etConfirmPassword);
            return false;
        }else if(!(newPassword.equals(confirmPassword))){
            etNewPasswordLayout.setErrorEnabled(true);
            etNewPasswordLayout.setError("Passwords are not matching..!");
            etConfirmPasswordLayout.setErrorEnabled(true);
            etConfirmPasswordLayout.setError("Passwords are not matching..!");
            requestFocus(etConfirmPassword);
            return false;
        }
        etConfirmPasswordLayout.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        startActivity(new Intent(NeedyPasswordChange.this, NeedyMainPage.class));
    }
}
