package com.asascience.sos.dataproducts;

/**
 * Interface for getting data out of an abstracted data products
 * @author abird
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public interface IDataProduct {

	public String[] getOfferingList();
	
	public String getReferencedFileLocation(String offering);
	
	public String getFeatureDatasetType(String offering);
	
	public boolean hasFeatureDatasetType(String offering);
	
	public boolean closeFile(String offering);
	
	public String findFeatureType(String offering);
	
	/**
	 * input axis type/name
	 */
	public String findCoordinateAxis(String name);
	
	public double[] getLats(String offering);
	
	public double[] getLons(String offering);
	
	public LatLonBounds getLatLonBounds();
	public HashMap<Integer, LatLonRect> getLatLonRects();
	
	public String[] getVariables(String offering);
	
	/**
	 * if available else return none
	 * @param offering
	 * @param variable
	 * @return
	 */
	public String getUnitsOfVariable(String offering,String variable);
	
	public String[] getSensorNames(String offering);
	
	
	
	/**
	 * get start date time
	 * @return
	 */
	public DateTime getStartDateTime( String offering);
	/**
	 * overall
	 * @return
	 */
	public DateTime getStartDateTime();
	
	/**
	 * get end date time
	 * @return
	 */
	public DateTime getEndDateTime(String offering);
	/**
	 * overall
	 * @return
	 */
	public DateTime getEndDateTime();
	
	/**
	 * gets the date time span
	 * @return
	 */
	public Interval getDateTimeRange(String offering);
	public HashMap<Integer, Interval> getDateTimeRanges();

	public HashMap<Integer, String> getStationNames();
	
	/**
     * Attempts to find the coordinate reference authority
     * @param varName var name to check for crs authority
     * @return the authority name, if there is one
     */
	public String getReferenceAuthority();
	
	
	public double getStationLat(int stationIndex);
	
	public double getStationLon(int stationIndex);
	
	
	public String[] getCRS_SRS_authorities();
	
	public ArrayList<String> getCoordinateNames();
	
	public HashMap<String, Object> getGlobalAttributes();

	public List<String> getSensorNames();

	public void setOfferings(Object object);

	public void setObservedProperty(Object object);

	public void setup();
	
	
}
