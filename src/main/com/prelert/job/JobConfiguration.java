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

/**
 * This class encapsulates all the data required to create a new job it
 * does not represent the state of a created job (see {@linkplain JobDetails}
 * for that).
 * <p/>
 * If a value has not been set it will be <code>null</code> Object wrappers 
 * are used around integral types & booleans so they can take <code>null</code> 
 * values.
 */
public class JobConfiguration 
{
	
	private AnalysisConfig m_AnalysisConfig;
	private AnalysisLimits m_AnalysisLimits;
	private DataDescription m_DataDescription;
	private String m_ReferenceJobId;
	private Long m_Timeout;
	
	
	public JobConfiguration()
	{
	}
	
	public JobConfiguration(String jobReferenceId)
	{
		this();
		m_ReferenceJobId = jobReferenceId;
	}
	
	public JobConfiguration(AnalysisConfig analysisConfig)
	{
		this();
		m_AnalysisConfig = analysisConfig;
	}
	
	/**
	 * The analysis configuration. A properly configured job must have 
	 * a valid AnalysisConfig
	 * @return AnalysisConfig or null if not set.
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
	 * The analysis limits 
	 * 
	 * @return Analysis limits or null if not set.
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
	 * If the job is to be created with the same configuration as a previously
	 * run job then this is the id of that job. If set then this option
	 * overrides the {@linkplain #getAnalysisConfig()} settings i.e. they will
	 * be ignored.
	 * @return A String or <code>null</code> if not set
	 */
	public String getReferenceJobId() 
	{
		return m_ReferenceJobId;
	}

	public void setReferenceJobId(String refId) 
	{
		m_ReferenceJobId = refId;
	}
	
	/**
	 * The timeout period for the job in seconds
	 * @return The timeout in seconds
	 */
	public Long getTimeout() 
	{
		return m_Timeout;
	}
	
	public void setTimeout(Long timeout) 
	{
		m_Timeout = timeout;
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
	
	public void setDataDescription(DataDescription description)
	{
		m_DataDescription = description;
	}
	
	
	/**
	 * Builder for constructing JobConfiguration instances.
	 */
	static public class JobConfigurationBuilder
	{
		private JobConfiguration m_JobConfig;
		
		public JobConfigurationBuilder(String jobReferenceId)
		{
			m_JobConfig = new JobConfiguration(jobReferenceId);
		}
		
		public JobConfigurationBuilder(AnalysisConfig analysisConfig)
		{
			m_JobConfig = new JobConfiguration(analysisConfig);
		}
				
		public JobConfigurationBuilder analysisLimits(AnalysisLimits analysisLimits)
		{		
			m_JobConfig.m_AnalysisLimits = analysisLimits;
			return this;
		}
		
		public JobConfigurationBuilder dataDescription(DataDescription dataDescription)
		{		
			m_JobConfig.m_DataDescription = dataDescription;
			return this;
		}
				
		public JobConfigurationBuilder timeout(Long timeout)
		{
			m_JobConfig.m_Timeout = timeout;
			return this;
		}
		
		public JobConfiguration build()
		{
			return m_JobConfig;
		}
	}

}
