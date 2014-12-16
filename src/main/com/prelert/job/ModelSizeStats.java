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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Provide access to the C++ model memory usage numbers
 * for the Java process.
 */
@JsonIgnoreProperties({"id"})
public class ModelSizeStats
{
	/**
	 * Field Names
	 */
	public static final String ID = "id";
	public static final String MODEL_BYTES = "modelBytes";
	public static final String TOTAL_BY_FIELD_COUNT = "totalByFieldCount";
	public static final String TOTAL_OVER_FIELD_COUNT = "totalOverFieldCount";
	public static final String TOTAL_PARTITION_FIELD_COUNT = "totalPartitionFieldCount";

    /**
     * type
     */
	public static final String TYPE = "modelSizeStats";

	private long m_ModelBytes;
	private long m_TotalByFieldCount;
    private long m_TotalOverFieldCount;
	private long m_TotalPartitionFieldCount;

	public String getId()
	{
		return TYPE;
	}

	public void setId(String id)
	{
	}

	public void setModelBytes(long m)
	{
		m_ModelBytes = m;
	}

	public long getModelBytes()
	{
		return m_ModelBytes;
	}

	public void setTotalByFieldCount(long m)
	{
		m_TotalByFieldCount = m;
	}

	public long getTotalByFieldCount()
	{
		return m_TotalByFieldCount;
	}

	public void setTotalPartitionFieldCount(long m)
	{
		m_TotalPartitionFieldCount = m;
	}

	public long getTotalPartitionFieldCount()
	{
		return m_TotalPartitionFieldCount;
	}

    public void setTotalOverFieldCount(long m)
    {
        m_TotalOverFieldCount = m;
    }

    public long getTotalOverFieldCount()
    {
        return m_TotalOverFieldCount;
    }

}

