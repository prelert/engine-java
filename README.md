engine-java
===========

A Java client to the Prelert Anomaly Detective Engine REST API. This provides automated anomaly detection and behavioral analytics for big data. The client creates analysis jobs, streams data to them and queries the results.

Prior to using the client, the Engine API needs to be installed and setup. Please follow these steps:

- Have a read of our documentation: http://www.prelert.com/docs/engine_api/latest
- Download and install the Anomaly Detective Engine API from here: http://www.prelert.com/reg/anomaly-detective-engine-api.html
- We recommend you try our quick start example: http://www.prelert.com/docs/engine_api/latest/quick-start.html



Building 
---------
Prelert uses the Maven build system

To compile the code 

    mvn compile
    
and clean

    mvn clean

create the Java docs

    mvn javadoc:javadoc
    
or do it all

    mvn package
