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
import com.google.android.gms.maps.GoogleMap;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.PlaceAutocompleteAdapter;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class UpdateRideActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener, AdapterView.OnItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private EditText mPlaceName,mPlaceCity,mPlaceDescription;
    private ImageView mPlaceImage;
    private Spinner mPlaceCategory;
    private Switch mPlaceVisibility;
    private DatabaseReference mDatabase;
    private String contact,uid;
    private Trip currentTrip = null;
    private String mTripCategory,mTripVisibility;
    private String mTripImagePath;
    private StorageReference mStorage;
    private DatabaseReference firebaseDatabaseInstanceReference;
    private Uri mUri = null;
    private Uri imageUrl;
    private String currentUserDisplayName;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private int mTripCategoryId;
    private Button mSelectImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseDatabaseInstanceReference = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        Button mBackButton,mUpdateButton;

        mPlaceName = findViewById(R.id.placeName);
        mPlaceCity = findViewById(R.id.placeSearch);
        mPlaceDescription = findViewById(R.id.placeDescription);
        mPlaceCategory = findViewById(R.id.placeCatergory);
        mPlaceImage = findViewById(R.id.placeImage);
        mPlaceVisibility = findViewById(R.id.placeVisibility);
        mSearchText = findViewById(R.id.placeSearch);
        mGps = findViewById(R.id.ic_gps);

        mBackButton = findViewById(R.id.add_trip_reset_button);
        mUpdateButton = findViewById(R.id.add_trip_submit);
        mSelectImageButton = findViewById(R.id.add_trip_image_button);

        mBackButton.setText(R.string.back);
        mUpdateButton.setText(R.string.update);
        mBackButton.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);
        mSelectImageButton.setOnClickListener(this);
        

        mPlaceCategory.setOnItemSelectedListener(this);
        getLocationPermission();
        init();

        Bundle intent = getIntent().getExtras();
        if(intent!=null){
            currentTrip = (Trip) intent.getSerializable(TRIP_DETAILS_SERIALIZABLE);
        }
        if(auth.getCurrentUser()!=null)
        {
            currentUserDisplayName = auth.getCurrentUser().getDisplayName();
            Log.d("TPV-NOTE","currentUserDisplayName from fire: "+currentUserDisplayName);
        }

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                currentTrip = dataSnapshot.getValue(Trip.class);
                if(currentTrip != null)
                {
                    mPlaceCategory.setSelection(currentTrip.getmTripCatergoryId());
                    mPlaceVisibility.setText(currentTrip.getmTripVisibility());
                    mPlaceName.setText(currentTrip.getmTripPlaceName());
                    mPlaceCity.setText(currentTrip.getmTripCity());
                    mPlaceDescription.setText(currentTrip.getmTripDescription());
                    //mPlaceCategory
                    Picasso.with(getApplicationContext()).load(currentTrip.getImageDownloadUrl()).resize(MainActivity.width,MainActivity.height/2).into(mPlaceImage);
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
        return dataValid;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_SELECTED_RESULT && resultCode == MainActivity.RESULT_OK
                && data != null && data.getData() != null)
        {
            mUri = data.getData();
//            selectedImage.setImageURI(mUri);
            Picasso.with(this).load(mUri).resize(MainActivity.width/2,MainActivity.height/4).into(mPlaceImage);
            Log.i("VANILLA_INFO",mUri.toString());
        }
        if(requestCode == IMAGE_CAPTURED_RESULT && resultCode == MainActivity.RESULT_OK
                && data != null ) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mUri = data.getData();
            //selectedImage.setImageURI(mUri);
            mPlaceImage.setImageBitmap(photo);

            Log.i("VANILLA_INFO",photo.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_trip_reset_button:
                finish();
                break;

            case R.id.add_trip_submit:

                if (mPlaceVisibility.isChecked())
                    mTripVisibility = mPlaceVisibility.getTextOn().toString();
                else
                    mTripVisibility = mPlaceVisibility.getTextOff().toString();

                if(validInput()) {

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    //Update Time stamp
                    currentTrip.setmCreationTimestamp(timestamp.getTime());

                    //Update Trip ImagePath and URI if new image is selected.
                    if(mUri!=null)
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Log.i("NOTE-TPV","Updating trip image information");
                        mTripImagePath = mPlaceName.getText().toString().replaceAll(" ", "_").toLowerCase()
                                + "_" + String.valueOf(timestamp.getTime()) + "_" + user.getUid().toString().toLowerCase() + ".jpg";
                        mStorage.child(FIREBASE_PHOTO_LIST).child(currentTrip.getmTripImagePath()).delete();
                        StorageReference filePath = mStorage.child(FIREBASE_PHOTO_LIST).child(mTripImagePath);
                        currentTrip.setmTripImagePath(mTripImagePath);

                        filePath.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageUrl = taskSnapshot.getDownloadUrl();
                                currentTrip.setImageDownloadUrl(imageUrl.toString());
                                Log.d("rew", "Image download URL :" + imageUrl);
                                Log.i("rew",currentTrip.toString());
                                Toast.makeText(UpdateRideActivity.this, "Uploaded Image", Toast.LENGTH_LONG).show();
                            }
                        });
                       // Log.d("rew", "Image download URL :" + imageUrl);
                       // currentTrip.setImageDownloadUrl(imageUrl.toString());
                    }

                    //Update Visibility only if changed
                    if (!mTripVisibility.equalsIgnoreCase(currentTrip.getmTripVisibility()) || currentTrip.getmTripVisibility()==null)
                        currentTrip.setmTripVisibility(mTripVisibility);

                    //Update Category only if changed
                    if (!mTripCategory.equalsIgnoreCase(currentTrip.getmTripCategory()) || currentTrip.getmTripCategory()==null)
                    {
                        currentTrip.setmTripCategory(mTripCategory);
                        currentTrip.setmTripCatergoryId(mTripCategoryId);
                    }

                    //Update description if changed
                    if(!mPlaceDescription.getText().toString().equalsIgnoreCase(currentTrip.getmTripDescription()) || currentTrip.getmTripDescription()==null)
                        currentTrip.setmTripDescription(mPlaceDescription.getText().toString().trim());

                    //Update location if changed
                    if(!mPlaceCity.getText().toString().equalsIgnoreCase(currentTrip.getmTripCity()) || currentTrip.getmTripCity()==null)
                        currentTrip.setmTripCity(mPlaceCity.getText().toString().trim());

                    //Update place name if changed
                    if(!mPlaceName.getText().toString().equalsIgnoreCase(currentTrip.getmTripPlaceName()) || currentTrip.getmTripPlaceName()==null)
                        currentTrip.setmTripPlaceName(mPlaceName.getText().toString().trim());

                    try {
                            Log.i("rew",currentTrip.toString());
                            firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).child(currentTrip.getmTripId()).setValue(currentTrip);
                           // firebaseDatabaseInstanceReference.child(FIREBASE_CURRENT_RIDES).child(currentUserDisplayName).up.setValue(currentTrip.getmTripId());
                            Intent intent = new Intent(UpdateRideActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.d("TPV-NOTE", "Exception: " + e);
                        }
                }
                break;

            case R.id.add_trip_image_button:
                Intent selectImageFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                selectImageFromGallery.setType("image/*");
                startActivityForResult(selectImageFromGallery, IMAGE_SELECTED_RESULT);
                break;
        }
    }

    private void init(){
        Log.d("", "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("rew", "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });


    }

    private void geoLocate(){
        Log.d("rew", "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(UpdateRideActivity.this);
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

    private void getDeviceLocation(){
        Log.d("rew", "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d("rew", "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();



                        }else{
                            Log.d("rew", "onComplete: current location is null");
                            Toast.makeText(UpdateRideActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("rew", "getDeviceLocation: SecurityException: " + e.getMessage() );
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
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        mTripCategory = parent.getItemAtPosition(position).toString();
        mTripCategoryId = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
