package com.ourapps.scribefinder;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ContactUs extends AppCompatActivity {

    DatabaseReference databaseReference;
    private RadioGroup typeSelector;
    private TextView commentsSelector;
    private EditText et_Comments;
    private ProgressDialog progressDialog;
    private String commentsType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        commentsSelector = findViewById(R.id.commentsSelector);

        typeSelector = findViewById(R.id.typeSelector);
        typeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (typeSelector.getCheckedRadioButtonId()) {
                    case R.id.suggestionRadioButton:
                        commentsType = "Suggestion";
                        commentsSelector.setText(R.string.suggestions_text);
                        break;
                    case R.id.queriesRadioButton:
                        commentsType = "Query";
                        commentsSelector.setText(R.string.queries_text);
                        break;
                }
            }
        });

        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        et_Comments = findViewById(R.id.et_Comments);
        Button commentsSend = findViewById(R.id.btnCommentsSend);
        commentsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Submitting please wait...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                final String userAccType = sharedPreferences.getString("accType", "");
                final String userName = sharedPreferences.getString("name", "");
                final String userMessage = et_Comments.getText().toString();
                final String userEmail = sharedPreferences.getString("email", "");

                databaseReference.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String adminEmail = Objects.requireNonNull(dataSnapshot.child("emailId").getValue()).toString();
                        String adminPassword = Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString();

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            GMailSender gMailSender = new GMailSender(adminEmail, adminPassword);
                            gMailSender.sendMail(commentsType + " From " + userEmail,
                                    "\n\nUser Email : " + userEmail + "\n" + "Account Type : " + userAccType + "\n"
                                            + "User Name : " + userName + "\n" + "Comments Type : " + commentsType + "\n" + "Comments : " + userMessage + "\n",
                                    adminEmail,
                                    adminEmail);
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ContactUs.this);
                            alertBuilder.setTitle("Email Sent");
                            alertBuilder.setMessage("We have received your " + commentsType + ". Thank you for your time.");
                            alertBuilder.setNegativeButton(
                                    "Okay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finishActivity(123);
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
        });
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
