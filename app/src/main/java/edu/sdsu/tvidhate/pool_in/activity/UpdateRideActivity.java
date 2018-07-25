package edu.sdsu.tvidhate.pool_in.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class UpdateRideActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener{

    private EditText mPlaceName,mPlaceCity,mPlacePinCode;
    private DatabaseReference mDatabase;
    private String contact,color,license,uid;
    private User currentTripPoster;
    private Date mTripStartDate;
    private Trip currentTrip = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_trip);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button mBackButton,mUpdateButton;

        mPlaceName = findViewById(R.id.placeName);
        mPlaceCity = findViewById(R.id.placeCity);
        mPlacePinCode = findViewById(R.id.placePincode);
        mBackButton = findViewById(R.id.add_trip_reset_button);
        mUpdateButton = findViewById(R.id.add_trip_submit);

        mBackButton.setText(R.string.back);
        mUpdateButton.setText(R.string.update);
        mBackButton.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);

        Bundle intent = getIntent().getExtras();
        if(intent!=null){
            currentTrip = (Trip) intent.getSerializable(TRIP_DETAILS_SERIALIZABLE);
        }
        if(auth.getCurrentUser()!=null){
            contact = auth.getCurrentUser().getDisplayName();
        }

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                Trip currentTrip = dataSnapshot.getValue(Trip.class);
                if(currentTrip != null)
                {
                    mPlaceName.setText(currentTrip.getmTripPlaceName());
                    mPlaceCity.setText(currentTrip.getmTripCity());
                    mPlacePinCode.setText(currentTrip.getmTripPincode());
                    uid=currentTrip.getmTripId();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_MY_RIDES).child(currentTrip.getmTripId());
        people.addValueEventListener(valueEventListener);
    }

    private boolean validInput() {
        boolean dataValid = true;
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_trip_reset_button:
                finish();
                break;

            case R.id.add_trip_submit:
                if(validInput()){
                    Trip currentTrip = new Trip();
                    currentTrip.setmCreationTimestamp(System.currentTimeMillis());
                    currentTrip.setmTripPlaceName(mPlaceName.getText().toString());
                    currentTrip.setmTripCity(mPlaceCity.getText().toString());
                    currentTrip.setmTripPincode(mPlacePinCode.getText().toString());
                    currentTrip.setmTripId(uid);

                    try{
                        mDatabase.child(FIREBASE_MY_RIDES).child(uid).setValue(currentTrip);
                        Log.d("TPV-NOTE","Data updated successfully");
                        finish();
                    }catch(Exception e){
                        Log.d("TPV-NOTE","Exception: "+e);
                    }
                }else{
                    Toast.makeText(UpdateRideActivity.this,ENTER_REQUIRED_FIELDS, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
