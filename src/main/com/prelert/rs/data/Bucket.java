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
	public static final String RAW_ANOMALY_SCORE =  "rawAnomalyScore";
	public static final String ANOMALY_SCORE =  "anomalyScore";
	public static final String UNUSUAL_SCORE =  "unusualScore";
	public static final String RECORD_COUNT = "recordCount";
	public static final String EVENT_COUNT = "eventCount";
	public static final String DETECTORS = "detectors";
	public static final String RECORDS = "records";
	
	
	static public final String TYPE = "bucket";
	
	private String m_Id;
	private Date m_Timestamp;
	private double m_RawAnomalyScore;	
	private double m_AnomalyScore;	
	private double m_UnusualScore;	
	private int m_RecordCount;
	private List<Detector> m_Detectors;
	private long m_Epoch;
	private List<AnomalyRecord> m_Records;
	private long m_EventCount;
	
	public Bucket()
	{
		m_Detectors = new ArrayList<>();
	}
	
	/**
	 * The bucket Id is the bucket's timestamp in seconds 
	 * from the epoch. As the id is derived from the timestamp 
	 * field it doesn't need to be serialised.
	 *  
	 * @return The bucket id
	 */
	public String getId()
	{
		return m_Id;
	}
	
	public void setId(String id)
	{
		m_Id = id;
	}
	
	public long getEpoch()
	{
		return m_Epoch;
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
		
		m_Id = Long.toString(m_Epoch); 
	}
	
	
	public double getRawAnomalyScore() 
	{
		return m_RawAnomalyScore;
	}	

	public void setRawAnomalyScore(double rawAnomalyScore) 
	{
		this.m_RawAnomalyScore = rawAnomalyScore;
	}


	public double getAnomalyScore() 
	{
		return m_AnomalyScore;
	}	

	public void setAnomalyScore(double anomalyScore) 
	{
		this.m_AnomalyScore = anomalyScore;
	}


	public double getUnusualScore() 
	{
		return m_UnusualScore;
	}	

	public void setUnusualScore(double unusualScore) 
	{
		this.m_UnusualScore = unusualScore;
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
	
	/**
	 * The number of records (events) actually processed 
	 * in this bucket.
	 * @return
	 */
	public long getEventCount()
	{
		return m_EventCount;
	}
	
	public void setEventCount(long value)
	{
		m_EventCount = value;
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(m_RawAnomalyScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m_AnomalyScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m_UnusualScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((m_Detectors == null) ? 0 : m_Detectors.hashCode());
		result = prime * result + (int) (m_Epoch ^ (m_Epoch >>> 32));
		result = prime * result + ((m_Id == null) ? 0 : m_Id.hashCode());
		result = prime * result + m_RecordCount;
		result = prime * result
				+ ((m_Records == null) ? 0 : m_Records.hashCode());
		result = prime * result
				+ ((m_Timestamp == null) ? 0 : m_Timestamp.hashCode());
		return result;
	}

	/**
	 * Compare all the fields and embedded anomaly records
	 * (if any), does not compare detectors as they are not
	 * serialized anyway.
	 */
	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		
		if (other instanceof Bucket == false)
		{
			return false;
		}
		
		Bucket that = (Bucket)other;
		
		boolean equals = (this.m_Id.equals(that.m_Id)) &&
				(this.m_Timestamp.equals(that.m_Timestamp)) &&
				(this.m_EventCount == that.m_EventCount) &&
				(this.m_RawAnomalyScore == that.m_RawAnomalyScore) &&
				(this.m_AnomalyScore == that.m_AnomalyScore) &&
				(this.m_UnusualScore == that.m_UnusualScore) &&
				(this.m_RecordCount == that.m_RecordCount) &&
				(this.m_Epoch == that.m_Epoch);
		
		// don't bother testing detectors
		if (this.m_Records == null && that.m_Records == null)
		{
			equals &= true;
		}
		else if (this.m_Records != null && that.m_Records != null)
		{
			equals &= this.m_Records.size() == that.m_Records.size();
			for (int i=0; i<this.m_Records.size(); i++)
			{
				equals &= this.m_Records.get(i).equals(that.m_Records.get(i));
			}			
		}
		else
		{
			// one null the other not
			equals = false;
		}
		
		return equals;
	}
}
