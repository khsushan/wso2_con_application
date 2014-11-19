package org.com.wso2.ushan.bean;

public class LocalEvent {

    private String userID;
    private Double distance;
    private String sensorId;
    private double x;
    private double y;
    private double error;
    private long time;


    public String getUserID() {
        return userID;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }


    public Double getDistance() {
        return distance;
    }


    public void setDistance(Double distance) {
        this.distance = distance;
    }


    public double getX() {
        return x;
    }


    public void setX(double x) {
        this.x = x;
    }


    public double getY() {
        return y;
    }


    public void setY(double y) {
        this.y = y;
    }


    public String getSensorId() {
        return sensorId;
    }


    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }


    public double getError() {
        return error;
    }


    public void setError(double error) {
        this.error = error;
    }


    public long getTime() {
        return time;
    }


    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public boolean equals(Object obj) {
        LocalEvent user = (LocalEvent) obj;
        return this.sensorId.equals(user.getSensorId());
    }


}
