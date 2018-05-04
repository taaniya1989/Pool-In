package edu.sdsu.tvidhate.pool_in.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Car;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class TripDetailsActivity extends AppCompatActivity implements SharedConstants
{
    private TextView source,destination,date,time,seats,poster,posterContact,car,carColor,license;
    private Button back,join,delete,update,complete;
    private DatabaseReference firebaseDatabaseInstanceReference;
    private String requestorName="";
    private String requestorContact="";
    List<String> joineeList = new ArrayList<>();
    private String uid;

    private FirebaseDatabase firebaseDatabaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        String phNo="";
        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        firebaseDatabaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabaseInstanceReference = firebaseDatabaseInstance.getReference();
        
        Bundle intent = getIntent().getExtras();
        Trip currentTrip = null;
        if(intent!=null){
            currentTrip = (Trip) intent.getSerializable(TRIP_DETAILS_SERIALIZABLE);
        }
        if(firebaseAuthInstance.getCurrentUser()!=null){
            phNo = firebaseAuthInstance.getCurrentUser().getDisplayName();
        }

        source = findViewById(R.id.trip_source);
        destination = findViewById(R.id.trip_destination);
        date = findViewById(R.id.trip_date);
        time = findViewById(R.id.trip_time);
        seats = findViewById(R.id.trip_seats);
        poster = findViewById(R.id.trip_poster);
        posterContact = findViewById(R.id.trip_contact);
        car = findViewById(R.id.trip_car);
        carColor = findViewById(R.id.trip_car_color);
        license = findViewById(R.id.trip_car_license);
        back = findViewById(R.id.trip_cancel_button);
        join = findViewById(R.id.trip_join_button);
        update = findViewById(R.id.trip_update_button);
        delete = findViewById(R.id.trip_delete_button);
        complete = findViewById(R.id.trip_complete_button);
        posterContact.setText(currentTrip.getmTripDriver().getmContactNumber());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildren() + " trips available in trip details activity");
                Trip selectedTrip = dataSnapshot.getValue(Trip.class);

                if(selectedTrip!=null)
                {
                    source.setText(selectedTrip.getmSourceAddress());
                    destination.setText(selectedTrip.getmDestinationAddress());
                    date.setText(selectedTrip.getmStartDate());
                    time.setText(selectedTrip.getmStartTime());
                    seats.setText(String.valueOf(selectedTrip.getmSeatsAvailable()));
                    poster.setText(selectedTrip.getmTripDriver().getFullName());
                    uid = selectedTrip.getmTripId();

                    Log.d("TPV-NOTE","contact from cloud: "+posterContact.getText().toString());
                    Car thisTripCar = selectedTrip.getmTripCar();
                    if(thisTripCar != null)
                    {
                        car.setText(thisTripCar.toString());
                        carColor.setText(thisTripCar.getmColor());
                        license.setText(thisTripCar.getmNumberPlate());
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TPV-NOTE","data error in trip details activity: "+databaseError);
            }
        };
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_TRIP_DETAILS).child(posterContact.getText().toString());
        people.addValueEventListener(valueEventListener);

       // requestedPassengersData(phNo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(phNo.equals(posterContact.getText().toString())){
            join.setEnabled(false);
            Log.d("TPV-NOTE","phno: "+phNo);
            Log.d("TPV-NOTE","poster contact: "+posterContact.getText().toString());
            Log.d("TPV-NOTE","Join Disabled");
            joineeList.add(posterContact.getText().toString());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TPV-NOTE","delete onclick");
                    AlertDialog.Builder alert = new AlertDialog.Builder(TripDetailsActivity.this);
                    alert.setTitle(DELETE);
                    alert.setMessage(DELETE_RIDE);
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int n) {
                            for(int i=0;i<joineeList.size();i++) {
                                ValueEventListener valueEventListener1 = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                            Log.d("TPV-NOTE", "key: " + snapshot.getKey());
                                            Log.d("TPV-NOTE", "value: " + snapshot.getValue());
                                            DatabaseReference ref = snapshot.getRef();
                                            if(snapshot.getValue().equals(uid)){
                                                Log.d("TPV-NOTE","reference: "+ref);
                                                Log.d("TPV-NOTE","reference key: "+ref.getKey());
                                                ref.removeValue();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("TPV-NOTE","database error: "+databaseError);
                                    }
                                };
                                FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                                DatabaseReference people1 = database1.getReference(FIREBASE_CURRENT_RIDES).child(joineeList.get(i));
                                people1.addValueEventListener(valueEventListener1);
                            }
                            try {
                                Log.d("TPV-NOTE","delay");
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            firebaseDatabaseInstanceReference.child(FIREBASE_REQUESTS).child(posterContact.getText().toString()).removeValue();
                            firebaseDatabaseInstanceReference.child(FIREBASE_TRIP_DETAILS).child(posterContact.getText().toString()).removeValue();
                            firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).child(uid).removeValue();
                            dialogInterface.dismiss();
                            finish();
                        }

                    });
                    alert.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();

                }
            });
            //update.setVisibility(View.GONE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(TripDetailsActivity.this,UpdateRideActivity.class);
                    startActivity(intent1);
                }
            });

            String dateString=currentTrip.getmStartDate().concat(" ").concat(currentTrip.getmStartTime());
            DateFormat formatter ;
            Date date ;
            formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
            try {
                date = formatter.parse(dateString);
                Calendar cal= Calendar.getInstance();
                cal.setTime(date);
                Log.d("TPV-NOTE","formatted time: "+date);
                Log.d("TPV-NOTE","current time: "+ Calendar.getInstance().getTime());
                if(Calendar.getInstance().getTime().after(date)){
                    complete.setVisibility(View.VISIBLE);
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("TPV-NOTE","delete onclick");
                            AlertDialog.Builder completeAlert = new AlertDialog.Builder(TripDetailsActivity.this);
                            completeAlert.setTitle(COMPLETE);
                            completeAlert.setMessage(COMPLETE_RIDE_ALERT);
                            completeAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int n) {
                                    firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).child(uid).child(APPROVAL_STATUS_CHILD).setValue(true);
                                    firebaseDatabaseInstanceReference.child(FIREBASE_REQUESTS).child(posterContact.getText().toString()).removeValue();
                                    firebaseDatabaseInstanceReference.child(FIREBASE_TRIP_DETAILS).child(posterContact.getText().toString()).removeValue();
                                    try {
                                        TimeUnit.SECONDS.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            });
                            completeAlert.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            completeAlert.show();
                        }

                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            complete.setVisibility(View.GONE);
            update.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            String dateString=currentTrip.getmStartDate().concat(" ").concat(currentTrip.getmStartTime());
            DateFormat formatter ;
            Date date ;
            formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
            try {
                date = formatter.parse(dateString);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Log.d("TPV-NOTE", "formatted time: " + date);
                Log.d("TPV-NOTE", "current time: " + Calendar.getInstance().getTime());
                if (Calendar.getInstance().getTime().after(date)) {
                    join.setEnabled(false);
                }
            }catch(Exception e){

            }
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                    if (dataSnapshot.getChildrenCount() > 0) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        requestorName = currentUser.getFullName();
                        requestorContact = currentUser.getmContactNumber();
                        Log.d("TPV-NOTE", requestorName);
                    } else {
                        Log.d("TPV-NOTE","No Data yet");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
            DatabaseReference people1 = database1.getReference(FIREBASE_PERSONAL_DATA).child(phNo);
            people1.addValueEventListener(valueEventListener1);
            final String finalPhNo = phNo;
/*            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TPV-NOTE","Different contact");
                    RequestDetailsPOJO requestDetails = new RequestDetailsPOJO();
                    requestDetails.setRequestorName(requestorName);
                    requestDetails.setRequestorContact(finalPhNo);
                    requestDetails.setPosterName(poster.getText().toString());
                    requestDetails.setPosterContact(posterContact.getText().toString());
                    try{
                        firebaseDatabaseInstanceReference.child(FIREBASE_REQUESTS).child(posterContact.getText().toString()).child(finalPhNo).setValue(requestDetails);
                        Log.d("TPV-NOTE","request data submitted");
                        finish();
                    }catch (Exception e){
                        Log.d("TPV-NOTE","Exception: "+e);
                    }
                }
            });*/
        }


    }

/*    private void requestedPassengersData(final String phNo) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildren() + " trips available in tripdetails activity");
                LinearLayout linearLayout = findViewById(R.id.text_programatically);
                joineeList.add(posterContact.getText().toString());
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                        RequestDetailsPOJO requestDetailsPOJO = msgSnapshot.getValue(RequestDetailsPOJO.class);
                        if(requestDetailsPOJO.isApprovalStatus()){
                            requestorContact = requestDetailsPOJO.getRequestorContact();
                            Log.d("TPV-NOTE","Approved for: "+requestorContact);
                            TextView passengers = new TextView(TripDetailsActivity.this);
                            passengers.setText(requestDetailsPOJO.getRequestorName().concat(" "+JOINED));
                            joineeList.add(requestDetailsPOJO.getRequestorContact());
                            passengers.setGravity(Gravity.CENTER);
                            passengers.setTypeface(Typeface.DEFAULT_BOLD);
                            linearLayout.addView(passengers);
                        }
                        if(phNo.equalsIgnoreCase(requestDetailsPOJO.getRequestorContact())){
                            join.setEnabled(false);
                        }
                        Log.d("TPV-NOTE", requestDetailsPOJO.getRequestorName());
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_REQUESTS).child(posterContact.getText().toString());
        people.addValueEventListener(valueEventListener);
    }*/
}
