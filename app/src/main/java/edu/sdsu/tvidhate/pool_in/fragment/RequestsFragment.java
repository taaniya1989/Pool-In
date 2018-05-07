package edu.sdsu.tvidhate.pool_in.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Request;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.RequestsAdapter;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;
import edu.sdsu.tvidhate.pool_in.helper.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment implements SharedConstants{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView requestsListView;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private String uid = "";
    private String currentUserContactNumber;
    RequestsAdapter listadapter;
    List<Request> requestList = new ArrayList<>();
    private int seatsAvailable;
    private OnFragmentInteractionListener mListener;
    private Trip currentTrip;

    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        auth = FirebaseAuth.getInstance();

        Utilities utilities = new Utilities(getFragmentManager());
        utilities.checkProfile();

        if(auth.getCurrentUser()!=null){
            currentUserContactNumber = auth.getCurrentUser().getDisplayName();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("rew","in oncreate view");
        requestsListView = view.findViewById(R.id.requests_list_view);

        checkNumberOfSeats();
        return view;
    }

    private void getRequestData(final String currentUserFBContactNumber)
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                requestList.clear();

                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " trips available");

                if (dataSnapshot.getChildrenCount() > 0)
                {
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren())
                    {
                        Request requestDetailsPOJO = msgSnapshot.getValue(Request.class);
                        Log.i("NIK","Request :-" + requestDetailsPOJO.toString());
                        requestList.add(requestDetailsPOJO);
                    }

                    Collections.reverse(requestList);

                    if(getActivity() != null){
                        listadapter = new RequestsAdapter(getActivity(), 0, requestList);
                        requestsListView.setAdapter(listadapter);
                    }

                } else
                {
                    if (listadapter != null) {
                        listadapter.notifyDataSetChanged();
                    }
                }

                requestsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        final Request requestDetailsPOJO = (Request) adapterView.getItemAtPosition(i);
                        final String requestorContact = ((Request) adapterView.getItemAtPosition(i)).getRequestorContact();

                        final User joiningUser = ((Request) adapterView.getItemAtPosition(i)).getmJoinTripRequester();

                        if(!requestDetailsPOJO.isApprovalStatus() && seatsAvailable > 0)
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle(PASSENGER_REQUEST);
                            alert.setMessage(ACCEPT_REQUEST);
                            alert.setPositiveButton(ADD, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestDetailsPOJO.setApprovalStatus(true);
                                    mDatabase.child(FIREBASE_REQUESTS).child(currentUserContactNumber)
                                            .child(requestorContact).setValue(requestDetailsPOJO);
                                    seatsAvailable = seatsAvailable-1;
                                    Map map = new HashMap();
                                    map.put(requestorContact,joiningUser);

                                    mDatabase.child(FIREBASE_MY_RIDES).child(uid).getRef().child(JOINEE_CHILD_ELEMENT).updateChildren(map);
                                    mDatabase.child(FIREBASE_TRIP_DETAILS).child(currentUserContactNumber).getRef().child(JOINEE_CHILD_ELEMENT)
                                            .updateChildren(map);
                                    mDatabase.child(FIREBASE_TRIP_DETAILS)
                                            .child(currentUserContactNumber).child(SEATS_AVAILABLE_CHILD_ELEMENT).setValue(seatsAvailable);
                                    Map map2 = new HashMap();
                                    String key = mDatabase.child(FIREBASE_CURRENT_RIDES).child(requestorContact).push().getKey();
                                    map2.put(key,uid);
                                    mDatabase.child(FIREBASE_CURRENT_RIDES).child(requestorContact).updateChildren(map2);
                                    listadapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });


                            alert.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                            return true;
                        }
                        else if(requestDetailsPOJO.isApprovalStatus())
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle(REMOVE_PASSENGER);
                            alert.setMessage(REMOVAL_CONFIRMATION);
                            alert.setPositiveButton(REMOVE, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestDetailsPOJO.setApprovalStatus(false);
                                    mDatabase.child(FIREBASE_REQUESTS).child(currentUserContactNumber).child(requestorContact)
                                            .setValue(requestDetailsPOJO);

                                    seatsAvailable = seatsAvailable + 1;

                                    mDatabase.child(FIREBASE_TRIP_DETAILS)
                                            .child(currentUserFBContactNumber).child(SEATS_AVAILABLE_CHILD_ELEMENT).setValue(seatsAvailable);
                                    mDatabase.child(FIREBASE_MY_RIDES).child(uid).child(JOINEE_CHILD_ELEMENT).child(requestorContact).removeValue();
                                    listadapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });

                            alert.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                            return true;
                        }else{
                            return true;
                        }

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_REQUESTS).child(currentUserContactNumber);
        people.addValueEventListener(valueEventListener);
    }

    private void checkNumberOfSeats()
    {
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");

                if(dataSnapshot.getChildrenCount()!=0)
                {
                    final Trip addTripDetailsPOJO = dataSnapshot.getValue(Trip.class);
                    seatsAvailable = addTripDetailsPOJO.getmSeatsAvailable();
                    uid = addTripDetailsPOJO.getmTripId();
                    getRequestData(currentUserContactNumber);
                    if(getContext()!=null){
                        Toast.makeText(getContext(),SEATS_AVAILABLE+seatsAvailable, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference people1 = database1.getReference(FIREBASE_TRIP_DETAILS).child(currentUserContactNumber);
        people1.addValueEventListener(valueEventListener1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
