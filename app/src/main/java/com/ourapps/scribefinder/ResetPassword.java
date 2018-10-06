package com.ourapps.scribefinder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                                DatabaseReference idReference = rootRef.child("Users").child(postSnapshot.getKey());
                                idReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            Users userData = dataSnapshot.getValue(Users.class);
                                            assert userData != null;
                                            int SDK_INT = android.os.Build.VERSION.SDK_INT;
                                            if (SDK_INT > 8)
                                            {
                                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                        .permitAll().build();
                                                StrictMode.setThreadPolicy(policy);
                                                try {
                                                    GMailSender sender = new GMailSender("scribefinder.info@gmail.com", "9738469125");
                                                    sender.sendMail("Credentials of Your Scribe Finder",
                                                            "Hello "+userData.getName()+
                                                                    ",\n\n\tYou can login to your account using the following credentials.\n\n"+
                                                                    "Your Email: "+userData.getEmail()+
                                                                    "\nYour Password: "+userData.getPassword()+
                                                                    "\n\nThanks,\nScribe Finder Team",
                                                            "scribefinder.info@gmail.com",
                                                            email);
                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResetPassword.this);
                                                    alertDialog.setTitle("Email Sent");
                                                    alertDialog.setMessage("An Email with your credentials has been sent to "+email+", Please check your Email.");
                                                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,int which) {
                                                           finishActivity(123);
                                                        }
                                                    });
                                                    progressDialog.dismiss();
                                                    alertDialog.show();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
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
                            System.out.println("No data found");
                            etEmailLayout.setErrorEnabled(true);
                            etEmailLayout.setError("Email does not exists!");
                            requestFocus(etEmail);
                            Toast.makeText(ResetPassword.this, "Email does not exists", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError);

                    }
                };

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
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
