# NcSOS
Current Stable version of SOS used: **RC8**
=======
## postSOS update

* integrated geoserver requirements to pom file also added connection to ows service [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/applicationContext.xml) with the entry to the service being [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/sos_entry.java) as far as i am aware it needs to reside in the default package location
* started abstracting the dataset through an interface, de-coupled the majority of the getCaps NC calls to function calls.

## Installing postSOS
* grab a release from the github page [here](https://github.com/birdage/ncSOS/releases/download/postsos-1.0.0/postSOS-1.0.0.zip) or to view all the available releases go [here](https://github.com/birdage/ncSOS/releases)
* move zip file to ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* ```cd``` to ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* unzip/deflate zip file (i.e ```unzip postSOS-1.0.0.zip```)

### check the following
* postSOS-1.0.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* httpcore-4.3.2.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* httpclient-4.3.3.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* joda-time-2.3.jar should be visible in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* there should be a ```sos_resources``` directory in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib```
* there should be a ```properties``` and ```templates``` directory in ```/{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib/sos_resources```

## Building a release version
* pull down source
* in eclipse right click on project select maven > maven install
* this will build out the project and the resources needed to ```sos_resources```
* cd to postsos root project directory

use either the ```build.sh``` script or do the following
* generate zip file, but first remove the existing one ```rm ./release/postSOS-1.0.0.zip```
* grab current jar file ```zip  -j ./release/postSOS-1.0.0.zip ./target/postSOS-1.0.jar```  
* grab current resources and update zip file ```zip  ./release/postSOS-1.0.0.zip ./sos_resources/*```
* grab required dependancies ```zip -j ./release/postSOS-1.0.0.zip ./jar/*```



