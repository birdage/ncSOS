/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asascience.ncsos.cdmclasses;

import org.joda.time.Chronology;
import org.joda.time.chrono.ISOChronology;

import com.asascience.sos.dataproducts.Station;

import java.util.Date;
import java.util.List;

import javax.swing.text.DateFormatter;

/**
 * @author abird
 * @version 1.0.0
 */
public abstract class baseCDMClass implements iStationData {

    protected double upperLon, lowerLon, lowerLat, upperLat, lowerAlt, upperAlt;
    protected String startDate;
    protected String endDate;
    protected List<String> reqStationNames;
    protected int numberOfStations;
    protected static final String DATA_RESPONSE_ERROR = "Data Response IO Error: ";
    protected static final String ERROR_NULL_DATE = "ERROR NULL Date!!!!";
    protected static final int Invalid_Value = -9999999;
    protected static final String Invalid_Station = "INVALID_ST";
    protected Chronology chrono = ISOChronology.getInstance();
    protected DateFormatter df = new DateFormatter();
    
    protected static org.slf4j.Logger _log = org.slf4j.LoggerFactory.getLogger(baseCDMClass.class);
    
    public void checkLatLonAltBoundaries(List<Station> stationList, int i) {
        //LAT?LON PARSING
        //lat
        if (stationList.get(i).getLatitude() > upperLat) {
            upperLat = stationList.get(i).getLatitude();
        }
        if (stationList.get(i).getLatitude() < lowerLat) {
            lowerLat = stationList.get(i).getLatitude();
        }
        //lon
        if (stationList.get(i).getLatitude() > upperLon) {
            upperLon = stationList.get(i).getLatitude();
        }
        if (stationList.get(i).getLatitude() < lowerLon) {
            lowerLon = stationList.get(i).getLatitude();
        }
        // alt
        if (stationList.get(i).getAltitude() > upperAlt) {
            upperAlt = stationList.get(i).getAltitude();
        }
        if (stationList.get(i).getAltitude() < lowerAlt) {
            lowerAlt = stationList.get(i).getAltitude();
        }
    }
   
    public List<String> getStationNames() {
        return reqStationNames;
    }

  
    public double getBoundUpperLon() {
        return upperLon;
    }

   
    public double getBoundUpperLat() {
        return upperLat;
    }

   
    public double getBoundLowerLon() {
        return lowerLon;
    }

   
    public double getBoundLowerLat() {
        return lowerLat;
    }

  
    public String getBoundTimeBegin() {
        return startDate;
    }

   
    public String getBoundTimeEnd() {
        return endDate;
    }

  
    public void setStartDate(String startDateStr) {
        this.startDate = startDateStr;
    }

   
    public void setEndDate(String endDateStr) {
        this.endDate = endDateStr;
    }

    
    public void setNumberOfStations(int numOfStations) {
        this.numberOfStations = numOfStations;
    }

    
    public int getNumberOfStations() {
        return numberOfStations;
    }
    
    public boolean isStationInFinalList(int stNum) {
        return true;
    }

    public double getLowerAltitude(int stNum) {
        return 0;
    }
    
    public double getUpperAltitude(int stNum) {
        return 0;
    }
    
    
    public double getBoundLowerAlt() {
        if (Double.toString(lowerAlt).contains("fin") || Double.toString(lowerAlt).equalsIgnoreCase("nan"))
            return 0;
        return lowerAlt;
    }
    
    
    public double getBoundUpperAlt() {
        if (Double.toString(lowerAlt).contains("fin") || Double.toString(upperAlt).equalsIgnoreCase("nan"))
            return 0;
        return upperAlt;
    }
}
