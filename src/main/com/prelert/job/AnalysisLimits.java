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

import java.util.Map;

/**
 * Analysis limits for autodetect (max field values, max time buckets). 
 * 
 * If an option has not been set it's value will be 0 in which case it
 * shouldn't be used so the default value is picked up instead.
 */
public class AnalysisLimits 
{
	/**
	 * Serialisation field names
	 */
	static final public String MAX_FIELD_VALUES = "maxFieldValues";
	static final public String MAX_TIME_BUCKETS = "maxTimeBuckets";
	
	private long m_MaxFieldValues;
	private long m_MaxTimeBuckets;
	
	/**
	 * Initialise values to 0.
	 * If the values are 0 they haven't been set 
	 */	
	public AnalysisLimits()
	{
		m_MaxFieldValues = 0;
		m_MaxTimeBuckets = 0;
	}
	
	public AnalysisLimits(long maxFieldValues, long maxTimeBuckets)
	{
		m_MaxFieldValues = maxFieldValues;
		m_MaxTimeBuckets = maxTimeBuckets;
	}
	
	/**
	 * Create and set field values from the Map.
	 * @param values
	 */
	public AnalysisLimits(Map<String, Object> values)
	{
		this();
		
		if (values.containsKey(MAX_FIELD_VALUES))
		{
			Object obj = values.get(MAX_FIELD_VALUES);
			if (obj != null)
			{
				m_MaxFieldValues = ((Number)obj).longValue();
			}
		}	
		if (values.containsKey(MAX_TIME_BUCKETS))
		{
			Object obj = values.get(MAX_TIME_BUCKETS);
			if (obj != null)
			{
				m_MaxTimeBuckets = ((Number)obj).longValue();
			}
		}		
		
	}
		
	/**
	 * Maximum number of distinct values of a single field before analysis
	 * of that field will be halted. If 0 then this is an invalid and the
	 * native process's default will be used. 
	 * @return The max distinct values in a single field
	 */
	public long getMaxFieldValues()
	{
		return m_MaxFieldValues;
	}
	
	public void setMaxFieldValues(long value)
	{
		m_MaxFieldValues = value;
	}
	
	/**
	 *  Maximum number of time buckets to process during anomaly detection 
	 *  before ceasing to output results. If 0 then this is an invalid and the
	 *  native process's default will be used. 
	 *  
	 * @return The max number of buckets to process in the job
	 */
	public long getMaxTimeBuckets()
	{
		return m_MaxTimeBuckets;
	}
	
	public void setMaxTimeBuckets(long value)
	{
		m_MaxTimeBuckets = value;
	}
	
	
	/**
	 * Overridden equality test
	 */
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		
		if (other instanceof AnalysisLimits == false)
		{
			return false;
		}
		
		AnalysisLimits that = (AnalysisLimits)other;
		return (this.m_MaxFieldValues == that.m_MaxFieldValues) &&
				(this.m_MaxTimeBuckets == that.m_MaxTimeBuckets);
	}
	
}
