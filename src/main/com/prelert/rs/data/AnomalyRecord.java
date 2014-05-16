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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Anomaly Record POJO.
 * Uses the object wrappers Boolean and Double so <code>null</code> values
 * can be returned if the members have not been set.
 */
@JsonInclude(Include.NON_NULL)
public class AnomalyRecord
{
	/**
	 * Serialisation fields
	 */
	static final public String DETECTOR_NAME = "detectorName";
	static final public String TYPE = "record";

	/**
	 * Result fields (all detector types)
	 */
	static final public String ANOMALY_SCORE =  "anomalyScore";
	static final public String PROBABILITY = "probability";
	static final public String BY_FIELD_NAME = "byFieldName";
	static final public String BY_FIELD_VALUE = "byFieldValue";
	static final public String PARTITION_FIELD_NAME = "partitionFieldName";
	static final public String PARTITION_FIELD_VALUE = "partitionFieldValue";
	static final public String FUNCTION = "function";
	static final public String TYPICAL = "typical";
	static final public String ACTUAL = "actual";

	/**
	 * Metric Results (including population metrics)
	 */
	static final public String FIELD_NAME = "fieldName";

	/**
	 * Population results
	 */
	static final public String OVER_FIELD_NAME = "overFieldName";
	static final public String OVER_FIELD_VALUE = "overFieldValue";
	static final public String IS_OVERALL_RESULT = "isOverallResult";

	/**
	 * Simple count detector
	 */
	static final public String IS_SIMPLE_COUNT = "isSimpleCount";

	private String m_DetectorName;

	private Double m_AnomalyScore;
	private Double m_Probability;
	private String m_ByFieldName;
	private String m_ByFieldValue;
	private String m_PartitionFieldName;
	private String m_PartitionFieldValue;
	private String m_Function;
	private Double m_Typical;
	private Double m_Actual;

	private String m_FieldName;

	private String m_OverFieldName;
	private String m_OverFieldValue;
	private Boolean m_IsOverallResult;

	private Boolean m_IsSimpleCount;


	public Double getAnomalyScore()
	{
		return m_AnomalyScore;
	}

	public void setAnomalyScore(Double anomalyScore)
	{
		m_AnomalyScore = anomalyScore;
	}

	public Double getProbability()
	{
		return m_Probability;
	}

	public void setProbability(Double value)
	{
		m_Probability = value;
	}


	public String getByFieldName()
	{
		return m_ByFieldName;
	}

	public void setByFieldName(String value)
	{
		m_ByFieldName = value;
	}

	public String getByFieldValue()
	{
		return m_ByFieldValue;
	}

	public void setByFieldValue(String value)
	{
		m_ByFieldValue = value;
	}

	public String getPartitionFieldName()
	{
		return m_PartitionFieldName;
	}

	public void setPartitionFieldName(String field)
	{
		m_PartitionFieldName = field;
	}

	public String getPartitionFieldValue()
	{
		return m_PartitionFieldValue;
	}

	public void setPartitionFieldValue(String value)
	{
		m_PartitionFieldValue = value;
	}

	public String getFunction()
	{
		return m_Function;
	}

	public void setFunction(String name)
	{
		m_Function = name;
	}

	public Double getTypical()
	{
		return m_Typical;
	}

	public void setTypical(Double typical)
	{
		m_Typical = typical;
	}

	public Double getActual()
	{
		return m_Actual;
	}

	public void setActual(Double actual)
	{
		m_Actual = actual;
	}

	public String getFieldName()
	{
		return m_FieldName;
	}

	public void setFieldName(String field)
	{
		m_FieldName = field;
	}

	public String getOverFieldName()
	{
		return m_OverFieldName;
	}

	public void setOverFieldName(String name)
	{
		m_OverFieldName = name;
	}

	public String getOverFieldValue()
	{
		return m_OverFieldValue;
	}

	public void setOverFieldValue(String value)
	{
		m_OverFieldValue = value;
	}


	public Boolean isOverallResult()
	{
		return m_IsOverallResult;
	}

	public void setOverallResult(boolean value)
	{
		m_IsOverallResult = value;
	}

	
	public String getDetectorName()
	{
		return m_DetectorName;
	}

	public void setDetectorName(String name)
	{
		m_DetectorName = name;
	}
	

	public Boolean isSimpleCount()
	{
		return m_IsSimpleCount;
	}

	public void setIsSimpleCount(boolean value)
	{
		m_IsSimpleCount = value;
	}

}
