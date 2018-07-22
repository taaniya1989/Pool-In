package edu.sdsu.tvidhate.pool_in.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment implements SharedConstants,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button back,update;
    private OnFragmentInteractionListener mListener;
    private String email,myPhoneNo;
    private TextView mUserName,mUserEmail,mUserContact;
    //mUserLastName,,mUserHomeAddress,mUserOfficeAddress;
    //private TextView mUserCarBrand,mUserCarModel,mUserCarColor,mUserCarLicense;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        mUserName = view.findViewById(R.id.updateUserNameEditText);
        mUserEmail = view.findViewById(R.id.updateEmailAddressTextView);
        mUserContact = view.findViewById(R.id.updateContactTextView);
     /*   mUserLastName = view.findViewById(R.id.updateLastNameTextView);
        mUserHomeAddress = view.findViewById(R.id.updateHomeAddressTextView);
        mUserOfficeAddress = view.findViewById(R.id.updateWorkAddressTextView);

        mUserCarBrand = view.findViewById(R.id.updateCarBrandTextView);
        mUserCarModel = view.findViewById(R.id.updateCarModelTextView);
        mUserCarColor = view.findViewById(R.id.updateCarColorTextView);
        mUserCarLicense = view.findViewById(R.id.updateCarNumberPlateTextView);
*/
        update = view.findViewById(R.id.update_user_profile_button);
        back = view.findViewById(R.id.back_button);


        if(auth.getCurrentUser()!=null){
            email = auth.getCurrentUser().getEmail();
            myPhoneNo = auth.getCurrentUser().getDisplayName();
        }

        mUserEmail.setText(email);
        mUserContact.setText(myPhoneNo);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()>0){
                    User currentUser = dataSnapshot.getValue(User.class);
                    if(currentUser != null) {
                        mUserName.setText(currentUser.getmUserName());
                        mUserEmail.setText(currentUser.getmEmailAddress());
                        mUserContact.setText(currentUser.getmContactNumber());
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_PERSONAL_DATA).child(mUserContact.getText().toString());
        people.addValueEventListener(valueEventListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update.setOnClickListener(this);
        back.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.update_user_profile_button:
                Log.d("TPV-NOTE","Update button clicked");
                String updateData = "User";
                Bundle bundle = new Bundle();
                bundle.putString("updateData",updateData);
                Fragment updateProfileFragment = new UpdateProfileFragment();
                updateProfileFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.screen_area,updateProfileFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;

            case R.id.back_button:
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
                break;
        }
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
