package com.asascience.sos.dataproducts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * 
 * @author abird
 * 
 */
public class PostgresDataReader implements IDataProduct {

	private static final String DBNAME = "postgres";
	private static final String DBUSERNAME = "rpsdev";
	private static final String DBPASS = "";
	private static final String DBSERVER = "localhost";
	private static final String DBPORT = "5432";

	Connection connection = null;
	Statement st = null;
	ResultSet rs = null;

	String[] requestedObservedProperty = null;
	String[] requestedOfferings = null;
	
	ArrayList<String> requestedOfferingList = new ArrayList<String>();

	public PostgresDataReader() {
		// setup new connection
		setupConnection();
	}

	public void setup() {
		// check that the data is setup
		try {
			st = connection.createStatement();
			rs = makeSqlRequest("select Version()");
			printResultsSet(rs);

			//station information
			if (requestedOfferings != null) {
				for (int i = 0; i < requestedOfferings.length; i++) {
					rs = makeSqlRequest(doesTableExist(requestedOfferings[i]));
					//is it available
					if (getBooleanValResultsSet(rs)){
						requestedOfferingList.add(requestedOfferings[i]);
					}
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String doesTableExist(String dataset_id) {
		String sqlcmd = "SELECT 1 FROM pg_catalog.pg_class WHERE relname = \'" + dataset_id + "\';";
		return sqlcmd;
	}

	public ResultSet makeSqlRequest(String query) throws SQLException {
		st = connection.createStatement();
		rs = st.executeQuery(query);
		return rs;
	}

	public void printResultsSet(ResultSet rs) throws SQLException {
		while (rs.next()) {
			System.out.println(rs.getString(1));
		}
		rs.close();
	}

	public boolean getBooleanValResultsSet(ResultSet rs) throws SQLException {
		boolean ret = false;
		while (rs.next()) {
			ret = Boolean.parseBoolean(rs.getString(1));
			
		}
		rs.close();
		return ret;
	}

	public void setupConnection() {
		System.out
				.println("-------- PostgreSQL JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;

		}
		System.out.println("PostgreSQL JDBC Driver Registered!");
		connection = null;

		try {

			connection = DriverManager.getConnection("jdbc:postgresql://"
					+ DBSERVER + ":" + DBPORT + "/" + DBNAME, DBUSERNAME,
					DBPASS);

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

	public void setOfferings(Object object) {
		if (object instanceof String[]) {
			this.requestedOfferings = (String[]) object;
		} else if (object instanceof String) {
			this.requestedOfferings = new String[] { object.toString() };

		}

	}

	public void setObservedProperty(Object object) {
		if (object instanceof String[]) {
			this.requestedObservedProperty = (String[]) object;
		} else if (object instanceof String) {
			this.requestedObservedProperty = new String[] { object.toString() };
		}
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
		try {
			st.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
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

	public List<String> getSensorNames() {
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

}
