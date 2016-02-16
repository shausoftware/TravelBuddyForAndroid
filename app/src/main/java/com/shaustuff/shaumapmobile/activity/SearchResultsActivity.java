package com.shaustuff.shaumapmobile.activity;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.shaustuff.shaumapmobile.model.KmlGeometry;
import com.shaustuff.shaumapmobile.xmlparser.KmlParser;
import com.shaustuff.shaumapmobile.R;
import com.shaustuff.shaumapmobile.ShauMapApplication;
import com.shaustuff.shaumapmobile.callback.SearchResultCallback;
import com.shaustuff.shaumapmobile.listadapter.SearchResultListAdapter;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SearchResultsActivity extends ActionBarActivity implements SearchResultCallback {

    private int displayWidth;
    private boolean slotAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayWidth = metrics.widthPixels;

        List<KmlGeometry> searchResults = new ArrayList<KmlGeometry>();

        //get parameters sent from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("searchParameter") != null) {
            searchResults = search(bundle.getString("searchParameter"));
        }
        if (bundle.getBoolean("slotAvailable")) {
            slotAvailable = true;
        }

        String[] searchResultsText = new String[searchResults.size()];
        int i = 0;
        for (KmlGeometry searchResult : searchResults) {
            searchResultsText[i] = searchResult.getName();
            i++;
        }

        SearchResultListAdapter adapter = new SearchResultListAdapter(this, searchResultsText, searchResults, slotAvailable, this);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.getRootView().setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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

    private List<KmlGeometry> search(String searchParameter) {

        List<KmlGeometry> searchResults = new ArrayList<KmlGeometry>();

        searchResults.addAll(searchTranportData(searchParameter));

        return searchResults;
    }

    private List<KmlGeometry> searchTranportData(String searchParameter) {

        List<KmlGeometry> kmlGeometry = new ArrayList<KmlGeometry>();

        String formattedParameter = searchParameter.replaceAll(" ", "%20");
        formattedParameter = formattedParameter.replaceAll("&", "and");
        formattedParameter = formattedParameter.replaceAll("'", "");

        String kmlUrl = "http://www.shaustuff.com/kml/search?SEARCH_PARAMETER=" + formattedParameter;
        //String kmlUrl = "http://192.168.1.3:8080/kml/search?SEARCH_PARAMETER=" + searchParameter;

        try {
            final URL url = new URL(kmlUrl);
            final URLConnection conn = url.openConnection();
            conn.setReadTimeout(15000);  // timeout for reading the kml data: 15 secs
            conn.connect();

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            XMLReader xr = sp.getXMLReader();
            KmlParser kmlParser = new KmlParser();
            xr.setContentHandler(kmlParser);
            xr.parse(new InputSource(url.openStream()));

            kmlGeometry = kmlParser.getParsedGeometry();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return kmlGeometry;
    }


    @Override
    public void openTimetable(KmlGeometry searchResult) {

        if (searchResult.getCategory() == KmlGeometry.CATEGORY_LOCATION) {
            //no link for postcode so show in map
            showResultInMap(searchResult);
        } else {
            //open timetable in browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchResult.getLink()));
            startActivity(browserIntent);
        }
    }

    @Override
    public void showResultInMap(KmlGeometry searchResult) {

        if (searchResult.getCoordinates() != null && searchResult.getCoordinates().size() > 0) {

            LatLng latLng = searchResult.getCoordinates().get(0);
            ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
            shauMapApplication.setCurrentLatitude(latLng.latitude);
            shauMapApplication.setCurrentLongitude(latLng.longitude);
            shauMapApplication.setCurrentZoom(16);

            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
        }
    }

    @Override
    public void saveLocation(KmlGeometry location) {

        ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
        shauMapApplication.addLocation(location);

        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivity(searchIntent);
    }
}
