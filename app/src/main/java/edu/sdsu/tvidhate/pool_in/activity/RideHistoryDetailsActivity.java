package edu.sdsu.tvidhate.pool_in.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Set;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class RideHistoryDetailsActivity extends AppCompatActivity implements SharedConstants
{

    private TextView placeName,placeCity,placeDescription;
    private ImageView placeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);


        placeName = findViewById(R.id.tripDetailsPlaceName);
        placeCity = findViewById(R.id.tripDetailsPlaceCity);
        placeDescription = findViewById(R.id.tripDetailsPlaceDescription);
        placeImage = findViewById(R.id.tripDetailsPlaceImage);

        Button back = findViewById(R.id.tripDetailsCancelButton);
        Bundle intent = getIntent().getExtras();
        String uidFromIntent = intent != null ? intent.getString(UID) : null;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                Trip myRideDetailsPOJO = dataSnapshot.getValue(Trip.class);
                if(myRideDetailsPOJO!=null){

                    //placeName.setText(myRideDetailsPOJO.getmTripPlaceName());
                    //placeCity.setText(myRideDetailsPOJO.getmTripCity());
                    placeName.setText(myRideDetailsPOJO.getmTripPlaceName());
                    placeCity.setText(myRideDetailsPOJO.getmTripCity());
                    if(myRideDetailsPOJO.getmTripDescription()!=null)
                        placeDescription.setText(myRideDetailsPOJO.getmTripDescription());
                    else
                        placeDescription.setText(".\n.\n.\n.\n.\n.");
                    Picasso.with(getApplicationContext()).load(myRideDetailsPOJO.getImageDownloadUrl()).resize(MainActivity.width,MainActivity.height/2).into(placeImage);
                    //uid = myRideDetailsPOJO.getmTripId();
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
