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

package com.prelert.job.results;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Model Debug POJO.
 * Some of the fields being with the word "debug".  This avoids creation of
 * reserved words that are likely to clash with fields in the input data (due to
 * the restrictions on Elasticsearch mappings).
 */
@JsonIgnoreProperties({"id"})
@JsonInclude(Include.NON_NULL)
public class ModelDebugOutput
{
    public static final String TYPE = "modelDebugOutput";
    public static final String TIMESTAMP = "timestamp";
    public static final String PARTITION_FIELD_NAME = "partitionFieldName";
    public static final String PARTITION_FIELD_VALUE = "partitionFieldValue";
    public static final String OVER_FIELD_NAME = "overFieldName";
    public static final String OVER_FIELD_VALUE = "overFieldValue";
    public static final String BY_FIELD_NAME = "byFieldName";
    public static final String BY_FIELD_VALUE = "byFieldValue";
    public static final String DEBUG_FEATURE = "debugFeature";
    public static final String DEBUG_LOWER = "debugLower";
    public static final String DEBUG_UPPER = "debugUpper";
    public static final String DEBUG_MEAN = "debugMean";
    public static final String ACTUAL = "actual";

    private Date m_Timestamp;
    private String m_Id;
    private String m_PartitionFieldName;
    private String m_PartitionFieldValue;
    private String m_OverFieldName;
    private String m_OverFieldValue;
    private String m_ByFieldName;
    private String m_ByFieldValue;
    private String m_DebugFeature;
    private double m_DebugLower;
    private double m_DebugUpper;
    private double m_DebugMean;
    private double m_Actual;

    public String getId()
    {
        return m_Id;
    }

    public void setId(String id)
    {
        m_Id = id;
    }

    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        m_Timestamp = timestamp;
    }

    public String getPartitionFieldName()
    {
        return m_PartitionFieldName;
    }

    public void setPartitionFieldName(String partitionFieldName)
    {
        m_PartitionFieldName = partitionFieldName;
    }

    public String getPartitionFieldValue()
    {
        return m_PartitionFieldValue;
    }

    public void setPartitionFieldValue(String partitionFieldValue)
    {
        m_PartitionFieldValue = partitionFieldValue;
    }

    public String getOverFieldName()
    {
        return m_OverFieldName;
    }

    public void setOverFieldName(String overFieldName)
    {
        m_OverFieldName = overFieldName;
    }

    public String getOverFieldValue()
    {
        return m_OverFieldValue;
    }

    public void setOverFieldValue(String overFieldValue)
    {
        m_OverFieldValue = overFieldValue;
    }

    public String getByFieldName()
    {
        return m_ByFieldName;
    }

    public void setByFieldName(String byFieldName)
    {
        m_ByFieldName = byFieldName;
    }

    public String getByFieldValue()
    {
        return m_ByFieldValue;
    }

    public void setByFieldValue(String byFieldValue)
    {
        m_ByFieldValue = byFieldValue;
    }

    public String getDebugFeature()
    {
        return m_DebugFeature;
    }

    public void setDebugFeature(String debugFeature)
    {
        m_DebugFeature = debugFeature;
    }

    public double getDebugLower()
    {
        return m_DebugLower;
    }

    public void setDebugLower(double debugLower)
    {
        m_DebugLower = debugLower;
    }

    public double getDebugUpper()
    {
        return m_DebugUpper;
    }

    public void setDebugUpper(double debugUpper)
    {
        m_DebugUpper = debugUpper;
    }

    public double getDebugMean()
    {
        return m_DebugMean;
    }

    public void setDebugMean(double debugMean)
    {
        m_DebugMean = debugMean;
    }

    public double getActual()
    {
        return m_Actual;
    }

    public void setActual(double actual)
    {
        m_Actual = actual;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other instanceof ModelDebugOutput == false)
        {
            return false;
        }
        // m_Id excluded here as it is generated by the datastore
        ModelDebugOutput that = (ModelDebugOutput) other;
        return Objects.equals(this.m_Timestamp, that.m_Timestamp) &&
                Objects.equals(this.m_PartitionFieldValue, that.m_PartitionFieldValue) &&
                Objects.equals(this.m_PartitionFieldName, that.m_PartitionFieldName) &&
                Objects.equals(this.m_OverFieldValue, that.m_OverFieldValue) &&
                Objects.equals(this.m_OverFieldName, that.m_OverFieldName) &&
                Objects.equals(this.m_ByFieldValue, that.m_ByFieldValue) &&
                Objects.equals(this.m_ByFieldName, that.m_ByFieldName) &&
                Objects.equals(this.m_DebugFeature, that.m_DebugFeature) &&
                this.m_DebugLower == that.m_DebugLower &&
                this.m_DebugUpper == that.m_DebugUpper &&
                this.m_DebugMean == that.m_DebugMean &&
                this.m_Actual == that.m_Actual;
    }

    @Override
    public int hashCode()
    {
        // m_Id excluded here as it is generated by the datastore
        return Objects.hash(m_Timestamp, m_PartitionFieldName, m_PartitionFieldValue,
                m_OverFieldName, m_OverFieldValue, m_ByFieldName, m_ByFieldValue,
                m_DebugFeature, m_DebugLower, m_DebugUpper, m_DebugMean, m_Actual);
    }
}
