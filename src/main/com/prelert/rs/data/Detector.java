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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents the anomaly detector.
 * Only the detector name is serialised anomaly records aren't.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"records"})
public class Detector 
{
	static final public String TYPE = "detector";
	static final public String NAME = "name";
	static final public String RECORDS = "records";
	
	static final private Logger s_Logger = Logger.getLogger(Detector.class);
	
	private String m_Name;
	private List<AnomalyRecord> m_Records;
	
	public Detector()
	{
		m_Records = new ArrayList<>();
	}
	
	public Detector(String name)
	{
		this();
		m_Name = name;		
	}
	
	/**
	 * Create the detector from a map. Only the name field is read
	 * @param values
	 */
	public Detector(Map<String, Object> values)
	{
		if (values.containsKey(NAME))
		{
			m_Name = values.get(NAME).toString();
		}
		else
		{
			s_Logger.error("Constructing detector from map with no " + NAME + " field");
		}
	}
	
	public String getName()
	{
		return m_Name;
	}
	
	public void setName(String name)
	{
		m_Name = name;
	}
	
	public void addRecord(AnomalyRecord record)
	{
		m_Records.add(record);
	}
	
	public List<AnomalyRecord> getRecords()
	{
		return m_Records;
	}
	
}
