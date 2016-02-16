package com.shaustuff.shaumapmobile;

import android.app.PendingIntent;
import android.app.Application;
import android.app.AlarmManager;
import android.app.NotificationManager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.Context;

import android.media.RingtoneManager;

import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import android.net.Uri;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.shaustuff.shaumapmobile.model.KmlGeometry;
import com.shaustuff.shaumapmobile.model.RouteAlert;
import com.shaustuff.shaumapmobile.model.TubeLineStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ShauMapApplication extends Application {

    public static long pollDelay = 60000 * 3;

    public static final String ALERTS_CONFIG = "shauTransportMapLondonAlertConfig";
    private List<RouteAlert> routeAlerts = new ArrayList<RouteAlert>();

    public static final int IDX_BUS = 0;
    public static final int IDX_HAIL_AND_RIDE = 1;
    public static final int IDX_TRAIN = 2;
    public static final int IDX_RIVER_BOAT = 3;
    public static final int IDX_BAKERLOO = 4;
    public static final int IDX_CENTRAL = 5;
    public static final int IDX_CIRCLE = 6;
    public static final int IDX_DISTRICT = 7;
    public static final int IDX_DLR = 8;
    public static final int IDX_HAMMERSMITH_CITY = 9;
    public static final int IDX_JUBILEE = 10;
    public static final int IDX_METROPOLITAN = 11;
    public static final int IDX_NORTHERN = 12;
    public static final int IDX_PICCADILLY = 13;
    public static final int IDX_VICTORIA = 14;
    public static final int IDX_WATERLOO_CITY = 15;

    public static final String ALERT_SERVICE_RUNNING = "shauLineAlertServiceRunning";

    private double currentLatitude = 51.5072;
    private double currentLongitude = 0.1275;
    private float currentZoom = 16;
    private boolean located = false;

    private List<KmlGeometry> myLocations = new ArrayList<KmlGeometry>();

    private String currentSesarchParameter = null;
    private String lastKmlUpdate = null;

    private Map<String, BitmapDescriptor> appIcons = new HashMap<String, BitmapDescriptor>();

    public void setCurrentSearchParameter(String currentSesarchParameter) {
        this.currentSesarchParameter = currentSesarchParameter;
    }
    public String getCurrentSearchParameter() {
        return currentSesarchParameter;
    }

    public void setLastKmlUpdate(String lastKmlUpdate) {
        this.lastKmlUpdate = lastKmlUpdate;
    }
    public String getLastKmlUpdate() {
        return lastKmlUpdate;
    }

    public void setLastAlertUpdate(String lastAlertUpdate) {
        SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LAST_ALERT_UPDATE", lastAlertUpdate);
        editor.commit();
    }
    public String getLastAlertUpdate() {
        SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);
        return settings.getString("LAST_ALERT_UPDATE", null);
    }

    public ShauMapApplication() {

        routeAlerts.add(new RouteAlert("Bus Stops (Except Hail & Ride)", false, false, R.drawable.ic_bus_stop));
        routeAlerts.add(new RouteAlert("Hail and Ride", false, false, R.drawable.ic_hailandride));
        routeAlerts.add(new RouteAlert("National Rail Stations", false, false, R.drawable.ic_britishrail));
        routeAlerts.add(new RouteAlert("Bakerloo Line", true, false, R.drawable.ic_bakerloo));
        routeAlerts.add(new RouteAlert("Central Line", true, false, R.drawable.ic_central));
        routeAlerts.add(new RouteAlert("Circle Line", true, false, R.drawable.ic_circle));
        routeAlerts.add(new RouteAlert("District Line", true, false, R.drawable.ic_district));
        routeAlerts.add(new RouteAlert("DLR", true, false, R.drawable.ic_dlr));
        routeAlerts.add(new RouteAlert("Hammersmith & City Line", true, false, R.drawable.ic_hammersmith_city));
        routeAlerts.add(new RouteAlert("Jubilee Line", true, false, R.drawable.ic_jubilee));
        routeAlerts.add(new RouteAlert("Metropolitan Line", true, false, R.drawable.ic_metropolitan));
        routeAlerts.add(new RouteAlert("Northern Line", true, false, R.drawable.ic_northern));
        routeAlerts.add(new RouteAlert("Piccadilly Line", true, false, R.drawable.ic_piccadilly));
        routeAlerts.add(new RouteAlert("Victoria Line", true, false, R.drawable.ic_victoria));
        routeAlerts.add(new RouteAlert("Waterloo & City Line", true, false, R.drawable.ic_dlr));
        routeAlerts.add(new RouteAlert("River", false, false, R.drawable.ic_riverboat));
    }

    public void changeAlertPollTime(String pollTimeReadable) {

        stopAlertService();

        if (getString(R.string.alertoff).equals(pollTimeReadable)) {
            pollDelay = 0;
            //don't restart
        } else if (getString(R.string.everymin).equals(pollTimeReadable)) {
            pollDelay = 60000;
            startAlertService();
        } else if (getString(R.string.every5mins).equals(pollTimeReadable)) {
            pollDelay = 60000 * 5;
            startAlertService();
        } else if (getString(R.string.every10mins).equals(pollTimeReadable)) {
            pollDelay = 60000 * 10;
            startAlertService();
        } else if (getString(R.string.every30mins).equals(pollTimeReadable)) {
            pollDelay = 60000 * 30;
            startAlertService();
        } else if (getString(R.string.everyhour).equals(pollTimeReadable)) {
            pollDelay = 60000 * 60;
            startAlertService();
        }
    }

    private void startAlertService() {

        Intent intent = new Intent(this, TubeLinePullService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pollDelay, pintent);

        startService(new Intent(getBaseContext(), TubeLinePullService.class));
        saveBooleanPreference(ALERT_SERVICE_RUNNING, true);
    }

    private void stopAlertService() {

        Intent intent = new Intent(getBaseContext(), TubeLinePullService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pintent);

        stopService(intent);
        saveBooleanPreference(ALERT_SERVICE_RUNNING, false);
    }

    private void clear() {
        SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LOCATION_0", null);
        editor.putString("LOCATION_1", null);
        editor.putString("LOCATION_2", null);
        editor.putString("LOCATION_3", null);
        editor.putString("LOCATION_4", null);
        editor.commit();
    }

    public void loadMyLocations() {

        //clear();

        SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);

        int slots = 5;
        myLocations = new ArrayList<KmlGeometry>();

        for (int i = 0; i < slots; i++) {

            String slot = "LOCATION_" + i;

            KmlGeometry myLocation = new KmlGeometry();
            myLocation.setSettingsSlot(slot);

            String location = settings.getString(slot, null);
            if (location != null) {
                myLocation.setStateFromString(location);
            }

            myLocations.add(myLocation);
        }
    }

    public void addLocation(KmlGeometry newLocation) {

        int i = 0;
        for (KmlGeometry location : myLocations) {

            if (location.getCategory() == KmlGeometry.CATEGORY_UNDEFINED) {

                SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(location.getSettingsSlot(), newLocation.getStateAsString());
                editor.commit();
                break;
            }
            i++;
        }
    }

    public void deleteLocation(KmlGeometry locationToRemove) {

        int i = 0;
        for (KmlGeometry location : myLocations) {

            if (location.getStateAsString().equals(locationToRemove.getStateAsString())) {

                SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(location.getSettingsSlot(), null);
                editor.commit();
                break;
            }
            i++;
        }
    }

    public boolean isLocationSlotAvailable() {

        boolean slotAvailable = false;

        for (KmlGeometry myLocation : myLocations) {
            if (myLocation.getCategory() == KmlGeometry.CATEGORY_UNDEFINED) {
                slotAvailable = true;
                break;
            }
        }

        return slotAvailable;
    }

    public List<KmlGeometry> getMyLocations() {
        loadMyLocations();
        return myLocations;
    }

    public void loadAlertConfig() {

        SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);

        for (RouteAlert routeAlert : routeAlerts) {
            if (routeAlert.isAlertEnabled()) {
                routeAlert.setAlertOn(settings.getBoolean(routeAlert.getRouteName(), false));
            }
        }
    }

    public void updateAlertConfig(String lineName, boolean activated) {

        //save changed alert preference
        saveBooleanPreference(lineName, activated);

        //refresh config
        loadAlertConfig();
    }

    public void updateAlertData(Map<String, TubeLineStatus> lineStatusMap) {

        if (routeAlerts.get(IDX_BAKERLOO).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Bakerloo");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_bakerloo, "Bakerloo Line", 1, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_CENTRAL).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Central");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_central, "Central Line", 2, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_CIRCLE).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Circle");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_circle, "Circle Line", 3, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_DISTRICT).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("District");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_district, "District Line", 4, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_DLR).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("DLR");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_dlr, "DLR", 5, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_HAMMERSMITH_CITY).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Hammersmith and City");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_hammersmith_city, "Hammersmith & City Line", 6, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_JUBILEE).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Jubilee");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_jubilee, "Jubilee Line", 7, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_METROPOLITAN).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Metropolitan");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_metropolitan, "Metropolitan Line", 8, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_NORTHERN).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Northern");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_northern, "Northern Line", 9, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_PICCADILLY).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Piccadilly");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_piccadilly, "Piccadilly Line", 10, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_VICTORIA).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Victoria");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_victoria, "Victoria Line", 11, tls.getLineStatusDetails());
            }
        }
        if (routeAlerts.get(IDX_WATERLOO_CITY).isAlertOn()) {
            TubeLineStatus tls = lineStatusMap.get("Waterloo and City");
            if (!tls.isLineOk()) {
                sendNotification(R.drawable.ic_dlr, "Waterloo & City Line", 12, tls.getLineStatusDetails());
            }
        }
    }

    private void sendNotification(int imageId, String lineName, int notificationId, String statusDetails) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                                .setSmallIcon(imageId)
                                                .setContentTitle(lineName)
                                                .setContentText(statusDetails);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("pref_alert_sound", false)) {
            //define sound URI, the sound to be played when there's a notification
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(soundUri);
        }
        if (sp.getBoolean("pref_alert_vibrate", false)) {
            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }

        NotificationManager notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Build the notification and issue it.
        notificationMgr.notify(notificationId, builder.build());
    }

    public List<RouteAlert> getLoadedAlertConfig() {
        return routeAlerts;
    }

    public void setCurrentLatitude(double latitude) {
        this.currentLatitude = latitude;
    }
    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLongitude(double longitude) {
        this.currentLongitude = longitude;
    }
    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentZoom(float zoom) {
        this.currentZoom = zoom;
    }
    public float getCurrentZoom() {
        return currentZoom;
    }

    public void setLocated(boolean located) {
        this.located = located;
    }
    public boolean isLocated() {
        return located;
    }

    private void saveBooleanPreference(String name, boolean value) {
        SharedPreferences settings = getSharedPreferences(ALERTS_CONFIG, 0);
        Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }
}