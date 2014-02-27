package com.asascience.ncsos.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.asascience.ncsos.error.ExceptionResponseHandler;
import com.asascience.ncsos.gc.GetCapabilitiesRequestHandler;
import com.asascience.ncsos.go.GetObservationRequestHandler;
import com.asascience.ncsos.outputformatter.CachedFileFormatter;
import com.asascience.ncsos.outputformatter.OutputFormatter;
import com.asascience.ncsos.util.LowerCaseStringMap;
import com.asascience.sos.dataproducts.IDataProduct;

public class Parser {

	public static final String ERROR = "error";
	public static final String GETCAPABILITIES = "GetCapabilities";
	public static final String GETOBSERVATION = "GetObservation";
	public static final String DESCRIBESENSOR = "DescribeSensor";
	public static final String OUTPUT_FORMATTER = "outputFormatter";
	public static final String SECTIONS = "sections";
	public static final String USECACHE = "usecache";
	public static final String XML = "xml";
	private LowerCaseStringMap queryParameters;
	private Logger _log;
	private Map<String, String> coordsHash;
	private final String defService = "sos";
	private final String defVersion = "1.0.0";
	private final String TRUE_STRING = "true";
	public final static String PROCEDURE = "procedure";
	public final static String ACCEPT_VERSIONS = "AcceptVersions";
	public final static String VERSION = "version";
	public final static String REQUEST = "request";
	public final static String SERVICE = "service";
	public final static String LAT = "latitude";
	public final static String LON = "longitude";
	public final static String DEPTH = "depth";
	public final static String RESPONSE_FORMAT = "responseFormat";
	public final static String OUTPUT_FORMAT = "outputFormat";
	public final static String OBSERVED_PROPERTY = "observedProperty";
	public final static String OFFERING = "offering";
	public final static String EVENT_TIME = "eventTime";

	// Exception codes - Table 25 of OGC 06-121r3 (OWS Common)
	protected static String INVALID_PARAMETER = "InvalidParameterValue";
	protected static String MISSING_PARAMETER = "MissingParameterValue";
	protected static String OPTION_NOT_SUPPORTED = "OptionNotSupported";
	protected static String OPERATION_NOT_SUPPORTED = "OperationNotSupported";
	protected static String VERSION_NEGOTIATION = "VersionNegotiationFailed";

	ExceptionResponseHandler errorHandler = null;

	/**
	 * Sets the logger for error output using the Parser class.
	 */
	public Parser() throws IOException {
		_log = LoggerFactory.getLogger(Parser.class);
		errorHandler = new ExceptionResponseHandler();
	}

	/**
	 * enhanceGETRequest - provides direct access to parsing a sos request and
	 * create handler for the type of request coming in
	 * 
	 * @param dataset
	 *            NetcdfDataset to enhanceGETRequest the NCML
	 * @param query
	 *            query string provided by request
	 * @param threddsURI
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, Object> enhanceGETRequest(
			final IDataProduct dataset, final String query, String threddsURI)
			throws IOException {
		return enhanceGETRequest(dataset, query, threddsURI, null);
	}

	/**
	 * enhanceGETRequest - provides direct access to parsing a sos request and
	 * create handler for the type of request coming in
	 * 
	 * @param dataset
	 *            NetcdfDataset to enhanceGETRequest the NCML
	 * @param query
	 *            query string provided by request
	 * @param threddsURI
	 * @param savePath
	 *            provides a directory to cache requests for quick replies
	 *            (currently unsupported)
	 * @return
	 * @throws IOException
	 */

	public HashMap<String, Object> enhanceGETRequest(
			final IDataProduct dataset, final String query, String threddsURI,
			String savePath) throws IOException {
		// clear anything that can cause issue if we were to use the same parser
		// for multiple requests
		queryParameters = new LowerCaseStringMap();
		coordsHash = new HashMap<String, String>();

		if (query != null) {
			// parse the query string
			queryParameters.putAll(parseQuery(query));
		} else {
			// we are assuming that a request made here w/o a query string is a
			// 'GetCapabilities' request
			// add the request to our query parameters, as well as some other
			// default values
			queryParameters.put(REQUEST, GETCAPABILITIES);
			queryParameters.put(SERVICE, defService);
			queryParameters.put(ACCEPT_VERSIONS, defVersion);
		}

		// check the query parameters to make sure all required parameters are
		// passed in
		HashMap<String, Object> retval = checkQueryParameters();

		if (retval.containsKey(ERROR)) {
			retval.put(OUTPUT_FORMATTER, errorHandler.getOutputFormatter());
			return retval;
		}

		try {
			String request = queryParameters.get(REQUEST).toString();

			if (request.equalsIgnoreCase(GETCAPABILITIES)) {
				GetCapabilitiesRequestHandler capHandler = null;
				String sections = "all";
				if (queryParameters.containsKey(SECTIONS)) {
					sections = queryParameters.get(SECTIONS).toString();
				}

				try {
					capHandler = new GetCapabilitiesRequestHandler(dataset,
							threddsURI, sections);
				} catch (IOException ex) {
					_log.error(ex.getMessage(), ex);
					capHandler = null;
				}

				if (capHandler != null) {
					parseGetCaps(capHandler);
					retval.put(OUTPUT_FORMATTER,
							capHandler.getOutputFormatter());
				} else if (!retval.containsKey(OUTPUT_FORMATTER)) {
					errorHandler
							.setException("Internal Error in preparing output for GetCapabilities request, received null handler.");
					retval.put(OUTPUT_FORMATTER,
							errorHandler.getOutputFormatter());
				}
			} else if (request.equalsIgnoreCase(GETOBSERVATION)) {
				GetObservationRequestHandler obsHandler = null;
				// setup our coordsHash
				if (queryParameters.containsKey(LAT)) {
					coordsHash.put(LAT, queryParameters.get(LAT).toString());
				}
				if (queryParameters.containsKey(LON)) {
					coordsHash.put(LON, queryParameters.get(LON).toString());
				}
				if (queryParameters.containsKey(DEPTH)) {
					coordsHash
							.put(DEPTH, queryParameters.get(DEPTH).toString());
				}
				try {

					String[] procedure = null;
					String[] eventTime = null;

					if (queryParameters.containsKey(PROCEDURE)) {
						procedure = (String[]) queryParameters.get(PROCEDURE);
					}

					if (queryParameters.containsKey(EVENT_TIME)) {
						eventTime = (String[]) queryParameters.get(EVENT_TIME);
					}
					// create a new handler for our get observation request and
					// then write its result to output
					obsHandler = new GetObservationRequestHandler(dataset,
							procedure, (String) queryParameters.get(OFFERING),
							(String[]) queryParameters.get(OBSERVED_PROPERTY),
							eventTime, queryParameters.get(RESPONSE_FORMAT)
									.toString(), coordsHash);

					obsHandler.parseObservations();

					// add our handler to the return value
					retval.put(OUTPUT_FORMATTER,
							obsHandler.getOutputFormatter());
				} catch (Exception ex) {
					_log.error(
							"Internal Error in creating output for GetObservation request:",
							ex);
					errorHandler
							.setException("Internal Error in creating output for GetObservation request - "
									+ ex.toString());
					retval.put(OUTPUT_FORMATTER,
							errorHandler.getOutputFormatter());
				}
			} else {
				// return a 'not supported' error
				String message = queryParameters.get(REQUEST).toString()
						+ " is not a supported request.";
				_log.error(message);
				errorHandler.setException(message, OPERATION_NOT_SUPPORTED,
						"request");
				retval.put(OUTPUT_FORMATTER, errorHandler.getOutputFormatter());
				return retval;
			}
		} catch (IllegalArgumentException ex) {
			// create a get caps response with exception
			String message = "Unrecognized request "
					+ queryParameters.get("request").toString();
			_log.error(message, ex);
			errorHandler.setException(message, INVALID_PARAMETER, "request");
			retval.put(OUTPUT_FORMATTER, errorHandler.getOutputFormatter());
			return retval;
		}

		return retval;
	}

	private void parseGetCaps(GetCapabilitiesRequestHandler capHandler)
			throws IOException {
		// do our parsing
		capHandler.parseGetCapabilitiesDocument();
	}

	private HashMap<String, Object> parseQuery(String query) {
		// @TODO: this needs to be rewritten using apache commons
		// URLEncodedUtils sadly not available until 4.0

		HashMap<String, Object> queryMap = new HashMap<String, Object>();

		String[] queryArguments = query.split("&");
		for (String arg : queryArguments) {
			String[] keyVal = arg.split("=");
			String key;
			String value;

			try {
				key = URLDecoder.decode(keyVal[0], "UTF-8");
				value = URLDecoder.decode(keyVal[1], "UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				_log.info("Unsupported Encoding exception, continuing", e);
				continue;
			}

			if (key.equalsIgnoreCase(PROCEDURE)) {
				String[] howManyStation = value.split(",");
				queryMap.put(key.toLowerCase(), howManyStation);
			} else if (key.equalsIgnoreCase(RESPONSE_FORMAT)) {
				parseOutputFormat(RESPONSE_FORMAT, value);
			} else if (key.equalsIgnoreCase(OUTPUT_FORMAT)) {
				parseOutputFormat(OUTPUT_FORMAT, value);
			} else if (key.equalsIgnoreCase(EVENT_TIME)) {
				String[] eventtime;
				if (value.contains("/")) {
					eventtime = value.split("/");
				} else {
					eventtime = new String[] { value };
				}
				queryMap.put(key, eventtime);
			} else if (key.equalsIgnoreCase(OBSERVED_PROPERTY)) {
				String[] param;
				if (value.contains(",")) {
					param = value.split(",");
				} else {
					param = new String[] { value };
				}
				queryMap.put(key, param);
			} else if (key.equalsIgnoreCase(ACCEPT_VERSIONS)) {
				String[] param;
				if (value.contains(",")) {
					param = value.split(",");
				} else {
					param = new String[] { value };
				}
				queryMap.put(ACCEPT_VERSIONS, param);

			} else {
				queryMap.put(key, value);
			}
		}

		return queryMap;
	}

	public void parseOutputFormat(String fieldName, String value) {
		queryParameters.put(fieldName, value);
	}

	private String getCacheXmlFileName(String threddsURI) {
		_log.debug("thredds uri: " + threddsURI);
		String[] splitStr = threddsURI.split("/");
		String dName = splitStr[splitStr.length - 1];
		splitStr = dName.split("\\.");
		dName = "";
		for (int i = 0; i < splitStr.length - 1; i++) {
			dName += splitStr[i] + ".";
		}
		return "/" + dName + XML;
	}

	private GetCapabilitiesRequestHandler createGetCapsCacheFile(
			IDataProduct dataset, String threddsURI, String savePath)
			throws IOException {
		_log.debug("Writing cache file for Get Capabilities request.");
		GetCapabilitiesRequestHandler cacheHandle = new GetCapabilitiesRequestHandler(
				dataset, threddsURI, "all");
		parseGetCaps(cacheHandle);
		File file = new File(savePath + getCacheXmlFileName(threddsURI));
		file.createNewFile();
		Writer writer = new BufferedWriter(new FileWriter(file, false));
		writer.flush();
		cacheHandle.formatter.writeOutput(writer);
		_log.debug("Write cache to: " + file.getAbsolutePath());
		return cacheHandle;
	}

	private OutputFormatter fileIsInDate(File f, String sections)
			throws ParserConfigurationException, SAXException, IOException {
		_log.debug("Using cached get capabilities doc");
		CachedFileFormatter retval = new CachedFileFormatter(f);
		retval.setSections(sections);
		return retval;
	}

	private HashMap<String, Object> checkQueryParameters() {
		try {
			HashMap<String, Object> retval = new HashMap<String, Object>();
			String[] requiredGlobalParameters = { REQUEST, SERVICE };
			String[] requiredDSParameters = { PROCEDURE, OUTPUT_FORMAT, VERSION };
			// required GRID Parameters
			String[] requiredGOParameters = { OFFERING, OBSERVED_PROPERTY,
					RESPONSE_FORMAT, VERSION };

			// general parameters expected
			if (queryParameters.containsKey(ERROR)) {
				errorHandler.setException("Error with request - "
						+ queryParameters.get(ERROR).toString());
				retval.put(ERROR, true);
				return retval;
			} else {
				for (String req : requiredGlobalParameters) {
					if (!queryParameters.containsKey(req.toLowerCase())) {
						errorHandler
								.setException(
										"Required parameter '"
												+ req
												+ "' not found. Check GetCapabilities document for required parameters.",
										MISSING_PARAMETER, req);
						retval.put(ERROR, true);
						return retval;
					}
				}

				if (!queryParameters.get(SERVICE).toString()
						.equalsIgnoreCase(defService)) {
					errorHandler.setException(
							"Currently the only supported service is SOS.",
							OPERATION_NOT_SUPPORTED, "service");
					retval.put(ERROR, true);
					return retval;
				}
			}
			// specific parameters expected
			String request = queryParameters.get(REQUEST).toString();

			if (request.equalsIgnoreCase(GETCAPABILITIES)) {
				// check requirements for version and service
				if (queryParameters.containsKey(ACCEPT_VERSIONS)) {
					String[] versions = null;
					if (queryParameters.get(ACCEPT_VERSIONS) instanceof String[]) {
						versions = (String[]) queryParameters
								.get(ACCEPT_VERSIONS);
					} else if (queryParameters.get(ACCEPT_VERSIONS) instanceof String) {
						versions = new String[] { (String) queryParameters
								.get(ACCEPT_VERSIONS) };
					}
					if (versions != null && versions.length == 1) {
						if (!versions[0].equalsIgnoreCase(defVersion)) {
							errorHandler.setException(
									"Currently only SOS version " + defVersion
											+ " is supported.",
									VERSION_NEGOTIATION);
							retval.put(ERROR, true);
							return retval;
						}
					} else {
						errorHandler.setException("Currently only SOS version "
								+ defVersion + " is supported.",
								VERSION_NEGOTIATION);
						retval.put(ERROR, true);
						return retval;
					}
				}
			} else if (request.equalsIgnoreCase(DESCRIBESENSOR)) {
				for (String req : requiredDSParameters) {
					if (!queryParameters.containsKey(req.toLowerCase())) {
						errorHandler
								.setException(
										"Required parameter '"
												+ req
												+ "' not found. Check GetCapabilities document for required parameters of DescribeSensor requests.",
										MISSING_PARAMETER, req);
						retval.put(ERROR, true);
						return retval;
					}
				}
				// Check version
				if (!queryParameters.get(VERSION).equals(defVersion)) {
					errorHandler.setException("Currently only SOS version "
							+ defVersion + " is supported", INVALID_PARAMETER,
							"version");
					retval.put(ERROR, true);
					return retval;
				}
			} else if (request.equalsIgnoreCase(GETOBSERVATION)) {
				for (String req : requiredGOParameters) {
					if (!queryParameters.containsKey(req)) {
						errorHandler
								.setException(
										"Required parameter '"
												+ req
												+ "' not found. Check GetCapabilities document for required parameters of GetObservation requests.",
										MISSING_PARAMETER, req);
						retval.put(ERROR, true);
						return retval;
					}
				}
				// Check version
				if (!queryParameters.get(VERSION).equals(defVersion)) {
					errorHandler.setException("Currently only SOS version "
							+ defVersion + " is supported", INVALID_PARAMETER,
							"version");
					retval.put(ERROR, true);
					return retval;
				}
			}

			return retval;

		} catch (Exception ex) {
			_log.error(ex.toString());
			HashMap<String, Object> retval = new HashMap<String, Object>();
			errorHandler
					.setException("Error in request. Check required parameters from GetCapabilities document and try again.");
			retval.put(ERROR, true);
			return retval;
		}
	}
}
