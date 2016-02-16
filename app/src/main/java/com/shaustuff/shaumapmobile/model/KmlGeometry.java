package com.shaustuff.shaumapmobile.model;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.shaustuff.shaumapmobile.R;

/**
 * Geometry for marker
 */
public class KmlGeometry implements Comparable<KmlGeometry> {

    public static final int CATEGORY_UNDEFINED = 0;
    public static final int CATEGORY_BUS_STOP = 1;
    public static final int CATEGORY_TUBE_STATION = 2;
    public static final int CATEGORY_TRAIN_STATION = 3;
    public static final int CATEGORY_RIVER_BOAT = 4;
    public static final int CATEGORY_HAIL_AND_RIDE = 5;
    public static final int CATEGORY_LOCATION = 6;

    private static final String STATE_UNDEFINED = "UNDEFINED";

    private static final String STATE_DELIMITER = "&&&";

    /* Internal Fields */

    private int category = CATEGORY_UNDEFINED;
    private String name = STATE_UNDEFINED;
    private String route = STATE_UNDEFINED;
    private String id = STATE_UNDEFINED;
    private String link = STATE_UNDEFINED;
    private String settingsSlot;

    int colour;

    private List<LatLng> coordinates = new ArrayList<LatLng>();

    boolean valid = true;

    @Override
    public int compareTo(KmlGeometry other) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (category == CATEGORY_UNDEFINED && other.getCategory() == CATEGORY_UNDEFINED) {
            return EQUAL;
        } else if (category == CATEGORY_UNDEFINED) {
            return AFTER;
        } else if (other.getCategory() == CATEGORY_UNDEFINED) {
            return BEFORE;
        }

        //compare strings
        return this.getName().compareTo(other.getName());
    }

    public void setSettingsSlot(String settingsSlot) {
        this.settingsSlot = settingsSlot;
    }
    public String getSettingsSlot() {
        return settingsSlot;
    }

    public String getStateAsString() {

        String state = category + STATE_DELIMITER +
                name + STATE_DELIMITER +
                route + STATE_DELIMITER +
                id + STATE_DELIMITER +
                link + STATE_DELIMITER;

        int i = 0;
        for (LatLng coordinate : coordinates) {
            if (i != 0) {
                state += ":";
            }
            state += coordinate.latitude + "," + coordinate.longitude;
            i++;
        }
        return state;
    }

    public void setStateFromString(String state) {

        String[] split = state.split(STATE_DELIMITER);

        category = Integer.valueOf(split[0]);
        name = split[1];
        route = split[2];
        id = split[3];
        link = split[4];

        String coordssplit[] = split[6].split(":");
        for (int i = 0; i < coordssplit.length; i++) {
            String coordsplit[] = coordssplit[i].split(",");
            LatLng latLng = new LatLng(Double.valueOf(coordsplit[0]), Double.valueOf(coordsplit[1]));
            coordinates.add(latLng);
        }
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * Get image for marker
     * @return marker image resource id
     */
    public int getImageResourceId() {

        int resourceId = 0;

        if (CATEGORY_BUS_STOP == category) {
            resourceId = R.drawable.bus_stop_20;
        } else if (CATEGORY_TUBE_STATION == category) {

            if ("bakerloo".equalsIgnoreCase(route)) {
                resourceId = R.drawable.bakerloo_20;
            } else if ("central".equalsIgnoreCase(route)) {
                resourceId = R.drawable.central_20;
            } else if ("circle".equalsIgnoreCase(route)) {
                resourceId = R.drawable.circle_20;
            } else if ("district".equalsIgnoreCase(route)) {
                resourceId = R.drawable.district_20;
            } else if ("hammersmith-city".equalsIgnoreCase(route)) {
                resourceId = R.drawable.hammersmith_city_20;
            } else if ("jubilee".equalsIgnoreCase(route)) {
                resourceId = R.drawable.jubilee_20;
            } else if ("metropolitan".equalsIgnoreCase(route)) {
                resourceId = R.drawable.metropolitan_20;
            } else if ("northern".equalsIgnoreCase(route)) {
                resourceId = R.drawable.northern_20;
            } else if ("piccadilly".equalsIgnoreCase(route)) {
                resourceId = R.drawable.piccadilly_20;
            } else if ("victoria".equalsIgnoreCase(route)) {
                resourceId = R.drawable.victoria_20;
            } else if ("waterloo-city".equalsIgnoreCase(route)) {
                resourceId = R.drawable.dlr_20;
            } else if ("dlr".equalsIgnoreCase(route)) {
                resourceId = R.drawable.dlr_20;
            }
        } else if (CATEGORY_TRAIN_STATION == category) {
            resourceId = R.drawable.britishrail_20;
        } else if (CATEGORY_HAIL_AND_RIDE == category) {
            resourceId = R.drawable.hailandride_20;
        } else if (CATEGORY_RIVER_BOAT == category) {
            resourceId = R.drawable.riverboat_20;
        } else if (CATEGORY_LOCATION == category) {
            resourceId = R.drawable.postcode;
        } else if (CATEGORY_UNDEFINED == category) {
            resourceId = R.drawable.travelbuddy;
        }

        return resourceId;
    }

    /* Field Accessors */

    public void setCategory(int category) {
        this.category = category;
    }
    public int getCategory() {
        return category;
    }

    public void setRoute(String route) {
        this.route = route;
    }
    public String getRoute() {

        String displayName = route;

        if (CATEGORY_BUS_STOP == category || CATEGORY_RIVER_BOAT == category || CATEGORY_HAIL_AND_RIDE == category) {
            if (displayName != null && displayName.length() > 0) {
                if (displayName.charAt(0) == 58) {
                    //magic 58 = :
                    displayName = displayName.substring(1, displayName.length());
                }
            }
        }
        return displayName;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public int getColour() {
        return colour;
    }
    public void setColour(int colour) {
        this.colour = colour;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public String getLink() {
        return link;
    }

    /**
     * Get marker coodinates
     * @return google LatLng
     */
    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    /**
     * Build geometry from string
     * @param geometryString
     */
    public void buildGeometry(String geometryString) {

        coordinates = new ArrayList<LatLng>();

        String lines[] = geometryString.split("\\r\\n");
        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].trim();

            if (line != null && line.length() > 0) {

                String coordpairs[] = line.split(",");
                if (coordpairs.length > 1) {
                    LatLng coordinate = new LatLng(Float.valueOf(coordpairs[0]), Float.valueOf(coordpairs[1]));
                    coordinates.add(coordinate);
                } else {
                    valid = false;
                }
            }
        }
    }

    /**
     * Build Link to external Timetable
     */
    public void buildLink() {

        if (CATEGORY_BUS_STOP == category || CATEGORY_RIVER_BOAT == category || CATEGORY_HAIL_AND_RIDE == category) {
            link = "http://m.countdown.tfl.gov.uk/arrivals/" + id;
        } else if (CATEGORY_TUBE_STATION == category) {
            link = "https://www.tfl.gov.uk/tube/timetable/" + route + "?FromId=" + id;
        } else if (CATEGORY_TRAIN_STATION == category) {
            link = "http://ojp.nationalrail.co.uk/service/ldbboard/dep/" + id;
        }
    }
}
