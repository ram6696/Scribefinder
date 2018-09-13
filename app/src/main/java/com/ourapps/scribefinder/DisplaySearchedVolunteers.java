package com.ourapps.scribefinder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DisplaySearchedVolunteers extends AppCompatActivity {

    private Button btnFindMore;

    List<String> volunteersName = new ArrayList<>();
    List<String> volunteersCity = new ArrayList<>();
    List<String> volunteersId = new ArrayList<>();
    List<String> volunteersProfilePicUrl = new ArrayList<>();

    private Boolean flag = false;

    GPSTracker gps;
    ListView list;
    private ProgressDialog progressDialog;

    DatabaseReference rootRef;

    private ArrayList<String> filteredIds;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_searched_volunteers);

        progressDialog = new ProgressDialog(this);

        final String curLocType = getIntent().getStringExtra("locType");

        btnFindMore = findViewById(R.id.btnFindMore);
        btnFindMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curLocType.contentEquals("pincode")){
                    progressDialog.setMessage("Searching for more volunteers in your whole District.");
                    progressDialog.setCancelable(false);
                    searchForScribesNearMe("district");
                }else if(curLocType.contentEquals("district")){
                    progressDialog.setMessage("Searching for more volunteers in your whole State.");
                    progressDialog.setCancelable(false);
                    searchForScribesNearMe("state");
                } else {
                    progressDialog.setMessage("Searching for more volunteers in your whole Country.");
                    progressDialog.setCancelable(false);
                    searchForScribesNearMe("country");
                }
            }
        });

        rootRef = FirebaseDatabase.getInstance().getReference();
        list = findViewById(R.id.volunteersFoundList);
        filteredIds = getIntent().getStringArrayListExtra("volunteersList");

        progressDialog.setMessage("Please wait a moment..");
        progressDialog.setCancelable(false);
        displayVolunteersList(filteredIds);
    }

    private void displayVolunteersList(final ArrayList<String> filteredIds) {

        list.setTextFilterEnabled(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Intent displayPage = new Intent(DisplaySearchedVolunteers.this, DisplaySingleScribeDetails.class);
                displayPage.putExtra("name", volunteersName.get(position));
                displayPage.putExtra("id", volunteersId.get(position));
                startActivity(displayPage);
            }
        });

        for(int i = 0; i < filteredIds.size(); i++){
            DatabaseReference idReference = rootRef.child("Volunteer").child(filteredIds.get(i));
            final int finalI = i;
            idReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        VolunteerData volunteerData = dataSnapshot.getValue(VolunteerData.class);
                        assert volunteerData != null;
                        volunteersName.add(finalI, volunteerData.getName());
                        volunteersCity.add(finalI, volunteerData.getCity());
                        volunteersId.add(finalI, volunteerData.getVolunteerId());
                        volunteersProfilePicUrl.add(finalI, volunteerData.getPhotoUrl());
                    }

                    if(filteredIds.size() == finalI+1){
                        CustomAdapter customAdapter = new CustomAdapter();
                        list.setAdapter(customAdapter);
                        customAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    public void searchForScribesNearMe(String searchLocation) {

        flag = checkLocationPermission();

        if(flag){
            gps = new GPSTracker(DisplaySearchedVolunteers.this);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                String locName = null;
                String locType = null;

                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        if(searchLocation.contentEquals("district")){
                            locName = addresses.get(0).getSubAdminArea();
                            locType = "district";
                            //System.out.println(locName);
                        }else if(searchLocation.contentEquals("state")){
                            locName = addresses.get(0).getAdminArea();
                            locType = "state";
                            //System.out.println(locName);
                        }else if(searchLocation.contentEquals("country")){
                            locName = addresses.get(0).getCountryName();
                            locType = "country";
                            //System.out.println(locName);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(locName == null){
                    Toast.makeText(getApplicationContext(), "Unable to get location. Please turn off and turn on the GPS Manually and try again.", Toast.LENGTH_LONG).show();
                }else{
                    searchAndSetValue(locType, locName);
                }
            } else {
                progressDialog.dismiss();
                gps.showSettingsAlert();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            progressDialog.dismiss();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public boolean checkLocationPermission() {
        int res = this.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            searchForScribesNearMe("district");
        }
    }

    private void searchAndSetValue(final String locType, String locName) {

        final String filterString = locName;

        final ArrayList<String> filteredIds = new ArrayList<String>();
        final Intent displayPage = new Intent(DisplaySearchedVolunteers.this, DisplaySearchedVolunteers.class);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    filteredIds.add(postSnapshot.getKey());
                }
                if(filteredIds.size()>0){
                    displayPage.putExtra("volunteersList", filteredIds);
                    displayPage.putExtra("locType", locType);
                    progressDialog.dismiss();
                    startActivity(displayPage);
                    //displayVolunteersList(filteredIds);
                }else{
                    if(locType.contentEquals("district")){
                        showErrorDialog("district", "state", filterString);
                    }else if(locType.contentEquals("state")){
                        showErrorDialog("state", "country", filterString);
                    }else{
                        showErrorDialog("country", "country", filterString);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Volunteer");
        Query query = reference.orderByChild(locType).equalTo(filterString);
        query.addValueEventListener(valueEventListener);
    }

    private void showErrorDialog(final String oldLoc, final String newLoc, String filterString) {

        String dialogTitle = "", dialogMessage = "", dialogOkTitle = "", dialogCancelTitle = "";
        System.out.println(oldLoc);
        if(oldLoc.contentEquals("district")){
            dialogTitle = "Unable to find Scribes";
            dialogMessage = "There are no scribes registered in "+filterString+", Would you like to search in your whole State..?";
            dialogOkTitle = "Search in my State";
            dialogCancelTitle = "Cancel";
        }else if(oldLoc.contentEquals("state")){
            dialogTitle = "Unable to find Scribes";
            dialogMessage = "There are no scribes registered in "+filterString+", Would you like to search in your whole Country..?";
            dialogOkTitle = "Search in my Country";
            dialogCancelTitle = "Cancel";
        }else{
            dialogTitle = "Unable to find Scribes";
            dialogMessage = "Sorry... we could not find more volunteers for you.";
            dialogOkTitle = "Okay";
            dialogCancelTitle = "Cancel";
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplaySearchedVolunteers.this);
        alertDialog.setTitle(dialogTitle);
        alertDialog.setMessage(dialogMessage);

        // On pressing the ok button.
        alertDialog.setPositiveButton(dialogOkTitle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                if(!newLoc.contentEquals("country")){
                    searchForScribesNearMe(newLoc);
                }else {
                    dialog.cancel();
                }
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton(dialogCancelTitle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        progressDialog.dismiss();

        // Showing Alert Message
        alertDialog.show();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }

    class CustomAdapter extends BaseAdapter{

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return volunteersName.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return null;
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.scribeslistdisplaylayout, null);

            TextView textView = convertView.findViewById(R.id.txtName);
            TextView textView1 = convertView.findViewById(R.id.txtCity);
            ImageView profilePic = convertView.findViewById(R.id.profilePic);

            textView.setText(volunteersName.get(position));
            textView1.setText(volunteersCity.get(position));
            String picture = volunteersProfilePicUrl.get(position);
            if(!(picture.isEmpty()))
                Picasso.get().load(picture).into(profilePic);

            return convertView;
        }
    }

    public class GPSTracker extends Service implements LocationListener {

        private final Context mContext;

        // Flag for GPS status
        boolean isGPSEnabled = false;

        // Flag for network status
        boolean isNetworkEnabled = false;

        // Flag for GPS status
        boolean canGetLocation = false;

        Location location; // Location
        double latitude; // Latitude
        double longitude; // Longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30000; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public GPSTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {

            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                // Getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // Getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGPSEnabled || isNetworkEnabled) {

                    this.canGetLocation = true;

                    flag = checkLocationPermission();

                    if (flag) {
                        if (isNetworkEnabled) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }

                        // If GPS enabled, get latitude/longitude using GPS Services
                        if (isGPSEnabled) {
                            if (location == null) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                                if (locationManager != null) {
                                    location = locationManager
                                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("111111111111111111111111111111111111111111111");
                    }
                } else {
                    System.out.println("000000000000000000000000000000000000000000000000");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app.
         * */
        public void stopUsingGPS(){
            if(locationManager != null){
                locationManager.removeUpdates(GPSTracker.this);
            }
        }

        /**
         * Function to get latitude
         * */
        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */
        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/Wi-Fi enabled
         * @return boolean
         * */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog.
         * On pressing the Settings button it will launch Settings Options.
         * */
        public void showSettingsAlert(){

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS Settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing the Settings button.
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // On pressing the cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location) { }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
    }
}
