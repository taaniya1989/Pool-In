package edu.sdsu.tvidhate.pool_in.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Set;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class RideHistoryDetailsActivity extends AppCompatActivity implements SharedConstants
{
    private TextView source;
    private TextView destination;
    private TextView date;
    private TextView time;
    private TextView joinees;
    private TextView poster;
    private TextView posterContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_details);


        source = findViewById(R.id.ride_history_source);
        destination = findViewById(R.id.ride_history_destination);
        date = findViewById(R.id.ride_history_date);
        time = findViewById(R.id.ride_history_time);
        joinees = findViewById(R.id.ride_history_people);
        poster = findViewById(R.id.ride_history_posted_by);
        posterContact = findViewById(R.id.ride_history_poster_contact);
        Button back = findViewById(R.id.ride_history_back);
        Bundle intent = getIntent().getExtras();
        String uidFromIntent = intent != null ? intent.getString(UID) : null;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                Trip myRideDetailsPOJO = dataSnapshot.getValue(Trip.class);
                if(myRideDetailsPOJO!=null){
                    source.setText(myRideDetailsPOJO.getmSourceAddress());
                    destination.setText(myRideDetailsPOJO.getmDestinationAddress());
                    date.setText(myRideDetailsPOJO.getmStartDate());
                    time.setText(myRideDetailsPOJO.getmStartTime());
                  //  poster.setText(myRideDetailsPOJO.getmTripDriver().getFullName());
                    posterContact.setText(myRideDetailsPOJO.getmTripDriver().getmContactNumber());

                    Map<String,User> currentTripPassengers = myRideDetailsPOJO.getmTripPassengers();
                    String currentTripPassengersNames = "";
                    if(currentTripPassengers != null)
                    {
                        Set currentTripPassengersContact = currentTripPassengers.keySet();
                        int passengerCount = 0;
                        for(Object contact : currentTripPassengersContact)
                        {
                            passengerCount++;
//                            if(currentTripPassengersContact.size() == 1)
//                                currentTripPassengersNames = currentTripPassengers.get(contact).getFullName();
//                            else {
//                                if(passengerCount == 1)
//                                    currentTripPassengersNames = currentTripPassengers.get(contact).getFullName();
//                                else
//                                    currentTripPassengersNames += "\n" + currentTripPassengers.get(contact).getFullName();
//                            }
                        }
                    }
                    joinees.setText(currentTripPassengersNames);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (uidFromIntent != null)
        {
            DatabaseReference people = database.getReference(FIREBASE_MY_RIDES).child(uidFromIntent);
            people.addValueEventListener(valueEventListener);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
