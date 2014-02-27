package com.asascience.sos.dataproducts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

	public String findCoordinateAxis(String name) {
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

	
	public void connection(){
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");
 
		try {
 
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;
 
		}
 
		System.out.println("PostgreSQL JDBC Driver Registered!");
 
		Connection connection = null;
 
		try {
 
			connection = DriverManager.getConnection("jdbc:postgresql://eoi-dev1.oceanobservatories.org:5432/postgres", "postgres","");
 
		} catch (SQLException e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
 
		}
 
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}
	
}
