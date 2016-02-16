package com.shaustuff.shaumapmobile.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ListView;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.DialogInterface;

import com.google.android.gms.maps.model.LatLng;
import com.shaustuff.shaumapmobile.model.KmlGeometry;
import com.shaustuff.shaumapmobile.R;
import com.shaustuff.shaumapmobile.ShauMapApplication;
import com.shaustuff.shaumapmobile.callback.MyLocationCallback;
import com.shaustuff.shaumapmobile.listadapter.MyLocationListAdapter;

import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import java.util.List;
import java.util.Collections;

public class SearchActivity extends ActionBarActivity implements MyLocationCallback {

    private EditText searchInput;
    private int displayWidth;
    private boolean slotAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();

        setContentView(R.layout.activity_search);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayWidth = metrics.widthPixels;

        //SEARCH
        String currentSearchParameter = shauMapApplication.getCurrentSearchParameter();
        searchInput = (EditText) findViewById(R.id.searchInput);
        searchInput.setTextColor(Color.WHITE);
        if (currentSearchParameter != null && currentSearchParameter.length() > 0) {
            searchInput.setText(currentSearchParameter);
        }

        searchInput.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String searchParameter = searchInput.getText().toString();

                    if (searchParameter != null && searchParameter.length() > 0) {
                        ShauMapApplication shauMapApplicationX = (ShauMapApplication) getApplicationContext();
                        shauMapApplicationX.setCurrentSearchParameter(searchParameter);
                        Intent searchResultsIntent = new Intent(view.getContext(), SearchResultsActivity.class);
                        searchResultsIntent.putExtra("searchParameter", searchParameter);
                        searchResultsIntent.putExtra("slotAvailable", slotAvailable);
                        startActivity(searchResultsIntent);
                    } else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                    }

                    return true;
                }
                return false;
            }
        });

        final String searchHelpText = "Searches can be performed for the following:\n\n - Train stations\n - Tube Stations\n - Bus Stops\n - Piers\n - Tube Lines\n - Bus Routes\n - Places\n - Postcodes\n\n" +
                "View results on the map or add them to the list of your favourite locations for further recall.\n";

        TextView searchHelp = (TextView) findViewById(R.id.searchHelp);
        searchHelp.setTextColor(Color.WHITE);
        searchHelp.setText(searchHelpText);

        //MY LOCATION LIST
        List<KmlGeometry> myLocations = shauMapApplication.getMyLocations();
        Collections.sort(myLocations);
        slotAvailable = shauMapApplication.isLocationSlotAvailable();
        int i = 0;
        String[] locationNames = new String[myLocations.size()];
        for (KmlGeometry myLocation : myLocations) {
            locationNames[i] = myLocation.getName();
        }

        MyLocationListAdapter adapter = new MyLocationListAdapter(this, locationNames, myLocations, this);
        ListView myLocationListView = (ListView) findViewById(R.id.myLocationList);
        //myLocationListView.setAdapter(adapter);
        ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.my_locations_header, myLocationListView, false);
        myLocationListView.addHeaderView(header, null, false);
        myLocationListView.setAdapter(adapter);

        searchHelp.getRootView().setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openTimetable(KmlGeometry location) {

        if (location.getCategory() == KmlGeometry.CATEGORY_LOCATION
                ) {
            //no link for postcode so show in map
            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
        } else {
            //open timetable in browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(location.getLink()));
            startActivity(browserIntent);
        }
    }
    @Override
    public void openInMap(KmlGeometry location) {

        if (location.getCoordinates() != null && location.getCoordinates().size() > 0) {

            LatLng latLng = location.getCoordinates().get(0);
            ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
            shauMapApplication.setCurrentLatitude(latLng.latitude);
            shauMapApplication.setCurrentLongitude(latLng.longitude);
            shauMapApplication.setCurrentZoom(16);

            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
        }
    }
    @Override
    public void deleteLocation(final KmlGeometry location) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Delete...");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want delete location?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
                shauMapApplication.deleteLocation(location);
                finish();
                startActivity(getIntent());
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
