package com.shaustuff.shaumapmobile.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.net.Uri;

import android.os.AsyncTask;
import android.os.StrictMode;

import android.preference.PreferenceManager;
import android.os.Bundle;

import android.location.Location;
import android.location.LocationManager;
import android.content.Intent;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.shaustuff.shaumapmobile.model.KmlGeometry;
import com.shaustuff.shaumapmobile.xmlparser.KmlParser;
import com.shaustuff.shaumapmobile.R;
import com.shaustuff.shaumapmobile.ShauMapApplication;

/**
 * Android Google map application that shows stopping points for buses, tube & trains in london
 * The markers can be used to open links to the relevant timetable for the station
 */

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;

    private Map<String, Marker> markers = new HashMap<String, Marker>();

    private boolean infoWindowOpening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                return true;
            case R.id.action_search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.action_map_key:
                Intent mapKeyIntent = new Intent(this, MapKeyActivity.class);
                startActivity(mapKeyIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * See if we can get a reference to map fragment
     */
    private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Initialise google map
     */
    private void setUpMap() {

        ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
        LatLng currentPosition = new LatLng(shauMapApplication.getCurrentLatitude(), shauMapApplication.getCurrentLongitude());

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        //where am I? first time only
        if (!shauMapApplication.isLocated()) {
            Location myLocation = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation != null) {
                currentPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                shauMapApplication.setLocated(true);
            }
        }

        //listen to map move events
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

               if (infoWindowOpening) {
                   infoWindowOpening = false;
               } else {
                   if (mMap.getCameraPosition().zoom > 14) {
                       //load kml
                       //loadKmlData();
                       LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                       new KmlLoader().execute(bounds);
                       //update current position
                       ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
                       shauMapApplication.setCurrentLatitude(cameraPosition.target.latitude);
                       shauMapApplication.setCurrentLongitude(cameraPosition.target.longitude);
                       shauMapApplication.setCurrentZoom(cameraPosition.zoom);
                   }
               }
            }
        });

        //listen to marker click event
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                infoWindowOpening = true;
                return false;
            }
        });

        //listen to info window click events
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Iterator it = markers.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    Marker value = markers.get(key);
                    if (value.getId().equals(marker.getId())) {
                        //use key as url to open timetable in browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(key));
                        startActivity(browserIntent);
                    }
                }
            }
        });

        //Move the camera instantly to where I am with a zoom of 16.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, shauMapApplication.getCurrentZoom()));
    }



    private class KmlLoader extends AsyncTask<LatLngBounds, Void, List<KmlGeometry>> {

        @Override
        protected List<KmlGeometry> doInBackground(LatLngBounds... params) {

            LatLngBounds bounds = params[0];

            List<KmlGeometry> results = new ArrayList<KmlGeometry>();

            String kmlUrl = "http://www.shaustuff.com/kml/River%20Boat/Hail%20and%20Ride/Bus%20Stop/Tube%20Station/Train%20Station?BBOX=" + bounds.southwest.longitude + "," + bounds.southwest.latitude + "," + bounds.northeast.longitude + "," + bounds.northeast.latitude;
            //String kmlUrl = "http://192.168.1.3:8080/kml/River%20Boat/Hail%20and%20Ride/Bus%20Stop/Tube%20Station/Train%20Station?BBOX=" + bounds.southwest.longitude + "," + bounds.southwest.latitude + "," + bounds.northeast.longitude + "," + bounds.northeast.latitude;

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

                results = kmlParser.getParsedGeometry();
                String lastKmlUpdate = kmlParser.getLastUpdate();

                ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
                shauMapApplication.setLastKmlUpdate(lastKmlUpdate);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<KmlGeometry> results) {

            //clear markers
            mMap.clear();
            //clear marker references
            markers = new HashMap<String, Marker>();

            for (KmlGeometry geometry : results) {

                if (!markers.containsKey(geometry.getLink())) {
                    if (geometry.isValid()) {
                        drawGeometry(geometry);
                    }
                }
            }
        }
    }

    /**
     * Some stations have multiple lines and stops
     * This routine offsets markers so they are not all sitting on top of each other
     * @param position
     * @return valid position for marker
     */
    private LatLng getNextValidMarkerPosition(LatLng position) {

        Iterator it = markers.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Marker marker = markers.get(key);

            if (marker.getPosition().latitude == position.latitude && marker.getPosition().longitude == position.longitude) {
                //position clash
                //move marker up a bit
                position = new LatLng(position.latitude + 0.0003f, position.longitude);
                //try again
                getNextValidMarkerPosition(position);
            }
        }

        return position;
    }

    /**
     * Draw KML data on map
     * @param geometry
     */
    private void drawGeometry(KmlGeometry geometry) {

        if (KmlGeometry.CATEGORY_UNDEFINED != geometry.getCategory()) {

            //Marker
            LatLng position = getNextValidMarkerPosition(geometry.getCoordinates().get(0));
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(geometry.getImageResourceId()))
                    .title(geometry.getName()));
            if (geometry.getCategory() == KmlGeometry.CATEGORY_BUS_STOP ||
                    geometry.getCategory() == KmlGeometry.CATEGORY_HAIL_AND_RIDE ||
                    geometry.getCategory() == KmlGeometry.CATEGORY_RIVER_BOAT) {
                marker.setSnippet(geometry.getRoute());
            }
            //so we can get url to open on click event
            markers.put(geometry.getLink(), marker);
        }
    }
}