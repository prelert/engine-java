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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value={"rawAnomalyScore","initialAnomalyScore"}, allowSetters=true)
public class BucketInfluencer
{
    /**
     * Elasticsearch type
     */
    public static final String TYPE = "bucketInfluencer";

    /**
     * This is the field name of the time bucket influencer.
     */
    public static final String BUCKET_TIME = "bucketTime";

    /*
     * Field names
     */
    public static final String INFLUENCER_FIELD_NAME = "influencerFieldName";
    public static final String INITIAL_ANOMALY_SCORE = "initialAnomalyScore";
    public static final String ANOMALY_SCORE = "anomalyScore";
    public static final String RAW_ANOMALY_SCORE = "rawAnomalyScore";
    public static final String PROBABILITY = "probability";

    private String m_InfluenceField;
    private double m_InitialAnomalyScore;
    private double m_AnomalyScore;
    private double m_RawAnomalyScore;
    private double m_Probability;

    public BucketInfluencer()
    {
    }

    public double getProbability()
    {
        return m_Probability;
    }

    public void setProbability(double probability)
    {
        this.m_Probability = probability;
    }

    public String getInfluencerFieldName()
    {
        return m_InfluenceField;
    }

    public void setInfluencerFieldName(String fieldName)
    {
        this.m_InfluenceField = fieldName;
    }

    public double getInitialAnomalyScore()
    {
        return m_InitialAnomalyScore;
    }

    public void setInitialAnomalyScore(double influenceScore)
    {
        this.m_InitialAnomalyScore = influenceScore;
    }

    public double getAnomalyScore()
    {
        return m_AnomalyScore;
    }

    public void setAnomalyScore(double score)
    {
        m_AnomalyScore = score;
    }

    public double getRawAnomalyScore()
    {
        return m_RawAnomalyScore;
    }

    public void setRawAnomalyScore(double score)
    {
        m_RawAnomalyScore = score;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_InfluenceField, m_InitialAnomalyScore, m_AnomalyScore,
                m_RawAnomalyScore, m_Probability);
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

        BucketInfluencer other = (BucketInfluencer) obj;

        return  Objects.equals(m_InfluenceField, other.m_InfluenceField) &&
                Double.compare(m_InitialAnomalyScore, other.m_InitialAnomalyScore) == 0 &&
                Double.compare(m_AnomalyScore, other.m_AnomalyScore) == 0 &&
                Double.compare(m_RawAnomalyScore, other.m_RawAnomalyScore) == 0 &&
                Double.compare(m_Probability, other.m_Probability) == 0;
    }
}
