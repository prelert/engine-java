/****************************************************************************
 *                                                                          *
 * Copyright 2015-2016 Prelert Ltd                                          *
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Analysis limits for autodetect
 *
 * If an option has not been set it shouldn't be used so the default value is picked up instead.
 */
@JsonInclude(Include.NON_NULL)
public class AnalysisLimits
{
    /**
     * Serialisation field names
     */
    public static final String MODEL_MEMORY_LIMIT = "modelMemoryLimit";
    public static final String CATEGORIZATION_EXAMPLES_LIMIT = "categorizationExamplesLimit";

    /**
     * It is initialised to 0.  A value of 0 indicates it was not set, which in
     * turn causes the C++ process to use its own default limit.  A negative
     * value means no limit.  All negative input values are stored as -1.
     */
    private long m_ModelMemoryLimit;

    /**
     * It is initialised to <code>null</code>.
     * A value of <code>null</code> indicates it was not set.
     * */
    private Long m_CategorizationExamplesLimit;

    public AnalysisLimits()
    {
        m_ModelMemoryLimit = 0;
        m_CategorizationExamplesLimit = null;
    }

    public AnalysisLimits(long modelMemoryLimit, Long categorizationExamplesLimit)
    {
        if (modelMemoryLimit < 0)
        {
            // All negative numbers mean "no limit"
            m_ModelMemoryLimit = -1;
        }
        else
        {
            m_ModelMemoryLimit = modelMemoryLimit;
        }
        m_CategorizationExamplesLimit = categorizationExamplesLimit;
    }

    /**
     * Maximum size of the model in MB before the anomaly detector
     * will drop new samples to prevent the model using any more
     * memory
     *
     * @return The set memory limit or 0 if not set
     */
    public long getModelMemoryLimit()
    {
        return m_ModelMemoryLimit;
    }

    public void setModelMemoryLimit(long value)
    {
        if (value < 0)
        {
            // All negative numbers mean "no limit"
            m_ModelMemoryLimit = -1;
        }
        else
        {
            m_ModelMemoryLimit = value;
        }
    }

    /**
     * Gets the limit to the number of examples that are stored per category
     * @return the limit or <code>null</code> if not set
     */
    public Long getCategorizationExamplesLimit()
    {
        return m_CategorizationExamplesLimit;
    }

    public void setCategorizationExamplesLimit(Long value)
    {
        m_CategorizationExamplesLimit = value;
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
        return this.m_ModelMemoryLimit == that.m_ModelMemoryLimit
                && Objects.equals(this.m_CategorizationExamplesLimit,
                        that.m_CategorizationExamplesLimit);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_ModelMemoryLimit, m_CategorizationExamplesLimit);
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(MODEL_MEMORY_LIMIT, m_ModelMemoryLimit);
        if (m_CategorizationExamplesLimit != null)
        {
            map.put(CATEGORIZATION_EXAMPLES_LIMIT, m_CategorizationExamplesLimit);
        }
        return map;
    }
}
