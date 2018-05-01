package edu.sdsu.tvidhate.pool_in.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Car;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class UpdateProfileFragment extends Fragment implements SharedConstants,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DatabaseReference firebaseDatabaseInstanceReference;

    private OnFragmentInteractionListener mListener;

    private EditText mUserFirstname,mUserLastname,mUserContact,mUserEmailId,mUserHomeAddress;
    private EditText mCarBrand,mCarModel,mCarColor,mCarRegistrationNumber;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfileFragment newInstance(String param1, String param2) {
        UpdateProfileFragment fragment = new UpdateProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabaseInstanceReference = firebaseDatabase.getReference();

        mUserFirstname = view.findViewById(R.id.updateFirstNameEditText);
        mUserLastname = view.findViewById(R.id.updateLastNameEditText);
        mUserContact = view.findViewById(R.id.updateContactEditText);
        mUserEmailId = view.findViewById(R.id.updateEmailAddressEditText);
        mUserHomeAddress = view.findViewById(R.id.updateHomeAddressEditText);

        mCarBrand = view.findViewById(R.id.updateCarBrandEditText);
        mCarModel = view.findViewById(R.id.updateCarModelEditText);
        mCarColor = view.findViewById(R.id.updateCarColorEditText);
        mCarRegistrationNumber = view.findViewById(R.id.updateCarNumberPlateEditText);

        Button updateUserInfoButton = view.findViewById(R.id.update_user_profile_button);
        updateUserInfoButton.setOnClickListener(this);

        if(firebaseAuthInstance.getCurrentUser()!=null){
            mUserEmailId.setText(firebaseAuthInstance.getCurrentUser().getEmail());
            mUserContact.setText(firebaseAuthInstance.getCurrentUser().getDisplayName());
        }

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()>0)
                {
                    User currentUser = dataSnapshot.getValue(User.class);

                    if(currentUser != null) {
                        mUserFirstname.setText(currentUser.getmFirstName());
                        mUserLastname.setText(currentUser.getmLastName());
                        mUserHomeAddress.setText(currentUser.getmHomeAddress());
                        if(currentUser.hasACar())
                        {
                            Car currentUsersCar = currentUser.getmCar();
                            mCarBrand.setText(currentUsersCar.getmBrand());
                            mCarModel.setText(currentUsersCar.getmModel());
                            mCarColor.setText(currentUsersCar.getmColor());
                            mCarRegistrationNumber.setText(currentUsersCar.getmNumberPlate());
                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference people = firebaseDatabase.getReference(FIREBASE_PERSONAL_DATA).child(mUserContact.getText().toString());
        people.addValueEventListener(valueEventListener);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.update_user_profile_button:
                if(validInput()){
                    Log.d("rew","update button clicked");
                    //Handle Name & Address
                    User currentUser = new User(mUserFirstname.getText().toString().trim(),mUserLastname.getText().toString().trim(),mUserContact.getText().toString().trim(),
                            mUserEmailId.getText().toString().trim(),"Add Home Address");
                    //boolean updated = databaseHelper.updateProfileData(signUpDetailsPOJO);
                    try{
                        firebaseDatabaseInstanceReference.child(FIREBASE_PERSONAL_DATA).child(mUserContact.getText().toString()).removeValue();
                        firebaseDatabaseInstanceReference.child(FIREBASE_PERSONAL_DATA).child(mUserContact.getText().toString()).setValue(currentUser);
                        Log.d("rew","Data submitted successfully");
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }catch(Exception e){
                        Log.d("rew","Exception: "+e);
                    }
                }else{
                    Toast.makeText(getContext(), VALIDATION_FAILURE, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean validInput() {
        boolean dataValid = SUCCESS;
        if (TextUtils.isEmpty(mUserFirstname.getText().toString())) {
            mUserFirstname.setError(ENTER_NAME);
            dataValid = FAILURE;
        }
        if (TextUtils.isEmpty(mUserLastname.getText().toString())) {
            mUserLastname.setError(ENTER_NAME);
            dataValid = FAILURE;
        }

        if (TextUtils.isEmpty(mCarBrand.getText().toString())) {
            mCarBrand.setError(ENTER_CAR);
            dataValid = FAILURE;
        }
        if (TextUtils.isEmpty(mCarModel.getText().toString())) {
            mCarModel.setError(ENTER_CAR);
            dataValid = FAILURE;
        }
        if (TextUtils.isEmpty(mCarColor.getText().toString())) {
            mCarColor.setError(ENTER_CAR_COLOR);
            dataValid = FAILURE;
        }
        if (TextUtils.isEmpty(mCarRegistrationNumber.getText().toString())) {
            mCarRegistrationNumber.setError(ENTER_CAR_LICENSE);
            dataValid = FAILURE;
        }
        return dataValid;
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
