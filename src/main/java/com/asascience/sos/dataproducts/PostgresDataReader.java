package com.asascience.sos.dataproducts;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * 
 * @author abird
 *
 */
public class PostgresDataReader implements IDataProduct {

	public String getReferencedFileLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFeatureDatasetType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean closeFile() {
		// TODO Auto-generated method stub
		return false;
	}

	public String findFeatureType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String findCoordinateAxis(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getOfferingList() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReferencedFileLocation(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFeatureDatasetType(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasFeatureDatasetType(String offering) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean closeFile(String offering) {
		// TODO Auto-generated method stub
		return false;
	}

	public String findFeatureType(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getLats(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getLons(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public LatLonBounds getLatLonBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<Integer, LatLonRect> getLatLonRects() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getVariables(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUnitsOfVariable(String offering, String variable) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getSensorNames(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public DateTime getStartDateTime(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public DateTime getStartDateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public DateTime getEndDateTime(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public DateTime getEndDateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public Interval getDateTimeRange(String offering) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<Integer, Interval> getDateTimeRanges() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<Integer, String> getStationNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReferenceAuthority() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getStationLat(int stationIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getStationLon(int stationIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getCRS_SRS_authorities() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> getCoordinateNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, Object> getGlobalAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public FeatureType getFeatureType() {
		// TODO Auto-generated method stub
		return null;
	}

}
