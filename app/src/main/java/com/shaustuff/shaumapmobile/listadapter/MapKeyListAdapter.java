package com.shaustuff.shaumapmobile.listadapter;

import java.util.List;

import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ImageView;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.content.res.Resources;

import com.shaustuff.shaumapmobile.R;
import com.shaustuff.shaumapmobile.model.RouteAlert;
import com.shaustuff.shaumapmobile.callback.AlertCallback;

public class MapKeyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private List<RouteAlert> routeAlerts;
    private AlertCallback alertCallback;

    public MapKeyListAdapter(Activity context, String[] routeAlertNames, List<RouteAlert> routeAlerts, AlertCallback alertCallback) {

        super(context, R.layout.map_key_list, routeAlertNames);

        this.context = context;
        this.routeAlerts = routeAlerts;

        this.alertCallback = alertCallback;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        RouteAlert routeAlert = routeAlerts.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.map_key_list, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        txtTitle.setTextColor(Color.WHITE);
        txtTitle.setTextSize(15);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        boolean checkboxEnabled = routeAlert.isAlertEnabled();

        final CheckBox alertCheckbox = (CheckBox) rowView.findViewById(R.id.checkBox);
        if (checkboxEnabled) {
            alertCheckbox.setTextColor(Color.WHITE);
            alertCheckbox.setHighlightColor(Color.WHITE);
            int id = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
            alertCheckbox.setButtonDrawable(id);
            alertCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //checkbox clicked - callback to save preference
                    CheckBox cb = (CheckBox) v;
                    alertCallback.alertStateChanged(routeAlerts.get(position).getRouteName(), cb.isChecked());
                }
            });
        }

        txtTitle.setText(routeAlert.getRouteName());
        imageView.setImageResource(routeAlert.getRouteIconId());
        alertCheckbox.setEnabled(checkboxEnabled);
        alertCheckbox.setChecked(routeAlert.isAlertOn());

        return rowView;
    };
}
