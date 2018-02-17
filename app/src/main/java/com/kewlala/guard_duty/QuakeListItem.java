package com.kewlala.guard_duty;

import android.net.Uri;
import java.util.Date;

/**
 * Created by jhancock2010 on 1/27/18.
 * POJO for list item data
 */

public class QuakeListItem {

    public QuakeListItem(double magnitude, String location, Date date, String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.date = date;
        this.url = url;
    }

    private double magnitude;
    private String location;
    private Date date;
    private String url;

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
