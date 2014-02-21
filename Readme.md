# NcSOS

# postSOS update

[![Build Status](https://travis-ci.org/asascience-open/ncSOS.png?branch=master)](https://travis-ci.org/asascience-open/ncSOS)

Stable version: **RC8**

NcSOS adds an OGC SOS service to datasets in your existing [THREDDS](http://www.unidata.ucar.edu/projects/THREDDS/) server.  It complies with the [IOOS SWE Milestone 1.0](https://code.google.com/p/ioostech/source/browse/#svn%2Ftrunk%2Ftemplates%2FMilestone1.0) templates and requires your datasets be in any of the [CF 1.6 Discrete Sampling Geometries](http://cf-pcmdi.llnl.gov/documents/cf-conventions/1.6/cf-conventions.html#discrete-sampling-geometries).

NcSOS acts like other THREDDS services (such an OPeNDAP and WMS) where as there are individual service endpoints for each dataset.  It is best to aggregate your files and enable the NcSOS service on top of the aggregation.  i.e. The NcML aggregate of hourly files from an individual station would be a good candidate to serve with NcSOS.  Serving the individual hourly files with NcSOS would not be as beneficial.

_You will need a working THREDDS installation of a least version **4.3.16** to run NcSOS_.

# Quick Links
1. *Mailing list*: https://groups.google.com/forum/#!forum/ncsos
2. *Documentation wiki*: https://github.com/asascience-open/ncSOS/wiki
3. *Source repository*: https://github.com/asascience-open/ncSOS/
4. *Issues and Ideas*: https://github.com/asascience-open/ncSOS/issues?state=open
5. *Get source/installers*: https://github.com/asascience-open/ncSOS/releases

## ChangeLog

### RC8
* Better/automatic workaround for aggregation caching problem
* Bug fixes

### RC7
* Testing refactor
* Better class names
* Lots of cleanup in code and comments
* Now uses JDOM and XML objects instead of strings
* Jenkins integration

### RC6
* Testing cleanup
* Lots of bug schema related bug fixes

### RC5
* IOOS SWE Milestone 1.0 support
* Code cleanup and documentation
* Hosten in Maven
* Caching bug fix (see _Known Issues_ below)

### RC3-4
* Updated DescribeSensor formatting to match new IOOS DescribeSensorPlatform and DescribeSensorNetwork
* Added new response format for time series datasets
* New IOOS reponse format for GetObservation requests
* Templates for new IOOS formats found here: https://code.google.com/p/ioostech/source/browse/trunk/templates/Milestone1.0
* New system for logging missing variables and attributes in datasets 

### RC2:
* Expanded metadata reporting from files.
* Began updating the responses to the SOS 2.0 response format.
* Fixed formatting.
* Fixed many, many smaller bugs.
* Added error handling for large datasets.

### RC1:
* Added support for GetCapabilities, DescribeSensor and GetObservation requests.
* Added caching for GetCapabilities requests
* Supports CF 1.6 convention files including: TimeSeries, TimeSeriesProfile, Trajectory, Profile, TrajectoryProfile (Section) and Grid datasets.

##Known Issues
* Aggregating files using NcML does not work with the built in THREDDS caching system.  This is an issue on Unidata's side.  NcSOS has a built-in workaround that will disable caching when an aggregated dataset is detected.
