package com.asascience.postsos;

import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

import com.asascience.ncsos.outputformatter.OutputFormatter;
import com.asascience.ncsos.service.Parser;
import com.asascience.ncsos.util.XMLDomUtils;
import com.asascience.sos.dataproducts.IDataProduct;
import com.asascience.sos.dataproducts.PostgresDataReader;
import com.asascience.sos.dataproducts.SampleDataReader;

public class GC_Test {

	private static HashMap<String, String> kvp = new HashMap<String, String>();

	protected static String baseOutputDir = null;
	protected static String baseExampleDir = null;
	protected static List<Element> fileElements;

	public static void setUpClass() throws Exception {

		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();

		try {
			File configFile = new File("resources/tests_config.xml");
			InputStream templateInputStream = new FileInputStream(configFile);
			Document configDoc = XMLDomUtils
					.getTemplateDom(templateInputStream);
			// read from the config file
			baseOutputDir = configDoc.getRootElement()
					.getChild("outputDirectory").getValue();
			baseExampleDir = configDoc.getRootElement()
					.getChild("examplesDirectory").getValue();

			Element testFilesElement = XMLDomUtils.getNestedChild(
					configDoc.getRootElement(), "TestFiles");
			fileElements = testFilesElement.getChildren();

			templateInputStream.close();
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	@Test
	public void testGenerateSimpleGETCAPSRequest() throws IOException, SQLException {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.closeFile();
		
		String tempdir = System.getProperty("java.io.tmpdir");

		Parser md = new Parser();
		IDataProduct dataset = new SampleDataReader();

		String request = "request=getCapabilities&service=sos&version=1.0.0&offering="+table;

		HashMap<String, Object> respMap = md.enhanceGETRequest(dataset,
				request, "eoi-dev1.oceanobservatories.org" + "?".toString(),
				tempdir);
		
		OutputFormatter output = (OutputFormatter) respMap.get("outputFormatter");
		Writer writer = new CharArrayWriter();
		output.writeOutput(writer);
		 // Write to disk
        System.out.println("------ Saving output: " + output +" ------");
        fileWriter("/Users/rpsdev/Documents/workspace/postSOS/examples/post_Get_caps.xml", writer,false);
		
	}
	
	@Test
	public void testGenerateSimpleGETCAPSRequest_fails() throws IOException, SQLException {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.closeFile();
		
		String tempdir = System.getProperty("java.io.tmpdir");

		Parser md = new Parser();
		IDataProduct dataset = new SampleDataReader();

		String request = "request=getCapabilities&service=sos";

		HashMap<String, Object> respMap = md.enhanceGETRequest(dataset,
				request, "eoi-dev1.oceanobservatories.org" + "?".toString(),
				tempdir);
		
		OutputFormatter output = (OutputFormatter) respMap.get("outputFormatter");
		Writer writer = new CharArrayWriter();
		output.writeOutput(writer);
		 // Write to disk
        System.out.println("------ Saving output: " + output +" ------");
        fileWriter("/Users/rpsdev/Documents/workspace/postSOS/examples/post_Get_caps_fails.xml", writer,false);
		
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
	public void testGenerateSimpleGETOBSRequest() throws IOException, SQLException {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.closeFile();
		
		
		String tempdir = System.getProperty("java.io.tmpdir");

		Parser md = new Parser();
		IDataProduct dataset = new PostgresDataReader();

		String request = "request=getObservation&service=sos&version=1.0.0&offering="+table+"&observedproperty=time,temp,density&responseformat=text/xml";

		HashMap<String, Object> respMap = md.enhanceGETRequest(dataset,
				request, "http://localhost:8080/geoserver/ows/" + "?".toString(),
				tempdir);
		
		OutputFormatter output = (OutputFormatter) respMap.get("outputFormatter");
		Writer writer = new CharArrayWriter();
		output.writeOutput(writer);
		 // Write to disk
        System.out.println("------ Saving output: " + output +" ------");
        fileWriter("/Users/rpsdev/Documents/workspace/postSOS/examples/post_Get_Obs.xml", writer,false);
		
	}
	
	public static void fileWriter(String filePath, Writer writer, boolean append) throws IOException {
        File file = new File(filePath);
        Writer output = new BufferedWriter(new FileWriter(file, append));
        output.write(writer.toString());
        output.close();
    }
	
	@Test
	public void test_setupJDBCDAtaProduct() {
		IDataProduct dataset = new PostgresDataReader();
		dataset.setup();
	}	
	
	@Test
	public void test_postgresExample() throws Exception {
		PostgresDataReader dr = new PostgresDataReader();
		String table = grabFDT(dr);
		dr.closeFile();
		
		String tempdir = System.getProperty("java.io.tmpdir");

		Parser md = new Parser();
		IDataProduct dataset = new PostgresDataReader();
		String request = "request=getCapabilities&service=sos&version=1.0.0&offering="+table+"";

		HashMap<String, Object> respMap = md.enhanceGETRequest(dataset,
				request, "eoi-dev1.oceanobservatories.org" + "?".toString(),
				tempdir);
		
		OutputFormatter output = (OutputFormatter) respMap.get("outputFormatter");
		Writer writer = new CharArrayWriter();
		output.writeOutput(writer);
		 // Write to disk
        System.out.println("------ Saving output: " + output +" ------");
        fileWriter("/Users/rpsdev/Documents/workspace/postSOS/examples/post_Get_caps.xml", writer,false);
		int a =1;
	}

}
