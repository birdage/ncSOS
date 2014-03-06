package com.asascience.ncsos.gc;

import com.asascience.ncsos.outputformatter.ErrorFormatter;
import com.asascience.ncsos.outputformatter.gc.GetCapsFormatter;
import com.asascience.ncsos.service.BaseRequestHandler;
import com.asascience.sos.dataproducts.IDataProduct;
import com.asascience.sos.dataproducts.LatLonRect;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Creates basic Get Capabilites request handler that can read from a netcdf dataset
 * the information needed to populate a get capabilities template.
 * @author Abird
 * @version 1.0.0
 */
public class GetCapabilitiesRequestHandler extends BaseRequestHandler {

    private final String threddsURI;

    private enum Sections {

        OPERATIONSMETADATA, SERVICEIDENTIFICATION, SERVICEPROVIDER, CONTENTS
    }
    private String sections;
    private BitSet requestedSections;
    private static final int SECTION_COUNT = 4;
    //private static CalendarDate setStartDate;
    private static DateTime setStartDate;
    //private static CalendarDate setEndDate;
    private static DateTime setEndDate;
    //private static HashMap<Integer, CalendarDateRange> stationDateRange;
    private static HashMap<Integer, Interval> stationDateRange;
    
    //private static HashMap<Integer, LatLonRect> stationBBox;
    private static HashMap<Integer, LatLonRect> stationBBox;
    private static org.slf4j.Logger _log = org.slf4j.LoggerFactory.getLogger(GetCapabilitiesRequestHandler.class);

    /**
     * Creates an instance of GetCapabilitiesRequestHandler to handle the dataset
     * and uri from the thredds request.
     * @param netCDFDataset dataset for which the Get Capabilities request is being
     * directed to
     * @param threddsURI uri from the thredds Get Capabilities request
     * @param sections string detailing what sections of the GC response should be returned
     * @throws IOException
     */
    public GetCapabilitiesRequestHandler(IDataProduct dataset, String threddsURI, String sections) throws IOException {
        super(dataset);
        this.threddsURI = threddsURI;
        this.sections = sections.toLowerCase();
        this.formatter = new GetCapsFormatter(this);
        SetSectionBits();
        CalculateBoundsForFeatureSet();
    }

    public void resetCapabilitiesSections(String sections) throws IOException {
        this.sections = sections.toLowerCase();
        this.requestedSections = new BitSet(SECTION_COUNT);
        SetSectionBits();
    }

    /**
     * Creates the output for the get capabilities response
     */
    public void parseGetCapabilitiesDocument() {
        // early exit if we have an exception output
        if (formatter instanceof ErrorFormatter) {
            return;
        }

        GetCapsFormatter out = (GetCapsFormatter) formatter;

        // service identification; parse if it is the section identified or 'all'
        if (this.requestedSections.get(Sections.SERVICEIDENTIFICATION.ordinal())) {
            out.parseServiceIdentification(this.global_attributes);
        } else {
            // remove identification from doc
            out.removeServiceIdentification();
        }

        // service provider; parse if it is the section identified or 'all'
        if (this.requestedSections.get(Sections.SERVICEPROVIDER.ordinal())) {
            out.parseServiceDescription();
        } else {
            // remove service provider from doc
            out.removeServiceProvider();
        }

        // operations metadata; parse if it is the section identified or 'all'
        if (this.requestedSections.get(Sections.OPERATIONSMETADATA.ordinal())) {
            // Set the THREDDS URI
            out.setURL(threddsURI);
            // Set the GetObservation Operation
            out.setOperationsMetadataGetObs(threddsURI, getSensorNames(), getStationNames().values().toArray(new String[getStationNames().values().size()]));
            // Set the DescribeSensor Operation
            out.setOperationsMetadataDescSen(threddsURI, getSensorNames(), getStationNames().values().toArray(new String[getStationNames().values().size()]));
            // Set the ExtendedCapabilities
            out.setVersionMetadata();
        } else {
            // remove operations metadata
            out.removeOperationsMetadata();
        }

        // Contents; parse if it is the section identified or 'all'
        if (this.requestedSections.get(Sections.CONTENTS.ordinal())) {
            // observation offering list
            // network-all
            // get the bounds
            Double latMin = Double.MAX_VALUE, latMax = Double.NEGATIVE_INFINITY, lonMin = Double.MAX_VALUE, lonMax = Double.NEGATIVE_INFINITY;
            
            for (LatLonRect rect : stationBBox.values()) {
                latMin = (latMin > rect.getLatMin()) ? rect.getLatMin() : latMin;
                latMax = (latMax < rect.getLatMax()) ? rect.getLatMax() : latMax;
                lonMin = (lonMin > rect.getLonMin()) ? rect.getLonMin() : lonMin;
                lonMax = (lonMax < rect.getLonMax()) ? rect.getLonMax() : lonMax;
            }
            
            LatLonRect setRange = new LatLonRect(latMin, lonMin, latMax, lonMax);
            
            Interval setTime = null;
            if (setStartDate != null && setEndDate != null) {
                setTime = new Interval(setStartDate, setEndDate);
            }

            out.setObservationOfferingNetwork(setRange, getStationNames().values().toArray(new String[getStationNames().values().size()]), getSensorNames(), setTime);
            // Add an offering for every station
            for (Integer index : getStationNames().keySet()) {
                ((GetCapsFormatter) formatter).setObservationOffering(this.getUrnName(getStationNames().get(index)), stationBBox.get(index), getSensorNames(), stationDateRange.get(index));
            }
        } else {
            // remove Contents node
            out.removeContents();
        }
    }
    
    

    private void CalculateBoundsForFeatureSet() throws IOException {
            this.stationDateRange = dataset.getDateTimeRanges();
            this.stationBBox = dataset.getLatLonRects();
            
            this.setStartDate = dataset.getStartDateTime();
            this.setEndDate = dataset.getEndDateTime();
    }

    private void SetSectionBits() throws IOException {
        this.requestedSections = new BitSet(this.SECTION_COUNT);
        try {
            for (String sect : this.sections.split(",")) {
                if (sect.equals("all")) {
                    this.requestedSections.set(0, this.SECTION_COUNT);
                } else {
                    this.requestedSections.set(Sections.valueOf(sect.toUpperCase()).ordinal());
                }
            }
        } catch (Exception ex) {
            _log.error(ex.toString());
            // assume that an invalid value was passed in the sections parameter, print out exception out
            formatter = new ErrorFormatter();
            ((ErrorFormatter)formatter).setException("Invalid value for 'Sections' parameter, please see GetCapabilities for valid values.", INVALID_PARAMETER, "sections");
        }
    }
}
