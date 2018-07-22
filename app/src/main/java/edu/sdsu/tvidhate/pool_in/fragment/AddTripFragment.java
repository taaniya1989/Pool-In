package edu.sdsu.tvidhate.pool_in.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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

public class AddTripFragment extends Fragment implements SharedConstants,View.OnClickListener {
            // the fragment initialization parameters, e.g. ARG_ITEM_NUMBE
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;

    private String currentUserDisplayName;
    private User mTripPoster;
    private DatabaseReference firebaseDatabaseInstanceReference;
    private FirebaseDatabase firebaseDatabaseInstance;

    //Member Variables
    private EditText mPlaceName,mPlaceCity,mPlacePinCode;
    private Button mPlaceImageSelectButton;
    private ImageView mPlaceImagePreview;
    private String mTripImagePath;
    private Uri mUri;
    private int height,width;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view =  inflater.inflate(R.layout.fragment_add_trip,null);
        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        Button mResetButton,mSubmitButton;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        if(firebaseAuthInstance.getCurrentUser()!=null)
        {
            currentUserDisplayName = firebaseAuthInstance.getCurrentUser().getDisplayName();
            Log.d("TPV-NOTE","currentUserDisplayName from fire: "+currentUserDisplayName);
        }

        firebaseDatabaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabaseInstanceReference = firebaseDatabaseInstance.getReference();

        mPlaceName = view.findViewById(R.id.placeName);
        mPlaceCity = view.findViewById(R.id.placeCity);
        mPlacePinCode = view.findViewById(R.id.placePincode);
        mResetButton = view.findViewById(R.id.add_trip_reset_button);
        mSubmitButton = view.findViewById(R.id.add_trip_submit);
        mPlaceImageSelectButton = view.findViewById(R.id.add_trip_image_button);
        mPlaceImagePreview = view.findViewById(R.id.placeImage);

        mResetButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
        mPlaceImageSelectButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_SELECTED_RESULT && resultCode == MainActivity.RESULT_OK
                && data != null && data.getData() != null)
        {
            mUri = data.getData();
//            selectedImage.setImageURI(mUri);
            Picasso.with(getContext()).load(mUri).resize(width/2,height/4).into(mPlaceImagePreview);
            Log.i("VANILLA_INFO",mUri.toString());
        }
        if(requestCode == IMAGE_CAPTURED_RESULT && resultCode == MainActivity.RESULT_OK
                && data != null ) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mUri = data.getData();
            //selectedImage.setImageURI(mUri);
            mPlaceImagePreview.setImageBitmap(photo);

            Log.i("VANILLA_INFO",photo.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.add_trip_reset_button:
                mPlaceName.setText(EMPTY_STRING);
                mPlaceCity.setText(EMPTY_STRING);
                mPlacePinCode.setText(EMPTY_STRING);
                break;

            case R.id.add_trip_submit:
                if(validInput())
                {
                    Trip newTrip = new Trip(System.currentTimeMillis(),firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).push().getKey(),
                            mPlaceName.getText().toString().trim(),mPlaceCity.getText().toString().trim(),
                            mPlacePinCode.getText().toString().trim(),mTripPoster,mTripImagePath);

                    Log.d("TPV-NOTE","uid: "+newTrip.getmTripId());
                    try{
                        firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).child(newTrip.getmTripId()).setValue(newTrip);
                        firebaseDatabaseInstanceReference.child(FIREBASE_CURRENT_RIDES).child(currentUserDisplayName).push().setValue(newTrip.getmTripId());
                        Log.d("TPV-NOTE","Data submitted successfully"+getActivity().getLocalClassName());
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    }catch(Exception e){
                        Log.d("TPV-NOTE","Exception: "+e);
                    }
                }else{
                    Toast.makeText(getContext(),VALIDATION_FAILURE, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.add_trip_image_button:
                Intent selectImageFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                selectImageFromGallery.setType("image/*");
                startActivityForResult(selectImageFromGallery, IMAGE_SELECTED_RESULT);
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

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                User currentUser = dataSnapshot.getValue(User.class);
                if(currentUser != null)
                {
                    mTripPoster = currentUser;
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
        if (TextUtils.isEmpty(mPlaceName.getText().toString()))
        {
            mPlaceName.setError(ENTER_PLACE);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mPlaceCity.getText().toString()))
        {
            mPlaceCity.setError(ENTER_PLACE_CITY);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mPlacePinCode.getText().toString()))
        {
            mPlacePinCode.setError(ENTER_PLACE_PIN);
            dataValid = FAILURE;
        }
        return dataValid;
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
