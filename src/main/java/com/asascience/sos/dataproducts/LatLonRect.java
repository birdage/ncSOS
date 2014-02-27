package com.asascience.sos.dataproducts;

public class LatLonRect {

	double latMin;
	double latMax;
	double lonMin;
	double lonMax;
	
	public LatLonRect(double latMin2, double lonMin2, double latMax2,
			double lonMax2) {
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
		return "";
	}

	public String getUpperRightPoint_lat() {
		return "";
	}

	public String getLowerLeftPoint_lat() {
		return "";
	}

	public String getUpperRightPoint_lon() {
		return "";
	}

	
	
}
