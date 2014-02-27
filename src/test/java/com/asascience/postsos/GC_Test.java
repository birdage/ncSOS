package com.asascience.postsos;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.junit.Test;

import java.net.URLEncoder;

public class GC_Test {

	private static HashMap<String,String> kvp = new HashMap<String, String>();
	
	
	@Test
	public void testGenerateSimpleRequest() throws UnsupportedEncodingException {
		 kvp.put("responseFormat",   URLEncoder.encode("text/xml;subtype=\"om/1.0.0/profiles/ioos_sos/1.0\"", "UTF-8"));
	        kvp.put("request",          "GetCapabilities");
	        kvp.put("version",          "1.0.0");
	        kvp.put("service",          "SOS");
	        //kvp.put("procedure",        currentFile.getChild("platform").getAttributeValue("id"));
	        kvp.put("offering",         "urn:ioos:network:ncsos:all");
	        //kvp.put("observedProperty", currentFile.getChild("platform").getChild("sensor").getAttributeValue("standard"));
	}

}
