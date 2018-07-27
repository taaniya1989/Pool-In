package edu.sdsu.tvidhate.pool_in.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class TripDetailsActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener
{
    private TextView placeName,placeDescription,placeLocation,placeCategory;
    private Switch placeVisibility;
    private DatabaseReference firebaseDatabaseInstanceReference;
    private StorageReference firebaseStorageInstanceReference;
    private String posterContact;
    private String uid;
    private String currentUserDisplayName="";
    private ImageView placeImage;
    Trip currentTrip = null;

    private User poster;
    private String placeImagePath,placeImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        
        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        firebaseDatabaseInstanceReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorageInstanceReference = FirebaseStorage.getInstance().getReference();

        if(firebaseAuthInstance.getCurrentUser()!=null){
            currentUserDisplayName = firebaseAuthInstance.getCurrentUser().getDisplayName();
        }

        Bundle intent = getIntent().getExtras();
        if(intent!=null){
            currentTrip = (Trip) intent.getSerializable(TRIP_DETAILS_SERIALIZABLE);
        }

        //Map layout components
        placeCategory = findViewById(R.id.tripDetailsPlaceCategory);
        placeVisibility = findViewById(R.id.tripDetailsPlaceVisibility);
        placeName = findViewById(R.id.tripDetailsPlaceName);
        placeLocation = findViewById(R.id.tripDetailsPlaceCity);
        placeDescription = findViewById(R.id.tripDetailsPlaceDescription);
        placeImage = findViewById(R.id.tripDetailsPlaceImage);
        Button back = findViewById(R.id.tripDetailsCancelButton);
        Button update = findViewById(R.id.tripDetailsUpdateButton);
        Button delete = findViewById(R.id.tripDetailsDeleteButton);
        Button navigate = findViewById(R.id.tripDetailsNavigateButton);

        //Set on click listeners for all buttons used
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        update.setOnClickListener(this);
        navigate.setOnClickListener(this);

        if(currentTrip != null){
            poster = currentTrip.getmTripPoster();
            posterContact = poster.getmContactNumber();
        }else
            Toast.makeText(this,"No Trip found for this User",Toast.LENGTH_LONG).show();

        placeVisibility.setVisibility(View.INVISIBLE);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildren() + " trips available in trip details activity");
                Trip selectedTrip = dataSnapshot.getValue(Trip.class);

                if(selectedTrip!=null)
                {
                    if(selectedTrip.getmTripCategory()==null || selectedTrip.getmTripCategory().isEmpty())
                        placeCategory.setText(DEFAULT_CATEGORY);
                    else
                        placeCategory.setText(selectedTrip.getmTripCategory());
                    placeVisibility.setText(selectedTrip.getmTripVisibility());

                    placeName.setText(selectedTrip.getmTripPlaceName());
                    placeLocation.setText(selectedTrip.getmTripCity());
                    if(selectedTrip.getmTripDescription()!=null)
                        placeDescription.setText(selectedTrip.getmTripDescription());
                    else
                        placeDescription.setText(".\n.\n.\n.\n.\n.");

                    placeImagePath = selectedTrip.getmTripImagePath();
                    placeImageURL = selectedTrip.getImageDownloadUrl();

                    Picasso.with(getApplicationContext()).load(placeImageURL).resize(MainActivity.width,MainActivity.height/2).into(placeImage);
                    uid = selectedTrip.getmTripId();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TPV-NOTE","data error in trip details activity: "+databaseError);
            }
        };

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_PLACE_DETAILS).child(currentTrip.getmTripId());
        people.addValueEventListener(valueEventListener);


        if(!currentUserDisplayName.equals(poster.getmContactNumber())){
            update.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
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

                        firebaseDatabaseInstanceReference.child(FIREBASE_REQUESTS).child(posterContact).removeValue();
                        firebaseDatabaseInstanceReference.child(FIREBASE_PLACE_DETAILS).child(uid).removeValue();
                        firebaseStorageInstanceReference.child(FIREBASE_PHOTO_LIST).child(placeImagePath).delete();
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

            case R.id.tripDetailsNavigateButton:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+placeLocation.getText()));
                startActivity(intent);
        }
    }
}
