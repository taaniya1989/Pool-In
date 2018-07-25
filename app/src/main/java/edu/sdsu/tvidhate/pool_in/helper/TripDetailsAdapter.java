package edu.sdsu.tvidhate.pool_in.helper;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.activity.MainActivity;
import edu.sdsu.tvidhate.pool_in.entity.Trip;


public class TripDetailsAdapter extends ArrayAdapter implements SharedConstants
{
    private Activity context;
    private List<Trip> userProperties;
    private StorageReference firebaseStorageRef;
    private Uri imageUri;

    public TripDetailsAdapter(Activity context, int resource, List<Trip> list) {
        super(context, resource, list);
        this.context = context;
        this.userProperties = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.trip_list_item, null);

        TextView placeName,placeCity;
        final ImageView placeImage;
        placeName = view.findViewById(R.id.itemPlaceName);
        placeCity = view.findViewById(R.id.itemPlaceCity);
        placeImage = view.findViewById(R.id.itemPlaceImage);


        placeName.setText(userProperties.get(position).getmTripPlaceName());
        placeCity.setText(userProperties.get(position).getmTripCity());

        if(userProperties.get(position).getImageDownloadUrl()!=null){
            Picasso.with(getContext()).load(userProperties.get(position).getImageDownloadUrl()).resize(MainActivity.width,MainActivity.height/2).into(placeImage);

        }
        return view;
    }
}
