package com.ourapps.scribefinder;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {
    private EditText etEmail;
    private TextInputLayout etEmailLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        etEmail = findViewById(R.id.etEmail);
        etEmailLayout = findViewById(R.id.etEmailLayout);
        Button btnReset = findViewById(R.id.btnPasswordReset);

        progressDialog = new ProgressDialog(this);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String email = etEmail.getText().toString().trim();
               if(!checkEmail()){
                   return;
               }
                progressDialog.setMessage("Sending Email...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference idReference = rootRef.child(getString(R.string.databaseUsersParentReference)).child(postSnapshot.getKey());
                                idReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            final Users userData = dataSnapshot.getValue(Users.class);
                                            assert userData != null;
                                            int SDK_INT = android.os.Build.VERSION.SDK_INT;
                                            if (SDK_INT > 8) {
                                                rootRef.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @TargetApi(Build.VERSION_CODES.KITKAT)
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        String senderEmailId = Objects.requireNonNull(dataSnapshot.child("emailId").getValue()).toString();
                                                        String password = Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString();

                                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                                .permitAll().build();
                                                        StrictMode.setThreadPolicy(policy);
                                                        try {
                                                            GMailSender gMailSender = new GMailSender(senderEmailId, password);
                                                            gMailSender.sendMail("Your EyeDroid Username and Password",
                                                                    "Hey " + userData.getName() +
                                                                            ",\n\nWe are so happy to have you on EyeDroid" + "\n\nHere are your login credentials:" +
                                                                            "\n\nEmail: " + userData.getEmail() +
                                                                            "\nPassword: " + userData.getPassword() +
                                                                            "\n\nYou can set a new password after logging in to the app." +
                                                                            "\n\nThanks,\nEyeDroid Team",
                                                                    senderEmailId,
                                                                    email);
                                                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ResetPassword.this);
                                                            alertBuilder.setTitle("Email Sent");
                                                            alertBuilder.setMessage("An email with your credentials has been sent to " + email + ", Please check your Email.");
                                                            alertBuilder.setPositiveButton(
                                                                    "Open Email",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                                                            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                            alertBuilder.setNegativeButton(
                                                                    "Cancel",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            finishActivity(123);
                                                                        }
                                                                    });
                                                            alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            });
                                                            progressDialog.dismiss();
                                                            alertBuilder.show();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        System.out.println("The read failed: " + databaseError.getCode());
                                    }
                                });
                            }
                        }else {
                            progressDialog.dismiss();
                            etEmailLayout.setErrorEnabled(true);
                            etEmailLayout.setError("Email does not exists!");
                            requestFocus(etEmail);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError);

                    }
                };
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.databaseUsersParentReference));
                Query query = reference.orderByChild("email").equalTo(email);
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        });

    }
    @Override
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(ResetPassword.this);
        super.onResume();
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