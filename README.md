engine-java
===========

A Java client to the Prelert Anomaly Detective Engine REST API. This provides automated anomaly detection and behavioral analytics for big data. The client creates analysis jobs, streams data to them and queries the results.

Prior to using the client, the Engine API needs to be installed and setup. Please follow these steps:

- Have a read of our documentation: http://www.prelert.com/docs/engine_api/latest
- Download and install the Anomaly Detective Engine API from here: http://www.prelert.com/reg/anomaly-detective-engine-api.html
- We recommend you try our quick start example: http://www.prelert.com/docs/engine_api/latest/quick-start.html

Building
---------
Prelert uses the Maven build system.

To compile the code:

    mvn compile

Clean:

    mvn clean

Create the Java docs:

    mvn javadoc:javadoc

Or do it all:

    mvn package

Farequote Example
------------------
As an illustration of the Java client we present a walk-through of creating a new job
uploading data to the job then finding the most anomalous records in the data. The
example code is in [Farequote.java](prelert-engine-api-client/src/main/java/com/prelert/rs/examples/Farequote.java)
and the data used in the analysis can be downloaded from
[http://s3.amazonaws.com/prelert_demo/farequote.csv](http://s3.amazonaws.com/prelert_demo/farequote.csv)

Running the Example
~~~~~~~~~~~~~~~~~~~
Run the example from Maven using this command, within the prelert-engine-api-client directory:

    mvn exec:java -Dexec.mainClass="com.prelert.rs.examples.Farequote" -Dexec.args="/path/to/farequote.csv http://localhost:8080/engine/v2"

The first step is to set the job configuration:

    // Configure a detector
    Detector responseTimebyAirline = new Detector();
    responseTimebyAirline.setFieldName("responsetime");
    responseTimebyAirline.setByFieldName("airline");
    responseTimebyAirline.setFunction(Detector.METRIC);

    AnalysisConfig ac = new AnalysisConfig();
    ac.setBucketSpan(3600L); // 3600 seconds = 1 hour
    ac.setDetectors(Arrays.asList(responseTimebyAirline));

    // Data is CSV format with time field such as 2014-05-19 00:00:00+0000
    DataDescription dd = new DataDescription();
    dd.setFormat(DataDescription.DataFormat.DELIMITED);
    dd.setFieldDelimiter(',');
    dd.setTimeField("time");
    dd.setTimeFormat("yyyy-MM-dd HH:mm:ssX");


    JobConfiguration jobConfig = new JobConfiguration();
    jobConfig.setAnalysisConfig(ac);
    jobConfig.setDataDescription(dd);

The `EngineApiClient` implements `Closeable` and can be used in a try-with-resource
statement. Here the a new client is created and used to make the new Engine API job:

    try (EngineApiClient engineApiClient = new EngineApiClient())
    {
        String jobId = engineApiClient.createJob("http://localhost:8080/engine/v2/", jobConfig);
        if (jobId == null || jobId.isEmpty())
        {
            s_Logger.error("No Job Id returned by create job");
            ApiError error = engineApiClient.getLastError();
            if (error != null)
            {
                s_Logger.warn(error.toJson());
            }
            return;
        }

        ...
    }

If successful the new Job ID is returned otherwise if Job ID is empty an error has occurred.
The API Client logs any error message received after a call to the REST API and sets it to
an internal variable accessible through the `getLastError` function. The last error is
reset after every call to the API.

The client's upload functions accept `InputStream` instances. In this case a `FileIputStream`
is used:

    boolean success = engineApiClient.streamingUpload(baseUrl, jobId, fileStream, false);
    if (success == false)
    {
        s_Logger.error("Failed to upload file to job " + jobId);
        ApiError error = engineApiClient.getLastError();
        if (error != null)
        {
            s_Logger.warn(error.toJson());
        }
        return;
    }

The final parameter is a boolean indicating whether the stream is Gzipped compressed.
If we accidentally set it to `true` for our uncompressed data the API returns
an error. We can try that out:

    boolean success = engineApiClient.streamingUpload(baseUrl, jobId, fileStream, true);
    ApiError error = engineApiClient.getLastError();
    System.out.println(error.toJson());

Prints out a handy error informing us of the mistake:

    {
      "message" : "Content-Encoding = gzip but the data is not in gzip format",
      "errorCode" : 30102
    }

For more information on the possible errors and error codes see the Engine API documentation.

Once the upload is complete close the job to indicate that there is no more data.
This also causes the analytic process to flush its results buffer:

    engineApiClient.closeJob(baseUrl, jobId);

We can now request the results using the fluent [BucketsRequestBuilder](prelert-engine-api-client/src/main/java/com/prelert/rs/client/BucketsRequestBuilder.java) class. In this example the first 100 buckets are requested, for now we just
want the buckets not the anomaly records so the `expand` option is not used:

    BucketsRequestBuilder builder = new BucketsRequestBuilder(engineApiClient, jobId).take(100);
    Pagination<Bucket> page = builder.get();

The `get()` method returns a [Pagination](prelert-engine-api-common/src/main/java/com/prelert/rs/data/Pagination.java) object containing the buckets.
If there are more than one page of results `page.getNextPage()` returns the URI to the next page
of buckets:

    while (page.getNextPage() != null)
    {
        // get next page using the generic get and TypeReference
        page = engineApiClient.get(page.getNextPage().toString(),
                new TypeReference<Pagination<Bucket>>() {});

        // Alternatively use the skip parameter to get the next page
        /*
        page = builder.skip(skip).get();
        */

        ...
    }

See the API documentation for more details on paging results.

Printing out the buckets we notice a couple of anomalies let's find the largest one.
First sort all the buckets by anomaly:

    // Sort by anomaly score
    Collections.sort(allBuckets, new Comparator<Bucket>() {
        @Override
        public int compare(Bucket b1, Bucket b2)
        {
            return Double.compare(b2.getAnomalyScore(),
                        b1.getAnomalyScore());
        }
    });

Now the first bucket in the list has the largest anomaly score, find the ID of that
bucket:

    if (allBuckets.size() > 0)
    {
        String bucketTime = String.valueOf(allBuckets.get(0).getEpoch());

        // ask for the bucket and its anomaly records
        SingleDocument<Bucket> bucket =
                engineApiClient.getBucket(baseUrl, jobId, bucketTime, true);

        String msg = String.format(
                "The bucket at time %1$TF %1$TT%1$Tz has the "
                + "largest anomaly score with a value of %2$f",
                bucket.getDocument().getTimestamp(),
                bucket.getDocument().getAnomalyScore());


        // Print the bucket and anomaly records using a FasterXml Object mapper
        ObjectWriter objectWriter = new ObjectMapper()
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .writer().withDefaultPrettyPrinter();

        System.out.println(objectWriter.writeValueAsString(bucket));
    }

Reviewing the output we see that the bucket contains 2 records one of which is highly anomalous:
    
    {
      "timestamp" : "2014-06-25T16:00:00.000+0000",
      "bucketSpan" : 3600,
      "anomalyScore" : 70.19383,
      "maxNormalizedProbability" : 98.44456,
      "initialAnomalyScore" : 60.0985,
      "recordCount" : 1,
      "records" : [ {
        "timestamp" : "2014-06-25T16:00:00.000+0000",
        "fieldName" : "responsetime",
        "byFieldName" : "airline",
        "function" : "metric",
        "bucketSpan" : 3600,
        "anomalyScore" : 70.19383,
        "probability" : 3.28197E-36,
        "detectorIndex" : 0,
        "normalizedProbability" : 98.44456,
        "initialNormalizedProbability" : 97.1544,
        "byFieldValue" : "AAL",
        "functionDescription" : "mean",
        "typical" : 100.831,
        "actual" : 242.75,
        "isInterim" : false
      } ],
      "eventCount" : 909,
      "influencers" : [ ],
      "bucketInfluencers" : [ {
        "anomalyScore" : 70.19383,
        "probability" : 5.5367E-25,
        "influencerFieldName" : "bucketTime"
      } ],
      "isInterim" : false
    }

In the bucket at time *2014-06-25T16:00:00.000+0000* the *responsetime* value for the
airline *AAL* has a *mean* (function = mean) value of 242.75 compared to the typical
bucket mean of 100.852.

The second record where *function = count* and *simpleCount = true* is the number of records
analyzed in the bucket. Records of this type serve only to track the number of records processed
and will always have an anomaly score of 0.

