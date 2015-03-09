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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import org.apache.http.client.methods.HttpUriRequest;
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
import com.prelert.job.alert.Alert;
import com.prelert.rs.data.AnomalyRecord;
import com.prelert.rs.data.ApiError;
import com.prelert.rs.data.Bucket;
import com.prelert.rs.data.Pagination;
import com.prelert.rs.data.SingleDocument;

/**
 * A Http Client for the Prelert Engine RESTful API.
 *
 * <br/>
 * Contains methods to create jobs, list jobs, upload data and query results.
 * <br/>
 * Implements closeable so it can be used in a try-with-resource statement
 */
public class EngineApiClient implements Closeable
{
    private static final Logger LOGGER = Logger.getLogger(EngineApiClient.class);

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
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @return The {@link Pagination} object containing a list of {@link JobDetails jobs}
     * @throws IOException
     */
    public Pagination<JobDetails> getJobs(String baseUrl)
    throws IOException
    {
        String url = baseUrl + "/jobs";
        LOGGER.debug("GET jobs: " + url);

        Pagination<JobDetails> page = this.get(url,
                new TypeReference<Pagination<JobDetails>>() {});

        if (page == null)
        {
            page = new Pagination<>();
            page.setDocuments(Collections.<JobDetails>emptyList());
        }

        return page;
    }

    /**
     * Get the individual job on the provided URL
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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
        LOGGER.debug("GET job: " + url);

        SingleDocument<JobDetails> doc = this.get(url,
                new TypeReference<SingleDocument<JobDetails>>() {});

        if (doc == null)
        {
            doc = new SingleDocument<>();
        }
        return doc;
    }


    /**
     * Create a new Job from the <code>JobConfiguration</code> object.
     * <br/>
     * Internally this function converts <code>jobConfig</code> to a JSON
     * string and calls {@link #createJob(String, String)}
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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
        LOGGER.debug("Create job: " + url);

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
                    LOGGER.error("Job created but no 'id' field in returned content");
                    LOGGER.error("Response Content = " + content);
                }
            }
            else
            {
                String msg = String.format(
                        "Error creating job status code = %d. "
                        + "Returned content: %s",
                        response.getStatusLine().getStatusCode(),
                        content);

                LOGGER.error(msg);

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
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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
        LOGGER.debug("PUT job description: " + url);

        HttpPut put = new HttpPut(url);
        StringEntity entity = new StringEntity(description);
        put.setEntity(entity);

        return executeRequest(put, "putting job description");
    }

    /**
     * Executes an HTTP request and checks if the response was OK. If not, it logs the error.
     *
     * @return True if response was OK, otherwise false.
     */
    private boolean executeRequest(HttpUriRequest httpRequest, String activityDescription)
            throws IOException, JsonParseException, JsonMappingException
    {
        try (CloseableHttpResponse response = m_HttpClient.execute(httpRequest))
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
                        "Error %s. Status code = %d, "
                        + "Returned content: %s",
                        activityDescription,
                        response.getStatusLine().getStatusCode(),
                        content);

                LOGGER.error(msg);

                m_LastError = m_JsonMapper.readValue(content,
                        new TypeReference<ApiError>() {} );

                return false;
            }
        }
    }

    /**
     * Delete an individual job
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @return If the job existed and was deleted return true else false
     * @throws IOException, ClientProtocolException
     */
    public boolean deleteJob(String baseUrl, String jobId)
    throws ClientProtocolException, IOException
    {
        String url = baseUrl + "/jobs/" + jobId;
        LOGGER.debug("DELETE job: " + url);

        HttpDelete delete = new HttpDelete(url);

        return executeRequest(delete, "deleting job");
    }

    /**
     * Read the input stream in 4Mb chunks and upload making a new connection
     * for each chunk.
     * The data is not set line-by-line or broken in chunks on newline
     * boundaries it is send in fixed size blocks. The API will manage
     * reconstructing the records from the chunks.
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>>
     * @param jobId The Job's unique Id
     * @param inputStream The data to write to the web service
     * @return True
     * @throws IOException
     * @see #streamingUpload(String, String, InputStream, boolean)
     */
    public boolean chunkedUpload(String baseUrl, String jobId,
            InputStream inputStream)
    throws IOException
    {
        String postUrl = baseUrl + "/data/" + jobId;
        LOGGER.debug("Uploading chunked data to " + postUrl);

        final int BUFF_SIZE = 4096 * 1024;
        byte [] buffer = new byte[BUFF_SIZE];
        int read = 0;
        int uploadCount = 0;
        while ((read = inputStream.read(buffer)) > -1)
        {
            ByteArrayEntity entity = new ByteArrayEntity(buffer, 0, read);
            entity.setContentType("application/octet-stream");

            LOGGER.info("Upload " + ++uploadCount);

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

                    LOGGER.error(msg);

                    m_LastError = m_JsonMapper.readValue(content,
                            new TypeReference<ApiError>() {} );
                }
                else
                {
                    m_LastError = null;
                }
            }
        }

        return true;
    }

    /**
     * Stream data from <code>inputStream</code> to the service.
     * This is different to {@link #chunkedUpload(String, String, InputStream)}
     * in that the entire stream is read and uploading at once without breaking
     * the connection.
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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
        LOGGER.debug("Uploading data to " + postUrl);

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

                LOGGER.error(msg);

                if (content.isEmpty() == false)
                {
                    m_LastError = m_JsonMapper.readValue(content,
                            new TypeReference<ApiError>() {} );
                }
                else
                {
                    m_LastError = null;
                }

                return false;
            }

            return true;
        }
    }


    /**
     * Upload the contents of <code>dataFile</code> to the server.
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's Id
     * @param dataFile Should match the data configuration format of the job
     * @param compressed Is the data gzipped compressed?
     * @return True if successful
     * @throws IOException
     */
    public boolean fileUpload(String baseUrl, String jobId, File dataFile, boolean compressed)
    throws IOException
    {
        FileInputStream stream = new FileInputStream(dataFile);

        return streamingUpload(baseUrl, jobId, stream, compressed);
    }


    /**
     * Flush the job, ensuring that no previously uploaded data is waiting in
     * buffers.
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param calcInterim Should interim results for the most recent bucket be
     * calculated based on the partial data uploaded for it so far?
     * @return True if successful
     * @throws IOException
     */
    public boolean flushJob(String baseUrl, String jobId, boolean calcInterim)
    throws IOException
    {
        // Send flush message
        String flushUrl = baseUrl + "/data/" + jobId + "/flush";
        if (calcInterim)
        {
            flushUrl += "?calcInterim=true";
        }
        LOGGER.debug("Flushing job " + flushUrl);

        HttpPost post = new HttpPost(flushUrl);
        try (CloseableHttpResponse response = m_HttpClient.execute(post))
        {
            String content = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200)
            {
                String msg = String.format(
                        "Error flushing job %s, status code = %d. "
                        + "Returned content: %s",
                        jobId,
                        response.getStatusLine().getStatusCode(),
                        content);

                LOGGER.error(msg);

                m_LastError = m_JsonMapper.readValue(content,
                        new TypeReference<ApiError>() {} );

                return false;
            }
            else
            {
                m_LastError = null;
            }
        }

        return true;
    }


    /**
     * Finish the job after all the data has been uploaded
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @return True if successful
     * @throws IOException
     */
    public boolean closeJob(String baseUrl, String jobId)
    throws IOException
    {
        // Send finish message
        String closeUrl = baseUrl + "/data/" + jobId + "/close";
        LOGGER.debug("Closing job " + closeUrl);

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

                LOGGER.error(msg);

                m_LastError = m_JsonMapper.readValue(content,
                        new TypeReference<ApiError>() {} );

                return false;
            }
            else
            {
                m_LastError = null;
            }
        }

        return true;
    }


    /**
     * Get the bucket results for a particular job.
     * Calls {@link #getBuckets(String, String, boolean, Long, Long, Double, Double)}
     * with the skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public Pagination<Bucket> getBuckets(String baseUrl, String jobId, boolean expand)
    throws IOException
    {
        return getBuckets(baseUrl, jobId, expand, false, null, null, null, null);
    }

    /**
     * Get the bucket results for a particular job.
     * Calls {@link #getBuckets(String, String, boolean, Long, Long, Double, Double)}
     * with the skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     * @param includeInterim Include interim results
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public Pagination<Bucket> getBuckets(String baseUrl, String jobId, boolean expand,
            boolean includeInterim)
    throws IOException
    {
        return getBuckets(baseUrl, jobId, expand, includeInterim, null, null, null, null);
    }

    /**
     * Get the bucket results for a particular job and optionally filter by
     * anomaly or unusual score.
     *
     * Calls {@link #getBuckets(String, String, boolean, Long, Long, , Double, Double)}
     * with the skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     * @param anomalyScoreThreshold Return only buckets with an anomalyScore >=
     * this value. If <code>null</code> then ignored
     * @param normalizedProbabilityThreshold Return only buckets with a maxNormalizedProbability >=
     * this value. If <code>null</code> then ignored
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public Pagination<Bucket> getBuckets(String baseUrl, String jobId,
            boolean expand, Double anomalyScoreThreshold, Double normalizedProbabilityThreshold)
    throws IOException
    {
        return getBuckets(baseUrl, jobId, expand, false, null, null,
                anomalyScoreThreshold, normalizedProbabilityThreshold);
    }

    /**
     * Get the bucket results for a particular job and optionally filter by
     * anomaly or unusual score.
     *
     * Calls {@link #getBuckets(String, String, boolean, Long, Long, , Double, Double)}
     * with the skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     * @param anomalyScoreThreshold Return only buckets with an anomalyScore >=
     * this value. If <code>null</code> then ignored
     * @param normalizedProbabilityThreshold Return only buckets with a maxNormalizedProbability >=
     * this value. If <code>null</code> then ignored
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public Pagination<Bucket> getBuckets(String baseUrl, String jobId, boolean expand,
            boolean includeInterim, Double anomalyScoreThreshold, Double normalizedProbabilityThreshold)
    throws IOException
    {
        return getBuckets(baseUrl, jobId, expand, includeInterim, null, null,
                anomalyScoreThreshold, normalizedProbabilityThreshold);
    }

    /**
     * Get the bucket results for a particular job with paging parameters.
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     * @param skip The number of buckets to skip
     * @param take The max number of buckets to request.
     * @param anomalyScoreThreshold Return only buckets with an anomalyScore >=
     * this value. If <code>null</code> then ignored
     * @param normalizedProbabilityThreshold Return only buckets with a maxNormalizedProbability >=
     * this value. If <code>null</code> then ignored
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public Pagination<Bucket> getBuckets(String baseUrl, String jobId,
            boolean expand, Long skip, Long take,
            Double anomalyScoreThreshold, Double normalizedProbabilityThreshold)
    throws IOException
    {
        return this.<String>getBuckets(baseUrl, jobId, expand, false, skip, take,
                null, null, anomalyScoreThreshold, normalizedProbabilityThreshold);
    }

    /**
     * Get the bucket results for a particular job with paging parameters.
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     * @param includeInterim Include interim results
     * @param skip The number of buckets to skip
     * @param take The max number of buckets to request.
     * @param anomalyScoreThreshold Return only buckets with an anomalyScore >=
     * this value. If <code>null</code> then ignored
     * @param normalizedProbabilityThreshold Return only buckets with a maxNormalizedProbability >=
     * this value. If <code>null</code> then ignored
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public Pagination<Bucket> getBuckets(String baseUrl, String jobId,
            boolean expand, boolean includeInterim, Long skip, Long take,
            Double anomalyScoreThreshold, Double normalizedProbabilityThreshold)
    throws IOException
    {
        return this.<String>getBuckets(baseUrl, jobId, expand, includeInterim, skip, take,
                null, null, anomalyScoreThreshold, normalizedProbabilityThreshold);
    }

    /**
     * Get the bucket results filtered between the start and end dates.
     * The arguments are optional only one of start/end needs be set
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     * @param skip The number of buckets to skip. If <code>null</code> then ignored
     * @param take The max number of buckets to request. If <code>null</code> then ignored
     * @param start The start date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param end The end date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param anomalyScoreThreshold Return only buckets with an anomalyScore >=
     * this value. If <code>null</code> then ignored
     * @param normalizedProbabilityThreshold Return only buckets with a maxNormalizedProbability >=
     * this value. If <code>null</code> then ignored
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public <T> Pagination<Bucket> getBuckets(String baseUrl, String jobId,
            boolean expand, Long skip, Long take, T start, T end,
            Double anomalyScoreThreshold, Double normalizedProbabilityThreshold)
    throws IOException
    {
        return this.getBuckets(baseUrl, jobId, expand, false, skip, take,
                start, end, anomalyScoreThreshold, normalizedProbabilityThreshold);
    }

    /**
     * Get the bucket results filtered between the start and end dates.
     * The arguments are optional only one of start/end needs be set
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param expand If true include the anomaly records for the bucket
     * @param includeInterim Include interim results
     * @param skip The number of buckets to skip. If <code>null</code> then ignored
     * @param take The max number of buckets to request. If <code>null</code> then ignored
     * @param start The start date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param end The end date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param anomalyScoreThreshold Return only buckets with an anomalyScore >=
     * this value. If <code>null</code> then ignored
     * @param normalizedProbabilityThreshold Return only buckets with a maxNormalizedProbability >=
     * this value. If <code>null</code> then ignored
     *
     * @return A {@link Pagination} object containing a list of {@link Bucket buckets}
     * @throws IOException
     */
    public <T> Pagination<Bucket> getBuckets(String baseUrl, String jobId,
            boolean expand, boolean includeInterim,
            Long skip, Long take, T start, T end,
            Double anomalyScoreThreshold, Double normalizedProbabilityThreshold)
    throws IOException
    {
        String url = baseUrl + "/results/" + jobId + "/buckets/";
        char queryChar = '?';
        if (expand)
        {
            url += queryChar + "expand=true";
            queryChar = '&';
        }
        if (includeInterim)
        {
            url += queryChar + "includeInterim=true";
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
        if (anomalyScoreThreshold != null)
        {
            url += queryChar + "anomalyScore=" + anomalyScoreThreshold;
            queryChar = '&';
        }
        if (normalizedProbabilityThreshold != null)
        {
            url += queryChar + "maxNormalizedProbability=" + normalizedProbabilityThreshold;
            queryChar = '&';
        }

        LOGGER.debug("GET buckets " + url);

        Pagination<Bucket> page = this.get(url,
                new TypeReference<Pagination<Bucket>>() {});

        // else return empty page
        if (page == null)
        {
            page = new Pagination<>();
            page.setDocuments(Collections.<Bucket>emptyList());
        }

        return page;
    }


    /**
     * Get a single bucket for a particular job and bucket Id
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param bucketId The bucket to get
     * @param expand If true include the anomaly records for the bucket
     *
     * @return A {@link SingleDocument} object containing the requested
     * {@link Bucket} or an empty {@link SingleDocument} if it does not exist
     * @throws IOException
     */
    public SingleDocument<Bucket> getBucket(String baseUrl, String jobId,
            String bucketId, boolean expand)
    throws JsonMappingException, IOException
    {
        return getBucket(baseUrl, jobId, bucketId, expand, false);
    }


    /**
     * Get a single bucket for a particular job and bucket Id
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param bucketId The bucket to get
     * @param expand If true include the anomaly records for the bucket
     * @param includeInterim Include interim results
     *
     * @return A {@link SingleDocument} object containing the requested
     * {@link Bucket} or an empty {@link SingleDocument} if it does not exist
     * @throws IOException
     */
    public SingleDocument<Bucket> getBucket(String baseUrl, String jobId,
            String bucketId, boolean expand, boolean includeInterim)
    throws JsonMappingException, IOException
    {
        String url = baseUrl + "/results/" + jobId + "/buckets/" + bucketId;
        char queryChar = '?';
        if (expand)
        {
            url += queryChar + "expand=true";
            queryChar = '&';
        }
        if (includeInterim)
        {
            url += queryChar + "includeInterim=true";
            queryChar = '&';
        }

        LOGGER.debug("GET bucket " + url);

        SingleDocument<Bucket> doc = this.get(url,
                new TypeReference<SingleDocument<Bucket>>() {});

        // else return empty doc
        if (doc == null)
        {
            doc = new SingleDocument<>();
        }
        return doc;
    }


    /**
     * Get the anomaly records for the job. The records aren't organised
     * by bucket
     *
     * Calls {@link #getRecords(String, String, Long, Long)} with the
     * skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     *
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId)
    throws IOException
    {
        return this.<String>getRecords(baseUrl, jobId, null, null, null, null,
                false, null, null, null, null);
    }

    /**
     * Get the anomaly records for the job. The records aren't organised
     * by bucket
     *
     * Calls {@link #getRecords(String, String, Long, Long)} with the
     * skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param includeInterim Include interim results
     *
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId,
            boolean includeInterim)
    throws IOException
    {
        return this.<String>getRecords(baseUrl, jobId, null, null, null, null,
                includeInterim, null, null, null, null);
    }

    /**
     * Get the anomaly records for the job with skip and take parameters.
     * The records aren't organised by bucket
     *
     * Calls {@link #getRecords(String, String, Long, Long)} with the
     * skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param skip The number of records to skip
     * @param take The max number of records to request.
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId,
                    Long skip, Long take)
    throws IOException
    {
        return this.<String>getRecords(baseUrl, jobId, skip, take, null, null,
                false, null, null, null, null);
    }

    /**
     * Get the anomaly records for the job with skip and take parameters.
     * The records aren't organised by bucket
     *
     * Calls {@link #getRecords(String, String, Long, Long)} with the
     * skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param skip The number of records to skip
     * @param take The max number of records to request.
     * @param includeInterim Include interim results
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId,
                    Long skip, Long take, boolean includeInterim)
    throws IOException
    {
        return this.<String>getRecords(baseUrl, jobId, skip, take, null, null,
                includeInterim, null, null, null, null);
    }


    /**
     * Get the anomaly records for the job with skip and take parameters.
     * The records aren't organised by bucket
     *
     * Calls {@link #getRecords(String, String, Long, Long)} with the
     * skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param skip The number of records to skip
     * @param take The max number of records to request.
     * @param start The start date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param end The end date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     *
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public <T> Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId,
                    Long skip, Long take, T start, T end)
    throws IOException
    {
        return this.<T>getRecords(baseUrl, jobId, skip, take, start, end, false,
                null, null, null, null);
    }

    /**
     * Get the anomaly records for the job with skip and take parameters.
     * The records aren't organised by bucket
     *
     * Calls {@link #getRecords(String, String, Long, Long)} with the
     * skip and take parameters set to <code>null</code>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param skip The number of records to skip
     * @param take The max number of records to request.
     * @param start The start date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param end The end date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param includeInterim Include interim results
     *
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public <T> Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId,
                    Long skip, Long take, T start, T end, boolean includeInterim)
    throws IOException
    {
        return this.<T>getRecords(baseUrl, jobId, skip, take, start, end,
                includeInterim, null, null, null, null);
    }


    /**
     * Get the anomaly records for the job between the start and
     * end dates with skip and take parameters sorted by field
     * and optionally filtered by score. Only one of
     * <code>anomalyScoreFilterValue</code> and <code>normalizedProbabilityFilterValue</code>
     * should be specified it is an error if both are set
     *
     * The records aren't grouped by bucket
     *
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param skip The number of records to skip
     * @param take The max number of records to request.
     * @param start The start date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param end The end date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param sortField The field to sort the results by, ignored if <code>null</code>
     * @param sortDescending If sort_field is not <code>null</code> then sort
     * records in descending order if true else sort ascending
     * @param anomalyScoreFilterValue If not <code>null</code> return only the
     * records with an anomalyScore >= anomalyScoreFilterValue
     * @param normalizedProbabilityFilterValue If not <code>null</code> return only the
     * records with a normalizedProbability >= normalizedProbabilityFilterValue
     *
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public <T> Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId,
            Long skip, Long take, T start, T end,
            String sortField, Boolean sortDescending,
            Double anomalyScoreFilterValue, Double normalizedProbabilityFilterValue)
    throws IOException
    {
        return this.<T>getRecords(baseUrl, jobId, skip, take, start, end,
                false, sortField, sortDescending,
                anomalyScoreFilterValue, normalizedProbabilityFilterValue);
    }

    /**
     * Get the anomaly records for the job between the start and
     * end dates with skip and take parameters sorted by field
     * and optionally filtered by score. Only one of
     * <code>anomalyScoreFilterValue</code> and <code>normalizedProbabilityFilterValue</code>
     * should be specified it is an error if both are set
     *
     * The records aren't grouped by bucket
     *
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param skip The number of records to skip
     * @param take The max number of records to request.
     * @param start The start date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param end The end date filter as either a Long (seconds from epoch)
     * or an ISO 8601 date String. If <code>null</code> then ignored
     * @param includeInterim Include interim results
     * @param sortField The field to sort the results by, ignored if <code>null</code>
     * @param sortDescending If sort_field is not <code>null</code> then sort
     * records in descending order if true else sort ascending
     * @param anomalyScoreFilterValue If not <code>null</code> return only the
     * records with an anomalyScore >= anomalyScoreFilterValue
     * @param normalizedProbabilityFilterValue If not <code>null</code> return only the
     * records with a normalizedProbability >= normalizedProbabilityFilterValue
     *
     * @return A {@link Pagination} object containing a list of
     * {@link AnomalyRecord anomaly records}
     * @throws IOException
     */
    public <T> Pagination<AnomalyRecord> getRecords(String baseUrl, String jobId,
            Long skip, Long take, T start, T end, boolean includeInterim,
            String sortField, Boolean sortDescending,
            Double anomalyScoreFilterValue, Double normalizedProbabilityFilterValue)
    throws IOException
    {
        String url = baseUrl + "/results/" + jobId + "/records/";
        char queryChar = '?';

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
        if (includeInterim)
        {
            url += queryChar + "includeInterim=true";
            queryChar = '&';
        }
        if (sortField != null)
        {
            url += queryChar + "sort=" + URLEncoder.encode(sortField, "UTF-8");
            queryChar = '&';
        }
        if (sortDescending != null && sortDescending == false)
        {
            url += queryChar + "desc=false";
            queryChar = '&';
        }
        if (anomalyScoreFilterValue != null)
        {
            url += queryChar + "anomalyScore=" + anomalyScoreFilterValue.toString();
            queryChar = '&';
        }
        if (normalizedProbabilityFilterValue != null)
        {
            url += queryChar + "normalizedProbability=" + normalizedProbabilityFilterValue.toString();
            queryChar = '&';
        }


        LOGGER.debug("GET records " + url);

        Pagination<AnomalyRecord> page = this.get(url,
                new TypeReference<Pagination<AnomalyRecord>>() {});

        if (page == null)
        {
            page = new Pagination<>();
            page.setDocuments(Collections.<AnomalyRecord>emptyList());
        }
        return page;
    }


    /**
     * Long poll an alert from the job. Blocks until the alert occurs or the
     * timeout period expires.
     * If <code>timeout</code> is not null then wait <code>timeout</code> seconds
     *
     * @param baseUrl The base URL for the REST API including version number
     * @param jobId The job id
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param timeout Timeout the request after this many seconds.
     * If <code>null</code> then use the default.
     * @param anomalyScoreThreshold Alert if a record has an anomalyScore threshold
     * >= this value. This should be in the range 0-100, ignored if <code>null</code>.
     * @param maxNormalizedProbability Alert if a bucket's maxNormalizedProbability
     * is >= this value. This should be in the range 0-100, ignored if <code>null</code>.
     *
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public Alert pollJobAlert(String baseUrl, String jobId, Integer timeout,
            Double anomalyScoreThreshold, Double maxNormalizedProbability)
    throws JsonParseException, JsonMappingException, IOException
    {
        String url = baseUrl + "/alerts_longpoll/" + jobId;
        char queryChar = '?';
        if (timeout != null)
        {
            url += "?timeout=" + timeout;
            queryChar = '&';
        }

        if (anomalyScoreThreshold != null)
        {
            url += queryChar + "score=" + anomalyScoreThreshold;
            queryChar = '&';
        }

        if (maxNormalizedProbability != null)
        {
            url += queryChar + "probability=" + maxNormalizedProbability;
        }


        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = m_HttpClient.execute(get);

        try
        {
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);

            if (response.getStatusLine().getStatusCode() == 200)
            {
                Alert alert = m_JsonMapper.readValue(content, Alert.class);
                m_LastError = null;
                return alert;
            }
            else
            {
                String msg = String.format(
                        "long poll alert returned status code %d for job %s. "
                        + "Returned content = %s",
                        response.getStatusLine().getStatusCode(), jobId, content);

                LOGGER.error(msg);

                m_LastError = m_JsonMapper.readValue(content,
                        new TypeReference<ApiError>() {} );
            }
        }
        finally
        {
            response.close();
        }

        return null;
    }


    /**
     * Get the last 10 lines of the job's latest log file
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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

        LOGGER.debug("GET tail log " + url);

        return this.getStringContent(url);
    }


    /**
     * Tails the last <code>lineCount</code> lines from the named log file.
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @param logfileName the name of the log file without the '.log' suffix.
     * @param lineCount The number of lines to return
     * @return The last <code>lineCount</code> lines of the log file
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String tailLog(String baseUrl, String jobId, String logfileName,
            int lineCount)
    throws ClientProtocolException, IOException
    {
        String url = String.format("%s/logs/%s/%s/tail?lines=%d",
                baseUrl, jobId, logfileName, lineCount);

        LOGGER.debug("GET tail log " + url);

        return this.getStringContent(url);
    }

    /**
     * Get content from Url and return as a string
     *
     * @param url
     * @return If status code == 200 return the HTTP response content
     * else return an empty string.
     * @throws IOException
     * @throws ClientProtocolException
     */
    private String getStringContent(String url)
    throws ClientProtocolException, IOException
    {
        HttpGet get = new HttpGet(url);

        try (CloseableHttpResponse response = m_HttpClient.execute(get))
        {
            String content = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == 200)
            {
                m_LastError = null;
                return content;
            }
            else
            {
                String msg = String.format(
                        "Error reading string content. Status code = %d. "
                                + "Returned content: %s",
                                response.getStatusLine().getStatusCode(),
                                content);

                LOGGER.error(msg);

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
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
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

        LOGGER.debug("GET log file " + url);

        return this.getStringContent(url);
    }


    /**
     * Download all the log files for the given job.
     *
     * <b>Important: the caller MUST close the ZipInputStream returned by
     * this method, otherwise all subsequent client/server communications
     * will be blocked.</b>
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v1/</code>
     * @param jobId The Job's unique Id
     * @return A ZipInputStream for the log files. If the inputstream is
     * empty an error may have occurred.  The caller MUST close this
     * ZipInputStream when they have finished with it.
     * @throws ClientProtocolException
     * @throws IOException
     */
    public ZipInputStream downloadAllLogs(String baseUrl, String jobId)
    throws ClientProtocolException, IOException
    {
        String url = String.format("%s/logs/%s", baseUrl, jobId);

        LOGGER.debug("GET download logs " + url);

        HttpGet get = new HttpGet(url);

        CloseableHttpResponse response = m_HttpClient.execute(get);
        try
        {
            if (response.getStatusLine().getStatusCode() == 200)
            {
                ZipInputStream result = new ZipInputStream(response.getEntity().getContent());
                // In this case we DON'T want the response to be automatically
                // closed - the caller MUST close the ZipInputStream when they
                // are finished with it
                m_LastError = null;
                response = null;
                return result;
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

                LOGGER.error(msg);

                m_LastError = m_JsonMapper.readValue(content,
                        new TypeReference<ApiError>() {} );

                // return an empty stream
                return new ZipInputStream(new ByteArrayInputStream(new byte[0]));
            }
        }
        finally
        {
            if (response != null)
            {
                response.close();
            }
        }
    }


    /**
     * A generic HTTP GET to any Url. The result is converted from Json to
     * the type referenced in <code>typeRef</code>. A <code>TypeReference</code>
     * has to be used to preserve the generic type information that is usually
     * lost in due to erasure.
     * <br/>
     * If the response code is 200 or 404 try to parse the returned content
     * into an object of the generic parameter type <code>T</code>.
     * The 404 status code is not considered an error it simply means an
     * empty document was returned by the API.
     * <br/>
     * This method is useful for paging through a set of results via the
     * next or previous page links in a {@link Pagination} object.
     *
     * @param fullUrl
     * @param typeRef
     * @return A new T or <code>null</code>
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @see #get(URI, TypeReference)
     */
    public <T> T get(String fullUrl, TypeReference<T> typeRef)
    throws JsonParseException, JsonMappingException, IOException
    {
        HttpGet get = new HttpGet(fullUrl);
        return get(get,typeRef);
    }

    /**
     * A generic HTTP GET to any Url. The result is converted from Json to
     * the type referenced in <code>typeRef</code>. A <code>TypeReference</code>
     * has to be used to preserve the generic type information that is usually
     * lost in due to erasure.
     * <br>
     * If the response code is 200 or 404 try to parse the returned content
     * into an object of the generic parameter type <code>T</code>.
     * The 404 status code is not considered an error it simply means an
     * empty document was returned by the API.
     * <br>
     * This method is useful for paging through a set of results via the
     * next or previous page links in a {@link Pagination} object.
     *
     * @param uri
     * @param typeRef
     * @return A new T or <code>null</code>
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @see #get(String, TypeReference)
     */
    public <T> T get(URI uri, TypeReference<T> typeRef)
    throws JsonParseException, JsonMappingException, IOException
    {
        HttpGet get = new HttpGet(uri);
        return get(get,typeRef);
    }

    private <T> T get(HttpGet get, TypeReference<T> typeRef)
    throws JsonParseException, JsonMappingException, IOException
    {
        CloseableHttpResponse response = m_HttpClient.execute(get);

        try
        {
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);

            // 404 errors return empty paging docs so still read them
            if (response.getStatusLine().getStatusCode() == 200 ||
                response.getStatusLine().getStatusCode() == 404)

            {
                T docs = m_JsonMapper.readValue(content, typeRef);
                m_LastError = null;
                return docs;
            }
            else
            {
                String msg = String.format(
                        "GET returned status code %d for url %s. "
                        + "Returned content = %s",
                        response.getStatusLine().getStatusCode(), get.getURI(),
                        content);

                LOGGER.error(msg);

                m_LastError = m_JsonMapper.readValue(content,
                        new TypeReference<ApiError>() {} );
            }
        }
        finally
        {
            response.close();
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
