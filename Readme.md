# NcSOS
Current Stable version of SOS used: **RC8**
=======
## postSOS update

### DATA MUST BE LOADED VIA COI SERVICES TEST ([TABLELOADER](https://github.com/ooici/coi-services/blob/master/ion/services/eoi/table_loader.py)) 
	using the following command `nose -vs -a INTMAN ion.services.eoi.test.test_table_loader` it will create a breakpoint in coi to test on.

example request...
* getCaps `http://localhost:8080/geoserver/ows?request=getCapabilities&service=sos&version=1.0.0&offering=_9ad8acf0581b48aa9557fa610e04d670_view&responseFormat=text%2Fxml%3Bsubtype%3D%22om%2F1.0.0%22`
* getObs `http://localhost:8080/geoserver/ows?request=getObservation&service=sos&version=1.0.0&observedProperty=time,temp&offering=_9ad8acf0581b48aa9557fa610e04d670_view&responseFormat=text%2Fxml%3Bsubtype%3D%22om%2F1.0.0%22`	

* integrated geoserver requirements to pom file also added connection to ows service [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/applicationContext.xml) with the entry to the service being [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/sos_entry.java) as far as i am aware it needs to reside in the default package location
* started abstracting the dataset through an interface, de-coupled the majority of the getCaps NC calls to function calls.

## Installing postSOS
* grab a release from the github page [here](https://github.com/birdage/ncSOS/releases/download/postsos-1.0.0/postSOS-1.0.0.zip) or to view all the available releases go [here](https://github.com/birdage/ncSOS/releases)
* move zip file to ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* ```cd``` to ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* unzip/deflate zip file (i.e ```unzip postSOS-1.0.0.zip```)
* make sure to move the sos_properties to "/"

### check the following
* postSOS-1.0.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* httpcore-4.3.2.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* httpclient-4.3.3.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* joda-time-2.3.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* there should be a ```sos_resources``` directory in ```/```
* there should be a ```properties``` and ```templates``` directory in ```sos_resources```

## Building a release version
* pull down source
* in eclipse right click on project select maven > maven install
* this will build out the project and the resources needed to ```sos_resources```
* cd to postsos root project directory

use either the ```build.sh``` script or do the following
* generate zip file, but first remove the existing one ```rm ./release/postSOS-1.0.0.zip```
* grab current jar file ```zip  -j ./release/postSOS-1.0.0.zip ./target/postSOS-1.0.jar```  
* grab current resources and update zip file ```zip -r ./release/postSOS-1.0.0.zip ./sos_resources/*```
* grab required dependancies ```zip -j ./release/postSOS-1.0.0.zip ./jar/*```

## Using the service

There are two files to take note of when using the service, the first is the [applicationContent.xml](https://github.com/birdage/ncSOS/blob/master/src/main/java/applicationContext.xml), the second is the [sos_entry](https://github.com/birdage/ncSOS/blob/master/src/main/java/sos_entry.java). These two files combined tell the system where and how the response is processed.

There is a service up command there tells the user if the sos service is running on geonode
`http://{server/port}/geoserver/ows?request=echo&service=sos&version=1.0.0`, request can then be replaced with `getCapabilities` or `getObservation`. At present there is not enough meta data pass through the system to make performing a describe sensor worth while.

