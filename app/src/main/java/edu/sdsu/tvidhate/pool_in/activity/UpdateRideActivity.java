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
import edu.sdsu.tvidhate.pool_in.entity.Car;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class UpdateRideActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener{

    private EditText mSourceAddress,mDestinationAddress,mSeatsAvailable,mStartDate,mStartTime;
    private EditText mSourceNeighbordhood,mSourcePin,mDestinationPin,mDestinationNeighbordhood;
    private DatabaseReference mDatabase;
    private String contact,color,license,uid;
    private User currentTripPoster;
    private Car thisTripCar;
    private Date mTripStartDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_trip);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button mDatePickerButton,mTimePickerButton,mBackButton,mUpdateButton;
        mSourceAddress = findViewById(R.id.add_trip_from);
        mDestinationAddress = findViewById(R.id.add_trip_to);
        mStartDate = findViewById(R.id.add_trip_date_text);
        mStartTime = findViewById(R.id.add_trip_time_text);
        mSeatsAvailable = findViewById(R.id.add_trip_number_of_seats);
        mSourceNeighbordhood = findViewById(R.id.add_trip_from_neighborhood);
        mSourcePin = findViewById(R.id.add_trip_from_pincode);
        mDestinationNeighbordhood = findViewById(R.id.add_trip_to_neighborhood);
        mDestinationPin = findViewById(R.id.add_trip_to_pincode);

        mDatePickerButton = findViewById(R.id.add_trip_date_button);
        mTimePickerButton = findViewById(R.id.add_trip_time_button);
        mBackButton = findViewById(R.id.add_trip_reset_button);
        mUpdateButton = findViewById(R.id.add_trip_submit);

        mBackButton.setText(R.string.back);
        mUpdateButton.setText(R.string.update);

        mTimePickerButton.setOnClickListener(this);
        mDatePickerButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);

        if(auth.getCurrentUser()!=null){
            contact = auth.getCurrentUser().getDisplayName();
        }

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                Trip currentTrip = dataSnapshot.getValue(Trip.class);
                if(currentTrip != null){
                    currentTripPoster = currentTrip.getmTripDriver();
                    thisTripCar = currentTrip.getmTripCar();
                    if(thisTripCar != null)
                    {
                        color = thisTripCar.getmColor();
                        license = thisTripCar.getmNumberPlate();
                    }
                    mSourceAddress.setText(currentTrip.getmSourceAddress());
                    mDestinationAddress.setText(currentTrip.getmDestinationAddress());
                    mStartDate.setText(currentTrip.getmStartDate());
                    mStartTime.setText(currentTrip.getmStartTime());
                    mSeatsAvailable.setText(String.valueOf(currentTrip.getmSeatsAvailable()));
                    mSourceNeighbordhood.setText(String.valueOf(currentTrip.getmSourceNeighbordhood()));
                    mDestinationNeighbordhood.setText(String.valueOf(currentTrip.getmDestinationNeighbordhood()));
                    mSourcePin.setText(String.valueOf(currentTrip.getmSourcePin()));
                    mDestinationPin.setText(String.valueOf(currentTrip.getmDestinationPin()));

                    String mDateString = mStartDate.getText().toString().concat(" ").concat(mStartTime.getText().toString());
                    DateFormat formatter ;
                    Date date;
                    formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
                    try {
                        mTripStartDate = formatter.parse(mDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ;
                    uid=currentTrip.getmTripId();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference("trip_details").child(contact);
        people.addValueEventListener(valueEventListener);
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(mSourceAddress.getText().toString()))
        {
            mSourceAddress.setError(ENTER_SOURCE);
            dataValid = FAILURE;
        }
        else if(TextUtils.isEmpty(mSourceNeighbordhood.getText().toString()))
        {
            mSourceNeighbordhood.setError(ENTER_SOURCE_NEIGHBORHOOD);
            dataValid = FAILURE;
        }
        else if(TextUtils.isEmpty(mSourcePin.getText().toString()))
        {
            mSourcePin.setError(ENTER_SOURCE_PIN);
            dataValid = FAILURE;
        }
        else if(TextUtils.isEmpty(mDestinationAddress.getText().toString()))
        {
            mDestinationAddress.setError(ENTER_DESTINATION);
            dataValid = FAILURE;
        }
        else if(TextUtils.isEmpty(mDestinationNeighbordhood.getText().toString()))
        {
            mDestinationNeighbordhood.setError(ENTER_DESTINATION_NEIGHBORHOOD);
            dataValid = FAILURE;
        }
        else if(TextUtils.isEmpty(mDestinationPin.getText().toString()))
        {
            mDestinationPin.setError(ENTER_DESTINATION_PIN);
            dataValid = FAILURE;
        }
        if (TextUtils.isEmpty(mStartDate.getText().toString())) {
            mStartDate.setError(ENTER_DATE);
            dataValid = false;
        }
        if (TextUtils.isEmpty(mStartTime.getText().toString())) {
            mStartTime.setError(ENTER_TIME);
            dataValid = false;
        }
        if (TextUtils.isEmpty(mSeatsAvailable.getText().toString())) {
            mSeatsAvailable.setError(ENTER_SEATS);
            dataValid = false;
        }
        if (Integer.parseInt(mSeatsAvailable.getText().toString())<1) {
            mSeatsAvailable.setError(SEATS_ZERO);
            dataValid = false;
        }
        String dateString = mStartDate.getText().toString().concat(" ").concat(mStartTime.getText().toString());
        DateFormat formatter ;
        Date date ;
        formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        try {
            date = formatter.parse(dateString);
            mTripStartDate = date;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Log.d("TPV-NOTE", "formatted time: " + date);
            Log.d("TPV-NOTE", "current time: " + Calendar.getInstance().getTime());
            if(date.before(Calendar.getInstance().getTime())){
                dataValid = false;
            }
        }catch (Exception e){
            Log.d("TPV-NOTE","Exception: "+e);
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
            case R.id.add_trip_date_button:
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateRideActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formatDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                mStartDate.setText(formatDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.add_trip_time_button:
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateRideActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String formatTime = hourOfDay + ":" + minute;
                                mStartTime.setText(formatTime);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;

            case R.id.add_trip_reset_button:
                finish();
                break;

            case R.id.add_trip_submit:
                if(validInput()){
                    Trip currentTrip = new Trip();
                    currentTripPoster.setmContactNumber(contact);
                    currentTrip.setmTripDriver(currentTripPoster);
                    if(thisTripCar != null) {
                        thisTripCar.setmColor(color);
                        thisTripCar.setmNumberPlate(license);
                        currentTrip.setmTripCar(thisTripCar);
                    }
                    currentTrip.setmSourceAddress(mSourceAddress.getText().toString());
                    currentTrip.setmDestinationAddress(mDestinationAddress.getText().toString());
                    currentTrip.setmStartDate(mStartDate.getText().toString());
                    currentTrip.setmStartTime(mStartTime.getText().toString());
                    currentTrip.setmCreationTimestamp(System.currentTimeMillis());
                    currentTrip.setmTripStatus(TRIP_UPDATED);
                    currentTrip.setmTripVisible(SUCCESS);
                    currentTrip.setmSourceNeighbordhood(mSourceNeighbordhood.getText().toString());
                    currentTrip.setmSourcePin(mSourcePin.getText().toString());
                    currentTrip.setmDestinationNeighbordhood(mDestinationNeighbordhood.getText().toString());
                    currentTrip.setmDestinationPin(mDestinationPin.getText().toString());
                    currentTrip.setmStartTimestamp(mTripStartDate);
                    currentTrip.setmTripId(uid);
                    currentTrip.setmSeatsAvailable(Integer.parseInt(mSeatsAvailable.getText().toString()));

                    try{
                        mDatabase.child(FIREBASE_TRIP_DETAILS).child(contact).setValue(currentTrip);
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
