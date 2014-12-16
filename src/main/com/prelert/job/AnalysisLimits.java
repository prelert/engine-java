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
import java.util.Objects;

/**
 * Analysis limits for autodetect (max model memory size).
 *
 * If an option has not been set it's value will be 0 in which case it
 * shouldn't be used so the default value is picked up instead.
 */
public class AnalysisLimits
{
	/**
	 * Serialisation field names
	 */
	public static final String MODEL_MEMORY_LIMIT = "modelMemoryLimit";

	private long m_ModelMemoryLimit;

	/**
	 * Initialise values to 0.
	 * If the values are 0 they haven't been set
	 */
	public AnalysisLimits()
	{
		m_ModelMemoryLimit = 0;
	}

	public AnalysisLimits(long modelMemoryLimit)
	{
		m_ModelMemoryLimit = modelMemoryLimit;
	}

	/**
	 * Create and set field values from the Map.
	 * @param values
	 */
	public AnalysisLimits(Map<String, Object> values)
	{
		this();

		if (values.containsKey(MODEL_MEMORY_LIMIT))
		{
			Object obj = values.get(MODEL_MEMORY_LIMIT);
			if (obj != null)
			{
				m_ModelMemoryLimit = ((Number)obj).longValue();
			}
		}
	}

	/**
	 * Maximum size of the model in MB before the anomaly detector
     * will drop new samples to prevent the model using any more
     * memory
	 */
	public long getModelMemoryLimit()
	{
		return m_ModelMemoryLimit;
	}

	public void setModelMemoryLimit(long value)
	{
		m_ModelMemoryLimit = value;
	}

	/**
	 * Overridden equality test
	 */
	@Override
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
		return (this.m_ModelMemoryLimit == that.m_ModelMemoryLimit);
	}

    @Override
    public int hashCode()
    {
        return Objects.hash(m_ModelMemoryLimit);
    }

}

