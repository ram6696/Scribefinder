package com.ourapps.scribefinder.needy;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ourapps.scribefinder.GMailSender;
import com.ourapps.scribefinder.NetworkUtil;
import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.Users;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class UploadNeedyCertificate extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = UploadNeedyCertificate.class.getSimpleName();

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;

    private ImageView certificateImageView;
    private EditText etChoose;
    private Button btnRegister;

    private ProgressDialog progressDialog;

    private String currentUserId;
    private String name;
    private String email;
    private String mobileNumber;
    private String password;
    private String urlOfUploadedCertificate;
    private boolean certificateUploadStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_needy_certificate);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        certificateImageView = findViewById(R.id.certificateImageView);
        etChoose = findViewById(R.id.etChoose);
        btnRegister = findViewById(R.id.btnRegisterUploadNeedyCertificate);

        Log.i(TAG, "Fetching all entered data from previous needy page");
        currentUserId = getIntent().getStringExtra("currentUserId");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        mobileNumber = getIntent().getStringExtra("mobileNumber");
        password = getIntent().getStringExtra("password");
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkUtil.getConnectivityStatusString(UploadNeedyCertificate.this);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        finishActivity(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void etChooseClickedInUploadCertificate(View view) {
        boolean flag = checkStoragePermission();
        if(flag){
            Intent intentToGetImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGetImage, RESULT_LOAD_IMAGE);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            progressDialog.setMessage("Uploading your certificate please wait..");
            progressDialog.show();
            progressDialog.setCancelable(false);
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            if (selectedImage != null) {
                cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            } else {
                Log.e(TAG, "Image not selected");
                Toast.makeText(UploadNeedyCertificate.this, "Image not selected", Toast.LENGTH_LONG).show();
            }
            int columnIndex;
            File newFile = null;
            if (cursor != null) {
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                newFile = saveBitmapToFile(new File(picturePath));
            }
            final Uri[] downloadUri = new Uri[1];
            try {
                Uri uri = Uri.fromFile(newFile);
                if (currentUserId != null) {
                    mStorageRef = mStorageRef.child("NeedyCertificates/" + name.concat(currentUserId) + ".jpg");
                    mStorageRef.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    downloadUri[0] = taskSnapshot.getDownloadUrl();
                                    if (downloadUri[0] != null) {
                                        etChoose.clearFocus();
                                        etChoose.setText(name.concat("'s Certificate"));
                                        Picasso.get().load(downloadUri[0]).into(certificateImageView);
                                        urlOfUploadedCertificate = downloadUri[0].toString();
                                        certificateUploadStatus = true;
                                        btnRegister.setText(getText(R.string.register));
                                        progressDialog.dismiss();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.e(TAG, "Failed to upload Image");
                                    Toast.makeText(UploadNeedyCertificate.this, "An error occurred while uploading, please try again.. ", Toast.LENGTH_SHORT).show();
                                }
                            })
                    ;
                }
            } catch (NullPointerException ne) {
                Log.e(TAG, ne.getMessage());
            }
        }
    }

    public boolean checkStoragePermission() {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void storeNeedyDataToDatabase() {
        if (currentUserId != null && !currentUserId.isEmpty()) {
            progressDialog.setMessage("Registering user please wait a moment..");
            progressDialog.show();
            Users currUser = new Users(currentUserId, email, password, "Needy", name, mobileNumber);
            mDatabaseRef.child(getString(R.string.databaseUsersParentReference)).child(currentUserId).setValue(currUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            NeedyData needyData = new NeedyData(currentUserId, name, email, mobileNumber, password, "Needy", "", urlOfUploadedCertificate, false);
                            mDatabaseRef.child(getString(R.string.databaseNeedyParentReference)).child(currentUserId).setValue(needyData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sendEmailVerification();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Failed to upload data into the Needy Table");
                                            Toast.makeText(UploadNeedyCertificate.this, "An error occurred while registering, please try again.. ", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            ;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to upload data into the Users Table");
                            Toast.makeText(UploadNeedyCertificate.this, "An error occurred while registering, please try again.. ", Toast.LENGTH_SHORT).show();
                        }
                    })
            ;
        } else {
            Log.e(TAG, "Current User is null");
            Toast.makeText(UploadNeedyCertificate.this, "An error occurred while registering, please try again.. ", Toast.LENGTH_SHORT).show();
        }
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);

            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=100;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            if (file.createNewFile()) {
                FileOutputStream outputStream = new FileOutputStream(file);
                if (selectedBitmap != null) {
                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
            }
            return file;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    etChooseClickedInUploadCertificate(null);
                } else {
                    // permission denied
                    Log.e(TAG, "Permission Not provided..");
                }
            }
            default:
                Log.e(TAG, "Default code - " + requestCode);
        }
    }

    private void sendEmailVerification(){
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            mCurrentUser.sendEmailVerification()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFirebaseAuth.signOut();
                            registeredSuccessfully();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Unable to send verification mail");
                    Toast.makeText(UploadNeedyCertificate.this, "An error occurred while registering, please try again.. ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Current User is null, Unable to send mail");
            Toast.makeText(UploadNeedyCertificate.this, "An error occurred while registering, please try again.. ", Toast.LENGTH_SHORT).show();
        }
    }

    private void registeredSuccessfully() {
        if(certificateUploadStatus){
            mDatabaseRef.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
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
                        gMailSender.sendMail("Needy Certificate for Verification(New User)",
                                "\n\nPlease find below the details of the user." +
                                        "\n\n\nName : " + name +
                                        "\n\nEmail ID: " + email +
                                        "\n\nNeedy ID : " + currentUserId +
                                        "\n\nCertificate URL : " + urlOfUploadedCertificate +
                                        "\n\nVerify as soon as possible." +
                                        "\n\nThanks & Regards,\nScribe Finder Team",
                                senderEmailId,
                                senderEmailId);
                        progressDialog.dismiss();
                        startActivity(new Intent(UploadNeedyCertificate.this, RegistrationSuccessPage.class));
                        finish();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error while sending Certificate validation mail");
                        Log.e(TAG, e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else{
            progressDialog.dismiss();
            startActivity(new Intent(UploadNeedyCertificate.this, RegistrationSuccessPage.class));
            finish();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            }
            Log.i(TAG, "Certificate not uploaded so not initiating verification process");
        }
    }

    public void registerNeedyAfterPhotoUploaded(View view) {
        storeNeedyDataToDatabase();
    }
}
