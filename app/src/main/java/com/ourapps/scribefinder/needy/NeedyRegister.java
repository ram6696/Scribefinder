package com.ourapps.scribefinder.needy;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.ourapps.scribefinder.NetworkUtil;
import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.TypeOfUser;

import java.util.Objects;

public class NeedyRegister extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etEmail, etMobileNumber, etNewPassword, etConfirmPassword;
    private TextInputLayout etNameLayout, etEmailLayout, etMobileNumberLayout, etNewPasswordLayout, etConfirmPasswordLayout;
    private Button btnRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        etName = findViewById(R.id.etName);
        etName.clearFocus();
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        etNameLayout = findViewById(R.id.etNameLayout);
        etEmailLayout = findViewById(R.id.etEmailLayout);
        etMobileNumberLayout = findViewById(R.id.etMobileNumberLayout);
        etNewPasswordLayout = findViewById(R.id.etNewPasswordLayout);
        etConfirmPasswordLayout = findViewById(R.id.etConfirmPasswordLayout);

        etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        btnRegister = findViewById(R.id.btnRegiser);
        btnRegister.setOnClickListener(this);
    }
    @Override
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(NeedyRegister.this);
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this,TypeOfUser.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == btnRegister){
            registerUser();
        }
    }

    private void  registerUser() {
        if(!checkName()){
            return;
        }
        if(!checkEmail()){
            return;
        }
        if(!checkMobileNumber()){
            return;
        }
        if(!checkPasswords()){
            return;
        }

        etNameLayout.setErrorEnabled(false);
        etEmailLayout.setErrorEnabled(false);
        etMobileNumberLayout.setErrorEnabled(false);
        etNewPasswordLayout.setErrorEnabled(false);

        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String mobileNumber = etMobileNumber.getText().toString().trim();
        final String password = etNewPassword.getText().toString().trim();

        progressDialog.setMessage("Registering User..");
        progressDialog.show();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthUserCollisionException emailIdAlreadyUsed) {
                        progressDialog.dismiss();
                        Toast.makeText(NeedyRegister.this, "Email Id already exists..!", Toast.LENGTH_SHORT).show();
                         etEmailLayout.setErrorEnabled(true);
                         etEmailLayout.setError("Email already Exists");
                        etEmailLayout.requestFocus();
                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                        progressDialog.dismiss();
                        Toast.makeText(NeedyRegister.this, "Password should be at least 6 characters..!", Toast.LENGTH_SHORT).show();
                        etNewPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                        progressDialog.dismiss();
                        Toast.makeText(NeedyRegister.this, "Invalid Email Id..!.", Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(NeedyRegister.this, "Unknown Error Occured..!.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    String currentUserId = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
                    Intent certificatePage = new Intent(NeedyRegister.this, UploadNeedyCertificate.class);
                    certificatePage.putExtra("currentUserId", currentUserId);
                    certificatePage.putExtra("name", name);
                    certificatePage.putExtra("email", email);
                    certificatePage.putExtra("mobileNumber", mobileNumber);
                    certificatePage.putExtra("password", password);
                    progressDialog.dismiss();
                    startActivity(certificatePage);
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

    private boolean checkEmail() {
        String email = etEmail.getText().toString().trim();
        if(email.isEmpty()){
            etEmailLayout.setErrorEnabled(true);
            etEmailLayout.setError("Please enter Email Address");
            requestFocus(etEmail);
            return false;
        }else if(!isValidEmail(email)){
            etEmailLayout.setErrorEnabled(true);
            etEmailLayout.setError("Please enter Valid Email Address");
            requestFocus(etEmail);
            return false;
        }
        etEmailLayout.setErrorEnabled(false);
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

    private boolean checkPasswords() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if(newPassword.isEmpty()){
            etNewPasswordLayout.setErrorEnabled(true);
            etNewPasswordLayout.setError("Please enter New Password");
            requestFocus(etNewPassword);
            return false;
        }else if(confirmPassword.isEmpty()){
            etConfirmPasswordLayout.setErrorEnabled(true);
            etConfirmPasswordLayout.setError("Please enter Confirm Password");
            requestFocus(etConfirmPassword);
            return false;
        } else if(!newPassword.contentEquals(confirmPassword)){
            etConfirmPasswordLayout.setErrorEnabled(true);
            etConfirmPasswordLayout.setError("Passwords not matching");
            requestFocus(etConfirmPassword);
            return false;
        }
        etNewPasswordLayout.setErrorEnabled(false);
        return true;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) &&     Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
