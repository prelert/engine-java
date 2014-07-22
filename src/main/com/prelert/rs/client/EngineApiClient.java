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

package com.prelert.rs.client;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prelert.job.JobConfiguration;
import com.prelert.job.JobDetails;
import com.prelert.rs.data.ApiError;
import com.prelert.rs.data.Bucket;
import com.prelert.rs.data.Detector;
import com.prelert.rs.data.Pagination;
import com.prelert.rs.data.SingleDocument;

/**
 * The client for the Prelert Engine RESTful API.
 * <br/>
 * Contains methods to create jobs, list jobs, upload data and query results.
 */
public class EngineApiClient implements Closeable
{
	static final private Logger s_Logger = Logger.getLogger(EngineApiClient.class);
	
	private ObjectMapper m_JsonMapper;
		
	private CloseableHttpClient m_HttpClient;
	
	private ApiError m_LastError;

	/**
	 * Creates a new http client and Json object mapper.
	 * Call {@linkplain #close()} once finished
	 */
	public EngineApiClient()
	{
		m_HttpClient = HttpClients.createDefault();
		m_JsonMapper = new ObjectMapper();
		m_JsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	/**
	 * Close the http client
	 */
	@Override
	public void close() throws IOException
	{
		m_HttpClient.close();
	}
	
	/**
	 * Get details of all the jobs in database
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @return The {@link Pagination} object containing a list of {@link JobDetails jobs}
	 * @throws IOException
	 */
	public Pagination<JobDetails> getJobs(String baseUrl) 
	throws IOException
	{
		String url = baseUrl + "/jobs";
		s_Logger.info("GET jobs: " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			HttpEntity entity = response.getEntity();				
			String content = EntityUtils.toString(entity);

			if (response.getStatusLine().getStatusCode() == 200)
			{
				Pagination<JobDetails> docs = m_JsonMapper.readValue(content, 
						new TypeReference<Pagination<JobDetails>>() {} );
				
				m_LastError = null;
				return docs;
			}
			else
			{
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}
		}		
		
		Pagination<JobDetails> page = new Pagination<>();
		page.setDocuments(Collections.<JobDetails>emptyList());
		return page;
	}
	
	/**
	 * Get the individual job on the provided URL
	 *  
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * 
	 * @return If the job exists a {@link com.prelert.rs.data.SingleDocument SingleDocument}
	 * containing the {@link JobDetails job} is returned else the SingleDocument is empty
	 * @throws IOException
	 */
	public SingleDocument<JobDetails> getJob(String baseUrl, String jobId)
	throws IOException
	{
		String url = baseUrl + "/jobs/" + jobId;
		s_Logger.info("GET job: " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 200)
			{
				SingleDocument<JobDetails> doc = m_JsonMapper.readValue(content, 
						new TypeReference<SingleDocument<JobDetails>>() {} );
				
				m_LastError = null;
				return doc;
			}
			else
			{
				String msg = String.format(
						"Failed to get job %s, status code = %d. "
						+ "Returned content: %s", 
						jobId, response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
							new TypeReference<ApiError>() {} );
				
				return new SingleDocument<>();
			}
		}
	}
	
	
	/**
	 * Create a new Job from the <code>JobConfiguration</code> object.
	 * <br/>
	 * Internally this function converts <code>jobConfig</code> to a JSON
	 * string and calls {@link #createJob(String, String)}
	 * 
	 * @param baseUrl he base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobConfig 
	 * @return The new job's Id or an empty string if there was an error
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String createJob(String baseUrl, JobConfiguration jobConfig) 
	throws ClientProtocolException, IOException
	{
		String payLoad = m_JsonMapper.writeValueAsString(jobConfig);
		return createJob(baseUrl, payLoad);
	}
	
	
	/**
	 * Create a new job with the configuration in <code>createJobPayload</code>
	 * and return the newly created job's Id
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param createJobPayload The Json configuration for the new job
	 * @return The new job's Id or an empty string if there was an error
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String createJob(String baseUrl, String createJobPayload) 
	throws ClientProtocolException, IOException
	{
		String url = baseUrl + "/jobs";
		s_Logger.info("Create job: " + url);
		
		HttpPost post = new HttpPost(url);
		
		StringEntity entity = new StringEntity(createJobPayload,
				ContentType.create("application/json", "UTF-8"));
		post.setEntity(entity);
		
		try (CloseableHttpResponse response = m_HttpClient.execute(post))
		{
			HttpEntity responseEntity = response.getEntity();				
			String content = EntityUtils.toString(responseEntity);

			if (response.getStatusLine().getStatusCode() == 201)
			{
				
				Map<String, String> msg = m_JsonMapper.readValue(content, 
						new TypeReference<Map<String, String>>() {} );
				
				m_LastError = null;
				if (msg.containsKey("id"))
				{
					return msg.get("id");
				}
				else
				{
					s_Logger.error("Job created but no 'id' field in returned content");
					s_Logger.error("Response Content = " + content);
				}
			}
			else
			{
				String msg = String.format(
						"Error creating job status code = %d. "
						+ "Returned content: %s", 
						response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}
			
			return "";
		}
	}
	
	
	/**
	 * PUTS the description parameter to the job and sets it as 
	 * the job's new description field
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The job's unique ID
	 * @param description New description field
	 * 
	 * @return True
	 * @throws IOException
	 */
	public boolean setJobDescription(String baseUrl, String jobId, 
			String description) 
	throws IOException
	{
		String url = baseUrl + "/jobs/" + jobId + "/description";
		s_Logger.debug("PUT job description: " + url);
		
		HttpPut put = new HttpPut(url);
		StringEntity entity = new StringEntity(description);
		put.setEntity(entity);
		
		try (CloseableHttpResponse response = m_HttpClient.execute(put))
		{
			if (response.getStatusLine().getStatusCode() == 200)
			{
				m_LastError = null;
				return true;
			}
			else 
			{
				String content = EntityUtils.toString(response.getEntity());
				String msg = String.format(
						"Error putting job descriptio. Status code = %d, "
						+ "Returned content: %s", 
						response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
				
				return false;
			}
		}
	}
	
	
	/**
	 * Delete an individual job 
	 *  
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @return If the job existed and was deleted return true else false
	 * @throws IOException, ClientProtocolException
	 */
	public boolean deleteJob(String baseUrl, String jobId) 
	throws ClientProtocolException, IOException
	{
		String url = baseUrl + "/jobs/" + jobId;
		s_Logger.info("DELETE job: " + url);
		
		HttpDelete delete = new HttpDelete(url);
		
		try (CloseableHttpResponse response = m_HttpClient.execute(delete))
		{
			if (response.getStatusLine().getStatusCode() == 200)
			{
				m_LastError = null;
				return true;
			}
			else 
			{
				String content = EntityUtils.toString(response.getEntity());
				String msg = String.format(
						"Error deleting job, status code = %d. "
						+ "Returned content: %s", 
						response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
				
				return false;
			}
		}
	}
	
	/**
	 * Read the input stream in 4Mb chunks and upload making a new connection
	 * for each chunk.
	 * The data is not set line-by-line or broken in chunks on newline 
	 * boundaries it is send in fixed size blocks. The API will manage 
	 * reconstructing the records from the chunks.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param inputStream The data to write to the web service
	 * @return true if successful else false
	 * @throws IOException 
	 * @see #streamingUpload(String, String, InputStream, boolean)
	 */
	public boolean chunkedUpload(String baseUrl, String jobId,
			InputStream inputStream) 
	throws IOException
	{
		String postUrl = baseUrl + "/data/" + jobId; 	
		s_Logger.info("Uploading chunked data to " + postUrl);
		
		final int BUFF_SIZE = 4096 * 1024;
		byte [] buffer = new byte[BUFF_SIZE];
		int read = 0;
		int uploadCount = 0;
		while ((read = inputStream.read(buffer)) > -1)
		{
			ByteArrayEntity entity = new ByteArrayEntity(buffer, 0, read);
			entity.setContentType("application/octet-stream");
			
			s_Logger.info("Upload " + ++uploadCount);			
			
			HttpPost post = new HttpPost(postUrl);			
			post.setEntity(entity);
			try (CloseableHttpResponse response = m_HttpClient.execute(post))
			{

				String content = EntityUtils.toString(response.getEntity());

				if (response.getStatusLine().getStatusCode() != 202)
				{
					String msg = String.format(
							"Upload of chunk %d failed, status code = %d. "
							+ "Returned content: %s", 
							uploadCount, response.getStatusLine().getStatusCode(),
							content);
					
					s_Logger.error(msg);
					
					m_LastError = m_JsonMapper.readValue(content, 
							new TypeReference<ApiError>() {} );
					
					return false;
				}
			}
		}
			
		m_LastError = null;
		return true;
	}
	
	/**
	 * Stream data from <code>inputStream</code> to the service.
	 * This is different to {@link #chunkedUpload(String, String, InputStream)}
	 * in that the entire stream is read and uploading at once without breaking
	 * the connection.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param inputStream The data to write to the web service
	 * @param compressed Is the data gzipped compressed?
	 * @return True if successful
	 * @throws IOException
	 * @throws InterruptedException
	 * @see #chunkedUpload(String, String, InputStream)
	 */
	public boolean streamingUpload(String baseUrl, String jobId,
			InputStream inputStream, boolean compressed) 
	throws IOException
	{
		String postUrl = baseUrl + "/data/" + jobId;	
		s_Logger.info("Uploading data to " + postUrl);

		InputStreamEntity entity = new InputStreamEntity(inputStream);
		entity.setContentType("application/octet-stream");
		entity.setChunked(true);
	
		HttpPost post = new HttpPost(postUrl);	
		if (compressed)
		{
			post.addHeader("Content-Encoding", "gzip");
		}
		post.setEntity(entity);
		
        try (CloseableHttpResponse response = m_HttpClient.execute(post)) 
        {
        	String content = EntityUtils.toString(response.getEntity());

        	if (response.getStatusLine().getStatusCode() != 202)
			{
				String msg = String.format(
						"Streaming upload failed, status code = %d. "
						+ "Returned content: %s", 
						response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				if (content.isEmpty() == false)
				{
					m_LastError = m_JsonMapper.readValue(content, 
							new TypeReference<ApiError>() {} );
				}
				
				return false;
			}
			
			return true;
        } 
	}
	
	/**
	 * Finish the job after all the data has been uploaded
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @return True if successful 
	 * @throws IOException
	 */
	public boolean closeJob(String baseUrl, String jobId)
	throws IOException
	{
		// Send finish message
		String closeUrl = baseUrl + "/data/" + jobId + "/close";	
		s_Logger.info("Closing job " + closeUrl);
		
		HttpPost post = new HttpPost(closeUrl);		
		try (CloseableHttpResponse response = m_HttpClient.execute(post))
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() != 202)
			{
				String msg = String.format(
						"Error closing job %s, status code = %d. "
						+ "Returned content: %s",
						jobId,
						response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
				
				return false;
			}			
		}
		
		return true;
	}
	
	
	/**
	 * Get the list of detectors used a in particular job.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * 
	 * @return A {@link Pagination} object containing a list of 
	 * {@link Detector detectors}
	 * @throws IOException 
	 */
	public Pagination<Detector> getDetectors(String baseUrl, String jobId) 
	throws IOException 
	{
		String url = baseUrl + "/detectors/" + jobId;
		s_Logger.info("GET detectors " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			String content = EntityUtils.toString(response.getEntity());
			
			if (response.getStatusLine().getStatusCode() == 200)
			{
				Pagination<Detector> docs = m_JsonMapper.readValue(content, 
						new TypeReference<Pagination<Detector>>() {} );
				return docs;
			}
			else
			{
				String msg = String.format(
						"Error getting detectors for job %s, status code = %d. "
						+ "Returned content: %s",
						jobId,
						response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}
		}
		
		// else return empty page
		Pagination<Detector> page = new Pagination<>();
		page.setDocuments(Collections.<Detector>emptyList());
		return page;
	}
	
	
	/**
	 * Get the bucket results for a particular job.
	 * Calls {@link #getBuckets(String, String, boolean, Long, Long)} with the 
	 * skip and take parameters set to <code>null</code>
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param expand If true return the anomaly records for the bucket
	 * 
	 * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
	 * @throws IOException 
	 */
	public Pagination<Bucket> getBuckets(String baseUrl, String jobId, 
			boolean expand) 
	throws IOException 
	{
		return getBuckets(baseUrl, jobId, expand, null, null);
	}
			
	/**
	 * Get the bucket results for a particular job with paging parameters.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param expand If true include the anomaly records for the bucket
	 * @param skip The number of buckets to skip 
	 * @param take The max number of buckets to request. 
	 * 
	 * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
	 * @throws IOException 
	 */
	public Pagination<Bucket> getBuckets(String baseUrl, String jobId,
			boolean expand, Long skip, Long take) 
	throws IOException
	{
		return this.<String>getBuckets(baseUrl, jobId, expand, skip, take, null, null);
	}
	
	/**
	 * Get the bucket results filtered between the start and end dates.
	 * The arguments are optional only one of start/end needs be set
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param expand If true include the anomaly records for the bucket
	 * @param skip The number of buckets to skip. If <code>null</code> then ignored 
	 * @param take The max number of buckets to request. If <code>null</code> then ignored 
	 * @param start The start date filter as either a Long (seconds from epoch)
	 * or an ISO 8601 date String. If <code>null</code> then ignored
	 * @param end The end date filter as either a Long (seconds from epoch)
	 * or an ISO 8601 date String. If <code>null</code> then ignored
	 * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
	 * @throws IOException
	 */
	public <T> Pagination<Bucket> getBuckets(String baseUrl, String jobId, 
			boolean expand, Long skip, Long take, T start, T end) 
	throws IOException
	{
		String url = baseUrl + "/results/" + jobId ;
		char queryChar = '?';
		if (expand)
		{
			url += queryChar + "expand=true";
			queryChar = '&';
		}		
		if (skip != null)
		{
			url += queryChar + "skip=" + skip;
			queryChar = '&';
		}
		if (take != null)
		{
			url += queryChar + "take=" + take;
			queryChar = '&';
		}
		if (start != null)
		{
			url += queryChar + "start=" + URLEncoder.encode(start.toString(), "UTF-8");
			queryChar = '&';
		}
		if (end != null)
		{
			url += queryChar + "end=" + URLEncoder.encode(end.toString(), "UTF-8");
			queryChar = '&';
		}
		
		s_Logger.info("GET buckets " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		CloseableHttpResponse response = m_HttpClient.execute(get);
		try
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 200)
			{
				Pagination<Bucket> docs = m_JsonMapper.readValue(content, 
						new TypeReference<Pagination<Bucket>>() {} );
				return docs;
			}
			else
			{
				String msg = String.format(
						"Error getting buckets for job %s, status code = %d. "
						+ "Returned content: %s",
						jobId,
						response.getStatusLine().getStatusCode(),
						content);
				
				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}
		}
		finally 
		{
			response.close();
		}	
		
		// else return empty page
		Pagination<Bucket> page = new Pagination<>();
		page.setDocuments(Collections.<Bucket>emptyList());
		return page;
	}
		
	/**
	 * Get a single bucket for a particular job and bucket Id
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param bucketId The bucket to get
	 * @param expand If true include the anomaly records for the bucket
	 * @return A {@link SingleDocument} object containing the requested 
	 * {@link Bucket} or an empty {@link SingleDocument} if it does not exist 
	 * @throws IOException 
	 */
	public SingleDocument<Bucket> getBucket(String baseUrl, String jobId, 
			String bucketId, boolean expand) 
	throws IOException
	{
		String url = baseUrl + "/results/" + jobId + "/" + bucketId;
		if (expand)
		{
			url += "?expand=true";
		}
		
		s_Logger.info("GET bucket " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		CloseableHttpResponse response = m_HttpClient.execute(get);
		try
		{
			String content = EntityUtils.toString(response.getEntity());
			
			if (response.getStatusLine().getStatusCode() == 200)
			{
				
				
				SingleDocument<Bucket> docs = m_JsonMapper.readValue(content, 
						new TypeReference<SingleDocument<Bucket>>() {} );
				return docs;
			}
			else
			{
				String msg = String.format(
						"Error getting single bucket for job %s and bucket %s, "
						+ "status code = %d. Returned content: %s",
						jobId, bucketId,
						response.getStatusLine().getStatusCode(),
						content);

				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}
		}
		finally 
		{
			response.close();
		}	
		
		// else return empty doc
		SingleDocument<Bucket> doc = new SingleDocument<>();
		return doc;
	}
	
	/**
	 * Get the anomaly records for the bucket. Records from all detectors are
	 * returned.
	 * This is similar to calling {@linkplain #getBucket(String, String, String, boolean)}
	 * with <code>expand=true</code> except the bucket isn't returned only the
	 * anomaly records.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param bucketId The bucket to get the anomaly records from
	 * @return A {@link Pagination} object containing a Map<String,Object>
	 * @throws IOException 
	 */
	public Pagination<Map<String,Object>> getRecords(String baseUrl, 
			String jobId, String bucketId)
	throws IOException
	{
		String url = baseUrl + "/results/" + jobId + "/" + bucketId + "/records";
		s_Logger.info("GET records " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		CloseableHttpResponse response = m_HttpClient.execute(get);
		try
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 200)
			{
				Pagination<Map<String,Object>> docs = m_JsonMapper.readValue(content, 
						new TypeReference<Pagination<Map<String,Object>>>() {} );
				return docs;
			}
			else
			{
				String msg = String.format(
						"Error getting records for job %s and bucket %s, "
						+ "status code = %d. Returned content: %s",
						jobId, bucketId,
						response.getStatusLine().getStatusCode(),
						content);

				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}			
		}
		finally 
		{
			response.close();
		}	
		
		// else return empty page
		Pagination<Map<String,Object>> page = new Pagination<>();
		return page;
	}
	
	/**
	 * Get the anomaly records from a particular anomaly detector 
	 * in the given bucket.
	 * This is similar to {@linkplain #getRecords(String, String, String)}  
	 * but only the records produced by the detetor <code>detectorId</code> 
	 * are returned.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param bucketId The bucket to get the anomaly records from
	 * @param detectorId Only get anomaly records from this detector
	 * @return A {@link Pagination} object containing a Map<String,Object>
	 * @throws IOException 
	 */
	public Pagination<Map<String,Object>> getRecordByDetector(String baseUrl, 
			String jobId, String bucketId, String detectorId)
	throws IOException
	{
		String url = baseUrl + "/results/" + jobId + "/" + bucketId + "/records/"
				+ detectorId;
		s_Logger.info("GET records by detectors " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		CloseableHttpResponse response = m_HttpClient.execute(get);
		try
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 200)
			{
				Pagination<Map<String,Object>> docs = m_JsonMapper.readValue(content, 
						new TypeReference<Pagination<Map<String,Object>>>() {} );
				return docs;
			}
			else
			{
				String msg = String.format(
						"Error getting records for detector %s, job %s and bucket %s, "
						+ "status code = %d. Returned content: %s",
						detectorId, jobId, bucketId,
						response.getStatusLine().getStatusCode(),
						content);

				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}			
		}
		finally 
		{
			response.close();
		}	
		
		// else return empty page
		Pagination<Map<String,Object>> page = new Pagination<>();
		return page;
	}


	/**
	 * Get the last 10 lines of the job's latest log file
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @return The last 10 lines of the last log file
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String tailLog(String baseUrl, String jobId) 
	throws ClientProtocolException, IOException
	{
		return tailLog(baseUrl, jobId, 10);
	}
	
	/**
	 * Tails the last <code>lineCount</code> lines from the job's
	 * last log file. This tails the autodetect process log file. 
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param lineCount The number of lines to return 
	 * @return The last <code>lineCount</code> lines of the log file
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String tailLog(String baseUrl, String jobId, int lineCount) 
	throws ClientProtocolException, IOException
	{
		String url = String.format("%s/logs/%s/tail?lines=%d", 
				baseUrl, jobId, lineCount);
		
		s_Logger.info("GET tail log " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 200)
			{
				return content;
			}
			else
			{
				String msg = String.format(
						"Error tailing logs for job %s, status code = %d. "
								+ "Returned content: %s",
								jobId,
								response.getStatusLine().getStatusCode(),
								content);

				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
				
				return "";
			}			
		}		
	}
	
	
	/**
	 * Tails the last <code>lineCount</code> lines from the named log file.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param logfileName the name of the log file without the '.log' suffix. 
	 * @param lineCount The number of lines to return 
	 * @return The last <code>lineCount</code> lines of the log file
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String tailLog(String baseUrl, String jobId, String filename,
			int lineCount) 
	throws ClientProtocolException, IOException
	{
		String url = String.format("%s/logs/%s/%s/tail?lines=%d", 
				baseUrl, jobId, filename, lineCount);
		
		s_Logger.info("GET tail log " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 200)
			{
				return content;
			}
			else
			{
				String msg = String.format(
						"Error tailing logs for job %s, status code = %d. "
								+ "Returned content: %s",
								jobId,
								response.getStatusLine().getStatusCode(),
								content);

				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
				
				return "";
			}			
		}		
	}
	
	
	/**
	 * Download the specified log file for the job.
	 * The autodetect process writes a log file named after with the job id
	 * <job_id>.log while the Java component logs to engine_api.log.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @param logfileName the name of the log file without the '.log' suffix. 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String downloadLog(String baseUrl, String jobId, String logfileName) 
	throws ClientProtocolException, IOException
	{
		String url = String.format("%s/logs/%s/%s", 
				baseUrl, jobId, logfileName);
		
		s_Logger.info("GET log file " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			String content = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 200)
			{
				return content;
			}
			else
			{
				String msg = String.format(
						"Error downloading log file for job %s, status code = %d. "
								+ "Returned content: %s",
								jobId,
								response.getStatusLine().getStatusCode(),
								content);

				s_Logger.error(msg);
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
				
				return "";
			}			
		}		
	}
	
	
	/**
	 * Download all the log files for the given job.
	 * 
	 * @param baseUrl The base URL for the REST API 
	 * e.g <code>http://localhost:8080/engine/version/</code>
	 * @param jobId The Job's unique Id
	 * @return A ZipInputStream for the log files. If the inputstream is
	 * empty an error may have occurred
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public ZipInputStream downloadAllLogs(String baseUrl, String jobId) 
	throws ClientProtocolException, IOException
	{
		String url = String.format("%s/logs/%s", baseUrl, jobId);
		
		s_Logger.info("GET download logs " + url);
		
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			if (response.getStatusLine().getStatusCode() == 200)
			{
				 return new ZipInputStream(response.getEntity().getContent());
			}
			else
			{
				String content = EntityUtils.toString(response.getEntity());
				String msg = String.format(
						"Error downloading log files for job %s, status code = %d. "
								+ "Returned content: %s",
								jobId,
								response.getStatusLine().getStatusCode(),
								content);

				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
				
				// return an empty stream
				return new ZipInputStream(new ByteArrayInputStream(new byte[0]));
			}			
		}		
	}	
	
	
	/**
	 * A generic HTTP GET to any Url. The result is converted from Json to 
	 * the type referenced in <code>typeRef</code>. A <code>TypeReference</code> 
	 * has to be used to preserve the generic type information that is usually 
	 * lost in due to erasure.<br/>
	 * This method is useful for paging through a set of results in a 
	 * {@link Pagination} object.
	 *  
	 * @param fullUrl
	 * @param typeRef
	 * @return A new T or <code>null</code>
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> T get(String fullUrl, TypeReference<T> typeRef) 
	throws JsonParseException, JsonMappingException, IOException
	{
		s_Logger.info("GET " + fullUrl + ". Return type = " 
					+ typeRef.getType().toString());
		
		
		HttpGet get = new HttpGet(fullUrl);
		get.addHeader("Content-Type", "application/json");
		
		try (CloseableHttpResponse response = m_HttpClient.execute(get))
		{
			HttpEntity entity = response.getEntity();				
			String content = EntityUtils.toString(entity);

			if (response.getStatusLine().getStatusCode() == 200)
			{
				T docs = m_JsonMapper.readValue(content, typeRef);
				return docs;
			}
			else
			{
				String msg = String.format(
						"GET returned status code %d for url %s. "
						+ "Returned content = %s",
						response.getStatusLine().getStatusCode(), fullUrl,
						content);

				s_Logger.error(msg);
				
				m_LastError = m_JsonMapper.readValue(content, 
						new TypeReference<ApiError>() {} );
			}				
		}

		return null;		
	}
	
	
	/**
	 * Get the last error message
	 * @return The error or null if no errors have occurred 
	 */
	public ApiError getLastError()
	{
		return m_LastError;
	}
}