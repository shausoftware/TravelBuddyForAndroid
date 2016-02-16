package com.shaustuff.shaumapmobile.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.DisplayMetrics;

import com.shaustuff.shaumapmobile.R;
import com.shaustuff.shaumapmobile.ShauMapApplication;

public class HelpActivity extends ActionBarActivity {

    private int displayWidth;
    private int displayHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayWidth = metrics.widthPixels;
        displayHeight = metrics.heightPixels;

        setContentView(R.layout.activity_help);

        String introText = "The ‘Travel Buddy London’ application is an easy to use tool that allows you to find embarkation points " +
                "and view live timetables for the following modes of public transport in London:\n\n" +
                "- Bus Stops (Except ‘Hail & Ride’)\n" +
                "- Tube Stations\n" +
                "- National Rail Stations\n" +
                "- River Boats\n";

        String mapText = "On the main 'Map' page navigate the google map around London to see the embarkation points in the vicinity. " +
                "Selecting an embarkation point on the map displays the name of the stop or station. " +
                "Selecting the text displayed in the information window opens a link to the appropriate timetable in your devices web browser. " +
                "The tube station icons are colour coded to indicate the the line to which they belong.\n" +
                "To prevent excessive data loading it should be noted that zooming out too far will prevent load of embarkation points.\n" +
                "Allow ‘Location Services’ in the Settings page for this application to locate embarkation points in your local vicinity.\n";

        String searchText = "Searches can be performed for bus stops, stations, piers, bus routes, tube lines, places and postcodes." +
                " A list of 5 of your favourite locations are displayed at the bottom of the page. If defined the location displays 3 icons:\n" +
                " - Selecting the leftmost icon opens the timetable in the web browser (for postcodes and places the user is redirected to the map page).\n" +
                " - Selecting the middle icon opens the map page at the selected coordinates.\n" +
                " - Selecting the rightmost icon removes the location from the list.\n" +
                "Once a search is performed another three icons are displayed for each matching search result.\n" +
                " - Selecting the leftmost icon opens the timetable in the web browser (for postcodes and places the user is redirected to the map page).\n" +
                " - Selecting the middle icon opens the map page at the selected coordinates.\n" +
                " - Selecting the rightmost icon adds the location to the 'My Locations' list. The icon will not be displayed if there are no free 'My Location' slots available.\n";

        String alertsText = "On the 'Alerts' page it is possible to set up alerts for tube lines. The alerts generate notifications when there is service disruption. " +
                "Alerts are switched on for the individual tube lines by selecting the rightmost checkmark.\n" +
                "Notification sound, vibration and polling frequency can be configured from the 'Settings Page'.\n";

        String dataText = "This application uses data leveraged from Transport for London via London Datastore and National Rail Enquiries.\n\n" +
                "Last Data Update: ";

        ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
        String lastKmlUpdate = shauMapApplication.getLastKmlUpdate();
        if (lastKmlUpdate != null) {
            dataText += lastKmlUpdate;
        }
        dataText += "\n\n";

        TextView introTextView = (TextView) findViewById(R.id.introductionText);
        introTextView.setText(introText);

        TextView mapTextView = (TextView) findViewById(R.id.mapText);
        mapTextView.setText(mapText);

        TextView searchTextView = (TextView) findViewById(R.id.searchText);
        searchTextView.setText(searchText);

        TextView alertsTextView = (TextView) findViewById(R.id.alertsText);
        alertsTextView.setText(alertsText);

        TextView dataTextView = (TextView) findViewById(R.id.dataText);
        dataTextView.setText(dataText);

        mapTextView.getRootView().setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
