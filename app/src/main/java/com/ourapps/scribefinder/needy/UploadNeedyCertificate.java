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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ourapps.scribefinder.GMailSender;
import com.ourapps.scribefinder.NetworkUtil;
import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.Users;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class UploadNeedyCertificate extends AppCompatActivity implements View.OnClickListener{

    public static final int RESULT_LOAD_IMAGE = 1;

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mFirebaseAuth;

    private ImageView certificateImageView;
    private TextView textViewStatus;

    private ProgressDialog progressDialog;

    private String currentUserId = null;
    private String name;
    private String email;
    private String mobileNumber;
    private String password;
    private String urlOfUploadedCertificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_needy_certificate);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        certificateImageView = findViewById(R.id.certificateImageView);
        textViewStatus = findViewById(R.id.textViewStatus);

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

    public boolean checkStoragePermission() {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            File newFile = saveBitmapToFile(new File(picturePath));

            final ProgressDialog progressDialog = new ProgressDialog( this);

            final Uri[] downloadUri = new Uri[1];

            if(!name.equals("")){
                Uri uri = Uri.fromFile(newFile);
                mStorageRef = mStorageRef.child("NeedyCertificates/"+ name.concat(currentUserId) +".jpg");
                mStorageRef.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri[0] = taskSnapshot.getDownloadUrl();
                                urlOfUploadedCertificate = downloadUri[0].toString();
                                storeNeedyDataToDatabase();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                System.out.println("Failed to upload Image......");
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @SuppressWarnings("VisibleForTests")
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                textViewStatus.setText((int) progress + "% Uploading...");
                            }
                        })
                ;
            }
        }
    }

    private void storeNeedyDataToDatabase() {
        progressDialog.setMessage("Registering user please wait a moment..");
        progressDialog.show();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            currentUserId = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        }
        if(currentUserId != null && !currentUserId.isEmpty()){
            Users currUser = new Users(currentUserId, email, password, "Needy", name, mobileNumber);
            final String finalCurrentUserId = currentUserId;

            mDatabaseRef.child("Users").child(currentUserId).setValue(currUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                NeedyData needyData = new NeedyData(finalCurrentUserId, name, email, mobileNumber, password, "Needy","", urlOfUploadedCertificate, false);
                                mDatabaseRef.child("Needy").child(finalCurrentUserId).setValue(needyData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                    sendEmailVerification();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("Failed to upload data into the needy database");
                                            }
                                        })
                                ;
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to upload data into the Users database");
                    }
                })
            ;
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

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
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
                    System.out.println("Permission not given.....");
                }
            }
            default:
                System.out.println("Default code---------"+requestCode);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
               // uploadFile();
                System.out.println("Will upload soon");
                break;
        }
    }

    private void sendEmailVerification(){
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                        Toast.makeText(UploadNeedyCertificate.this, "Unable to send Verification mail.", Toast.LENGTH_SHORT).show();
                    }
                })
            ;
        }
    }

    private void registeredSuccessfully() {
        if(sendMailForCertificateVerification()){
            progressDialog.dismiss();
            startActivity(new Intent(this, RegistrationSuccessPage.class));
        }
    }

    private boolean sendMailForCertificateVerification() {

        final boolean[] successFlag = {false};

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
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
                        gMailSender.sendMail("Needy Data for Verification",
                                        ",\n\nNew Needy Registered" +
                                        "\n\n\nName : " + name +
                                        "\nEmail : " + email +
                                        "\nNeedy ID : " +  currentUserId+
                                        "\nCertificate URL : "+urlOfUploadedCertificate+
                                        "\n\nVerify as soon as possible." +
                                        "\n\nThanks,\nEyeDroid Team",
                                senderEmailId,
                                email);
                        successFlag[0] = true;
                    } catch (Exception e) {
                        successFlag[0] = false;
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return successFlag[0];
    }
}
