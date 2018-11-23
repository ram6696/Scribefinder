package com.ourapps.scribefinder.needy;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.volunteer.VolunteerData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class BasicProfileFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = BasicProfileFragment.class.getSimpleName();

    TextView scribeName, scribeMobileNumber, scribeEmail;
    private ImageView profilePic;
    ListView languagesList;
    String myId = null;
    private ProgressDialog progressDialog;
    final List<String> languagesKnownToWrite = new ArrayList<>();
    Button EmailButton,CallButton;


    View view;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    LinearLayout mobileNumberLayout, emailLayout;

    public BasicProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            myId = bundle.getString("id","");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_basic_profile, container, false);

        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCancelable(false);

        scribeName = view.findViewById(R.id.scribeName);
        scribeMobileNumber = view.findViewById(R.id.scribeMobileNumber);
        scribeEmail = view.findViewById(R.id.scribeEmail);

        languagesList = view.findViewById(R.id.languagesList);

        mobileNumberLayout = view.findViewById(R.id.mobileNumberLayout);
        mobileNumberLayout.setOnClickListener(this);
        emailLayout = view.findViewById(R.id.emailLayout);
        emailLayout.setOnClickListener(this);
        EmailButton = view.findViewById(R.id.EmailButton) ;
        EmailButton.setOnClickListener(this);
        CallButton = view.findViewById(R.id.CallButton);
        CallButton.setOnClickListener(this);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.database_volunteer_parent_reference)).child(myId);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    VolunteerData volunteerData = dataSnapshot.getValue(VolunteerData.class);
                    assert volunteerData != null;
                    scribeName.setText(volunteerData.getName());

                    profilePic = view.findViewById(R.id.profilePic);
                    String picture = volunteerData.getPhotoUrl();
                    if(!(picture.isEmpty()))
                        Picasso.get().load(picture).into(profilePic);

                    scribeMobileNumber.setText(volunteerData.getMobileNumber());

                    scribeEmail.setText(volunteerData.getEmail());

                    if(volunteerData.isEnglish())
                        languagesKnownToWrite.add("English");
                    if(volunteerData.isKannada())
                        languagesKnownToWrite.add("Kannada");
                    if(volunteerData.isTelugu())
                        languagesKnownToWrite.add("Telugu");
                    if(volunteerData.isHindi())
                        languagesKnownToWrite.add("Hindi");
                    if(volunteerData.isTamil())
                        languagesKnownToWrite.add("Tamil");

                    BasicProfileFragment.CustomAdapter customAdapter = new CustomAdapter();
                    languagesList.setAdapter(customAdapter);

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if(view == CallButton){
//            int permissionCheck = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
//            } else {
//                makeCall();
//            }
            makeCall();
        }else if(view == EmailButton){
            sendMail();
        }

    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sendMail() {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        SharedPreferences prefs = Objects.requireNonNull(this.getActivity()).getSharedPreferences("Login", MODE_PRIVATE);
        String uriText = "mailto:" + Uri.encode(scribeEmail.getText().toString().trim()) +
                "?subject=" + Uri.encode("Requesting for Scribe") +
                "&body=" + Uri.encode("Hi, I am "+prefs.getString("name",null)+", looking for scribe.\n If you are interested Please call me on this number : "+prefs.getString("mobileNumber", null)+".\n\nThanks,\n"+prefs.getString("name",null));
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + scribeMobileNumber.getText().toString().trim()));
        startActivity(callIntent);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class CustomAdapter extends BaseAdapter {

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return languagesKnownToWrite.size();
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
            convertView = getLayoutInflater().inflate(R.layout.languageslistdisplaylayout, null);
            TextView txtLanguage = convertView.findViewById(R.id.txtLanguage);
            txtLanguage.setText(languagesKnownToWrite.get(position));
            return convertView;
        }
    }
}
