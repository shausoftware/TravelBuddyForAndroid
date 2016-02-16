package com.shaustuff.shaumapmobile.xmlparser;

import com.shaustuff.shaumapmobile.model.TubeLineStatus;

import java.util.Map;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TubeLineParser extends DefaultHandler {

    private static final String ELEM_LINE_STATUS = "LineStatus";
    private static final String ELEM_LINE = "Line";
    private static final String ELEM_STATUS = "Status";
    private static final String ATTR_NAME = "Name";
    private static final String ATTR_STATUS_DETAILS = "StatusDetails";
    private static final String ATTR_STATUS_DESCRIPTION  = "Description";

    private Map<String, TubeLineStatus> lineStatusMap = new HashMap<String, TubeLineStatus>();
    private TubeLineStatus currentTubeLineStatus = null;

    public Map<String, TubeLineStatus> getStatusMap() {
        return lineStatusMap;
    }

    @Override
    public void startDocument() throws SAXException {
        lineStatusMap = new HashMap<String, TubeLineStatus>();
        currentTubeLineStatus = null;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        if (ELEM_LINE_STATUS.equals(localName)) {

            currentTubeLineStatus = new TubeLineStatus();
            currentTubeLineStatus.setLineStatusDetails(atts.getValue(ATTR_STATUS_DETAILS));

        } else if (ELEM_LINE.equals(localName)) {

            String lineName = atts.getValue(ATTR_NAME);
            currentTubeLineStatus.setLineName(lineName);

        } else if (ELEM_STATUS.equals(localName)) {

            String description = atts.getValue(ATTR_STATUS_DESCRIPTION);
            currentTubeLineStatus.setLineStatusDescription(description);
            if ("Good Service".equalsIgnoreCase(description) ||
                    description.contains("Train service resumes")) {
                currentTubeLineStatus.setLineOk(true);
            } else {
                currentTubeLineStatus.setLineOk(false);
            }
            //done
            lineStatusMap.put(currentTubeLineStatus.getLineName(), currentTubeLineStatus);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    @Override
    public void characters(char ch[], int start, int length) {
    }
}