package edu.sdsu.tvidhate.pool_in.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class TripDetailsActivity extends AppCompatActivity implements SharedConstants
{

    private Button back;

    //join,delete,update,complete,submit;
    private FirebaseAuth firebaseAuthInstance;
    private DatabaseReference mDatabase;
    private String requestorName="";
    private String requestorContact="";
    List<String> joineeList = new ArrayList<>();
    private String uid;
    private EditText mSourceAddress,mDestinationAddress,mStartDate,mStartTime,mSeatsAvailable,mTripPoster,mTripPosterContact,mTripCarName,mTripCarRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        String phNo="";
        firebaseAuthInstance = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle intent = getIntent().getExtras();
        Trip currentTrip = null;

        if(intent!=null){
            currentTrip = (Trip) intent.getSerializable(TRIP_DETAILS_SERIALIZABLE);
        }
        if(firebaseAuthInstance.getCurrentUser()!=null){
            phNo = firebaseAuthInstance.getCurrentUser().getDisplayName();
        }

        mSourceAddress = findViewById(R.id.tripDetailsSourceAddress);
        mDestinationAddress = findViewById(R.id.tripDetailsDestinationAddress);
        mStartDate = findViewById(R.id.tripDetailsStartDate);
        mStartTime = findViewById(R.id.tripDetailsStartTime);
        mSeatsAvailable = findViewById(R.id.tripDetailsSeatsAvailable);
        mTripPoster = findViewById(R.id.tripDetailsDriverName);
        mTripPosterContact = findViewById(R.id.tripDetailsDriverContact);
        mTripCarName = findViewById(R.id.tripDetailsCarName);
        mTripCarRegistration = findViewById(R.id.tripDetailsCarRegistration);

        back = findViewById(R.id.tripDetailsBackButton);
     //   submit = findViewById(R.id.tripDetailsSubmitButton);
     //   update = findViewById(R.id.tripDetailsBackButton);
     //   delete = findViewById(R.id.tripDetailsBackButton);
     //   complete = findViewById(R.id.tripDetailsBackButton);
        mTripPosterContact.setText(currentTrip.getmTripDriver().getmContactNumber());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildren() + " trips available in trip details activity");
                Trip currentTrip = dataSnapshot.getValue(Trip.class);
                if(currentTrip!=null) {
                    mSourceAddress.setText(currentTrip.getmSourceAddress());
                    mDestinationAddress.setText(currentTrip.getmDestinationAddress());
                    mStartDate.setText(currentTrip.getmStartDate());
                    mStartTime.setText(currentTrip.getmStartTime());
                    mSeatsAvailable.setText(String.valueOf(currentTrip.getmSeatsAvailable()));
                    mTripPoster.setText(currentTrip.getmTripDriver().getFullName());
                    uid = currentTrip.getmTripId();
                    //Handle Car
                  //  mTripCarName.setText(currentTrip.getmTripCar().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("rew","data error in trip details activity: "+databaseError);
            }
        };
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_TRIP_DETAILS).child(mTripPosterContact.getText().toString());
        people.addValueEventListener(valueEventListener);

     //   requestedPassengersData(phNo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(phNo.equals(mTripPosterContact.getText().toString())){
  //          join.setEnabled(false);
            Log.d("rew","phno: "+phNo);
            Log.d("rew","poster contact: "+mTripPosterContact.getText().toString());
            Log.d("rew","Join Disabled");
            joineeList.add(mTripPosterContact.getText().toString());
      /*      delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("rew","delete onclick");
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
                                        Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                            Log.d("rew", "key: " + snapshot.getKey());
                                            Log.d("rew", "value: " + snapshot.getValue());
                                            DatabaseReference ref = snapshot.getRef();
                                            if(snapshot.getValue().equals(uid)){
                                                Log.d("rew","reference: "+ref);
                                                Log.d("rew","reference key: "+ref.getKey());
                                                ref.removeValue();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("rew","database error: "+databaseError);
                                    }
                                };
                                FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                                DatabaseReference people1 = database1.getReference(FIREBASE_CURRENT_RIDES).child(joineeList.get(i));
                                people1.addValueEventListener(valueEventListener1);
                            }
                            try {
                                Log.d("rew","delay");
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mDatabase.child(FIREBASE_REQUESTS).child(mTripPosterContact.getText().toString()).removeValue();
                            mDatabase.child(FIREBASE_TRIP_DETAILS).child(mTripPosterContact.getText().toString()).removeValue();
                            mDatabase.child(FIREBASE_MY_RIDES).child(uid).removeValue();
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
      */    /*  update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(TripDetailsActivity.this,UpdateRideActivity.class);
                    startActivity(intent1);
                }
            });*/

            String dateString=currentTrip.getmStartDate().concat(" ").concat(currentTrip.getmStartTime());
            DateFormat formatter ;
            Date date ;
            formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
            try {
                date = formatter.parse(dateString);
                Calendar cal= Calendar.getInstance();
                cal.setTime(date);
                Log.d("rew","formatted time: "+date);
                Log.d("rew","current time: "+ Calendar.getInstance().getTime());
                if(Calendar.getInstance().getTime().after(date)){
            /*        complete.setVisibility(View.VISIBLE);
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("rew","delete onclick");
                            AlertDialog.Builder completeAlert = new AlertDialog.Builder(TripDetailsActivity.this);
                            completeAlert.setTitle(COMPLETE);
                            completeAlert.setMessage(COMPLETE_RIDE_ALERT);
                            completeAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int n) {
                                    mDatabase.child(FIREBASE_MY_RIDES).child(uid).child(APPROVAL_STATUS_CHILD).setValue(true);
                                    mDatabase.child(FIREBASE_REQUESTS).child(mTripPosterContact.getText().toString()).removeValue();
                                    mDatabase.child(FIREBASE_TRIP_DETAILS).child(mTripPosterContact.getText().toString()).removeValue();
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
            */    }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{/*
            complete.setVisibility(View.GONE);
            update.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);*/
            String dateString=currentTrip.getmStartDate().concat(" ").concat(currentTrip.getmStartTime());
            DateFormat formatter ;
            Date date ;
            formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
            try {
                date = formatter.parse(dateString);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Log.d("rew", "formatted time: " + date);
                Log.d("rew", "current time: " + Calendar.getInstance().getTime());
                if (Calendar.getInstance().getTime().after(date)) {
                    //join.setEnabled(false);
                }
            }catch(Exception e){

            }
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                    if (dataSnapshot.getChildrenCount() > 0) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        requestorName = currentUser.getFullName();
                        requestorContact = currentUser.getmContactNumber();
                        Log.d("rew", requestorName);
                    } else {
                        Log.d("rew","No Data yet");
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
                    Log.d("rew","Different contact");
                    RequestDetailsPOJO requestDetails = new RequestDetailsPOJO();
                    requestDetails.setRequestorName(requestorName);
                    requestDetails.setRequestorContact(finalPhNo);
                    requestDetails.setPosterName(poster.getText().toString());
                    requestDetails.setPosterContact(posterContact.getText().toString());
                    try{
                        mDatabase.child(FIREBASE_REQUESTS).child(posterContact.getText().toString()).child(finalPhNo).setValue(requestDetails);
                        Log.d("rew","request data submitted");
                        finish();
                    }catch (Exception e){
                        Log.d("rew","Exception: "+e);
                    }
                }
            });*/
        }


    }

    /*private void requestedPassengersData(final String phNo) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildren() + " trips available in tripdetails activity");
                LinearLayout linearLayout = findViewById(R.id.text_programatically);
                joineeList.add(posterContact.getText().toString());
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                        RequestDetailsPOJO requestDetailsPOJO = msgSnapshot.getValue(RequestDetailsPOJO.class);
                        if(requestDetailsPOJO.isApprovalStatus()){
                            requestorContact = requestDetailsPOJO.getRequestorContact();
                            Log.d("rew","Approved for: "+requestorContact);
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
                        Log.d("rew", requestDetailsPOJO.getRequestorName());
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
