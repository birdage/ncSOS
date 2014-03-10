# NcSOS

Current Stable version of SOS used: **RC8**
=======
# postSOS update

* integrated geoserver requirements to pom file also added connection to ows service [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/applicationContext.xml) with the entry to the service being [here](https://github.com/birdage/ncSOS/blob/postsos/src/main/java/sos_entry.java) as far as i am aware it needs to reside in the default package location
* started abstracting the dataset through an interface, de-coupled the majority of the getCaps NC calls to function calls.

# install
(below are examples)
* building using maven dependancies
* copy jar to lib directory '''cp /{locationofrepo}/.m2/repository/org/geoserver/postSOS/1.0/postSOS-1.0.jar /Users/rpsdev/geonode/geoserver/geoserver/WEB-INF/lib'''
* copy dependancies 

** '''cp /{locationofrepo}/.m2/repository/org/apache/httpcomponents/httpclient/4.3.3/httpclient-4.3.3.jar /Users/rpsdev/geonode/geoserver/geoserver/WEB-INF/lib/'''

** '''cp /{locationofrepo}/.m2/repository/org/apache/httpcomponents/httpcore/4.3.2/httpcore-4.3.2.jar /Users/rpsdev/geonode/geoserver/geoserver/WEB-INF/lib/'''

** '''cp /{locationofrepo}/.m2/repository/joda-time/joda-time/2.3/joda-time-2.3.jar /{locationofgeonode}/geonode/geoserver/geoserver/WEB-INF/lib/'''

*** should not work



