package edu.sdsu.tvidhate.pool_in.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.PlaceAutocompleteAdapter;

import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.EMPTY_STRING;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.ENTER_PLACE;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.ENTER_PLACE_CITY;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.ENTER_PLACE_PIN;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.FAILURE;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.FIREBASE_CURRENT_RIDES;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.FIREBASE_MY_RIDES;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.FIREBASE_PERSONAL_DATA;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.FIREBASE_PHOTO_LIST;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.IMAGE_CAPTURED_RESULT;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.IMAGE_SELECTED_RESULT;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.SUCCESS;
import static edu.sdsu.tvidhate.pool_in.helper.SharedConstants.VALIDATION_FAILURE;

public class AddPlaceActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
    private String currentUserDisplayName;
    private User mTripPoster;
    private DatabaseReference firebaseDatabaseInstanceReference;
    private FirebaseDatabase firebaseDatabaseInstance;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    //Member Variables
    private EditText mPlaceName;
    private Button mPlaceImageSelectButton;
    private ImageView mPlaceImagePreview;
    private String mTripImagePath;
    private Uri mUri;
    private StorageReference mStorage;
    private Uri imageUrl;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        //mGps =  findViewById(R.id.ic_gps);
        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        if(firebaseAuthInstance.getCurrentUser()!=null)
        {
            currentUserDisplayName = firebaseAuthInstance.getCurrentUser().getDisplayName();
            Log.d("TPV-NOTE","currentUserDisplayName from fire: "+currentUserDisplayName);
        }

        firebaseDatabaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabaseInstanceReference = firebaseDatabaseInstance.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        Toast.makeText(AddPlaceActivity.this,"In the new activity",Toast.LENGTH_LONG).show();
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

        Button mResetButton,mSubmitButton;



        mPlaceName = findViewById(R.id.placeName);
        mResetButton = findViewById(R.id.add_trip_reset_button);
        mSubmitButton = findViewById(R.id.add_trip_submit);
        mPlaceImageSelectButton = findViewById(R.id.add_trip_image_button);
        mPlaceImagePreview = findViewById(R.id.placeImage);

        mResetButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
        mPlaceImageSelectButton.setOnClickListener(this);
        mSearchText = findViewById(R.id.placeSearch);


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
//            selectedImage.setImageURI(mUri);
            Picasso.with(this).load(mUri).resize(MainActivity.width/2,MainActivity.height/4).into(mPlaceImagePreview);
            Log.i("VANILLA_INFO",mUri.toString());
        }
        if(requestCode == IMAGE_CAPTURED_RESULT && resultCode == MainActivity.RESULT_OK
                && data != null ) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mUri = data.getData();
            //selectedImage.setImageURI(mUri);
            mPlaceImagePreview.setImageBitmap(photo);

            Log.i("VANILLA_INFO",photo.toString());
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.add_trip_reset_button:
                mPlaceName.setText(EMPTY_STRING);
                mSearchText.setText(EMPTY_STRING);
                break;

            case R.id.add_trip_submit:
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                mTripImagePath = mPlaceName.getText().toString().replaceAll(" ", "_").toLowerCase()
                        +"_"+String.valueOf(timestamp.getTime())+"_"+user.getUid().toString().toLowerCase()+".jpg";

                Log.i("PhotoPath",mTripImagePath);
                StorageReference filePath = mStorage.child(FIREBASE_PHOTO_LIST).child(mTripImagePath);

                Map<String ,Object > data = new HashMap<String, Object>();
                data.put(mTripImagePath,mTripImagePath);
                firebaseDatabaseInstanceReference.child(FIREBASE_PHOTO_LIST).updateChildren(data);

                filePath.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageUrl = taskSnapshot.getDownloadUrl();
                        Log.d("rew","Image download URL :"+imageUrl);
                        Toast.makeText(AddPlaceActivity.this,"Uploaded Image",Toast.LENGTH_LONG).show();
                        if(validInput())
                        {
                            Trip newTrip = new Trip(System.currentTimeMillis(),firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).push().getKey(),
                                    mPlaceName.getText().toString().trim(),mSearchText.getText().toString().trim(),
                                    mTripPoster,mTripImagePath,imageUrl.toString(),mPlaceName.getText().toString().trim());

                            Log.d("TPV-NOTE","uid: "+newTrip.getmTripId());
                            try{
                                firebaseDatabaseInstanceReference.child(FIREBASE_MY_RIDES).child(newTrip.getmTripId()).setValue(newTrip);
                                firebaseDatabaseInstanceReference.child(FIREBASE_CURRENT_RIDES).child(currentUserDisplayName).push().setValue(newTrip.getmTripId());
                                Intent intent = new Intent(AddPlaceActivity.this, MainActivity.class);
                                finish();
                                startActivity(intent);
                            }catch(Exception e){
                                Log.d("TPV-NOTE","Exception: "+e);
                            }
                        }else{
                            Toast.makeText(AddPlaceActivity.this,VALIDATION_FAILURE, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


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
        if (TextUtils.isEmpty(mPlaceName.getText().toString()))
        {
            mPlaceName.setError(ENTER_PLACE);
            dataValid = FAILURE;
        }
        if(TextUtils.isEmpty(mSearchText.getText().toString()))
        {
            mSearchText.setError(ENTER_PLACE_CITY);
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
        finish();
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
                            Toast.makeText(AddPlaceActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
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
}
