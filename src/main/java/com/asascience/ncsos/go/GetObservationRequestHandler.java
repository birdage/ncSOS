package com.asascience.ncsos.go;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.asascience.ncsos.cdmclasses.iStationData;
import com.asascience.ncsos.outputformatter.ErrorFormatter;
import com.asascience.ncsos.outputformatter.go.OosTethysFormatter;
import com.asascience.ncsos.service.BaseRequestHandler;
import com.asascience.ncsos.util.ListComprehension;
import com.asascience.sos.dataproducts.IDataProduct;

public class GetObservationRequestHandler extends BaseRequestHandler {
    public static final String DEPTH = "depth";
    public static final String STANDARD_NAME = "standard_name";
    private static final String LAT = "latitude";
    private static final String LON = "longitude";

    public static final String TEXTXML = "text/xml";
    public static final String UNKNOWN = "unknown";
    private String[] obsProperties;
    private String[] procedures;
    private iStationData CDMDataSet;
    private org.slf4j.Logger _log = org.slf4j.LoggerFactory.getLogger(GetObservationRequestHandler.class);
    private static final String FILL_VALUE_NAME = "_FillValue";
    public static final String IOOS10_RESPONSE_FORMAT = "text/xml;subtype=\"om/1.0.0/profiles/ioos_sos/1.0\"";
    public static final String OOSTETHYS_RESPONSE_FORMAT = "text/xml;subtype=\"om/1.0.0\"";
    private final List<String> eventTimes;

    /**
     * SOS get obs request handler
     * @param netCDFDataset dataset for which the get observation request is being made
     * @param requestedStationNames collection of offerings from the request
     * @param variableNames collection of observed properties from the request
     * @param eventTime event time range from the request
     * @param responseFormat response format from the request
     * @param latLonRequest map of the latitudes and longitude (points or ranges) from the request
     * @throws IOException 
     */
    public GetObservationRequestHandler(IDataProduct dataset,
                                        String[] requestedProcedures,
                                        String offering,
                                        String[] variableNames,
                                        String[] eventTime,
                                        String responseFormat,
                                        Map<String, String> latLonRequest) throws IOException {
    	
        super(dataset);
        _log.warn("SOS GETOBSERVATION HANDLER");

        // Translate back to an URN.  (gml:id fields in XML can't have colons)
        offering = offering.replace("_-_",":");
        offering = URLDecoder.decode(offering,"UTF-8");
        responseFormat = URLDecoder.decode(responseFormat,"UTF-8");
        if (eventTime != null && eventTime.length > 0) {
            eventTimes = Arrays.asList(eventTime);
        } else {
            eventTimes = new ArrayList<String>();
        }

        // set up our formatter
        if (responseFormat.equalsIgnoreCase(OOSTETHYS_RESPONSE_FORMAT)) {
            formatter = new OosTethysFormatter(this);
        }else if (responseFormat.equalsIgnoreCase("text/xml")) {
        	 formatter = new OosTethysFormatter(this);
        }
        else {
            formatter = new ErrorFormatter();
            ((ErrorFormatter)formatter).setException("Could not recognize response format: " + responseFormat, INVALID_PARAMETER, "responseFormat");
        }

        // Since the obsevedProperties can be standard_name attributes, map everything to an actual variable name here.
        String[] actualVariableNames = variableNames.clone();

        // make sure that all of the requested variable names are in the dataset
        for (int i = 0 ; i < variableNames.length ; i++) {
            String vars = variableNames[i];
            boolean isInDataset = false;
            isInDataset = dataset.isVariableAvailable(offering, vars);
            if (!isInDataset) {
                formatter = new ErrorFormatter();
                ((ErrorFormatter)formatter).setException("observed property - " + vars + " - was not found in the dataset", INVALID_PARAMETER, "observedProperty");
                CDMDataSet = null;
                return;
            }
        }

        //CoordinateAxis heightAxis = dataset.findCoordinateAxis("AxisType.Height");

        //this.obsProperties = checkNetcdfFileForAxis(heightAxis, actualVariableNames);

        // Figure out what procedures to use...
        try {
            if (requestedProcedures == null) {
                if (offering.equalsIgnoreCase(this.getUrnNetworkAll())) {
                    // All procedures
                    requestedProcedures = getStationNames().values().toArray(new String[getStationNames().values().size()]);
                } else {
                    // Just the single procedure supplied by the offering
                    requestedProcedures = new String[1];
                    requestedProcedures[0] = offering;
                }
            } else {
                if (requestedProcedures.length == 1 && requestedProcedures[0].equalsIgnoreCase(getUrnNetworkAll())) {
                    requestedProcedures = getStationNames().values().toArray(new String[getStationNames().values().size()]);
                } else {
                    for (int i = 0; i < requestedProcedures.length; i++) {
                        requestedProcedures[i] = requestedProcedures[i].substring(requestedProcedures[i].lastIndexOf(":") + 1);
                    }
                }
            }
            // Now map them all to the station URN
            List<String> naProcs = ListComprehension.map(Arrays.asList(requestedProcedures),
                 new ListComprehension.Func<String, String>() {
                     public String apply(String in) {
                         return getUrnName(in);
                     }
                 }
            );
            this.procedures = naProcs.toArray(new String[naProcs.size()]);
        } catch (Exception ex) {
            _log.error(ex.toString());
            this.procedures = null;
        }

        // check that the procedures are valid
        checkProcedureValidity();
        // and are a part of the offering
        if (offering != null) {
            checkProceduresAgainstOffering(offering);
        }
        //MAYBE?
        obsProperties = actualVariableNames;
        //obsProperties = dataset.getSensorNames(offering);
        setCDMDatasetForStations(dataset, offering,eventTime, latLonRequest,variableNames);
    }

    private void setCDMDatasetForStations(IDataProduct dataset, String offering, String[] eventTime, Map<String, String> latLonRequest,String[] requestedVariables) throws IOException {
        // strip out text if the station is defined by indices
        //grid operation
            if (dataset.getStationData()!=null){
            	CDMDataSet = dataset.getStationData();
            	CDMDataSet.setNumberOfStations(1);
            }else {
                formatter = new ErrorFormatter();
                ((ErrorFormatter)formatter).setException("NetCDF-Java could not recognize the dataset's FeatureType");
                CDMDataSet = null;
                return;
            }
            
            
            //only set the data is it is valid
            //dont use.....
            CDMDataSet.setData(dataset.getFeatureTypeDataSet());
            //sets the param information
            CDMDataSet.setParms(offering,eventTime,requestedVariables);
        
    }

     /**
     * Create the observation data for go, passing it to our formatter
     */
    public void parseObservations() {
    	if(CDMDataSet != null){
    		for (int s = 0; s < CDMDataSet.getNumberOfStations(); s++) {
    			String dataString = CDMDataSet.getDataResponse(s);
    			for (String dataPoint : dataString.split(";")) {
    				if (!dataPoint.equals("")) {
    					formatter.addDataFormattedStringToInfoList(dataPoint);
    				}
    			}
    		}
    	}
    }

    /**
     * Returns the 'standard_name' attribute of a variable, if it exists
     * @param varName the name of the variable
     * @return the 'standard_name' if it exists, otherwise ""
     */
    public String getVariableStandardName(String varName) {
        String retval = UNKNOWN;
       dataset.getVariableStandardName(varName);
        return retval;
    }

    public List<String> getRequestedEventTimes() {
        return this.eventTimes;
    }

    /**
     * Gets the dataset wrapped by the cdm feature type giving multiple easy to 
     * access functions
     * @return dataset wrapped by iStationData
     */
    public iStationData getCDMDataset() {
        return CDMDataSet;
    }

    //<editor-fold defaultstate="collapsed" desc="Helper functions for building GetObs XML">
    /**
     * Looks up a stations index by a string name
     * @param stName the name of the station to look for
     * @return the index of the station (-1 if it does not exist)
     */
    public int getIndexFromStationName(String stName) {
        return getStationIndex(stName);
    }

    public String getStationLowerCorner(int relIndex) {
        return formatDegree(CDMDataSet.getLowerLat(relIndex)) + " " + formatDegree(CDMDataSet.getLowerLon(relIndex));
    }

    public String getStationUpperCorner(int relIndex) {
        return formatDegree(CDMDataSet.getUpperLat(relIndex)) + " " + formatDegree(CDMDataSet.getUpperLon(relIndex));
    }

    public String getBoundedLowerCorner() {
        return formatDegree(CDMDataSet.getBoundLowerLat()) + " " + formatDegree(CDMDataSet.getBoundLowerLon());
    }

    public String getBoundedUpperCorner() {
        return formatDegree(CDMDataSet.getBoundUpperLat()) + " " + formatDegree(CDMDataSet.getBoundUpperLon());
    }

    public String getStartTime(int relIndex) {
        return CDMDataSet.getTimeBegin(relIndex);
    }

    public String getEndTime(int relIndex) {
        return CDMDataSet.getTimeEnd(relIndex);
    }

    public List<String> getRequestedObservedProperties() {
        /*
        CoordinateAxis heightAxis = netCDFDataset.findCoordinateAxis(AxisType.Height);

        List<String> retval = Arrays.asList(obsProperties);

        if (heightAxis != null) {
            retval = ListComprehension.filterOut(retval, heightAxis.getShortName());
        }
		*/
    	
        //return retval;
    	return null;
    }

    public String[] getObservedProperties() {
        return obsProperties;
    }

    public String[] getProcedures() {
        return procedures;
    }

    public String getUnitsString(String dataVarName) {
        return getUnitsOfVariable(dataVarName);
    }

    public String getValueBlockForAllObs(String block, String decimal, String token, int relIndex) {
        _log.info("Getting data for index: " + relIndex);
        String retval = CDMDataSet.getDataResponse(relIndex);
        return retval.replaceAll("\\.", decimal).replaceAll(",", token).replaceAll(";", block);
    }
    //</editor-fold>

    public String getFillValue(String obsProp) {
       return dataset.getFillValue(obsProp);
    }

    public boolean hasFillValue(String obsProp) {
        return dataset.hasFillValue(obsProp);

    }

    private void checkProcedureValidity() throws IOException {
        List<String> stProc = new ArrayList<String>();
        stProc.add(this.getUrnNetworkAll());
        for (String stname : this.getStationNames().values()) {
            for (String senname : this.getSensorNames()) {
                stProc.add(this.getSensorUrnName(stname, senname));
            }
            stProc.add(this.getUrnName(stname));
        }

        for (String proc : this.procedures) {
            if (ListComprehension.filter(stProc, proc).size() < 1) {
                formatter = new ErrorFormatter();
                ((ErrorFormatter)formatter).setException("Invalid procedure " + proc + ". Check GetCapabilities document for valid procedures.", INVALID_PARAMETER, "procedure");
            }
        }

    }

    private void checkProceduresAgainstOffering(String offering) throws IOException {
        // if the offering is 'network-all' no error (network-all should have all procedures)
        if (offering.equalsIgnoreCase(this.getUrnNetworkAll())) {
            return;
        }
        // currently in ncSOS the only offerings that exist are network-all and each of the stations
        // in the dataset. So basically we need to check that the offering exists
        // in each of the procedures requested.
        for (String proc : this.procedures) {
            if (!proc.toLowerCase().contains(offering.toLowerCase())) {
                formatter = new ErrorFormatter();
                ((ErrorFormatter)formatter).setException("Offering: " + proc + " does not exist in the dataset.  Check GetCapabilities document for valid offerings.", INVALID_PARAMETER, "offering");
            }
        }

    }
}
