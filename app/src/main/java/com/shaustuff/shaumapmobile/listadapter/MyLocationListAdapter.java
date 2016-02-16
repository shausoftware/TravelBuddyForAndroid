package com.shaustuff.shaumapmobile.listadapter;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaustuff.shaumapmobile.model.KmlGeometry;
import com.shaustuff.shaumapmobile.R;
import com.shaustuff.shaumapmobile.callback.MyLocationCallback;

public class MyLocationListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<KmlGeometry> locations = new ArrayList<KmlGeometry>();
    private MyLocationCallback callback;

    public MyLocationListAdapter(Activity context, String[] locationNames, List<KmlGeometry> locations, MyLocationCallback callback) {

        super(context, R.layout.my_location_list, locationNames);

        this.context = context;
        this.locations = locations;
        this.callback = callback;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        final KmlGeometry location = locations.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.my_location_list, null,true);

        //transport mode icon
        ImageView typeImageView = (ImageView) rowView.findViewById(R.id.typeIcon);
        typeImageView.setImageResource(location.getImageResourceId());
        if (location.getCategory() != KmlGeometry.CATEGORY_UNDEFINED) {
            typeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.openTimetable(location);
                }
            });
        }
        //view on map
        ImageView mapImageView = (ImageView) rowView.findViewById(R.id.mapIcon);
        if (location.getCategory() != KmlGeometry.CATEGORY_UNDEFINED) {
            mapImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.openInMap(location);
                }
            });
        } else {
            mapImageView.setImageResource(R.drawable.disabled);
        }
        //delete
        ImageView deleteImageView = (ImageView) rowView.findViewById(R.id.deleteIcon);
        if (location.getCategory() != KmlGeometry.CATEGORY_UNDEFINED) {
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.deleteLocation(location);
                }
            });
        } else {
            deleteImageView.setImageResource(R.drawable.disabled);
        }

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        txtTitle.setTextColor(Color.WHITE);
        txtTitle.setTextSize(15);
        txtTitle.setText(location.getName());

       return rowView;
    }
}