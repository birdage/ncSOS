package com.asascience.sos.dataproducts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class SampleDataReader implements IDataProduct {

	// GETCAPS--------------------------------------------------------------------
	
	public String[] getOfferingList() {
		String[] data = { "offer1" };
		return data;
	}

	public String getReferencedFileLocation(String offering) {
		return "temp";
	}

	public String getFeatureDatasetType(String offering) {
		return "none";
	}

	public boolean hasFeatureDatasetType(String offering) {
		return false;
	}

	/**
	 * no file to close
	 */
	public boolean closeFile(String offering) {
		return true;
	}

	public String findFeatureType(String offering) {
		return "none";
	}

	public String findCoordinateAxis(String name) {
		return "axis";
	}

	public double[] getLats(String offering) {
		double[] vals = { 1 };
		return vals;
	}

	public double[] getLons(String offering) {
		double[] vals = { 1 };
		return vals;
	}

	public LatLonBounds getLatLonBounds() {
		LatLonBounds b = new LatLonBounds();
		b.setBotLeft_lat(1);
		b.setBotLeft_lon(1);
		b.setTopLeft_lat(1);
		b.setTopLeft_lon(1);

		b.setBotRight_lat(1);
		b.setBotRight_lon(1);
		b.setTopRight_lat(1);
		b.setTopRight_lon(1);

		return b;
	}

	public HashMap<Integer, LatLonRect> getLatLonRects() {
		HashMap<Integer, LatLonRect> test = new HashMap<Integer, LatLonRect>();
		test.put(1, new LatLonRect(1, 1, 1, 1));
		return test;
	}

	public String[] getVariables(String offering) {
		String[] vars = { "temp", "density", "salinity", "temp_L1" };
		return vars;
	}

	public String getUnitsOfVariable(String offering, String variable) {
		return "none";
	}

	public String[] getSensorNames(String offering) {
		String[] vars = { "temp", "density", "salinity",
				"temp_L1_sen" };
		return vars;
	}

	public DateTime getStartDateTime(String offering) {
		return new DateTime().minusDays(1);
	}

	public DateTime getStartDateTime() {
		return new DateTime().minusDays(1);
	}

	public DateTime getEndDateTime(String offering) {
		return new DateTime();
	}

	public DateTime getEndDateTime() {
		return new DateTime();
	}

	public Interval getDateTimeRange(String offering) {
		return new Interval(getStartDateTime(), getEndDateTime());
	}

	public HashMap<Integer, Interval> getDateTimeRanges() {
		HashMap<Integer, Interval> test = new HashMap<Integer, Interval>();
		test.put(1, new Interval(getStartDateTime(), getEndDateTime()));

		return test;
	}

	public HashMap<Integer, String> getStationNames() {
		HashMap<Integer, String> test = new HashMap<Integer, String>();
		test.put(1, "offer1");
		return test;
	}

	public String getReferenceAuthority() {
		return "ooi";
	}

	public double getStationLat(int stationIndex) {
		return 0;
	}

	public double getStationLon(int stationIndex) {
		return 0;
	}

	public String[] getCRS_SRS_authorities() {
		String[] data = { "ooi", "test", "data" };
		return data;
	}

	public ArrayList<String> getCoordinateNames() {
		ArrayList<String> data = new ArrayList<String>();
		data.add("time");
		return data;
	}

	public HashMap<String, Object> getGlobalAttributes() {
		HashMap<String, Object> test = new HashMap<String, Object>();
		test.put("title", "ooi title");
		test.put("summary", "test sample sample");
		test.put("access_constraints", "test contraints");
		test.put("publisher_name", "ooi");
		test.put("publisher_email","ooi");
		test.put("keywords", "");
		return test;
	}

	public List<String> getSensorNames() {
		List<String> vars = new ArrayList<String>();
		vars.add("temp_sen");
		vars.add("density_sen");
		vars.add("salinity_sen");
		vars.add("temp_L1_sen");
		return vars;
	}

	public void setOfferings(Object object) {
		// TODO Auto-generated method stub
		
	}

	public void setObservedProperty(Object object) {
		// TODO Auto-generated method stub
		
	}

	public void setup() {
		// TODO Auto-generated method stub
		
	}

	public boolean isVariableAvailable(String offering, String variableRequested) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDatasetFeatureType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getFeatureTypeDataSet() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVariableStandardName(String variable) {
		// TODO Auto-generated method stub
		return "";
	}

	public String getFillValue(String obsProp) {
		// TODO Auto-generated method stub
		return "";
	}

	public boolean hasFillValue(String obsProp) {
		// TODO Auto-generated method stub
		return false;
	}

	//--------------------------------------------------------------------
}
