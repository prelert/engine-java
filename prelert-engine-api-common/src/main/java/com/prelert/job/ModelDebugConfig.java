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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties({"enabled"})
@JsonInclude(Include.NON_NULL)
public class ModelDebugConfig
{
    /**
     * Enum of the acceptable output destinations.
     */
    public enum DebugDestination
    {
        FILE, DATA_STORE;

        /**
         * Case-insensitive from string method.
         * Works with FILE, File, file, etc.
         *
         * @param value String representation
         * @return The output destination
         */
        @JsonCreator
        public static DebugDestination forString(String value)
        {
            String valueUpperCase = value.toUpperCase();
            return DebugDestination.valueOf(valueUpperCase);
        }
    }

    public static final String TYPE = "modelDebugConfig";
    public static final String WRITE_TO = "writeTo";
    public static final String BOUNDS_PERCENTILE = "boundsPercentile";
    public static final String TERMS = "terms";

    private DebugDestination m_WriteTo;
    private Double m_BoundsPercentile;
    private String m_Terms;

    public ModelDebugConfig()
    {
        // NB: m_WriteTo defaults to null in this case, otherwise an update to
        // the bounds percentile could switch where the debug is written to
    }

    public ModelDebugConfig(Double boundsPercentile, String terms)
    {
        m_WriteTo = DebugDestination.FILE;
        m_BoundsPercentile = boundsPercentile;
        m_Terms = terms;
    }

    public ModelDebugConfig(DebugDestination writeTo, Double boundsPercentile, String terms)
    {
        m_WriteTo = writeTo;
        m_BoundsPercentile = boundsPercentile;
        m_Terms = terms;
    }

    public DebugDestination getWriteTo()
    {
        return m_WriteTo;
    }

    public void setWriteTo(DebugDestination writeTo)
    {
        m_WriteTo = writeTo;
    }

    public boolean isEnabled()
    {
        return m_BoundsPercentile != null;
    }

    public Double getBoundsPercentile()
    {
        return m_BoundsPercentile;
    }

    public void setBoundsPercentile(Double boundsPercentile)
    {
        m_BoundsPercentile = boundsPercentile;
    }

    public String getTerms()
    {
        return m_Terms;
    }

    public void setTerms(String terms)
    {
        m_Terms = terms;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof ModelDebugConfig == false)
        {
            return false;
        }

        ModelDebugConfig that = (ModelDebugConfig) other;
        return Objects.equals(this.m_WriteTo, that.m_WriteTo)
                && Objects.equals(this.m_BoundsPercentile, that.m_BoundsPercentile)
                && Objects.equals(this.m_Terms, that.m_Terms);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_WriteTo, m_BoundsPercentile, m_Terms);
    }
}
