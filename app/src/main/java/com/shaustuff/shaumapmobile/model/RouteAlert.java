package com.shaustuff.shaumapmobile.model;

public class RouteAlert {

    private String routeName;
    private boolean alertEnabled;
    private boolean alertOn;
    private int routeIconId;

    public RouteAlert() {
    }

    public RouteAlert(String routeName, boolean alertEnabled, boolean alertOn, int routeIconId) {
        this.routeName = routeName;
        this.alertEnabled = alertEnabled;
        this.alertOn = alertOn;
        this.routeIconId = routeIconId;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    public String getRouteName() {
        return routeName;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }
    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertOn(boolean alertOn) {
        this.alertOn = alertOn;
    }
    public boolean isAlertOn() {
        if (!alertEnabled) {
            return false;
        }
        return alertOn;
    }

    public void setRouteIconId(int routeIconId) {
        this.routeIconId = routeIconId;
    }
    public int getRouteIconId() {
        return routeIconId;
    }
}
