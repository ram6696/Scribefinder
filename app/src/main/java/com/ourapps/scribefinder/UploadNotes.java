package com.ourapps.scribefinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadNotes extends AppCompatActivity implements View.OnClickListener {

    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    TextView textViewStatus;
    EditText etChoose;
    ProgressBar progressBar;
    private TextInputLayout etFileChooserLayout;

    //the firebase objects for storage and database
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    SharedPreferences sp = null;
    String currentUserId;

    Uri selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notes);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(UploadConstant .DATABASE_PATH_UPLOADS);

        textViewStatus = findViewById(R.id.textViewStatus);
        etFileChooserLayout = findViewById(R.id.etFileChooserLayout);
        etFileChooserLayout.setErrorEnabled(false);
        etChoose = findViewById(R.id.etChoose);
        progressBar = findViewById(R.id.progressbar);

        findViewById(R.id.btnUpload).setOnClickListener(this);

        sp = getSharedPreferences("Login", MODE_PRIVATE);
        currentUserId = sp.getString("uid", "");
    }
    @Override
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(UploadNotes.this);
        super.onResume();
    }

    //this function will get the pdf from the storage
    private void getPDF() {
        if(isStoragePermissionGranted()){
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);
        }
    }

    private boolean isStoragePermissionGranted() {
        if(Build.VERSION.SDK_INT >=23) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getPDF();
        }else{
            Toast.makeText(this, "Permission required to read file from the storage...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                etFileChooserLayout.setErrorEnabled(false);
                Uri returnUri = data.getData();
                @SuppressLint("Recycle") Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                int nameIndex = 0;
                if (returnCursor != null) {
                    nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();
                    etChoose.setText(returnCursor.getString(nameIndex));
                    etChoose.setError(null);
                    etChoose.clearFocus();
                    selectedFile = data.getData();
                }
            }else{
                etFileChooserLayout.setErrorEnabled(true);
                etFileChooserLayout.setError("Please select a file to upload");
                requestFocus(etChoose);
            }
        }
    }

    public void uploadFile() {
        if (selectedFile!= null) {
            progressBar.setVisibility(View.VISIBLE);
            StorageReference sRef = mStorageReference.child(UploadConstant .STORAGE_PATH_UPLOADS + etChoose.getText());
            sRef.putFile(selectedFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            textViewStatus.setText("File Uploaded Successfully");

                            UploadData upload = new UploadData (etChoose.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                            mDatabaseReference.push().setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                 @SuppressWarnings("VisibleForTests")
                                                 @Override
                                                 public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                     double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                     textViewStatus.setText((int) progress + "% Uploading...");
                                                 }
                                             }
            );
        }else{
            etFileChooserLayout.setErrorEnabled(true);
            etFileChooserLayout.setError("Please select a file to upload");
            requestFocus(etChoose);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnUpload:
                uploadFile();
                break;
        }
    }

    public void etChooseClicked(View view) {
        getPDF();
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
