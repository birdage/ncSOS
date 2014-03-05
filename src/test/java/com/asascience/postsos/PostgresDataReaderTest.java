package com.asascience.postsos;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import com.asascience.sos.dataproducts.HttpURLConnectionExample;
import com.asascience.sos.dataproducts.LatLonRect;
import com.asascience.sos.dataproducts.PostgresDataReader;

public class PostgresDataReaderTest {

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
	public void test_DR_GetAvaiableUnits() throws Exception {
		fail("need to add units to something!");
	}
	
	@Test
	public void test_DR_GetStartAndEndDate() throws Exception {
		fail("need to add start and end dates");
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
		//is it valiud
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
		
		assertEquals(lats_max, rect.getLatMax(),0);
		assertEquals(lons_max, rect.getLonMax(),0);
		assertEquals(lats_min, rect.getLatMin(),0);
		assertEquals(lons_min, rect.getLonMin(),0);
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
		fail("not done");
		PostgresDataReader dr = new PostgresDataReader();
		dr.setup();
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
		dr.queryResourceRegistry(table.substring(1, table.length()-5));
		assertTrue(dr.unitList!=null);
		assertTrue(dr.unitList.size()==1);
	}
}
