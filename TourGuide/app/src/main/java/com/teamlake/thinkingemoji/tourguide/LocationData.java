package com.teamlake.thinkingemoji.tourguide;

/**
 * Created by rahul_000 on 7/15/2017.
 */

public class LocationData {
    private double lon, lat;
    private String name;
    private String url;
    private int id;

    public LocationData() {

    }

    public LocationData(int _id, double _lat, double _lon, String _name, String _url) {
        id = _id;
        lat = _lat;
        lon = _lon;
        name = _name;
        url = _url;
    }

    public String toString() {
        return "( ID: " + id + ", LAT: " + lat + ", LON: " + lon + ", NAME: " + name + ", URL: " + url + ")";
    }

    public double getLon() {
        return lon;
    }
    public void setLon(double _lon) {
        lon = _lon;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double _lat) {
        lat = _lat;
    }

    public void setName(String _name) {
        name = _name;
    }
    public String getName() {
        return name;
    }

    public void setURL(String _url) {
        url = _url;
    }
    public String getURL() {
        return url;
    }

    public void setID(int _id) {
        id = _id;
    }
    public int getID() {
        return id;
    }
}