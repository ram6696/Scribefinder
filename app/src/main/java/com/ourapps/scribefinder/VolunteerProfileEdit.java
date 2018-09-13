package com.ourapps.scribefinder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class VolunteerProfileEdit extends AppCompatActivity implements View.OnClickListener{

    private EditText etName, etEmail, etMobileNumber, etdob, etAddress, etPincode;
    private TextInputLayout etNameLayout, etMobileNumberLayout, etdobLayout, etAddressLayout, etPincodeLayout;
    private RadioGroup genderGroup;
    private RadioButton male_radioButton, female_radioButton;
    private Spinner districtSpinner, stateSpinner, citySpinner;
    private CheckBox chEnglish, chKannada, chTelugu, chHindi, chTamil;
    private Button btnUpdate, btnDatePicker;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;
    SharedPreferences sp = null;
    String currentUserId;
    VolunteerData volunteerData;
    String citySpinnerItems[] =null;
    String districtSpinnerItems[] = null;
    List<String> languagesKnownToWrite = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_profile_edit);

        progressDialog = new ProgressDialog(this);

        etName = findViewById(R.id.etName);
        etName.clearFocus();
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etdob = findViewById(R.id.etDOB);
        etAddress = findViewById(R.id.etAddress);
        etPincode = findViewById(R.id.etPincode);

        etNameLayout = findViewById(R.id.etNameLayout);
        etMobileNumberLayout = findViewById(R.id.etMobileNumberLayout);
        etdobLayout = findViewById(R.id.etDOBLayout);
        etAddressLayout = findViewById(R.id.etAddressLayout);
        etPincodeLayout = findViewById(R.id.etPincodeLayout);

        genderGroup = findViewById(R.id.genderGroup);
        male_radioButton = findViewById(R.id.male_radioButton);
        female_radioButton = findViewById(R.id.female_radioButton);

        citySpinner = findViewById(R.id.CitySpinner);
        districtSpinner = findViewById(R.id.DistrictSpinner);
        stateSpinner = findViewById(R.id.StateSpinner);

        chEnglish = findViewById(R.id.chEnglish);
        chKannada = findViewById(R.id.chKannada);
        chHindi = findViewById(R.id.chHindi);
        chTelugu = findViewById(R.id.chTelugu);
        chTamil = findViewById(R.id.chTamil);

        btnUpdate = findViewById(R.id.btnEdit);
        btnDatePicker = findViewById(R.id.btnDatePicker);

        btnUpdate.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        sp = getSharedPreferences("Login", MODE_PRIVATE);
        currentUserId = sp.getString("uid", "");

        ArrayAdapter<CharSequence> statesListAdapter = ArrayAdapter.createFromResource(this, R.array.india_states, android.R.layout.simple_spinner_item);
        statesListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(statesListAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setValuesForDistrict(stateSpinner.getSelectedItem().toString());
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

        setPreviousValues();
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
            case "Bagalkot":
                citySpinnerItems = new String[]{"Badami", "Bagalkot", "Bilgi", "Guledguddu", "Hungund", "Likal", "Jamkhandi", "Kerur", "Mahalingpur", "Mudhol", "Rabgavi Banhatti", "Terdal"};
                break;
            case "Bangalore Urban":
                citySpinnerItems = new String[]{"A F Station Yelahanka ","Adugodi","Agara","Agram","Amruthahalli ","Anandnagar","Anekal","Ashoknagar","Attibele","Attur","Austin Town ","Bagalgunte ","Bagalur","Banashankari","Banaswadi","Bandikodigehalli","Bannerghatta","Basavanagudi","Basaveshwaranagar","Begur","Bellandur","Benson Town","Bestamaranahalli","Bettahalsur","Bilekahalli","Bommanahalli","Bommasandra","BSF Campus Yelahanka","Byatarayanapura ","C.V.Raman Nagar","Chamarajasagara","Chamrajpet","Chandapura","Chickpet","carmelram","Chikkabidarkal","Chandra Layout","Chikkabettahalli","Chikkajala","Chunchanakuppe","Doddagubbi","Doddanekkundi","Devanagundi","Devasandra","Domlur","Doorvaninagar","Doorvaninagar","Deepanjalinagar","Doddakallasandra","Dasarahalli","Dommasandra","Dasanapura","Doddajala","Deshapande Guttahalli","Electronics City","G.K.V.K","Gunjur","Gavipuram","Girinagar","Gottigere","Gayathrinagar","Guddanahalli","Hebbal Kempapura","Hulsur","Hoodi","Horamavu","Haragadde","Hulimavu","HSR Layout","Hebbagodi","Hongasandra","Hulimangala","Hulimangala","Hennagara","Hosakerehalli","Hampinagar","Herohalli","Handenalli","Hunasamaranahalli","Indiranagar","Indalavadi" ,"J.C.Nagar","Jakkur","Jeevabhimanagar","Jalavayuvihar","Jayanagar","JP Nagar","Jigani","Jalahalli","Kadugodi Extension","Kannamangala","Kothanur","Kanakapura","Krishnarajapuram","Kacharakanahalli","Kadugodi","Kundalahalli","Kalyananagar","Kodigehalli","Konanakunte","Kumaraswamy Layout","Kormanagala","Kumbalagodu","Kallubalu","Kathriguppe","Kenchanahalli","Kamakshipalya","K.G.Road","Kadabagere","Kugur","Kannur","Mundur","Mahadevapura","Muthusandra","Marathahalli Colony","Medimallasandra","Museum Road","Mico Layout","Mount St Joseph","Muthanallur","Madhavan Park","Madivala","Mallathahalli","Mavalli","Magadi Road","Milk Colony","Mahalakshmipuram Layout","Malleswaram","Mathikere","Marsur","Madanayakanahalli","Mayasandra" ,"Nagawara","Nayandahalli","Nagarbhavi","Nagasandra","Neriga","Neralur","Rajbavan","R T Nagar","Rajanakunte","Ramamurthy Nagar","Richmond Town","Rameshnagar","Rajarajeshwarinagar","Ramohalli","Ragihalli","Rajajinagar","Sahakaranagar ","St. Thomas Town","Sadashivanagar","Singanayakanahalli","Sri Jayachamarajendra Road","Shanthinagar","Seshadripuram","Srirampuram","Sarjapura","Samandur","Sidihoskote","Vartur","Vasanthanagar","Vidhana Soudha","Vimanapura","Venkateshapura","Virgonagar","Viveknagar","Vidyaranyapura","Viswaneedam","Vijayanagar","Vanakanahalli","Vidyanagar","Varthur","Thalaghattapura","Tilaknagar","Tyagrajnagar","Tataguni","Tavarekere","Whitefield","Wilson Garden","Whitefield","Wipro Limited","Yalahanka","yemalur","Yelachenahalli","Yeshwanthpura","yedavanahalli"};
                break;
            case "Bangalore Rural" :
                citySpinnerItems = new String[]{"Adarangi","Alurduddanahalli","Alluru","Anjanapura","Anneshwar"};
                break;
            case "Belgaum":
                citySpinnerItems = new String[]{"Athni", "Bail Hongal", "belgaum", "Chikodi","Gokak","Hindalgi","Hukeri","Kangrali","Khanapur","Konnur","Kudchi","Londa","Mudalgi","Nipani","Ramdurg","Raybag","Sadalgi","Sankeshwar","Saundatti-Yellamma"};
                break;
            case "Bellary":
                citySpinnerItems = new String[]{"Bellary","Donimallai Township","Hagaribommanahalli","Hoovina Hadagalii"};
                break;
        }

        ArrayAdapter<String> cityListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citySpinnerItems);
        cityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityListAdapter);

    }

    private void setValuesForDistrict(String selectedItem) {
        switch (selectedItem){
            case "Andaman and Nicobar Islands":
                districtSpinnerItems = new String[]{"Nicobar","North & Middle Andaman","South Andaman"};
                break;
            case "Andhra Pradesh":
                districtSpinnerItems = new String[]{"Anantapur","Chittoor","East Godavari","Guntur","Kadapa","Krishna","Kurnool","Nellore","Prakasam","Srikakulam","Visakhapatnam","Vizianagaram","West Godavari"};
                break;
            case "Arunachal Pradesh":
                districtSpinnerItems = new String[]{"Anjaw","Central Siang", "Changlang","Dibang Valley","East Kameng","East Siang","Kra Daadi","Kurung Kumey","Lohit","Longding","Lower Dibang Valley","Lower Siang","Lower Subansiri","Namsai","Papum Pare","Tawang","Tirap","Upper Siang","Upper Subansiri","West Kameng","West Siang"};
                break;
            case "Assam":
                districtSpinnerItems = new String[]{"Baksa","Barpeta","Biswanath","Bongaigaon","Cachar","Charaideo","Chirang","Darrang","Dhemaji","Dhubri","Dibrugarh","Dima Hasao","Goalpara","Golaghat","Hailakandi","Hojai","Jorhat","Kamrup","Kamrup Metropolitan","Karbi Anglong","Karimganj","Kokrajhar","Lakhimpur","Majuli","Morigaon","Nagaon","Nalbari","Sivasagar","Sonitpur","South Salmara-Mankachar","Tinsukia","Udalguri","West Karbi Anglong"};
                break;
            case "Bihar":
                districtSpinnerItems = new String[]{"Araria", "Arwal", "Aurangabad" , "Banka" , "Begusarai" , "Bhagalpur" , "Bhojpur" , "Buxar" , "Darbhanga" , "East Champaran" , "Gaya" , "Gopalganj" , "Jamui","Jehanabad" , "Kaimur" , "Katihar" , "Khagaria" , "Kishanganj" , "Lakhisarai" , "Madhepura" , "Madhubani" , "Munger" , "Muzaffarpur" , "Nalanda" , "Nawada" , "Patna" , "Purnia" , "Rohtas" , "Saharsa", "Samastipur" , "Saran" , "Sheikhpura" , "Sheohar" , "Sitamarhi" , "Siwan" , "Supaul","Vaishali","West Champaran"};
                break;
            case "Chandigarh":
                districtSpinnerItems = new String[]{"Chandigarh"};
                break;
            case "Chhattisgarh":
                districtSpinnerItems = new String[]{"Balod","Baloda Bazar","Balrampur","Bastar","Bemetara","Bijapur","Bilaspur","Dantewada","Dhamtari","Durg","Gariaband","Janjgir Champa","Jashpur","Kabirdham","Kanker","Kondagaon","Korba","Koriya","Mahasamund","Mungeli","Narayanpur","Raigarh","Raipur","Rajnandgaon","Sukma","Surajpur","Surguja"};
                break;
            case "Dadra and Nagar Haveli":
                districtSpinnerItems = new String[]{"Dadra and Nagar Haveli","Silvasa"};
                break;
            case "Daman and Diu":
                districtSpinnerItems =new String[]{"Daman","Diu"};
                break;
            case "Delhi":
                districtSpinnerItems = new String[]{"Central Delhi" ,"East Delhi" , "New Delhi" , "North Delhi" , "North East Delhi" , "North West Delhi" , "Shahdara" , "South Delhi" , "South East Delhi" , "South West Delhi " , "West Delhi "};
                break;
            case "Goa":
                districtSpinnerItems = new String[]{"Goa","Panaji"};
                break;
            case "Gujarat":
                districtSpinnerItems =new String[]{"Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch", "Bhavnagar", "Botad","Chhota Udaipur", "Dahod", "Dang", "Devbhoomi Dwarka", "Gandhinagar", "Gir Somnath", "Jamnagar", "Junagadh", "Kheda", "Kutch", "Mahisagar", "Mehsana", "Morbi", "Narmada", "Navsari", "Panchmahal", "Patan", "Porbandar", "Rajkot", "Sabarkantha", "Surat", "Surendranagar", "Tapi", "Vadodara", "Valsad"};
                break;
            case "Haryana":
                districtSpinnerItems = new String[]{"Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram", "Hisar", "Jhajjar","Jind","Kaithal", "Karnal", "Kurukshetra", "Mahendragarh", "Mewat", "Palwal", "Panchkula", "Panipat", "Rewari", "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"};
                break;
            case "Himachal Pradesh":
                districtSpinnerItems = new String[]{"Bilaspur","Chamba","Hamirpur","Kangra","Kinnaur","Kullu","Lahaul Spiti","Mandi","Shimla","Sirmaur","Solan","Una"};
                break;
            case "Jammu and Kashmi":
                districtSpinnerItems =new String []{"Anantnag","Bandipora","Baramulla","Budgam","Doda","Ganderbal","Jammu","Kargil","Kathua","Kishtwar","Kulgam","Kupwara","Leh","Poonch","Pulwama","Rajouri","Ramban","Reasi","Samba","Shopian","Srinagar","Udhampur"};
                break;
            case "Jharkhand":
                districtSpinnerItems = new String[]{"Bokaro","Chatra","Deoghar","Dhanbad","Dumka","East Singhbhum","Garhwa","Giridih","Godda","Gumla","Hazaribagh","Jamtara","Khunti","Koderma","Latehar","Lohardaga","Pakur","Palamu","Ramgarh","Ranchi","Sahebganj","Seraikela Kharsawan","Simdega","West Singhbhum"};
                break;
            case "Karnataka":
                districtSpinnerItems = new String[]{"Bagalkot","Bangalore Urban","Bangalore Rural","Belgaum","Bellary","Bidar","Bijapur","Chamarajanagar","Chikkaballapur","Chikkamagaluru","Chitradurga","Dakshina Kannada","Davanagere","Dharwad","Gadag","Gulbarga","Hassan","Haveri","Kodagu","Kolar","Koppal","Mandya","Mysore","Raichur","Ramanagara","Shimoga","Tumkur","Udupi","Uttara Kannada","Yadgir"};
                break;
            case "Kerala":
                districtSpinnerItems =new String[]{"Alappuzha","Ernakulam","Idukki","Kannur","Kasaragod","Kollam","Kottayam","Kozhikode"+"Malappuram","Palakkad","Pathanamthitta","Thiruvananthapuram","Thrissur","Wayanad"};
                break;
            case "Lakshadweep":
                districtSpinnerItems =new String[]{"Lakshadweep"};
                break;
            case "Madhya Pradesh":
                districtSpinnerItems =new String[]{"Agar Malwa","Alirajpur","Anuppur","Ashoknagar","Balaghat","Barwani","Betul","Bhind","Bhopal","Burhanpur","Chhatarpur","Chhindwara","Damoh","Datia","Dewas","Dhar","Dindori","Guna","Gwalior","Harda","Hoshangabad","Indore","Jabalpur","Jhabua","Katni","Khandwa","Khargone","Mandla","Mandsaur","Morena","Narsinghpur","Neemuch","Panna","Raisen","Rajgarh","Ratlam","Rewa","Sagar","Satna","Sehore","Seoni","Shahdol","Shajapur","Sheopur","Shivpuri","Sidhi","Singrauli","Tikamgarh","Ujjain","Umaria","Vidisha"};
                break;
            case "Maharashtra":
                districtSpinnerItems = new String[]{"Ahmednagar","Akola","Amravati","Aurangabad","Beed","Bhandara","Buldhana","Chandrapur","Dhule","Gadchiroli","Gondia","Hingoli","Jalgaon","Jalna","Kolhapur","Latur","Mumbai City","Mumbai Suburban","Nagpur","Nanded","Nandurbar","Nashik","Osmanabad","Palghar","Parbhani","Pune","Raigad","Ratnagiri","Sangli","Satara","Sindhudurg","Solapur","Thane","Wardha","Washim","Yavatmal"};
                break;
            case "Manipur":
                districtSpinnerItems = new String[]{"Bishnupur","Chandel","Churachandpur","Imphal East","Imphal West","Jiribam","Kakching","Kamjong","Kangpokpi","Noney","Pherzawl","Senapati","Tamenglong","Tengnoupal","Thoubal","Ukhrul"};
                break;
        }

        ArrayAdapter<String> districtListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districtSpinnerItems);
        districtListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtListAdapter);
    }

    private void setPreviousValues() {
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCancelable(false);

        DatabaseReference idReference = databaseReference.child("Volunteer").child(currentUserId);
        idReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    volunteerData = dataSnapshot.getValue(VolunteerData.class);
                    if (volunteerData != null) {
                        setValuesToFields(volunteerData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setValuesToFields(VolunteerData volunteerData) {
        etName.setText(volunteerData.getName());
        etEmail.setText(volunteerData.getEmail());
        etMobileNumber.setText(volunteerData.getMobileNumber());
        switch (volunteerData.getGender()){
            case "Male":
                male_radioButton.setChecked(true);
                break;
            case "Female":
                female_radioButton.setChecked(true);
                break;
        }
        etdob.setText(volunteerData.getDob());
        etAddress.setText(volunteerData.getAddress());
        etPincode.setText(volunteerData.getPincode());

        System.out.println("State Position : "+volunteerData.getStatePosition());
        stateSpinner.setSelection(volunteerData.getStatePosition());
        System.out.println("Selected State : "+stateSpinner.getSelectedItem().toString());

        setValuesForDistrict(stateSpinner.getSelectedItem().toString());
        System.out.println("District Position :"+volunteerData.getDistrictPosition());
        districtSpinner.setSelection(volunteerData.getDistrictPosition());
        System.out.println("Selected District:"+districtSpinner.getSelectedItem().toString());

        setValuesForCity(districtSpinner.getSelectedItem().toString());
        System.out.println("City Position :"+volunteerData.getCityPosition());
        citySpinner.setSelection(volunteerData.getCityPosition());
        System.out.println("Selected City :"+citySpinner.getSelectedItem().toString());

        if(volunteerData.isEnglish()){
            chEnglish.setChecked(true);
            onCheckboxClicked(chEnglish);
        }
        if(volunteerData.isKannada()){
            chKannada.setChecked(true);
            onCheckboxClicked(chKannada);
        }
        if(volunteerData.isTelugu()){
            chTelugu.setChecked(true);
            onCheckboxClicked(chTelugu);
        }
        if(volunteerData.isHindi()){
            chHindi.setChecked(true);
            onCheckboxClicked(chHindi);
        }
        if(volunteerData.isTamil()){
            chTamil.setChecked(true);
            onCheckboxClicked(chTamil);
        }
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        if(view == btnUpdate){
            updateVolunteer();
        }else if(view == btnDatePicker){
            showDateDialog(DIALOG_ID);
        }
    }

    private void updateVolunteer() {
        if(!checkName()){
            return;
        }
        if(!checkMobileNumber()){
            return;
        }
        if(!checkDOB()){
            return;
        }
        if(!checkAddress()){
            return;
        }
        if(!checkPincode()){
            return;
        }

        etNameLayout.setErrorEnabled(false);
        etMobileNumberLayout.setErrorEnabled(false);
        etAddressLayout.setErrorEnabled(false);

        final String name           = etName.getText().toString();
        final String email          = volunteerData.getEmail();
        final String mobileNumber   = etMobileNumber.getText().toString().trim();
        final String password       = volunteerData.getPassword();
        int genderSelectedId        = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender  = findViewById(genderSelectedId);
        final String gender         = (String) selectedGender.getText();
        final String dob            = etdob.getText().toString().trim();
        final String address        = etAddress.getText().toString().trim();
        final String pincode        = etPincode.getText().toString().trim();
        final String city           = citySpinner.getSelectedItem().toString().trim();
        final int cityPosition      = citySpinner.getSelectedItemPosition();
        final String district       = districtSpinner.getSelectedItem().toString();
        final int districtPosition  = districtSpinner.getSelectedItemPosition();
        final String state          = stateSpinner.getSelectedItem().toString();
        final int statePosition     = stateSpinner.getSelectedItemPosition();
        String filterAddress        = "";
        String photoUrl             = volunteerData.getPhotoUrl();
        String languages            = "";
        boolean english             = false;
        boolean kannada             = false;
        boolean telugu              = false;
        boolean hindi               = false;
        boolean tamil               = false;
        if(languagesKnownToWrite.contains("English")){
            english = true;
            languages = languages+"English";
        }
        if(languagesKnownToWrite.contains("Kannada")){
            kannada = true;
            languages = languages+"Kannada";
        }
        if(languagesKnownToWrite.contains("Telugu")){
            telugu = true;
            languages = languages+"Telugu";
        }
        if(languagesKnownToWrite.contains("Hindi")){
            hindi = true;
            languages = languages+"Hindi";
        }
        if(languagesKnownToWrite.contains("Tamil")){
            tamil = true;
            languages = languages+"Tamil";
        }

        filterAddress = filterAddress+state+district+city;

        if (languagesKnownToWrite.isEmpty()) {
            Toast.makeText(this, "Please select Languages you know to write..!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Updating Profile Details..");
            progressDialog.show();
            progressDialog.setCancelable(false);

            final DatabaseReference volunteerReference = databaseReference.child("Volunteer").child(currentUserId);
            final DatabaseReference usersReference = databaseReference.child("Users").child(currentUserId);

            VolunteerData newVolunteerData = new VolunteerData(currentUserId, name, email, mobileNumber, password, gender, dob, address, pincode, city, cityPosition, district, districtPosition, state, statePosition, english, kannada, telugu, hindi, tamil, "Volunteer", filterAddress, languages, photoUrl);

            volunteerReference.setValue(newVolunteerData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Users newUsers = new Users(currentUserId, email, password, "Volunteer", name, mobileNumber);
                        usersReference.setValue(newUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    System.out.println("Successfully Updated");
                                    SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                                    SharedPreferences.Editor Ed= sp.edit();
                                    Ed.putString("name", name);
                                    Ed.putString("mobileNumber", mobileNumber);
                                    Ed.apply();
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VolunteerProfileEdit.this);
                                    builder1.setMessage("Your details are successfully updated.");
                                    builder1.setCancelable(false);
                                    builder1.setPositiveButton(
                                            "Okay",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    finish();
                                                    startActivity(new Intent(VolunteerProfileEdit.this, VolunteerMainPage.class));
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }else if(task.isCanceled()){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VolunteerProfileEdit.this);
                                    builder1.setMessage("Oops..Occurred an error during update. Please try after some time.");
                                    builder1.setCancelable(false);
                                    builder1.setPositiveButton(
                                            "Okay",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    finish();
                                                    startActivity(new Intent(VolunteerProfileEdit.this, VolunteerMainPage.class));
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                    System.out.println(Objects.requireNonNull(task.getException()).getMessage());
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }else if(task.isCanceled()) {
                        System.out.println(Objects.requireNonNull(task.getException()).getMessage());
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private boolean checkName() {
        if(etName.getText().toString().trim().isEmpty()){
            etNameLayout.setErrorEnabled(true);
            etNameLayout.setError("Please enter Valid Name");
            etName.setError("Valid Input Required");
            requestFocus(etName);
            return false;
        }
        etNameLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkMobileNumber() {
        String mobileNumber = etMobileNumber.getText().toString().trim();
        if(mobileNumber.isEmpty()){
            etMobileNumberLayout.setErrorEnabled(true);
            etMobileNumberLayout.setError("Please enter Mobile Number");
            etMobileNumber.setError("Valid Input Required");
            requestFocus(etMobileNumber);
            return false;
        }else if(mobileNumber.length() != 10){
            etMobileNumberLayout.setErrorEnabled(true);
            etMobileNumberLayout.setError("Please enter Valid Mobile Number");
            etMobileNumber.setError("Valid Input Required");
            requestFocus(etMobileNumber);
            return false;
        }
        etMobileNumberLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkDOB(){
        try{
            boolean isDateValid = false;

            String[] dateValues = etdob.getText().toString().split("/");
            int date = Integer.parseInt(dateValues[0]);
            int month = Integer.parseInt(dateValues[1]);
            int year = Integer.parseInt(dateValues[2]);

            if(date > 0 && date < 32 && month > 0 && month < 13){
                Calendar now = Calendar.getInstance();
                int curryear = now.get(Calendar.YEAR);
                System.out.println(curryear);
                System.out.println(year);
                if(year < (curryear-18)){
                    System.out.println("Valid Date");
                    isDateValid = true;
                }
            }

            if(etdob.getText().toString().trim().isEmpty() && isDateValid){
                etdobLayout.setErrorEnabled(true);
                etdobLayout.setError("Please select a valid date.");
                etdob.setError("");
                requestFocus(etdob);
                return false;
            }
        }catch(Exception ex){
            etdobLayout.setErrorEnabled(true);
            etdobLayout.setError("Please select a valid date.");
            etdob.setError("Valid Input Required");
            requestFocus(etdob);
            return false;
        }
        etdobLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkAddress() {
        String address = etAddress.getText().toString().trim();
        if(address.isEmpty()){
            etAddressLayout.setErrorEnabled(true);
            etAddressLayout.setError("Please enter Address");
            etAddress.setError("Valid Input Required");
            requestFocus(etAddress);
            return false;
        }
        etAddressLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkPincode() {
        String pincode = etPincode.getText().toString().trim();
        if(pincode.isEmpty()){
            etPincodeLayout.setErrorEnabled(true);
            etPincodeLayout.setError("Please enter Pincode");
            etPincode.setError("Valid Input Required");
            requestFocus(etPincode);
            return false;
        }
        etPincodeLayout.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void showDateDialog(int id) {
        showDialog(id);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID){
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            etdob.setText(day_x+"/"+(month_x+1)+"/"+year_x);
        }
    };


    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.chEnglish:
                if (checked)
                    languagesKnownToWrite.add("English");
                else{
                    if(languagesKnownToWrite.contains("English")){
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("English"));
                    }
                }
                break;
            case R.id.chHindi:
                if (checked)
                    languagesKnownToWrite.add("Hindi");
                else{
                    if(languagesKnownToWrite.contains("Hindi")){
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Hindi"));
                    }
                }
                break;
            case R.id.chKannada:
                if (checked)
                    languagesKnownToWrite.add("Kannada");
                else{
                    if(languagesKnownToWrite.contains("Kannada")){
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Kannada"));
                    }
                }
                break;
            case R.id.chTamil:
                if (checked)
                    languagesKnownToWrite.add("Tamil");
                else{
                    if(languagesKnownToWrite.contains("Tamil")){
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Tamil"));
                    }
                }
                break;
            case R.id.chTelugu:
                if (checked)
                    languagesKnownToWrite.add("Telugu");
                else{
                    if(languagesKnownToWrite.contains("Telugu")){
                        languagesKnownToWrite.remove(languagesKnownToWrite.indexOf("Telugu"));
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(VolunteerProfileEdit.this, VolunteerMainPage.class));
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        startActivity(new Intent(VolunteerProfileEdit.this, VolunteerMainPage.class));
    }
}
