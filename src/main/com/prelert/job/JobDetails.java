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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

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
	static final public boolean DEFAULT_PERSIST_MODEL = true;
	
	/**
	 * Field names used in serialisation
	 */
	static final public String ID = "id";
	static final public String STATUS = "status";
	static final public String CREATE_TIME = "createTime";
	static final public String FINISHED_TIME = "finishedTime";
	static final public String LAST_DATA_TIME = "lastDataTime";
	static final public String TIMEOUT = "timeout";
	static final public String PERSIST_MODEL = "persistModel";
	static final public String ANALYSIS_CONFIG = "analysisConfig";
	static final public String ANALYSIS_LIMITS = "analysisLimits";
	static final public String DATA_DESCRIPTION = "dataDescription";
	
	static final public String TYPE = "job";
	
	private String m_JobId;
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
	private URI m_ResultsEndpoint;
	private URI m_LogsEndpoint;
		
	/**
	 * Default constructor required for serialisation
	 */
	public JobDetails()
	{
		
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
		m_JobId = jobId;
		m_Status = JobStatus.RUNNING;
		m_CreateTime = new Date();		
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
		m_JobId = jobId;
		m_Status = JobStatus.RUNNING;
		m_CreateTime = new Date();		
		
		m_Timeout = details.getTimeout();		
									
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
	}
	
	/**
	 * Create a JobDetails from a map of values.
	 * WARNING THE OBJECT MAY BE IN AN INCONSITENT STATE AFTER THIS CONSTRUCTOR
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	public JobDetails(Map<String, Object> values)
	{
		final DateFormat isoDateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		isoDateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		if (values.containsKey(ID))
		{
			m_JobId = values.get(ID).toString();
		}
		if (values.containsKey(STATUS))
		{
			m_Status = JobStatus.valueOf(values.get(STATUS).toString());
		}
		if (values.containsKey(CREATE_TIME))
		{
			Object obj = values.get(CREATE_TIME);
			if (obj != null)
			{
				try 
				{
					m_CreateTime = isoDateParser.parse(obj.toString());
				}
				catch (ParseException e) 
				{
					s_Logger.error("Cannot parse create date from '" + 
								obj.toString() + "'");
				}
			}
		}
		if (values.containsKey(FINISHED_TIME))
		{
			Object obj = values.get(FINISHED_TIME);
			if (obj != null)
			{
				try 
				{
					m_FinishedTime = isoDateParser.parse(obj.toString());
				}
				catch (ParseException e) 
				{
					s_Logger.error("Cannot parse finish date from '" + 
								obj.toString() + "'");
				}
			}
		}
		if (values.containsKey(LAST_DATA_TIME))
		{
			Object obj = values.get(LAST_DATA_TIME);
			if (obj != null)
			{
				try 
				{
					m_LastDataTime = isoDateParser.parse(obj.toString());
				}
				catch (ParseException e) 
				{
					s_Logger.error("Cannot parse last data date from '" + 
								obj.toString() + "'");
				}
			}			
		}
		if (values.containsKey(TIMEOUT))
		{
			m_Timeout = (Integer)values.get(TIMEOUT);
		}
		if (values.containsKey(ANALYSIS_CONFIG))
		{
			Object obj = values.get(ANALYSIS_CONFIG);
			if (obj != null && obj instanceof Map)
			{
				m_AnalysisConfig = new AnalysisConfig((Map<String, Object>)obj);
			}
		}
		if (values.containsKey(ANALYSIS_LIMITS))
		{
			Object obj = values.get(ANALYSIS_LIMITS);
			if (obj != null && obj instanceof Map)
			{
				m_AnalysisLimits = new AnalysisLimits((Map<String, Object>)obj); 
			}
		}
		
		if (values.containsKey(DATA_DESCRIPTION))
		{
			Object obj = values.get(DATA_DESCRIPTION);
			if (obj != null && obj instanceof Map)
			{
				m_DataDescription = new DataDescription((Map<String, Object>)obj);
			}
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
	 * Return the Job Status. Jobs are initialised to {@link JobStatus#RUNNING}
	 * when created.
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
	 * This Job's results endpoint as the full URL path
	 * 
	 * @return The Job's results URI
	 */
	public URI getResultsEndpoint() 
	{
		return m_ResultsEndpoint;
	}	
	
	/**
	 * Set this Job's results endpoint
	 */
	public void setResultsEndpoint(URI results) 
	{
		m_ResultsEndpoint = results;
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
	 * Prints the more salient fields in a JSON-like format suitable for logging.
	 * If every field was written it woul spam the log file.
	 */
	@Override 
	public String toString()
	{
		StringBuilder sb = new StringBuilder("{");
		sb.append("id:").append(getId())
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
				(this.m_Status == that.m_Status) &&			
				bothNullOrEqual(this.m_CreateTime, that.m_CreateTime) &&
				bothNullOrEqual(this.m_FinishedTime, that.m_FinishedTime) &&
				bothNullOrEqual(this.m_LastDataTime, that.m_LastDataTime) &&
				(this.m_Timeout == that.m_Timeout) &&
				bothNullOrEqual(this.m_AnalysisConfig, that.m_AnalysisConfig) &&
				bothNullOrEqual(this.m_AnalysisLimits, that.m_AnalysisLimits) &&
				bothNullOrEqual(this.m_DataDescription, that.m_DataDescription) &&
				bothNullOrEqual(this.m_Location, that.m_Location) &&
				bothNullOrEqual(this.m_DataEndpoint, that.m_DataEndpoint) &&
				bothNullOrEqual(this.m_ResultsEndpoint, that.m_ResultsEndpoint);				
	}
}
