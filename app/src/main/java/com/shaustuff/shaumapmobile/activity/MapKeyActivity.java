package com.shaustuff.shaumapmobile.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.shaustuff.shaumapmobile.R;
import com.shaustuff.shaumapmobile.model.RouteAlert;
import com.shaustuff.shaumapmobile.ShauMapApplication;
import com.shaustuff.shaumapmobile.callback.AlertCallback;
import com.shaustuff.shaumapmobile.listadapter.MapKeyListAdapter;

public class MapKeyActivity extends ActionBarActivity implements AlertCallback {

    private int displayWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_key);

        ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayWidth = metrics.widthPixels;

        shauMapApplication.loadAlertConfig();
        List<RouteAlert> routeAlerts = shauMapApplication.getLoadedAlertConfig();
        //strings required for list adapter
        String[] routeAlertNames = new String[routeAlerts.size()];
        int i = 0;
        for (RouteAlert routeAlert : routeAlerts) {
            routeAlertNames[i] = routeAlert.getRouteName();
            i++;
        }

        MapKeyListAdapter adapter = new MapKeyListAdapter(this, routeAlertNames, routeAlerts, this);
        ListView listView = (ListView) findViewById(R.id.list);
        //listView.setAdapter(adapter);
        ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.alerts_header, listView, false);
        String lastUpdate = shauMapApplication.getLastAlertUpdate();
        if (lastUpdate != null) {
            TextView headerText = (TextView) header.findViewById(R.id.myLocationListHeader);
            headerText.setText("Last Updated: " + lastUpdate);
        }
        listView.addHeaderView(header, null, false);
        listView.setAdapter(adapter);

        listView.getRootView().setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_key, menu);
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

    @Override
    public void alertStateChanged(String lineName, boolean activated) {
        ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
        shauMapApplication.updateAlertConfig(lineName, activated);
    }
}