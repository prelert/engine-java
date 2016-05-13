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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value={"initialAnomalyScore"}, allowSetters=true)
public class Influencer
{
    /**
     * Elasticsearch type
     */
    public static final String TYPE = "influencer";

    /*
     * Field names
     */
    public static final String PROBABILITY = "probability";
    public static final String TIMESTAMP = "timestamp";
    public static final String INFLUENCER_FIELD_NAME = "influencerFieldName";
    public static final String INFLUENCER_FIELD_VALUE = "influencerFieldValue";
    public static final String INITIAL_ANOMALY_SCORE = "initialAnomalyScore";
    public static final String ANOMALY_SCORE = "anomalyScore";

    private String m_Id;

    private Date m_Timestamp;
    private String m_InfluenceField;
    private String m_InfluenceValue;
    private double m_Probability;
    private double m_InitialAnomalyScore;
    private double m_AnomalyScore;
    private boolean m_HadBigNormalisedUpdate;
    private boolean m_IsInterim;

    public Influencer()
    {
    }

    public Influencer(String fieldName, String fieldValue)
    {
        m_InfluenceField = fieldName;
        m_InfluenceValue = fieldValue;
    }

    /**
     * Data store ID of this record.  May be null for records that have not been
     * read from the data store.
     */
    @JsonIgnore
    public String getId()
    {
        return m_Id;
    }

    @JsonIgnore
    public void setId(String id)
    {
        m_Id = id;
    }

    public double getProbability()
    {
        return m_Probability;
    }

    public void setProbability(double probability)
    {
        m_Probability = probability;
    }


    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date date)
    {
        m_Timestamp = date;
    }


    public String getInfluencerFieldName()
    {
        return m_InfluenceField;
    }

    public void setInfluencerFieldName(String fieldName)
    {
        m_InfluenceField = fieldName;
    }


    public String getInfluencerFieldValue()
    {
        return m_InfluenceValue;
    }

    public void setInfluencerFieldValue(String fieldValue)
    {
        m_InfluenceValue = fieldValue;
    }

    public double getInitialAnomalyScore()
    {
        return m_InitialAnomalyScore;
    }

    public void setInitialAnomalyScore(double influenceScore)
    {
        m_InitialAnomalyScore = influenceScore;
    }

    public double getAnomalyScore()
    {
        return m_AnomalyScore;
    }

    public void setAnomalyScore(double score)
    {
        m_AnomalyScore = score;
    }

    public boolean isInterim()
    {
        return m_IsInterim;
    }

    @JsonProperty("isInterim")
    public void setInterim(boolean value)
    {
        m_IsInterim = value;
    }

    public boolean hadBigNormalisedUpdate()
    {
        return m_HadBigNormalisedUpdate;
    }

    public void resetBigNormalisedUpdateFlag()
    {
        m_HadBigNormalisedUpdate = false;
    }

    public void raiseBigNormalisedUpdateFlag()
    {
        m_HadBigNormalisedUpdate = true;
    }

    @Override
    public int hashCode()
    {
        // ID is NOT included in the hash, so that a record from the data store
        // will hash the same as a record representing the same anomaly that did
        // not come from the data store

        // m_HadBigNormalisedUpdate is also deliberately excluded from the hash

        return Objects.hash(m_Timestamp, m_InfluenceField, m_InfluenceValue, m_InitialAnomalyScore,
                m_AnomalyScore, m_Probability, m_IsInterim);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        Influencer other = (Influencer) obj;

        // ID is NOT compared, so that a record from the data store will compare
        // equal to a record representing the same anomaly that did not come
        // from the data store

        // m_HadBigNormalisedUpdate is also deliberately excluded from the test
        return Objects.equals(m_Timestamp, other.m_Timestamp) &&
                Objects.equals(m_InfluenceField, other.m_InfluenceField) &&
                Objects.equals(m_InfluenceValue, other.m_InfluenceValue) &&
                Double.compare(m_InitialAnomalyScore, other.m_InitialAnomalyScore) == 0 &&
                Double.compare(m_AnomalyScore, other.m_AnomalyScore) == 0 &&
                Double.compare(m_Probability, other.m_Probability) == 0 &&
                (m_IsInterim == other.m_IsInterim);
    }


}
