package com.shaustuff.shaumapmobile.xmlparser;

import com.shaustuff.shaumapmobile.model.KmlGeometry;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * SAX Parser for Transport KML - Code is ugly ;)
 */
public class KmlParser extends DefaultHandler {

    private static final String ATTR_PLACEMARK = "Placemark";
    private static final String ATTR_COORDINATES = "coordinates";
    private static final String ATTR_EXTENDED_DATA = "ExtendedData";
    private static final String ATTR_DATA = "Data";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_LAST_UPDATE = "LastDataUpdate";

    private boolean inPlacemark = false;
    private boolean inCoordinates = false;
    private boolean inExtendedData = false;
    private boolean inData = false;
    private boolean inValue = false;
    private boolean inLastDataUpdate = false;

    private List<KmlGeometry> geometries = new ArrayList<KmlGeometry>();
    private KmlGeometry geometry;
    private String fieldName;
    private String stopType;

    private String lastUpdate = null;

    public List<KmlGeometry> getParsedGeometry() {
        return  geometries;
    }
    public String getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public void startDocument() throws SAXException {
        geometries = new ArrayList<KmlGeometry>();
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        if (ATTR_LAST_UPDATE.equals(localName)) {
            inLastDataUpdate = true;
        } else if (ATTR_PLACEMARK.equals(localName)) {
            geometry = new KmlGeometry();
            inPlacemark = true;
        } else if (ATTR_COORDINATES.equals(localName)) {
            inCoordinates = true;
        } else if (ATTR_EXTENDED_DATA.equals(localName)) {
            inExtendedData = true;
        } else if (ATTR_DATA.equals(localName)) {
            fieldName = atts.getValue("name");
            inData = true;
        } else if (ATTR_VALUE.equals(localName)) {
            inValue = true;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

        if (ATTR_LAST_UPDATE.equals(localName)) {
            inLastDataUpdate = false;
        } else if (ATTR_PLACEMARK.equals(localName)) {
            geometries.add(geometry);
            inPlacemark = false;
        } else if (ATTR_COORDINATES.equals(localName)) {
            inCoordinates = false;
        } else if (ATTR_EXTENDED_DATA.equals(localName)) {
            inExtendedData = false;
        } else if (ATTR_DATA.equals(localName)) {
            inData = false;
        } else if (ATTR_VALUE.equals(localName)) {
            inValue = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {

        if (inLastDataUpdate) {
            lastUpdate = new String(ch, start, length);
        }

        if (inCoordinates) {
            geometry.buildGeometry(new String(ch, start, length));
        }

        if (inExtendedData) {

            if (inData) {
                if (inValue) {

                    if ("stopType".equals(fieldName)) {
                        stopType = new String(ch, start, length);
                    } else {

                        if ("Bus Stop".equalsIgnoreCase(stopType)) {

                            //Bus Stops
                            if ("stopName".equals(fieldName)) {
                                //Bus Stop Name
                                geometry.setName(new String(ch, start, length));
                            } else if ("stopRoute".equals(fieldName)) {
                                //Bus Route
                                geometry.setRoute(new String(ch, start, length));
                            } else if ("stopId".equals(fieldName)) {
                                //Bus Stop Id
                                geometry.setId(new String(ch, start, length));
                                geometry.setCategory(KmlGeometry.CATEGORY_BUS_STOP);
                                geometry.buildLink();
                                //debug if geometry invalid
/*
                                if (!geometry.isValid()) {
                                    System.out.println("* BUS STOP GEOMETRY INVALID *");
                                    System.out.println("name:" + geometry.getName());
                                    System.out.println("route:" + geometry.getRoute());
                                    System.out.println("id:" + geometry.getId());
                                }
//*/
                            }

                        } else if ("Tube Station".equalsIgnoreCase(stopType)) {

                            //Tube Stations
                            if ("stopName".equals(fieldName)) {
                                //Tube Station Name
                                geometry.setName(new String(ch, start, length));
                            } else if ("stopRoute".equals(fieldName)) {
                                //Tube Line
                                String tubeLine =  new String(ch, start, length).trim();
                                geometry.setRoute(tubeLine);

                            } else if ("stopId".equals(fieldName)) {
                                //Station Id
                                geometry.setId(new String(ch, start, length));
                                geometry.setCategory(KmlGeometry.CATEGORY_TUBE_STATION);
                                geometry.buildLink();
                                //debug if geometry invalid
/*
                                if (!geometry.isValid()) {
                                    System.out.println("* TUBE STATION GEOMETRY INVALID *");
                                    System.out.println("name:" + geometry.getName());
                                    System.out.println("route:" + geometry.getRoute());
                                    System.out.println("id:" + geometry.getId());
                                }
//*/
                            }

                        } else if ("Train Station".equalsIgnoreCase(stopType)) {

                            //Train Stations
                            if ("stopName".equals(fieldName)) {
                                //Station Name
                                geometry.setName(new String(ch, start, length));
                            } else if ("stopRoute".equals(fieldName)) {
                                //Station Route - not implemented
                                geometry.setRoute(new String(ch, start, length));
                            } else if ("stopId".equals(fieldName)) {
                                //Station Id
                                geometry.setId(new String(ch, start, length));
                                geometry.setCategory(KmlGeometry.CATEGORY_TRAIN_STATION);
                                geometry.buildLink();
                                //debug if geometry invalid
/*
                                if (!geometry.isValid()) {
                                    System.out.println("* TRAIN STATION GEOMETRY INVALID *");
                                    System.out.println("name:" + geometry.getName());
                                    System.out.println("route:" + geometry.getRoute());
                                    System.out.println("id:" + geometry.getId());
                                }
//*/
                            }
                        } else if ("Hail and Ride".equalsIgnoreCase(stopType)) {

                            //Hail and Ride same as Bus Stops
                            if ("stopName".equals(fieldName)) {
                                //Stop Name
                                geometry.setName(new String(ch, start, length));
                            } else if ("stopRoute".equals(fieldName)) {
                                //Bus Route
                                geometry.setRoute(new String(ch, start, length));
                            } else if ("stopId".equals(fieldName)) {
                                //Stop Id
                                geometry.setId(new String(ch, start, length));
                                geometry.setCategory(KmlGeometry.CATEGORY_HAIL_AND_RIDE);
                                geometry.buildLink();
                                //debug if geometry invalid
/*
                                if (!geometry.isValid()) {
                                    System.out.println("* HAIL AND RIDE GEOMETRY INVALID *");
                                    System.out.println("name:" + geometry.getName());
                                    System.out.println("route:" + geometry.getRoute());
                                    System.out.println("id:" + geometry.getId());
                                }
//*/
                            }

                        } else if ("River Boat".equalsIgnoreCase(stopType)) {

                            //River Boat same as Bus Stops
                            if ("stopName".equals(fieldName)) {
                                //Pier Name
                                geometry.setName(new String(ch, start, length));
                            } else if ("stopRoute".equals(fieldName)) {
                                //Boat Route
                                geometry.setRoute(new String(ch, start, length));
                            } else if ("stopId".equals(fieldName)) {
                                //Pier Stop Id
                                geometry.setId(new String(ch, start, length));
                                geometry.setCategory(KmlGeometry.CATEGORY_RIVER_BOAT);
                                geometry.buildLink();
                                //debug if geometry invalid
/*
                                if (!geometry.isValid()) {
                                    System.out.println("* RIVER BOAT GEOMETRY INVALID *");
                                    System.out.println("name:" + geometry.getName());
                                    System.out.println("route:" + geometry.getRoute());
                                    System.out.println("id:" + geometry.getId());
                                }
//*/
                            }
                        } else if ("Location".equalsIgnoreCase(stopType)) {

                            //Location Name
                            if ("stopName".equals(fieldName)) {
                                //Location
                                geometry.setName(new String(ch, start, length));
                                geometry.setRoute(new String(ch, start, length));
                            } else if ("stopRoute".equals(fieldName)) {
                                //Not utilised
                            } else if ("stopId".equals(fieldName)) {
                                //Pier Stop Id
                                geometry.setId(new String(ch, start, length));
                                geometry.setCategory(KmlGeometry.CATEGORY_LOCATION);
                                //debug if geometry invalid
/*
                                if (!geometry.isValid()) {
                                    System.out.println("* LOCATION GEOMETRY INVALID *");
                                    System.out.println("name:" + geometry.getName());
                                    System.out.println("route:" + geometry.getRoute());
                                    System.out.println("id:" + geometry.getId());
                                }
//*/
                            }
                        }

                    }
                }
            }
        }
    }
}