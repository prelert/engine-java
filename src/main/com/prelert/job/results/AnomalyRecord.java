/****************************************************************************
 *                                                                          *
 * Copyright 2015 Prelert Ltd                                               *
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
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Anomaly Record POJO.
 * Uses the object wrappers Boolean and Double so <code>null</code> values
 * can be returned if the members have not been set.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"parent", "id", "detectorName"})
public class AnomalyRecord
{
    /**
     * Serialisation fields
     */
    public static final String TYPE = "record";

    /**
     * Data store ID field
     */
    public static final String ID = "id";

    /**
     * Result fields (all detector types)
     */
    public static final String PROBABILITY = "probability";
    public static final String BY_FIELD_NAME = "byFieldName";
    public static final String BY_FIELD_VALUE = "byFieldValue";
    public static final String PARTITION_FIELD_NAME = "partitionFieldName";
    public static final String PARTITION_FIELD_VALUE = "partitionFieldValue";
    public static final String FUNCTION = "function";
    public static final String TYPICAL = "typical";
    public static final String ACTUAL = "actual";
    public static final String IS_INTERIM = "isInterim";

    /**
     * Metric Results (including population metrics)
     */
    public static final String FIELD_NAME = "fieldName";

    /**
     * Population results
     */
    public static final String OVER_FIELD_NAME = "overFieldName";
    public static final String OVER_FIELD_VALUE = "overFieldValue";
    public static final String CAUSES = "causes";

    /**
     * Normalisation
     */
    public static final String ANOMALY_SCORE = "anomalyScore";
    public static final String NORMALIZED_PROBABILITY = "normalizedProbability";

    private static final Logger LOGGER = Logger.getLogger(AnomalyRecord.class);

    private String m_DetectorName;
    private int m_IdNum;
    private double m_Probability;
    private String m_ByFieldName;
    private String m_ByFieldValue;
    private String m_PartitionFieldName;
    private String m_PartitionFieldValue;
    private String m_Function;
    private Double m_Typical;
    private Double m_Actual;
    private Boolean m_IsInterim;

    private String m_FieldName;

    private String m_OverFieldName;
    private String m_OverFieldValue;
    private List<AnomalyCause> m_Causes;

    private double m_AnomalyScore;
    private double m_NormalizedProbability;
    private Date   m_Timestamp;

    private boolean m_HadBigNormalisedUpdate;

    private String m_Parent;

    /**
     * Data store ID of this record.  May be null for records that have not been
     * read from the data store.
     */
    public String getId()
    {
        if (m_IdNum == 0)
        {
            return null;
        }
        return m_Parent + m_DetectorName + m_IdNum;
    }

    /**
     * This should only be called by code that's reading records from the data
     * store.  The ID must be set to the data stores's unique key to this
     * anomaly record.
     *
     * TODO - this is a breach of encapsulation that should be rectified when
     * a big enough change is made to justify invalidating all previously
     * stored data.  Currently it makes an assumption about the format of the
     * detector name, which should be opaque to the Java code.
     */
    public void setId(String id)
    {
        int epochLen = 0;
        while (id.length() > epochLen && Character.isDigit(id.charAt(epochLen)))
        {
            ++epochLen;
        }
        int idStart = -1;
        if (m_PartitionFieldValue == null || m_PartitionFieldValue.isEmpty())
        {
            idStart = id.lastIndexOf("/") + 1;
        }
        else
        {
            idStart = id.lastIndexOf("/" + m_PartitionFieldValue);
            if (idStart >= epochLen)
            {
                idStart += 1 + m_PartitionFieldValue.length();
            }
        }
        if (idStart <= epochLen)
        {
            LOGGER.error("Anomaly record ID not in expected format: " + id);
            return;
        }
        m_Parent = id.substring(0, epochLen).intern();
        m_DetectorName = id.substring(epochLen, idStart).intern();
        m_IdNum = Integer.parseInt(id.substring(idStart));
    }


    /**
     * Generate the data store ID for this record.
     *
     * TODO - the current format is hard to parse back into its constituent
     * parts, but cannot be changed without breaking backwards compatibility.
     * If backwards compatibility is ever broken for some other reason then the
     * opportunity should be taken to change this format.
     */
    public String generateNewId(String parent, String detectorName, int count)
    {
        m_Parent = parent.intern();
        m_DetectorName = detectorName.intern();
        m_IdNum = count;
        return getId();
    }


    public double getAnomalyScore()
    {
        return m_AnomalyScore;
    }

    public void setAnomalyScore(double anomalyScore)
    {
        m_HadBigNormalisedUpdate |= isBigUpdate(m_AnomalyScore, anomalyScore);
        m_AnomalyScore = anomalyScore;
    }

    public double getNormalizedProbability()
    {
        return m_NormalizedProbability;
    }

    public void setNormalizedProbability(double normalizedProbability)
    {
        m_HadBigNormalisedUpdate |= isBigUpdate(m_NormalizedProbability, normalizedProbability);
        m_NormalizedProbability = normalizedProbability;
    }


    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        m_Timestamp = timestamp;
    }

    public double getProbability()
    {
        return m_Probability;
    }

    public void setProbability(double value)
    {
        m_Probability = value;
    }


    public String getByFieldName()
    {
        return m_ByFieldName;
    }

    public void setByFieldName(String value)
    {
        m_ByFieldName = value.intern();
    }

    public String getByFieldValue()
    {
        return m_ByFieldValue;
    }

    public void setByFieldValue(String value)
    {
        m_ByFieldValue = value.intern();
    }

    public String getPartitionFieldName()
    {
        return m_PartitionFieldName;
    }

    public void setPartitionFieldName(String field)
    {
        m_PartitionFieldName = field.intern();
    }

    public String getPartitionFieldValue()
    {
        return m_PartitionFieldValue;
    }

    public void setPartitionFieldValue(String value)
    {
        m_PartitionFieldValue = value.intern();
    }

    public String getFunction()
    {
        return m_Function;
    }

    public void setFunction(String name)
    {
        m_Function = name.intern();
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

    @JsonProperty("isInterim")
    public Boolean isInterim()
    {
        return m_IsInterim;
    }

    @JsonProperty("isInterim")
    public void setInterim(Boolean isInterim)
    {
        m_IsInterim = isInterim;
    }

    public String getFieldName()
    {
        return m_FieldName;
    }

    public void setFieldName(String field)
    {
        m_FieldName = field.intern();
    }

    public String getOverFieldName()
    {
        return m_OverFieldName;
    }

    public void setOverFieldName(String name)
    {
        m_OverFieldName = name.intern();
    }

    public String getOverFieldValue()
    {
        return m_OverFieldValue;
    }

    public void setOverFieldValue(String value)
    {
        m_OverFieldValue = value.intern();
    }

    public List<AnomalyCause> getCauses()
    {
        return m_Causes;
    }

    public void setCauses(List<AnomalyCause> causes)
    {
        m_Causes = causes;
    }

    public String getParent()
    {
        return m_Parent;
    }

    public void setParent(String parent)
    {
        m_Parent = parent.intern();
    }

    @Override
    public int hashCode()
    {
        // ID is NOT included in the hash, so that a record from the data store
        // will hash the same as a record representing the same anomaly that did
        // not come from the data store

        // m_HadBigNormalisedUpdate is also deliberately excluded from the hash

        return Objects.hash(m_Probability, m_AnomalyScore, m_NormalizedProbability,
                m_Typical, m_Actual, m_Function, m_FieldName, m_ByFieldName, m_ByFieldValue,
                m_PartitionFieldName, m_PartitionFieldValue, m_OverFieldName, m_OverFieldValue,
                m_Timestamp, m_Parent, m_IsInterim, m_Causes);
    }


    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof AnomalyRecord == false)
        {
            return false;
        }

        AnomalyRecord that = (AnomalyRecord)other;

        // ID is NOT compared, so that a record from the data store will compare
        // equal to a record representing the same anomaly that did not come
        // from the data store

        // m_HadBigNormalisedUpdate is also deliberately excluded from the test
        boolean equal = this.m_Probability == that.m_Probability &&
                this.m_AnomalyScore == that.m_AnomalyScore &&
                this.m_NormalizedProbability == that.m_NormalizedProbability &&
                Objects.equals(this.m_Typical, that.m_Typical) &&
                Objects.equals(this.m_Actual, that.m_Actual) &&
                Objects.equals(this.m_Function, that.m_Function) &&
                Objects.equals(this.m_FieldName, that.m_FieldName) &&
                Objects.equals(this.m_ByFieldName, that.m_ByFieldName) &&
                Objects.equals(this.m_ByFieldValue, that.m_ByFieldValue) &&
                Objects.equals(this.m_PartitionFieldName, that.m_PartitionFieldName) &&
                Objects.equals(this.m_PartitionFieldValue, that.m_PartitionFieldValue) &&
                Objects.equals(this.m_OverFieldName, that.m_OverFieldName) &&
                Objects.equals(this.m_OverFieldValue, that.m_OverFieldValue) &&
                Objects.equals(this.m_Timestamp, that.m_Timestamp) &&
                Objects.equals(this.m_Parent, that.m_Parent) &&
                Objects.equals(this.m_IsInterim, that.m_IsInterim);

        if (this.m_Causes == null && that.m_Causes == null)
        {
            equal &= true;
        }
        else if (this.m_Causes != null && that.m_Causes != null)
        {
            equal &= this.m_Causes.size() == that.m_Causes.size();
            if (equal)
            {
                for (int i=0; i<this.m_Causes.size(); i++)
                {
                    equal &= this.m_Causes.get(i).equals(that.m_Causes.get(i));
                }
            }
        }
        else
        {
            // one null the other not
            equal = false;
        }

        return equal;
    }


    public boolean hadBigNormalisedUpdate()
    {
        return m_HadBigNormalisedUpdate;
    }


    public void resetBigNormalisedUpdateFlag()
    {
        m_HadBigNormalisedUpdate = false;
    }


    /**
     * Encapsulate the logic for deciding whether a change to a normalised score
     * is "big".
     *
     * Current logic is that a big change is a change of at least 1 or more than
     * than 50% of the higher of the two values.
     *
     * @param oldVal The old value of the normalised score
     * @param newVal The new value of the normalised score
     * @return true if the update is considered "big"
     */
    static boolean isBigUpdate(double oldVal, double newVal)
    {
        if (Math.abs(oldVal - newVal) >= 1.0)
        {
            return true;
        }

        if (oldVal > newVal)
        {
            if (oldVal * 0.5 > newVal)
            {
                return true;
            }
        }
        else
        {
            if (newVal * 0.5 > oldVal)
            {
                return true;
            }
        }

        return false;
    }
}
