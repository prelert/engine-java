/****************************************************************************
 *                                                                          *
 * Copyright 2015 Prelert Ltd                                               *
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
package com.prelert.job;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.prelert.job.transform.TransformConfig;

/**
 * This class represents a configured and created Job. The creation time is
 * set to the time the object was constructed, Status is set to
 * {@link JobStatus#RUNNING} and the finished time and last data time fields
 * are {@code null} until the job has seen some data or it is finished
 * respectively. If the job was created to read data from a list of files
 * FileUrls will be a non-empty list else the expects data to be streamed to it.
 */
@JsonInclude(Include.NON_NULL)
public class JobDetails
{
    public static final long DEFAULT_TIMEOUT = 600;
    public static final long DEFAULT_BUCKETSPAN = 300;

    /*
     * Field names used in serialization
     */
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String CREATE_TIME = "createTime";
    public static final String FINISHED_TIME = "finishedTime";
    public static final String LAST_DATA_TIME = "lastDataTime";

    public static final String TIMEOUT = "timeout";

    public static final String ANALYSIS_CONFIG = "analysisConfig";
    public static final String ANALYSIS_LIMITS = "analysisLimits";
    public static final String DATA_DESCRIPTION = "dataDescription";
    public static final String COUNTS = "counts";
    public static final String TRANSFORMS = "transforms";
    public static final String DESCRIPTION = "description";
    public static final String MODEL_DEBUG_CONFIG = "modelDebugConfig";

    public static final String TYPE = "job";

    private String m_JobId;
    private String m_Description;
    private JobStatus m_Status;

    private Date m_CreateTime;
    private Date m_FinishedTime;
    private Date m_LastDataTime;

    private long m_Timeout;

    private AnalysisConfig m_AnalysisConfig;
    private AnalysisLimits m_AnalysisLimits;
    private DataDescription m_DataDescription;
    private ModelSizeStats m_ModelSizeStats;
    private List<TransformConfig> m_Transforms;
    private ModelDebugConfig m_ModelDebugConfig;
    private DataCounts m_Counts;

    /* These URIs are transient they don't need to be persisted */
    private URI m_Location;
    private URI m_DataEndpoint;
    private URI m_BucketsEndpoint;
    private URI m_CategoryDefinitionsEndpoint;
    private URI m_RecordsEndpoint;
    private URI m_LogsEndpoint;
    private URI m_AlertsLongPollEndpoint;


    /**
     * Default constructor required for serialisation
     */
    public JobDetails()
    {
        m_Counts = new DataCounts();
        m_Status = JobStatus.CLOSED;
        m_CreateTime = new Date();
    }

    /**
     * Create a new Job with the passed <code>jobId</code> and the
     * configuration parameters, where fields are not set in the
     * JobConfiguration defaults will be used.
     *
     * @param jobId the job id
     * @param jobConfig the job configuration
     */
    public JobDetails(String jobId, JobConfiguration jobConfig)
    {
        this();

        m_JobId = jobId;
        m_Description = jobConfig.getDescription();
        m_Timeout = (jobConfig.getTimeout() != null) ? jobConfig.getTimeout() : DEFAULT_TIMEOUT;

        m_AnalysisConfig = jobConfig.getAnalysisConfig();
        m_AnalysisLimits = jobConfig.getAnalysisLimits();
        m_Transforms = jobConfig.getTransforms();
        m_ModelDebugConfig = jobConfig.getModelDebugConfig();

        m_DataDescription = jobConfig.getDataDescription();
        if (m_DataDescription == null)
        {
            m_DataDescription = new DataDescription();
        }
    }

    /**
     * Create a new Job with the passed <code>jobId</code> inheriting all the
     * values set in the <code>details</code> argument, any fields set in
     * <code>jobConfig</code> then override the settings in <code>details</code>.
     *
     * @param jobId the job id
     * @param details the job details
     * @param jobConfig the job configuration
     */

    public JobDetails(String jobId, JobDetails details, JobConfiguration jobConfig)
    {
        this();

        m_JobId = jobId;
        m_Status = JobStatus.CLOSED;
        m_CreateTime = new Date();

        m_Timeout = details.getTimeout();

        m_Description = details.getDescription();
        m_AnalysisConfig = details.getAnalysisConfig();
        m_AnalysisLimits = details.getAnalysisLimits();
        m_DataDescription = details.getDataDescription();
        m_Transforms = details.getTransforms();

        // only override these if explicitly set
        if (jobConfig.getTimeout() != null)
        {
            m_Timeout = jobConfig.getTimeout();
        }

        if (jobConfig.getAnalysisConfig() != null)
        {
            m_AnalysisConfig = jobConfig.getAnalysisConfig();
        }

        if (jobConfig.getAnalysisLimits() != null)
        {
            m_AnalysisLimits = jobConfig.getAnalysisLimits();
        }

        if (jobConfig.getDataDescription() != null)
        {
            m_DataDescription = jobConfig.getDataDescription();
        }

        if (jobConfig.getDescription() != null)
        {
            m_Description = jobConfig.getDescription();
        }

        if (jobConfig.getTransforms() != null)
        {
            m_Transforms = jobConfig.getTransforms();
        }
    }


    /**
     * Return the Job Id
     * @return The job Id string
     */
    public String getId()
    {
        return m_JobId;
    }

    /**
     * Set the job's Id.
     * In general this method should not be used as the Id does not change
     * once set. This method is provided for the Jackson object mapper to
     * de-serialise this class from Json.
     *
     * @param id the job id
     */
    public void setId(String id)
    {
        m_JobId = id;
    }

    /**
     * The job description
     * @return job description {@code String}
     */
    public String getDescription()
    {
        return m_Description;
    }

    public void setDescription(String description)
    {
        m_Description = description;
    }

    /**
     * Return the Job Status. Jobs are initialised to {@link JobStatus#CLOSED}
     * when created and move into the @link JobStatus#RUNNING} state when
     * processing data. Once data has been processed the status will be
     * either {@link JobStatus#CLOSED} or {@link JobStatus#FAILED}
     *
     * @return The job's status
     */
    public JobStatus getStatus()
    {
        return m_Status;
    }

    public void setStatus(JobStatus status)
    {
        m_Status = status;
    }

    /**
     * The Job creation time.
     * @return The date the job was created
     */
    public Date getCreateTime()
    {
        return m_CreateTime;
    }

    public void setCreateTime(Date time)
    {
        m_CreateTime = time;
    }

    /**
     * The time the job was finished or <code>null</code> if not finished.
     * @return The date the job was last retired or <code>null</code>
     */
    public Date getFinishedTime()
    {
        return m_FinishedTime;
    }

    public void setFinishedTime(Date finishedTime)
    {
        m_FinishedTime = finishedTime;
    }

    /**
     * The last time data was uploaded to the job or <code>null</code>
     * if no data has been seen.
     * @return The data at which the last data was processed
     */
    public Date getLastDataTime()
    {
        return m_LastDataTime;
    }

    public void setLastDataTime(Date lastTime)
    {
        m_LastDataTime = lastTime;
    }


    /**
     * The job timeout setting in seconds. Jobs are retired if they do not
     * receive data for this period of time.
     * The default is 600 seconds
     * @return The timeout period in seconds
     */
    public long getTimeout()
    {
        return m_Timeout;
    }

    public void setTimeout(long timeout)
    {
        m_Timeout = timeout;
    }


    /**
     * The analysis configuration object
     * @return The AnalysisConfig
     */
    public AnalysisConfig getAnalysisConfig()
    {
        return m_AnalysisConfig;
    }

    public void setAnalysisConfig(AnalysisConfig config)
    {
        m_AnalysisConfig = config;
    }

    /**
     * The analysis options object
     * @return The AnalysisLimits
     */
    public AnalysisLimits getAnalysisLimits()
    {
        return m_AnalysisLimits;
    }

    public void setAnalysisLimits(AnalysisLimits options)
    {
        m_AnalysisLimits = options;
    }

    public ModelDebugConfig getModelDebugConfig()
    {
        return m_ModelDebugConfig;
    }

    public void setModelDebugConfig(ModelDebugConfig modelDebugConfig)
    {
        m_ModelDebugConfig = modelDebugConfig;
    }

    /**
    * The memory usage object
    * @return The ModelSizeStats
    */
    public ModelSizeStats getModelSizeStats()
    {
        return m_ModelSizeStats;
    }

    public void setModelSizeStats(ModelSizeStats modelSizeStats)
    {
        m_ModelSizeStats = modelSizeStats;
    }

    /**
     * If not set the input data is assumed to be csv with a '_time' field
     * in epoch format.
     * @return A DataDescription or <code>null</code>
     * @see DataDescription
     */
    public DataDescription getDataDescription()
    {
        return m_DataDescription;
    }

    public void setDataDescription(DataDescription dd)
    {
        m_DataDescription = dd;
    }

    public List<TransformConfig> getTransforms()
    {
        return m_Transforms;
    }

    public void setTransforms(List<TransformConfig> transforms)
    {
        m_Transforms = transforms;
    }

    /**
     * The URI of this resource
     * @return The URI of this job
     */
    public URI getLocation()
    {
        return m_Location;
    }

    /**
     * Set the URI path of this resource
     * 
     * @param location the location {@code URI}
     */
    public void setLocation(URI location)
    {
        m_Location = location;
    }

    /**
     * This Job's data endpoint as the full URL
     *
     * @return The Job's data URI
     */
    public URI getDataEndpoint()
    {
        return m_DataEndpoint;
    }

    /**
     * Set this Job's data endpoint
     * 
     * @param value the value {@code URI}
     */
    public void setDataEndpoint(URI value)
    {
        m_DataEndpoint = value;
    }

    /**
     * This Job's buckets endpoint as the full URL path
     *
     * @return The Job's buckets URI
     */
    public URI getBucketsEndpoint()
    {
        return m_BucketsEndpoint;
    }

    /**
     * Set this Job's buckets endpoint
     * 
     * @param results the results {@code URI}
     */
    public void setBucketsEndpoint(URI results)
    {
        m_BucketsEndpoint = results;
    }

    public URI getCategoryDefinitionsEndpoint()
    {
        return m_CategoryDefinitionsEndpoint;
    }

    public void setCategoryDefinitionsEndpoint(URI categoryDefinitions)
    {
        m_CategoryDefinitionsEndpoint = categoryDefinitions;
    }

    /**
     * This Job's results endpoint as the full URL path
     *
     * @return The Job's results URI
     */
    public URI getRecordsEndpoint()
    {
        return m_RecordsEndpoint;
    }

    /**
     * Set this Job's records endpoint
     * 
     * @param results the results {@code URI}
     */
    public void setRecordsEndpoint(URI results)
    {
        m_RecordsEndpoint = results;
    }


    /**
     * This Job's logs endpoint as the full URL
     *
     * @return The Job's logs URI
     */
    public URI getLogsEndpoint()
    {
        return m_LogsEndpoint;
    }

    /**
     * Set this Job's logs endpoint
     * 
     * @param value the value {@code URI}
     */
    public void setLogsEndpoint(URI value)
    {
        m_LogsEndpoint = value;
    }

    /**
     * This Job's alert long poll endpoint
     *
     * @return The Job's logs URI
     */
    public URI getAlertsLongPollEndpoint()
    {
        return m_AlertsLongPollEndpoint;
    }

    /**
     * Set this Job's alert long poll endpoint
     * 
     * @param value the value {@code URI}
     */
    public void setAlertsLongPollEndpoint(URI value)
    {
        m_AlertsLongPollEndpoint = value;
    }

    /**
     * Processed records count
     * @return the processed records counts {@code DataCounts}
     */
    public DataCounts getCounts()
    {
        return m_Counts;
    }

    /**
     * Processed records count
     * 
     * @param counts the counts {@code DataCounts}
     */
    public void setCounts(DataCounts counts)
    {
        m_Counts = counts;
    }

    /**
     * Prints the more salient fields in a JSON-like format suitable for logging.
     * If every field was written it would spam the log file.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("{");
        sb.append("id:").append(getId())
            .append(" description:").append(getDescription())
            .append(" status:").append(getStatus())
            .append(" createTime:").append(getCreateTime())
            .append(" lastDataTime:").append(getLastDataTime())
            .append("}");

        return sb.toString();
    }

    /**
     * Equality test
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof JobDetails == false)
        {
            return false;
        }

        JobDetails that = (JobDetails)other;

        return Objects.equals(this.m_JobId, that.m_JobId) &&
                Objects.equals(this.m_Description, that.m_Description) &&
                (this.m_Status == that.m_Status) &&
                Objects.equals(this.m_CreateTime, that.m_CreateTime) &&
                Objects.equals(this.m_FinishedTime, that.m_FinishedTime) &&
                Objects.equals(this.m_LastDataTime, that.m_LastDataTime) &&
                (this.m_Timeout == that.m_Timeout) &&
                Objects.equals(this.m_AnalysisConfig, that.m_AnalysisConfig) &&
                Objects.equals(this.m_AnalysisLimits, that.m_AnalysisLimits) &&
                Objects.equals(this.m_DataDescription, that.m_DataDescription) &&
                Objects.equals(this.m_ModelDebugConfig, that.m_ModelDebugConfig) &&
                Objects.equals(this.m_ModelSizeStats, that.m_ModelSizeStats) &&
                Objects.equals(this.m_Transforms, that.m_Transforms) &&
                Objects.equals(this.m_Counts, that.m_Counts) &&
                Objects.equals(this.m_Location, that.m_Location) &&
                Objects.equals(this.m_DataEndpoint, that.m_DataEndpoint) &&
                Objects.equals(this.m_CategoryDefinitionsEndpoint, that.m_CategoryDefinitionsEndpoint) &&
                Objects.equals(this.m_BucketsEndpoint, that.m_BucketsEndpoint) &&
                Objects.equals(this.m_AlertsLongPollEndpoint, that.m_AlertsLongPollEndpoint) &&
                Objects.equals(this.m_RecordsEndpoint, that.m_RecordsEndpoint);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_JobId, m_Description, m_Status, m_CreateTime, m_FinishedTime,
                m_LastDataTime, m_Timeout, m_AnalysisConfig, m_AnalysisLimits, m_DataDescription,
                m_ModelDebugConfig, m_ModelSizeStats, m_Transforms, m_Counts, m_Location,
                m_DataEndpoint, m_CategoryDefinitionsEndpoint, m_BucketsEndpoint, m_RecordsEndpoint,
                m_AlertsLongPollEndpoint);
    }
}
