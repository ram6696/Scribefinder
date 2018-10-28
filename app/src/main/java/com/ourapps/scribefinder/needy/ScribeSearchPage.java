package com.ourapps.scribefinder.needy;

import android.Manifest;
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
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ourapps.scribefinder.NetworkUtil;
import com.ourapps.scribefinder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScribeSearchPage extends AppCompatActivity implements View.OnClickListener {
    private Spinner districtSpinner, stateSpinner, citySpinner;
    private Button btnSearch, btnNearMe;
    private ProgressDialog progressDialog;
    private String citySpinnerItems[] = null;
    private String districtSpinnerItems[] = null;
    private List<String> languagesKnownToWrite = new ArrayList<>();
    private Boolean flag = false;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribe_search_page);

        citySpinner = findViewById(R.id.CitySpinner);
        districtSpinner = findViewById(R.id.DistrictSpinner);
        stateSpinner = findViewById(R.id.StateSpinner);
        btnSearch = findViewById(R.id.btnSearch);
        btnNearMe = findViewById(R.id.btnNearMe);

        progressDialog = new ProgressDialog(this);

        ArrayAdapter<CharSequence> statesListAdapter = ArrayAdapter.createFromResource(this, R.array.india_states, android.R.layout.simple_spinner_item);
        statesListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(statesListAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setValuesForState(stateSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setValuesForCity(districtSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSearch.setOnClickListener(this);
        btnNearMe.setOnClickListener(this);
    }
    @Override
    public void onResume() {
        NetworkUtil.getConnectivityStatusString(ScribeSearchPage.this);

        super.onResume();


    }


    private void setValuesForCity(String district) {
        switch (district) {
            case "Nicobar":
                citySpinnerItems = new String[]{"Nicobar"};
                break;
            case "North & Middle Andaman":
                citySpinnerItems = new String[]{"Bakultala"};
                break;
            case "South Andaman":
                citySpinnerItems = new String[]{"Bambooflat", "Garacharma", "Port Blair", "Prothrapur"};
                break;
            case "Anantapur":
                citySpinnerItems = new String[]{"Ananthapur", "Dhamavaram", "Gooty", "Gorantla", "Guntakal", "Hindupur", "Kadiri", "Kalyandurgam", "Madakasira", "Nallamada", "Penukonda", "Rayadurgam", "Singanambala", "Tadpatri", "Uravakonda"};
                break;
            case "Chittoor":
                citySpinnerItems = new String[]{"Chandragiri", "Chittoor", "Kuppam", "Madanapalli", "Nagari", "Palmaner", "Pileru", "Punganur", "Puttur", "Satyavedu", "Sri Kalahasthu", "Thamballapalle", "Tirupathi", "Vayalpad", "Vepanjeri"};
                break;

            //Districts of karnataka

            case "Bagalkot":
                citySpinnerItems = new String[]{"Badami", "Bagalkot", "Bilgi", "Guledguddu", "Hungund", "Likal", "Jamkhandi", "Kerur", "Mahalingpur", "Mudhol", "Rabgavi Banhatti", "Terdal"};
                break;
            case "Bangalore Urban":
                citySpinnerItems = new String[]{"A F Station Yelahanka ","Adugodi","Agara","Agram","Amruthahalli ","Anandnagar","Anekal","Ashoknagar","Attibele","Attur","Austin Town ","Bagalgunte ","Bagalur","Banashankari","Banaswadi","Bandikodigehalli","Bannerghatta","Basavanagudi","Basaveshwaranagar","Begur","Bellandur","Benson Town","Bestamaranahalli","Bettahalsur","Bilekahalli","Bommanahalli","Bommasandra","BSF Campus Yelahanka","Byatarayanapura ","C.V.Raman Nagar","Chamarajasagara","Chamrajpet","Chandapura","Chickpet","carmelram","Chikkabidarkal","Chandra Layout","Chikkabettahalli","Chikkajala","Chunchanakuppe","Doddagubbi","Doddanekkundi","Devanagundi","Devasandra","Domlur","Doorvaninagar","Doorvaninagar","Deepanjalinagar","Doddakallasandra","Dasarahalli","Dommasandra","Dasanapura","Doddajala","Deshapande Guttahalli","Electronics City","G.K.V.K","Gunjur","Gavipuram","Girinagar","Gottigere","Gayathrinagar","Guddanahalli","Hebbal Kempapura","Hulsur","Hoodi","Horamavu","Haragadde","Hulimavu","HSR Layout","Hebbagodi","Hongasandra","Hulimangala","Hulimangala","Hennagara","Hosakerehalli","Hampinagar","Herohalli","Handenalli","Hunasamaranahalli","Indiranagar","Indalavadi" ,"J.C.Nagar","Jakkur","Jeevabhimanagar","Jalavayuvihar","Jayanagar","JP Nagar","Jigani","Jalahalli","Kadugodi Extension","Kannamangala","Kothanur","Kanakapura","Krishnarajapuram","Kacharakanahalli","Kadugodi","Kundalahalli","Kalyananagar","Kodigehalli","Konanakunte","Kumaraswamy Layout","Kormanagala","Kumbalagodu","Kallubalu","Kathriguppe","Kenchanahalli","Kamakshipalya","K.G.Road","Kadabagere","Kugur","Kannur","Mundur","Mahadevapura","Muthusandra","Marathahalli Colony","Medimallasandra","Museum Road","Mico Layout","Mount St Joseph","Muthanallur","Madhavan Park","Madivala","Mallathahalli","Mavalli","Magadi Road","Milk Colony","Mahalakshmipuram Layout","Malleswaram","Mathikere","Marsur","Madanayakanahalli","Mayasandra" ,"Nagawara","Nayandahalli","Nagarbhavi","Nagasandra","Neriga","Neralur","Rajbavan","R T Nagar","Rajanakunte","Ramamurthy Nagar","Richmond Town","Rameshnagar","Rajarajeshwarinagar","Ramohalli","Ragihalli","Rajajinagar","Sahakaranagar ","St. Thomas Town","Sadashivanagar","Singanayakanahalli","Sri Jayachamarajendra Road","Shanthinagar","Seshadripuram","Srirampuram","Sarjapura","Samandur","Sidihoskote","Vartur","Vasanthanagar","Vidhana Soudha","Vimanapura","Venkateshapura","Virgonagar","Viveknagar","Vidyaranyapura","Viswaneedam","Vijayanagar","Vanakanahalli","Vidyanagar","Varthur","Thalaghattapura","Tilaknagar","Tyagrajnagar","Tataguni","Tavarekere","Whitefield","Wilson Garden","Whitefield","Wipro Limited","Yalahanka","yemalur","Yelachenahalli","Yeshwanthpura","yedavanahalli"};
                break;
            case "Bangalore Rural" :
                citySpinnerItems = new String[]{"Adarangi","Alurduddanahalli","Alluru","Anjanapura","Anneshwar"};
                break;
            case "belgaum":
                citySpinnerItems = new String[]{"Athni", "Bail Hongal", "belgaum", "Chikodi", "Gokak", "Hindalgi", "Hukeri", "Kangrali", "Khanapur", "Konnur", "Kudchi", "Londa", "Mudalgi", "Nipani", "Ramdurg", "Raybag", "Sadalgi", "Sankeshwar", "Saundatti-Yellamma"};
                break;
            case "Bellary":
                citySpinnerItems = new String[]{"Bellary", "Donimallai Township", "Hagaribommanahalli", "Hoovina Hadagalii"};
                break;
        }

        ArrayAdapter<String> cityListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citySpinnerItems);
        cityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityListAdapter);

    }

    private void setValuesForState(String selectedItem) {
        switch (selectedItem) {
            case "Andaman and Nicobar Islands":
                districtSpinnerItems = new String[]{"Nicobar", "North & Middle Andaman", "South Andaman"};
                break;
            case "Andhra Pradesh":
                districtSpinnerItems = new String[]{"Anantapur", "Chittoor", "East Godavari", "Guntur", "Kadapa", "Krishna", "Kurnool", "Nellore", "Prakasam", "Srikakulam", "Visakhapatnam", "Vizianagaram", "West Godavari"};
                break;
            case "Arunachal Pradesh":
                districtSpinnerItems = new String[]{"Anjaw", "Central Siang", "Changlang", "Dibang Valley", "East Kameng", "East Siang", "Kra Daadi", "Kurung Kumey", "Lohit", "Longding", "Lower Dibang Valley", "Lower Siang", "Lower Subansiri", "Namsai", "Papum Pare", "Tawang", "Tirap", "Upper Siang", "Upper Subansiri", "West Kameng", "West Siang"};
                break;
            case "Assam":
                districtSpinnerItems = new String[]{"Baksa", "Barpeta", "Biswanath", "Bongaigaon", "Cachar", "Charaideo", "Chirang", "Darrang", "Dhemaji", "Dhubri", "Dibrugarh", "Dima Hasao", "Goalpara", "Golaghat", "Hailakandi", "Hojai", "Jorhat", "Kamrup", "Kamrup Metropolitan", "Karbi Anglong", "Karimganj", "Kokrajhar", "Lakhimpur", "Majuli", "Morigaon", "Nagaon", "Nalbari", "Sivasagar", "Sonitpur", "South Salmara-Mankachar", "Tinsukia", "Udalguri", "West Karbi Anglong"};
                break;
            case "Bihar":
                districtSpinnerItems = new String[]{"Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur", "Bhojpur", "Buxar", "Darbhanga", "East Champaran", "Gaya", "Gopalganj", "Jamui", "Jehanabad", "Kaimur", "Katihar", "Khagaria", "Kishanganj", "Lakhisarai", "Madhepura", "Madhubani", "Munger", "Muzaffarpur", "Nalanda", "Nawada", "Patna", "Purnia", "Rohtas", "Saharsa", "Samastipur", "Saran", "Sheikhpura", "Sheohar", "Sitamarhi", "Siwan", "Supaul", "Vaishali", "West Champaran"};
                break;
            case "Chandigarh":
                districtSpinnerItems = new String[]{"Chandigarh"};
                break;
            case "Chhattisgarh":
                districtSpinnerItems = new String[]{"Balod", "Baloda Bazar", "Balrampur", "Bastar", "Bemetara", "Bijapur", "Bilaspur", "Dantewada", "Dhamtari", "Durg", "Gariaband", "Janjgir Champa", "Jashpur", "Kabirdham", "Kanker", "Kondagaon", "Korba", "Koriya", "Mahasamund", "Mungeli", "Narayanpur", "Raigarh", "Raipur", "Rajnandgaon", "Sukma", "Surajpur", "Surguja"};
                break;
            case "Dadra and Nagar Haveli":
                districtSpinnerItems = new String[]{"Dadra and Nagar Haveli", "Silvasa"};
                break;
            case "Daman and Diu":
                districtSpinnerItems = new String[]{"Daman", "Diu"};
                break;
            case "Delhi":
                districtSpinnerItems = new String[]{"Central Delhi", "East Delhi", "New Delhi", "North Delhi", "North East Delhi", "North West Delhi", "Shahdara", "South Delhi", "South East Delhi", "South West Delhi ", "West Delhi "};
                break;
            case "Goa":
                districtSpinnerItems = new String[]{"Goa", "Panaji"};
                break;
            case "Gujarat":
                districtSpinnerItems = new String[]{"Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch", "Bhavnagar", "Botad", "Chhota Udaipur", "Dahod", "Dang", "Devbhoomi Dwarka", "Gandhinagar", "Gir Somnath", "Jamnagar", "Junagadh", "Kheda", "Kutch", "Mahisagar", "Mehsana", "Morbi", "Narmada", "Navsari", "Panchmahal", "Patan", "Porbandar", "Rajkot", "Sabarkantha", "Surat", "Surendranagar", "Tapi", "Vadodara", "Valsad"};
                break;
            case "Haryana":
                districtSpinnerItems = new String[]{"Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram", "Hisar", "Jhajjar", "Jind", "Kaithal", "Karnal", "Kurukshetra", "Mahendragarh", "Mewat", "Palwal", "Panchkula", "Panipat", "Rewari", "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"};
                break;
            case "Himachal Pradesh":
                districtSpinnerItems = new String[]{"Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kinnaur", "Kullu", "Lahaul Spiti", "Mandi", "Shimla", "Sirmaur", "Solan", "Una"};
                break;
            case "Jammu and Kashmi":
                districtSpinnerItems = new String[]{"Anantnag", "Bandipora", "Baramulla", "Budgam", "Doda", "Ganderbal", "Jammu", "Kargil", "Kathua", "Kishtwar", "Kulgam", "Kupwara", "Leh", "Poonch", "Pulwama", "Rajouri", "Ramban", "Reasi", "Samba", "Shopian", "Srinagar", "Udhampur"};
                break;
            case "Jharkhand":
                districtSpinnerItems = new String[]{"Bokaro", "Chatra", "Deoghar", "Dhanbad", "Dumka", "East Singhbhum", "Garhwa", "Giridih", "Godda", "Gumla", "Hazaribagh", "Jamtara", "Khunti", "Koderma", "Latehar", "Lohardaga", "Pakur", "Palamu", "Ramgarh", "Ranchi", "Sahebganj", "Seraikela Kharsawan", "Simdega", "West Singhbhum"};
                break;
            case "Karnataka":
                districtSpinnerItems = new String[]{"Bagalkot", "Bangalore Urban","Bangalore Rural", "Belgaum", "Bellary", "Bidar", "Bijapur", "Chamarajanagar", "Chikkaballapur", "Chikkamagaluru", "Chitradurga", "Dakshina Kannada", "Davanagere", "Dharwad", "Gadag", "Gulbarga", "Hassan", "Haveri", "Kodagu", "Kolar", "Koppal", "Mandya", "Mysore", "Raichur", "Ramanagara", "Shimoga", "Tumkur", "Udupi", "Uttara Kannada", "Yadgir"};
                break;
            case "Kerala":
                districtSpinnerItems = new String[]{"Alappuzha", "Ernakulam", "Idukki", "Kannur", "Kasaragod", "Kollam", "Kottayam", "Kozhikode" + "Malappuram", "Palakkad", "Pathanamthitta", "Thiruvananthapuram", "Thrissur", "Wayanad"};
                break;
            case "Lakshadweep":
                districtSpinnerItems = new String[]{"Lakshadweep"};
                break;
            case "Madhya Pradesh":
                districtSpinnerItems = new String[]{"Agar Malwa", "Alirajpur", "Anuppur", "Ashoknagar", "Balaghat", "Barwani", "Betul", "Bhind", "Bhopal", "Burhanpur", "Chhatarpur", "Chhindwara", "Damoh", "Datia", "Dewas", "Dhar", "Dindori", "Guna", "Gwalior", "Harda", "Hoshangabad", "Indore", "Jabalpur", "Jhabua", "Katni", "Khandwa", "Khargone", "Mandla", "Mandsaur", "Morena", "Narsinghpur", "Neemuch", "Panna", "Raisen", "Rajgarh", "Ratlam", "Rewa", "Sagar", "Satna", "Sehore", "Seoni", "Shahdol", "Shajapur", "Sheopur", "Shivpuri", "Sidhi", "Singrauli", "Tikamgarh", "Ujjain", "Umaria", "Vidisha"};
                break;
            case "Maharashtra":
                districtSpinnerItems = new String[]{"Ahmednagar", "Akola", "Amravati", "Aurangabad", "Beed", "Bhandara", "Buldhana", "Chandrapur", "Dhule", "Gadchiroli", "Gondia", "Hingoli", "Jalgaon", "Jalna", "Kolhapur", "Latur", "Mumbai City", "Mumbai Suburban", "Nagpur", "Nanded", "Nandurbar", "Nashik", "Osmanabad", "Palghar", "Parbhani", "Pune", "Raigad", "Ratnagiri", "Sangli", "Satara", "Sindhudurg", "Solapur", "Thane", "Wardha", "Washim", "Yavatmal"};
                break;
            case "Manipur":
                districtSpinnerItems = new String[]{"Bishnupur", "Chandel", "Churachandpur", "Imphal East", "Imphal West", "Jiribam", "Kakching", "Kamjong", "Kangpokpi", "Noney", "Pherzawl", "Senapati", "Tamenglong", "Tengnoupal", "Thoubal", "Ukhrul"};
                break;
        }

        ArrayAdapter<String> districtListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districtSpinnerItems);
        districtListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtListAdapter);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view == btnSearch) {
            searchForScribes(false);
        } else if (view == btnNearMe) {
            searchForScribes(true);
        }
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.chEnglish:
                if (checked)
                    languagesKnownToWrite.add("English");
                else {
                    if (languagesKnownToWrite.contains("English")) {
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("English"));
                    }
                }
                break;
            case R.id.chHindi:
                if (checked)
                    languagesKnownToWrite.add("Hindi");
                else {
                    if (languagesKnownToWrite.contains("Hindi")) {
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Hindi"));
                    }
                }
                break;
            case R.id.chKannada:
                if (checked)
                    languagesKnownToWrite.add("Kannada");
                else {
                    if (languagesKnownToWrite.contains("Kannada")) {
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Kannada"));
                    }
                }
                break;
            case R.id.chTamil:
                if (checked)
                    languagesKnownToWrite.add("Tamil");
                else {
                    if (languagesKnownToWrite.contains("Tamil")) {
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Tamil"));
                    }
                }
                break;
            case R.id.chTelugu:
                if (checked)
                    languagesKnownToWrite.add("Telugu");
                else {
                    if (languagesKnownToWrite.contains("Telugu")) {
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Telugu"));
                    }
                }
                break;
        }
    }

    private void searchForScribes(boolean nearMeValidator) {
        progressDialog.setMessage("Searching for Volunteers..");
        progressDialog.show();

        if (nearMeValidator) {
            searchForScribesNearMe("your location");
        } else {
            final String city = citySpinner.getSelectedItem().toString().trim();
            final String district = districtSpinner.getSelectedItem().toString();
            final String state = stateSpinner.getSelectedItem().toString();
            searchAndSetValue("filterAddress", state+district+city);
        }
    }

    public void searchForScribesNearMe(String searchLocation) {

        flag = checkLocationPermission();

        if(flag){
            gps = new GPSTracker(ScribeSearchPage.this);
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
                        if(searchLocation.contentEquals("your location")){
                            locName = addresses.get(0).getPostalCode();
                            locType = "pincode";
                           // Toast.makeText(getApplicationContext(), "Current location pincode is "+locName, Toast.LENGTH_LONG).show();

                        }else if(searchLocation.contentEquals("district")){
                            locName = addresses.get(0).getSubAdminArea();
                            locType = "district";
                            //Toast.makeText(getApplicationContext(), "Current location district is "+locName, Toast.LENGTH_LONG).show();
                        }else if(searchLocation.contentEquals("state")){
                            locName = addresses.get(0).getAdminArea();
                            locType = "state";
                           // Toast.makeText(getApplicationContext(), "Current location state is "+locName, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(locName == null){
                    //searchAndSetValue(locType, locName);
                    Toast.makeText(getApplicationContext(), "Unable to get location. Please turn off and turn on the GPS Manually and try again.", Toast.LENGTH_LONG).show();
                }else{
                    searchAndSetValue(locType, locName);
                }
            } else {
                progressDialog.dismiss();
                gps.showSettingsAlert();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            progressDialog.dismiss();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void searchAndSetValue(final String locType, String locName) {

        final String filterString = locName;

        final ArrayList<String> filteredIds = new ArrayList<String>();
        final Intent displayPage = new Intent(ScribeSearchPage.this, DisplaySearchedVolunteers.class);

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
                }else{
                    if(locType.contentEquals("pincode")){
                        showErrorDialog("your location", "district", filterString);
                    }else if(locType.contentEquals("district")){
                        showErrorDialog("district", "state", filterString);
                    }else if(locType.contentEquals("state")){
                        showErrorDialog("state", "country", filterString);
                    }else if(locType.contentEquals("country")){
                        showErrorDialog("country", "filterAddress", filterString);
                    }else{
                        showErrorDialog("filterAddress", "filterAddress", filterString);
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

        if(!false){
            if(oldLoc.contentEquals("your location")){
                dialogTitle = "Unable to find Scribes";
                dialogMessage = "There are no scribes registered in your location, Would you like to search in your whole District..?";
                dialogOkTitle = "Search in my District";
                dialogCancelTitle = "Cancel";
            }else if(oldLoc.contentEquals("district")){
                dialogTitle = "Unable to find Scribes";
                dialogMessage = "There are no scribes registered in "+filterString+", Would you like to search in your whole State..?";
                dialogOkTitle = "Search in my State";
                dialogCancelTitle = "Cancel";
            }else if(oldLoc.contentEquals("state")){
                dialogTitle = "Unable to find Scribes";
                dialogMessage = "Sorry.. There are no scribes registered in "+filterString+".";
                dialogOkTitle = "Okay";
                dialogCancelTitle = "Cancel";
            }else{
                dialogTitle = "Unable to find Scribes";
                dialogMessage = "There are no scribes found for your search criteria...";
                dialogOkTitle = "Okay";
                dialogCancelTitle = "Cancel";
            }
        }else{
            dialogTitle = "Error";
            dialogMessage = "Some error occured?";
            dialogOkTitle = "Search ";
            dialogCancelTitle = "Cancel";
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScribeSearchPage.this);
        alertDialog.setTitle(dialogTitle);
        alertDialog.setMessage(dialogMessage);

        // On pressing the ok button.
        alertDialog.setPositiveButton(dialogOkTitle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                if(!newLoc.contentEquals("filterAddress")){
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

    public boolean checkLocationPermission() {
        int res = this.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            searchForScribes(true);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(ScribeSearchPage.this, NeedyMainPage.class));
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        startActivity(new Intent(ScribeSearchPage.this, NeedyMainPage.class));
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

