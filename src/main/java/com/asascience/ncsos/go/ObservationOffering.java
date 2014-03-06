/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asascience.ncsos.go;

import java.util.List;

/**
 * This object is an observation offering object containing all the info needed for the section.
 * Used primarily as a shared structure among observations defining much of the items
 * needed for observation responses. Also implements the ObservationOfferingInterface
 * interface for a collection of getters and setters.
 * @author abird
 * @version 1.0.0
 */
public final class ObservationOffering implements ObservationOfferingInterface {
   private String stationID;
    private String stationDescription;
    private String stationName;
    private String srsName;
    private String lowerCornerLatLon;
    private String upperCornerLatLon;
    private String timeBegin;
    private String timeEnd;
    private String procedureLink;
    private String observedProperty;
    private String featureOfInterest;
    private String obsResultModel;
    private String obsResponseMode;
    private List observedProperties;

    /**
     * Creates an instance of ObservationOffering with empty values;
     */
    public ObservationOffering() {
        setObservationStationID(" ");
        setObservationStationDescription(" ");
        setObservationName(" ");
        setObservationSrsName(" ");
        setObservationStationLowerCorner(" ", " ");
        setObservationStationUpperCorner(" ", " ");
        setObservationTimeBegin(" ");
        setObservationTimeEnd(" ");
        setObservationProcedureLink(" ");
        setObservationObservedProperty(" ");
        setObservationFeatureOfInterest(" ");
        setObservationModel(" ");
        setObservationResponseMode(" ");
    }

    /**
     * observedProperties getter
     * @return List (of string types)
     */
    public List getObservationObserveredList(){
        return observedProperties;
    }

    /**
     * observedProperties setter
     * @param list List (of string types)
     */
    public void setObservationObserveredList(List list){
        this.observedProperties = list;
    }

    /**
     * getter for stationID
     * @return stationID as String
     */   
     public String getObservationStationID() {
      return stationID;
    }

    /**
     * setter for stationID
     * @param stationID stationID as a String
     */
   
    public void setObservationStationID(String stationID) {
        this.stationID = stationID;
    }

    /**
     * getter for stationDescription
     * @return stationDescription as a String
     */
  
    public String getObservationStationDescription() {
       return stationDescription;
    }

    /**
     * setter for stationDescription
     * @param stationDescription stationDescription as a String
     */
    
    public void setObservationStationDescription(String stationDescription) {
        this.stationDescription = stationDescription;
    }

    /**
     * getter for stationName
     * @return stationName as a String
     */
   
    public String getObservationName() {
       return stationName;
    }

    /**
     * setter for stationName
     * @param obsName stationName as a String
     */
   
    public void setObservationName(String obsName) {
      this.stationName = obsName;
    }

    /**
     * getter for srsName
     * @return srsName as a String
     */
    
    public String getObservationSrsName() {
        return srsName;
    }

    /**
     * setter for srsName
     * @param obsSrsName srsName as a String
     */
    
    public void setObservationSrsName(String obsSrsName) {
       this.srsName = obsSrsName;
    }

    /**
     * getter for the observation's lower corner "lat lon"
     * @return String representation of the lower corner
     */
  
    public String getObservationStationLowerCorner() {
       return lowerCornerLatLon;
    }

    /**
     * setter for the observation's lower corner
     * @param lat latitude of the observation's lower corner
     * @param lon longitude of the observation's lower corner
     */
   
    public void setObservationStationLowerCorner(String lat, String lon) {
        this.lowerCornerLatLon = lat.concat(" "+lon);
    }

    /**
     * getter for the observation's upper corner 
     * @return String representation of the upper corner
     */
  
    public String getObservationStationUpperCorner() {
        return upperCornerLatLon;
    }

    /**
     * setter for the observation's upper corner
     * @param lat latitude of the observation's upper corner
     * @param lon longitude of the observation's upper corner
     */
   
    public void setObservationStationUpperCorner(String lat, String lon) {
       this.upperCornerLatLon = lat.concat(" "+lon);
    }

    /**
     * getter for the starting time of the observation
     * @return String time stamp of the start time
     */
  
    public String getObservationTimeBegin() {
        return timeBegin;
    }

    /**
     * setter for the starting time of the observation
     * @param DateTime time stamp of the start time
     */
    
    public void setObservationTimeBegin(String DateTime) {
        this.timeBegin = DateTime;
    }

    /**
     * getter for the ending time of the observation
     * @return time stamp of the end time
     */
   
    public String getObservationTimeEnd() {
       return timeEnd;
    }

    /**
     * setter for the ending time of the observation
     * @param DateTime time stamp of the end time
     */
    
    public void setObservationTimeEnd(String DateTime) {
        this.timeEnd = DateTime;
    }

    /**
     * adds the link as an attribute
     * @return @String HTTP Link
     */
    
    public String getObservationProcedureLink() {
        return procedureLink;
    }

    /**
     * setter for the procedureLink
     * @param procedureLink the procedureLink
     */
    
    public void setObservationProcedureLink(String procedureLink) {
        this.procedureLink = procedureLink;
    }

     /**
     * adds the link as an attribute
     * @return @String HTTP Link
     */
   
    public String getObservationObservedProperty() {
        return observedProperty;
    }

    /**
     * setter for the observedProperty (singular)
     * @param observedProperty the observed property from the request
     */
    
    public void setObservationObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }

    /**
     * adds the link as an attribute
     * @return @String HTTP Link
     */

    public String getObservationFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * setter for the featureOfIneterest from the dataset
     * @param FeatureOfInterest featureOfInterest
     */
   
    public void setObservationFeatureOfInterest(String FeatureOfInterest) {
        this.featureOfInterest = FeatureOfInterest;
    }

    /**
     * getter for obsResultModel
     * @return obsResultModel
     */
  
    public String getObservationModel() {
       return obsResultModel;
    }

    /**
     * setter for obsResultModel
     * @param obsResultModel obsResultModel
     */
  
    public void setObservationModel(String obsResultModel) {
       this.obsResultModel = obsResultModel;
    }

    /**
     * getter for obsResponseMode
     * @return obsResponseMode
     */
  
    public String getObservationResponseMode() {
        return obsResponseMode;
    }

    /**
     * setter for obsResponseMode
     * @param responseMode obsResponseMode
     */
    
    public void setObservationResponseMode(String responseMode) {
        this.obsResponseMode = responseMode;
    }

}
