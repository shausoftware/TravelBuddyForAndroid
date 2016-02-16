package com.shaustuff.shaumapmobile.listadapter;

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
import com.shaustuff.shaumapmobile.callback.SearchResultCallback;

import java.util.List;

public class SearchResultListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<KmlGeometry> searchResults;
    private SearchResultCallback callback;
    private boolean slotavailable = true;

    public SearchResultListAdapter(Activity context, String[] searchResultsText, List<KmlGeometry> searchResults, boolean slotAvailable, SearchResultCallback callback) {

        super(context, R.layout.search_result_list, searchResultsText);

        this.context = context;
        this.searchResults = searchResults;
        this.slotavailable = slotAvailable;
        this.callback = callback;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        final KmlGeometry searchResult = searchResults.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.search_result_list, null, true);

        //open timetable when clicked
        ImageView typeImageView = (ImageView) rowView.findViewById(R.id.icon);
        //set image to map icon
        typeImageView.setImageResource(searchResult.getImageResourceId());
        typeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (searchResult.getCategory() != KmlGeometry.CATEGORY_UNDEFINED) {
                callback.openTimetable(searchResult);
            }
            }
        });
        //display in map
        ImageView mapImageView = (ImageView) rowView.findViewById(R.id.map_icon);
        mapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchResult.getCategory() != KmlGeometry.CATEGORY_UNDEFINED) {
                    callback.showResultInMap(searchResult);
                }
            }
        });
        //save to slot
        ImageView saveImageView = (ImageView) rowView.findViewById(R.id.save_icon);
        if (slotavailable) {
            //allow save
            saveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (searchResult.getCategory() != KmlGeometry.CATEGORY_UNDEFINED) {
                        callback.saveLocation(searchResult);
                    }
                }
            });
        } else {
            //no free slots
            saveImageView.setImageResource(R.drawable.disabled);
        }

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        txtTitle.setTextColor(Color.WHITE);
        txtTitle.setTextSize(15);
        txtTitle.setText(searchResult.getName());

        return rowView;
    }
}