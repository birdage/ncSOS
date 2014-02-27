package com.asascience.ncsos.service;

import com.asascience.ncsos.outputformatter.OutputFormatter;
import com.asascience.ncsos.util.DiscreteSamplingGeometryUtil;
import com.asascience.ncsos.util.ListComprehension;
import com.asascience.sos.dataproducts.IDataProduct;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public abstract class BaseRequestHandler {
	public static final String CF_ROLE = "cf_role";
	public static final String GRID = "grid";
	public static final String GRID_MAPPING = "grid_mapping";
	public static final String NAME = "name";
	public static final String PROFILE = "profile";
	public static final String TRAJECTORY = "trajectory";
	public static final String PROFILE_ID = "profile_id";
	public static final String TRAJECTORY_ID = "trajectory_id";

	public static final String STATION_URN_BASE = "urn:ioos:station:";
	public static final String SENSOR_URN_BASE = "urn:ioos:sensor:";
	public static final String NETWORK_URN_BASE = "urn:ioos:network:";
	public static final String DEFAULT_NAMING_AUTHORITY = "ncsos";
	private static final NumberFormat FORMAT_DEGREE;
	// list of keywords to filter variables on to remove non-data variables from
	// the list
	private static final String[] NON_DATAVAR_NAMES = { "rowsize", "row_size",
			PROFILE, "info", "time", "z", "alt", "height", "station_info" };
	// private FeatureDataset featureDataset;
	// private FeatureCollection CDMPointFeatureCollection;
	// private GridDataset gridDataSet = null;

	// Global Attributes
	protected HashMap<String, Object> global_attributes = new HashMap<String, Object>();

	// Variables and other information commonly needed
	protected final IDataProduct dataset;
	// protected Variable latVariable, lonVariable, timeVariable, depthVariable;
	// protected Variable stationVariable;
	private HashMap<Integer, String> stationNames;
	private List<String> sensorNames;

	// Exception codes - Table 25 of OGC 06-121r3 (OWS Common)
	protected static String INVALID_PARAMETER = "InvalidParameterValue";
	protected static String MISSING_PARAMETER = "MissingParameterValue";
	protected static String OPTION_NOT_SUPPORTED = "OptionNotSupported";
	protected static String OPERATION_NOT_SUPPORTED = "OperationNotSupported";

	private org.slf4j.Logger _log = org.slf4j.LoggerFactory
			.getLogger(BaseRequestHandler.class);

	static {
		FORMAT_DEGREE = NumberFormat.getNumberInstance();
		FORMAT_DEGREE.setMinimumFractionDigits(1);
		FORMAT_DEGREE.setMaximumFractionDigits(14);
	}

	// private FeatureType dataFeatureType;
	protected OutputFormatter formatter;

	/**
	 * Takes in a dataset and wraps it based on its feature type.
	 * 
	 * @param netCDFDataset
	 *            the dataset being acted on
	 * @throws IOException
	 */
	public BaseRequestHandler(IDataProduct dataset) throws IOException {
		// check for non-null dataset
		if (dataset == null) {
			// _log.error("received null dataset -- probably exception output");
			this.dataset = null;
			return;
		}
		this.dataset = dataset;

		getPlatformNames();
		// find the global attributes
		parseGlobalAttributes();
		// get the station variable and several other bits needed
		findAndParseStationVariable();
		// get sensor Variable names
		parseSensorNames();
		// get Axis vars (location, time, depth)
		/*
		latVariable = dataset.findCoordinateAxis("Lat");
		lonVariable = dataset.findCoordinateAxis("Lon");
		timeVariable = dataset.findCoordinateAxis("Time");
		depthVariable = dataset.findCoordinateAxis("Height");
		*/
	}

	public OutputFormatter getOutputFormatter() {
        return formatter;
    }
	
	/**
     * Finds commonly used global attributes in the netcdf file.
     */
    private void parseGlobalAttributes() {
        
        this.global_attributes = dataset.getGlobalAttributes();
        // Fill in required naming authority attribute
        if (!this.global_attributes.containsKey("naming_authority") ) {
            this.global_attributes.put("naming_authority", DEFAULT_NAMING_AUTHORITY);
        }

        if (this.global_attributes.get("naming_authority").equals("")) {
            this.global_attributes.put("naming_authority", DEFAULT_NAMING_AUTHORITY);
        }
        // Fill in required naming authority attribute
        if (!this.global_attributes.containsKey("featureType")) {
            this.global_attributes.put("featureType", "UNKNOWN");
        }
    }

	/**
	 * Finds the station variable with several approaches 1) Attempts to find a
	 * variable with the attribute "cf_role"; only the station defining variable
	 * should have this attribute 2) Looks for a variable with 'grid' and 'name'
	 * in its name; GRID datasets do not have a "cf_role" attribute 3) Failing
	 * above, will
	 * 
	 * @throws IOException
	 */
	private void findAndParseStationVariable() throws IOException {

	}

	/**
	 * Finds all variables that are sensor (data) variables and compiles a list
	 * of their names.
	 */
	private void parseSensorNames() {
		this.sensorNames = dataset.getSensorNames();
	}

	private void getPlatformNames() {
		this.stationNames = dataset.getStationNames();
	}

	/**
	 * Attempts to find the coordinate reference authority
	 * 
	 * @param varName
	 *            var name to check for crs authority
	 * @return the authority name, if there is one
	 */
	private String getAuthorityFromVariable(String varName) {
		String retval = null;

		dataset.getReferenceAuthority();

		return retval;
	}

	/**
	 * Returns the index of the station name
	 * 
	 * @param stationToLookFor
	 *            the name of the station to find
	 * @return the index of the station; -1 if no station with the name exists
	 */
	protected int getStationIndex(String stationToLookFor) {
		try {
			if (stationToLookFor == null)
				throw new Exception("Looking for null station");
			// look for the station in the hashmap and return its index
			int retval = -1;
			if (stationNames != null
					&& stationNames.containsValue(stationToLookFor)) {
				int index = 0;
				for (String stationName : stationNames.values()) {
					if (stationToLookFor.equalsIgnoreCase(stationName)) {
						retval = index;
						break;
					}
					index++;
				}
			}

			return retval;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return -1;
	}

	/**
	 * Get the station names, parsed from a Variable containing "station" and
	 * "name"
	 * 
	 * @return list of station names
	 */
	protected HashMap<Integer, String> getStationNames() {
		return this.stationNames;
	}

	/**
	 * Return the list of sensor names
	 * 
	 * @return string list of sensor names
	 */
	protected List<String> getSensorNames() {
		return this.sensorNames;
	}

	/**
	 * Return the list of sensor names
	 * 
	 * @return string list of sensor names
	 */
	protected List<String> getSensorUrns(String stationName) {
		List<String> urnNames = new ArrayList<String>(this.sensorNames.size());
		for (String s : this.sensorNames) {
			urnNames.add(this.getSensorUrnName(stationName, s));
		}
		return urnNames;

	}

	/**
	 * 
	 * @param stationIndex
	 * @return
	 */
	protected final double[] getStationCoords(int stationIndex) {
		try {
			// get the lat/lon of the station
			if (stationIndex >= 0) {
				double[] coords = new double[] { Double.NaN, Double.NaN };

				// find lat/lon values for the station
				coords[0] = dataset.getStationLat(stationIndex);
				coords[1] = dataset.getStationLon(stationIndex);

				return coords;
			} else {
				return null;
			}
		} catch (Exception e) {
			_log.error("exception in getStationCoords " + e.getMessage());
			return null;
		}
	}

	/**
	 * Gets the units string of a variable
	 * 
	 * @param varName
	 *            the name of the variable to look for
	 * @return the units string or "none" if the variable could not be found
	 */
	protected String getUnitsOfVariable(String varName) {
		return dataset.getUnitsOfVariable(null, varName);
	}

	protected String[] getAttributesOfVariable(String varName) {
		/*
		 * Variable var; if (featureDataset != null) { var = (Variable)
		 * featureDataset.getDataVariable(varName); } else { var =
		 * netCDFDataset.findVariable(varName); } if (var != null) { return
		 * var.getAttributes().toArray(new
		 * Attribute[var.getAttributes().size()]); }
		 */
		return null;
	}

	/**
	 * Attempts to find a variable in the dataset.
	 * 
	 * @param variableName
	 *            name of the variable
	 * @return either the variable if found or null
	 */
	public Object getVariableByName(String variableName) {
		return null;
		// return this.netCDFDataset.findVariable(variableName);
	}

	/**
	 * Returns the urn of a station
	 * 
	 * @param stationName
	 *            the station name to add to the name base
	 * @return
	 */
	public String getUrnName(String stationName) {
		String[] feature_name = stationName.split(":");
		if (feature_name.length > 1 && feature_name[0].equalsIgnoreCase("urn")) {
			// We already have a URN, so just return it.
			return stationName;
		} else {
			return STATION_URN_BASE
					+ this.global_attributes.get("naming_authority") + ":"
					+ stationName;
		}
	}

	public String getUrnNetworkAll() {
		// returns the network-all urn of the authority
		return NETWORK_URN_BASE
				+ this.global_attributes.get("naming_authority") + ":all";
	}

	/**
	 * Returns a composite string of the sensor urn
	 * 
	 * @param stationName
	 *            name of the station holding the sensor
	 * @param sensorName
	 *            name of the sensor
	 * @return urn of the station/sensor combo
	 */
	public String getSensorUrnName(String stationName, String sensorName) {
		String[] feature_name = stationName.split(":");
		if (feature_name.length > 1 && feature_name[0].equalsIgnoreCase("urn")) {
			// We have a station URN, so strip out the name
			stationName = feature_name[feature_name.length - 1];
		}
		return SENSOR_URN_BASE + this.global_attributes.get("naming_authority")
				+ ":" + stationName + ":" + sensorName;
	}

	/**
	 * Finds the CRS/SRS authorities used for the data vars. This method reads
	 * through variables in a highly inefficient manner, therefore if a method
	 * provided by the netcdf-java api is found that provides the same output it
	 * should be favored over this.
	 * 
	 * @return an array of crs/srs authorities if there are any; else null
	 */
	public String[] getCRSSRSAuthorities() {
		String[] returnList;
		returnList = dataset.getCRS_SRS_authorities();
		if (returnList.length > 0)
			return returnList;
		else
			return null;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getCoordinateNames() {
		return dataset.getCoordinateNames();
	}

	/**
	 * Formats degree, using a number formatter
	 * 
	 * @param degree
	 *            a number to format to a degree
	 * @return the number as a degree
	 */
	public static String formatDegree(double degree) {
		return FORMAT_DEGREE.format(degree);
	}

	public Object getGlobalAttribute(String key, Object fillvalue) {
		if (this.global_attributes.containsKey(key)) {
			return this.global_attributes.get(key);
		} else {
			return fillvalue;
		}
	}

	public Object getGlobalAttribute(String key) {
		if (this.global_attributes.containsKey(key)) {
			return this.global_attributes.get(key);
		} else {
			return null;
		}
	}

	/**
	 * Attempts to find an attribute from a given variable
	 * 
	 * @param variable
	 *            variable to look in for the attribute
	 * @param attributeName
	 *            attribute with value desired
	 * @param defaultValue
	 *            default value if attribute does not exist
	 * @return the string value of the attribute if exists otherwise
	 *         defaultValue
	 */
	/*
	public static String getValueFromVariableAttribute(
			VariableSimpleIF variable, String attributeName, String defaultValue) {
		Attribute attr = variable.findAttributeIgnoreCase(attributeName);
		if (attr != null) {
			return attr.getStringValue();
		}
		return defaultValue;
	}
	*/
	/**
	 * Get all of the data variables from the dataset. Removes any axis
	 * variables or variables that are not strictly measurements.
	 * 
	 * @return list of variable interfaces
	 */
	/*
	public List<VariableSimpleIF> getDataVariables() {
		List<VariableSimpleIF> retval = ListComprehension
				.map(this.featureDataset.getDataVariables(),
						new ListComprehension.Func<VariableSimpleIF, VariableSimpleIF>() {
							public VariableSimpleIF apply(VariableSimpleIF in) {
								// check for direct name comparisons
								for (String name : NON_DATAVAR_NAMES) {
									String sname = in.getShortName()
											.toLowerCase();
									if (sname.equalsIgnoreCase(name))
										return null;
								}
								return in;
							}
						});
		retval = ListComprehension.filterOut(retval, null);
		// get any ancillary variables from the current data variables
		List<String> ancillaryVariables = ListComprehension.map(retval,
				new ListComprehension.Func<VariableSimpleIF, String>() {
					public String apply(VariableSimpleIF in) {
						Attribute av = in
								.findAttributeIgnoreCase("ancillary_variables");
						if (av != null)
							return av.getStringValue();
						return null;
					}
				});
		final List<String> ancillaryVariablesF = ListComprehension.filterOut(
				ancillaryVariables, null);
		// remove any ancillary variables from the current retval list
		retval = ListComprehension
				.map(retval,
						new ListComprehension.Func<VariableSimpleIF, VariableSimpleIF>() {
							public VariableSimpleIF apply(VariableSimpleIF in) {
								List<Boolean> add = ListComprehension
										.map(ancillaryVariablesF,
												in,
												new ListComprehension.Func2P<String, VariableSimpleIF, Boolean>() {
													public Boolean apply(
															String sin,
															VariableSimpleIF vin) {
														if (sin.equals(vin
																.getShortName()))
															return false;
														return true;
													}
												});
								// filter out all of the 'trues' in the list, if
								// there are any 'falses' left, then the
								// variable should not be in the final list
								add = ListComprehension.filterOut(add, true);
								if (add.size() > 0)
									return null;

								return in;
							}
						});
		return ListComprehension.filterOut(retval, null);
	}
	*/
}
