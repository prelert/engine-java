/****************************************************************************
 *                                                                          *
 * Copyright 2014 Prelert Ltd                                               *
 *                                                                          *
 * Licensed under the Apache License, Version 2.0 (the "License");          *
 * you may not use this file except in compliance with the License.         *
 * You may obtain a copy of the License at                                  *
 *                                                                          *
 *    http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                          *
 * Unless required by applicable law or agreed to in writing, software      *
 * distributed under the License is distributed on an "AS IS" BASIS,        *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and      *
 * limitations under the License.                                           *
 *                                                                          *
 ***************************************************************************/

package com.prelert.rs.examples.farequote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.prelert.job.AnalysisConfig;
import com.prelert.job.DataDescription;
import com.prelert.job.Detector;
import com.prelert.job.JobConfiguration;
import com.prelert.job.JobDetails;
import com.prelert.rs.client.EngineApiClient;
import com.prelert.rs.data.ApiError;
import com.prelert.rs.data.Bucket;
import com.prelert.rs.data.Pagination;
import com.prelert.rs.data.SingleDocument;

/**
 * Example of using the Prelert Engine API Java client.
 * 
 * This example works with the farequote.csv file that can be
 * downloaded from http://s3.amazonaws.com/prelert_demo/farequote.csv
 * the first 5 lines of which should resemble:
 * 
 * time,airline,responsetime,sourcetype
 * 2014-06-23 00:00:00Z,AAL,132.2046,farequote
 * 2014-06-23 00:00:00Z,JZA,990.4628,farequote
 * 2014-06-23 00:00:00Z,JBU,877.5927,farequote
 * 
 * The <code>main</code> method takes 2 arguments - the path to farequote.csv
 * and optionally the Url of the REST API. If the Url is not passed 
 * {@value #API_BASE_URL} is used.
 */
public class Farequote
{
	/**
	 * The default base Url
	 */
	static final public String API_BASE_URL = "http://localhost:8080/engine/v1";
	
	static final private Logger s_Logger = Logger.getLogger(Farequote.class);
	
	
	/**
	 * Object to JSON mapper.
	 * Writes dates in ISO 8601 format
	 */
	static final private ObjectWriter s_ObjectWriter = 
			new ObjectMapper()
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.writer().withDefaultPrettyPrinter();
	
	/**
	 * Create the job configuration for the farequote data set.
	 * The config has one detector set to analyze the field 
	 * 'responsetime' by the field 'airline' using the 'metric'
	 * functions. The data is CSV format with a time field formatted 
	 * as 2014-05-19 00:00:00+0000. Bucket span is set to 1 hour.
	 * 
	 * @return The job configuration.
	 */
	static public JobConfiguration createFarequoteJobConfig() 
	{
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
		dd.setFormat(DataDescription.DataFormat.DELINEATED);
		dd.setFieldDelimiter(',');
		dd.setTimeField("time");
		dd.setTimeFormat("yyyy-MM-dd HH:mm:ssX");
		
		
		JobConfiguration jobConfig = new JobConfiguration();
		jobConfig.setAnalysisConfig(ac);
		jobConfig.setDataDescription(dd);
		
		return jobConfig;
	}
	
	
	/**
	 * Check for the last error from the REST API and log if present.
	 * 
	 * @param error The error to report, if <code>null</code> nothing 
	 * is reported
	 */
	static public void reportApiErrorMessage(ApiError error)
	{
		if (error != null)
		{
			s_Logger.warn(error.toJson());
		}
	}
	
	
	/**
	 * Print the CSV header for the bucket scores
	 */
	static public void printBucketScoresHeader()
	{
		System.out.println("Time, Bucket Id, Anomaly Score, Unusual Score");
	}
	
	
	/**
	 * Print the bucket time, id and anomaly score to std out
	 * in CSV format.
	 * 
	 * @param buckets The bucket results 
	 */
	static public void printBucketScores(List<Bucket> buckets)
	{
		for (Bucket bucket : buckets)
		{
			System.out.println(String.format("%s,%s,%f,%f", 
					bucket.getTimestamp().toString(),
					bucket.getId(),
					bucket.getAnomalyScore(),
					bucket.getUnusualScore()));
		}
	}
	
	/**
	 * Print the bucket as a JSON document.
	 * 
	 * @throws JsonProcessingException 
	 */
	static public void printBucket(Bucket bucket) 
	throws JsonProcessingException
	{
		System.out.println(s_ObjectWriter.writeValueAsString(bucket));
	}
	
	
	/**
	 * Create a new Engine API analytics job, upload data to it and 
	 * print the results.  
	 * 
	 * @param args If set the first argument is the path to farequote.csv
	 * and must be set. The second argument is the Engine API Url if not set 
	 * {@value #API_BASE_URL} is used.
	 * 
	 * @throws ClientProtocolException 
	 * @throws IOException
	 */
	public static void main(String[] args) 
	throws ClientProtocolException, IOException 
	{
		// configure logging to console
		ConsoleAppender console = new ConsoleAppender(); 		
		console.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n")); 
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
				
		if (args.length == 0)
		{
			System.out.println("This script expects at least one argument - "
					+ "the path to farequote.csv. Download the file from "
					+ "http://s3.amazonaws.com/prelert_demo/farequote.csv");
			System.out.println("Usage: The first (mandatory) argument is the path to "
					+ "farequote.csv the second (optional) argument is the API " 
					+ "Url, the default is " +  API_BASE_URL);
			
			return;
		}
		
		File dataFile = new File(args[0]);
		FileInputStream fileStream;
		try
		{
			fileStream = new FileInputStream(dataFile);
		}	
		catch (FileNotFoundException e)
		{
			s_Logger.error("Cannot find data file " + dataFile, e);
			return;
		}

		
		String baseUrl = API_BASE_URL;
		if (args.length > 1)
		{
			baseUrl = args[1];
		}
		
		
		// Create the job config
		JobConfiguration jobConfig = Farequote.createFarequoteJobConfig();
		
		try (EngineApiClient engineApiClient = new EngineApiClient())
		{
			String jobId = engineApiClient.createJob(baseUrl, jobConfig);
			if (jobId == null || jobId.isEmpty())
			{
				s_Logger.error("No Job Id returned by create job");
				reportApiErrorMessage(engineApiClient.getLastError());
				return;
			}
			
			// Review the job details
			SingleDocument<JobDetails> jobDoc = engineApiClient.getJob(baseUrl, jobId);
			if (jobDoc.isExists() == false)
			{
				// Strange, the job does not exist review any error messages
				reportApiErrorMessage(engineApiClient.getLastError());
				return;
			}

			boolean success = engineApiClient.streamingUpload(baseUrl, jobId, fileStream, false);
	 		if (success == false)
	 		{
				s_Logger.error("Failed to upload file to job " + jobId);
				reportApiErrorMessage(engineApiClient.getLastError());
				return;
	 		}
	 		
	 		// commit the uploaded data and close the job.	 		
	 		engineApiClient.closeJob(baseUrl, jobId);
	 				
	 		// results are available immediately after the close
	 		Pagination<Bucket> page = engineApiClient.getBuckets(baseUrl, jobId, false, 0.0, 0.0);
	 		if (page.getDocumentCount() == 0 && engineApiClient.getLastError() == null)
	 		{
				s_Logger.error("Error reading analysis results");
				reportApiErrorMessage(engineApiClient.getLastError());
				return;
	 		}
	 		
	 		// print
	 		printBucketScoresHeader();
	 		printBucketScores(page.getDocuments());
	 		
	 		List<Bucket> allBuckets = new ArrayList<>(page.getDocuments());
	 		
	 		while (page.getNextPage() != null)
	 		{
	 			// get next page using the generic get and TypeReference
	 			page = engineApiClient.get(page.getNextPage().toString(),
	 					new TypeReference<Pagination<Bucket>>() {});
	 			
	 			
	 			// or use the skip and take parameters
	 			/*
	 			page = engineApiClient.getBuckets(baseUrl, jobId, false, 
	 					new Long(page.getSkip() + page.getTake()), 
	 					new Long(page.getTake()));
	 			*/
	 			
	 			printBucketScores(page.getDocuments());
	 			allBuckets.addAll(page.getDocuments());
	 		}
	 		
	 		// Sort by anomaly score
	 		Collections.sort(allBuckets, new Comparator<Bucket>() { 
				@Override
	            public int compare(Bucket b1, Bucket b2)
	            {
		 			return Double.compare(b2.getAnomalyScore(),
		 						b1.getAnomalyScore());
	            }
	 		});
	 		
	 		if (allBuckets.size() > 0)
	 		{
	 			String bucketId = allBuckets.get(0).getId();
	 			
	 		    // ask for the bucket and its anomaly records
	 			SingleDocument<Bucket> bucket = 
	 					engineApiClient.getBucket(baseUrl, jobId, bucketId, true);
	 			if (bucket.isExists() == false)
	 			{
	 				// error, where has the bucket gone?
	 				reportApiErrorMessage(engineApiClient.getLastError());	 				
	 			}
	 			else		
	 			{
	 				String msg = String.format(
	 						"The bucket at time %1$TF %1$TT%1$Tz has the "
	 						+ "largest anomaly score with a value of %2$f", 
	 						bucket.getDocument().getTimestamp(),
	 						bucket.getDocument().getAnomalyScore());
	 				
	 				System.out.println(msg);
	 				printBucket(bucket.getDocument());	
	 			}
	 		}
	 		
		}
	}
	
}
