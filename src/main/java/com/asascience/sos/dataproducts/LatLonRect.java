package com.asascience.sos.dataproducts;

/**
 * class provides lat lon support for stations/bounding boxes
 * @author abird
 *
 */
public class LatLonRect {

	double latMin;
	double latMax;
	double lonMin;
	double lonMax;
	
	/**
	 * generates a bounding box for lat/lon
	 * @param latMin
	 * @param lonMin
	 * @param latMax
	 * @param lonMax
	 */
	public LatLonRect(double latMin, double lonMin, double latMax, double lonMax) {
		setLatMax(latMax);
		setLonMax(lonMax);
		setLatMin(latMin);
		setLonMin(lonMin);
	}


	public double getLatMin() {
		return latMin;
	}

	public double getLatMax() {
		return latMax;
	}

	public double getLonMin() {
		return lonMin;
	}

	public double getLonMax() {
		return lonMax;
	}

	public void setLatMin(double latMin) {
		this.latMin = latMin;
	}

	public void setLatMax(double latMax) {
		this.latMax = latMax;
	}

	public void setLonMin(double lonMin) {
		this.lonMin = lonMin;
	}

	public void setLonMax(double lonMax) {
		this.lonMax = lonMax;
	}

	public String getLowerLeftPoint_lon() {
		return Double.toString(lonMin);
	}

	public String getUpperRightPoint_lat() {
		return Double.toString(getLatMax());
	}

	public String getLowerLeftPoint_lat() {
		return Double.toString(latMin);
	}

	public String getUpperRightPoint_lon() {
		return Double.toString(lonMax);
	}

	
	
}
