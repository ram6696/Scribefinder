package com.ourapps.scribefinder.Needy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.Volunteer.VolunteerData;

public class AddressFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView scribeAddress, scribeCity, scribeDistrict, scribeState;
    String myId = null;

    public AddressFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        scribeAddress   = view.findViewById(R.id.scribeAddress);
        scribeCity      = view.findViewById(R.id.scribeCity);
        scribeDistrict  = view.findViewById(R.id.scribeDistrict);
        scribeState     = view.findViewById(R.id.scribeState);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference idReference = rootRef.child("Volunteer").child(myId);
        idReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    VolunteerData volunteerData = dataSnapshot.getValue(VolunteerData.class);
                    assert volunteerData != null;
                    scribeAddress.setText(volunteerData.getAddress());
                    scribeCity.setText(volunteerData.getCity());
                    scribeDistrict.setText(volunteerData.getDistrict());
                    scribeState.setText(volunteerData.getState());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return view;
    }


 
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
