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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class TripDetailsActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener
{
   // private TextView source,destination,date,time,seats,poster,posterContact,car,carColor,license;
    private TextView placeName,placeCity,placePin;
    private Button join;
    private DatabaseReference firebaseDatabaseInstanceReference;
    private String requestorName="";
    private String requestorContact="";
    private String posterContact;
    private List<String> joineeList = new ArrayList<>();
    private String uid;
    private String phNo="";
    private ImageView placeImage;
    Trip currentTrip = null;

    private User joiningRequester,approverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabaseInstanceReference = firebaseDatabaseInstance.getReference();
        
        Bundle intent = getIntent().getExtras();
        if(intent!=null){
            currentTrip = (Trip) intent.getSerializable(TRIP_DETAILS_SERIALIZABLE);
        }
        if(firebaseAuthInstance.getCurrentUser()!=null){
            phNo = firebaseAuthInstance.getCurrentUser().getDisplayName();
        }

        placeName = findViewById(R.id.tripDetailsPlaceName);
        placeCity = findViewById(R.id.tripDetailsPlaceCity);
        //placePin = findViewById(R.id.tripDetailsPlacePin);
        placeImage = findViewById(R.id.tripDetailsPlaceImage);

        Button back = findViewById(R.id.tripDetailsCancelButton);
        Button update = findViewById(R.id.tripDetailsUpdateButton);
        Button delete = findViewById(R.id.tripDetailsDeleteButton);

        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        update.setOnClickListener(this);

        Log.i("!!!!!",currentTrip.toString());
        if(currentTrip != null){
            approverUser = currentTrip.getmTripPoster();
            posterContact = approverUser.getmContactNumber();
        }else
            Toast.makeText(this,"No Trip found for this User",Toast.LENGTH_LONG).show();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildren() + " trips available in trip details activity");
                Trip selectedTrip = dataSnapshot.getValue(Trip.class);

                if(selectedTrip!=null)
                {
                    placeName.setText(selectedTrip.getmTripPlaceName());
                //    placePin.setText(selectedTrip.getmTripPincode());
                    placeCity.setText(selectedTrip.getmTripCity());
                    Picasso.with(getApplicationContext()).load(selectedTrip.getImageDownloadUrl()).resize(MainActivity.width,MainActivity.height/2).into(placeImage);
                    uid = selectedTrip.getmTripId();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TPV-NOTE","data error in trip details activity: "+databaseError);
            }
        };
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_MY_RIDES).child(currentTrip.getmTripId());
        people.addValueEventListener(valueEventListener);

        if(phNo.equals(posterContact)){
            Log.d("TPV-NOTE","phno: "+phNo);
            Log.d("TPV-NOTE","poster contact: "+posterContact);
            Log.d("TPV-NOTE","Join Disabled");
        }else{
            update.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " trips available");
                    if (dataSnapshot.getChildrenCount() > 0) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        joiningRequester = currentUser;
                        if (currentUser != null) {
                            requestorContact = currentUser.getmContactNumber();
                        }
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
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tripDetailsCancelButton:
                finish();
                break;

            case R.id.tripDetailsUpdateButton:
                Intent intent1 = new Intent(TripDetailsActivity.this,UpdateRideActivity.class);
                intent1.putExtra(TRIP_DETAILS_SERIALIZABLE,currentTrip);
                startActivity(intent1);
                break;

            case R.id.tripDetailsDeleteButton:
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
                                        if(Objects.requireNonNull(snapshot.getValue()).equals(uid)){
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
                        firebaseDatabaseInstanceReference.child(FIREBASE_REQUESTS).child(posterContact).removeValue();
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
                break;
        }
    }
}
