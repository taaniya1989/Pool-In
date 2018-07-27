package edu.sdsu.tvidhate.pool_in.helper;


import android.Manifest;

public interface SharedConstants {
    String EMPTY_STRING = "";
    String INVALID_EMAIL_PW_TOAST = "Invalid email or password";
    String ENTER_EMAIL = "Enter Email ID";
    String ENTER_PASSWORD = "Enter Password";
    String PASSWORD_LENGTH = "Minimum length of Password is 6";
    String INVALID_EMAIL = "Invalid Email ID";
    String REGISTRATION_FAILED = "Registration failed";
    String ENTER_REQUIRED_FIELDS = "Enter Required Fields";
    int IMAGE_SELECTED_RESULT = 2;
    int IMAGE_CAPTURED_RESULT = 3;
    int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    int DEFAULT_TRIP_CATEGORY = 1;
    String FIREBASE_PHOTO_LIST = "PhotoFileNames";

    String DEFAULT_CATEGORY = "No Category";
    String TRIP_VISIBLE = "Share";
    String TRIP_INVISIBLE = "Hide";
    String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    boolean SUCCESS = true;
    boolean FAILURE = false;

    String VALIDATION_FAILURE = "Validation Failure";
    String ADD_IMAGE_ERROR = "Please select image for post";
    String TAG_HOME = "home";
    String TAG_ADD_TRIP = "add_trip";
    String TAG_MY_TRIPS = "my_trips";
    String TAG_MY_PROFILE = "my_profile";
    String TAG_REQUESTS = "requests";
    String TAG_LOG_OUT= "logout";
    String UID = "uid";
    String COMPLETED = "Completed";
    String WAITING = "Waiting";

    String FIREBASE_PLACE_DETAILS = "place_details";
    String FIREBASE_CURRENT_PLACES = "current_places";
    String FIREBASE_REQUESTS = "requests";
    String TRIP_DETAILS_SERIALIZABLE = "tripDetailsFromPoster";
    String DELETE = "Delete";
    String DELETE_RIDE = "Delete Ride?";
    String CANCEL = "CANCEL";
    String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm";
    String COMPLETE = "Complete";
    String COMPLETE_RIDE_ALERT = "Did you complete the ride?";
    String APPROVAL_STATUS_CHILD = "approvalStatus";
    String FIREBASE_PERSONAL_DATA = "personal_data";
    String JOINED = "Joined";
    String ENTER_PLACE = "Enter Place";
    String ENTER_PLACE_CITY = "Enter City";
    String ENTER_PLACE_PIN = "Enter Pin";
    String ENTER_DESTINATION = "Enter Destination";
    String ENTER_DESTINATION_NEIGHBORHOOD = "Enter Destination Neighborhood";
    String ENTER_DESTINATION_PIN = "Enter Destination Pin";
    String ENTER_DATE = "Enter Date";
    String ENTER_TIME = "Enter Time";
    String ENTER_SEATS = "Enter Seats";
    String SEATS_ZERO = "Seats can't be Zero";
    String LOADING = "Loading...";
    String RIDE_SCHEDULED ="Ride is Scheduled";
    String ACCEPT_REQUEST = "Add the Passenger";
    String ADD = "Add";
    String SEATS_AVAILABLE = "Seats Available: ";
    String REMOVE_PASSENGER = "Remove Passenger";
    String REMOVAL_CONFIRMATION = "Are you sure?";
    String REMOVE = "Remove";
    String SEATS_AVAILABLE_CHILD_ELEMENT = "mSeatsAvailable";
    String JOINEE_CHILD_ELEMENT = "mTripPassengers";
    String ENTER_NAME = "Enter Name";
    String RIDE_STATUS_COMPLETED = "Completed";
    String REQUEST_STATUS_APPROVED = "Approved";
    String REQUEST_STATUS_WAITING = "Awaiting Approval";
    String DATE_VALIDATION_FAILURE_TOAST = "Date Validation Failed";

    String FILTER_START_TIME_ASC = "Start_Time_asc";
    String FILTER_START_TIME_DESC = "Start_Time_desc";
    String FILTER_NO_OF_SEATS_ASC = "No_of_Seats_asc";
    String FILTER_NO_OF_SEATS_DESC = "No_of_Seats_desc";

    String SEAT_VALIDATION_ERROR = "Enter valid number of seats (>1 or <5)" ;
}
