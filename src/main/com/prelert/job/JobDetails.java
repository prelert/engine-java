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
package com.prelert.job;

import java.net.URI;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This class represents a configured and created Job. The creation time is 
 * set to the time the object was constructed, Status is set to 
 * {@link JobStatus#RUNNING} and the finished time and last data time fields 
 * are <code>null</code> until the job has seen some data or it is finished 
 * respectively. If the job was created to read data from a list of files
 * FileUrls will be a non-empty list else the expects data to be streamed to it. 
 */
public class JobDetails 
{		
	static final public Logger s_Logger = Logger.getLogger(JobDetails.class);

	static final public long DEFAULT_TIMEOUT = 600;
	static final public long DEFAULT_BUCKETSPAN = 300;
	
	/*
	 * Field names used in serialisation
	 */
	static final public String ID = "id";
	static final public String STATUS = "status";
	static final public String CREATE_TIME = "createTime";
	static final public String FINISHED_TIME = "finishedTime";
	static final public String LAST_DATA_TIME = "lastDataTime";
	
	static final public String COUNTS = "counts";
	static final public String BUCKET_COUNT = "bucketCount";
	static final public String PROCESSED_RECORD_COUNT = "processedRecordCount";
	static final public String PROCESSED_FIELD_COUNT = "processedFieldCount";
	static final public String INPUT_BYTES = "inputBytes";
	static final public String INPUT_RECORD_COUNT = "inputRecordCount";
	static final public String INPUT_FIELD_COUNT = "inputFieldCount";
	static final public String INVALID_DATE_COUNT = "invalidDateCount";
	static final public String MISSING_FIELD_COUNT = "missingFieldCount";
	static final public String OUT_OF_ORDER_TIME_COUNT = "outOfOrderTimeStampCount";
	
	static final public String TIMEOUT = "timeout";
	
    static final public String ANALYSIS_CONFIG = "analysisConfig";
	static final public String ANALYSIS_LIMITS = "analysisLimits";
	static final public String DATA_DESCRIPTION = "dataDescription";
	
	static final public String DESCRIPTION = "description";
	
	static final public String TYPE = "job";
	
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
	
	/* These URIs are transient they don't need to be persisted */
	private URI m_Location;
	private URI m_DataEndpoint;
	private URI m_BucketsEndpoint;
	private URI m_RecordsEndpoint;
	private URI m_LogsEndpoint;

	private Counts m_Counts;
	
		
	/**
	 * Default constructor required for serialisation
	 */
	public JobDetails()
	{
		m_Counts = new Counts();
		m_Status = JobStatus.CLOSED;
		m_CreateTime = new Date();		
	}
	
	/**
	 * Create a new Job with the passed <code>jobId</code> and the 
	 * configuration parameters, where fields are not set in the 
	 * JobConfiguration defaults will be used. 
	 * 
	 * @param jobId
	 * @param jobConfig
	 */
	public JobDetails(String jobId, JobConfiguration jobConfig)
	{
		this();
		
		m_JobId = jobId;
		m_Description = jobConfig.getDescription();
		m_Timeout = (jobConfig.getTimeout() != null) ? jobConfig.getTimeout() : DEFAULT_TIMEOUT; 		
						
		m_AnalysisConfig = jobConfig.getAnalysisConfig();
		m_AnalysisLimits = jobConfig.getAnalysisLimits();
		m_DataDescription = jobConfig.getDataDescription();
	}
	
	/**
	 * Create a new Job with the passed <code>jobId</code> inheriting all the 
	 * values set in the <code>details</code> argument, any fields set in 
	 * <code>jobConfig</code> then override the settings in <code>details</code>.
	 * 
	 * @param jobId
	 * @param details
	 * @param jobConfig
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
	 * @param id
	 */
	public void setId(String id)
	{
		m_JobId = id;
	}
	
	/**
	 * The job description
	 * @return
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
	 */
	public void setBucketsEndpoint(URI results) 
	{
		m_BucketsEndpoint = results;
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
	 */
	public void setLogsEndpoint(URI value) 
	{
		m_LogsEndpoint = value;
	}	
	
	/**
	 * Processed records count
	 * @return
	 */
	public Counts getCounts()
	{
		return m_Counts;
	}
	
	/**
	 * Processed records count
	 * @return
	 */
	public void setCounts(Counts counts)
	{
		m_Counts = counts;
	}
		
	/**
	 * Prints the more salient fields in a JSON-like format suitable for logging.
	 * If every field was written it woul spam the log file.
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
	 * Helper function returns true if the objects are equal or
	 * both null.
	 * 
	 * @param o1
	 * @param o2
	 * @return True if both null or both are non-null and equal
	 */
	public static <T> boolean bothNullOrEqual(T o1, T o2)
	{
		if (o1 == null && o2 == null)
		{
			return true;
		}
		
		if (o1 == null || o2 == null)
		{
			return false;
		}
		
		return o1.equals(o2);		
	}
	
	
	/**
	 * Equality test
	 */
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
		
		return bothNullOrEqual(this.m_JobId, that.m_JobId) &&
				bothNullOrEqual(this.m_Description, that.m_Description) &&
				(this.m_Status == that.m_Status) &&			
				bothNullOrEqual(this.m_CreateTime, that.m_CreateTime) &&
				bothNullOrEqual(this.m_FinishedTime, that.m_FinishedTime) &&
				bothNullOrEqual(this.m_LastDataTime, that.m_LastDataTime) &&
				this.m_Counts.equals(that.m_Counts) &&
				(this.m_Timeout == that.m_Timeout) &&
				bothNullOrEqual(this.m_AnalysisConfig, that.m_AnalysisConfig) &&
				bothNullOrEqual(this.m_AnalysisLimits, that.m_AnalysisLimits) &&
				bothNullOrEqual(this.m_DataDescription, that.m_DataDescription) &&
				bothNullOrEqual(this.m_Location, that.m_Location) &&
				bothNullOrEqual(this.m_DataEndpoint, that.m_DataEndpoint) &&
				bothNullOrEqual(this.m_BucketsEndpoint, that.m_BucketsEndpoint) &&				
				bothNullOrEqual(this.m_RecordsEndpoint, that.m_RecordsEndpoint);				
	}
	
	
	/**
	 * Job processed record counts
	 */
	public class Counts
	{
		private long m_BucketCount;
		private long m_ProcessedRecordCount;
		private long m_ProcessedFieldCount;
		private long m_InputRecordCount;
		private long m_InputBytes;
		private long m_InputFieldCount;
		private long m_InvalidDateCount;
		private long m_MissingFieldCount;
		private long m_OutOfOrderTimeStampCount;
		
		
		/**
		 * The number of bucket results
		 * @return
		 */
		public long getBucketCount()
		{
			return m_BucketCount;			
		}
		
		public void setBucketCount(long count)
		{
			m_BucketCount = count;
		}
		
		/**
		 * Number of records processed by this job.
		 * This value is the number of records sent to the job
		 * including any records that my be discarded for any 
		 * reason e.g. because the date cannot be read
		 * @return 
		 */
		public long getProcessedRecordCount() 
		{
			return m_ProcessedRecordCount;
		}
		
		public void setProcessedRecordCount(long count) 
		{
			m_ProcessedRecordCount = count;
		}
		
		/**
		 * Number of data points (processed record count * the number 
		 * of analysed fields) processed by this job. This count does
		 * not include the time field.
		 * @return 
		 */
		public long getProcessedFieldCount() 
		{
			return m_ProcessedFieldCount;
		}
		
		public void setProcessedFieldCount(long count) 
		{
			m_ProcessedFieldCount = count;
		}		
		
		/**
		 * Total number of input records
		 * @return
		 */
		public long getInputRecordCount()
		{
			return m_InputRecordCount;
		}
		
		public void setInputRecordCount(long count)
		{
			m_InputRecordCount = count;
		}
		
		/**
		 * The total number of bytes sent to this job.
		 * This value includes the bytes from any  records 
		 * that have been discarded for any  reason 
		 * e.g. because the date cannot be read
		 * @return Volume in bytes
		 */
		public long getInputBytes()
		{
			return m_InputBytes;
		}
		
		public void setInputBytes(long volume)
		{
			m_InputBytes = volume;
		}
		
		/**
		 * The total number of fields sent to the job
		 * including fields that aren't analysed.
		 * @return
		 */
		public long getInputFieldCount()
		{
			return m_InputFieldCount;
		}
		
		public void setInputFieldCount(long volume)
		{
			m_InputFieldCount = volume;
		}
		
		
		/**
		 * The number of records with an invalid date field that could
		 * not be parsed or converted to epoch time.
		 * @return
		 */
		public long getInvalidDateCount() 
		{
			return m_InvalidDateCount;
		}
		
		public void setInvalidDateCount(long count) 
		{
			m_InvalidDateCount = count;
		}
		
		/**
		 * The number of records missing a field that had been 
		 * configured for analysis.
		 * @return
		 */
		public long getMissingFieldCount() 
		{
			return m_MissingFieldCount;
		}
		
		public void setMissingFieldCount(long count) 
		{
			m_MissingFieldCount = count;
		}
		
		/**
		 * The number of records with a timestamp that is
		 * before the time of the latest record. Records should 
		 * be in ascending chronological order
		 * @return
		 */
		public long getOutOfOrderTimeStampCount() 
		{
			return m_OutOfOrderTimeStampCount;
		}
		
		public void setOutOfOrderTimeStampCount(long count) 
		{
			m_OutOfOrderTimeStampCount = count;
		}	
		
		/**
		 * Equality test
		 */
		public boolean equals(Object other)
		{
			if (this == other)
			{
				return true;
			}
			
			if (other instanceof Counts == false)
			{
				return false;
			}
			
			Counts that = (Counts)other;

			return this.m_BucketCount == that.m_BucketCount &&
					this.m_ProcessedRecordCount == that.m_ProcessedRecordCount &&
					this.m_ProcessedFieldCount == that.m_ProcessedFieldCount &&
					this.m_InputBytes == that.m_InputBytes &&
					this.m_InputFieldCount == that.m_InputFieldCount &&
					this.m_InputRecordCount == that.m_InputRecordCount &&
					this.m_InvalidDateCount == that.m_InvalidDateCount &&
					this.m_MissingFieldCount == that.m_MissingFieldCount &&
					this.m_OutOfOrderTimeStampCount == that.m_OutOfOrderTimeStampCount;
		}
	}	
}
