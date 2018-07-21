package edu.sdsu.tvidhate.pool_in.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.activity.MainActivity;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;
import edu.sdsu.tvidhate.pool_in.helper.Utilities;

public class
        AddTripFragment extends Fragment implements SharedConstants,View.OnClickListener {
            // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
            private static final String ARG_PARAM1 = "param1";
            private static final String ARG_PARAM2 = "param2";
            private OnFragmentInteractionListener mListener;

            private String currentUserDisplayName;
            private User mTripDriver;
            private DatabaseReference firebaseDatabaseInstanceReference;
            private FirebaseDatabase firebaseDatabaseInstance;
            private String hasCar;
            //Member Variables
            private EditText mSourceAddress,mDestinationAddress,mSeatsAvailable;
            private EditText mSourceNeighbordhood,mSourcePin,mDestinationPin,mDestinationNeighbordhood;
            private TextView mStartDate,mStartTime;
            private String mDateString;
            private Date mTripStartDate;

    public AddTripFragment() {
                // Required empty public constructor
            }

            /**
             * Use this factory method to create a new instance of
             * this fragment using the provided parameters.
             *
             * @param param1 Parameter 1.
             * @param param2 Parameter 2.
             * @return A new instance of fragment AddTripFragment.
             */
            // TODO: Rename and change types and number of parameters
            public static AddTripFragment newInstance(String param1, String param2) {
                AddTripFragment fragment = new AddTripFragment();
                Bundle args = new Bundle();
                args.putString(ARG_PARAM1, param1);
                args.putString(ARG_PARAM2, param2);
                fragment.setArguments(args);
                return fragment;
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if (getArguments() != null) {
                    String mParam1 = getArguments().getString(ARG_PARAM1);
                    String mParam2 = getArguments().getString(ARG_PARAM2);
                }
            }

            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {

                View view =  inflater.inflate(R.layout.fragment_add_trip,null);
                FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
                Button mDatePickerButton,mTimePickerButton,mResetButton,mSubmitButton;

                if(firebaseAuthInstance.getCurrentUser()!=null){
                    currentUserDisplayName = firebaseAuthInstance.getCurrentUser().getDisplayName();
                    Log.d("TPV-NOTE","currentUserDisplayName from fire: "+currentUserDisplayName);
                }
        hasCar = "Car";
        Utilities utilities = new Utilities(getFragmentManager());
        utilities.checkProfile(hasCar);
        utilities.checkForExistingRide(getActivity());

        firebaseDatabaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabaseInstanceReference = firebaseDatabaseInstance.getReference();

        mSourceAddress = view.findViewById(R.id.add_trip_from);
        mDestinationAddress = view.findViewById(R.id.add_trip_to);
        mStartDate = view.findViewById(R.id.add_trip_date_text);
        mStartTime = view.findViewById(R.id.add_trip_time_text);
        mSeatsAvailable = view.findViewById(R.id.add_trip_number_of_seats);
        mSourceNeighbordhood = view.findViewById(R.id.add_trip_from_neighborhood);
        mSourcePin = view.findViewById(R.id.add_trip_from_pincode);
        mDestinationNeighbordhood = view.findViewById(R.id.add_trip_to_neighborhood);
        mDestinationPin = view.findViewById(R.id.add_trip_to_pincode);

        mDatePickerButton = view.findViewById(R.id.add_trip_date_button);
        mTimePickerButton = view.findViewById(R.id.add_trip_time_button);
        mResetButton = view.findViewById(R.id.add_trip_reset_button);
        mSubmitButton = view.findViewById(R.id.add_trip_submit);

        mTimePickerButton.setOnClickListener(this);
        mDatePickerButton.setOnClickListener(this);
        mResetButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()>0)
                {
                    User currentUser = dataSnapshot.getValue(User.class);
//
//                    if(currentUser != null) {
//                        if(currentUser.hasACar())
//                        {
//                            Log.i("TPV-NOTE","User already has a car");
//                        }
//                        else
//                        {
//                            Log.i("TPV-NOTE","Please add car details first");
//                            Utilities utilities = new Utilities(getFragmentManager());
//                            utilities.checkProfile(hasCar);
//                        }
//                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        DatabaseReference people = firebaseDatabaseInstance.getReference(FIREBASE_PERSONAL_DATA).child(currentUserDisplayName);
        people.addValueEventListener(valueEventListener);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.add_trip_reset_button:
                mSourceAddress.setText(EMPTY_STRING);
                mDestinationAddress.setText(EMPTY_STRING);
                mStartDate.setText(EMPTY_STRING);
                mStartTime.setText(EMPTY_STRING);
                mSeatsAvailable.setText(EMPTY_STRING);
                break;

            case R.id.add_trip_time_button:
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String timeFormat = hourOfDay + ":" + minute;
                                mStartTime.setText(timeFormat);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;

            case R.id.add_trip_date_button:
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String dateFormat = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                mStartDate.setText(dateFormat);
                                Log.i("NOTE-TPV","Date"+dateFormat+"Date set "+mStartDate.getText().toString());
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.add_trip_submit:
                if(validInput())
                {
                    //Handle Timestamp
                    int noOfSeats = Integer.parseInt(mSeatsAvailable.getText().toString());
                    Log.i("TPV-NOTE",mTripDriver.toString());
                    //User tripDriver = firebaseDatabaseInstanceReference.child(FIREBASE_PERSONAL_DATA).child(currentUserDisplayName);
                    Trip newTrip = new Trip(firebaseDatabaseInstanceReference.child(FIREBASE_TRIP_DETAILS).push().getKey(),
                            mSourceAddress.getText().toString().trim(),mDestinationAddress.getText().toString().trim(),
                            mSourcePin.getText().toString().trim(),mSourceNeighbordhood.getText().toString().trim(),
                            mDestinationPin.getText().toString().trim(), mDestinationNeighbordhood.getText().toString().trim(),
                            System.currentTimeMillis(),mTripStartDate,noOfSeats,mTripDriver,mStartTime.getText().toString(),mStartDate.getText().toString());

                    Log.d("TPV-NOTE","uid: "+newTrip.getmTripId());
                    try{
                        firebaseDatabaseInstanceReference.child(FIREBASE_TRIP_DETAILS).child(currentUserDisplayName).setValue(newTrip);
                        firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).child(newTrip.getmTripId()).setValue(newTrip);
                        firebaseDatabaseInstanceReference.child(FIREBASE_CURRENT_RIDES).child(currentUserDisplayName).push().setValue(newTrip.getmTripId());
                        Log.d("TPV-NOTE","Data submitted successfully"+getActivity().getLocalClassName());
                       // Intent intent = new Intent(getContext(), MainActivity.class);
                        getActivity().finish();
                        //startActivity(intent);
                    }catch(Exception e){
                        Log.d("TPV-NOTE","Exception: "+e);
                    }
                }else{
                    Toast.makeText(getContext(),VALIDATION_FAILURE, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkForExistingRide();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                User currentUser = dataSnapshot.getValue(User.class);
                if(currentUser != null)
                {
                    mTripDriver = currentUser;
                    Log.i("TPV-NOTE",currentUser.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        DatabaseReference people = firebaseDatabaseInstance.getReference(FIREBASE_PERSONAL_DATA).child(currentUserDisplayName);
        people.addValueEventListener(valueEventListener);
    }

    private boolean validInput()
    {
        boolean dataValid = SUCCESS;
        if (TextUtils.isEmpty(mSourceAddress.getText().toString()))
        {
            mSourceAddress.setError(ENTER_SOURCE);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mSourceNeighbordhood.getText().toString()))
        {
            mSourceNeighbordhood.setError(ENTER_SOURCE_NEIGHBORHOOD);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mSourcePin.getText().toString()))
        {
            mSourcePin.setError(ENTER_SOURCE_PIN);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mDestinationAddress.getText().toString()))
        {
            mDestinationAddress.setError(ENTER_DESTINATION);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mDestinationNeighbordhood.getText().toString()))
        {
            mDestinationNeighbordhood.setError(ENTER_DESTINATION_NEIGHBORHOOD);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mDestinationPin.getText().toString()))
        {
            mDestinationPin.setError(ENTER_DESTINATION_PIN);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mStartDate.getText().toString()))
        {
            mStartDate.setError(ENTER_DATE);
            dataValid = FAILURE;
        }else{
            mStartDate.setError(null);
        }
        if(TextUtils.isEmpty(mStartTime.getText().toString()))
        {
            mStartTime.setError(ENTER_TIME);
            dataValid = FAILURE;
        }else{
            mStartTime.setError(null);
        }
        if(TextUtils.isEmpty(mSeatsAvailable.getText().toString()))
        {
            mSeatsAvailable.setError(ENTER_SEATS);
            dataValid = FAILURE;
        }
        else
        {
            int seats = Integer.parseInt(mSeatsAvailable.getText().toString());
            if(seats > 5 || seats < 1) {
                dataValid = FAILURE;
            }
        }


        mDateString = mStartDate.getText().toString().concat(" ").concat(mStartTime.getText().toString());
        DateFormat formatter ;
        Date date;
        formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        try {
            date = formatter.parse(mDateString);
            mTripStartDate = date;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Log.d("TPV-NOTE", "formatted time: " + date);
            Log.d("TPV-NOTE", "current time: " + Calendar.getInstance().getTime());
            if(date.before(Calendar.getInstance().getTime())){
                Toast.makeText(getContext(),DATE_VALIDATION_FAILURE_TOAST, Toast.LENGTH_SHORT).show();
                dataValid = FAILURE;
            }
        }catch (Exception e){
            Log.d("TPV-NOTE","Exception: "+e);
        }
        return dataValid;
    }

    private void checkForExistingRide() {
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()>0 && getActivity()!=null){
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference people1 = firebaseDatabaseInstance.getReference(FIREBASE_TRIP_DETAILS).child(currentUserDisplayName);
        people1.addValueEventListener(valueEventListener1);
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
