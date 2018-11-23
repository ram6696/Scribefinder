package com.ourapps.scribefinder.needy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class UploadNeedyCertificateExistingUsers extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = UploadNeedyCertificateExistingUsers.class.getSimpleName();

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    private ImageView certificateImageViewExistingUser;
    private EditText etChoose;
    private TextInputLayout etFileChooserLayout;

    private ProgressDialog progressDialog;

    private String currentUserId;
    private String name;
    private String email;
    private String urlOfUploadedCertificate;
    private Uri photoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_needy_certificate_existing_users);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        certificateImageViewExistingUser = findViewById(R.id.certificateImageViewExistingUser);
        etChoose = findViewById(R.id.etChoose);
        etFileChooserLayout = findViewById(R.id.etFileChooserLayout);

        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        name = sp.getString("name", "");
        email = sp.getString("email", "");
        currentUserId = sp.getString("uid", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkUtil.getConnectivityStatusString(UploadNeedyCertificateExistingUsers.this);
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void etChooseClickedInUploadCertificate(View view) {
        boolean flag = checkStoragePermission();
        if (flag) {
            Intent intentToGetImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGetImage, RESULT_LOAD_IMAGE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public boolean checkStoragePermission() {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            progressDialog.setMessage("Reading your certificate please wait..");
            progressDialog.show();
            progressDialog.setCancelable(false);
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            if (selectedImage != null) {
                cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            } else {
                Log.e(TAG, "Selected image is null");
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
            photoUri = Uri.fromFile(newFile);
            Picasso.get().load(photoUri).into(certificateImageViewExistingUser);
            etChoose.setText(name + "'s Certificate");
            progressDialog.dismiss();
        } else {
            Log.e(TAG, "Image not selected");
        }
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            options.inJustDecodeBounds = true;
            FileInputStream inputStream = new FileInputStream(file);

            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    options.outHeight / scale / 2 >= REQUIRED_SIZE) {
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

    public void uploadCertificate(View view) {
        setProgressBarIndeterminateVisibility(true);
        Log.d(TAG, currentUserId);
        if (currentUserId != null && !currentUserId.isEmpty()) {

            if (photoUri != null) {
                progressDialog.setMessage("Uploading certificate please wait a moment..");
                progressDialog.show();
                try {
                    mStorageRef = mStorageRef.child(getString(R.string.storage_needy_certificates_reference) + "/" + name.concat(currentUserId) + ".jpg");
                    mStorageRef.putFile(photoUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        urlOfUploadedCertificate = Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString();
                                    }
                                    mDatabaseRef.child(getString(R.string.database_needy_parent_reference)).child(currentUserId).child("certificateUrl").setValue(urlOfUploadedCertificate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabaseRef.child(getString(R.string.database_needy_parent_reference)).child(currentUserId).child("validUser").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    setProgressBarIndeterminateVisibility(false);
                                                    registeredSuccessfullySendMail();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Log.e(TAG, "Failed to set data for valid user");
                                                    Toast.makeText(UploadNeedyCertificateExistingUsers.this, "An error occurred while uploading, please try again.. ", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Log.e(TAG, "Failed to set data for certificate Url");
                                            Toast.makeText(UploadNeedyCertificateExistingUsers.this, "An error occurred while uploading, please try again.. ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.e(TAG, "Failed to upload Image");
                                    Toast.makeText(UploadNeedyCertificateExistingUsers.this, "An error occurred while uploading, please try again.. ", Toast.LENGTH_SHORT).show();
                                }
                            })
                    ;
                } catch (NullPointerException ne) {
                    Log.e(TAG, ne.getMessage());
                }
            } else {
                etFileChooserLayout.setErrorEnabled(true);
                etFileChooserLayout.setError("Please select a file to upload");
                requestFocus(etChoose);
            }
        } else {
            Log.e(TAG, "Current User is null");
            Toast.makeText(UploadNeedyCertificateExistingUsers.this, "An error occurred while registering, please try again.. ", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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


    private void registeredSuccessfullySendMail() {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            mDatabaseRef.child(getString(R.string.database_admin_parent_reference)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        gMailSender.sendMail("Needy Certificate for Verification(Existing User)",
                                "\n\nPlease find below the details of the user." +
                                        "\n\n\nName : " + name +
                                        "\n\nEmail ID: " + email +
                                        "\n\nNeedy ID : " + currentUserId +
                                        "\n\nCertificate URL : " + urlOfUploadedCertificate +
                                        "\n\nVerify as soon as possible." +
                                        "\n\nThanks & Regards,\nScribe Finder Team",
                                senderEmailId,
                                senderEmailId);

                        AlertDialog.Builder certificateUpload = new AlertDialog.Builder(UploadNeedyCertificateExistingUsers.this);
                        certificateUpload.setMessage("Successfully uploaded your certificate & verification process as been initiated, Please check the status after 15 minutes.");
                        certificateUpload.setCancelable(false);
                        certificateUpload.setNegativeButton(
                                "Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        finish();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            finishAndRemoveTask();
                                        }
                                    }
                                });
                        AlertDialog deleteAlert = certificateUpload.create();
                        progressDialog.dismiss();
                        deleteAlert.show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error while sending Certificate validation mail");
                        Log.e(TAG, e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

}
