package com.shaustuff.shaumapmobile.callback;

import com.shaustuff.shaumapmobile.model.KmlGeometry;

public interface MyLocationCallback {

    public void openTimetable(KmlGeometry location);
    public void openInMap(KmlGeometry location);
    public void deleteLocation(KmlGeometry location);
}
