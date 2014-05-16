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

package com.prelert.rs.data;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Bucket Result POJO 
 */
@JsonIgnoreProperties({"epoch", "epochString", "id"})
@JsonInclude(Include.NON_NULL)
public class Bucket 
{
	/*
	 * Field Names
	 */
	public static final String ID = "id";
	public static final String TIMESTAMP = "timestamp";
	public static final String ANOMALY_SCORE =  "anomalyScore";
	public static final String RECORD_COUNT = "recordCount";
	public static final String DETECTORS = "detectors";
	public static final String RECORDS = "records";
	
	
	static public final String TYPE = "bucket";
	
	private Date m_Timestamp;
	private double m_AnomalyScore;	
	private int m_RecordCount;
	private List<Detector> m_Detectors;
	private long m_Epoch;
	private List<AnomalyRecord> m_Records; 
	
	public Bucket()
	{
		m_Records = new ArrayList<>();
		m_Detectors = new ArrayList<>();
	}
	
	/**
	 * The bucket Id is the bucket's timestamp in seconds 
	 * from the epoch. As the id is derived from the timestamp 
	 * field it doesn't need to be serialised.
	 *  
	 * @return The bucket id
	 */
	public long getEpoch()
	{
		return m_Epoch;
	}
	
	/**
	 * Get the epoch as a string
	 */
	public String getEpochString()
	{
		return Long.toString(m_Epoch);
	}
	
	/**
	 * Same as getEpochString but serialised as the 'id' field. 
	 * @return
	 */
	public String getId()
	{
		return Long.toString(m_Epoch);
	}
	
	public Date getTimestamp() 
	{
		return m_Timestamp;
	}
	
	public void setTimestamp(Date timestamp) 
	{
		this.m_Timestamp = timestamp;
		
		// epoch in seconds
		m_Epoch = m_Timestamp.getTime() / 1000;
	}
	
	public double getAnomalyScore() 
	{
		return m_AnomalyScore;
	}	

	public void setAnomalyScore(double anomalyScore) 
	{
		this.m_AnomalyScore = anomalyScore;
	}
	
	public int getRecordCount() 
	{
		return m_RecordCount;
	}
			
	public void setRecordCount(int recordCount) 
	{
		this.m_RecordCount = recordCount;
	}
	
	/**
	 * Get the list of detectors that produced output in this bucket
	 * 
	 * @return A list of detector 
	 */	
	public List<Detector> getDetectors()
	{
		return m_Detectors;
	}
	
	public void setDetectors(List<Detector> detectors)
	{
		m_Detectors = detectors;
	}
	
	
	/**
	 * Get all the anomaly records associated with this bucket
	 * @return All the anomaly records
	 */
	public List<AnomalyRecord> getRecords()
	{
		return m_Records;
	}
		
	public void setRecords(List<AnomalyRecord> records)
	{
		m_Records = records;
	}
	
}
