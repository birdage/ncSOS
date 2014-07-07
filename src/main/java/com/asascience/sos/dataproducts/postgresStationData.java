package com.asascience.sos.dataproducts;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.asascience.ncsos.cdmclasses.baseCDMClass;
import com.asascience.ncsos.cdmclasses.iStationData;

public class postgresStationData extends baseCDMClass implements iStationData{

	private ArrayList<String> eventTimes;
    private String[] variableNames;
    Connection connection = null;
	Statement st = null;
	ResultSet rs = null;
	private String DBNAME;
	private String DBUSERNAME;
	private String DBPASS;
	private String DBSERVER;
	private String DBPORT;
	private String TITLE;
	private String CONNECTION_PASSED;
	private String SessionStartup;
	public static String TIME_FIELD = "time";
    
    public void setParms(String stationName, String[] eventTime, String[] variableNames) {
    	startDate = null;
        endDate = null;
        this.variableNames = variableNames;
        this.reqStationNames = new ArrayList<String>();
        reqStationNames.addAll(Arrays.asList(stationName));
        if (eventTime != null) {
            this.eventTimes = new ArrayList<String>();
            this.eventTimes.addAll(Arrays.asList(eventTime));
        }
        else
            this.eventTimes = null;
		
	}
    
	public void setData(Object featureCollection) throws IOException {
		
	}

	public void setInitialLatLonBoundaries(List<Station> tsStationList) {
		
		
	}

	public void setupConnection() {
		System.out.println(TITLE);

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver? "+ "Include in your library path!");
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
			System.out.println(CONNECTION_PASSED);
		} else {
			System.out.println("Failed to make connection!");
		}
	}
	
	/**
	 * close all the stuff down
	 */
	public void closeConnection() {
		try {
			rs.close();
			st.close();
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();		
		}
	}
	
	public ResultSet makeSqlRequest(String query) throws SQLException {
		st = connection.createStatement();
		rs = st.executeQuery(query);
		return rs;
	}
	
	public String getDataResponse(int stNum) {

		try {

			String ret = createDataString(stNum);
			if (ret!=null){
				return ret;
			}else{
				return DATA_RESPONSE_ERROR + postgresStationData.class;
			}

		} catch (Exception ex) {
			Logger.getLogger(postgresStationData.class.getName()).log(Level.SEVERE,
					null, ex);
			return DATA_RESPONSE_ERROR + postgresStationData.class;
		}finally{
			closeConnection();
		}
	}
	
	public String createDataString(int stNum) throws Exception{
		//if no event time
		//builder
		StringBuilder builder = new StringBuilder();
		
	        if (eventTimes == null) {
	        	getData(builder,null,null);
	        } //if bounded event time        
	        else if (eventTimes.size() == 2) {
	        	getData(builder,eventTimes.get(0),eventTimes.get(1));
	        } //if single event time        
	        else if (eventTimes.size() == 1) {
	        	getData(builder,eventTimes.get(0),null);
	        }
		return builder.toString();
		
	}

	public String getDataTypes_CMD(String table){
		String sqlcmd = "select column_name, data_type from information_schema.columns where table_name = '"+table+"';";
		return sqlcmd;
	}
	
	private String getDataField_CMD(String dataset_id,String whereClause){
		String sqlcmd ="select ";
		
		for (int i = 0; i < variableNames.length; i++) {
			sqlcmd+="\""+variableNames[i]+"\"";
			if (i< variableNames.length-1){
				sqlcmd+= ",";
			}
		}
		if (whereClause==null){
		sqlcmd+= " from " + dataset_id + ";";
		}else{
			sqlcmd+= " from " + dataset_id + " where " + whereClause + ";";	
		}
		return sqlcmd;
	}
	
	public void getData(StringBuilder builder, String startDate, String endDate) throws SQLException {
		setupConnection();
		
		String sqlcmd = null;
		if (startDate ==null){
			sqlcmd = getDataField_CMD(reqStationNames.get(0),null);
		}
		//just a start date
		else if (endDate==null && startDate!=null){
			sqlcmd = getDataField_CMD(reqStationNames.get(0),"time ='"+startDate+"'");
		}
		//both start and end
		else if(endDate!=null && startDate!=null){
			sqlcmd = getDataField_CMD(reqStationNames.get(0),"time between '"+startDate+"' and '"+endDate+"'");
		}
		
		//rs = makeSqlRequest("select Version()");
		//printResultsSet(rs);
		
		//rs = makeSqlRequest(SessionStartup);
		//printResultsSet(rs);
		
		//make sure you dont 
		connection.setAutoCommit(false);
		rs = makeSqlRequest(sqlcmd);
		printResultsSet(rs,builder);
		
	}
	
	

	//blockSeparator=" " decimalSeparator="." tokenSeparator=","/>
	private void printResultsSet(ResultSet rs,StringBuilder builder) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		while (rs.next()) {
			for (int i = 0; i < meta.getColumnCount(); i++) {
				if  (meta.getColumnLabel(i+1).equalsIgnoreCase(TIME_FIELD)){
					//get date time from data
					//Convert it to ISO date time					
					//string date time to iso date time
					String dateTimeString = (rs.getString(i+1));
					DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
					DateTime dt = formatter.withZone(DateTimeZone.forID("UTC")).parseDateTime(dateTimeString);
					DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
					builder.append(fmt.print(dt));
				}else{
					builder.append(rs.getString(i+1));
				}
				if (i<meta.getColumnCount()-1){
					builder.append(",");
				}
			}
			//line ending
			builder.append(";");
		} 
		rs.close();
	}

	public void printResultsSet(ResultSet rs) throws SQLException {
		while (rs.next()) {
				System.out.println(rs.getString(1));
		}
		rs.close();
	}

	public String getStationName(int idNum) {
		return reqStationNames.get(idNum);
	}

	public double getLowerLat(int stNum) {
		return 0;
	}

	public double getLowerLon(int stNum) {
		return 0;
	}

	public double getUpperLat(int stNum) {
		return 0;
	}

	public double getUpperLon(int stNum) {
		return 0;
	}

	public String getTimeEnd(int stNum) {
		String time = "";
		return time;
	}

	public String getTimeBegin(int stNum) {
		String time = "";
		return time;
	}

	public String getDescription(int stNum) {
		return "description";
	}

	public List<String> getLocationsString(int stNum){
		List<String> location = new ArrayList<String>();
		location.add("");
		return location;
	}
	
	public void setConnectionParams(String dbserver, String dbport,
			String dbname, String dbusername, String dbpass,
			String connectionPassed,String title,String sessionstartup) {
		this.DBSERVER=dbserver;
		this.DBUSERNAME=dbusername;
		this.DBPORT = dbport;
		this.DBNAME = dbname;
		this.DBPASS = dbpass;
		this.TITLE = title;
		this.CONNECTION_PASSED = connectionPassed;
		this.SessionStartup = sessionstartup;
	}


	

	

	

}
