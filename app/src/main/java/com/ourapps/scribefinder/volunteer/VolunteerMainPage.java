package com.ourapps.scribefinder.volunteer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.ourapps.scribefinder.ContactUs;
import com.ourapps.scribefinder.Instruction;
import com.ourapps.scribefinder.Login;
import com.ourapps.scribefinder.NetworkUtil;
import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.UploadNotes;
import com.ourapps.scribefinder.ViewUploads;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VolunteerMainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CircleImageView volunteerProfilePic;
    private TextView volunteerName, volunteerEmail;
    private ProgressDialog progressDialog;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = VolunteerMainPage.class.getSimpleName();

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseUser mCurrentUser;

    private String currentUserId;
    private String currentUserName;
    private String currentUserEmail;

    boolean doubleBackToExitPressedOnce = false;

    private TextView needyCount;
    private TextView volunteerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_main_page);
        Toolbar toolbar = findViewById(R.id.volunteertoolbar);
        setSupportActionBar(toolbar);

        volunteerCount = findViewById(R.id.Vcount);
        needyCount = findViewById(R.id.Ncount);
        volunteerName = findViewById(R.id.txvolunteerName);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUser  = FirebaseAuth.getInstance().getCurrentUser();

        mDatabaseRef.child(getString(R.string.database_volunteer_parent_reference)).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int finalVolunteerCount = (int) dataSnapshot.getChildrenCount() + 150;
                volunteerCount.setText("" + String.valueOf(finalVolunteerCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        } );

        mDatabaseRef.child(getString(R.string.database_needy_parent_reference)).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int finalNeedyCount = (int) dataSnapshot.getChildrenCount() + 50;
                needyCount.setText("" + String.valueOf(finalNeedyCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        } );

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        volunteerProfilePic =  headerView.findViewById(R.id.volunteerProfilePic);
        volunteerProfilePic.setOnClickListener(VolunteerMainPage.this);

        progressDialog = new ProgressDialog(this);

        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        currentUserName = sp.getString("name", "");
        currentUserEmail = sp.getString("email", "");
        currentUserId = sp.getString("uid", "");

        setDefaultValues();
    }

    @Override
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(VolunteerMainPage.this);
        super.onResume();
    }

    private void setDefaultValues() {

        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCancelable(false);

        mDatabaseRef.child(getString(R.string.database_volunteer_parent_reference)).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    volunteerProfilePic = findViewById(R.id.volunteerProfilePic);
                    volunteerName = findViewById(R.id.txvolunteerName);
                    volunteerEmail = findViewById(R.id.volunteerEmail);
                    String picture = Objects.requireNonNull(dataSnapshot.child("photoUrl").getValue()).toString();
                    if(!(picture.isEmpty()))
                        Picasso.get().load(picture).into(volunteerProfilePic);
                    volunteerName.setText(currentUserName);
                    volunteerEmail.setText(currentUserEmail);
                    progressDialog.dismiss();
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(VolunteerMainPage.this);
                    builder1.setMessage("Please Login again or register");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "OK",
                             new DialogInterface.OnClickListener(){
                                 public void onClick(DialogInterface dialog, int id) {

                                     SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                                     SharedPreferences.Editor editor = sharedPreferences.edit();
                                     editor.remove("uid");
                                     editor.remove("email");
                                     editor.remove("password");
                                     editor.remove("accType");
                                     editor.putBoolean("logStatus", false);
                                     editor.apply();
                                     startActivity(new Intent(VolunteerMainPage.this, Login.class));
                                     finish();
                                 }

                             }
                    );
                    android.app.AlertDialog alert11 = builder1.create();
                    alert11.show();



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.volunteer_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_contactUs) {
            //super.onOptionsItemSelected(item);
            //Intent contactUsIntent = new Intent(VolunteerMainPage.this, ContactUs.class);
            //startActivity(contactUsIntent);
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.volunteerlogout :
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Are you sure you want to logout from the current device..?");
                builder1.setCancelable(false);
                builder1.setPositiveButton(
                        "Logout",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("uid");
                                editor.remove("email");
                                editor.remove("password");
                                editor.remove("accType");
                                editor.putBoolean("logStatus", false);
                                editor.apply();
                                startActivity(new Intent(VolunteerMainPage.this, Login.class));
                                finish();
                            }
                        });
                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                               
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
            case R.id.volunteerdeleteAccount:
                AlertDialog.Builder deleteBuilderVolunteer = new AlertDialog.Builder(this);
                deleteBuilderVolunteer.setMessage("Are you sure you want to DELETE your Account?. Once you delete you cannot undo..!");
                deleteBuilderVolunteer.setCancelable(false);
                deleteBuilderVolunteer.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.setMessage("Deleting Account, Please Wait....");
                                progressDialog.show();
                                progressDialog.setCancelable(false);

                                String uid = mCurrentUser.getUid();
                                final Query volunteerDataRef = mDatabaseRef.child(getString(R.string.database_volunteer_parent_reference)).child(uid);
                                final Query usersDataRef = mDatabaseRef.child(getString(R.string.database_users_parent_reference)).child(uid);

                                mCurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            volunteerDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            usersDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(progressDialog.isShowing()){
                                                                                SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                                                                                SharedPreferences.Editor editor = prefs.edit();
                                                                                editor.remove("uid");
                                                                                editor.remove("email");
                                                                                editor.remove("password");
                                                                                editor.remove("accType");
                                                                                editor.putBoolean("logStatus", false);
                                                                                editor.apply();
                                                                                progressDialog.cancel();
                                                                                Toast.makeText(getApplicationContext(), "Account successfully deleted..!", Toast.LENGTH_LONG).show();

                                                                                startActivity(new Intent(VolunteerMainPage.this, Login.class));
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Error while deleting in Authentication.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });

                deleteBuilderVolunteer.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog deleteAlert = deleteBuilderVolunteer.create();
                deleteAlert.show();
                break;
            case R.id.volunteereditProfile:
                Intent volunteerProfileEditPageIntent= new Intent(VolunteerMainPage.this, VolunteerProfileEdit.class);
                startActivity(volunteerProfileEditPageIntent);
                break;
            case R.id.volunteerchangePassword:
                Intent volunteerPasswordChange= new Intent(VolunteerMainPage.this, VolunteerPasswordChange.class);
                startActivity(volunteerPasswordChange);
                break;
            case R.id.like_us_on_facebook:
                Intent likeUsOnFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pg/Scribe-Finder-327894291336361/posts"));
                startActivity(likeUsOnFacebook);
                break;
            case R.id.shareApp :
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Scribe Finder is a app to help the Visually impaired students to search volunteers for writing their exams.Install the app and register as a visually impaired to search volunteers or register as a volunteer to help visually impaired students,in the following link : https://play.google.com/store/apps/details?id=com.ourapps.scribefinder");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.contactUs:
                Intent contactUsIntent = new Intent(VolunteerMainPage.this, ContactUs.class);
                startActivity(contactUsIntent);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onDPClickVolunteer(View view) {
        boolean flag = checkLocationPermission();
        if(flag){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
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

            progressDialog.setMessage("Setting your profile picture....");
            progressDialog.show();
            progressDialog.setCancelable(false);

            final Uri[] downloadUri = new Uri[1];

            if(!currentUserName.equals("")){
                Uri uri = Uri.fromFile(newFile);
                StorageReference storageReference = mStorageRef.child("dp/"+ currentUserId.concat(currentUserName) +".jpg");
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri[0] = taskSnapshot.getDownloadUrl();
                        mDatabaseRef.child(getString(R.string.database_volunteer_parent_reference)).child(currentUserId).child("photoUrl").setValue(downloadUri[0].toString()).isSuccessful();
                        mDatabaseRef.child(getString(R.string.database_volunteer_parent_reference)).child(currentUserId).child("photoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                            @TargetApi(Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String picture = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                Picasso.get().load(picture).noFade().into(volunteerProfilePic);
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
                        Log.e(TAG, "Failed to upload......");
                    }
                });
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

            boolean fileCreatedFlag = file.createNewFile();
            if(fileCreatedFlag){
                FileOutputStream outputStream = new FileOutputStream(file);
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            }
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
                    onDPClickVolunteer(null);
                } else {
                    Toast.makeText(this, "Permission required to read file from the storage...", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "-----------------------Permission not given");
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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


    public void UploadNotes(View view) {
        Intent i = new Intent(VolunteerMainPage.this, UploadNotes.class);
        startActivity(i);
    }

    public void viewUploads(View view) {
        Intent i = new Intent(VolunteerMainPage.this, ViewUploads.class);
        startActivity(i);
    }

    public void Instructions(View view) {
        Intent i = new Intent(VolunteerMainPage.this,Instruction.class);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {
        if(view == volunteerProfilePic){
            onDPClickVolunteer(view);
        }
    }
}