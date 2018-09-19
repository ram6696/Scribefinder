package com.ourapps.scribefinder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class VolunteerRegister extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etEmail, etMobileNumber, etNewPassword, etConfirmPassword, etdob, etAddress, etPincode;
    private TextInputLayout etNameLayout, etEmailLayout, etMobileNumberLayout, etNewPasswordLayout, etConfirmPasswordLayout, etdobLayout, etAddressLayout, etPincodeLayout;
    private RadioGroup genderGroup;
    private Spinner districtSpinner, stateSpinner, citySpinner;
    private Button btnRegister, btnDatePicker;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseVolunteer;
    String citySpinnerItems[] =null;
    String districtSpinnerItems[] = null;
    List<String> languagesKnownToWrite = new ArrayList<>();

    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseVolunteer = FirebaseDatabase.getInstance().getReference();

        etName = findViewById(R.id.etName);
        etName.clearFocus();
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etdob = findViewById(R.id.etDOB);
        etAddress = findViewById(R.id.etAddress);
        etPincode = findViewById(R.id.etPincode);

        etNameLayout = findViewById(R.id.etNameLayout);
        etEmailLayout = findViewById(R.id.etEmailLayout);
        etMobileNumberLayout = findViewById(R.id.etMobileNumberLayout);
        etNewPasswordLayout = findViewById(R.id.etNewPasswordLayout);
        etConfirmPasswordLayout = findViewById(R.id.etConfirmPasswordLayout);
        etdobLayout = findViewById(R.id.etDOBLayout);
        etAddressLayout = findViewById(R.id.etAddressLayout);
        etPincodeLayout = findViewById(R.id.etPincodeLayout);

        etPincode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        genderGroup = findViewById(R.id.genderGroup);

        citySpinner = findViewById(R.id.CitySpinner);
        districtSpinner = findViewById(R.id.DistrictSpinner);
        stateSpinner = findViewById(R.id.StateSpinner);

        btnRegister = findViewById(R.id.btnVolunteerRegister);
        btnDatePicker = findViewById(R.id.btnDatePicker);

        btnRegister.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

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


                //Districts of karnataka

            case "Bagalkot":
                citySpinnerItems = new String[]{"Badami", "Bagalkot", "Bilgi", "Guledguddu", "Hungund", "Likal", "Jamkhandi", "Kerur", "Mahalingpur", "Mudhol", "Rabgavi Banhatti", "Terdal"};
                break;
            case "Bangalore Urban":
                citySpinnerItems = new String[]{"A F Station Yelahanka ","Adugodi","Agara","Agram","Amruthahalli ","Anandnagar","Anekal","Ashoknagar","Attibele","Attur","Austin Town ","Bagalgunte ","Bagalur","Banashankari","Banaswadi","Bandikodigehalli","Bannerghatta","Basavanagudi","Basaveshwaranagar","Begur","Bellandur","Benson Town","Bestamaranahalli","Bettahalsur","Bilekahalli","Bommanahalli","Bommasandra","BSF Campus Yelahanka","Byatarayanapura ","C.V.Raman Nagar","Chamarajasagara","Chamrajpet","Chandapura","Chickpet","carmelram","Chikkabidarkal","Chandra Layout","Chikkabettahalli","Chikkajala","Chunchanakuppe","Doddagubbi","Doddanekkundi","Devanagundi","Devasandra","Domlur","Doorvaninagar","Doorvaninagar","Deepanjalinagar","Doddakallasandra","Dasarahalli","Dommasandra","Dasanapura","Doddajala","Deshapande Guttahalli","Electronics City","G.K.V.K","Gunjur","Gavipuram","Girinagar","Gottigere","Gayathrinagar","Guddanahalli","Hebbal Kempapura","Hulsur","Hoodi","Horamavu","Haragadde","Hulimavu","HSR Layout","Hebbagodi","Hongasandra","Hulimangala","Hulimangala","Hennagara","Hosakerehalli","Hampinagar","Herohalli","Handenalli","Hunasamaranahalli","Indiranagar","Indalavadi" ,"J.C.Nagar","Jakkur","Jeevabhimanagar","Jalavayuvihar","Jayanagar","JP Nagar","Jigani","Jalahalli","Kadugodi Extension","Kannamangala","Kothanur","Kanakapura","Krishnarajapuram","Kacharakanahalli","Kadugodi","Kundalahalli","Kalyananagar","Kodigehalli","Konanakunte","Kumaraswamy Layout","Kormanagala","Kumbalagodu","Kallubalu","Kathriguppe","Kenchanahalli","Kamakshipalya","K.G.Road","Kadabagere","Kugur","Kannur","Mundur","Mahadevapura","Muthusandra","Marathahalli Colony","Medimallasandra","Museum Road","Mico Layout","Mount St Joseph","Muthanallur","Madhavan Park","Madivala","Mallathahalli","Mavalli","Magadi Road","Milk Colony","Mahalakshmipuram Layout","Malleswaram","Mathikere","Marsur","Madanayakanahalli","Mayasandra" ,"Nagawara","Nayandahalli","Nagarbhavi","Nagasandra","Neriga","Neralur","Rajbavan","R T Nagar","Rajanakunte","Ramamurthy Nagar","Richmond Town","Rameshnagar","Rajarajeshwarinagar","Ramohalli","Ragihalli","Rajajinagar","Sahakaranagar ","St. Thomas Town","Sadashivanagar","Singanayakanahalli","Sri Jayachamarajendra Road","Shanthinagar","Seshadripuram","Srirampuram","Sarjapura","Samandur","Sidihoskote","Vartur","Vasanthanagar","Vidhana Soudha","Vimanapura","Venkateshapura","Virgonagar","Viveknagar","Vidyaranyapura","Viswaneedam","Vijayanagar","Vanakanahalli","Vidyanagar","Varthur","Thalaghattapura","Tilaknagar","Tyagrajnagar","Tataguni","Tavarekere","Whitefield","Wilson Garden","Whitefield","Wipro Limited","Yalahanka","yemalur","Yelachenahalli","Yeshwanthpura","yedavanahalli"};
                break;
            case "Bangalore Rural" :
                citySpinnerItems = new String[]{"Adarangi","Alurduddanahalli","Alluru","Anjanapura","Anneshwara","Antharahalli","Aradeshanahalli","Arakere","Arallumallige","Arasinakunte","Arebommanahalli","Arudi","Avathi","Baginigere","Baktharahalli","Banavadi","Baragenahalli","Basettihalli","Begur","Bendiganahalli","Bettakote","Bidalur","Bijjawara","Billanakote","Biskur","Budigere","Budihal","Byatha","Bylakere","Bylanarasapura","Channarayapatna","Chikkababanavara","Dasanapaura","Devanahalli","Devanahallifort","Devarahosahalli","Doddespet","Dodballapura","Doddabelavangala","Doddabele","Gangonahalli","Gantiganahalli","Gollahalli","Gudermaranahalli","Gundamgere","Hadonahalli","Hanabe","Harohalli","Hasigala","Heggadahalli","Heggunda","Hessarghatta","Hindaganala","Honnasandra","Hosahalli","Hoskote","Hulikal","Hulikunte","Huskur","Ibasapura","Jadigenahalli","Jakkanahalli","Jalige","Jodidasarahalli","Kadalappanahalli","Kadanur","Kagalipura","Kakolu","Kalkere","Kalkunte","Kambalu","Kanasavadi","Kannamangala","Karahalli","Kattigenahalli","Kembliganahalli","Kengeri","Kithanahalli","Kodigehalli","Koira","Koligere","Kollalagatta","Kongadiyappa Road","Konnagatta","Korati","Kudur","Kuluvanahatti","Kumbalahalli","Kundana","Lakkenahalli","Lakkur","Lakshmipura","Madalakote","Madavara","Madigondanahalli","Mahadevapura","Makali","Mallaarabanavadi","Mallathahalli","Mandibele","Manne","Manniganahalli","Maralakunte","Marasandra","Marikuppe","Mathahalli","Mayasandra","Melekote","Motagondanahalli","Mugadala","Mullahalli","Mylanahalli","Naduvaathi","Nandagudi","Narasandra","Narasipura","Narayanapura","Nelamangala","Nelavagilu","Niduvanda","Obalapura","Purushanahalli","Rajaghatta","Rameshwara","Reddihalli","Sadahalli","Sakalaavara","Sakkaaregollahalli","Samethanahalli","Sankighatta","Sasalu","Shivagange","Shivakote","Shivanapura","Shivapura","Silvepura","Singasandra","Solur","Somanhalli","Sondekoppa","Sugganahalli","Sulebele","Sulikere","Syakaladevanapura","Talaghatapura","Taralu","Tavarekere","Teppadabeguru","Thattekuppe","Thippasandra","Tippur","Tubugere","Tyamagondlu","Udaypura","Uganavadi","Vagata Agrahara","Veeraapura","Venkatagirikote","Vijayapura","Vishwanathapura","Yelekyathanahalli","Yeliyur","Yennegere","Yentiganahalli"};
                break;
            case "Belgaum":
                citySpinnerItems = new String[]{"Athni", "Bail Hongal", "belgaum", "Chikodi","Gokak","Hindalgi","Hukeri","Kangrali","Khanapur","Konnur","Kudchi","Londa","Mudalgi","Nipani","Ramdurg","Raybag","Sadalgi","Sankeshwar","Saundatti-Yellamma"};
                break;
            case "Bellary":
                citySpinnerItems = new String[]{"Bellary","Donimallai Township","Hagaribommanahalli","Hoovina Hadagalii","Hospet","Kamalapuram","Kampli","Kotturu","Kodligi","Sandur","Siruguppa","Tekkalakota"};
                break;
            case "Bidar":
                citySpinnerItems = new String[]{"Aurad","Basavakalyan","Bhalki","Bidar","Chitagoppa","Homnabad"};
                break;
            case "Chamrajnagar":
                citySpinnerItems = new String[]{"Chamrajnagar","Gudlupet","Kollegal","Yelandur"};
                break;
            case "Chikmaglur":
                citySpinnerItems = new String[]{"Chikmagalur","Kadur","Tarikere","Mudigere","Koppa","Narasimharajapura","Sringeri"};
                break;
            case "Chitradurga":
                citySpinnerItems = new String[]{"Challakere","Chitradurga","Hiriyur","Holalkere","Hosdurga","Molakalmuru"};
                break;
            case "Dakshina Kannada":
                citySpinnerItems = new String[]{"Mangalore","Bantval","Puttur","Beltangadi","Sulya"};
                break;
            case "Davanagere":
                citySpinnerItems = new String[]{"Davanagere","Channagiri","Harapanahalli","Harihar","Honnali","Jagalur"};
                break;
            case "Dharwad":
                citySpinnerItems = new String[]{"Alnavar","Annigere","Hubli Dharwad","Kalghatgi","Kundgol","Navalgund"};
                break;
            case "Gadag":
                citySpinnerItems = new String[]{"Gadag betigeri","Gajendragarh","Lakshmeshwar","Mulgund","Mundargi","Naregal","Nargund","Ron","Shirhati"};
                break;
            case "Gulbarga":
                citySpinnerItems = new String[]{"Afzalpur","Aland","Bhimrayanagudi","Chincholi","Chitapur","Gulbarga","Gurmatkal","Jevargi","Kurgunta","Sedam","Shahpur","Shorapur","Wadi","Yadgir"};
                break;
            case "Hassan":
                citySpinnerItems = new String[]{"Alur","Arkalgud","Arsikere","Belur","Channarayanpattana","Hassan","Holenarsipur","Sakleshpur","Sathyamangla"};
                break;
            case "Haveri":
                citySpinnerItems = new String[]{"Bankapura","Byadgi","Hangal","Haveri","Hirekurur","Kodiyal","Raaniberrur","Savanur","Shiggaon"};
                break;
            case "Kodagu":
                citySpinnerItems = new String[]{"Gonikappul","Kushalnagar","Madikere","Somvarpet","Virajpet"};
                break;
            case "Kolar":
                citySpinnerItems = new String[]{"Bagepalli","Bangarapet","Chik Ballapur","Chintamani","Gauribidanur","Gudibanda","Kolar","Malur","Mulbagal","Robertson Pet","Sidlaghatta","Srinivasapur"};
                break;
            case "Koppal":
                citySpinnerItems = new String[]{"Gangawati","Koppal","Kushtagi","Munirabad Project Area","Yelbarga"};
                break;
            case "Mandya":
                citySpinnerItems = new String[]{"Krishnarajasagara","Krishnarajpet","Maddur","Mandya","Melavalli","Nagamangala","Pandavapura","Shrirangapattana"};
                break;
            case "Mysore":
                citySpinnerItems = new String[]{"Bannur","Belvata","Bhogadi","Hebbalu","heggadadevabekote","Hunsur","Krishnarajanagar","Mysore","Nanjangud","Piriyapatna","Tirumankundal-narsipur"};
                break;
            case "Raichur":
                citySpinnerItems = new String[]{"Devadurga","Hatti","Lingsugur","Manvi","Mudgal","Raichur","Shaktinagar","Sindhnur"};
                break;
            case "Shimoga":
                citySpinnerItems = new String[]{"Bhadravati","Hasanagara","Jog Falls","Sagar","Shikarpur","Shimoga","Siralkoppa","Sorab","Tirthahalli"};
                break;
            case "Tumkur":
                citySpinnerItems = new String[]{"Adityapatna","Chiknayankanhalli","Gubbi","Koratagere","Kunigal","Madhugiri","Pavangada","Sira","Tiptur","Tumkur","Turuvekere"};
                break;
            case "Udupi":
                citySpinnerItems = new String[]{"Karkal","Kundapura","Mallar","Saligram","Udupi","Yenagudde"};
                break;
            case "Uttar Kannada":
                citySpinnerItems = new String[]{"Ankola","Bhatkal","Haliyal","Honavar","Karwar","Kumta","Mundgod","Siddapur","Sirsi","Supa","Uttar Kannada"};
                break;


                //Distrcits of Odisa
            case "Angul":
                citySpinnerItems = new String[]{"Angul"};
                break;

            case "Balangir":
                citySpinnerItems = new String[]{"Balangir","Kantabanji","Patnagarh", "Titlagarh"};
                break;

            case "Baleswar":
                citySpinnerItems = new String[]{"Azimabad","Baleswar","Issannagar", "Sovarampur","Srikanthpur"};
                break;

            case "Bargarh":
                citySpinnerItems = new String[]{"Barapali","Bargarh","Khaliapali", "Padmapur"};
                break;

            case "Bhadrak":
                citySpinnerItems = new String[]{"Basudebpur","Bhadrak","Dhamanagar"};
                break;

            case "Bhubaneshwar":
                citySpinnerItems = new String[]{"Bhubaneshwar"};
                break;

            case "Boudh":
                citySpinnerItems = new String[]{"Boudhnagar","Malisahi"};
                break;

            case "Cuttak":
                citySpinnerItems = new String[]{"Ahagad","Banki","Belagachhia", "Charibatia","Choudwar","Cuttak"};
                break;

            case "Debagarh":
                citySpinnerItems = new String[]{"Debagarh"};
                break;

            case "Dhenkanal":
                citySpinnerItems = new String[]{"Bhuban","Dhenkanal","Kamakshyanagar"};
                break;

            case "Gajapati":
                citySpinnerItems = new String[]{"Kashinagara","Parlakhemundi"};
                break;

            case "Ganjam":
                citySpinnerItems = new String[]{"Brahmapur","Ganjam"};
                break;

            case "Jagatsinghapur":
                citySpinnerItems = new String[]{"Jagatsinghapur","Paradip"};
                break;

            case "Jajapur":
                citySpinnerItems = new String[]{"Byasanagar","Jajapur"};
                break;

            case "Jharsuguda":
                citySpinnerItems = new String[]{"Belpahar","Brajarajnagar","Jharsuguda"};
                break;

            case "Kalahandi":
                citySpinnerItems = new String[]{"Bhawanipatna","Junagarh","Kesinga"};
                break;

            case "Kandhamal":
                citySpinnerItems = new String[]{"G.udayagiri","Phulabani"};
                break;

            case "Kendrapara":
                citySpinnerItems = new String[]{"Kendrapara","Pattamundai"};
                break;

            case "Kendujhar":
                citySpinnerItems = new String[]{"Kendujhar"};
                break;

            case "Khordha":
                citySpinnerItems = new String[]{"Khordha"};
                break;

            case "Koraput":
                citySpinnerItems = new String[]{"Koraput"};
                break;

            case "Malkangiri":
                citySpinnerItems = new String[]{"Malkangiri"};
                break;

            case "Mayurbhanj":
                citySpinnerItems = new String[]{"Mayurbhanj"};
                break;

            case "Nabarangapur":
                citySpinnerItems = new String[]{"Nabarangapur"};
                break;

            case "Naupada":
                citySpinnerItems = new String[]{"Naupada"};
                break;

            case "Nayagarh":
                citySpinnerItems = new String[]{"Nayagar"};
                break;

            case "Puri":
                citySpinnerItems = new String[]{"Puri"};
                break;

            case "Rayagada":
                citySpinnerItems = new String[]{"Rayagada"};
                break;

            case "Sambalpur":
                citySpinnerItems = new String[]{"Bamra","Dhankauda","Jamankira", "Jujumora","Kuchinda", "Maneswar","Kuchinda", "Naktideul","Rairakhol", "Rengali","Sambalpur"};
                break;

            case "Sonapur":
                citySpinnerItems = new String[]{"Sonapur"};
                break;

            case "Sundergarh":
                citySpinnerItems = new String[]{"Sundergarh"};
                break;

            //District and cities of Manipur


            case "Bishnupur":
                citySpinnerItems = new String[]{" Bishnupur","Kumbi","Moirang","Nambol","oinam","Thanga"};
                break;

            case "Chandel":
                citySpinnerItems = new String[]{"Chandel","Tengnoupal"};
                break;

            case "Churachandpur":
                citySpinnerItems = new String[]{"Churachandpur","Henglep ","Saikot", "Singhat ","Thanlon","Tipaimukh"};
                break;

            case "Imphal East":
                citySpinnerItems = new String[]{"Andro","Heingang","Jiribam","Keirao","Khetrigao","Khundrakpam","Khurai","Lamlai","Thongju","wangkhei","Yaiskul"};
                break;

            case "Imphal West":
                citySpinnerItems = new String[]{"Keisamthong","Konthoujam","Lamsang","Langthabal","mayang imphal","Naoriya Pakhanglakpa","Patasoi","Sagolband","Sekmai","Singiamei","Thangmeiband","Uripok","Wangoi"};
                break;

            case "Senapathi":
                citySpinnerItems = new String[]{"Kangpokpi","Karong","Mao","Saikul","Saitu","Tadubi"};
                break;

            case "Tamenglong":
                citySpinnerItems = new String[]{"Nungba","Tamei","Tamenglong"};
                break;

            case "Thoubal" :
                citySpinnerItems = new String[]{"Heirok","Hiyanglam","Kakching","Khangabok","Lilong","Sugnoo","Thoubal","Wabgai","Wangjing Tentha","Wangkhem"};

            case "Ukhrul":
                citySpinnerItems = new String[]{"Handwara","Karnah","Kupwara","Langate","Lolab"};
                break;



//District and cities of Goa

            case "Goa":
                citySpinnerItems = new String[]{"Aldona","Bogmalo","Chandor","Dabolim","Dramapur","Mardol","Ponda","Siroda","Velpai"};
                break;

            case "Panaji":
                citySpinnerItems = new String[]{"Caranzalem ","N.i.o. Donapaula","Panaji","Raibandar","Santacruz"};
                break;



//District and cities of Gujarat


            case "Ahmedabad":
                citySpinnerItems = new String[]{"Ahmedabad"};
                break;

            case "Amerli":
                citySpinnerItems = new String[]{"Amreli ", "Bagasara ","Chalala ", "Damnagar ","Jafrabad", "Lathi ", "Lathi ","Rajula ", " Savarkundla"};
                break;

            case "Anand":
                citySpinnerItems = new String[]{"Anand ", " Anklav","Bariavi ", "Borsad ","Karamsad", "Khambhat ", "ode ","Petlad ", "Umreth ","Vallabh Vidyanagar", "Vasna Borsad Ina ","Vitthal Udyognagar Ina"};
                break;

            case "Banas kantha":
                citySpinnerItems = new String[]{"Ambaji ", "Deesa ","Dhanera ", "Konodar ","Palanpur", "Tharad "};
                break;

            case "Bharuch":
                citySpinnerItems = new String[]{"Andada", "Anklesvar ","Anklesvar Ina ", "Bharuch ","Bharuch Ina", "Jambusar ", "Muktampur ","Palej "};
                break;

            case "Bhavnagar":
                citySpinnerItems = new String[]{" Alang","Bhavnagar ","Botad ", "Dhola ","Gadhada ", "Gariadhar ","Ghogha", "Katpur ","Mahuva ", "Palitana ","Sihor ", " Talaja"," Vartej"};
                break;

            case "Dohad":
                citySpinnerItems = new String[]{"Devgadbaria "," Dohad"," Freelandgunj", "Zalod "};
                break;

            case "Gandhinagar":
                citySpinnerItems = new String[]{" Adalaj"," Chandkheda","Chhatral Ina ", "Chiloda(Naroda) "," Dahegam", " Electonic Estate Gandhi","Kalol", "Kalol Ina ","Kolavada ", "Mansa ","Motera "};
                break;

            case "Jamnagar":
                citySpinnerItems = new String[]{ "Aarambhada"," Bedi"," Bhanvad", " Dhrol","Digvijaygram ", " Dwarka","Jamnagar", "Kalavad "," Khambhalia", "Mithapur ","Navghamghed ", " Okha Port","Salaya ", "Sikka ","Surajkaradi"};
                break;

            case "Junagadh":
                citySpinnerItems = new String[]{"Bantwa "," Chorvad"," Joshipura", " Junagadh"," Keshod", " kodinar","Manavadar", " Mangrol","Una ", " Vanthali","Veraval ","Visavadar"};
                break;

            case "Kachchh":
                citySpinnerItems = new String[]{"Anjar","Bhachau ","Bhuj ", " Gandhidam"," Kandla", " Mandvi","Mundra", " Rapar" };
                break;

            case "Kheda":
                citySpinnerItems = new String[]{"Balasinor ","Chaklasi "," Dakor", " kapadvanj","Kheda ", "Mahemdavad ","Mahudha", " Nadiad"};
                break;

            case "Mahesana":
                citySpinnerItems = new String[]{"Aambaliyasan "," kadi","Kheralu ", "Mahesana "," Unjha", "Vadnagar ","Vijapur", " Visnagar" };
                break;

            case "Narmada":
                citySpinnerItems = new String[]{"Kevadiya","Rajpipla ", "Vadia "};
                break;

            case "Navsari":
                citySpinnerItems = new String[]{"Aantaliya "," Bilimora"," Chikhli", " Devsar"," Gandevi", " Jalalpore","Mahuvar", " Navsari"," Vijalpor"};
                break;

            case "Panchmahals":
                citySpinnerItems = new String[]{" Godhra","Halol "," Kalol", "Kalol Ina "," Lunawada", "Santrampur" };
                break;

            case "Patan":
                citySpinnerItems = new String[]{"Chanasma ","Harij "," Patan", " Rahanpur"," Sidhpur"};
                break;

            case "Porbandar":
                citySpinnerItems = new String[]{"Aadityana "," Chhaya","Kutiyana ", " Porbandar","Ranavav " };
                break;

            case "Rajkot":
                citySpinnerItems = new String[]{"Bhayavadar"," Dhoraji","Gondal ", "Jasdan ","Jethpur Navagadh ", " Morvi","Paddhari", "Rajkot ","Upleta ", "Wankaner "};
                break;

            case "Sabarkantha":
                citySpinnerItems = new String[]{"Himatnagar"," Idar","Khedbrahma ", " Malpur","Meghraj ", "Modasa ","Prantij", " Talod" };
                break;

            case "Surat":
                citySpinnerItems = new String[]{"Bardoli ","Chalthan ","Chhaprabatha ", " Hajira Ina"," Ichchhapur", " kodadara","Kosmba", " Limla","Parvat ", " Sachin"," Sachin Ina"," Sayan"," Songadh", " surat"," Ukai", " Un","utran", " Vyara" };
                break;

            case "Surendranagar":
                citySpinnerItems = new String[]{" Dhrangadhra","Halvad ","Kharaghoda ", " Limbdi","Surendranagar Dudhrej ", " Thangadh","Wadhwan" };
                break;

            case "Thedangs":
                citySpinnerItems = new String[]{" The Dangs"};
                break;

            case "Vadodara":
                citySpinnerItems = new String[]{"Bajva","Bodeli ","Chhota Udaipur ", "Dabhoi ","Gsfc Complex Ina ", " Jawaharnagar","Karachiya", " Karjan"," Nandesari", "Nandesari Ina "," Padra"," Petro Chemical Complex"," Ranoli", " Tarsali"," Vadodara", "Vadodara Ina " };
                break;

            case "Valsad":
                citySpinnerItems = new String[]{"Abrama","Atul","Chala","Chanod","Dharampur","Dungra","Mogravadi","Nanakvada","Pardi","Parnera","Sarigam Ina","Umbergoan","Umbergoan Ina","Valsad","Valsad Ina","Vapi","Vapi Ina" };
                break;





//District and cities of Jammu & Kashmir

            case "Anantnag":
                citySpinnerItems = new String[]{" Ananthnag ", " Bijbehara ","Devsar  ", " Dooru","Homeshalibug", "Kokernag ","Kulgam","Noorabad","Pahalgam","Shangus"};
                break;


            case " Baramulla":
                citySpinnerItems = new String[]{"Bandipora  ", "Baramulla  ","Gulmarg  ", "Gurez ","Pattan", "Rafiabad ","Sangrama","Sonawari","Sopare","Uri"};
                break;

            case "Budgam ":
                citySpinnerItems = new String[]{" Badgam", "Beerwah  ","Chadoora  ", "Char-i-Sharief ","Khansahib" };
                break;

            case " Doda":
                citySpinnerItems = new String[]{"  Banihal", "Bhaderwah  "," Doda ", " Inderwal","Kishtwar", " Ramban"};
                break;

            case " Jammu":
                citySpinnerItems = new String[]{"  Akhnoor", " Bishnah ","  Chhamb", "Gandhinagar ","Jammu", "Marh ","Nagrota","R.s.pura","Raipur Domana","Samba","Suchetgarh","VIijaypur"};
                break;


            case "Kargil ":
                citySpinnerItems = new String[]{"Kargil  ", "  Zanskar"};
                break;


            case "Kathua ":
                citySpinnerItems = new String[]{"Bani  ", " Basohil ","Billawar  ", " Hiranagar","Kathua"};
                break;

            case "Kupwara ":
                citySpinnerItems = new String[]{" Handwara ", "Karnah "," Kupwara ", " Langate","Lolab" };
                break;

            case " Leh":
                citySpinnerItems = new String[]{"Leh  ", "  Nobra" };
                break;

            case "Poonch ":
                citySpinnerItems = new String[]{" Mendhar ", " Poonch Haveli "," Surankote " };
                break;

            case "Pulwama ":
                citySpinnerItems = new String[]{"Pampore  ", " Pulwama "," Rajpora ", " Shopian","Tral", "Wachi " };
                break;

            case " Rajouri":
                citySpinnerItems = new String[]{" Darhal ", " Kalakote ","  Nowshera", "Rajouri " };
                break;

            case "Srinagar ":
                citySpinnerItems = new String[]{" Amirakadal", " Batmaloo "," Ganderbal ", "Habbakadal ","Hazratbal", "Idgah ","Kangan","Sonawar","Zadiwal"};
                break;

            case "Udhampur ":
                citySpinnerItems = new String[]{"Chenani  ", " Gool Arnas ","Gulabgarh  ", " Ramnagar","Reasi", " Udhampur"};
                break;

            //Districts and Cities of Andrapradesh

            case "East Godavari" :
                citySpinnerItems = new String[]{"Alamuru","Allavaram","Amalapuram","Anaparty","Annavaram","Burugupudi","Eleswaram","Jagampeta","Kadiam","Kakinada","Mandapeta","Mummidivaram","Peddapuram","Pithapuram","Prathipadu","Rajahmundry","Ramachadrapuram","Rampachodapuram","Ravulapuram","Samalkot","Samara","Seethanagaram","Tallarevu","Tuni","Yellavaram"};
                break;

            case "Guntur":
                citySpinnerItems = new String[]{"Amaravathi","Anu Campus","Bapatla","Chilakaluripet","Dubbirala","Guntur","Gurzala","Kuchinapudi","Marcherla","Mangalagiri","Narasaraopet","Peddakurapaadu","Pherangipuram","Peduguralla","Punnur","Prathipadu","Repalle","Satenapalli","Tadepalli","Tadikonda","Tenali","Vemur","Vinukonda"};
                break;

            case "Kadapa":
                citySpinnerItems = new String[]{"Badvel","Jammalamadugu","Kadapa","Kamalapuram","Kodur","Lakkireddipalli","Mydukur","Prodattur","Pulivendula","Rajampet","Rayachoty"};
                break;


            case "Krishna":
                citySpinnerItems = new String[]{"Avanigadda","Bandar","Gannavaram","Gudivada","Jaggayapet","Kaikalur","Kankipadu","Machilipatnam","Malleshwaram","Mudinepalli","Mylavaram","Nandigama","Nidumolu","Nuzvid","Tiruvuru","Vijayawada","Vuyyur"};
                break;

            case "Kurnool":
                citySpinnerItems = new String[]{"Adoni","Allagadda","Alur","Atmakur","Dhone","Kodumur","Koilkunta","Kurnool","Nandikotkur","Nandyal","Panyam","Pattikonda","Yammiganur"};
                break;

            case "Nellore":
                citySpinnerItems = new String[]{"Allur","Atmakur","Gudgur","Kavali","Kovur","Nellur","Rapur","Sarvepalli","Sulurpet","Udayagiri","Venkatagiri"};
                break;


            case "Prakasam":
                citySpinnerItems = new String[]{"Addanki","Chudala","Cumbum","Darsi","Giddalur","Kandukur","Kanigiri","Kondepi","Markapur","Martur","Ongale","Parchur","Santhanuthalapadu"};
                break;


            case "Srikakulam":
                citySpinnerItems = new String[]{"Hiramandalam","Ichapuram","Nivagam","Palasa","Rajam","Sompeta","Srikakulam","Tenali"};
                break;


            case "Vishakapatanam":
                citySpinnerItems = new String[]{"Anakapalli","Araku","Bheemunipatnam","Chadavaram","Elamanchali","Gajwaka","Madivala","Narsipatnam","Paderu","Payakaraopeta","Pendurthi","Vishakapatanam"};
                break;

            case "Vizianagaram":
                citySpinnerItems = new String[]{"Badangi","Balijipeta","Bobbili","Cheppuripalli","Gajapathinagar","Parvathipuram","Perumali","Salur","Srungavarukota","Tagarapuvalasa","Therlam","Vizianagaram"};
                break;

            case "West Godavari":
                citySpinnerItems = new String[]{"Achanta","Attili","Bhimavaram","Chintalapudi","Dendulur","Eluru","Gopalpuram","Kovvur","Narasapur","Nidadavole","Palacole","Penugonda","Polamuru","Polavaram","Tadepalligudem","Tanuku","Undi","Ungutur"};
                break;

//Districts and States of Arunachal pradesh


            case "Changlang":
                citySpinnerItems = new String[]{"Bordumsa-diyum","Changlang South","Changlang North","Miao","Nampong"};
                break;

            case "Dibang valley":
                citySpinnerItems = new String[]{"Anini"};
                break;

            case "East Kameng":
                citySpinnerItems = new String[]{"Bameng","Chayang Tajo","Pakka Kesang","Seppa East","Seppa West"};
                break;

            case "East Siang":
                citySpinnerItems = new String[]{"Mebo","Nari-koyu","Pangin","Pasighat East","Pasighat West"};
                break;


            case "Lohit":
                citySpinnerItems = new String[]{"Chowkham","Hayuliang","Lekang","Namsai","Tezu"};
                break;

            case "Lower Debang Valley":
                citySpinnerItems = new String[]{"Dambuk","Roing"};
                break;


            case "Lowersubansiri":
                citySpinnerItems = new String[]{"Koloriang","Nyapin","Palin","Tali","Yachuli","Ziro Hapoli"};
                break;

            case "Pampum pare ":
                citySpinnerItems = new String[]{"Doimukh","Itanagar","Sagali"};
                break;

            case "Tawang":
                citySpinnerItems = new String[]{"Lumia","Mukto","Tawang"};
                break;

            case "Tirap":
                citySpinnerItems = new String[]{"Borduria Bogapani","Kanubari","Khonsa East","Khonsa West","Longdin Pumao","Namsang","Pongchao wakka"};
                break;

            case "Upper Siang":
                citySpinnerItems = new String[]{"Mariang geku","Tuting Yingkiong"};
                break;

            case "Uppersubansiri":
                citySpinnerItems = new String[]{"Daporijo","Dumporijo","Nacho","Raga","Taliha"};
                break;

            case "West Kameng":
                citySpinnerItems = new String[]{"Bomdilla","Dirang","Kalaktang","Thrizino-buragaon"};
                break;

            case "West Siang":
                citySpinnerItems = new String[]{"Along East ","Along West","Basar","Likabali","Liromoba","Mechkha","Rumgong"};
                break;

//District and city of Assam


            case "Barpeta":
                citySpinnerItems = new String[]{"Baghbar","Barpeta","Bhabanipur","Chenag","Jania","Patacharkuchi","Sarukhetri","Sorbhog"};
                break;

            case "Bongaigaon":
                citySpinnerItems = new String[]{"Abhayapuri","Bijni","Bongaigaon"};
                break;

            case "Cachar":
                citySpinnerItems = new String[]{"Barkhola","Dholai","Khatigora","Lakhipur","Silchar","Sonai","Udharbond"};
                break;

            case "Darrang":
                citySpinnerItems = new String[]{"Dalgaon","Kalaigaon","Mangaldoi","Sipajhar"};
                break;

            case "Dhemji":
                citySpinnerItems = new String[]{"Dhemaji","Jonai"};
                break;

            case "Dhubri":
                citySpinnerItems = new String[]{"Bilasipara","Dhubri","Gauripur","Golakganj","Mankachar","Samara south"};
                break;

            case "Dibrugarh":
                citySpinnerItems = new String[]{"Chabua","Dibrugarh","Duliajan","Lahowal","Moran","Naharkatia","Yingkiong"};
                break;

            case "Goalpara":
                citySpinnerItems = new String[]{"Dudhnai","Goalpara","Jaleshwar"};
                break;

            case "Golaghat":
                citySpinnerItems = new String[]{"Bhokakhat","Golaghat","Khumtai","Saruputhar"};
                break;

            case "Hailakandi":
                citySpinnerItems = new String[]{"Algapur","Hailakandi","katlicherra"};
                break;

            case "Jorhat":
                citySpinnerItems = new String[]{"Dergaon","Jorhat","Majuli","Mariani","Teok","Titibar"};
                break;

            case "Kamrup":
                citySpinnerItems = new String[]{"Boko","Chaygaon","Hajo","Kamalpur","Palasbari","Rangiya"};
                break;

            case "Kamrup Metro":
                citySpinnerItems = new String[]{"Dispur","Guwahati","Jalukbari"};
                break;

            case "Karbi Anglong":
                citySpinnerItems = new String[]{"Baithalongso","Bokajan","Diphu","Howraghat"};
                break;

            case "Karimganj":
                citySpinnerItems = new String[]{"Badarpur","Karimganj","Patharkandi","Ratabari"};
                break;

            case "Kokrajhar":
                citySpinnerItems = new String[]{"Gossaigaon","Kokrajhar","Sidli"};
                break;

            case  "Lakimpur":
                citySpinnerItems = new String[]{"Bihpuria","Dhakuakhana","Lakimpur","Naoboicha"};
                break;

            case "Morigaon":
                citySpinnerItems = new String[]{"Jagiroad","Laharighat","Marigaon"};
                break;

            case "N.c.hills":
                citySpinnerItems = new String[]{"Haflong"};
                break;

            case "Nagaon":
                citySpinnerItems = new String[]{"Barhampur","Batadroba","Dhing","Hojai","Jamunamukh","Kaliabor","Lumding","Nowgong","Raha","Rupohihat","Samaguri"};
                break;

            case "Nalbari":
                citySpinnerItems = new String[]{"Barama","Barkhetry","Chapaguri","Dharmapur","Nalbari","Tamulpur"};
                break;

            case "Sibsagar":
                citySpinnerItems = new String[]{"Amguri","Mahmara","Nazira","Sibsagar","Sonari","Thowra"};
                break;

            case  "Sonitpur":
                citySpinnerItems = new String[]{"Barchalla","Behalli","Biswanath","Dhekiajuli","Gohpur","Rangapara","Sootea","Tezpur"};
                break;
            case "Tinsukia" :
                citySpinnerItems= new String[]{"Digbol","Doom Dooma","Margherita","Sadiya","Tinsukia"};
                break;

            case "Udalguri"  :
                citySpinnerItems = new String[]{"Majbat","Panery","Udalguri"};
             break;

//Districts and cities of Telangana


            case "Adilabad":
                citySpinnerItems = new String[]{"Adilabad","Asifabad","Bhainsa","Boath","Chinnur","Khanapur","Luxettipet","Mancherial","Mudhole","Nirmal","Sirpur"};
                break;

            case "Hyderabad":
                citySpinnerItems = new String[]{"A.S.Rao Nagar","Abids","Alwal","Amberpet","Ameerpet","B.H.E.L","Banjarahills","Begumpet","Bolarum","Bowenpally","Chandrayanagutta","Charminar","Cherlapally","Dilsukhnagar","E.C.I.L","Gachibowli","Himayatnagar","Hyderabad","Jeedimetla","Jublihills","K.P.H.B","kachiguda","Karkhana","Khairatabad","Kondapur","Kukatpally","L.B.Nagar","Lakdikapol","Madhapur","Malakpet","Malkajgiri","Manikonda","Maredpally","Mehdipatnam","Miyapur","Motinagar","Musheerabad","Nacharam","Nampally","Panjagutta","Rajendra Nagar","Sainikpuri","Secunderabad","Taranka","Tirmulgherry","Toli Chowki","Uppal","Vasanthalipuram","Yosufguda"};
                break;

            case "Karimnagar":
                citySpinnerItems = new String[]{"Buggaram","Choppadandi","Godavarikhani","Huzurabad","Indurthi","Jagital","Kamalapur","Karimnagar","Manthani","Metpalli","Mydaram","Narella","Pedapalli","Sircilla"};
                break;

            case "Khammam":
                citySpinnerItems = new String[]{"Bhadrachalam","Burgampahad","Khammam","Kothagudem","Madhira","Palair","Sathupalli","Shujatnagar","Yellandu"};
                break;

            case "Medak":
                citySpinnerItems = new String[]{"Andole","Dommat","Medak","Narayankhed","Narsapur","Ramayampet","Sangareddy","Siddipet","Zahirabad"};
                break;

            case "Mehboobnagar":
                citySpinnerItems = new String[]{"Achampet","Alampur","Amarchinta","Gadwal","Jadcherla","Kalwakurthi","Kodangal","Kollapur","Mahbubunagar","Makthal","Nagarkurnool","Narayanpet","Shadnagar","Wanaparthy"};
                break;

            case "Nalgonda":
                citySpinnerItems = new String[]{"Alair","Bhongir","Chalakurthi","Deverkonda","kodad","Miryalguda","Mungode","Nakrekal","Nalgonda","Ramannapet","Suryapet","Tungaturthi"};
                break;

            case "Nizamabad":
                citySpinnerItems = new String[]{"Armoor","Balkonda","Banswada","Bodhan","Dichpalli","Jukkal","Kamareddy","Nizamabad","Yellareddy"};
                break;

            case "Rangareddy":
                citySpinnerItems = new String[]{"Chevella","Ibrhimpatnam","Medchal","Pargi","Tandur","Vikarabad"};
                break;

            case "Warangal":
                citySpinnerItems = new String[]{"Chennur","Cheriyal","Dornakal","Ghanpur","Hanamkonda","Janagam","Jangaon","Mahbubabad","Mandi Gobindgarh","Mulug","Narsampet","Parkal","Shyampet","Warangal","Wardhannapet"};
                break;

            //Districts and cities of Punjab



            case "Amritsar":
                citySpinnerItems = new String[]{"Ajnala","Amritsar","Attari","Beas","Jandiala","Khadoor Sahib","Majitha","Naushahra Panwan","Patti","Raja Sansi","Tarn Taran","Valtoha","Verka"};
                break;


            case "Bhatinda":
                citySpinnerItems = new String[]{"Bhatinda","Nathana","Pakka Kalan","Rampura Phul","Talwandi Sabo"};
                break;


            case "Faridkot":
                citySpinnerItems = new String[]{"Farikot","Kot Kapura","Panjgrain"};
                break;


            case "Fatehgarh Sahib":
                citySpinnerItems = new String[]{"Amloh","Sirhind"};
                break;


            case "Freozepur":
                citySpinnerItems = new String[]{"Abohar","Balluana","Fazilka","Firozepur","Firozepur Cantonment","Guru Har Sahai","Jalalabad","Zira"};
                break;


            case "Gurdaspur":
                citySpinnerItems = new String[]{"Batala","Dhariwal","Dina Nagar","Fatehgarh","Gurdaspur","Kahnuwan","Narot Mehra","Parhankot","Qadian","Srihargobindpur","Sujanpur"};
                break;


            case "Hoshiarpur":
                citySpinnerItems = new String[]{"Dasuya","Garhdiwala","Garhshankar","Hoshiarpur","Mahilpur","Mukerian","Sham Chaurasi","Tanda"};
                break;


            case "Jullundur":
                citySpinnerItems = new String[]{"Adampur","Jullundur","Kartarpur","Lohian","Nakodar","Nur Mahal","Phillaur"};
                break;


            case "Kapurthala":
                citySpinnerItems = new String[]{"Bholath","Kapurthala","Phagwara","Sultanpur"};
                break;


            case "Ludhiana":
                citySpinnerItems = new String[]{"Dakha","Jagraon","Khanna","Kum Kalan","Ludhiana","Payal","Qila Raipur","Raikot","Samrala"};
                break;


            case "Mansa":
                citySpinnerItems = new String[]{"Budhlada","Joga","Mansa","Sardulgarh"};
                break;


            case "Moga":
                citySpinnerItems = new String[]{"Bagha Purana","Dharamkot","Moga","Nihal Singh Wala"};
                break;


            case "Mohali":
                citySpinnerItems = new String[]{"Mohali","Zirakpur"};
                break;


            case "Muktsar":
                citySpinnerItems = new String[]{"Giddar Baha","Lambi","Malout","Muktsar"};
                break;


            case "Nawan Shahr":
                citySpinnerItems = new String[]{"Balachaur","Banga","Nawan Shahr"};
                break;


            case "Patiala":
                citySpinnerItems = new String[]{"Banur","Dakala","Ghanaur","Nabha","Patiala Town","Rajpura","Samana","Shutrana"};
                break;


            case "Ropar":
                citySpinnerItems = new String[]{"Anandpur Sahib","Chamkaur Sahib","Kharar","Morinda","Nangal"};
                break;


            case "Sangrur":
                citySpinnerItems = new String[]{"Barnala","Bhadur","Dhanaula","Dhuri","Dirbha","Lehra","Malerkotla","Sangrur","Sherpur","Sunam"};
                break;


            case "Tarn Taran":
                citySpinnerItems = new String[]{"Tarn Taran"};
                break;


//Districts and cities of Tripura


            case "Dhalai District":
                citySpinnerItems = new String[]{"Chhawmanu","Kamalpur","kulai", "Raima Vally","Salema","Surma"};
                break;

            case "North Tripura District":
                citySpinnerItems = new String[]{"Chandipur","Dharmanagar","Fatikroy","Jubarainagar","Kadamtala","kailashpur","Kanchanpur","Kurthi","Pabiachhara","Panisagar","Pencharthal"};
                break;

            case "South Tripura District":
                citySpinnerItems = new String[]{"Ampinagar","Bagma","Belonia", "Birganj","Hrishyamukh","Jolaibari","Kakraban","Manu","Matarbari","Radhakishorepur","Rajnagar","Sabroom","Salgarh","Santribazar"};
                break;

            case "West Tripura District":
                citySpinnerItems = new String[]{"Agartala","Asharambari","Badharghat","Bamutia","Banamalipur","Barjala","Bishalgarh","Boxanagar","Charilam","Dhanpur","Golaghati","Kalyanpur","Kamalasagar","Khayerpur","Khowal","Krishnapur","Majishpur","Mandaibazar","Mohanpur","Nalchar","Pratapgarh","Promodenagar","Ramnagar","Simna","Sonamura","Takarjala","Teliamura","Town Bardowali"};
                break;





//Districts and cities of Mizoram


            case "Aizawl":
                citySpinnerItems = new String[]{"Azl","Ratu","Saitual","Sateek","Suangpuilawn","Tlungvel"};
                break;

            case "Champhai":
                citySpinnerItems = new String[]{"Champhai","Khawbung","Khawhai","Khawzawl","Ngopa"};
                break;

            case "Kolasib":
                citySpinnerItems = new String[]{"Bilkhawthlir","Kawnpui","Kolasib"};
                break;

            case "Lwangtai":
                citySpinnerItems = new String[]{"Chawngte","Lwangtai"};
                break;

            case "Lunglei":
                citySpinnerItems = new String[]{"Buarpui","Hnahthial","Lunglei","Tawipu","Tlabung","Vanva"};
                break;

            case "Mamit":
                citySpinnerItems = new String[]{"Kawrtha","Lokicherra","phuldungsei"};
                break;

            case "Saiha":
                citySpinnerItems = new String[]{"Saiha","Sangau","Tuipang"};
                break;

            case "Serchhip":
                citySpinnerItems = new String[]{"Lungpho","N.vanlaiphai","Serchhip"};
                break;







//Districts and cities of Daman and Diu


            case "Daman":
                citySpinnerItems = new String[]{"Daman","Moti Daman"};
                break;

            case "Daman and Diu":
                citySpinnerItems = new String[]{"Daman and Diu"};
                break;





//Districts and cities of Nagaland



            case "Dimapur":
                citySpinnerItems = new String[]{"Chumukedima","Dimapur"};
                break;

            case "Kohima":
                citySpinnerItems = new String[]{"Kohima","Peren"};
                break;

            case "Mokochung":
                citySpinnerItems = new String[]{"Mokochung"};
                break;

            case "Mon":
                citySpinnerItems = new String[]{"Mon"};
                break;

            case "Phek":
                citySpinnerItems = new String[]{"Phek"};
                break;

            case "Tuensang":
                citySpinnerItems = new String[]{"Tuensang"};
                break;

            case "Wokha":
                citySpinnerItems = new String[]{"Wokha"};
                break;

            case "Zunheboto":
                citySpinnerItems = new String[]{"Zunheboto"};
                break;




//Districts and cities Pondicherry



            case "Karaikal":
                citySpinnerItems = new String[]{"Cotchery","Karaikal","Karaikal South","Mahe","Neduncadu","Neravy-grand Aldee","Pallor","Tirunallar","Yanam"};
                break;

            case "Pondicherry":
                citySpinnerItems = new String[]{"Ariankuppam","Bahour","Busy","Cassicade","Embalom","Kuruvinatham","Lawspet","Mannadipeth","Muthialpet","Nellithope","Nettapakkam","Orleampeth","Ossudu","Oupalam","Ozhukarai","Raj Bhavan","Reddiarpalayam","Thattanchavady","Thirubuvanai","Villenour"};
                break;


//Districts and cities of Uttaranchal



            case "Almora":
                citySpinnerItems = new String[]{"Almora","Bhikiasain","Dwarahat","Jageshwar","Ranjkhet","Salt","Someshwar"};
                break;


            case "Bageshwar":
                citySpinnerItems = new String[]{"Bageshwar","Kanda","Kapkot"};
                break;


            case "Chamoli":
                citySpinnerItems = new String[]{"Badrinath","Karanprayag","Nandprayag","Pindar"};
                break;


            case "Champawat":
                citySpinnerItems = new String[]{"Champawat","Lohaghat"};
                break;


            case "Dehradun":
                citySpinnerItems = new String[]{"Chakrata","Dehradun","Doiwala","Laxman Chowk","Mussorie","Rajpur","Rishikesh","Sahaspur","Vikasnagar"};
                break;


            case "Haridwar":
                citySpinnerItems = new String[]{"Bahadrabad","Bhagwanpur","Haridwar","Iqbalpur","Laksar","Laldhang","Landhaura","Mangalore","Roorkie"};
                break;


            case "Nainital":
                citySpinnerItems = new String[]{"Dhari","Haldwani","Mukteshwar","Nainital","Ramnagar"};
                break;


            case "Pauri Garhwal":
                citySpinnerItems = new String[]{"Bironkhal","Dhumakot","Kotdwar","Lansdowne","Pauri","Srinagar","Thalisian","Yamkeshwar"};
                break;


            case "Pithoragarh":
                citySpinnerItems = new String[]{"Dharchula","Didihat","Gangolihat","Kanalichhina","Pithogarh"};
                break;


            case "Rudraprayag":
                citySpinnerItems = new String[]{"Kedarnath","Rudraprayag"};
                break;

            case "Tehri Garhwal":
                citySpinnerItems = new String[]{"Deoprayag","Ghansali","Narendranagar","Pratapnagar","Tehri"};
                break;


            case "Udham Singh Nagar":
                citySpinnerItems = new String[]{"Bajpur","Jaspur","Kashipur","Khatima","Pantnagar-gadarpur","Rudrapur-kichha","Sitarganj"};
                break;


            case "Uttarkashi":
                citySpinnerItems = new String[]{"Gangotri","Purola","Yamunotri"};
                break;


 //Districts of Bihar


            case "Araria":
                 citySpinnerItems = new String[]{"Araria","Forbesganj","Jokihat","Narapatganj","Raniganj","Sikti"};
                 break;
            case "Arwal":
                citySpinnerItems = new String[]{"Arwal","Kurtha"};
                break;
            case "Aurangabad":
                citySpinnerItems = new String[]{"Aurangabad","Deb","Goh","Nabinagar","Ovra","Rafiganj"};
                break;
            case "Banka":
                citySpinnerItems = new String[]{"Amarpur","Banka","Belhar","Dhuraiya","Katoriya"};
                break;
            case "Begusarai":
                citySpinnerItems = new String[]{"Bachhavada","Bakhri","Balia","Barauni","Begusarai","Cheria Bariyarpur","Matihani"};
                break;
            case "Bhagalpur":
                citySpinnerItems =  new String[]{"Bhagalpur","Bihpur","Gopalapur","Kahalgaon","Nathnagar","Pirapainti","Sultanganj"};
                break;
            case "Bhojpur":
                citySpinnerItems = new String[]{"Ara","Badahara","Jagdishpur","Piro","Sahar","Sandesh","Shahpur"};
                break;
            case "Buxor":
                citySpinnerItems = new String[]{"Brahmpur","Buxor","Dumarav","Rajpur"};
                break;
            case "Darbhanga":
                citySpinnerItems = new String[]{"Darbhanga","Baheda","Bahedi","Ghanshyampur","Hayaghat","Jale","Keoti","manigachhi"};
                break;
            case "Gaya":
                citySpinnerItems = new String[]{"Atari","Barachatti","Baudh Gaya","Belaganj","Fatehpur","Gaya Mufafasil","Gaya Town","Gurua","Imamganj","Konch"};
                break;
            case "Gopalganj":
                citySpinnerItems = new String[]{"Baikuthpur","Barauli","Bhore","Gopalganj","Kateya","Mirganj"};
                break;
            case "Jahanabad":
                citySpinnerItems = new String[]{"Ghosi","Jahanabad","Makhdamapur"};
                break;
            case "Jamui":
                citySpinnerItems = new String[]{"Chakai","Jamui","Jhanjha","Sikandra","Tarapur"};
                break;
            case "Kaimur":
                citySpinnerItems = new String[]{"Bhabhua","Chainpur","Mohaniya","Ramagadh"};
                break;
            case "Katihar":
                citySpinnerItems = new String[]{"Barari","Barsoi","Kadwa","Kaithar","Kodha","Manihari","Pranapur"};
                break;
            case "Khagaria":
                citySpinnerItems = new String[]{"Alauli","Chautham","Khagariya","Parbatta"};
                break;
            case "Kishanganj":
                citySpinnerItems = new String[]{"Bahadurganj","Kishanganj","Thakurganj"};
                break;
            case "Lakhisarai":
                citySpinnerItems = new String[]{"Lakhisarai","Surajagadha"};
                break;
            case "Madhepura":
                citySpinnerItems = new String[]{"Madhepura","Alamnagar","Kishanganj","Kumarkhand","Singheshwar"};
                break;
            case "Madhubani":
                citySpinnerItems = new String[]{"Fulparas","Babubrhi","Benipatti","Bisfi","Harlakhi","Jhanjharpur","Khajauli","Laukaha","Madhepur","Madhubani","Pandaul"};
                break;
            case "Munger":
                citySpinnerItems = new String[]{"Jamalpur","Khadagpur","Munger"};
                break;
            case "Muzaffapur":
                citySpinnerItems = new String[]{"Aurai","Baruraj","Bochaha","Gaighatti","Kanti","Kurhani","Minapur","Muzaffarpur","Paru","Sahebganj","Sakra"};
                break;
            case "Nalanda":
                citySpinnerItems = new String[]{"Asthawan","Bihar","Chandi","Harnaut","Hilsa","Islampur","Nalanda","Rajgir"};
                break;
            case "Nawada":
                citySpinnerItems = new String[]{"Gobindpur","Hisua","Nawada","Rajoli","Warsaliganj"};
                break;
            case "Patna":
                citySpinnerItems= new String[]{"Badh","Bakthiyarpur","Danapur","Fatuha","Fulwari","Maner","Masaudhi","Mokama","Paliganj","Patna(Central)","Patna(East)","Patna(West)","Vikram"};
                break;
            case "Purnea":
                citySpinnerItems = new String[]{"Amour","Baisi","Banmankhi","Dhamdaha","Kasba","Purnea","Rupauli"};
                break;
            case "Rohtas":
                citySpinnerItems = new String[]{"Bikramganj","Chenari","Dehury","Dinara","Karakat","Nokha","Sasaram"};
                break;
            case "Saharsa":
                citySpinnerItems = new String[]{"Mahishi","Saharsa","Simri Bakhtiyarpur","Sonabarsa"};
                break;
            case "Samastipur":
                citySpinnerItems = new String[]{"Bibhutapur","Dalsinghsari","Hasanpur","Kalyanpur","Mohiuddin Nagar","Roseda","Samastiapur","Sarairanjan","Singhiya","Varishnagar"};
                break;
            case "Saran":
                citySpinnerItems = new String[]{"Baniapur","Chapra","Garkha","Jalalpur","Mannjhi","Maraurha","Mashrakh","Parsa","Sonpur","Taraiya"};
                break;
            case "Sheikhpura":
                citySpinnerItems = new String[]{"Barbigha","Sheikhpur"};
                break;
            case "Sheohar":
                citySpinnerItems = new String[]{"Dumri Katsari","Piprarhi","Purnahiya","Sheohar","Tariani Chowk"};
                break;
            case "Sitamarhi":
                citySpinnerItems = new String[]{"Bathnaha","Belsand","Majorganj","Pupri","Runi Saiadpur","Saunbarasa","Sitamarhi","Sursand"};
                break;
            case "Siwan":
                citySpinnerItems = new String[]{"Basantpur","Darauli","Goraikothi","Jiradei","Maharajganj","Mairwa","Raghunathpur","Siwan"};
                break;
            case "Supaul":
                citySpinnerItems = new String[]{"Chhatapur","Kisanpur","Raghopur","Supaul","Triveniganj"};
                break;
            case "Vaishali":
                citySpinnerItems = new String[]{"Hajipur","Jandaha","Lalganj","Mahnar","Mahua","Patepur","Raghopur","Vaishali"};
                break;
            case "West Champaran":
                citySpinnerItems= new String[]{"Adapur","Bagaha","Betiya","Chanpatiya","Dhaka","Dhanha","Ghodasahan","Gobindganj","Harsiddhi","Kesariya","Loria","Madhuban","Motihari","Nautan","Pipra","Ramnagar"};
                break;

                //Districts of Rajasthan

            case "Ajmer":
                citySpinnerItems = new String[]{"Ajmer","Beawar","Bhinai","Kekri","Kishangarh","Masuda","Nasirabad","Pushkar"};
                break;
            case "Alwar":
                citySpinnerItems = new String[]{"Alwar","Bansur","Behror","Kathumar","Khairthal","Laxmangarh","Mundawar","Rajgarh","Ramgarh","Thanagazi","Tijara"};
                break;
            case "Banswara":
                citySpinnerItems = new String[]{"Bagidora","Banswara","Da Npur","Ghatol","Kushalgarh"};
                break;
            case "Baran":
                citySpinnerItems = new String[]{"Atru","Baran","Chhabra","Kishanganj"};
                break;
            case "Barmer":
                citySpinnerItems = new String[]{"Barmer","Chouthan","Gudamalani","Pachpada","Sheo","Siwana"};
                break;
            case "Bharatpur":
                citySpinnerItems = new String[]{"Bayana","Bharatpur","Deeg","Kaman","Kumher","Nadbai","Nagar","Roopbas","Weir"};
                break;
            case "Bhilwara":
                citySpinnerItems = new String[]{"Asind","Banera","Bhilwara","Jahazpur","Mandal","Mandalgarh","Sahada","Shahpura"};
                break;
            case "Bikaner":
                citySpinnerItems = new String[]{"Bikaner","Dungargarh","Kolayat","Lunkaransar","Nokha"};
                break;
            case "Bundi":
                citySpinnerItems = new String[]{"Bundi","Hindoli","Nainwa","Patan"};
                break;
            case "Chittorgarh":
                citySpinnerItems = new String[]{"Barisadri","Begun","Chittorgarh","Gangrar","kapasan","Nimbahera"};
                break;
            case "Churu":
                citySpinnerItems = new String[]{"Churu","Ratangarh","Sadulpur","Sadarshahar","Sujangarh","Taranagar"};
                break;
            case "Dausa":
                citySpinnerItems = new String[]{"Bandikui","Dausa","Lalsot","Mahuwa","Sikrai"};
                break;
            case "Dholpur":
                citySpinnerItems = new String[]{"Bari","Dholpur","Rajakhera"};
                break;
            case "Dungarpur":
                citySpinnerItems = new String[]{"Aspur","Chorasi","Dungarpur","Sagwara"};
                break;
            case "Hanumangarh":
                citySpinnerItems = new String[]{"Bhadra","Hanumangarh","Nohar","Pilibanga","Rawatsar Tehsil","Sangaria","Tibi"};
                break;
            case "Jaipur":
                citySpinnerItems = new String[]{"Adarsh Nagar","Amber","Bairath","Banipur","Bassi","Chomu","Dudu","Fagi","Hawamaahal","Jagatpura","Jaipur","Jaipur Gramin","Jamwa Ramgarh","Jawahar Nagar","Jhotwara","Johri Bazar","Kishanpole","Kotpuli","Malviya Nagar","Phulera","Pratap Nagar","Shastri Nagar","Snaganer","Sodala","Vaishali","Vidyadhar Nagar"};
                break;
            case "Jaisalmer":
                citySpinnerItems = new String[]{"Fatehgarh","Jaisalmer","Pokaran"};
                break;
            case "Jalore":
                citySpinnerItems = new String[]{"Ahore","Bhinmal","Jalore","Raniwara","Sanchore"};
                break;
            case "Jhalawar":
                citySpinnerItems = new String[]{"Dag","Jhalrapatan","Khanpur","Manoharthana","Pirawa"};
                break;
            case "Jhunjhunu":
                citySpinnerItems = new String[]{"Gudha","Jhunjhunu","Khetri","Mandwa","Nawalgarh","Pilani","Surajgarh"};
                break;
            case "Jodhpur":
                citySpinnerItems = new String[]{"Bhopalgarh","Bilara","Jodhpur","Luni","Osiyan","Phalodi","Sardarpura","Shergarh","Soorsagar"};
                break;
            case "Karauli":
                citySpinnerItems = new String[]{"Hindaun","Karauli","Sapotra","Todabhim"};
                break;
            case "Kota":
                citySpinnerItems = new String[]{"Digod","Kota","Ladpura","Pipalda","Ramganjmandi"};
                break;
            case "Nagaur":
                citySpinnerItems = new String[]{"Degana","Didwana","Jayal","Ladnun","Makrana","Merta","Mundwa","Nagaur","Nawa"};
                break;
            case "Pali":
                citySpinnerItems = new String[]{"Bali","Desuri","Jaitaran","Kharchi","Pali","Raipur","Sojat","Sumerpur"};
                break;
            case "Pratapgarh":
                citySpinnerItems = new String[]{"Arnod","Chhoti Sadari","Dalot","Dhariawad","Pipalkhut","Pratapgarh"};
                break;
            case "Rajsamand":
                citySpinnerItems = new String[]{"Bhim","Kumbhalgarh","Nathdwara","Rajsamand"};
                break;
            case "Sawai Madhopur":
                citySpinnerItems = new String[]{"Bamanwas","Gangapur","Khandar","Sawai Madhopur"};
                break;
            case "Sikar":
                citySpinnerItems = new String[]{"Dantaramgarh","Dhod","Fatehpur","Khandela","Laxmangarh","Neem Ka Thana","Sikar","Srimadhopur"};
                break;
            case "Sirohi":
                citySpinnerItems = new String[]{"Pindwara-abu","Reodar","Sirohi"};
                break;
            case "Sri Ganganagar":
                citySpinnerItems = new String[]{"Ganganagar","Karanpur","Kishansinghpur","Raisnghnagar","Suratgarh"};
                break;
            case "Tonk":
                citySpinnerItems = new String[]{"Malpura","Newai","Toda Raysingh","Tonk","Uniara"};
                break;
            case "Udaipur":
                citySpinnerItems = new String[]{"Gogunda","Kherwara","Lasadia","Mavli","Phalasia","Salumber","Sarada","Udaipur","Udaipur Rural","Vallanagar"};

                //Distrcts of Sikkim

            case "East":
                citySpinnerItems = new String[]{"Asssam-lingley","Central Pendum","Gangtok","Khamdong","Loosing Pachekhani","Martham","Pathing","Rakdong Tentek","Ranka","Regu","Rthenock","Rumtek","Sangha"};
                break;
            case "North":
                citySpinnerItems = new String[]{"Djongu","Kabi Tingda","Lachen-mangshila"};
                break;
            case "South":
                citySpinnerItems = new String[]{"Damthang","Jorthang-nayabazar","Melli","Ralong","Rateypani","Temi-tarku","Wak"};
                break;
            case "West":
                citySpinnerItems = new String[]{"Barmiok","Chakung","Daramdin","Dentam","Geyzing","Rinchenpong","Soreong","Tashiding","Yoksam"};
                break;

                //Districts of Tamil Nadu

            case "Ariyalur":
                citySpinnerItems = new String[]{"Andimadam","Ariyalur","Sendurai","Udayarpalayam"};
                break;
            case  "Chennai":
                citySpinnerItems = new String[]{"Adambakkam","Adyar","Alwarpet","Ambattur","Anna Nagar Arcot","Annanagar","Annasalai","Ashok Nagar","Avadi","Ayanavaram","Besant Nagar","Broadway","Chepauk","Chetput","Chitlepakkam","Choolaimedu","Dr.Radhakrushnan Nagar","Egmore","Ennore","Guindy","Harbour","Jaffarkhanpet","Jawahar Nagar","Kamaraj Nagar","Kilpauk","Kodambakkam","Kolathur","Korattur","Kottupuram","Madhavaram","Madipakkam","Mambalam","Manali","Manapakkam","Minambakkam","Mylapore","Nandambakkam","Nandanam","Nanganallur","Nungambakkam","Padi","Pallavaram","Pammal","Park Town","Perambur","Porur","Purasawalkam","Ramapuram","Royapuram","Saidapet","Tambaram","Theagaraya Nagar","Thiruvanmiyur","Thousand Lights","Tirulam","Tondiarpet","Triplicane","Vadapalani","Vandalur","Velacherry","Villiwakkam","Virugambakkam","Washermanpet"};
                break;
            case "Coimbatore":
                citySpinnerItems = new String[]{"Avinashi","Coimbatore","Dharapuram","Kinathukkadavu","Mettupalayam","Palladam","Perur","Pollachi","Ponghai","Singanallur","Thondamuthur","Tiruppur","Udumalipet","Valparai"};
                break;
            case "Cuddalore":
                citySpinnerItems = new String[]{"Bhuvanagiri","Chidambaram","Cuddalore","Kattumannarkoli","Kurinjipadi","Mangalore","Nellikuppam","Neyvuli","Panruti","Vridhachalam"};
                break;
            case "Dharampuri":
                citySpinnerItems = new String[]{"Dharmapuri","Harur","Maorrapur","Palacode","Pennagaram"};
                break;
            case "Dindigul":
                citySpinnerItems = new String[]{"Athoor","Dindigul","Nathan","Nilakottai","Oddanchatram","Palani","Vedasandur"};
                break;
            case "Erode":
                citySpinnerItems = new String[]{"Andhiyur","Bhavani","Bhavanisagar","Erode","Gobichettpalayam","Kangeyam","Modakurichi","Perundurai","Sathyamangalam","Velakoil"};
                break;
            case "Kancheepuram":
                citySpinnerItems = new String[]{"Acharapakkam","Alandur","Chenagalpattu","Kancheepuram","Maduranthakam","Sriperumbadar","Tambaram","Tiruporur","Uthiramerur"};
                break;
            case "Kanniyakumari":
                citySpinnerItems = new String[]{"Colachel","Kanniyakumari","Killiyur","Nagercoil","Padmanabhapuram","Thiruvattar","Vilavancode"};
                break;
            case "Karur":
                citySpinnerItems = new String[]{"Aravakurichi","Kadavur","Karur","Krishnarayapuram","Kulithalai"};
                break;
            case "Killiyur":
                citySpinnerItems = new String[]{"Killiyur"};
                break;
case "Krishnagiri":
            citySpinnerItems = new String[] {"Bargur","Hosur","Kavaeripattinam","Krishnagiri","Thalli"};
            break;

case "Madurai":
            citySpinnerItems = new String[] {"Madurai","Melur","Samayanallur","Sedapatti","Sholavandan","Thirumangalam","Tiruparankundram","Usilampatti"};
            break;

case "Nagapattinam":
            citySpinnerItems = new String[] {"Kuttalam","Mayiladuthurai","Nagapattinam","Poompuhar","Sirkali","Vedaranyam"};
            break;
            case "Namakkal":
                citySpinnerItems = new String[] {"Kapilamalai","Namakkal","Rasipuram","Sankari","Sendamangalam","Tiruchengode"};
                break;

            case "Nilgiris":
                citySpinnerItems = new String[] {"Coonoor","Gudalur","Ooatacamund"};
                break;

            case "Perambalur":
                citySpinnerItems = new String[] {"Alathur","Jayankondam","Kunnam","Peranmbalur","Varahur","Veppanthattai"};
                break;

            case "Pudukkottai":
                citySpinnerItems = new String[] {"Alangudi","Aranthangi","Kolathur","Pudukkottai","Thirumayam"};
                break;

            case "Ramanathapuram":
                citySpinnerItems = new String[] {"Kadaladi","Mudukulathur","Paramakudi","Ramanathapuram","Thiruvadanai"};
                break;

            case "Salem":
                citySpinnerItems = new String[] {"Athur","Edapadi","Mettur","Omalur","Panamarathupatty","Salem","Talavasal","Taramangalam","Veerapandi","Yercaud"};
                break;

            case "Sivaganga":
                citySpinnerItems = new String[] {"Ilayangudi","Karaikudi","Manamadurai","Sivaganga","Tiruppathur"};
                break;

            case "Thanjavur":
                citySpinnerItems = new String[] {"Kumbakonam","Orathanad","Papanasam","Pattukottai","Thanjavur","Thiruvidaimaruthur","Thiruvonam","Tiruvaiyaru"};
                break;

            case "Theni":
                citySpinnerItems = new String[] {"Andipatti","Bodinayakkanur","Cumbum","Periyakulam","Theni"};
                break;

            case "Thiruchirappalli":
                citySpinnerItems = new String[] {"Lalkudi","Marungapuri","Musiri","Srirangam","Thiruverumbur","Thottiam","Tiruchirapalli","Uppiliapuram"};
                break;

            case "Thiruvarur":
                citySpinnerItems = new String[] {"Mannargudi","Nannilam","Tiruthuraipoondi","Tiruvarur","valangiman"};
                break;

            case "Tirunelveli":
                citySpinnerItems = new String[] {"Alangulam","Ambasamudram","Cheranmahadevi","Kadayanallur","Nanguneri","Palayamkottai","Radhapuram","Sankaranayanarkoil","Tenkasi","Tirunelveli","Vasudevanallur"};
                break;

            case "Tirupur":
                citySpinnerItems = new String[] {"Dharapuram","Kangeyam","Nallur","Palladam","Tirupur","Udumalaipettai","Velampalayam","Vellakoil"};
                break;

            case "Tiruvallur":
                citySpinnerItems = new String[] {"Gummidipoondi","Pakkipet","Ponneri","Poonamallee","Thiruttani","Thiruvallur","Thiruvottiyur","Villivakkam"};
                break;

            case "Tiruvannamalai":
                citySpinnerItems = new String[] {"Arni","Chengam","Cheyyar","Kalasapakkam","Peranamallur","Polur","Thandarambattu","Tiruvannamalai","Vandavasi"};
                break;

            case "Tuticorin":
                citySpinnerItems = new String[] {"koilpatti","Ottapidaram","Sattangulam","Srivaikuntam","Tiruchendur","Tuticorin","Vilathikulam"};
                break;

            case "Vellore":
                citySpinnerItems = new String[] {"Vellore"};
                break;

            case "Villupuram":
                citySpinnerItems = new String[] {"Chinnsalem","Gingee","Kandamangalam","Melmalayanur","Mugaiyur","Rishivandiam","Sankarapuram","Thirunavalur","Tindivanam","TiruKoilur","Ulundurpet","Vanur","Villupuram"};
                break;

            case "Virudhunagar":
                citySpinnerItems = new String[] {"Aruppukottai","Rajapalayam","Sattur","Sivakasi","Srivillithur","Virudhunagar"};
                break;

            //Districts of Chattisgarh


            case "Bastar":
                citySpinnerItems = new String[] {"Bastar"};
                break;

            case "Bijapur":
                citySpinnerItems = new String[] {"Bijapur"};
                break;

            case "Bilaspur":
                citySpinnerItems = new String[] {"Bilaspur","Bilha","Jarhagaon","Kota","Lormi","Manendragarh","Marwahi","Mungeli","Takhatpur"};
                break;

            case "Dantewada":
                citySpinnerItems = new String[] {"Dantewada"};
                break;

            case "Dhamtari":
                citySpinnerItems = new String[] {"Dhamtari"};
                break;

            case "Durg":
                citySpinnerItems = new String[] {"Durg"};
                break;

            case "Janjgir":
                citySpinnerItems = new String[] {"Akaltara","Champa","Katghora","Masturi","Rampur","Sakti","Sipat","Tanakhar"};
                break;

            case "Jashpur":
                citySpinnerItems = new String[] {"Jashpur"};
                break;

            case "Kabirdham":
                citySpinnerItems = new String[] {"Kabirdham"};
                break;

            case "Kanker":
                citySpinnerItems = new String[] {"Bhanupratappur","Sihawa"};
                break;

            case "Korba":
                citySpinnerItems = new String[] {"Korba"};
                break;


            case "Korea":
                citySpinnerItems = new String[] {"Korea"};
                break;

            case "Mahasamund":
                citySpinnerItems = new String[] {"Basna","Bindranavagarh","Dhamtari","Khaari","Kurud","Mahasamund","Rajim","Saraipali"};
                break;

            case "Narayanpur":
                citySpinnerItems = new String[] {"Narayanpur"};
                break;

            case "Raigarhv":
                citySpinnerItems = new String[] {"Bagicha","Dharmjaigarh","Jashpur","Kharsia","Lailunga","Pathalgaon","Raigarh","Sarangarh","Tapkara"};
                break;

            case "Raipur":
                citySpinnerItems = new String[] {"Abhanpur","Arang","Balodabazar","Bhatapara","Dharsiwa","Mandir Hasoud","Raipur Garmin","Raipur Nagar"};
                break;

            case "Rajnandgaon":
                citySpinnerItems = new String[] {"Dongargarh","Rajnandgaon"};
                break;

            case "Sarangarh":
                citySpinnerItems = new String[] {"Bhatgaon","Chandrapur","Kasdol","Malkharuoda","Palari","Pamgarh","Sarangarh","Saria"};
                break;

            case "Surguja":
                citySpinnerItems = new String[] {"Ambikapur","Baikunthpur","Lundra","Pal","Pilkha","Premnagar","Samri","Sitapur","Surajpur"};
                break;

                //Districts of Harayana
            case "Ambala":
                citySpinnerItems = new String[]{"Ambala","Mullana","Naggal","Naraingarh"};
                break;
            case "Bhiwani":
                citySpinnerItems = new String[]{"Badhra","Bawani Khera","Bhiwani","Dadri","Loharu","Mundhal Khurd","Tosham"};
                break;
                case "Faridabad":
            citySpinnerItems = new String[] {"Ballabgarh","Faridabad","Hassanpur","Hathin","Mewlamaharajpur","Palwal"};
            break;

case "Fatehabad":
            citySpinnerItems = new String[] {"Bhattu Kalan","Fathehabad","Ratia","Tohana"};
            break;

case "Gurgaon":
            citySpinnerItems = new String[] {"Ferozepur Jhikra","Gurgaon","Nuh","Pataudi","Sohna","Taoru"};
            break;

case "Hissar":
            citySpinnerItems = new String[] {"Adampur","Barwala","Ghirai","Hansi","Hissar","Narnaund"};
            break;

case "Jhajjar":
            citySpinnerItems = new String[] {"Badli","Bahadurgarh","Beri","Jhajjar","Salhawas"};
            break;

case "Jind":
            citySpinnerItems = new String[] {"Jind","Julana","Narwana","Rajond","Safidon","Uchana Kalan"};
            break;

case "Kaithal":
            citySpinnerItems = new String[] {"Guhla","Kaithal","Kalayat","Paj","Pundri"};
            break;

case "Karnal":
            citySpinnerItems = new String[] {"Assandh","Gharaunda","Indri","Jundla","Karnal","Nilokheri"};
            break;


case "Kurukshetra":
            citySpinnerItems = new String[] {"Pehowa","Shahabad","Thanesar"};
            break;

case "Mahendragarh":
            citySpinnerItems = new String[] {"Ateli","Mahendragarh","Narnaul"};
            break;

case "Panchkula":
            citySpinnerItems = new String[] {"Panchkula"};
            break;

case "Panipat":
            citySpinnerItems = new String[] {"Naultha","Panipat","Samalkha"};
            break;

case "Rewari":
            citySpinnerItems = new String[] {"Bawal","Jatusana","Rewari"};
            break;

case "Rohtak":
            citySpinnerItems = new String[] {"Hassangarh","Kalanaur","Kiloi","Meham","Rohtak"};
            break;

case "Sirsa":
            citySpinnerItems = new String[] {"Dabwali","Darba Kalan","Ellenabad","Rori","Sirsa"};
            break;

case "Sonepat":
            citySpinnerItems = new String[] {"Baroda","Gohana","Kailana","Rai","Rohat","Sonepet"};
            break;

case "Yamunanagar":
            citySpinnerItems = new String[] {"Yamunanagar"};
            break;


//Districts of Himachal Pradesh



case "Bilasapur":
            citySpinnerItems = new String[] {"Bilaspur","Geharwin","Ghumarwin","Kotkehloor"};
            break;

case "Chamba":
            citySpinnerItems = new String[] {"Banikhet","Bharmour","Bhattiyat","Chamba","Rajnagar"};
            break;

case "Hamirpur":
            citySpinnerItems = new String[] {"Bamsan","Hamirpur","Mewa","Nadaunta","Nandaun"};
            break;

case "Kangra":
            citySpinnerItems = new String[] {"Baijnath","Dharamshala","Guler","Jaswan","Jawalamukhi","Kangra","Nagrota","Palampur","Pragpur","Rajgir","Shahpur","Sulah","Thural"};
            break;

case "Kinnaur":
            citySpinnerItems = new String[] {"Kinnaur"};
            break;

case "Kullu":
            citySpinnerItems = new String[] {"Ani","Banjar","Kullu"};
            break;

case "Lahaul-spiti":
            citySpinnerItems = new String[] {"Lahaul-spiti"};
            break;

case "Mandi":
            citySpinnerItems = new String[] {"Balh","Chachiot","Darang","Dharampur","Gopalpur","Jogindernagar","Karsog","Mandi","Nachan","Sundernagar"};
            break;

case "Shimla":
            citySpinnerItems = new String[] {"Chopal","Jubbal-kothkhai","Kasumpti","Kumarsain","Rampur","Rohru","Simla","Theog"};
            break;

case "Sirmour":
            citySpinnerItems = new String[] {"Nahan","Pachhad","Paonta-doon","Rainka","Shillai"};
            break;

case "Solan":
            citySpinnerItems = new String[] {"Solan"};
            break;

case "Una":
            citySpinnerItems = new String[] {"Chintpurni","Gagret","Kultlehar","Santokhgarh","Una"};
            break;


//Districts of Jharkhand
case "Bokaro":
            citySpinnerItems = new String[] {"Amlabad","Bandhgaro","Bermo","Bhojudih","Bokaro","Bokaro Steel City","Chandrapura","Chas","Dugda","Gumia","Jaridih Bazar","Jena","Kurpania","Phusro","Sijhua","Tena-dum-cum Kathha"};
            break;

case "Chatra":
            citySpinnerItems = new String[] {"Chatra"};
            break;

case "Deoghar":
            citySpinnerItems = new String[] {"Deoghar","Jasidih","Madhupur"};
            break;

case "Dhanbad":
            citySpinnerItems = new String[] {"Angarpathar","Babua Kalan","Baliari","Basaria","Bhagatdih","Bhowrah","Bhuli","Chandaur","Chhatatnar","Chhotaputki","Chirkunda","Dhanbad","Dhaunsar","Dumarkunda","Egarkunr","Gobindpur","Godhar","Gomoh","Jamadoba","Jaria Khas","Jharia","Jorapokhar","Kailudih","katras","Kenduadih","Kharkhari","Kustai","Lakarka","Loyabad","Maithon","Malkera","Marma","Mera","Mugma","Nagri Kalan","Nirsa","Panchet","Pathardih","Pondar Kanali","Rohraband","Sahnidih","Sariadela","Sijua","Sindri","Siuliban","Tisra","Topchanchi"};
            break;

case "Dumka":
            citySpinnerItems = new String[] {"Basukinath","Dumka","Mihijham"};
            break;

case "Garhwa":
            citySpinnerItems = new String[] {"Garhwa","Sinduria"};
            break;

case "Godda":
            citySpinnerItems = new String[] {"Godda"};
            break;

case "Gridih":
            citySpinnerItems = new String[] {"Dhanwar","Giridih","Isri","Paratdih"};
            break;

case "Gumla":
            citySpinnerItems = new String[] {"Basia","Bherno","Bishunpur","Chainpur","Dumri","Ghaghra","Gumla","Kimdara","Palkot","Raidih","Sisai"};
            break;

case "Hazaribag":
            citySpinnerItems = new String[] {"Ara","Balkundra","Barhi","Barkakana","Barughutu","Dari","Gidi","Hazaribag","Hesla","Kedla","Kuju","Lapanga","Meru","Okni No. Ii","Orla","Palawa","Patratu","Ramgarh Cantonment","Religara Alias Pachhiari","Saunda","Sewai","Sirka","Topa"};
            break;

case "Jamtara":
            citySpinnerItems = new String[] {"Jamtara"};
            break;

case "koderma":
            citySpinnerItems = new String[] {"Koderma"};
            break;

case "Latehar":
            citySpinnerItems = new String[] {"Latehar"};
            break;

case "Lohardaga":
            citySpinnerItems = new String[] {"Lohardaga"};
            break;

case "Pakur":
            citySpinnerItems = new String[] {"Pakur"};
            break;

case "Palamu":
            citySpinnerItems = new String[] {"Barwadih","Daltonganj","Deorikalan","Hussainabad"};
            break;

case "Ranchi":
            citySpinnerItems = new String[] {"Bundu","Churi","Dhurwa","Harmu Housing Society","Hatia Railway Colony","Hehal","Itki","Kanke","Khelari","Khunti","Muri","Namkum","Ranchi","Tati"};
            break;

case "Sahibganj":
            citySpinnerItems = new String[] {"Rajmahal","Sahibganj"};
            break;

case "Seraikela":
            citySpinnerItems = new String[] {"Chandil","Gamharia","Ichagarh","Kharsawan","Kuchai","Nimdih","Rajnagar","Seraikela"};
            break;

case "Simdega":
            citySpinnerItems = new String[] {"Simdega"};
            break;

case "Singhbhum":
            citySpinnerItems = new String[] {"Jamshedpur","Singhbhum"};
            break;

//Districts of kerala.


case "Alappuzha":
            citySpinnerItems = new String[] {"Alleppey","Ambalapuzha","Chengannur","Cherthala","Haripad","Kayamkulam","Kuttanad","Mavelikara","Pandalam"};
            break;

case "Ernakulam":
            citySpinnerItems = new String[] {"Alwaye","Ankamali","Aroor","Ernakulam","Kochi","Kothamangalam","Kunnathunad","Mattancherry","Muvattupuzha","Narakal","Palluruthy","Parur","Perumbavoor","Piravom","Trippunithura","Vadakkekara"};
            break;

case "Idukki":
            citySpinnerItems = new String[] {"Devicolam","Idukki","Peermade","Thodupuzha","Udumbanchola"};
            break;

case "Kannur":
            citySpinnerItems = new String[] {"Azhicode","Cannanore","Edakkad","Irikkur","Kuthuparamba","Payyannur","Peravoor","Peringalam","Taliparamba","Tellicherry"};
            break;

case "Kasaragod":
            citySpinnerItems = new String[] {"Hosdrug","Kasaragod","Manjeshwar","Trikkarpur","Udma"};
            break;

case "Kollam":
            citySpinnerItems = new String[] {"Chadayamangalam","Chathanoor","Chavara","Eravipuram","Karunagapally","Kottarakara","Kundara","Kunnathur","Neduvathur","Pathanapuram","Punaloor","Quilon"};
            break;

case "Kottayam":
            citySpinnerItems = new String[] {"Changanacherry","Erumeli","Ettumanoor","Kaduthuruthy","Kanjirappally","Kottayam","Mararikulam","Palai","Poonjar","Puthuppally","Vaikom","Vazhoor"};
            break;

case "kozhikode":
            citySpinnerItems = new String[] {"Badagara","Balusseri","Beypore","Calicut","Koduvally","Kunnamangalam","Meppayur","Nadapuram","Narikkuni","Perambra","Quilandy","Thiruvambadi"};
            break;

case "Malappuram":
            citySpinnerItems = new String[] {"Kondotty","Kottakkal","Kuttippuram","Malappuram","Manjeri","Mankada","Nilambur","Perinthalmanna","Ponnani","Tanur","Tirur","Wandoor"};
            break;

case "Palakkad":
            citySpinnerItems = new String[] {"Alathur","Chittur","Coyalmannam","Kollengode","Malampuzha","Mannarkkad","Ottapalam","Palghat","Pattambi","Sreekrishnapuram","Thrithala"};
            break;

case "Pathanamthitta":
            citySpinnerItems = new String[] {"Adoor","Aranmula","Kallooppara","Konni","Kozhencherry","Pathanamthitta","Ranni","Thiruvalla"};
            break;

case "Thiruvananthapuram":
            citySpinnerItems = new String[] {"Ariyanad","Attingal","Kazhakuttam","Kilmanoor","Kovalam","Nedumangad","Nemom","Neyyattinkara","Parassala","Trivandrum","Vamanapuram","Varkala"};
            break;

case "Thrissur":
            citySpinnerItems = new String[] {"Chalakudi","Chelakara","Cherpu","Guruvayoor","Irinjalakuda","Kodakara","Kodungallur","Kunnamkulam","Mala","Manalur","Nattika","ollur","Trichur","Wadakkancherry"};
            break;

case "Wayanad":
            citySpinnerItems = new String[] {"Kalpetta","North Wynad","Sultan's Battery"};
            break;

//Districts of












        }

        ArrayAdapter<String> cityListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citySpinnerItems);
        cityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityListAdapter);

    }

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
            case "Meghalaya":
                districtSpinnerItems = new String[]{"East Garo Hills","East Kasi Hills","Jaintia","South Garo Hills","Upper Shillong","West Garo Hills","West kasi Hills"};
                break;
            case "Mizoram" :
                districtSpinnerItems =new String[]{"Aizami","Champhai","Kolasib","Lawngtai","Lunglei","Mamit","Saiha","Serchhip"};
                break;
            case "Nagaland" :
                districtSpinnerItems = new String[]{"Dimapur","Kohima","Mokochung","Mon","Phek","Tuensang","Wokha","Zunheboto"};
                break;
            case "Odisha" :
                districtSpinnerItems = new String[]{"Angul","Balangir","Baleshwar","Bargarh","Bhadrak","Bhubaneshwar","Boudh","Cuttak","Debagarh","Dhenkanal","Gajapati","Ganjam","Jagatsinghapur","Jajapur","Jharsuguda","Kalahandi","Kandhamal","Kendrapara","Kendujar","Khordha","Koraput","Malkangiri","Mayurbhanj","Nabarangapur","Naupada","Nayagarh","Puri","Rayagada","Sambalpur","Sonapur","Sundergarh"};
                break;
            case "Pondicherry" :
                districtSpinnerItems = new String[] {"Karaikal","Pondicherry"};
                break;
            case "Punjab" :
                districtSpinnerItems = new String[] {"Amritsar","Bhatinda","Faridkot","Fatehgarh Sahib","Freozepur","Gurdaspur","Hoshiarpur","Jullundur","Kapurthala","Ludhiana","Mansa","Moga","Mohali","Muktsar","Nawan Shahr","Patiala","Ropar","Sangrur","Tarn Taran"};
                break;
            case "Rajasthan":
                districtSpinnerItems = new String[]{"Ajmer","Alwar","Banswara","Baran","Barmer","Bharatpur","Bhilwara","Bikaner","Bundi","Chittorgarh","Churu","Dausa","Dholpur","Dungarpur","Hanumangarh","Jaipur","Jaisalmer","Jalore","Jhalawar","Jhunjhunu","Jodhpur","Karauli","Kota","Nagaur","Pali","Pratapgarh","Rajsamand","Sawai Madhopur","Sikar","Sirohi","Sri Ganganagar","Tonk","Udaipur"};
                break;
            case "Sikkim":
                districtSpinnerItems = new String[]{"East","North","South","West"};
                break;
            case "Tamil Nadu":
                districtSpinnerItems = new String[]{"Ariyalur","Chennai","Coimbatore","Cuddalore","Dharmapuri","Dindigul","Erode","Kancheepuram","Kanniyakumari","Karur","Killiyur","Krishnagiri","Madurai","Nagapattinam","Namakkal","Nilgiris","Perambalur","Puddukkottai","Ramanathapuram","Salem","Sivaganga","Thanjavur","Theni","Thiruchirappalli","Thiruvarur","Tirunelveli","Tirupur","Tiruvallur","Tiruvannamalai","Tuticorn","Vellore","Villupuram","Virudhunagar"};
                break;
            case "Telangana":
                districtSpinnerItems = new String[]{"Adilabad","Hyderabad","Karimnagar","Khammam","Medak","Mehboobnagar","Nalgonda","Nizamabad","Rangareddy","Warangal"};
                break;
            case "Tripura":
                districtSpinnerItems = new String[]{"Dhalai District","North Tripura District","South Tripura District","West Tripura District"};
                break;
            case "Uttar Pradesh":
                districtSpinnerItems = new String[]{"Agra","Aligarh","Allahabad","Ambedkar Nagar","Auraiya","Azamgarh","Baduan","Bagpat","Bahraich","Ballia","Balrampur","Banda","Barabanki","Bareilly","Basti","Bijnor","Bulandshahr","Chandauli","Chitrakoot","Deoria","Etah","Etewah","Faizabad","Farrukhabad","Fatehpur","Firozabad","Ghaziabad","Ghazipur","Gonda","Gorakhpur","Hamirpur","Hardoi","Hathras","Jalaun","Jaunapur","Jhansi","Jyotiba Phule Nagar","Kannauj","Kanpur Dehat","Kanpru Nagar","Kaushambi","Kushinagar","Lakhimpur","Lalitpur","Lucknow","Maharajgunj","Mahoba","Mainpuri","Mathura","Mau","Meerut","Mirzapur","Moradabad","Muzaffarnagar","Noida","Pilibhit","Pratapghar","Raebareli","Rampur","Saharanpur","Sant Kabir Nagar","Sant Ravidas Nagar","Shahjahanpur","Shrawasti","Siddhartnagar","Sitapur","Sonbhadra","Sultanpur","Unnao","Varanasi"};
                break;
            case "Uttaranchal":
                districtSpinnerItems = new String[]{"Almora","Bageshwar","Chamoli","Champawat","Dehradun","Haridwar","Nainital","Pauri Garhwal","Pithoragarh","Rudraprayag","Tehri Garhwal","Udham Singh Nagar","Uttarkashi"};
                break;
            case "West Bengal":
                districtSpinnerItems = new String[]{"Bankura","Birbhum","Burdwan","Coochbehar","Dakshin Dinajpur","Darjeeling","Hoogly","Howrah","Jalpaiguri","Kolkatta","Malda","Murshidabad","Nadia","North 24-Parganas","Pashchim Medinpur","Purbo Medinpur","Purulia","South 24-Paraganas","Uttar Dinajpur"};
                break;

        }

        ArrayAdapter<String> districtListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districtSpinnerItems);
        districtListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtListAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view == btnRegister){
            volunteerRegister();
        }else if(view == btnDatePicker){
            showDateDialog(DIALOG_ID);
        }
    }

    private void volunteerRegister() {

        if(!checkName()){
            return;
        }
        if(!checkEmail()){
            return;
        }
        if(!checkMobileNumber()){
            return;
        }
        if(!checkPasswords()){
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
        etEmailLayout.setErrorEnabled(false);
        etNewPasswordLayout.setErrorEnabled(false);
        etMobileNumberLayout.setErrorEnabled(false);
        etAddressLayout.setErrorEnabled(false);

        final String name           = etName.getText().toString().trim();
        final String email          = etEmail.getText().toString().trim();
        final String mobileNumber   = etMobileNumber.getText().toString().trim();
        final String password       = etNewPassword.getText().toString().trim();
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

        if (languagesKnownToWrite.isEmpty()) {
            Toast.makeText(this, "Please select Languages you know to write..!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Registering User..");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            throw Objects.requireNonNull(task.getException());
                        }
                    } catch (FirebaseAuthUserCollisionException emailIdAlreadyUsed) {
                        progressDialog.dismiss();
                        Toast.makeText(VolunteerRegister.this, "Email Id already exists..!", Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                        progressDialog.dismiss();
                        Toast.makeText(VolunteerRegister.this, "Password should be at least 6 characters..!", Toast.LENGTH_SHORT).show();
                        etNewPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                        progressDialog.dismiss();
                        Toast.makeText(VolunteerRegister.this, "Invalid Email Id..!.", Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(VolunteerRegister.this, "Unknown Error Occured..!.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {

                    String filterAddress = "";
                    String languages = "";

                    boolean english=false;
                    boolean kannada=false;
                    boolean telugu=false;
                    boolean hindi=false;
                    boolean tamil=false;

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

                    sendEmailVerificaton();

                    String id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                    Users currUser = new Users(id, email, password, "Volunteer", name, mobileNumber);
                    databaseVolunteer.child("Users").child(id).setValue(currUser);
                    VolunteerData volunteerData = new VolunteerData(id, name, email, mobileNumber, password, gender, dob, address, pincode, city, cityPosition, district, districtPosition, state, statePosition, english, kannada, telugu, hindi, tamil, "Volunteer", filterAddress, languages, "");
                    databaseVolunteer.child("Volunteer").child(id).setValue(volunteerData);
                    progressDialog.dismiss();

                    finish();
                    //startActivity(new Intent(VolunteerRegister.this, RegisterSuccessfulMessage.class));


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VolunteerRegister.this);
                    builder1.setMessage("Successfully Registered. Verification mail has sent to ur Email id, Please verify to login.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    startActivity(new Intent(VolunteerRegister.this, Login.class));
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                }
            });
        }
    }

    private boolean checkName() {
        if(etName.getText().toString().trim().isEmpty()){
            etNameLayout.setErrorEnabled(true);
            etNameLayout.setError("Please enter Valid Name");
            requestFocus(etName);
            return false;
        }
        etNameLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkEmail() {
        String email = etEmail.getText().toString().trim();
        if(email.isEmpty()){
            etEmailLayout.setErrorEnabled(true);
            etEmailLayout.setError("Please enter Email");
            requestFocus(etEmail);
            return false;
        }else if(!isValidEmail(email)){
            etEmailLayout.setErrorEnabled(true);
            etEmailLayout.setError("Please enter Valid Email");
            requestFocus(etEmail);
            return false;
        }
        etEmailLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkMobileNumber() {
        String mobileNumber = etMobileNumber.getText().toString().trim();
        if(mobileNumber.isEmpty()){
            etMobileNumberLayout.setErrorEnabled(true);
            etMobileNumberLayout.setError("Please enter Mobile Number");
            requestFocus(etMobileNumber);
            return false;
        }else if(mobileNumber.length() != 10){
            etMobileNumberLayout.setErrorEnabled(true);
            etMobileNumberLayout.setError("Please enter Valid Mobile Number");
            requestFocus(etMobileNumber);
            return false;
        }
        etMobileNumberLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkPasswords() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if(newPassword.isEmpty()){
            etNewPasswordLayout.setErrorEnabled(true);
            etNewPasswordLayout.setError("Please enter New Password");
            requestFocus(etNewPassword);
            return false;
        }else if(confirmPassword.isEmpty()){
            etConfirmPasswordLayout.setErrorEnabled(true);
            etConfirmPasswordLayout.setError("Please enter Confirm Password");
            requestFocus(etConfirmPassword);
            return false;
        } else if(!newPassword.contentEquals(confirmPassword)){
            etConfirmPasswordLayout.setErrorEnabled(true);
            etConfirmPasswordLayout.setError("Passwords not matching");
            requestFocus(etConfirmPassword);
            return false;
        }
        etNewPasswordLayout.setErrorEnabled(false);
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
            requestFocus(etPincode);
            return false;
        }else if(pincode.length() < 6){
            etPincodeLayout.setErrorEnabled(true);
            etPincodeLayout.setError("Please enter Valid Pincode");
            requestFocus(etPincode);
            return false;
        }
        etPincodeLayout.setErrorEnabled(false);
        return true;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) &&     Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void sendEmailVerificaton() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseAuth.signOut();
                    } else {
                        Toast.makeText(VolunteerRegister.this, "Unable to send Verification mail.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
