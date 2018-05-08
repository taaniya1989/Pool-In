package edu.sdsu.tvidhate.pool_in.helper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;

public class TripDetailsAdapter extends ArrayAdapter implements SharedConstants
{
    private Activity context;
    private List<Trip> userProperties;
    public TripDetailsAdapter(Activity context, int resource, List list) {
        super(context, resource, list);
        this.context = context;
        this.userProperties = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.trip_list_item, null);
        TextView destination = view.findViewById(R.id.trip_list_destination);
        TextView source = view.findViewById(R.id.trip_list_source);
        TextView date = view.findViewById(R.id.trip_list_date);
        TextView time = view.findViewById(R.id.trip_list_time);
        TextView seats = view.findViewById(R.id.trip_list_seats);
        TextView name = view.findViewById(R.id.trip_list_name);
        TextView contact = view.findViewById(R.id.trip_list_contact);
       // TextView sourceNeighborhood = view.findViewById(R.id.trip_list_fromneighborhood);
       // TextView destinationNeighborhood = view.findViewById(R.id.trip_list_toneighborhood);

        source.setText(userProperties.get(position).getmSourceAddress());
        destination.setText(userProperties.get(position).getmDestinationAddress());
        date.setText(userProperties.get(position).getmStartDate());
        time.setText(userProperties.get(position).getmStartTime());
        seats.setText(String.valueOf(userProperties.get(position).getmSeatsAvailable()));
        name.setText(userProperties.get(position).getmTripDriver().toString());
        contact.setText(userProperties.get(position).getmTripDriver().getmContactNumber());
       // sourceNeighborhood.setText(userProperties.get(position).getmSourceNeighbordhood());
       // destinationNeighborhood.setText(userProperties.get(position).getmDestinationNeighbordhood());

        return view;
    }
}
