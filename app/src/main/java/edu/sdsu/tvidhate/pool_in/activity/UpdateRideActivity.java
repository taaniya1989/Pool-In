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
import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Car;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class UpdateRideActivity extends AppCompatActivity implements SharedConstants{
    private EditText source,destination,seats;
    private TextView date,time;
    private Button datePicker,timePicker,back,update;
    private int mYear,mMonth,mDay,mHour,mMinute;
    private DatabaseReference mDatabase;
    private String name,contact,car,color,license,uid;
    private FirebaseAuth auth;
    private User currentTripPoster;
    private Car thisTripCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ride);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("rew","firebase ref: "+mDatabase.toString());
        source = findViewById(R.id.update_trip_from);
        destination = findViewById(R.id.update_trip_to);
        date = findViewById(R.id.update_trip_date_text);
        time = findViewById(R.id.update_trip_time_text);
        seats = findViewById(R.id.update_trip_number_of_seats);
        datePicker = findViewById(R.id.update_trip_date_button);
        timePicker = findViewById(R.id.update_trip_time_button);
        back = findViewById(R.id.update_trip_back_button);
        update = findViewById(R.id.update_trip_submit);
        if(auth.getCurrentUser()!=null){
            contact = auth.getCurrentUser().getDisplayName();
        }
        selectDate();
        selectTime();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                Trip currentTrip = dataSnapshot.getValue(Trip.class);
                if(currentTrip != null){
                    currentTripPoster = currentTrip.getmTripDriver();
                    name = currentTripPoster.getFullName();
                    thisTripCar = currentTrip.getmTripCar();
                    if(thisTripCar != null)
                    {
                        car = thisTripCar.toString();
                        color = thisTripCar.getmColor();
                        license = thisTripCar.getmNumberPlate();
                    }
                    source.setText(currentTrip.getmSourceAddress());
                    destination.setText(currentTrip.getmDestinationAddress());
                    date.setText(currentTrip.getmStartDate());
                    time.setText(currentTrip.getmStartTime());
                    seats.setText(String.valueOf(currentTrip.getmSeatsAvailable()));
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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validInput()){
                    Trip currentTrip = new Trip();
                    currentTripPoster.setmContactNumber(contact);
                    currentTrip.setmTripDriver(currentTripPoster);
                    if(thisTripCar != null) {
                        thisTripCar.setmColor(color);
                        thisTripCar.setmNumberPlate(license);
                        currentTrip.setmTripCar(thisTripCar);
                    }
                    currentTrip.setmSourceAddress(source.getText().toString());
                    currentTrip.setmDestinationAddress(destination.getText().toString());
                    currentTrip.setmStartDate(date.getText().toString());
                    currentTrip.setmStartTime(time.getText().toString());

                    currentTrip.setmTripId(uid);
                    currentTrip.setmSeatsAvailable(Integer.parseInt(seats.getText().toString()));

                    try{
                        mDatabase.child(FIREBASE_TRIP_DETAILS).child(contact).setValue(currentTrip);
                        Log.d("rew","Data updated successfully");
                        finish();
                    }catch(Exception e){
                        Log.d("rew","Exception: "+e);
                    }
                }else{
                    Toast.makeText(UpdateRideActivity.this,ENTER_REQUIRED_FIELDS, Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(source.getText().toString())) {
            source.setError(ENTER_SOURCE);
            dataValid = false;
        }
        if (TextUtils.isEmpty(destination.getText().toString())) {
            destination.setError(ENTER_DESTINATION);
            dataValid = false;
        }
        if (TextUtils.isEmpty(date.getText().toString())) {
            date.setError(ENTER_DATE);
            dataValid = false;
        }
        if (TextUtils.isEmpty(time.getText().toString())) {
            time.setError(ENTER_TIME);
            dataValid = false;
        }
        if (TextUtils.isEmpty(seats.getText().toString())) {
            seats.setError(ENTER_SEATS);
            dataValid = false;
        }
        if (Integer.parseInt(seats.getText().toString())<1) {
            seats.setError(SEATS_ZERO);
            dataValid = false;
        }
        String dateString=date.getText().toString().concat(" ").concat(time.getText().toString());
        DateFormat formatter ;
        Date date ;
        formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        try {
            date = formatter.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Log.d("rew", "formatted time: " + date);
            Log.d("rew", "current time: " + Calendar.getInstance().getTime());
            if(date.before(Calendar.getInstance().getTime())){
                dataValid = false;
            }
        }catch (Exception e){
            Log.d("rew","Exception: "+e);
        }
        return dataValid;
    }



    private void selectTime() {
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateRideActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String formatTime = hourOfDay + ":" + minute;
                                time.setText(formatTime);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private void selectDate() {
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateRideActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formatDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                date.setText(formatDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
