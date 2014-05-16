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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * Defines the fields to be used in the analysis. 
 * <code>Fieldname</code> must be set and only one of <code>ByFieldName</code> 
 * and <code>OverFieldName</code> should be set.
 */
@JsonInclude(Include.NON_NULL)
public class Detector
{
	static final public String FUNCTION = "function";
	static final public String FIELD_NAME = "fieldName";
	static final public String BY_FIELD_NAME = "byFieldName";
	static final public String OVER_FIELD_NAME = "overFieldName";
	static final public String PARTITION_FIELD_NAME = "partitionFieldName";
	static final public String USE_NULL = "useNull";
	
	
	static final public String COUNT = "count";
	static final public String HIGH_COUNT = "high_count";
	static final public String LOW_COUNT = "low_count";
	static final public String NON_ZERO_COUNT = "non_zero_count";
	static final public String NZC = "nzc";
	static final public String DISTINCT_COUNT = "distinct_count";
	static final public String DC = "dc";
	static final public String RARE = "rare";
	static final public String FREQ_RARE = "freq_rare";
	static final public String METRIC = "metric";
	static final public String MEAN = "mean";
	static final public String AVG = "avg";
	static final public String MIN = "min";
	static final public String MAX = "max";
	static final public String SUM = "sum";
	
	/**
	 * The set of valid function names.
	 */
	static public final Set<String> ANALYSIS_FUNCTIONS = 
			new HashSet<String>(Arrays.<String>asList(new String [] {
				COUNT, 
				HIGH_COUNT, 
				LOW_COUNT,
				NON_ZERO_COUNT, NZC,
				DISTINCT_COUNT, DC,
				RARE,
				FREQ_RARE,
				METRIC,
				MEAN, AVG,
				MIN, 
				MAX,
				SUM
			}));
	
	/**
	 * The set of count functions requiring a by or over field
	 */
	static public final Set<String> COUNT_BY_OVER_FUNCTIONS = 
			new HashSet<String>(Arrays.<String>asList(new String [] {
				HIGH_COUNT, 
				LOW_COUNT,
				NON_ZERO_COUNT, NZC,
			}));
	

	/**
	 * The set of functions that require a fieldname
	 */
	static public final Set<String> FIELD_NAME_FUNCTIONS = 
			new HashSet<String>(Arrays.<String>asList(new String [] {
				DISTINCT_COUNT, DC,
				METRIC,
				MEAN, AVG,
				MIN, 
				MAX,
				SUM
			}));
	
	/**
	 * The set of functions that require a by fieldname
	 */
	static public final Set<String> BY_FIELD_NAME_FUNCTIONS = 
			new HashSet<String>(Arrays.<String>asList(new String [] {
				RARE,
				FREQ_RARE
			}));
	
	/**
	 * The set of functions that require a over fieldname
	 */
	static public final Set<String> OVER_FIELD_NAME_FUNCTIONS = 
			new HashSet<String>(Arrays.<String>asList(new String [] {
				DISTINCT_COUNT, DC,
				FREQ_RARE
			}));
	
	
	
	private String m_Function;
	private String m_FieldName;
	private String m_ByFieldName;
	private String m_OverFieldName;		
	private String m_PartitionFieldName;
	private Boolean m_UseNull;		
		
	public Detector()
	{
		
	}
	
	/**
	 * Populate the detector from the String -> object map.
	 * 
	 * @param detectorMap
	 */
	public Detector(Map<String, Object> detectorMap)
	{
		if (detectorMap.containsKey(FUNCTION))
		{
			Object field = detectorMap.get(FUNCTION);
			if (field != null)
			{
				this.setFunction(field.toString());
			}
		}
		if (detectorMap.containsKey(FIELD_NAME))
		{
			Object field = detectorMap.get(FIELD_NAME);
			if (field != null)
			{
				this.setFieldName(field.toString());
			}
		}
		if (detectorMap.containsKey(BY_FIELD_NAME))
		{
			Object field = detectorMap.get(BY_FIELD_NAME);
			if (field != null)
			{
				this.setByFieldName(field.toString());
			}
		}
		if (detectorMap.containsKey(OVER_FIELD_NAME))
		{
			Object field = detectorMap.get(OVER_FIELD_NAME);
			if (field != null)
			{
				this.setOverFieldName(field.toString());
			}
		}				
		if (detectorMap.containsKey(PARTITION_FIELD_NAME))
		{
			Object obj = detectorMap.get(PARTITION_FIELD_NAME);
			if (obj != null)
			{
				m_PartitionFieldName = obj.toString();
			}
		}
		if (detectorMap.containsKey(USE_NULL))
		{
			Object field = detectorMap.get(USE_NULL);
			if (field != null && field instanceof Boolean)
			{
				this.setUseNull((Boolean)field);
			}
		}						
	}
	
	
	
	/**
	 * The analysis function used e.g. count, rare, min etc. There is no 
	 * validation to check this value is one a predefined set 
	 * @return The function or <code>null</code> if not set
	 */
	public String getFunction() 
	{
		return m_Function;
	}
	
	public void setFunction(String m_Function) 
	{
		this.m_Function = m_Function;
	}
	
	/**
	 * The Analysis field
	 * @return The field to analyse
	 */
	public String getFieldName() 
	{
		return m_FieldName;
	}
	
	public void setFieldName(String m_FieldName) 
	{
		this.m_FieldName = m_FieldName;
	}
	
	/**
	 * The 'by' field or <code>null</code> if not set. 
	 * @return The 'by' field
	 */
	public String getByFieldName() 
	{
		return m_ByFieldName;
	}
	
	public void setByFieldName(String m_ByFieldName) 
	{
		this.m_ByFieldName = m_ByFieldName;
	}
	
	/**
	 * The 'over' field or <code>null</code> if not set. 
	 * @return The 'over' field
	 */
	public String getOverFieldName() 
	{
		return m_OverFieldName;
	}
	
	public void setOverFieldName(String m_OverFieldName) 
	{
		this.m_OverFieldName = m_OverFieldName;
	}	
	
	/**
	 * Segments the analysis along another field to have completely 
	 * independent baselines for each instance of partitionfield
	 *
	 * @return The Partition Field
	 */
	public String getPartitionFieldName() 
	{
		return m_PartitionFieldName;
	}
	
	public void setPartitionFieldName(String partitionFieldName) 
	{
		this.m_PartitionFieldName = partitionFieldName;
	}
	
	
	/**
	 * Where there isn't a value for the 'by' or 'over' field should a new
	 * series be used as the 'null' series. 
	 * @return true if the 'null' series should be created
	 */
	public Boolean isUseNull() 
	{
		return m_UseNull;
	}
	
	public void setUseNull(Boolean useNull) 
	{
		this.m_UseNull = useNull;
	}
			
	@Override 
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		
		if (other instanceof Detector == false)
		{
			return false;
		}
		
		Detector that = (Detector)other;
				
		return JobDetails.bothNullOrEqual(this.m_Function, that.m_Function) &&
				JobDetails.bothNullOrEqual(this.m_FieldName, that.m_FieldName) &&
				JobDetails.bothNullOrEqual(this.m_ByFieldName, that.m_ByFieldName) &&
				JobDetails.bothNullOrEqual(this.m_OverFieldName, that.m_OverFieldName) &&
				JobDetails.bothNullOrEqual(this.m_PartitionFieldName, that.m_PartitionFieldName) &&
				JobDetails.bothNullOrEqual(this.m_UseNull, that.m_UseNull);					
	}
}
