# NcSOS

Current Stable version of SOS used: **RC8**
=======
# postSOS update

* integrated geoserver requirements to pom file also added connection to ows service [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/applicationContext.xml) with the entry to the service being [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/sos_entry.java) as far as i am aware it needs to reside in the default package location
* started abstracting the dataset through an interface, de-coupled the majority of the getCaps NC calls to function calls.

* TODO
* Generate simple test class implementing the Idataproduct interface
* Generate postgres implementation
* above requires postgres data connection (JDBC)?



