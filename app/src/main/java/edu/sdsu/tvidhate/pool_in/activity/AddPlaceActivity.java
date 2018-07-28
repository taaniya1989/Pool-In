package edu.sdsu.tvidhate.pool_in.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.PlaceAutocompleteAdapter;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class AddPlaceActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener,GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {

    private String currentUserDisplayName;
    private DatabaseReference firebaseDatabaseInstanceReference;

    //Member Variables
    private EditText mPlaceName,mPlaceDescription;
    private ImageView mPlaceImagePreview;
    Spinner mPlaceCategory;
    private Switch mPlaceVisibility;
    private String mTripVisibility;
    private String mTripImagePath;
    private String mTripCategory = "";
    private User mTripPoster;
    private int mTripCategoryId;
    private Uri mUri,imageUrl;
    private Timestamp timestamp;
    private StorageReference firebaseStorageInstanceReference;
    private Button mSubmitButton;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    //widgets
    private AutoCompleteTextView mPlaceLocation;
    //private ImageView mGps;

    //vars
    private Boolean mLocationPermissionsGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_place);

        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabaseInstanceReference = firebaseDatabaseInstance.getReference();
        firebaseStorageInstanceReference = FirebaseStorage.getInstance().getReference();

        if(firebaseAuthInstance.getCurrentUser()!=null)
        {
            currentUserDisplayName = firebaseAuthInstance.getCurrentUser().getDisplayName();
            Log.d("TPV-NOTE","currentUserDisplayName from fire: "+currentUserDisplayName);
        }
        
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

        //Map layout components
        Button mResetButton,mPlaceImageSelectButton;
        mPlaceCategory = findViewById(R.id.placeCatergory);
        mPlaceVisibility = findViewById(R.id.placeVisibility);
        mPlaceName = findViewById(R.id.placeName);
        mPlaceLocation = findViewById(R.id.placeSearch);
        mPlaceDescription = findViewById(R.id.placeDescription);
        mPlaceImagePreview = findViewById(R.id.placeImage);
        mPlaceImageSelectButton = findViewById(R.id.add_trip_image_button);
        mResetButton = findViewById(R.id.add_trip_reset_button);
        mSubmitButton = findViewById(R.id.add_trip_submit);

        //Set on click listeners for all buttons used
        mResetButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
        mPlaceImageSelectButton.setOnClickListener(this);
        mPlaceCategory.setOnItemSelectedListener(this);

        //Enable Location
        getLocationPermission();
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_SELECTED_RESULT && resultCode == MainActivity.RESULT_OK
                && data != null && data.getData() != null)
        {
            mUri = data.getData();
            Picasso.with(this).load(mUri).resize(MainActivity.width/2,MainActivity.height/4).into(mPlaceImagePreview);
        }
        if(requestCode == IMAGE_CAPTURED_RESULT && resultCode == MainActivity.RESULT_OK
                && data != null ) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            mUri = data.getData();
            mPlaceImagePreview.setImageBitmap(photo);
        }
    }



    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.add_trip_reset_button:
                //Reset all input fields
                mPlaceCategory.setSelection(DEFAULT_TRIP_CATEGORY);
                mPlaceVisibility.setText(TRIP_VISIBLE);
                mPlaceName.setText(EMPTY_STRING);
                mPlaceLocation.setText(EMPTY_STRING);
                mPlaceDescription.setText(EMPTY_STRING);
                mPlaceImagePreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_add));
                break;

            case R.id.add_trip_submit:
                //Add trip to Database


                if (mPlaceVisibility.isChecked())
                    mTripVisibility = mPlaceVisibility.getTextOn().toString();
                else
                    mTripVisibility = mPlaceVisibility.getTextOff().toString();

                timestamp = new Timestamp(System.currentTimeMillis());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    mTripImagePath = mPlaceName.getText().toString().replaceAll(" ", "_").toLowerCase()
                            +"_"+String.valueOf(timestamp.getTime())+"_"+ user.getUid().toLowerCase()+".jpg";
                }
                StorageReference filePath = firebaseStorageInstanceReference.child(FIREBASE_PHOTO_LIST).child(mTripImagePath);

                //If Image is selected then upload to storage
                if(mUri!=null){
                    filePath.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mSubmitButton.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddPlaceActivity.this,"Uploaded Post",Toast.LENGTH_LONG).show();
                            imageUrl = taskSnapshot.getDownloadUrl();

                            //If input is valid post to database and storage
                            if(validInput())
                            {
                                Trip newTrip =  new Trip(timestamp.getTime(),firebaseDatabaseInstanceReference.child(FIREBASE_PLACE_DETAILS).push().getKey(),
                                        mPlaceName.getText().toString().trim(),mPlaceLocation.getText().toString().trim(),
                                        mTripPoster,mTripImagePath,imageUrl.toString(),mPlaceDescription.getText().toString().trim(),mTripCategory,
                                        mTripCategoryId, mTripVisibility);

                                try{
                                    firebaseDatabaseInstanceReference.child(FIREBASE_PLACE_DETAILS).child(newTrip.getmTripId()).setValue(newTrip);
                                    firebaseDatabaseInstanceReference.child(FIREBASE_CURRENT_PLACES).child(currentUserDisplayName).push().setValue(newTrip.getmTripId());
                                    Intent intent = new Intent(AddPlaceActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(intent);

                                }catch(Exception e){
                                    Log.d("TPV-NOTE","Exception: "+e);
                                }
                            }else{
                                Toast.makeText(AddPlaceActivity.this,VALIDATION_FAILURE, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }else{
                    Toast.makeText(AddPlaceActivity.this,"Add Image", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            case R.id.add_trip_image_button:
                Intent selectImageFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                selectImageFromGallery.setType("image/*");
                startActivityForResult(selectImageFromGallery, IMAGE_SELECTED_RESULT);
                break;
        }
    }

    private boolean validInput()
    {
        boolean dataValid = SUCCESS;

        //Report if Place Name is empty
        if (TextUtils.isEmpty(mPlaceName.getText().toString()))
        {
            mPlaceName.setError(ENTER_PLACE);
            dataValid = FAILURE;
        }

        //Report if Place Location is empty
        if(TextUtils.isEmpty(mPlaceLocation.getText().toString()))
        {
            mPlaceLocation.setError(ENTER_PLACE_CITY);
            dataValid = FAILURE;
        }

        //Report if Place Image is not selected
        if (TextUtils.isEmpty(mTripImagePath))
        {
            Toast.makeText(AddPlaceActivity.this,ADD_IMAGE_ERROR,Toast.LENGTH_LONG).show();
            dataValid = FAILURE;
        }

        return dataValid;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(AddPlaceActivity.this,"In the on back pressed activity",Toast.LENGTH_LONG).show();
        finish();
    }

    private void init(){
        Log.d("", "init: initializing");

        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        PlaceAutocompleteAdapter mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mPlaceLocation.setAdapter(mPlaceAutocompleteAdapter);

        mPlaceLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

    }

    private void geoLocate(){
        Log.d("rew", "geoLocate: geolocating");

        String searchString = mPlaceLocation.getText().toString();

        Geocoder geocoder = new Geocoder(AddPlaceActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e("rew", "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d("rew", "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();


        }
    }

    private void getLocationPermission(){
        Log.d("rew", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;

            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("rew", "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d("rew", "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d("rew", "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map

                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("rew","failed"+connectionResult);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mTripCategory = parent.getItemAtPosition(position).toString();
        mTripCategoryId = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
