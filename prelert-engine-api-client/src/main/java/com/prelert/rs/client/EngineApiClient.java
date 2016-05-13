/****************************************************************************
 *                                                                          *
 * Copyright 2015-2016 Prelert Ltd                                          *
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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.DeferredContentProvider;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.prelert.job.Detector;
import com.prelert.job.JobConfiguration;
import com.prelert.job.JobDetails;
import com.prelert.job.ModelSnapshot;
import com.prelert.job.errorcodes.ErrorCodes;
import com.prelert.job.results.CategoryDefinition;
import com.prelert.job.transform.TransformConfig;
import com.prelert.rs.data.ApiError;
import com.prelert.rs.data.DataPostResponse;
import com.prelert.rs.data.MultiDataPostResult;
import com.prelert.rs.data.Pagination;
import com.prelert.rs.data.SingleDocument;

/**
 * A HTTP Client for the Prelert Engine RESTful API.
 *
 * <br>
 * Contains methods to create jobs, list jobs, upload data and query results.
 * <br>
 * Implements closeable so it can be used in a try-with-resource statement
 * <br>
 * Not reentrant; each object of this class may only be used for one
 * interaction with the server at any time.  If you need multiple parallel
 * interactions with the server, you must create multiple instances of this
 * class.
 */
public class EngineApiClient implements Closeable
{
    private static final Logger LOGGER = Logger.getLogger(EngineApiClient.class);
    private static final int MAX_BUFFER_SIZE = 4096 * 1024;
    private static final int MIN_BUFFER_GROWTH = 1024;
    private static final double BUFFER_GROWTH_FACTOR = 0.1;
    private static final String UTF8 = "UTF-8";
    private static final String APPLICATION_JSON = "application/json";

    private final String m_BaseUrl;
    private final ObjectMapper m_JsonMapper;
    private final HttpClient m_HttpClient;
    private ApiError m_LastError;

    /**
     * Creates a new http client and Json object mapper.
     * Call {@linkplain #close()} once finished
     *
     * @param baseUrl The base URL for the REST API including version number
     * e.g <code>http://localhost:8080/engine/v2/</code>
     */
    public EngineApiClient(String baseUrl)
    {
        m_BaseUrl = baseUrl;
        m_HttpClient = new HttpClient();
        try
        {
            m_HttpClient.start();
        }
        catch (Exception e)
        {
            LOGGER.fatal("Failed to start the HTTP client", e);
        }
        m_JsonMapper = new ObjectMapper();
        m_JsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        m_JsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Close the http client
     */
    @Override
    public void close() throws IOException
    {
        try
        {
            m_HttpClient.stop();
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Get details of all the jobs in database
     *
     * @return The {@link Pagination} object containing a list of {@link JobDetails jobs}
     * @throws IOException If HTTP GET fails
     */
    public Pagination<JobDetails> getJobs()
    throws IOException
    {
        String url = m_BaseUrl + "/jobs";
        LOGGER.debug("GET jobs: " + url);

        Pagination<JobDetails> page = get(url, new TypeReference<Pagination<JobDetails>>() {});

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
     * @param jobId The Job's unique Id
     *
     * @return If the job exists a {@link com.prelert.rs.data.SingleDocument SingleDocument}
     * containing the {@link JobDetails job} is returned else the SingleDocument is empty
     * @throws IOException If HTTP GET fails
     */
    public SingleDocument<JobDetails> getJob(String jobId)
    throws IOException
    {
        String url = m_BaseUrl + "/jobs/" + encode(jobId);
        LOGGER.debug("GET job: " + url);

        SingleDocument<JobDetails> doc = get(url,
                new TypeReference<SingleDocument<JobDetails>>() {});

        if (doc == null)
        {
            doc = new SingleDocument<>();
        }
        return doc;
    }

    /**
     * Create a new Job from the <code>JobConfiguration</code> object.
     * <br>
     * Internally this function converts <code>jobConfig</code> to a JSON
     * string and calls {@link #createJob(String)}
     *
     * @param jobConfig the job configuration
     * @return The new job's Id or an empty string if there was an error
     * @throws IOException If HTTP POST fails
     */
    public String createJob(JobConfiguration jobConfig) throws IOException
    {
        String payLoad = m_JsonMapper.writeValueAsString(jobConfig);
        return createJob(payLoad);
    }

    /**
     * Create a new job with the configuration in <code>createJobPayload</code>
     * and return the newly created job's Id
     *
     * @param createJobPayload The Json configuration for the new job
     * @return The new job's Id or an empty string if there was an error
     * @throws JsonMappingException If JSON mapping fails
     * @throws JsonParseException If JSON parsing fails
     * @throws IOException If HTTP POST fails
     */
    public String createJob(String createJobPayload) throws JsonParseException,
            JsonMappingException, IOException
    {
        String url = m_BaseUrl + "/jobs";
        LOGGER.debug("Create job: " + url);

        Request request = m_HttpClient.POST(url)
                .header(HttpHeader.CONTENT_TYPE, APPLICATION_JSON)
                .header(HttpHeader.CONTENT_ENCODING, UTF8)
                .content(new StringContentProvider(createJobPayload));

        ContentResponse response = executeRequest(request);
        String content = response.getContentAsString();

        if (response.getStatus() == HttpStatus.CREATED_201)
        {
            m_LastError = null;

            Map<String, String> msg = m_JsonMapper.readValue(content,
                    new TypeReference<Map<String, String>>() {} );
            if (msg.containsKey("id"))
            {
                return msg.get("id");
            }

            LOGGER.error("Job created but no 'id' field in returned content");
            LOGGER.error("Response Content = " + content);
        }
        else
        {
            String msg = String.format("Error creating job status code = %d. "
                    + "Returned content: %s", response.getStatus(), content);
            LOGGER.error(msg);
            m_LastError = m_JsonMapper.readValue(content, new TypeReference<ApiError>() {} );
        }

        return "";
    }

    /**
     * Validate a detector object.
     * <br>
     * Internally this function converts <code>detector</code> to a JSON
     * string
     *
     * @param detector the detector to validate
     * @return boolean successful validation
     * @throws IOException If HTTP POST fails
     */
    public boolean validateDetector(Detector detector) throws IOException
    {
        String payload = m_JsonMapper.writeValueAsString(detector);
        return validateDetector(payload);
    }

    /**
     * Validate a detector string.
     *
     * @param detector the detector to validate
     * @return boolean successful validation
     * @throws IOException If HTTP POST fails
     */
    public boolean validateDetector(String detector) throws IOException
    {
        String url = m_BaseUrl + "/validate/detector";
        LOGGER.debug("Validate detector " + detector + ", at: " + url);

        Request request = m_HttpClient.POST(url)
                .header(HttpHeader.CONTENT_TYPE, APPLICATION_JSON)
                .header(HttpHeader.CONTENT_ENCODING, UTF8)
                .content(new StringContentProvider(detector));

        ContentResponse response = executeRequest(request);
        String content = response.getContentAsString();

        if (response.getStatus() != HttpStatus.OK_200)
        {
            String msg = String.format(
                    "Error validating detector, status code = %d. Returned content: %s",
                    response.getStatus(), content);

            LOGGER.error(msg);
            m_LastError = m_JsonMapper.readValue(content,
                    new TypeReference<ApiError>() {} );
            return false;
        }

        m_LastError = null;
        return true;
    }

    /**
     * Validate a TransformConfig object.
     * <br>
     * Internally this function converts <code>transform</code> to a JSON
     * string
     *
     * @param transform the transform to validate
     * @return boolean successful validation
     * @throws IOException If HTTP POST fails
     */
    public boolean validateTransform(TransformConfig transform) throws IOException
    {
        String payload = m_JsonMapper.writeValueAsString(transform);
        return validateTransform(payload);
    }

    /**
     * Validate a transform string.
     *
     * @param transform the transform to validate
     * @return boolean successful validation
     * @throws IOException If HTTP POST fails
     */
    public boolean validateTransform(String transform) throws IOException
    {
        String url = m_BaseUrl + "/validate/transform";
        LOGGER.debug("Validate transform " + transform + ", at: " + url);

        Request request = m_HttpClient.POST(url)
                .header(HttpHeader.CONTENT_TYPE, APPLICATION_JSON)
                .header(HttpHeader.CONTENT_ENCODING, UTF8)
                .content(new StringContentProvider(transform));

        ContentResponse response = executeRequest(request);
        String content = response.getContentAsString();

        if (response.getStatus() != HttpStatus.OK_200)
        {
            String msg = String.format(
                    "Error validating transform, status code = %d. Returned content: %s",
                    response.getStatus(), content);

            LOGGER.error(msg);
            m_LastError = m_JsonMapper.readValue(content,
                    new TypeReference<ApiError>() {} );
            return false;
        }

        m_LastError = null;
        return true;
    }

    /**
     * Validate an array of TransformConfig objects.
     * <br>
     * Internally this function converts <code>transforms</code> to a JSON
     * string
     *
     * @param transforms the transforms to validate
     * @return boolean successful validation
     * @throws IOException If HTTP POST fails
     */
    public boolean validateTransforms(TransformConfig[] transforms) throws IOException
    {
        String payload = m_JsonMapper.writeValueAsString(transforms);
        return validateTransforms(payload);
    }

    /**
     * Validate an array of transforms.
     *
     * @param transforms the transforms to validate
     * @return boolean successful validation
     * @throws IOException If HTTP POST fails
     */
    public boolean validateTransforms(String transforms) throws IOException
    {
        String url = m_BaseUrl + "/validate/transforms";
        LOGGER.debug("Validate transforms " + transforms + ", at: " + url);

        Request request = m_HttpClient.POST(url)
                .header(HttpHeader.CONTENT_TYPE, APPLICATION_JSON)
                .header(HttpHeader.CONTENT_ENCODING, UTF8)
                .content(new StringContentProvider(transforms));

        ContentResponse response = executeRequest(request);
        String content = response.getContentAsString();

        if (response.getStatus() != HttpStatus.OK_200)
        {
            String msg = String.format(
                    "Error validating transforms, status code = %d. Returned content: %s",
                    response.getStatus(), content);

            LOGGER.error(msg);
            m_LastError = m_JsonMapper.readValue(content,
                    new TypeReference<ApiError>() {} );
            return false;
        }

        m_LastError = null;
        return true;
    }

    /**
     * PUTS the description parameter to the job and sets it as
     * the job's new description field
     *
     * @param jobId The job's unique ID
     * @param description New description field
     *
     * @return True if the job description added successfully
     * @throws IOException If HTTP PUT fails
     */
    public boolean setJobDescription(String jobId, String description)
    throws IOException
    {
        String json = "{\"description\":\"" + description + "\"}";
        return updateJob(jobId, json);
    }

    /**
     * Submits a request to update a job
     *
     * @param jobId the id of the job to update
     * @param updateJson the JSON containing the fields to update and their new values
     * @return {@code true} if the update was successful, or {@code false} otherwise
     * @throws IOException If HTTP PUT fails
     */
    public boolean updateJob(String jobId, String updateJson) throws IOException
    {
        String url = m_BaseUrl + "/jobs/" + encode(jobId) + "/update";
        LOGGER.debug("PUT update job: " + url);

        Request request = m_HttpClient.newRequest(url)
                .method(HttpMethod.PUT)
                .header(HttpHeader.CONTENT_TYPE, APPLICATION_JSON)
                .header(HttpHeader.CONTENT_ENCODING, UTF8)
                .content(new StringContentProvider(updateJson));

        return executeRequest(request, "updating job");
    }

    /**
     * PUTS the description parameter to the job and sets it as
     * the ModelSnapshot's new description field
     *
     * @param jobId The job's unique ID
     * @param snapshotId The ModelSnapshot's ID
     * @param description New description field
     *
     * @return A {@link SingleDocument} object containing the requested {@link ModelSnapshot}
     * @throws IOException If HTTP PUT fails
     */
    public SingleDocument<ModelSnapshot> setModelSnapshotDescription(String jobId, String snapshotId, String description)
    throws IOException
    {
        String url = m_BaseUrl + "/modelsnapshots/" + encode(jobId) + "/" + encode(snapshotId) + "/description";
        LOGGER.debug("PUT update ModelSnapshot description: " + url);
        String json = "{\"description\":\"" + description + "\"}";

        Request request = m_HttpClient.newRequest(url)
                .method(HttpMethod.PUT)
                .header(HttpHeader.CONTENT_TYPE, APPLICATION_JSON)
                .header(HttpHeader.CONTENT_ENCODING, UTF8)
                .content(new StringContentProvider(json));

        ContentResponse response = executeRequest(request);

        SingleDocument<ModelSnapshot> doc = m_JsonMapper.readValue(response.getContentAsString(),
                                                            new TypeReference<SingleDocument<ModelSnapshot>>() {} );
        if (doc == null)
        {
            doc = new SingleDocument<>();
        }
        return doc;
    }

    /**
     * Revert to a saved ModelSnapshot
     *
     * @param jobId The job's unique ID
     * @param description The description of the desired ModelSnapshot
     * @param deleteInterveningResults Delete intervening results and reset last record time
     *
     * @return A {@link SingleDocument} object containing the reverted {@link ModelSnapshot}
     * @throws IOException If HTTP POST fails
     */
    public SingleDocument<ModelSnapshot> revertModelSnapshotByDescription(String jobId,
            String description, boolean deleteInterveningResults)
    throws IOException
    {
        String url = m_BaseUrl + "/modelsnapshots/" + encode(jobId) + "/revert?description=" + encode(description)
                + "&deleteInterveningResults=" + (deleteInterveningResults ? "true" : "false");

        LOGGER.debug("POST revert ModelSnapshot by description: " + url);
        return revertModelSnapshot(url);
    }

    /**
     * Revert to a saved ModelSnapshot
     *
     * @param jobId The job's unique ID
     * @param time The snapshotId of the desired ModelSnapshot
     * @param deleteInterveningResults Delete intervening results and reset last record time
     *
     * @return A {@link SingleDocument} object containing the reverted {@link ModelSnapshot}
     * @throws IOException If HTTP POST fails
     */
    public SingleDocument<ModelSnapshot> revertModelSnapshotByTime(String jobId,
            String time, boolean deleteInterveningResults)
    throws IOException
    {
        String url = m_BaseUrl + "/modelsnapshots/" + encode(jobId) + "/revert?time=" + encode(time)
                + "&deleteInterveningResults=" + (deleteInterveningResults ? "true" : "false");
        LOGGER.debug("POST revert ModelSnapshot by time: " + url);
        return revertModelSnapshot(url);
    }

    /**
     * Revert to a saved ModelSnapshot
     *
     * @param jobId The job's unique ID
     * @param snapshotId Reverts to the snapshot of this exact time, or the most recent one prior to this time.
     * @param deleteInterveningResults Delete intervening results and reset last record time
     *
     * @return A {@link SingleDocument} object containing the reverted {@link ModelSnapshot}
     * @throws IOException If HTTP POST fails
     */
    public SingleDocument<ModelSnapshot> revertModelSnapshotById(String jobId,
            String snapshotId, boolean deleteInterveningResults)
    throws IOException
    {
        String url = m_BaseUrl + "/modelsnapshots/" + encode(jobId) + "/revert?snapshotId=" + encode(snapshotId)
                + "&deleteInterveningResults=" + (deleteInterveningResults ? "true" : "false");
        LOGGER.debug("POST revert ModelSnapshot by snapshotId: " + url);
        return revertModelSnapshot(url);
    }

    private SingleDocument<ModelSnapshot> revertModelSnapshot(String url) throws JsonParseException, JsonMappingException, IOException
    {
        Request request = m_HttpClient.newRequest(url).method(HttpMethod.POST);

        ContentResponse response = executeRequest(request);
        return m_JsonMapper.readValue(response.getContentAsString(), new TypeReference<SingleDocument<ModelSnapshot>>() {} );
    }

    /**
     * DELETEs the ModelSnapshot
     *
     * @param jobId The job's unique ID
     * @param snapshotId The snapshot's ID
     *
     * @return boolean The success of the request
     * @throws IOException If HTTP DELETE fails
     */
    public boolean deleteModelSnapshot(String jobId, String snapshotId)
    throws IOException
    {
        String url = m_BaseUrl + "/modelsnapshots/" + encode(jobId) + "/" + encode(snapshotId);
        LOGGER.debug("DELETE ModelSnapshot: " + url);
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.DELETE),
                "deleting snapshot");
    }

    private ContentResponse executeRequest(Request request) throws IOException
    {
        try
        {
            return request.send();
        }
        catch (InterruptedException | TimeoutException | ExecutionException e)
        {
            LOGGER.error("An error occurred while executing an HTTP request", e);
            throw new IOException(e);
        }
    }

    /**
     * Executes an HTTP request and checks if the response was OK. If not, it logs the error.
     *
     * @return True if response was OK, otherwise false.
     * @throws ExecutionException If execution fails
     * @throws TimeoutException If HTTP request times out
     * @throws InterruptedException If request is interrupted
     * @throws JsonMappingException If JSON mapping fails
     * @throws JsonParseException If JSON parsing fails
     * @throws IOException If HTTP GET fails
     */
    private boolean executeRequest(Request request, String activityDescription)
            throws JsonParseException, JsonMappingException, IOException
    {
        ContentResponse response = executeRequest(request);

        if (response.getStatus() == HttpStatus.OK_200)
        {
            m_LastError = null;
            return true;
        }
        String content = response.getContentAsString();
        String msg = String.format("Error %s. Status code = %d, Returned content: %s",
                activityDescription, response.getStatus(), content);
        LOGGER.error(msg);
        m_LastError = m_JsonMapper.readValue(content, new TypeReference<ApiError>() {} );
        return false;
    }

    /**
     * Delete an individual job
     *
     * @param jobId The Job's unique Id
     * @return If the job existed and was deleted return true else false
     * @throws IOException If HTTP DELETE fails
     */
    public boolean deleteJob(String jobId) throws IOException
    {
        String url = m_BaseUrl + "/jobs/" + encode(jobId);
        LOGGER.debug("DELETE job: " + url);

        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.DELETE),
                "deleting job");
    }

    /**
     * Pause the analysis of a running job: this job will still accept data
     * but won't analyze it.
     *
     * @param jobId The job's unique ID
     *
     * @return True if the job is paused successfully
     * @throws IOException If HTTP POST fails
     */
    public boolean pauseJob(String jobId) throws IOException
    {
        String url = m_BaseUrl + "/jobs/" + encode(jobId) + "/pause";
        LOGGER.debug("Pause job: " + url);
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.POST),
                "pausing job");
    }

    /**
     * Resume a paused job
     *
     * @param jobId The job's unique ID
     *
     * @return True if the job is resumed successfully
     * @throws IOException If HTTP POST fails
     */
    public boolean resumeJob(String jobId) throws IOException
    {
        String url = m_BaseUrl + "/jobs/" + encode(jobId) + "/resume";
        LOGGER.debug("Resume job: " + url);
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.POST),
                "resuming job");
    }

    /**
     * Start the Elasticsearch Scheduler for a particular job.
     *
     * @param jobId The job's unique ID
     *
     * @return True if the scheduler is started successfully
     * @throws IOException If HTTP POST fails
     */
    public boolean startScheduler(String jobId) throws IOException
    {
        String url = m_BaseUrl + "/schedulers/" + encode(jobId) + "/start";
        LOGGER.debug("Start scheduler: " + url);
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.POST),
                "starting scheduler");
    }

    /**
     * Start the Elasticsearch Scheduler for a particular job.
     *
     * @param jobId The job's unique ID
     * @param start The time specifying the start (inclusive) of the interval data will be analyzed
     * @param end The time specifying the end (exclusive) of the interval data will be analyzed
     *
     * @return True if the scheduler is started successfully
     * @throws IOException If HTTP POST fails
     */
    public boolean startScheduler(String jobId, String start, String end) throws IOException
    {
        String url = String.format("%s/schedulers/%s/start?start=%s&end=%s", m_BaseUrl,
                encode(jobId), encode(start), encode(end));
        LOGGER.debug("Start scheduler: " + url);
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.POST),
                "starting scheduler");
    }

    /**
     * Stop the Elasticsearch Scheduler for a particular job.
     *
     * @param jobId The job's unique ID
     *
     * @return True if the scheduler is started successfully
     * @throws IOException If HTTP POST fails
     */
    public boolean stopScheduler(String jobId) throws IOException
    {
        String url = m_BaseUrl + "/schedulers/" + encode(jobId) + "/stop";
        LOGGER.debug("Stop scheduler: " + url);
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.POST),
                "stopping scheduler");
    }

    /**
     * Read the input stream in 4Mb chunks and upload making a new connection
     * for each chunk.
     * The data is not set line-by-line or broken in chunks on newline
     * boundaries; it is sent in fixed size blocks. The API will manage
     * reconstructing the records from the chunks.
     *
     * @param jobId The Job's unique Id
     * @param inputStream The data to write to the web service
     * @return the multiple data upload results in {@linkplain MultiDataPostResult}
     * @throws IOException If HTTP POST fails
     * @see #streamingUpload(String, InputStream, boolean)
     */
    public MultiDataPostResult chunkedUpload(String jobId, InputStream inputStream)
            throws IOException
    {
        String postUrl = m_BaseUrl + "/data/" + encode(jobId);
        LOGGER.debug("Uploading chunked data to " + postUrl);

        byte [] buffer = new byte[MAX_BUFFER_SIZE];
        int uploadCount = 0;
        MultiDataPostResult uploadSummary = new MultiDataPostResult();

        m_LastError = null;
        while (inputStream.read(buffer) > -1)
        {
            LOGGER.info("Upload " + ++uploadCount);

            Request request = m_HttpClient.POST(postUrl)
                    .header(HttpHeader.CONTENT_TYPE, "application/octet-stream")
                    .content(new BytesContentProvider(buffer));
            ContentResponse response = executeRequest(request);

            String content = response.getContentAsString();

            if (response.getStatus() != HttpStatus.ACCEPTED_202)
            {
                String msg = String.format(
                        "Upload of chunk %d failed, status code = %d. Returned content: %s",
                        uploadCount, response.getStatus(), content);

                LOGGER.error(msg);

                uploadSummary = convertMultiDataPostResponse(content);

                if (m_LastError == null)
                {
                    m_LastError = newUnknownError(msg);
                }
            }
            else
            {
                uploadSummary = m_JsonMapper.readValue(content, new TypeReference<MultiDataPostResult>() {});
            }
        }

        return uploadSummary;
    }

    /**
     * Stream data from <code>inputStream</code> to the service.
     * This is different to {@link #chunkedUpload(String, InputStream)}
     * in that the entire stream is read and uploading at once without breaking
     * the connection.
     *
     * @param jobId The Job's unique Id
     * @param inputStream The data to write to the web service
     * @param compressed Is the data gzipped compressed?
     * @return the multiple data upload results in {@linkplain MultiDataPostResult}
     * @throws IOException If HTTP POST fails
     * @see #chunkedUpload(String, InputStream)
     */
    public MultiDataPostResult streamingUpload(String jobId, InputStream inputStream, boolean compressed)
    throws IOException
    {
        return streamingUpload(jobId, inputStream, compressed, "", "");
    }

    /**
     * Stream data from <code>inputStream</code> to the service.
     * This is different to {@link #chunkedUpload(String, InputStream)}
     * in that the entire stream is read and uploading at once without breaking
     * the connection.
     *
     * @param jobId The Job's unique Id
     * @param inputStream The data to write to the web service
     * @param compressed Is the data gzipped compressed?
     * @param resetStart The start of the time range to reset buckets for (inclusive)
     * @param resetEnd The end of the time range to reset buckets for (inclusive)
     * @return the multiple data upload results in {@linkplain MultiDataPostResult}
     * @throws IOException If HTTP POST fails
     * @see #chunkedUpload(String, InputStream)
     */
    public MultiDataPostResult streamingUpload(String jobId, InputStream inputStream, boolean compressed,
            String resetStart, String resetEnd)
    throws IOException
    {
        String postUrl = String.format("%s/data/%s", m_BaseUrl, encode(jobId));
        if (!Strings.isNullOrEmpty(resetStart) || !Strings.isNullOrEmpty(resetEnd))
        {
            postUrl += String.format("?resetStart=%s&resetEnd=%s",
                    Strings.nullToEmpty(resetStart), Strings.nullToEmpty(resetEnd));
        }
        return uploadStream(inputStream, postUrl, compressed, new MultiDataPostResult(), true,
                content -> convertMultiDataPostResponse(content));
    }

    /**
     * Read data from <code>inputStream</code> and upload to multiple jobs
     * simultaneously. The response is a list of processed data counts/errors.
     *
     * @param jobIds The list of jobs to send the data to
     * @param inputStream The data to write to the web service
     * @param compressed Is the data gzipped compressed?
     * @return the multiple data upload results in {@linkplain MultiDataPostResult}
     * @throws IOException If HTTP POST fails
     */
    public MultiDataPostResult streamingUpload(List<String> jobIds, InputStream inputStream,
                                            boolean compressed)
    throws IOException
    {
        String postUrl = String.format("%s/data/%s", m_BaseUrl, String.join(",", jobIds));

        return uploadStream(inputStream, postUrl, compressed, new MultiDataPostResult(), true,
                content -> convertMultiDataPostResponse(content));
    }

    private MultiDataPostResult convertMultiDataPostResponse(String content) throws IOException
    {
        MultiDataPostResult uploadSummary = m_JsonMapper.readValue(content, new TypeReference<MultiDataPostResult>() {});
        for (DataPostResponse dpr : uploadSummary.getResponses())
        {
            if (dpr.getError() != null)
            {
                m_LastError = dpr.getError();
                break;
            }
        }
        return uploadSummary;
    }

    @FunctionalInterface
    private interface FunctionThatThrowsIoException<T, R>
    {
        R apply(T input) throws IOException;
    }

    private <T, E> T uploadStream(InputStream inputStream, String postUrl, boolean compressed,
            T defaultReturnValue, boolean convertResponseOnError,
            FunctionThatThrowsIoException<String, T> convertContentFunction)
    throws IOException
    {
        LOGGER.debug("Uploading data to " + postUrl);

        m_LastError = null;

        // It is possible that the server replies with an error and closes the stream.
        // In that case, there could be a case where a thread that writes into the inputStream
        // tries to write after the stream is closed. To avoid that, we make an asynchronous call
        // and we offer the inputStream in a deferred manner.

        CountDownLatch waitUntilRequestCompletesLatch = new CountDownLatch(1);
        AtomicInteger statusHolder = new AtomicInteger();
        DeferredContentProvider contentProvider = new DeferredContentProvider();
        Request request = m_HttpClient.POST(postUrl)
                .header(HttpHeader.CONTENT_TYPE, "application/octet-stream")
                .content(contentProvider);
        if (compressed)
        {
            request.header(HttpHeader.CONTENT_ENCODING, "gzip");
        }
        BufferingResponseListener responseListener = new BufferingResponseListener()
        {
            @Override
            public void onComplete(Result result)
            {
                statusHolder.getAndSet(result.getResponse().getStatus());
                waitUntilRequestCompletesLatch.countDown();
            }
        };
        request.send(responseListener);

        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        int bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) > -1
                && !contentProvider.isClosed()
                && waitUntilRequestCompletesLatch.getCount() > 0)
        {
            contentProvider.offer(ByteBuffer.wrap(buffer, 0, bytesRead));
            buffer = new byte[chooseBufferSize(bytesRead, buffer.length)];
        }
        contentProvider.close();
        inputStream.close();

        try
        {
            waitUntilRequestCompletesLatch.await();
        }
        catch (InterruptedException e)
        {
            LOGGER.error(e);
            return defaultReturnValue;
        }

        String content = Strings.nullToEmpty(responseListener.getContentAsString());

        if (statusHolder.get() != HttpStatus.ACCEPTED_202)
        {
            String msg = String.format(
                    "Streaming upload failed, status code = %d. Returned content: %s",
                    statusHolder.get(), content);

            LOGGER.error(msg);

            if (!content.isEmpty())
            {
                if (convertResponseOnError)
                {
                    // In this case convertContentFunction must set m_LastError
                    return convertContentFunction.apply(content);
                }
                m_LastError = m_JsonMapper.readValue(content, new TypeReference<ApiError>() {});
            }
            else
            {
                m_LastError = newUnknownError(msg);
            }

            return defaultReturnValue;
        }
        return convertContentFunction.apply(content);
    }

    private static int chooseBufferSize(int bytesRead, int currentBufferSize)
    {
        if (bytesRead >= MAX_BUFFER_SIZE)
        {
            return MAX_BUFFER_SIZE;
        }
        if (bytesRead < currentBufferSize)
        {
            return bytesRead;
        }
        int growth = Math.max(MIN_BUFFER_GROWTH, (int) (BUFFER_GROWTH_FACTOR * currentBufferSize));
        return Math.min(MAX_BUFFER_SIZE, currentBufferSize + growth);
    }

    /**
     * Upload the contents of <code>dataFile</code> to the server.
     *
     * @param jobId The Job's Id
     * @param dataFile Should match the data configuration format of the job
     * @param compressed Is the data gzipped compressed?
     * @return the multiple data upload results in {@linkplain MultiDataPostResult}
     * @throws IOException If HTTP POST fails
     */
    public MultiDataPostResult fileUpload(String jobId, File dataFile, boolean compressed)
    throws IOException
    {
        FileInputStream stream = new FileInputStream(dataFile);

        return streamingUpload(jobId, stream, compressed);
    }

    /**
     * Upload the contents of <code>dataFile</code> to the server.
     *
     * @param jobId The Job's Id
     * @param dataFile Should match the data configuration format of the job
     * @param compressed Is the data gzipped compressed?
     * @param resetStart The start of the time range to reset buckets for (inclusive)
     * @param resetEnd The end of the time range to reset buckets for (inclusive)
     * @return the multiple data upload results in {@linkplain MultiDataPostResult}
     * @throws IOException If HTTP POST fails
     */
    public MultiDataPostResult fileUpload(String jobId, File dataFile, boolean compressed,
            String resetStart, String resetEnd) throws IOException
    {
        FileInputStream stream = new FileInputStream(dataFile);

        return streamingUpload(jobId, stream, compressed, resetStart, resetEnd);
    }

    /**
     * Flush the job, ensuring that no previously uploaded data is waiting in
     * buffers.
     *
     * @param jobId The Job's unique Id
     * @param calcInterim Should interim results for the selected buckets be calculated
     * based on the partial data uploaded for it so far? Interim results will be calculated for
     * all available buckets (most recent bucket plus latency buckets if latency was specified).
     * @return True if successful
     * @throws IOException If HTTP POST fails
     */
    public boolean flushJob(String jobId, boolean calcInterim) throws IOException
    {
        return flushJob(jobId, calcInterim, null, "", "");
    }

    /**
     * Flush the job, ensuring that no previously uploaded data is waiting in
     * buffers.
     *
     * @param jobId The Job's unique Id
     * @param calcInterim Should interim results for the selected buckets be calculated
     * based on the partial data uploaded for it so far? Interim results will be calculated for
     * all available buckets (most recent bucket plus latency buckets if latency was specified).
     * @param advanceTime Finalize up to this time, and any ignore subsequent input with an earlier time
     * @return True if successful
     * @throws IOException If HTTP POST fails
     */
    public boolean flushJob(String jobId, boolean calcInterim, Date advanceTime) throws IOException
    {
        return flushJob(jobId, calcInterim, advanceTime, "", "");
    }

    /**
     * Flush the job, ensuring that no previously uploaded data is waiting in
     * buffers.
     *
     * @param jobId The Job's unique Id
     * @param calcInterim Should interim results for the selected buckets be calculated
     * based on the partial data uploaded for it so far? If both {@code start} and {@code end} are
     * empty, the default behaviour of calculating interim results for all available buckets
     * (most recent bucket plus latency buckets if latency was specified) will be assumed.
     * @param start The start of the time range to calculate interim results for (inclusive)
     * @param end The end of the time range to calculate interim results for (exclusive)
     * @return True if successful
     * @throws IOException If HTTP POST fails
     */
    public boolean flushJob(String jobId, boolean calcInterim, String start, String end)
            throws IOException
    {
        return flushJob(jobId, calcInterim, null, start, end);
    }

    /**
     * Flush the job, ensuring that no previously uploaded data is waiting in
     * buffers.
     *
     * @param jobId The Job's unique Id
     * @param calcInterim Should interim results for the selected buckets be calculated
     * based on the partial data uploaded for it so far? If both {@code start} and {@code end} are
     * empty, the default behaviour of calculating interim results for all available buckets
     * (most recent bucket plus latency buckets if latency was specified) will be assumed.
     * @param advanceTime Finalize up to this time, and any ignore subsequent input with an earlier time
     * @param start The start of the time range to calculate interim results for (inclusive)
     * @param end The end of the time range to calculate interim results for (exclusive)
     * @return True if successful
     * @throws IOException If HTTP POST fails
     */
    public boolean flushJob(String jobId, boolean calcInterim, Date advanceTime, String start, String end)
            throws IOException
    {
        // Send flush message
        String flushUrl = String.format(m_BaseUrl + "/data/%s/flush?calcInterim=%s&start=%s&end=%s",
                encode(jobId), calcInterim ? "true" : "false", encode(start), encode(end));
        if (advanceTime != null)
        {
            flushUrl += "&advanceTime=" + advanceTime.getTime();
        }
        LOGGER.debug("Flushing job " + flushUrl);

        ContentResponse response = executeRequest(m_HttpClient.POST(flushUrl));
        String content = response.getContentAsString();

        if (response.getStatus() != HttpStatus.OK_200)
        {
            String msg = String.format(
                    "Error flushing job %s, status code = %d. Returned content: %s",
                    jobId, response.getStatus(), content);

            LOGGER.error(msg);
            m_LastError = m_JsonMapper.readValue(content,
                    new TypeReference<ApiError>() {} );
            return false;
        }

        m_LastError = null;
        return true;
    }

    /**
     * Finish the job after all the data has been uploaded
     *
     * @param jobId The Job's unique Id
     * @return True if successful
     * @throws IOException If HTTP POST fails
     */
    public boolean closeJob(String jobId)
    throws IOException
    {
        // Send finish message
        String closeUrl = m_BaseUrl + "/data/" + encode(jobId) + "/close";
        LOGGER.debug("Closing job " + closeUrl);

        ContentResponse response = executeRequest(m_HttpClient.POST(closeUrl));
        String content = response.getContentAsString();

        if (response.getStatus() != HttpStatus.ACCEPTED_202
                && response.getStatus() != HttpStatus.OK_200)
        {
            String msg = String.format(
                    "Error closing job %s, status code = %d. Returned content: %s",
                    jobId, response.getStatus(), content);
            LOGGER.error(msg);
            m_LastError = m_JsonMapper.readValue(content, new TypeReference<ApiError>() {});
            return false;
        }

        m_LastError = null;
        return true;
    }

    /**
     * Returns a {@link BucketsRequestBuilder} for the given job through which
     * the request can be configured and executed
     *
     * @param jobId The jobId for which buckets are requested
     *
     * @return A {@link BucketsRequestBuilder}
     */
    public BucketsRequestBuilder prepareGetBuckets(String jobId)
    {
        return new BucketsRequestBuilder(this, jobId);
    }

    /**
     * Returns a {@link BucketRequestBuilder} for the given job through which
     * the request can be configured and executed
     *
     * @param jobId The jobId for which a bucket is requested
     * @param bucketTimestamp The timestamp of the bucket in seconds
     *
     * @return A {@link BucketRequestBuilder}
     */
    public BucketRequestBuilder prepareGetBucket(String jobId, String bucketTimestamp)
    {
        return new BucketRequestBuilder(this, jobId, bucketTimestamp);
    }

    /**
     * Returns a {@link CategoryDefinitionsRequestBuilder} for the given job through which
     * the request can be configured and executed
     *
     * @param jobId The jobId for which category definitions are requested
     *
     * @return A {@link CategoryDefinitionsRequestBuilder}
     */
    public CategoryDefinitionsRequestBuilder prepareGetCategoryDefinitions(String jobId)
    {
        return new CategoryDefinitionsRequestBuilder(this, jobId);
    }

    /**
     * Returns a single document with the category definition that was requested
     *
     * @param jobId the job id
     * @param categoryId the job's category id
     *
     * @return A {@link SingleDocument} object containing the requested {@link CategoryDefinition}
     * object
     * @throws JsonMappingException If JSON mapping fails
     * @throws IOException If HTTP GET fails
     */
    public SingleDocument<CategoryDefinition> getCategoryDefinition(String jobId, String categoryId)
            throws JsonMappingException, IOException
    {
        return new CategoryDefinitionRequestBuilder(this, jobId, categoryId).get();
    }

    /**
     * Returns a {@link InfluencersRequestBuilder} for the given job through which
     * the request can be configured and executed
     *
     * @param jobId The jobId for which influencers are requested
     *
     * @return A {@link InfluencersRequestBuilder}
     */
    public InfluencersRequestBuilder prepareGetInfluencers(String jobId)
    {
        return new InfluencersRequestBuilder(this, jobId);
    }

    /**
     * Returns a {@link RecordsRequestBuilder} for the given job through which
     * the request can be configured and executed
     *
     * @param jobId The jobId for which records are requested
     *
     * @return A {@link RecordsRequestBuilder}
     */
    public RecordsRequestBuilder prepareGetRecords(String jobId)
    {
        return new RecordsRequestBuilder(this, jobId);
    }

    /**
     * Returns a {@link ModelSnapshotsRequestBuilder} for the given job through which
     * the request can be configured and executed
     *
     * @param jobId The jobId for which snapshots are requested
     *
     * @return A {@link ModelSnapshotsRequestBuilder}
     */
    public ModelSnapshotsRequestBuilder prepareGetModelSnapshots(String jobId)
    {
        return new ModelSnapshotsRequestBuilder(this, jobId);
    }

    /**
     * Stream data from <code>inputStream</code> to the preview service.
     *
     * @param jobId The Job's unique Id
     * @param inputStream The data to write to the web service
     * @return String The preview result
     * @throws IOException If HTTP POST fails
     */
    public String previewUpload(String jobId, InputStream inputStream)
    throws IOException
    {
        String postUrl = String.format("%s/preview/%s", m_BaseUrl, encode(jobId));
        return uploadStream(inputStream, postUrl, false, "", false, content -> content);
    }

    /**
     * Get the last 10 lines of the job's latest log file
     *
     * @param jobId The Job's unique Id
     * @return The last 10 lines of the last log file
     * @throws IOException If HTTP GET fails
     */
    public String tailLog(String jobId) throws IOException
    {
        return tailLog(jobId, 10);
    }

    /**
     * Tails the last <code>lineCount</code> lines from the job's
     * last log file. This tails the autodetect process log file.
     *
     * @param jobId The Job's unique Id
     * @param lineCount The number of lines to return
     * @return The last <code>lineCount</code> lines of the log file
     * @throws IOException If HTTP GET fails
     */
    public String tailLog(String jobId, int lineCount) throws IOException
    {
        String url = String.format("%s/logs/%s/tail?lines=%d",
                m_BaseUrl, encode(jobId), lineCount);

        LOGGER.debug("GET tail log " + url);

        return getStringContent(url);
    }

    /**
     * Tails the last <code>lineCount</code> lines from the named log file.
     *
     * @param jobId The Job's unique Id
     * @param logfileName the name of the log file without the '.log' suffix.
     * @param lineCount The number of lines to return
     * @return The last <code>lineCount</code> lines of the log file
     * @throws IOException If HTTP GET fails
     */
    public String tailLog(String jobId, String logfileName, int lineCount) throws IOException
    {
        String url = String.format("%s/logs/%s/%s/tail?lines=%d",
                m_BaseUrl, encode(jobId), logfileName, lineCount);

        LOGGER.debug("GET tail log " + url);

        return getStringContent(url);
    }

    /**
     * Get content from Url and return as a string
     *
     * @param url
     * @return If status code == 200 return the HTTP response content
     * else return an empty string.
     * @throws IOException If HTTP GET fails
     */
    private String getStringContent(String url) throws IOException
    {
        ContentResponse response = executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.GET));
        String content = response.getContentAsString();

        if (response.getStatus() == HttpStatus.OK_200)
        {
            m_LastError = null;
            return content;
        }

        String msg = String.format(
                "Error reading string content. Status code = %d. Returned content: %s",
                response.getStatus(), content);
        LOGGER.error(msg);
        m_LastError = m_JsonMapper.readValue(content, new TypeReference<ApiError>() {});
        return "";
    }

    /**
     * Download the specified log file for the job.
     * The autodetect process writes a log file named after the job id (&lt;job_id&gt;.log)
     * while the Java component logs to engine_api.log.
     *
     * @param jobId The Job's unique Id
     * @param logfileName the name of the log file without the '.log' suffix.
     * @return The log file as a String
     * @throws IOException If HTTP GET fails
     */
    public String downloadLog(String jobId, String logfileName) throws IOException
    {
        String url = String.format("%s/logs/%s/%s",
                m_BaseUrl, encode(jobId), encode(logfileName));

        LOGGER.debug("GET log file " + url);

        return getStringContent(url);
    }

    /**
     * Download all the log files for the given job.
     *
     * <b>Important: the caller MUST close the ZipInputStream returned by
     * this method, otherwise all subsequent client/server communications
     * will be blocked.</b>
     *
     * @param jobId The Job's unique Id
     * @return A ZipInputStream for the log files. If an error occurred, the inputstream
     * may by empty or contain the server response. The caller MUST close this
     * ZipInputStream when they have finished with it.
     * @throws IOException If HTTP GET fails
     */
    public ZipInputStream downloadAllLogs(String jobId) throws IOException
    {
        String url = String.format("%s/logs/%s", m_BaseUrl, encode(jobId));

        LOGGER.debug("GET download logs " + url);

        m_LastError = null;
        InputStreamResponseListener responseListener = new InputStreamResponseListener();
        Request request = m_HttpClient.newRequest(url).method(HttpMethod.GET);
        request.send(responseListener);
        return new ZipInputStream(responseListener.getInputStream());
    }

    /**
     * Download the log files for Elasticsearch.
     *
     * <b>Important: the caller MUST close the ZipInputStream returned by
     * this method, otherwise all subsequent client/server communications
     * will be blocked.</b>
     *
     * @return A ZipInputStream for the log files. If an error occurred, the inputstream
     * may by empty or contain the server response. The caller MUST close this
     * ZipInputStream when they have finished with it.
     * @throws IOException If HTTP GET fails
     */
    public ZipInputStream downloadElasticsearchLogs() throws IOException
    {
        String url = String.format("%s/logs/elasticsearch", m_BaseUrl);

        LOGGER.debug("GET download logs " + url);

        m_LastError = null;
        InputStreamResponseListener responseListener = new InputStreamResponseListener();
        Request request = m_HttpClient.newRequest(url).method(HttpMethod.GET);
        request.send(responseListener);
        return new ZipInputStream(responseListener.getInputStream());
    }

    /**
     * Download the log files for the Engine API.
     *
     * <b>Important: the caller MUST close the ZipInputStream returned by
     * this method, otherwise all subsequent client/server communications
     * will be blocked.</b>
     *
     * @return A ZipInputStream for the log files. If an error occurred, the inputstream
     * may by empty or contain the server response. The caller MUST close this
     * ZipInputStream when they have finished with it.
     * @throws IOException If HTTP GET fails
     */
    public ZipInputStream downloadEngineLogs() throws IOException
    {
        String url = String.format("%s/logs/engine_api", m_BaseUrl);

        LOGGER.debug("GET download logs " + url);

        m_LastError = null;
        InputStreamResponseListener responseListener = new InputStreamResponseListener();
        Request request = m_HttpClient.newRequest(url).method(HttpMethod.GET);
        request.send(responseListener);
        return new ZipInputStream(responseListener.getInputStream());
    }

    /**
     * Download a support bundle file.
     *
     * <b>Important: the caller MUST close the ZipInputStream returned by
     * this method, otherwise all subsequent client/server communications
     * will be blocked.</b>
     *
     * @return A ZipInputStream for the log files. If an error occurred, the inputstream
     * may by empty or contain the server response. The caller MUST close this
     * ZipInputStream when they have finished with it.
     * @throws IOException If HTTP GET fails
     */
    public ZipInputStream downloadSupportBundle() throws IOException
    {
        String url = String.format("%s/support", m_BaseUrl);

        LOGGER.debug("GET support bundle " + url);

        m_LastError = null;
        InputStreamResponseListener responseListener = new InputStreamResponseListener();
        Request request = m_HttpClient.newRequest(url).method(HttpMethod.GET);
        request.send(responseListener);
        return new ZipInputStream(responseListener.getInputStream());
    }

    /**
     * A generic HTTP GET to any Url. The result is converted from Json to
     * the type referenced in <code>typeRef</code>. A <code>TypeReference</code>
     * has to be used to preserve the generic type information that is usually
     * lost due to erasure.
     * <br>
     * If the response code is 200 or 404 try to parse the returned content
     * into an object of the generic parameter type <code>T</code>.
     * The 404 status code is not considered an error it simply means an
     * empty document was returned by the API.
     * <br>
     * This method is useful for paging through a set of results via the
     * next or previous page links in a {@link Pagination} object.
     *
     * @param fullUrl The full URL to GET
     * @param typeRef The type of the returned document
     * @param <T> A generic document type
     * @return A new T or <code>null</code>
     * @throws JsonParseException If JSON parsing fails
     * @throws JsonMappingException If JSON mapping fails
     * @throws IOException If HTTP GET fails
     * @see #get(URI, TypeReference)
     */
    public <T> T get(String fullUrl, TypeReference<T> typeRef)
    throws JsonParseException, JsonMappingException, IOException
    {
        return get(fullUrl, typeRef, false);
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
     * @param uri The URI to GET
     * @param typeRef The type of the returned document
     * @param <T> A generic document type
     * @return A new T or <code>null</code>
     * @throws JsonParseException If JSON parsing fails
     * @throws JsonMappingException If JSON mapping fails
     * @throws IOException If HTTP GET fails
     * @see #get(String, TypeReference)
     */
    public <T> T get(URI uri, TypeReference<T> typeRef)
    throws JsonParseException, JsonMappingException, IOException
    {
        return executeRequest(m_HttpClient.newRequest(uri).method(HttpMethod.GET), typeRef, false);
    }

    /**
     * A generic HTTP GET to any Url. The result is converted from Json to
     * the type referenced in <code>typeRef</code>. A <code>TypeReference</code>
     * has to be used to preserve the generic type information that is usually
     * lost in due to erasure.
     * <br>
     * If the response code is 200 or 404 try to parse the returned content
     * into an object of the generic parameter type <code>T</code>.
     * The 404 status code is not considered an error: it simply means an
     * empty document was returned by the API.
     * <br>
     * This method is useful for paging through a set of results via the
     * next or previous page links in a {@link Pagination} object.
     *
     * @param url The URL to GET
     * @param typeRef The type of the returned document
     * @param errorOn404 Treat a 404 status code as an error
     * @param <T> A generic document type
     * @return A new T or <code>null</code>
     * @throws JsonParseException If JSON parsing fails
     * @throws JsonMappingException If JSON mapping fails
     * @throws IOException If HTTP GET fails
     * @see #get(String, TypeReference)
     */
    public <T> T get(String url, TypeReference<T> typeRef, boolean errorOn404)
    throws JsonParseException, JsonMappingException, IOException
    {
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.GET), typeRef, errorOn404);
    }

    private <T> T executeRequest(Request request, TypeReference<T> typeRef, boolean errorOn404)
    throws JsonParseException, JsonMappingException, IOException
    {
        ContentResponse response = executeRequest(request);
        String content = response.getContentAsString();

        // 404 errors return empty paging docs so still read them
        if (response.getStatus() == HttpStatus.OK_200
                || (response.getStatus() == HttpStatus.NOT_FOUND_404 && !errorOn404))
        {
            T docs = m_JsonMapper.readValue(content, typeRef);
            m_LastError = null;
            return docs;
        }

        String msg = String.format(request.getMethod() +
                " returned status code %d for url %s. Returned content = %s",
                response.getStatus(), request.getURI(), content);
        LOGGER.error(msg);
        m_LastError = m_JsonMapper.readValue(content, new TypeReference<ApiError>() {} );

        return null;
    }

    /**
     * A generic HTTP POST to any Url. The result is converted from Json to
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
     * @param fullUrl The URL to POST
     * @param typeRef The type of the returned document
     * @param <T> A generic document type
     * @return A new T or <code>null</code>
     * @throws JsonParseException If JSON parsing fails
     * @throws JsonMappingException If JSON mapping fails
     * @throws IOException If HTTP POST fails
     * @see #get(URI, TypeReference)
     */
    public <T> T post(String fullUrl, TypeReference<T> typeRef)
    throws JsonParseException, JsonMappingException, IOException
    {
        return post(fullUrl, typeRef, false);
    }

    /**
     * A generic HTTP POST to any Url. The result is converted from Json to
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
     * @param uri The URI to POST
     * @param typeRef THe type of the returned document
     * @param <T> A generic document type
     * @return A new T or <code>null</code>
     * @throws JsonParseException If JSON parsing fails
     * @throws JsonMappingException If JSON mapping fails
     * @throws IOException If HTTP POST fails
     * @see #post(String, TypeReference)
     */
    public <T> T post(URI uri, TypeReference<T> typeRef)
    throws JsonParseException, JsonMappingException, IOException
    {
        return executeRequest(m_HttpClient.newRequest(uri).method(HttpMethod.POST), typeRef, false);
    }

    /**
     * A generic HTTP POST to any Url. The result is converted from Json to
     * the type referenced in <code>typeRef</code>. A <code>TypeReference</code>
     * has to be used to preserve the generic type information that is usually
     * lost in due to erasure.
     * <br>
     * If the response code is 200 or 404 try to parse the returned content
     * into an object of the generic parameter type <code>T</code>.
     * The 404 status code is not considered an error: it simply means an
     * empty document was returned by the API.
     * <br>
     * This method is useful for paging through a set of results via the
     * next or previous page links in a {@link Pagination} object.
     *
     * @param url The URL to POST
     * @param typeRef The type of the returned document
     * @param errorOn404 Treat a 404 status code as an error
     * @param <T> A generic document type
     * @return A new T or <code>null</code>
     * @throws JsonParseException If JSON parsing fails
     * @throws JsonMappingException If JSON mapping fails
     * @throws IOException If HTTP POST fails
     * @see #post(String, TypeReference)
     */
    public <T> T post(String url, TypeReference<T> typeRef, boolean errorOn404)
    throws JsonParseException, JsonMappingException, IOException
    {
        return executeRequest(m_HttpClient.newRequest(url).method(HttpMethod.POST), typeRef,
                errorOn404);
    }

    /**
     * Get the last error message
     * @return The error or null if no errors have occurred
     */
    public ApiError getLastError()
    {
        return m_LastError;
    }

    public String getBaseUrl()
    {
        return m_BaseUrl;
    }

    private static ApiError newUnknownError(String msg)
    {
        ApiError error = new ApiError(ErrorCodes.UNKNOWN_ERROR);
        error.setMessage(msg);
        return error;
    }

    private String encode(String s)
    {
        try
        {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("Encoding error for " + s + ": " + e.getMessage());
        }
        return "";
    }
}
