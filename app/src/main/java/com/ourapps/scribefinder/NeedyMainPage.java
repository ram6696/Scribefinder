package com.ourapps.scribefinder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class NeedyMainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView needyProfilePic;
    private TextView needyName, needyEmail;
    private ProgressDialog progressDialog;

    public static final int RESULT_LOAD_IMAGE = 1;

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private String currentUserId;
    private String currentUserName;
    private String currentUserEmail;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_main_page);
        Toolbar toolbar = findViewById(R.id.needyToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.needy_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        currentUserName = sp.getString("name", "");
        currentUserEmail = sp.getString("email", "");
        currentUserId = sp.getString("uid", "");

        setDefaultValues();
    }

    private void setDefaultValues() {

        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCancelable(false);

        mDatabase.child("Needy").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                needyProfilePic = findViewById(R.id.needyProfilePic);
                needyName = findViewById(R.id.needyName);
                needyEmail = findViewById(R.id.needyEmail);
                String picture = dataSnapshot.child("photoUrl").getValue().toString();
                if(!(picture.isEmpty()))
                    Picasso.get().load(picture).into(needyProfilePic);
                needyName.setText(currentUserName);
                needyEmail.setText(currentUserEmail);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
                System.out.println(databaseError.toException().getStackTrace());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.needy_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_contactUs){
            Intent contactUsIntent = new Intent(NeedyMainPage.this, ContactUs.class);
            startActivity(contactUsIntent);
        }
        //return  || super.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.needyeditProfile) {
            Intent needyProfileEditPageIntent = new Intent(NeedyMainPage.this, NeedyProfileEdit.class);
            startActivity(needyProfileEditPageIntent);
        } else if (id == R.id.needychangePassword) {
            Intent needyPasswordChange = new Intent(NeedyMainPage.this, NeedyPasswordChange.class);
            startActivity(needyPasswordChange);
        } else if (id == R.id.needylogout) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Are you sure you want to logout from this Device?");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Logout",
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
                            finish();
                            startActivity(new Intent(NeedyMainPage.this, Login.class));
                        }
                    });
            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            startActivity(getIntent());
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }else if (id == R.id.like_us_on_facebook) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pg/EyeDroid-The-Scribe-Finder-1074854976011093/about/?entry_point=page_edit_dialog&tab=page_info"));
            startActivity(intent);
        }
        else if (id == R.id.about_us) {
            Intent volunteerPasswordChange= new Intent(NeedyMainPage.this, ContactUs.class);
            startActivity(volunteerPasswordChange);
        }
        else if(id == R.id.needydeleteAccount){
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
            deleteBuilder.setMessage("Are you sure you want to DELETE your Account?. Once you delete you cannot undo..!");
            deleteBuilder.setCancelable(false);
            deleteBuilder.setPositiveButton(
                    "Delete",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                             FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            final Query data = reference.child("Needy").child(uid);
                            final Query users = reference.child("Users").child(uid);
                            assert user != null;

                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        data.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                    dataSnapshot1.getRef().removeValue();
                                                    SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.remove("uid");
                                                    editor.remove("email");
                                                    editor.remove("password");
                                                    //editor.remove("accType");
                                                    editor.putBoolean("logStatus", false);
                                                    editor.apply();
                                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(NeedyMainPage.this);
                                                    builder1.setMessage("Account has been successfully Deleted.");
                                                    builder1.setCancelable(false);
                                                    builder1.setPositiveButton(
                                                            "Okay",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    finish();
                                                                    startActivity(new Intent(NeedyMainPage.this, Login.class));
                                                                }
                                                            });
                                                    AlertDialog alert11 = builder1.create();
                                                    alert11.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                                                    userSnapshot.getRef().removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }else{
                                        Toast.makeText(getApplicationContext(), "Account could not be Deleted.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });

            deleteBuilder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            startActivity(getIntent());
                            dialog.cancel();
                        }
                    });

            AlertDialog deleteAlert = deleteBuilder.create();
            deleteAlert.show();
        }

        DrawerLayout drawer = findViewById(R.id.needy_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void searchForScribe(View view) {
        Intent NeedyMainPage= new Intent(com.ourapps.scribefinder.NeedyMainPage.this, ScribeSearchPage.class);
        startActivity(NeedyMainPage);
    }

    public void onDPClickNeedy(View view) {
        boolean flag = checkLocationPermission();
        if(flag){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }else{
            System.out.println("Not Okay...... ");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    //saving image to firebase storage and displaying it in imageview
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
            progressDialog.setMessage("Setting your profile picture....");
            progressDialog.show();

            final Uri[] downloadUri = new Uri[1];

            if(!currentUserName.equals("")){
                Uri uri = Uri.fromFile(newFile);
                StorageReference storageReference = mStorageRef.child("dp/"+ currentUserId.concat(currentUserName) +".jpg");
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri[0] = taskSnapshot.getDownloadUrl();
                        boolean flag = mDatabase.child("Needy").child(currentUserId).child("photoUrl").setValue(downloadUri[0].toString()).isSuccessful();
                        mDatabase.child("Needy").child(currentUserId).child("photoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String picture = dataSnapshot.getValue().toString();
                                Picasso.get().load(picture).into(needyProfilePic);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        System.out.println("Failed to upload......");
                    }
                })
                ;
            }
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

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onDPClickNeedy(null);
                } else {
                    // permission denied
                    System.out.println("-----------------------Permission not given");
                }
            }
            default:
                System.out.println("Default code---------"+requestCode);
        }
    }

    public void StudyMaterial(View view) {
        startActivity(new Intent(NeedyMainPage.this, StudyMaterial.class));
        finish();
    }

    public void UploadNotes(View view) {
        Intent i = new Intent(NeedyMainPage.this, UploadNotes.class);
        startActivity(i);
    }

    public void viewUploads(View view) {
        Intent i = new Intent(NeedyMainPage.this, ViewUploadsActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.needy_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(doubleBackToExitPressedOnce) {
                finish();
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
