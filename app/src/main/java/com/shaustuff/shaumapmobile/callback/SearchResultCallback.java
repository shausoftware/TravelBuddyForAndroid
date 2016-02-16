package com.shaustuff.shaumapmobile.callback;

import com.shaustuff.shaumapmobile.model.KmlGeometry;

public interface SearchResultCallback {

    public void openTimetable(KmlGeometry searchResult);
    public void showResultInMap(KmlGeometry searchResult);
    public void saveLocation(KmlGeometry searchResult);

}
