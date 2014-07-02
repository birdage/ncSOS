package com.asascience.sos.dataproducts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.json.JSONArray;
import org.json.JSONObject;

import com.asascience.ncsos.cdmclasses.iStationData;

/**
 * 
 * @author abird
 * 
 */
@SuppressWarnings("unused")
public class PostgresDataReader implements IDataProduct {

	private static org.slf4j.Logger _log = org.slf4j.LoggerFactory.getLogger(PostgresDataReader.class);
	
	/**
	 * set from properties file
	 */
	private String station_prefix = null;
	private String station_suffix = null;
	private String USER_AGENT = null;
	private String server = null;
	private String LAT_FIELD = null;
	private String TIME_FIELD = null;
	private String LON_FIELD = null;
	private String SessionStartup = null;
	private String GATEWAY_RESPONSE_JSON_NODE = "GatewayResponse";
	private String DATA_JSON_NODE = "data";
	private String NOMINAL_DATETIME = null;
	private String GEOSPATIAL_BOUNDS = null;
	private String DBNAME = null;
	private String DBUSERNAME = null;
	private String DBPASS = null;
	private String DBSERVER = null;
	private String DBPORT = null;
	private String TITLE = "-------- PostgreSQL JDBC Connection Testing ------------";
	private String CONNECTION_PASSED = "Connection working...";
	
	/**
	 * 
	 */
	public DateTime startDateTime = null;
	public DateTime endDateTime = null;
	
	Connection connection = null;
	Statement st = null;
	ResultSet rs = null;
	String[] requestedObservedProperty = null;
	String[] requestedOfferings = null;
	//available offering list
	ArrayList<String> requestedOfferingList = new ArrayList<String>();

	HashMap<Integer, LatLonRect> latLonRects = new HashMap<Integer, LatLonRect>();
	LatLonBounds totalLatLonBounds = new LatLonBounds();
	private boolean RR_temporal_bounds = false;
	private boolean RR_geo_bounds = false;
	
	//used to hold the variables per offering/station
	public HashMap<String, String[]> stationParameterList = new HashMap<String, String[]>();
	//ignore certain things...
	private ArrayList<String> ignoreParamList = new ArrayList<String>(){{
		add("geom");
	}};
	//hashmap for the units of a given offering/station
	//is basically a mapping Station->Hash of param then unit
	//Station:Param:unit
	public HashMap<String, HashMap<String, String>> unitList = new HashMap<String, HashMap<String,String>>();
	private List<String> sensorList;

	/**
	 * properties file
	 */
	Properties prop = new Properties();
	InputStream input = null;
	public static final String propertyFileName = "postgresDataReader.props";
	
	
	public PostgresDataReader() {
		_log.info("using postgres data reader");
		// setup new connection to DB
		parseProperties();
		setupConnection();
	}

	private void parseProperties(){
		try {
		    File currentDirectory = new File(new File(SOSDirectory+propertyFileName).getAbsolutePath());
		    //System.out.println();
			input = new FileInputStream(currentDirectory.getAbsolutePath());
			// load a properties file
			prop.load(input);
			this.USER_AGENT = prop.getProperty("USER_AGENT");
			this.server = prop.getProperty("server");
			this.station_prefix = prop.getProperty("station_prefix");
			this.station_suffix = prop.getProperty("station_suffix");
			this.LAT_FIELD = prop.getProperty("LAT_FIELD");
			this.TIME_FIELD = prop.getProperty("TIME_FIELD");
			this.LON_FIELD = prop.getProperty("LON_FIELD");
			this.SessionStartup = prop.getProperty("SessionStartup");
			this.NOMINAL_DATETIME = prop.getProperty("NOMINAL_DATETIME");
			this.GEOSPATIAL_BOUNDS = prop.getProperty("GEOSPATIAL_BOUNDS");
			this.DBNAME = prop.getProperty("DBNAME");
			this.DBUSERNAME = prop.getProperty("DBUSERNAME");
			this.DBPASS = prop.getProperty("DBPASS");
			this.DBSERVER = prop.getProperty("DBSERVER");
			this.DBPORT = prop.getProperty("DBPORT");
			_log.info("properties loaded");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setup() {
		// check that the data is setup
		_log.warn("requested offering:"+requestedOfferings[0]);
		_log.warn("requested offering:"+requestedOfferings.length);
		try {
			st = connection.createStatement();
			//checks that the connection is valid
			rs = makeSqlRequest("select Version()");
			printResultsSet(rs);

			rs = makeSqlRequest(SessionStartup);
			printResultsSet(rs);
			
			//station information
			if (requestedOfferings != null) {
				for (int i = 0; i < requestedOfferings.length; i++) {
					rs = makeSqlRequest(doesTableExist_CMD(requestedOfferings[i]));
					//is an offering available 
					if (getBooleanValResultsSet(rs)){
						_log.warn("Table requested does exist:"+requestedOfferings[i]);
						//add the offering to the list
						requestedOfferingList.add(requestedOfferings[i]);
						//add the station parameteres to a list, make sure to put it just as the resource name
						stationParameterList.put(requestedOfferings[i].substring(1, requestedOfferings[i].length()-5), getVariables(requestedOfferings[i]));
						//get meta dota about a resource from the RR, make sure to use just the resource name
						queryResourceRegistry_Params(requestedOfferings[i].substring(1, requestedOfferings[i].length()-5));
						//get meta dota about a resource from the RR, make sure to use just the resource name
						queryResourceResistry_Extents(requestedOfferings[i].substring(1, requestedOfferings[i].length()-5));
					}else{
						_log.error("Table requested DOES NOT EXIST!!!!:"+requestedOfferings[i]);
					}
				}
				//if these are not set then poop
				if (!RR_geo_bounds){
					parseLatLonRects();
				}
				
				if(!RR_temporal_bounds){
					parseTemporalBounds();
				}
			}
			
			
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			 // stack trace as a string
			_log.error("POSTGRES READER:"+e.getMessage()+"\n"+sw.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * read extent (temporal,geo) from the resource registry
	 * @param offering
	 * @throws Exception 
	 */
	public void queryResourceResistry_Extents(String offering) throws Exception{

		String result = queryResoureResistry(offering, server+"/ion-service/resource_registry/find_resources", "find_resources" ,"\"object_id\": \""+offering+"\", \"restype\": \"DataProduct\"");
		if (result!=null){
			JSONArray obj = ((new JSONObject(result.toString())).getJSONObject(DATA_JSON_NODE)).getJSONArray(GATEWAY_RESPONSE_JSON_NODE);
			JSONArray k  = (JSONArray) obj.get(0);
			for (int i = 0; i < k.length(); i++) {
				try {
					JSONObject newObk = (JSONObject) k.get(i);
					JSONObject geoBounds = (JSONObject) newObk.get(GEOSPATIAL_BOUNDS);
					JSONObject tempBounds = (JSONObject) newObk.get(NOMINAL_DATETIME);
					//SET THE BOUNDS
					setTheBounds(geoBounds,tempBounds);
 					//information has been found!
 					break;
				} catch (Exception e) {
					//DAMN DIDNT FIND IT
					_log.error("POSTGRES READER:"+e.getMessage());
					e.printStackTrace();
				}
			}
			
		}else{
			RR_geo_bounds = false;
			RR_temporal_bounds = false;
		}
		
	}
	
	
	
	private void setTheBounds(JSONObject geoBounds, JSONObject tempBounds) {
	    
		double lon_w = geoBounds.getDouble("geospatial_longitude_limit_west");
		double lon_e = geoBounds.getDouble("geospatial_longitude_limit_east");
		double lat_n = geoBounds.getDouble("geospatial_latitude_limit_north");
		double lat_s = geoBounds.getDouble("geospatial_latitude_limit_south");
		
		latLonRects.put(0, new LatLonRect(lat_s, lon_w, lat_n, lon_e));
		RR_geo_bounds = true;
		
		 
		//double that represents seconds since 1900-01-01,
		//string that represents MILLIseconds since 1970-01-01 
		//python float that's the number of seconds since 1970-01-01
		
		//tempBounds
		Object st = tempBounds.get("start_datetime");
		System.out.println(st.toString());
		if (st instanceof String){		
		}else if  (st instanceof Float){
			//means its seconds since 1970-01-01
			double val = (Double) (st);
			val = val/1000;
			DateTime now = new DateTime(val,DateTimeZone.UTC);			
		}

		Object ed = tempBounds.get("end_datetime");
		System.out.println(ed.toString());
		if (ed instanceof String){
		}else if  (ed instanceof Float){
			//means its seconds since 1970-01-01
			double val = (Double) (ed);
			val = val/1000;
			DateTime now = new DateTime(val,DateTimeZone.UTC);	
		}
		
		RR_temporal_bounds = true;
	}

	/**
	 * main class for parsing post request
	 * @param offering
	 * @param queryUrl
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public String queryResoureResistry(String offering,String queryUrl, String serviceOp, String params) throws Exception{
		CloseableHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(queryUrl);
		
		int timeout = 10; // seconds
		HttpParams httpParams = client.getParams();
		httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout * 1000);
		httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout * 1000);
 
		// add header
		post.setHeader("User-Agent", USER_AGENT);

	    
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		String data = "{\"serviceRequest\": {\"serviceName\": \"resource_registry\", "
				+ "\"params\": {"+params+"}, "
						+ "\"serviceOp\": \""+serviceOp+"\"}}";
		
		urlParameters.add(new BasicNameValuePair("payload", data));
		
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		
		
		_log.warn("about to make post request to 5000 service");
		HttpResponse response = client.execute(post);
		int respCode = response.getStatusLine().getStatusCode();
		_log.warn("completed request to 5000 service with a status code of:"+Integer.toString(respCode));
		//System.out.println("Response Code : " + respCode);
		
		// if post was successful
		if (respCode == 200) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		}else{
			_log.error("POSTGRES READER:"+"could not get data from RR");
		}
		post.releaseConnection();
		client.close();
		return null;
	}
	
	
	/**
	 * read parameter information from the resource registry
	 * @param offering minus the prefix and suffix
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void queryResourceRegistry_Params(String offering) throws Exception {
		String result = queryResoureResistry(offering, server+"/ion-service/resource_registry/read", "read" ,"\"object_id\": \""+offering+"\"");
		
		System.out.println(result.toString());
		
		if (result!=null){
			// parse the json response and get the data/gatewayresponse
			JSONObject obj = ((new JSONObject(result.toString())).getJSONObject(DATA_JSON_NODE)).getJSONObject(GATEWAY_RESPONSE_JSON_NODE);
			String stationStatek = obj.getString("lcstate");
			// get param dict
			JSONObject l = obj.getJSONObject("parameter_dictionary");
			// number of variables
			int val = l.getInt("_ParameterDictionary__count");
	
			// loop through the parameters and get the units
			HashMap<String, String> unitHash = new HashMap<String, String>();
			for (int i = 0; i < stationParameterList.get(offering).length; i++) {
				String param = stationParameterList.get(offering)[i];
				if (!ignoreParamList.contains(param)) {
					try {
						JSONArray paramObject = l.getJSONArray(param);
						JSONObject actualParamObject = (JSONObject) paramObject
								.get(1);
						String paramUnits = actualParamObject.getString("uom");
						unitHash.put(param, paramUnits);
						// JSONObject d
						// =actualParamObject.getJSONObject("param_type");
					} catch (Exception e) {
						_log.error("POSTGRES READER:"+"invalid results from RR:"+e.getMessage());
						unitHash.put(param, "unknown");
					}
	
				}
			}
			// finally add the units to the main hash
			unitList.put(offering, unitHash);
		}else{
			_log.error("POSTGRES READER:"+"could not get result RR");
		}
		
	}
	
	@Deprecated //should not be used really
	private void parseLatLonRects(){
		this.latLonRects = new HashMap<Integer, LatLonRect>();
		
		for (int i = 0; i < requestedOfferings.length; i++) {
			if (isOfferingAvailable(requestedOfferings[i])){
				getCalculatedRectangle(requestedOfferings[i],i);
			}else{
				getCalculatedRectangle(null,i);
			}
		}
		
	}
	
	/**
	 * if the temporal bounds are not set then figure them out!
	 */
	@Deprecated //should not be used really
	private void parseTemporalBounds() {
		//dont do anything at the mo as this should of been done from the RR
	}
	
	@Deprecated //should not be used really
	private void getCalculatedRectangle(String requestedOffering,int id) {
		if (requestedOffering!=null){
			try {
				// special kind of result set
				rs = makeSqlRequest(getMinMaxLatLon_CMD(requestedOffering));
				double[] latlons = new double[4];
				int count = 0;
				while (rs.next()) {
					String val = (rs.getString(1));
					latlons[count] = (Double.parseDouble(val));
					count++;
				}
				
				this.latLonRects.put(id, new LatLonRect(latlons[0], latlons[1], latlons[2], latlons[3]));
				rs.close();
							
			} catch (SQLException e) {
				_log.warn("POSTGRES READER:"+"using blank lat lon");
				this.latLonRects.put(id, new LatLonRect(Double.NaN, Double.NaN, Double.NaN, Double.NaN));
				e.printStackTrace();
			}catch (IndexOutOfBoundsException e) {
				_log.warn("POSTGRES READER:"+"using blank lat lon");
				this.latLonRects.put(id, new LatLonRect(Double.NaN, Double.NaN, Double.NaN, Double.NaN));
				e.printStackTrace();
			}
			
		}else{
			//generate mock/blank rect
			this.latLonRects.put(id, new LatLonRect(Double.NaN, Double.NaN, Double.NaN, Double.NaN));
		}
		
	}

	private boolean isOfferingAvailable(String requested){
		if (requestedOfferingList.contains(requested)){
			return true;
		}
		return false;
	}
	
	/**
	 * SQL String checks to see if table exists
	 * @param dataset_id
	 * @return
	 */
	public String doesTableExist_CMD(String dataset_id) {
		String sqlcmd = "SELECT 1 FROM pg_catalog.pg_class WHERE relname = \'" + dataset_id + "\';";
		return sqlcmd;
	}
	
	/**
	 * SQL String gets the table fields available
	 * @param dataset_id
	 * @return
	 */
	public String getTableFields_CMD(String dataset_id){
		String sqlcmd = "select column_name from information_schema.columns where table_name = \'" + dataset_id + "\';";
		return sqlcmd;
	}
	
	/**
	 * SQL String get the min and max lat lon boundaries
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public String getMinMaxLatLon_CMD(String dataset_id){
		String sqlcmd ="select min("+LAT_FIELD+"),min("+LON_FIELD+"),max("+LAT_FIELD+"),max("+LON_FIELD+") from \"" + dataset_id + "\";";
		return sqlcmd;
	}
	
	public String getMinMaxDates_CMD(String dataset_id){
		String sqlcmd ="select min("+LAT_FIELD+"),min("+LON_FIELD+"),max("+LAT_FIELD+"),max("+LON_FIELD+") from \"" + dataset_id + "\";";
		return sqlcmd;
	}
	
	/**
	 * SQL String get the lats from an offering
	 * @param dataset_id
	 * @return
	 */
	public String getLatField_CMD(String dataset_id){
		return getDataField_CMD(dataset_id, LAT_FIELD);
	}
	
	/**
	 * SQL String get the lons from an offering
	 * @param dataset_id
	 * @return
	 */
	public String getLonField_CMD(String dataset_id){
		return getDataField_CMD(dataset_id, LON_FIELD);
	}
	
	/**
	 * SQL String, uses the {@link PostgresDataReader#OOI_PREFIX} 
	 * @return 
	 */
	@Deprecated //not needed yet
	public String getAllFDT_CMD(){
		String sqlcmd = "SELECT relname FROM pg_catalog.pg_class where relkind =\'foreign table\';";
		return sqlcmd;
	}
	
	/**
	 * SQL String uses the {@link PostgresDataReader#station_suffix} to get the views
	 * @return
	 */
	public String getAllFDTViews_CMD() {
		String sqlcmd = "SELECT viewname FROM pg_catalog.pg_views where schemaname='public' and viewname like \'%"+station_suffix+"\';";
		return sqlcmd;
	}
	
	/**
	 * util builder for queries from a field(s)
	 */
	private String getDataField_CMD(String dataset_id,String datafield){
		String sqlcmd ="select "+datafield+" from \"" + dataset_id + "\";";
		return sqlcmd;
	}
	
	
	public ResultSet makeSqlRequest(String query) throws SQLException {
		st = connection.createStatement();
		rs = st.executeQuery(query);
		return rs;
	}

	public void printResultsSet(ResultSet rs) throws SQLException {
		while (rs.next()) {
			_log.warn("SQL Resp Info:"+rs.getString(1));
			System.out.println(rs.getString(1));
		}
		rs.close();
	}

	private boolean convertToBoolean(String value) {
	    boolean returnValue = false;
	    if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || 
	        "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
	        returnValue = true;
	    return returnValue;
	}
	
	public double[] getDoubleArray(ResultSet rs) {
		ArrayList<Double> dblList = new ArrayList<Double>();
		
		try {
			while (rs.next()) {
				String val = (rs.getString(1));
				dblList.add(Double.parseDouble(val));
			}
			rs.close();
			
		} catch (SQLException e) {
			_log.error("POSTGRES READER:"+"error creating double array:"+e.getMessage());
			e.printStackTrace();
		}
		
		double dblArray[] = new double[dblList.size()];
		for(int i = 0; i < dblArray.length; i++){
		     dblArray[i] = dblList.get(i);
		}
		
		return dblArray;
	}
	
	public String[] getStringArray(ResultSet rs) {
		String[] ret = null;
		try {
			ArrayList<String> list = new ArrayList<String>();
			while (rs.next()) {
				String val = (rs.getString(1));
				list.add(val);
			}
			rs.close();
			
			ret = list.toArray(new String[list.size()]);
		} catch (SQLException e) {
			_log.error("POSTGRES READER:"+"error creating string array:"+e.getMessage());
			e.printStackTrace();
		}
		
		return ret;
	}

	
	public boolean getBooleanValResultsSet(ResultSet rs) throws SQLException {
		boolean ret = false;
		while (rs.next()) {
			String val = (rs.getString(1));
			ret = convertToBoolean(val);
		}
		rs.close();
		return ret;
	}

	public void setupConnection() {
		System.out.println(TITLE);

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {
			_log.error("POSTGRES READER:"+"CANNOT LOAD POSTGRES DRIVE!:"+e.getMessage());
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
			_log.error("POSTGRES READER:"+"Connection Failed:"+e.getMessage());
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println(CONNECTION_PASSED);
		} else {
			_log.error("POSTGRES READER:"+"failed to make connection");
			System.out.println("Failed to make connection!");
		}
	}

	/**
	 * set the requested offerings (stations)
	 */
	public void setOfferings(Object object) {
		if (object instanceof String[]) {
			this.requestedOfferings = (String[]) object;
		} else if (object instanceof String) {
			this.requestedOfferings = new String[] { object.toString() };
		}
	}

	/**
	 * set the properties requested i.e temperature
	 */
	public void setObservedProperty(Object object) {
		if (object instanceof String[]) {
			this.requestedObservedProperty = (String[]) object;
		} else if (object instanceof String) {
			this.requestedObservedProperty = new String[] { object.toString() };
		}
	}

	public String[] getOfferingList() {
		return requestedOfferings;
	}

	//covereage location
	public String getReferencedFileLocation(String offering) {
		return "";
	}

	public String getFeatureDatasetType(String offering) {
		return "";
	}

	public boolean hasFeatureDatasetType(String offering) {
		return false;
	}

	/**
	 * close all the stuff down
	 */
	public boolean closeFile(String offering) {
		try {
			rs.close();
			st.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			_log.error("POSTGRES READER:"+"Could not close connection, Failed!:"+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * close all the stuff down
	 */
	public boolean closeFile() {
		try {
			rs.close();
			st.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			_log.error("POSTGRES READER:"+"Could not close connection, Failed!:"+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public String findFeatureType(String offering) {
		return "";
	}

	public String findCoordinateAxis(String name) {
		return "";
	}

	public double[] getLats(String offering) {
		getLatField_CMD(offering);
		return null;
	}

	public double[] getLons(String offering) {
		getLatField_CMD(offering);
		return null;
	}

	/**
	 * should be the lat lon bounds for all the requests
	 */
	public LatLonBounds getLatLonBounds() {
		return totalLatLonBounds;
	}

	public HashMap<Integer, LatLonRect> getLatLonRects() {
		return latLonRects;
	}

	public String[] getVariables(String offering) {
		try {
			return getStringArray(makeSqlRequest(getTableFields_CMD(offering)));
		} catch (SQLException e) {
			_log.error("POSTGRES READER:"+"Could not get variables:"+e.getMessage());
			e.printStackTrace();
		}
		return new String[]{};
	}

	public String getUnitsOfVariable(String offering, String variable) {
		if (offering == null) {
			return unitList.get(requestedOfferingList.get(0).substring(1,requestedOfferingList.get(0).length()-5)).get(variable);

		} else {
			return unitList.get(offering).get(variable);
		}
	}

	public String[] getSensorNames(String offering) {
		try {
			getStringArray(makeSqlRequest(getTableFields_CMD(offering)));
		} catch (SQLException e) {
			_log.error("POSTGRES READER:"+"Could not get sensornames/variables:"+e.getMessage());
			e.printStackTrace();
		}
		return new String[]{};
	}

	public List<String> getSensorNames() {
		if (sensorList == null){
			this.sensorList = new ArrayList<String>(); 
			HashMap<String, String> list = unitList.get(requestedOfferingList.get(0).substring(1, requestedOfferingList.get(0).length()-5));
			sensorList.addAll(list.keySet());
		}
		return sensorList;
	}

	public DateTime getStartDateTime(String offering) {
		return new DateTime(startDateTime);
	}

	/**
	 * overall DT
	 * @return
	 */
	public DateTime getStartDateTime() {
		return (startDateTime);
	}

	public DateTime getEndDateTime(String offering) {
		return (endDateTime);
	}
	
	/**
	 * overall DT
	 * @return
	 */
	public DateTime getEndDateTime() {
		return new DateTime(endDateTime);
	}

	public Interval getDateTimeRange(String offering) {
		return new Interval(startDateTime,endDateTime);
	}

	public HashMap<Integer, Interval> getDateTimeRanges() {
		HashMap<Integer, Interval> hash = new HashMap<Integer, Interval>();
		hash.put(0, new Interval(startDateTime,endDateTime));
		return hash;
	}

	public HashMap<Integer, String> getStationNames() {
		HashMap<Integer, String> stationHash = new HashMap<Integer, String>();
		_log.info("Requested Offerings"+requestedOfferings);
		_log.info("Requested Offerings"+requestedOfferings.length);
		_log.info("Requested Offerings"+requestedOfferings[0]);
		for (int i = 0; i < requestedOfferings.length; i++) {
			stationHash.put(i, requestedOfferings[i]);
		}
		return stationHash;
	}

	public String getReferenceAuthority() {
		return "ooi";
	}

	public double getStationLat(int stationIndex) {
		return latLonRects.get(stationIndex).getLatMin();
	}

	public double getStationLon(int stationIndex) {
		return latLonRects.get(stationIndex).getLonMin();
	}

	public String[] getCRS_SRS_authorities() {
		String[] data = { "ooi", "data", "product" };
		return data;
	}

	public ArrayList<String> getCoordinateNames() {
		ArrayList<String> data = new ArrayList<String>();
		data.add("time");
		return data;
	}

	public HashMap<String, Object> getGlobalAttributes() {
		HashMap<String, Object> global = new HashMap<String, Object>();
		global.put("title", "ooi title");
		global.put("summary", "OOI data product");
		global.put("access_constraints", "");
		global.put("publisher_name", "ooi");
		global.put("publisher_email","ooi");
		global.put("keywords", "ooi_data_product");
		global.put("publisher_phone", "");
		global.put("publisher_url", "");
		return global;
	}
	
	/**
	 * GET OBSERVATION
	 */

	public boolean isVariableAvailable(String offering,String variableRequested){
		boolean ret = false;
		
		if(getSensorNames().contains(variableRequested)){
			ret = true;
		}
		return ret;
	}
	
	public String getDatasetFeatureType(){
		return IDataProduct.STATION;
	}
	
	public String getVariableStandardName(String variable){
		return variable;
	}

	public Object getFeatureTypeDataSet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getFillValue(String obsProp){
		return "";
	}
	
	public boolean hasFillValue(String obsProb){
		return false;
	}
	
	public iStationData getStationData(){
		//closeFile(null);
		postgresStationData dset = new postgresStationData();
		dset.setConnectionParams(DBSERVER,DBPORT,DBNAME,DBUSERNAME,DBPASS,CONNECTION_PASSED,TITLE,SessionStartup);
		return dset;
	}
}
