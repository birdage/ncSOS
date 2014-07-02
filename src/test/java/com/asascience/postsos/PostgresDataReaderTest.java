package com.asascience.postsos;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import com.asascience.sos.dataproducts.LatLonRect;
import com.asascience.sos.dataproducts.PostgresDataReader;
import com.asascience.sos.dataproducts.postgresStationData;

public class PostgresDataReaderTest {

	/**
	 * GET CAPS!
	 */
	
	@Test
	public void test_DR_CheckTableExists() throws SQLException {
		PostgresDataReader dr = new PostgresDataReader();
		dr.setup();
		//uses a known tble that should exist
		assertTrue(dr.getBooleanValResultsSet(dr.makeSqlRequest(dr.doesTableExist_CMD("geometry_columns"))));
	}
	
	@Test
	public void test_DR_GetAvailableFields() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		dr.setup();
		//gets the fields of an offering
		String[] fields =  {"f_table_catalog",
				 			"f_table_schema",
				 			"f_table_name",
				 			"f_geometry_column",
				 			"coord_dimension",
				 			"srid",
				 			"type"};
		
		String[] vals = dr.getStringArray(dr.makeSqlRequest(dr.getTableFields_CMD("geometry_columns")));
		
		//check fields, one by one
		for (int i = 0; i < fields.length; i++) {
			assertEquals(fields[i],vals[i]);
		}
	}
	
	
	@Test
	public void test_DR_GetLatLonFields() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		dr.setup();
		String table = grabFDT(dr);
		if  (table != null){
			double[] lats = dr.getDoubleArray(dr.makeSqlRequest(dr.getLatField_CMD(table)));
			double[] lons = dr.getDoubleArray(dr.makeSqlRequest(dr.getLonField_CMD(table)));
			System.out.println(lats[0]);
			System.out.println(lons[0]);
			assertTrue(lats.length>1);
			assertTrue(lons.length>1);			
		}else{
			fail("could not get data table!?!?");
		}
		
	}	
	
	@Test
	public void test_DR_GetLatLonRect() throws Exception {
		
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		
		HashMap<Integer, LatLonRect> a = dr.getLatLonRects();
		
		//is there data
		assertTrue(a!=null);
		assertTrue(a.size()>0);
		//is it valid
		assertTrue(a.get(0)!=null);
		//is there only one set of data for a request
		assertTrue(a.size()==1);
		//what does the data look like
		LatLonRect rect = a.get(0);
		
		double[] lats = dr.getDoubleArray(dr.makeSqlRequest(dr.getLatField_CMD(table)));
		double[] lons = dr.getDoubleArray(dr.makeSqlRequest(dr.getLonField_CMD(table)));
		//----------
		Arrays.sort(lats);
		Arrays.sort(lons);
		//----------
		double lats_min = lats[0];
		double lats_max = lats[lats.length-1];
		//----------
		double lons_min = lons[0];
		double lons_max = lons[lons.length-1];
		//check the vals
		assertFalse(Double.isNaN(lons_max));
		assertFalse(Double.isNaN(lons_min));
		assertFalse(Double.isNaN(lats_max));
		assertFalse(Double.isNaN(lats_min));
	}
	
	/**
	 * lat lon bounds for the entire request
	 * @throws Exception
	 */
	@Test
	public void test_DR_GetMin_Max_LatLonBounds() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		dr.setup();
		assertNotNull(dr.getLatLonBounds());
		//check bounds
		assertTrue(Double.isNaN(dr.getLatLonBounds().getBotLeft_lat()));
		assertTrue(Double.isNaN(dr.getLatLonBounds().getBotLeft_lon()));
		assertTrue(Double.isNaN(dr.getLatLonBounds().getTopLeft_lat()));
		assertTrue(Double.isNaN(dr.getLatLonBounds().getTopLeft_lon()));
		
	}
	
	
	public String grabFDT(PostgresDataReader dr) throws SQLException{
		String[] views = dr.getStringArray(dr.makeSqlRequest(dr.getAllFDTViews_CMD()));
		//only perform the following if there are views we can use
		if (views.length>0){
			return views[0];
		}
		return null;
	}
	
	@Test
	public void test_DR_TemporalExtraction() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		assertNotNull(dr.getStartDateTime());
		assertNotNull(dr.getEndDateTime());
		assertNotNull(dr.getDateTimeRange(table));
		assertNotNull(dr.getDateTimeRanges());
	}
	
	@Test
	public void test_DR_getUnits() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		assertNotNull(dr.getUnitsOfVariable(table.substring(1, table.length()-5),"density"));
	}
	
	@Test
	public void test_HTTP_POST() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		dr.queryResourceRegistry_Params(table.substring(1, table.length()-5));
		assertTrue(dr.unitList!=null);
		assertTrue(dr.unitList.size()==1);
	}
	
	@Test
	public void test_HTTP_POST_EXTENTS() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		String val =dr.queryResoureResistry(table.substring(1, table.length()-5), "resource_registry" , "http://localhost:5000/ion-service/resource_registry/find_resources", "find_resources" ,"'object_id': '"+table.substring(1, table.length()-5)+"', \"restype\": \"DataProduct\"");
		assertNotNull(val);
	}
	
	@Test
	public void testName() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		dr.queryResourceResistry_Extents(table.substring(1, table.length()-5));
	}
	
	@Test
	public void test_DR_sensorNames() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		assertNotNull(dr.getSensorNames());
		assertTrue(dr.getSensorNames().size()>1);
	}
	
	/**
	 * GET OBS!
	 */
	
	@Test
	public void test_DR_variableAvailable() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		//checks that something requested is available
		assertTrue(dr.isVariableAvailable(table, "temp"));
		assertFalse(dr.isVariableAvailable(table, "wiggle"));
	}
	
	@Test
	public void test_SR_getData() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		
		postgresStationData st = new postgresStationData();
		String[] variables = {"temp"};
		st.setParms(table, null, variables);
		String ret = st.createDataString(0);
		System.out.println(ret);
		assertNotNull(ret);
	}	

	@Test
	public void test_SR_getDataFromDB_noEventTime() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		postgresStationData st = (postgresStationData) dr.getStationData();
		String[] variables = {"time","temp","density"};
		st.setParms("_9de0c6acec074ab0bdf706ed1f99f6df_view", null, variables);
		String ret = st.createDataString(0);
		System.out.println(ret);
		assertNotNull(ret);
		assertTrue(ret.startsWith("2011-02-11T01:01:01.000Z"));
	}	
	
	@Test
	public void test_SR_getDataFromDB_SingleEventTime() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		postgresStationData st = (postgresStationData) dr.getStationData();
		String[] variables = {"time","temp","density"};
		String startDate = "2011-02-11T04:01:01.000Z";
		st.setParms(table, new String[]{startDate}, variables);
		String ret = st.createDataString(0);
		System.out.println(ret);
		assertNotNull(ret);
		String[] splitString = ret.split(" ");
		assertEquals(1,splitString.length,0);
		assertTrue(ret.startsWith("2011-02-11T04:01:01.000Z"));
	}	
	
	@Test
	public void test_SR_getDataFromDB_SingleEventTime2() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		postgresStationData st = (postgresStationData) dr.getStationData();
		String[] variables = {"time","temp","density"};
		String startDate = "2011-02-11T12:01:01.000Z";
		st.setParms(table, new String[]{startDate}, variables);
		String ret = st.createDataString(0);
		System.out.println(ret);
		assertNotNull(ret);
		String[] splitString = ret.split(" ");
		assertEquals(1,splitString.length,0);
		assertTrue(ret.startsWith("2011-02-11T12:01:01.000Z"));
	}	
	
	@Test
	public void test_SR_getDataFromDB_EventTimeRange() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.setOfferings(table);
		dr.setup();
		postgresStationData st = (postgresStationData) dr.getStationData();
		String[] variables = {"time","temp","density"};
		String startDate = "2011-02-11T04:01:01.000Z";
		String endDate = "2011-02-11T12:01:01.000Z";
		
		st.setParms(table, new String[]{startDate,endDate}, variables);
		
		String ret = st.createDataString(0);
		System.out.println(ret);
		assertNotNull(ret);
		String[] splitString = ret.split(" ");
		assertEquals(9,splitString.length,0);
		assertTrue(ret.startsWith("2011-02-11T12:01:01.000Z"));
	}	
	
	
}
