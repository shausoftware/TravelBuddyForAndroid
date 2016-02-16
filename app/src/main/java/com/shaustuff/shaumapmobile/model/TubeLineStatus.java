package com.shaustuff.shaumapmobile.model;

public class TubeLineStatus {

    private String lineName;
    private String lineStatusDetails;
    private String lineStatusDescription;
    private boolean lineOk;

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
    public String getLineName() {
        return lineName;
    }

    public void setLineStatusDetails(String lineStatusDetails) {
        this.lineStatusDetails = lineStatusDetails;
    }
    public String getLineStatusDetails() {
        return lineStatusDetails;
    }

    public void setLineStatusDescription(String lineStatusDescription) {
        this.lineStatusDescription = lineStatusDescription;
    }
    public String getLineStatusDescription() {
        return lineStatusDescription;
    }

    public void setLineOk(boolean lineOk) {
        this.lineOk = lineOk;
    }
    public boolean isLineOk() {
        return lineOk;
    }
}
